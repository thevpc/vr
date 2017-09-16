/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs.files;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UCtrlData;
import net.vpc.app.vainruling.core.web.UCtrlProvider;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.app.vainruling.core.web.menu.VRMenuDef;
import net.vpc.app.vainruling.core.web.menu.VRMenuDefFactory;
import net.vpc.app.vainruling.core.web.menu.VRMenuLabel;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.core.web.util.FileUploadEventHandler;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileType;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.common.vfs.impl.EmptyVFS;
import net.vpc.upa.UPA;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
public class DocumentsCtrl implements VRMenuDefFactory, UCtrlProvider {

    private static final Logger log = Logger.getLogger(DocumentsCtrl.class.getName());
    @Autowired
    private CorePlugin core;


    private Model model = new Model();


    @Override
    public List<VRMenuDef> createVRMenuDefList() {
        List<VRMenuDef> m = new ArrayList<>();
        m.add(new VRMenuDef("Documents Privés", "/FileSystem", "documents", "{type:'home'}", "Custom.FileSystem.MyFileSystem", null,"", 100, new VRMenuLabel[0]));
        m.add(new VRMenuDef("Mes Documents", "/FileSystem", "documents", "{type:'all'}", "Custom.FileSystem.MyFileSystem", null,"", 100, new VRMenuLabel[0]));
        m.add(new VRMenuDef("Tous les Documents", "/FileSystem", "documents", "{type:'root'}", "Custom.FileSystem.RootFileSystem", null,"", 500, new VRMenuLabel[0]));
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

            String login = UserSession.getCurrentLogin();
            if ("root".equals(c.getType())) {
                d.setTitle("Documents Racine");
                d.setSecurityKey("Custom.FileSystem.RootFileSystem");
            } else if ("user".equals(c.getType())) {
                d.setSecurityKey("Custom.FileSystem.MyFileSystem");
                String v = c.getValue();
                if (StringUtils.isEmpty(v)) {
                    v = login;
                }
                d.setTitle("Documents de " + v);
            } else if ("home".equals(c.getType())) {
                d.setSecurityKey("Custom.FileSystem.MyFileSystem");
                d.setTitle("Mes Documents");
            } else if ("profile".equals(c.getType())) {
                d.setSecurityKey("Custom.FileSystem.MyFileSystem");
                String v = c.getValue();
                if (StringUtils.isEmpty(v)) {
                    v = "user";
                }
                d.setTitle("Documents de " + v);
            } else {
                d.setSecurityKey("Custom.FileSystem.MyFileSystem");
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
        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
        Config c = getModel().getConfig();
        if (c == null) {
            c = new Config();
        }
        VirtualFileSystem rootfs = fsp.getFileSystem();
        VirtualFileSystem fs = null;
        String login = UserSession.getCurrentLogin();
        if(StringUtils.isEmpty(login)){
            fs= VFS.EMPTY_FS;
        }else {
            if ("root".equals(c.getType())) {
                fs = rootfs;
            } else if ("home".equals(c.getType())) {
                fs = fsp.getUserHomeFileSystem(login);
            } else if ("user".equals(c.getType())) {
                String v = c.getValue();
                if (StringUtils.isEmpty(v)) {
                    v = login;
                }
                fs = fsp.getUserHomeFileSystem(v);
            } else if ("profile".equals(c.getType())) {
                String v = c.getValue();
                if (StringUtils.isEmpty(v)) {
                    v = "user";
                }
                fs = fsp.getProfileFileSystem(v);
            } else {
                fs = fsp.getUserFileSystem(login);
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
        UserSession.get().setLastVisitedPageInfo(getModel().getCurrent().getFile().getPath());
        onRefresh();
    }

    protected VirtualFileSystem createFS() {
        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
        VirtualFileSystem rootfs = fsp.getFileSystem();
        VirtualFileSystem userfs = rootfs.filter(null);
        return userfs;
    }

    public void updateCurrent(VFile file) {
        getModel().setCurrent(DocumentsUtils.createFileInfo(file));
        UserSession.get().setLastVisitedPageInfo(getModel().getCurrent().getFile().getPath());
        onRefresh();
    }

    public StreamedContent downloadPath(String path) {
        InputStream stream = null;
        try {
            CorePlugin fsp = VrApp.getBean(CorePlugin.class);
            final VFile f = fsp.getFileSystem().get(path);
            if (f.exists() && f.isFile()) {
                fsp.markDownloaded(f);
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

    public void resetArea() {
        getModel().setArea("");
    }

    public void onNewFolder() {
        if (getModel().getArea().equals("NewFolder")) {
            resetArea();
            //nothing to do!
        } else {
            getModel().setArea("NewFolder");
            int x = 1;
            VFile file0 = getModel().getCurrent().getFile();
            while (true) {
                VFile vFile = file0.get("NouveauDossier" + (x == 1 ? "" : String.valueOf(x)));
                if (!vFile.exists()) {
                    file0 = vFile;
                    break;
                }
                x++;
            }
            getModel().setNewName(file0.getName());
        }
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
        if (getModel().getArea().equals("NewFile")) {
            resetArea();
            //nothing to do!
        } else {
            getModel().setArea("NewFile");
            getModel().setNewName("NouveauFichier");
        }
    }

    public void onUpload() {
        if (getModel().getArea().equals("Upload")) {
            resetArea();
            //nothing to do!
        } else {
            resetArea();
            getModel().setArea("Upload");
            getModel().setNewUploadName("");
        }
    }

    public void onCancel() {
        resetArea();
    }

    public void onSaveSecurity() {
        VFileInfo current = getModel().getCurrent();
        VFile file = current.getFile();
        if (!file.getACL().isReadOnly()) {
            current.writeACL();
        }
        fireEventExtraDialogClosed();
    }

    public void onSave() {
        if (!StringUtils.isEmpty(getModel().getArea())) {
            if ("NewFile".equals(getModel().getArea())) {
                String n = core.normalizeFilePath(getModel().getNewName().trim());
                VFile f2 = getModel().getCurrent().getFile().get(n);
                try {
                    f2.writeBytes(new byte[0]);
                } catch (IOException ex) {
                    Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                    FacesUtils.addErrorMessage("Empty File " + f2.getPath() + " could not be created.");
                }
            } else if ("NewFolder".equals(getModel().getArea())) {
                String n = core.normalizeFilePath(getModel().getNewName().trim());
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
        }
        resetArea();
        onRefresh();
    }

    public void onRefresh() {
        getModel().setFiles(DocumentsUtils.loadFiles(getModel().getCurrent().getFile()));
    }


    public Model getModel() {
        return model;
    }

    public boolean isEnabledButton(String buttonId) {
        if ("Refresh".equals(buttonId)) {
            return getModel().getArea().isEmpty();
        }
        if ("NewFile".equals(buttonId)) {
            // I think this is useless, will be removed
            return false;
//            return getModel().getArea().isEmpty()
//                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePlugin.RIGHT_FILESYSTEM_WRITE)
//                    && getModel().getCurrent().getFile().isAllowedCreateChild(VFileType.FILE, null);
        }
        if ("NewFolder".equals(buttonId)) {
            return getModel().getArea().isEmpty()
                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePlugin.RIGHT_FILESYSTEM_WRITE)
                    && getModel().getCurrent().getFile().isAllowedCreateChild(VFileType.DIRECTORY, null);
        }
        if ("Upload".equals(buttonId)) {
            VFile file = getModel().getCurrent().getFile();
            return getModel().getArea().isEmpty()
                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePlugin.RIGHT_FILESYSTEM_WRITE)
                    && (file.isAllowedCreateChild(VFileType.FILE, null)
                    || file.isAllowedUpdateChild(VFileType.FILE, null));
        }
        if ("Remove".equals(buttonId)) {
            return getModel().getArea().isEmpty()
                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePlugin.RIGHT_FILESYSTEM_WRITE)
                    && getModel().getCurrent().getFile().isAllowedRemoveChild(null, null);
        }
        if ("Save".equals(buttonId)) {
            return getModel().getArea().equals("NewFile") || getModel().getArea().equals("NewFolder");
        }
        if ("Cancel".equals(buttonId)) {
            return !getModel().getArea().isEmpty();
        }
        if ("SelectFile".equals(buttonId)) {
            return getModel().getArea().isEmpty();
        }
        if ("Security".equals(buttonId)) {
            return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePlugin.RIGHT_FILESYSTEM_ASSIGN_RIGHTS)
                    &&
                    !getModel().getCurrent().getFile().getACL().isReadOnly();
        }
        return UserSession.get().isAdmin();
    }

    public boolean isEnabledButton(String buttonId, VFileInfo forFile) {
        if ("Refresh".equals(buttonId)) {
            return true;
        }
        return true;
//        return UserSession.get().isAdmin();
    }

    public void handleNewFile(FileUploadEvent event) {
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
                FacesUtils.addErrorMessage(ex ,event.getFile().getFileName() + " uploading failed.");
            }
        } finally {
            resetArea();
        }
        onRefresh();
    }

    public void handleUpdatedFile(FileUploadEvent event) {
        try {
            CorePlugin.get().uploadFile(getModel().getCurrent().getFile(), new FileUploadEventHandler(event) {
                @Override
                public boolean acceptOverride(VFile file) {
                    return true;
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex,event.getFile().getFileName() + " uploading failed.");
        }

    }

    public void onShowSecurityDialog() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);

        RequestContext.getCurrentInstance().openDialog("/modules/files/documents-security-dialog", options, null);
        onRefresh();

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
        private String area = "";
        private String newName;
        private String newUploadName;

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

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getNewUploadName() {
            return newUploadName;
        }

        public void setNewUploadName(String newUploadName) {
            this.newUploadName = newUploadName;
        }

    }
}
