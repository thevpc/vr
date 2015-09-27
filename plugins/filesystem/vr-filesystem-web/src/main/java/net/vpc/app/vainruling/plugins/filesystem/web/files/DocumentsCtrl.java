/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.filesystem.web.files;

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
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.web.BreadcrumbItem;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UCtrlData;
import net.vpc.app.vainruling.api.web.UCtrlProvider;
import net.vpc.app.vainruling.api.web.VRMenuDef;
import net.vpc.app.vainruling.api.web.VRMenuDefFactory;
import net.vpc.app.vainruling.plugins.filesystem.service.FileSystemPlugin;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.streams.PathInfo;
import net.vpc.upa.impl.util.Strings;
import net.vpc.vfs.VFS;
import net.vpc.vfs.VFile;
import net.vpc.vfs.VirtualFileSystem;
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

    private static final Map<String, String> extensionsToCss = new HashMap<String, String>();

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
        VrApp.getBean(CorePlugin.class).createRight("Custom.FileSystem.RootFileSystem", "Root FileSystem Access");
        VrApp.getBean(CorePlugin.class).createRight("Custom.FileSystem.MyFileSystem", "My FileSystem Access");
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
                if (Strings.isNullOrEmpty(v)) {
                    v = login;
                }
                d.setTitle("Documents de " + v);
            } else if ("profile".equals(c.getType())) {
                String v = c.getValue();
                if (Strings.isNullOrEmpty(v)) {
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
        FileSystemPlugin fsp = VrApp.getBean(FileSystemPlugin.class);
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
            if (Strings.isNullOrEmpty(v)) {
                v = login;
            }
            fs = fsp.getUserFileSystem(v);
        } else if ("profile".equals(c.getType())) {
            String v = c.getValue();
            if (Strings.isNullOrEmpty(v)) {
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
        FileSystemPlugin fsp = VrApp.getBean(FileSystemPlugin.class);
        VirtualFileSystem rootfs = fsp.getFileSystem();
        VirtualFileSystem userfs = rootfs.filter(null);
        return userfs;
    }

    public void updateCurrent(VFile file) {
        getModel().setCurrent(createFileInfo(file.getName(), file));
        onRefresh();
    }

    public StreamedContent getContent(FileInfo i) {
        InputStream stream = null;
        try {
            if (i.getFile().isDirectory()) {
                //should zip it?   
            } else {
                final VFile f = i.getFile();
                stream = f.getInputStream();
                return new DefaultStreamedContent(stream, f.probeContentType(), f.getName());
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void onRefresh() {
        final VFile curr = getModel().getCurrent().getFile();
        VFile[] all = getModel().getFileSystem().listFiles(curr.getPath());
        ArrayList<FileInfo> ret = new ArrayList<>();
        for (VFile a : all) {
            ret.add(createFileInfo(a.getName(), a));
        }
        Collections.sort(ret);
        if (!"/".equals(curr.getPath())) {
            ret.add(0, createFileInfo("<Repertoire Parent>", curr.getParentFile()));
        }
        getModel().setFiles(ret);
    }

    public static class Model {

        FileInfo current;
        VirtualFileSystem fileSystem;
        Config config;
        private List<FileInfo> files = new ArrayList<>();

        public FileInfo getCurrent() {
            return current;
        }

        public void setCurrent(FileInfo current) {
            this.current = current;
        }

        public List<FileInfo> getFiles() {
            return files;
        }

        public void setFiles(List<FileInfo> files) {
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

    private FileInfo createFileInfo(String name, VFile file) {
        String css = "file";
        if (file.isDirectory()) {
            css = "folder";
        } else {
            String n = file.getName().toLowerCase();
            String e = PathInfo.create(n).getExtensionPart();
            css = extensionsToCss.get(e);
            if (css == null) {
                css = "file";
            }
        }
        return new FileInfo(name, file, css);
    }

    public class FileInfo implements Comparable<FileInfo> {

        private String name;
        private String css;
        private VFile file;
        private boolean selected;

        public FileInfo(String name, VFile file, String css) {
            this.name = name;
            this.file = file;
            this.css = css;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public VFile getFile() {
            return file;
        }

        public Date getLastModifiedDate() {
            return new Date(file.lastModified());
        }

        public void setFile(VFile file) {
            this.file = file;
        }

        public String getCss() {
            return css;
        }

        public void setCss(String css) {
            this.css = css;
        }

        @Override
        public int compareTo(FileInfo o) {
            if (file.isDirectory() != o.file.isDirectory()) {
                return file.isDirectory() ? -1 : 1;
            }
            return file.getName().compareToIgnoreCase(o.file.getName());
        }

    }

    public Model getModel() {
        return model;
    }

    public void handleNewFile(FileUploadEvent event) {
        try {
            String p = VrApp.getBean(FileSystemPlugin.class).getNativeFileSystemPath()
                    + "/Temp/Files/" + VrHelper.date(new Date(), "yyyy-MM-dd-HH-mm")
                    + "-" + VrApp.getBean(UserSession.class).getUser().getLogin();
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
            }
        } catch (Exception ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(event.getFile().getFileName() + " uploading failed.", ex.getMessage());
        }
    }

    public void handleUpdatedFile(FileUploadEvent event) {
        try {
            String p = VrApp.getBean(FileSystemPlugin.class).getNativeFileSystemPath()
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
