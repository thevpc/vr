/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UCtrlData;
import net.vpc.app.vainruling.core.web.UCtrlProvider;
import net.vpc.app.vainruling.core.web.menu.VRMenuDef;
import net.vpc.app.vainruling.core.web.menu.VRMenuDefFactory;
import net.vpc.app.vainruling.core.service.fs.FileSystemService;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.streams.PathInfo;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.UPA;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileType;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.common.vfs.VirtualFileACL;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author vpc
 */
@UCtrl(
        title = "Documents", css = "fa-dashboard", url = "modules/files/documents"
//        ,menu = "/FileSystem", securityKey = "Custom.FileSystem.Documents"
)
@ManagedBean
@Scope(value = "session")
public class DocumentsCtrl implements VRMenuDefFactory, UCtrlProvider {

    private static final Logger log = Logger.getLogger(DocumentsCtrl.class.getName());

    public static final Map<String, String> extensionsToCss = new HashMap<String, String>();

    static {
        extensionsToCss.put("csv", "file-csv");

        extensionsToCss.put("txt", "file-txt");
        extensionsToCss.put("properties", "file-txt");

        extensionsToCss.put("log", "file-log");

        extensionsToCss.put("xls", "file-xls");
        extensionsToCss.put("xlsx", "file-xls");
        extensionsToCss.put("ods", "file-xls");

        extensionsToCss.put("doc", "file-doc");
        extensionsToCss.put("docx", "file-doc");
        extensionsToCss.put("odt", "file-doc");

        extensionsToCss.put("zip", "file-zip");
        extensionsToCss.put("tar", "file-zip");
        extensionsToCss.put("rar", "file-zip");

        extensionsToCss.put("pdf", "file-pdf");
        extensionsToCss.put("xml", "file-xml");
        extensionsToCss.put("css", "file-css");
        extensionsToCss.put("html", "file-html");

        extensionsToCss.put("png", "file-img");
        extensionsToCss.put("gif", "file-img");
        extensionsToCss.put("jpg", "file-img");
        extensionsToCss.put("jpeg", "file-img");
    }

    private Model model = new Model();

    public static class Config {

        private String type;
        private String value;

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

    }

    @Override
    public List<VRMenuDef> createVRMenuDefList() {
        List<VRMenuDef> m = new ArrayList<>();
        m.add(new VRMenuDef("Tous les Documents", "/FileSystem", "documents", "{type:'root'}", "Custom.FileSystem.RootFileSystem", ""));
        m.add(new VRMenuDef("Mes Documents", "/FileSystem", "documents", "{type:'me'}", "Custom.FileSystem.MyFileSystem", ""));
        return m;
    }

    @Override
    public UCtrlData getUCtrl(String cmd) {
        try {
            Config c = VrHelper.parseJSONObject(cmd, Config.class);

            if (c == null) {
                c = new Config();
            }
            UCtrlData d = new UCtrlData();
            d.setUrl("modules/files/documents");
            d.setCss("fa-table");

            String login = VrApp.getBean(UserSession.class).getUser().getLogin();
            if ("root".equals(c.getType())) {
                d.setTitle("Tous les Documents");
            } else if ("user".equals(c.getType())) {
                String v = c.getValue();
                if (StringUtils.isEmpty(v)) {
                    v = login;
                }
                d.setTitle("Documents de " + v);
            } else if ("profile".equals(c.getType())) {
                String v = c.getValue();
                if (StringUtils.isEmpty(v)) {
                    v = "user";
                }
                d.setTitle("Documents de " + v);
            } else {
                d.setTitle("Mes Documents");
            }
            List<BreadcrumbItem> items = new ArrayList<>();
            items.add(new BreadcrumbItem("Système de Fichiers", "fa-dashboard", "", ""));
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
        FileSystemService fsp = VrApp.getBean(FileSystemService.class);
        Config c = getModel().getConfig();
        if (c == null) {
            c = new Config();
        }
        VirtualFileSystem rootfs = fsp.getFileSystem();
        VirtualFileSystem fs = null;
        String login = VrApp.getBean(UserSession.class).getUser().getLogin();
        if ("root".equals(c.getType())) {
            fs = rootfs;
        } else if ("user".equals(c.getType())) {
            String v = c.getValue();
            if (StringUtils.isEmpty(v)) {
                v = login;
            }
            fs = fsp.getUserFileSystem(v);
        } else if ("profile".equals(c.getType())) {
            String v = c.getValue();
            if (StringUtils.isEmpty(v)) {
                v = "user";
            }
            fs = fsp.getProfileFileSystem(v);
        } else {
            fs = fsp.getUserFileSystem(login);
        }
        getModel().setFileSystem(fs);
        getModel().setCurrent(createFileInfo("/", getModel().getFileSystem().get("/")));
        onRefresh();
    }

    protected VirtualFileSystem createFS() {
        FileSystemService fsp = VrApp.getBean(FileSystemService.class);
        VirtualFileSystem rootfs = fsp.getFileSystem();
        VirtualFileSystem userfs = rootfs.filter(null);
        return userfs;
    }

    public void updateCurrent(VFile file) {
        getModel().setCurrent(createFileInfo(file.getName(), file));
        onRefresh();
    }

    public StreamedContent downloadPath(String path) {
        InputStream stream = null;
        try {
            FileSystemService fsp = VrApp.getBean(FileSystemService.class);
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
        InputStream stream = null;
        try {
            if (i.getFile().isDirectory()) {
                //should zip it?   
            } else {
                final VFile f = i.getFile();
                FileSystemService fsp = VrApp.getBean(FileSystemService.class);
                fsp.markDownloaded(f);
                stream = f.getInputStream();
                onRefresh();
                return new DefaultStreamedContent(stream, f.probeContentType(), f.getName());
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            getModel().setNewName("NouveauDossier");
        }
    }

    public void onRemove() {
        try {
            for (VFileInfo file : getModel().getFiles()) {
                if (file.isSelected()) {
                    file.file.deleteAll();
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

    public void onSave() {
        if (!StringUtils.isEmpty(getModel().getArea())) {
            if ("NewFile".equals(getModel().getArea())) {
                String n = getModel().getNewName().trim();
                VFile f2 = getModel().getCurrent().file.get(n);
                try {
                    f2.writeBytes(new byte[0]);
                } catch (IOException ex) {
                    Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ("NewFolder".equals(getModel().getArea())) {
                String n = getModel().getNewName().trim();
                VFile f2 = getModel().getCurrent().file.get(n);
                try {
                    f2.mkdirs();
                } catch (Exception ex) {
                    Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        resetArea();
        onRefresh();
    }

    public void onRefresh() {
        final VFile curr = getModel().getCurrent().getFile();
        VFile[] all = getModel().getFileSystem().listFiles(curr.getPath());
        ArrayList<VFileInfo> ret = new ArrayList<>();
        for (VFile a : all) {
            ret.add(createFileInfo(a.getName(), a));
        }
        Collections.sort(ret);
        if (!"/".equals(curr.getPath())) {
            ret.add(0, createFileInfo("<Dossier Parent>", curr.getParentFile()));
        }
        getModel().setFiles(ret);
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

    private VFileInfo createFileInfo(String name, VFile file) {
        String css = "file";
        long downloads = 0;
        if (file.isDirectory()) {
            css = "folder";
        } else {
            String n = file.getName().toLowerCase();
            String e = PathInfo.create(n).getExtensionPart();
            css = extensionsToCss.get(e);
            if (css == null) {
                css = "file";
            }
            FileSystemService fsp = VrApp.getBean(FileSystemService.class);
            downloads = fsp.getDownloadsCount(file);
        }
        String desc = "<Dossier Parent>".equals(name) ? "" : evalVFileDesc(file);
        return new VFileInfo(name, file, css, downloads, desc);
    }

    public static String evalVFileDesc(VFile file) {
        if (file.isFile()) {
            return VrHelper.formatFileSize(file.length());
        }
        if (file.isDirectory()) {
            VFile[] files = file.listFiles();
            int f = 0;
            int d = 0;
            if (files != null) {
                for (VFile ff : files) {
                    if (ff.isDirectory()) {
                        d++;
                    } else {
                        f++;
                    }
                }
            }
            if (f == 0 && d == 0) {
                return "vide";
            }
            StringBuilder sb = new StringBuilder();
            if (d > 0) {
                sb.append(d).append(" rep.");
            }
            if (f > 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(f).append(" fich.");
            }
            return sb.toString();
        }
        return "";
    }

    public Model getModel() {
        return model;
    }

    public boolean isEnabledButton(String buttonId) {
        if ("Refresh".equals(buttonId)) {
            return getModel().getArea().isEmpty();
        }
        if ("NewFile".equals(buttonId)) {
            return getModel().getArea().isEmpty()
                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(FileSystemService.RIGHT_FILESYSTEM_WRITE)
                    && getModel().getCurrent().getFile().getACL().isAllowedCreateChild(VFileType.FILE, null);
        }
        if ("NewFolder".equals(buttonId)) {
            return getModel().getArea().isEmpty()
                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(FileSystemService.RIGHT_FILESYSTEM_WRITE)
                    && getModel().getCurrent().getFile().getACL().isAllowedCreateChild(VFileType.DIRECTORY, null);
        }
        if ("Upload".equals(buttonId)) {
            VirtualFileACL acl = getModel().getCurrent().getFile().getACL();
            return getModel().getArea().isEmpty()
                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(FileSystemService.RIGHT_FILESYSTEM_WRITE)
                    && (acl.isAllowedCreateChild(VFileType.FILE, null)
                    || acl.isAllowedUpdateChild(VFileType.FILE, null));
        }
        if ("Remove".equals(buttonId)) {
            return getModel().getArea().isEmpty()
                    && UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(FileSystemService.RIGHT_FILESYSTEM_WRITE)
                    && getModel().getCurrent().getFile().getACL().isAllowedRemoveChild(null, null);
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
        return VrApp.getBean(UserSession.class).isAdmin();
    }

    public boolean isEnabledButton(String buttonId, VFileInfo forFile) {
        if ("Refresh".equals(buttonId)) {
            return true;
        }
        return true;
//        return VrApp.getBean(UserSession.class).isAdmin();
    }

    public void handleNewFile(FileUploadEvent event) {
        try {
            VFile newFile = getModel().getCurrent().file.get(event.getFile().getFileName());
            if (newFile.exists()) {
                boolean doOverride = false;
                //check if alreay selected
                for (VFileInfo ex : getModel().getFiles()) {
                    if (ex.getFile().getName().equals(newFile.getName()) && ex.isSelected()) {
                        doOverride = true;
                        break;
                    }
                }
                if (!doOverride) {
                    FacesUtils.addErrorMessage(event.getFile().getFileName() + " already exists please select to force override.");
                    return;
                }
            }
            String tempPath = "/Temp/Files/" + VrHelper.date(new Date(), "yyyy-MM-dd-HH-mm")
                    + "-" + VrApp.getBean(UserSession.class).getUser().getLogin();
            FileSystemService fsp = VrApp.getBean(FileSystemService.class);
            String p = fsp.getNativeFileSystemPath() + tempPath;
            new File(p).mkdirs();
            File f = new File(p, event.getFile().getFileName());
            try {
                event.getFile().write(f.getPath());
                //do work here
                int count = 1;//
                if (count > 0) {
                    FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully uploaded.");
                } else {
                    FacesUtils.addWarnMessage(null, event.getFile().getFileName() + " is uploaded but nothing is updated.");
                }
            } finally {
                //should not delete the file!
                VirtualFileSystem nfs = VFS.createNativeFS();
                nfs.get(f.getPath()).copyTo(newFile);
            }
        } catch (Exception ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(event.getFile().getFileName() + " uploading failed.", ex.getMessage());
        }
        onRefresh();
    }

    public void handleUpdatedFile(FileUploadEvent event) {
        try {
            String p = VrApp.getBean(FileSystemService.class).getNativeFileSystemPath()
                    + "/Temp/Files/" + VrHelper.date(new Date(), "yyyy-MM-dd-HH-mm")
                    + "-" + VrApp.getBean(UserSession.class).getUser().getLogin();
            new File(p).mkdirs();
            File f = new File(p, event.getFile().getFileName());
            try {
                event.getFile().write(f.getPath());
                //do work here
                int count = 1;
                if (count > 0) {
                    FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully uploaded.");
                } else {
                    FacesUtils.addWarnMessage(null, event.getFile().getFileName() + " is uploaded but nothing is updated.");
                }
            } finally {
                //should not delete the file!
            }
        } catch (Exception ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(event.getFile().getFileName() + " uploading failed.", ex.getMessage());
        }

    }
}
