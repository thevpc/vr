/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs.files;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;
import org.primefaces.context.RequestContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl
public class DocumentsDialogCtrl {

    private static final Logger log = Logger.getLogger(DocumentsDialogCtrl.class.getName());

    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrUtils.parseJSONObject(config, Config.class));
    }

    public void openDialog(Config config) {
        initContent(config);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);

        RequestContext.getCurrentInstance().openDialog("/modules/files/documents-dialog", options, null);

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
        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
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
        String login = UserSession.getCurrentUser().getLogin();
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
        getModel().setCurrent(DocumentsUtils.createFileInfo("/", getModel().getFileSystem().get("/")));
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

    protected VirtualFileSystem createFS() {
        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
        VirtualFileSystem rootfs = fsp.getFileSystem();
        VirtualFileSystem userfs = rootfs.filter(null);
        return userfs;
    }

    public void selectDirectory(VFile file) {
        getModel().setCurrent(DocumentsUtils.createFileInfo(file.getName(), file));
        onRefresh();
    }

    public void selectFile(VFile file) {
        getModel().setCurrent(DocumentsUtils.createFileInfo(file.getName(), file));
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
        getModel().setFiles(DocumentsUtils.loadFiles(getModel().getCurrent().getFile()));
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

}
