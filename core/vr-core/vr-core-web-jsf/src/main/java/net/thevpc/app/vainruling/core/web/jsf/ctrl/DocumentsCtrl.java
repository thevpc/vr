/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.CorePluginSecurity;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.dialog.DocumentsUploadDialogCtrl;
import net.thevpc.app.vainruling.core.web.util.DocumentsUtils;
import net.thevpc.app.vainruling.VrPageInfo;
import net.thevpc.app.vainruling.core.service.TraceService;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.fs.VFileInfo;
import net.thevpc.app.vainruling.core.service.fs.VFileKind;
import net.thevpc.app.vainruling.core.service.util.I18n;
import net.thevpc.app.vainruling.core.web.jsf.DialogBuilder;
import net.thevpc.app.vainruling.core.web.jsf.VrJsf;
import net.thevpc.app.vainruling.VrBreadcrumbItem;
import net.thevpc.app.vainruling.VrMenuInfo;
import net.thevpc.app.vainruling.VrMenuLabel;
import net.thevpc.app.vainruling.core.service.editor.DialogResult;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.vfs.VFS;
import net.thevpc.common.vfs.VFile;
import net.thevpc.common.vfs.VFileType;
import net.thevpc.common.vfs.VirtualFileSystem;
import net.thevpc.upa.Action;
import net.thevpc.upa.UPA;
import net.thevpc.upa.VoidAction;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.thevpc.common.util.MapUtils;
import net.thevpc.app.vainruling.VrPageInfoResolver;
import net.thevpc.app.vainruling.VrPage;
import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrMenuProvider;
import net.thevpc.app.vainruling.core.service.CorePluginSSE;
import net.thevpc.app.vainruling.core.service.model.AppFsSharing;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        //        title = "Documents", css = "fa-dashboard",
        url = "modules/files/documents"
//        ,menu = "/FileSystem", securityKey = "Custom.FileSystem.Documents"
)
@Controller
public class DocumentsCtrl implements VrMenuProvider, VrPageInfoResolver, DocumentUploadListener {

    private static final Logger log = Logger.getLogger(DocumentsCtrl.class.getName());
    @Autowired
    private CorePlugin core;

    private Model model = new Model();

    @Override
    public List<VrMenuInfo> createCustomMenus() {
        List<VrMenuInfo> m = new ArrayList<>();
        m.add(new VrMenuInfo("Documents Privés", "/FileSystem", "documents", "{type:'home'}", CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM, null, "", 100, new VrMenuLabel[0]));
        m.add(new VrMenuInfo("Mes Documents", "/FileSystem", "documents", "{type:'all'}", CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM, null, "", 100, new VrMenuLabel[0]));
        m.add(new VrMenuInfo("Tous les Documents", "/FileSystem", "documents", "{type:'root'}", CorePluginSecurity.RIGHT_CUSTOM_FILESYSTEM_ROOT_FILE_SYSTEM, null, "", 500, new VrMenuLabel[0]));
        return m;
    }

    @Override
    public VrPageInfo resolvePageInfo(String cmd) {
        try {
            Config c = VrUtils.parseJSONObject(cmd, Config.class);

            if (c == null) {
                c = new Config();
            }
            VrPageInfo d = new VrPageInfo();
            d.setControllerName("documents");
            d.setCmd(cmd);
            d.setUrl("modules/files/documents");
            d.setCss("fa-table");

            String login = core.getCurrentUserLogin();
            if ("root".equals(c.getType())) {
                d.setTitle("Documents Racine");
                d.setSecurityKey(CorePluginSecurity.RIGHT_CUSTOM_FILESYSTEM_ROOT_FILE_SYSTEM);
            } else if ("user".equals(c.getType())) {
                d.setSecurityKey(CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);
                String v = c.getValue();
                if (StringUtils.isBlank(v)) {
                    v = login;
                }
                d.setTitle("Documents de " + v);
            } else if ("home".equals(c.getType())) {
                d.setSecurityKey(CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);
                d.setTitle("Mes Documents");
            } else if ("profile".equals(c.getType())) {
                d.setSecurityKey(CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);
                String v = c.getValue();
                if (StringUtils.isBlank(v)) {
                    v = "user";
                }
                d.setTitle("Documents de " + v);
            } else {
                d.setSecurityKey(CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);
                d.setTitle("Tous les documents");
            }
            List<VrBreadcrumbItem> items = new ArrayList<>();
            items.add(new VrBreadcrumbItem(I18n.get().getOrNull("Controller.Documents"), I18n.get().getOrNull("Controller.Documents.subTitle"), "fa-dashboard", "", ""));
            d.setBreadcrumb(items.toArray(new VrBreadcrumbItem[items.size()]));
            return d;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error", ex);
        }
        return null;
    }

    @VrOnPageLoad
    public void init(Config cmd) {
        getModel().setConfig(cmd);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        Config c = getModel().getConfig();
        if (c == null) {
            c = new Config();
        }
        VirtualFileSystem rootfs = UPA.getContext().invokePrivileged(core::getRootFileSystem);
        VirtualFileSystem fs = null;
        String login = core.getCurrentUserLogin();
        if (StringUtils.isBlank(login)) {
            fs = VFS.EMPTY_FS;
        } else {
            if ("root".equals(c.getType())) {
                fs = rootfs;
            } else if ("home".equals(c.getType())) {
                fs = core.getUserHomeFileSystem(login);
            } else if ("user".equals(c.getType())) {
                String v = c.getValue();
                if (StringUtils.isBlank(v)) {
                    v = login;
                }
                fs = core.getUserHomeFileSystem(v);
            } else if ("profile".equals(c.getType())) {
                String v = c.getValue();
                if (StringUtils.isBlank(v)) {
                    v = "user";
                }
                fs = core.getProfileFileSystem(v);
            } else {
                fs = core.getUserFileSystem(login);
            }
        }
        getModel().setSearchString(null);
        getModel().setFileSystem(fs);
        updatePath(c.getPath());
        onRefresh();
    }

    public void updatePath(String path) {
        if (StringUtils.isBlank(path)) {
            path = "/";
        }
        VFile file = getModel().getFileSystem().get(path);
        if (file.exists() && file.isDirectory()) {
            getModel().setCurrent(DocumentsUtils.createFileInfo(path, VFileKind.ORDINARY, file));
        } else {
            getModel().setCurrent(DocumentsUtils.createFileInfo("/", VFileKind.ROOT, getModel().getFileSystem().get("/")));
        }
        TraceService.get().trace("System.actions.visit-document", null, MapUtils.map("path", getModel().getCurrent().getFile().getPath()), "/System/Access", Level.FINE);
        onRefresh();
    }

    //    protected VirtualFileSystem createFS() {
//        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
//        VirtualFileSystem rootfs = fsp.getRootFileSystem();
//        VirtualFileSystem userfs = rootfs.filter(null);
//        return userfs;
//    }
    public void switchSelectionMode() {
        getModel().setSelectionMode(!getModel().isSelectionMode());
        if (!getModel().isSelectionMode() && getModel().getFiles() != null) {
            for (VFileInfo file : getModel().getFiles()) {
                file.setSelected(false);
            }
        }
    }

    public boolean hasParent() {
        VFileInfo c = getModel().getCurrent();
        if (c == null) {
            return false;
        }
        VFile f = c.getFile();
        if (!f.getPath().equals("/")) {
            VFile p = f.getParentFile();
            if (p != null) {
                return true;
            }
        }
        return false;
    }

    private void reloadCurrent() {
        if (getModel().getCurrent() != null) {
            updateCurrent(getModel().getCurrent().getFile());
        }
    }

    public void goParent() {
        VFileInfo c = getModel().getCurrent();
        if (c == null) {
            return;
        }
        VFile f = c.getFile();
        if (!f.getPath().equals("/")) {
            VFile p = f.getParentFile();
            if (p != null) {
                updateCurrent(p);
            }
        }
    }

    public void onPaste() {
        VirtualFileSystem rfs = UPA.getPersistenceUnit().invokePrivileged(() -> CorePlugin.get().getRootFileSystem());
        try {
            for (Iterator<String> it = getModel().getCopiedFiles().iterator(); it.hasNext();) {
                String file = it.next();
                VFile of = rfs.get(file);
                if (of.exists()) {
                    if (getModel().isCut()) {
                        of.renameTo(getModel().getCurrent().getFile().get(of.getName()));
                    } else {
                        of.copyTo(getModel().getCurrent().getFile().get(of.getName()));
                    }
                }
                it.remove();
            }
            getModel().setCut(false);
            reloadCurrent();
        } catch (IOException ex) {
            FacesUtils.addErrorMessage(ex);
            log.log(Level.SEVERE, null, ex);
        }
    }

    public void onCopy() {
        copySelected(false);
    }

    public void onCut() {
        copySelected(true);
    }

    public void copySelected(boolean cut) {
        boolean someCopied = false;
        getModel().setCut(false);
        getModel().getCopiedFiles().clear();
        for (VFileInfo file : getModel().getFiles()) {
            if (file.isSelected()) {
                String rp = CorePluginSSE.getRootPath(file.getFile());
                if (rp != null) {
                    getModel().getCopiedFiles().add(rp);
                    someCopied = true;
                }
            }
        }
        if (someCopied && cut) {
            getModel().setCut(true);
        }
        reloadCurrent();
    }

    public void updateCurrent(VFile file) {
        getModel().setCurrent(DocumentsUtils.createFileInfo(file));
        TraceService.get().trace("System.actions.visit-document", null, MapUtils.map("path", getModel().getCurrent().getFile().getPath()), "/System/Access", Level.FINE);
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
        StreamedContent content = VrJsf.getContent(i);
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
            VFile vFile = file0.get("Nouveau Dossier" + (x == 1 ? "" : (" " + String.valueOf(x))));
            if (!vFile.exists()) {
                file0 = vFile;
                break;
            }
            x++;
        }
        getModel().setNewName(file0.getName());
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
        new DialogBuilder("/modules/files/documents-newname-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
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
                List<AppFsSharing> s = getModel().getCurrentSharings();
                AppFsSharing y = null;
                if (s != null && s.size() > 0) {
                    y = s.get(0);
                    if (StringUtils.isBlank(current.getShareName())) {
                        core.removeFsSharing(y.getId());
                    } else {
                        y.setMountPath(current.getShareName());
                        y.setAllowedUsers(current.getShareProfiles());
                        y.setDisabled(false);
                        try {
                            core.saveFsSharing(y);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                            FacesUtils.addErrorMessage("Could not be saved.");
                        }
                    }
                } else {
                    y = new AppFsSharing();
                    y.setUser(core.getCurrentUser());
                    y.setSharedPath(baseFile);
                    y.setMountPath(current.getShareName());
                    y.setAllowedUsers(current.getShareProfiles());
                    y.setDisabled(false);
                    try {
                        core.saveFsSharing(y);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                        FacesUtils.addErrorMessage("Could not be saved.");
                    }
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
        getModel().setNewName("NoName");
        onRefresh();
//        fireEventExtraDialogClosed();
//        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(":listForm");
//        FacesContext.getCurrentInstance().getPartialViewContext().setRenderAll(true);//.add("listForm");
    }

    public boolean isCopied(VFile file) {
        if (file == null) {
            return false;
        }
        String p = CorePluginSSE.getRootPath(file);
        return getModel().getCopiedFiles().contains(p);
    }

    public void onRefresh() {
        if (getModel().getCurrent() == null) {
            getModel().setFiles(new ArrayList<>());
        } else {
            List<VFileInfo> searchFiles = DocumentsUtils.searchFiles(getModel().getCurrent().getFile(), getModel().getSearchString());
            for (VFileInfo searchFile : searchFiles) {
                VFile f = searchFile.getFile();
                if (isCopied(f)) {
                    if (getModel().isCut()) {
                        searchFile.setCut(true);
                    } else {
                        searchFile.setCopied(true);
                    }
                } else {
                    searchFile.setCut(false);
                    searchFile.setCopied(false);
                }
            }
            getModel().setFiles(searchFiles);
        }
//        if(FacesContext.getCurrentInstance()!=null) {
//            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("listForm");
//        }
    }

    public void setListMode(boolean b) {
        getModel().setListMode(b);
    }

    public Model getModel() {
        return model;
    }

    public boolean isSelectable(VFileInfo i) {
        return i != null && getModel().isSelectionMode()
                && i.isSelectable()
                && isEnabledButton("SelectFile");
    }

    public boolean isEnabledButton(String buttonId) {
        if ("Refresh".equals(buttonId)) {
            return true;
        }
        if ("Cancel".equals(buttonId)) {
            return false;
        }
//        if ("NewFile".equals(buttonId)) {
//            // I think this is useless, will be removed
//            return false;
////            return getModel().getArea().isEmpty()
////                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePlugin.RIGHT_FILESYSTEM_WRITE)
////                    && getModel().getCurrent().getFile().isAllowedCreateChild(VFileType.FILE, null);
//        }
        VFileInfo current = getModel().getCurrent();
        if (current == null) {
            return false;
        }
        if ("Paste".equals(buttonId)) {
            Set<String> f = getModel().getCopiedFiles();
            if (f.isEmpty()) {
                return false;
            }
            VirtualFileSystem rootfs = UPA.getContext().invokePrivileged(core::getRootFileSystem);
            Boolean FILE_RIGHT = null;
            Boolean FOLDER_RIGHT = null;
            for (String copiedFile : f) {
                VFile r = rootfs.get(copiedFile);
                if (r != null) {
                    if (r.isDirectory()) {
                        if (FOLDER_RIGHT == null) {
                            FOLDER_RIGHT = isEnabledButton("NewFolder");
                        }
                        if (FOLDER_RIGHT) {
                            return true;
                        }
                    } else {
                        if (FILE_RIGHT == null) {
                            FILE_RIGHT = isEnabledButton("NewFile");
                        }
                        if (FILE_RIGHT) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        if ("NewFolder".equals(buttonId)) {
            return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE)
                    && current.getFile().isAllowedCreateChild(VFileType.DIRECTORY, null);
        }
        if ("Upload".equals(buttonId) || "NewFile".equals(buttonId)) {
            VFile file = current.getFile();
            return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE)
                    && (file.isAllowedCreateChild(VFileType.FILE, null)
                    || file.isAllowedUpdateChild(VFileType.FILE, null));
        }
        if ("Remove".equals(buttonId)) {
            return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE)
                    && current.getFile().isAllowedRemoveChild(null, null);
        }
        if ("Cut".equals(buttonId)) {
            return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(CorePluginSecurity.RIGHT_FILESYSTEM_WRITE)
                    && current.getFile().isAllowedRemoveChild(null, null);
        }
        if ("copy".equals(buttonId)) {
            return true;
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
                    && !current.getFile().getACL().isReadOnly()
                    && (core.isCurrentSessionAdmin() || core.getCurrentUserLogin().equals(current.getFile().getACL().getOwner()));
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
        VFile file = getModel().getCurrent().getFile();
        getModel().getCurrent().setSharable(file.isDirectory());
        if (getModel().getCurrent().isSharable()) {
            Integer currentUserId = core.getCurrentUserId();
            if (currentUserId != null) {
                List<AppFsSharing> u = core.findFsSharings(currentUserId, null, file.getBaseFile("vrfs").getPath());
                if (u.size() > 0) {
                    AppFsSharing item = u.get(0);
                    getModel().getCurrent().setShareName(item.getMountPath());
                    getModel().getCurrent().setShareProfiles(item.getAllowedUsers());
                    getModel().setCurrentSharings(u);
                } else {
                    getModel().getCurrent().setShareName("");
                    getModel().getCurrent().setShareProfiles("");
                    getModel().setCurrentSharings(new ArrayList<>());
                }
            } else {
                return;
            }
        }
        //check is advanced
        getModel().getCurrent().setAdvanced(getModel().getCurrent().isAcceptAdvanced());
        getModel().getCurrent().readACL();
        new DialogBuilder("/modules/files/documents-security-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .setHeight(600)
                .setContentHeight("100%")
                .open();
        onRefresh();

    }

    public void onChangeAdvancedACL() {
        getModel().getCurrent().setAdvanced(getModel().getCurrent().isAdvanced());
        getModel().getCurrent().readACL();
    }

    public void fireEventExtraDialogClosed() {
        DialogBuilder.closeCurrent(new DialogResult(null, null));
    }

    public void onSearch() {
        onRefresh();
    }

    public void onCancelSearch() {
        getModel().setSearchString("");
        onRefresh();
    }

    public void onShowSearchDialog() {
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
        private Set<String> copiedFiles = new LinkedHashSet<>();
        private boolean cut;
        private String newName;
        private String searchString;
        private boolean newFolder;
        private boolean newFile;
        private boolean listMode;
        private boolean selectionMode;
        private List<AppFsSharing> currentSharings;

        public Set<String> getCopiedFiles() {
            return copiedFiles;
        }

        public void setCopiedFiles(Set<String> copiedFiles) {
            this.copiedFiles = copiedFiles;
        }

        public boolean isCut() {
            return cut;
        }

        public void setCut(boolean cut) {
            this.cut = cut;
        }

        public boolean isSelectionMode() {
            return selectionMode;
        }

        public void setSelectionMode(boolean selectionMode) {
            this.selectionMode = selectionMode;
        }

        public boolean isListMode() {
            return listMode;
        }

        public void setListMode(boolean listMode) {
            this.listMode = listMode;
        }

        public List<AppFsSharing> getCurrentSharings() {
            return currentSharings;
        }

        public void setCurrentSharings(List<AppFsSharing> currentSharings) {
            this.currentSharings = currentSharings;
        }

        public String getSearchString() {
            return searchString;
        }

        public void setSearchString(String searchString) {
            this.searchString = searchString;
        }

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
