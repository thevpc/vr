/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.dialog;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.fs.VFileInfo;
import net.vpc.app.vainruling.core.service.fs.VFileKind;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.jsf.DialogBuilder;
import net.vpc.app.vainruling.core.web.jsf.ctrl.DocumentsCtrl;
import net.vpc.app.vainruling.core.web.jsf.ctrl.FileUploadEventHandler;
import net.vpc.app.vainruling.core.service.editor.DialogResult;
import net.vpc.app.vainruling.core.web.util.DocumentsUtils;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileType;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.pages.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage
@Controller
public class DocumentsDialogCtrl {

    private static final Logger log = Logger.getLogger(DocumentsDialogCtrl.class.getName());

    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrUtils.parseJSONObject(config, Config.class));
    }


    public void openDialog(Config config) {
        initContent(config);


        new DialogBuilder("/modules/files/documents-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
    }

    public void onSave() {
        String n = getModel().getNewFolderName().trim();
        VFile f2 = getModel().getCurrent().getFile().get(n);
        try {
            if (!f2.mkdirs()) {
                FacesUtils.addErrorMessage("Directory " + f2.getPath() + " could not be created.");
            } else {
                getModel().setEditMode("");
                onRefresh();
            }
        } catch (Exception ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(f2.getPath() + " could not be created.");
        }
    }

    public void onNewFolder() {
        getModel().setNewFolderName("NewFolder");
        getModel().setEditMode("NewFolder");
        onRefresh();
    }

    public void onUpload() {
        getModel().setEditMode("Upload");
    }

    public void onCancel() {
        getModel().setEditMode("");
    }

    public boolean isEnabledButton(String buttonId) {
        if ("Refresh".equals(buttonId)) {
            return isDefaultMode();
        }
        if ("NewFolder".equals(buttonId)) {
            return isDefaultMode()
                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE)
                    && getModel().getCurrent().getFile().isAllowedCreateChild(VFileType.DIRECTORY, null);
        }
        if ("Upload".equals(buttonId)) {
            VFile file = getModel().getCurrent().getFile();
            return isDefaultMode()
                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE)
                    && (file.isAllowedCreateChild(VFileType.FILE, null)
                    || file.isAllowedUpdateChild(VFileType.FILE, null));
        }
        if ("Remove".equals(buttonId)) {
            return isDefaultMode()
                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE)
                    && getModel().getCurrent().getFile().isAllowedRemoveChild(null, null);
        }
        if ("Save".equals(buttonId)) {
            return isNewFolderMode();
        }
        if ("Cancel".equals(buttonId)) {
            return !isDefaultMode();
        }
//        if ("SelectFile".equals(buttonId)) {
//            return getModel().getArea().isEmpty();
//        }
        if ("Security".equals(buttonId)) {
            return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_ASSIGN_RIGHTS)
                    &&
                    !getModel().getCurrent().getFile().getACL().isReadOnly();
        }
        return CorePlugin.get().getCurrentToken().isAdmin();
    }

    public void fireEventExtraDialogClosed() {
        StringBuilder sb = new StringBuilder();
        for (VFileInfo file : getModel().getFiles()) {
            if (file.isSelected()) {
                if (sb.length() > 0) {
                    sb.append(";");
                }
                sb.append(file.getFile().getBaseFile("vrfs").getPath());
            }
        }
        //Object obj
        RequestContext.getCurrentInstance().closeDialog(
                new DialogResult(sb.toString(), getModel().getConfig().getUserInfo())
        );
    }

    public void initContent(Config cmd) {
        getModel().setEditMode("");
        getModel().setConfig(cmd);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        Config c = getModel().getConfig();
        if (c == null) {
            c = new Config();
        }
        String title = c.getTitle();
        if (StringUtils.isBlank(title)) {
            title = "Documents";
        }
        getModel().setTitle(title);

        String fspath = cmd.getFspath();
        if (StringUtils.isBlank(fspath)) {
            fspath = "";
        }
        VirtualFileSystem rootfs = UPA.getContext().invokePrivileged(new Action<VirtualFileSystem>() {
            @Override
            public VirtualFileSystem run() {
                return core.getRootFileSystem();
            }
        });
        VirtualFileSystem fs = null;
        String login = core.getCurrentUserLogin();
        if ("root".equals(c.getType())) {
            fs = rootfs;
        } else if ("user".equals(c.getType())) {
            String v = c.getValue();
            if (StringUtils.isBlank(v)) {
                v = login;
            }
            fs = core.getUserFileSystem(v);
        } else if ("profile".equals(c.getType())) {
            String v = c.getValue();
            if (StringUtils.isBlank(v)) {
                v = "user";
            }
            fs = core.getProfileFileSystem(v);
        } else {
            fs = core.getUserFileSystem(login);
        }
        if (fspath.isEmpty() || fspath.equals("/")) {
            //okkay
        } else {
            fs.get(fspath).mkdirs();
            fs = fs.subfs(fspath);
        }

        getModel().setFileSystem(fs);
        getModel().setCurrent(DocumentsUtils.createFileInfo("/", VFileKind.ROOT, getModel().getFileSystem().get("/")));
        String initialPath = c.getPath();
        if (!(StringUtils.isBlank(initialPath))) {
            VFile pp = null;//
            try {
                pp = getModel().getFileSystem().get(initialPath);
            } catch (Exception ignoreMe) {
                //
            }
            if (pp != null) {
                if (pp.isDirectory()) {
                    getModel().setCurrent(DocumentsUtils.createFileInfo(pp));
                } else if (pp.isFile()) {
                    VFile par = pp.getParentFile();
                    if (par != null) {
                        getModel().setCurrent(DocumentsUtils.createFileInfo(par));
                    }
                }
            }
        }
        onRefresh();
    }

//    protected VirtualFileSystem createFS() {
//        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
//        VirtualFileSystem rootfs = fsp.getRootFileSystem();
//        VirtualFileSystem userfs = rootfs.filter(null);
//        return userfs;
//    }

    public void selectDirectory(VFile file) {
        getModel().setCurrent(DocumentsUtils.createFileInfo(file.getName(), VFileKind.ORDINARY, file));
        onRefresh();
    }

    public void selectFile(VFile file) {
        getModel().setCurrent(DocumentsUtils.createFileInfo(file));
        RequestContext.getCurrentInstance().closeDialog(
                new DialogResult(getModel().getCurrent().getFile().getBaseFile("vrfs").getPath(), getModel().getConfig().getUserInfo())
        );
    }

    public void onRemove() {
        try {
            for (VFileInfo file : getModel().getFiles()) {
                if (file.getKind() == VFileKind.ORDINARY && file.isSelected()) {
                    file.getFile().deleteAll();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentsDialogCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        onRefresh();
    }

    public void onRefresh() {
        getModel().setFiles(DocumentsUtils.loadFiles(getModel().getCurrent().getFile()));
    }


    public void handleNewFile(FileUploadEvent event) {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    try {
                        CorePlugin.get().uploadFile(getModel().getCurrent().getFile(), new FileUploadEventHandler(event) {

                            @Override
                            public boolean acceptOverride(VFile file) {
//check if alreay selected
                                for (VFileInfo ex : getModel().getFiles()) {
                                    if (ex.getFile().getName().equals(file.getName()) && ex.isSelected()) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                        });
                        FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully uploaded.");
                    } catch (Exception ex) {
                        Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                        FacesUtils.addErrorMessage(ex, event.getFile().getFileName() + " uploading failed.");
                    }
                } finally {
                    getModel().setEditMode("");
                }

            }
        });
        onRefresh();
    }

    public boolean isDefaultMode() {
        return "".equals(getModel().getEditMode());
    }

    public boolean isUploadFileMode() {
        return "Upload".equals(getModel().getEditMode());
    }

    public boolean isNewFolderMode() {
        return "NewFolder".equals(getModel().getEditMode());
    }

    public Model getModel() {
        return model;
    }

    public static class Config {

        private String type;
        private String value;
        private String title;
        private String path;
        private String userInfo;
        private String fspath;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(String userInfo) {
            this.userInfo = userInfo;
        }

        public String getFspath() {
            return fspath;
        }

        public void setFspath(String fspath) {
            this.fspath = fspath;
        }
    }

    public static class Model {

        private boolean uploadAllowed;
        private boolean createFolderAllowed;
        private String editMode = "";
        private String newFolderName;
        private String title;
        private VFileInfo current;
        private VirtualFileSystem fileSystem;
        private Config config;

        public boolean isUploadAllowed() {
            return uploadAllowed;
        }

        public void setUploadAllowed(boolean uploadAllowed) {
            this.uploadAllowed = uploadAllowed;
        }

        public boolean isCreateFolderAllowed() {
            return createFolderAllowed;
        }

        public void setCreateFolderAllowed(boolean createFolderAllowed) {
            this.createFolderAllowed = createFolderAllowed;
        }

        public String getEditMode() {
            return editMode;
        }

        public void setEditMode(String editMode) {
            this.editMode = editMode;
        }

        public String getNewFolderName() {
            return newFolderName;
        }

        public void setNewFolderName(String newFolderName) {
            this.newFolderName = newFolderName;
        }

        private List<VFileInfo> files = new ArrayList<>();

        public VFileInfo getCurrent() {
            return current;
        }

        public void setCurrent(VFileInfo current) {
            this.current = current;
        }

        public List<VFileInfo> getFiles() {
            return files;
        }

        public void setFiles(List<VFileInfo> files) {
            this.files = files;
        }

        public VirtualFileSystem getFileSystem() {
            return fileSystem;
        }

        public void setFileSystem(VirtualFileSystem fileSystem) {
            this.fileSystem = fileSystem;
        }

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

}
