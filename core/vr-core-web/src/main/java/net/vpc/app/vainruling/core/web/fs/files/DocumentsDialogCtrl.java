/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.core.service.fs.FileSystemService;
import net.vpc.common.streams.PathInfo;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;
import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
@ManagedBean
@Scope(value = "session")
public class DocumentsDialogCtrl {

    private static final Logger log = Logger.getLogger(DocumentsDialogCtrl.class.getName());

    private Model model = new Model();

    public static class Config {

        private String type;
        private String value;
        private String title;
        private String path;
        private String userInfo;

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

    }

    public void openDialog(String config) {
        openDialog(VrHelper.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        initContent(config);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);

        RequestContext.getCurrentInstance().openDialog("/modules/files/documentsdialog", options, null);

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
        getModel().setConfig(cmd);
        FileSystemService fsp = VrApp.getBean(FileSystemService.class);
        Config c = getModel().getConfig();
        if (c == null) {
            c = new Config();
        }
        String title = c.getTitle();
        if (StringUtils.isEmpty(title)) {
            title = "Documents";
        }
        getModel().setTitle(title);

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
        String initialPath = c.getPath();
        if (!(StringUtils.isEmpty(initialPath))) {
            VFile pp = null;//
            try {
                pp = getModel().getFileSystem().get(initialPath);
            } catch (Exception ignoreMe) {
                //
            }
            if (pp != null) {
                if (pp.isDirectory()) {
                    getModel().setCurrent(createFileInfo(pp));
                } else if (pp.isFile()) {
                    VFile par = pp.getParentFile();
                    if (par != null) {
                        getModel().setCurrent(createFileInfo(par));
                    }
                }
            }
        }
        onRefresh();
    }

    protected VirtualFileSystem createFS() {
        FileSystemService fsp = VrApp.getBean(FileSystemService.class);
        VirtualFileSystem rootfs = fsp.getFileSystem();
        VirtualFileSystem userfs = rootfs.filter(null);
        return userfs;
    }

    public void selectDirectory(VFile file) {
        getModel().setCurrent(createFileInfo(file.getName(), file));
        onRefresh();
    }

    public void selectFile(VFile file) {
        getModel().setCurrent(createFileInfo(file.getName(), file));
        RequestContext.getCurrentInstance().closeDialog(
                new DialogResult(getModel().getCurrent().getFile().getBaseFile("vrfs").getPath(), getModel().getConfig().getUserInfo())
        );
    }

    public void onRemove() {
        try {
            for (VFileInfo file : getModel().getFiles()) {
                if (file.isSelected()) {
                    file.file.deleteAll();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentsDialogCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
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

        String title;
        VFileInfo current;
        VirtualFileSystem fileSystem;
        Config config;
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

    private VFileInfo createFileInfo(VFile file) {
        return createFileInfo(file.getName(), file);
    }

    private VFileInfo createFileInfo(String name, VFile file) {
        String css = "file";
        long downloads = 0;
        if (file.isDirectory()) {
            css = "folder";
        } else {
            String n = file.getName().toLowerCase();
            String e = PathInfo.create(n).getExtensionPart();
            css = DocumentsCtrl.extensionsToCss.get(e);
            if (css == null) {
                css = "file";
            }
            FileSystemService fsp = VrApp.getBean(FileSystemService.class);
            downloads = fsp.getDownloadsCount(file);
        }
        String desc = "<Dossier Parent>".equals(name) ? "" : DocumentsCtrl.evalVFileDesc(file);
        return new VFileInfo(name, file, css, downloads, desc);
    }

    public Model getModel() {
        return model;
    }

}
