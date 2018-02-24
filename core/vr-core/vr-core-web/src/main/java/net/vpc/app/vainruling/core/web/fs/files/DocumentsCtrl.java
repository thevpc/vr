/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs.files;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.fs.VrFSEntry;
import net.vpc.app.vainruling.core.service.fs.VrFSTable;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrlData;
import net.vpc.app.vainruling.core.web.UCtrlProvider;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.app.vainruling.core.web.menu.VRMenuDef;
import net.vpc.app.vainruling.core.web.menu.VRMenuDefFactory;
import net.vpc.app.vainruling.core.web.menu.VRMenuLabel;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.core.web.util.FileUploadEventHandler;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.*;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
//        title = "Documents", css = "fa-dashboard",
        url = "modules/files/documents"
//        ,menu = "/FileSystem", securityKey = "Custom.FileSystem.Documents"
)
public class DocumentsCtrl implements VRMenuDefFactory, UCtrlProvider,DocumentUploadListener {

    private static final Logger log = Logger.getLogger(DocumentsCtrl.class.getName());
    @Autowired
    private CorePlugin core;


    private Model model = new Model();


    @Override
    public List<VRMenuDef> createVRMenuDefList() {
        List<VRMenuDef> m = new ArrayList<>();
        m.add(new VRMenuDef("Documents Privés", "/FileSystem", "documents", "{type:'home'}", CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM, null, "", 100, new VRMenuLabel[0]));
        m.add(new VRMenuDef("Mes Documents", "/FileSystem", "documents", "{type:'all'}", CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM, null, "", 100, new VRMenuLabel[0]));
        m.add(new VRMenuDef("Tous les Documents", "/FileSystem", "documents", "{type:'root'}", CorePluginSecurity.RIGHT_CUSTOM_FILESYSTEM_ROOT_FILE_SYSTEM, null, "", 500, new VRMenuLabel[0]));
        return m;
    }

    @Override
    public UCtrlData getUCtrl(String cmd) {
        try {
            Config c = VrUtils.parseJSONObject(cmd, Config.class);

            if (c == null) {
                c = new Config();
            }
            UCtrlData d = new UCtrlData();
            d.setUrl("modules/files/documents");
            d.setCss("fa-table");

            String login = core.getCurrentUserLogin();
            if ("root".equals(c.getType())) {
                d.setTitle("Documents Racine");
                d.setSecurityKey(CorePluginSecurity.RIGHT_CUSTOM_FILESYSTEM_ROOT_FILE_SYSTEM);
            } else if ("user".equals(c.getType())) {
                d.setSecurityKey(CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);
                String v = c.getValue();
                if (StringUtils.isEmpty(v)) {
                    v = login;
                }
                d.setTitle("Documents de " + v);
            } else if ("home".equals(c.getType())) {
                d.setSecurityKey(CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);
                d.setTitle("Mes Documents");
            } else if ("profile".equals(c.getType())) {
                d.setSecurityKey(CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);
                String v = c.getValue();
                if (StringUtils.isEmpty(v)) {
                    v = "user";
                }
                d.setTitle("Documents de " + v);
            } else {
                d.setSecurityKey(CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);
                d.setTitle("Tous les documents");
            }
            List<BreadcrumbItem> items = new ArrayList<>();
            items.add(new BreadcrumbItem(I18n.get().getOrNull("Controller.Documents"), I18n.get().getOrNull("Controller.Documents.subTitle"), "fa-dashboard", "", ""));
            d.setBreadcrumb(items.toArray(new BreadcrumbItem[items.size()]));
            return d;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error", ex);
        }
        return null;
    }

    @OnPageLoad
    public void init(Config cmd) {
        getModel().setConfig(cmd);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        Config c = getModel().getConfig();
        if (c == null) {
            c = new Config();
        }
        VirtualFileSystem rootfs = UPA.getContext().invokePrivileged(new Action<VirtualFileSystem>() {
            @Override
            public VirtualFileSystem run() {
                return core.getRootFileSystem();
            }
        });
        VirtualFileSystem fs = null;
        String login = core.getCurrentUserLogin();
        if (StringUtils.isEmpty(login)) {
            fs = VFS.EMPTY_FS;
        } else {
            if ("root".equals(c.getType())) {
                fs = rootfs;
            } else if ("home".equals(c.getType())) {
                fs = core.getUserHomeFileSystem(login);
            } else if ("user".equals(c.getType())) {
                String v = c.getValue();
                if (StringUtils.isEmpty(v)) {
                    v = login;
                }
                fs = core.getUserHomeFileSystem(v);
            } else if ("profile".equals(c.getType())) {
                String v = c.getValue();
                if (StringUtils.isEmpty(v)) {
                    v = "user";
                }
                fs = core.getProfileFileSystem(v);
            } else {
                fs = core.getUserFileSystem(login);
            }
        }
        getModel().setFileSystem(fs);
        updatePath(c.getPath());
        onRefresh();
    }

    public void updatePath(String path) {
        if (StringUtils.isEmpty(path)) {
            path = "/";
        }
        VFile file = getModel().getFileSystem().get(path);
        if (file.exists() && file.isDirectory()) {
            getModel().setCurrent(DocumentsUtils.createFileInfo(path, VFileKind.ORDINARY, file));
        } else {
            getModel().setCurrent(DocumentsUtils.createFileInfo("/", VFileKind.ROOT, getModel().getFileSystem().get("/")));
        }
        TraceService.get().trace("visit-document", "path visited " + getModel().getCurrent().getFile().getPath(), getModel().getCurrent().getFile().getPath(), "/System/Access", Level.FINE);
        onRefresh();
    }

//    protected VirtualFileSystem createFS() {
//        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
//        VirtualFileSystem rootfs = fsp.getRootFileSystem();
//        VirtualFileSystem userfs = rootfs.filter(null);
//        return userfs;
//    }

    public void updateCurrent(VFile file) {
        getModel().setCurrent(DocumentsUtils.createFileInfo(file));
        TraceService.get().trace("visit-document", "path visited " + getModel().getCurrent().getFile().getPath(), getModel().getCurrent().getFile().getPath(), "/System/Access", Level.FINE);
        onRefresh();
    }

    public StreamedContent downloadPath(String path) {
        InputStream stream = null;
        try {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            final VFile f = UPA.getContext().invokePrivileged(new Action<VirtualFileSystem>() {
                @Override
                public VirtualFileSystem run() {
                    return core.getRootFileSystem();
                }
            }).get(path);
            if (f.exists() && f.isFile()) {
                core.markDownloaded(f);
                stream = f.getInputStream();
                return new DefaultStreamedContent(stream, f.probeContentType(), f.getName());
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public StreamedContent getContent(VFileInfo i) {
        StreamedContent content = DocumentsUtils.getContent(i);
        if (content != null) {
            onRefresh();
        }
        return content;
    }

    public void onNewFolder() {
        getModel().setNewFile(false);
        getModel().setNewFolder(true);
        int x = 1;
        VFile file0 = getModel().getCurrent().getFile();
        while (true) {
            VFile vFile = file0.get("Nouveau Dossier" + (x == 1 ? "" : (" "+String.valueOf(x))));
            if (!vFile.exists()) {
                file0 = vFile;
                break;
            }
            x++;
        }
        getModel().setNewName(file0.getName());

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/files/documents-newname-dialog", options, null);
    }

    public void onRemove() {
        try {
            for (VFileInfo file : getModel().getFiles()) {
                if (file.isSelected()) {
                    file.getFile().deleteAll();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        onRefresh();
    }

    public void onNewFile() {
        getModel().setNewFile(false);
        getModel().setNewFolder(true);
        getModel().setNewName("Nouveau Fichier.txt");
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("/modules/files/documents-newname-dialog", options, null);
    }

    public void onUpload() {
        DocumentsUploadDialogCtrl docs = VrApp.getBean(DocumentsUploadDialogCtrl.class);
        docs.openCustomDialog(new DocumentsUploadDialogCtrl.Config(), this);
    }

    public void onCancel() {
        getModel().setNewFolder(false);
        getModel().setNewFile(false);
    }

    public void onSaveSecurity() {
        VFileInfo current = getModel().getCurrent();
        VFile file = current.getFile();
        if (!file.getACL().isReadOnly()) {
            current.writeACL();
        }
        if (current.isSharable()) {
            if (core.isCurrentSessionAdmin() || core.getCurrentUserLogin().equals(file.getACL().getOwner())) {
                String baseFile = file.getBaseFile("vrfs").getPath();

                try {
                    core.setUserLinkPathEntry(core.getCurrentUserId(), new VrFSEntry(current.getShareProfiles(), "Profile", current.getShareName(), baseFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        fireEventExtraDialogClosed();
    }

    public void onSave() {
        if (getModel().isNewFile()) {
            String n = VrUtils.normalizeFilePath(getModel().getNewName().trim());
            VFile f2 = getModel().getCurrent().getFile().get(n);
            try {
                f2.writeBytes(new byte[0]);
            } catch (IOException ex) {
                Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                FacesUtils.addErrorMessage("Empty File " + f2.getPath() + " could not be created.");
            }
        } else if (getModel().isNewFolder()) {
            String n = VrUtils.normalizeFilePath(getModel().getNewName().trim());
            VFile f2 = getModel().getCurrent().getFile().get(n);
            try {
                if (!f2.mkdirs()) {
                    FacesUtils.addErrorMessage("Directory " + f2.getPath() + " could not be created.");
                }
            } catch (Exception ex) {
                Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                FacesUtils.addErrorMessage(f2.getPath() + " could not be created.");
            }
        }
        onRefresh();
        fireEventExtraDialogClosed();
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(":listForm");
//        FacesContext.getCurrentInstance().getPartialViewContext().setRenderAll(true);//.add("listForm");
    }

    public void onRefresh() {
        getModel().setFiles(DocumentsUtils.loadFiles(getModel().getCurrent().getFile()));
    }


    public Model getModel() {
        return model;
    }

    public boolean isEnabledButton(String buttonId) {
        if ("Refresh".equals(buttonId)) {
            return true;
        }
        if ("NewFile".equals(buttonId)) {
            // I think this is useless, will be removed
            return false;
//            return getModel().getArea().isEmpty()
//                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePlugin.RIGHT_FILESYSTEM_WRITE)
//                    && getModel().getCurrent().getFile().isAllowedCreateChild(VFileType.FILE, null);
        }
        if ("NewFolder".equals(buttonId)) {
            return
                    UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE)
                    && getModel().getCurrent().getFile().isAllowedCreateChild(VFileType.DIRECTORY, null);
        }
        if ("Upload".equals(buttonId)) {
            VFile file = getModel().getCurrent().getFile();
            return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE)
                    && (file.isAllowedCreateChild(VFileType.FILE, null)
                    || file.isAllowedUpdateChild(VFileType.FILE, null));
        }
        if ("Remove".equals(buttonId)) {
            return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE)
                    && getModel().getCurrent().getFile().isAllowedRemoveChild(null, null);
        }
//        if ("Save".equals(buttonId)) {
//            return getModel().getArea().equals("NewFile") || getModel().getArea().equals("NewFolder");
//        }
//        if ("Cancel".equals(buttonId)) {
//            return true;
//        }
        if ("SelectFile".equals(buttonId)) {
            return true;
        }
        if ("Security".equals(buttonId)) {
            return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_ASSIGN_RIGHTS)
                    &&
                    !getModel().getCurrent().getFile().getACL().isReadOnly()
                    &&
                    (core.isCurrentSessionAdmin() || core.getCurrentUserLogin().equals(getModel().getCurrent().getFile().getACL().getOwner()))
                    ;
        }
        return core.isCurrentSessionAdmin();
    }

    public boolean isEnabledButton(String buttonId, VFileInfo forFile) {
        if ("Refresh".equals(buttonId)) {
            return true;
        }
        return true;
//        return UserSession.get().isAdmin();
    }

    @Override
    public void onUpload(FileUploadEvent event) {
        try {
            UPA.getContext().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
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
                    } catch (IOException e1) {
                        Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, e1);
                        FacesUtils.addErrorMessage(e1, event.getFile().getFileName() + " uploading failed.");
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex, event.getFile().getFileName() + " uploading failed.");
        }
        onRefresh();
    }

    public void onShowSecurityDialog() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        VFile file = getModel().getCurrent().getFile();
        getModel().getCurrent().setSharable(file.isDirectory());
        if (getModel().getCurrent().isSharable()) {
            Integer currentUserId = core.getCurrentUserId();
            if (currentUserId != null) {
                VrFSTable userVrFSTable = core.getUserVrFSTable(currentUserId);
                VrFSEntry[] u = userVrFSTable.getEntriesByLinkPath(file.getBaseFile("vrfs").getPath());
                if (u.length > 0) {
                    getModel().getCurrent().setShareName(u[0].getMountPoint());
                    getModel().getCurrent().setShareProfiles(u[0].getFilterName());
                }
            } else {
                return;
            }
        }
        //check is advanced
        getModel().getCurrent().setAdvanced(getModel().getCurrent().isAcceptAdvanced());
        getModel().getCurrent().readACL();
        RequestContext.getCurrentInstance().openDialog("/modules/files/documents-security-dialog", options, null);
        onRefresh();

    }

    public void onChangeAdvancedACL(){
        getModel().getCurrent().setAdvanced(getModel().getCurrent().isAdvanced());
        getModel().getCurrent().readACL();
    }

    public void fireEventExtraDialogClosed() {
        //Object obj
        RequestContext.getCurrentInstance().closeDialog(new DialogResult(null, null));
    }


    public static class Config {

        private String type;
        private String value;
        private String path;

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

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class Model {

        VFileInfo current;
        VirtualFileSystem fileSystem;
        Config config;
        private List<VFileInfo> files = new ArrayList<>();
        private String newName;
        private boolean newFolder;
        private boolean newFile;

        public boolean isNewFile() {
            return newFile;
        }

        public void setNewFile(boolean newFile) {
            this.newFile = newFile;
        }

        public boolean isNewFolder() {
            return newFolder;
        }

        public void setNewFolder(boolean newFolder) {
            this.newFolder = newFolder;
        }

        public String getNewName() {
            return newName;
        }

        public void setNewName(String newName) {
            this.newName = newName;
        }

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
    }
}
