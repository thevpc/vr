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
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.obj.DialogResult;
import net.vpc.app.vainruling.core.web.util.FileUploadEventHandler;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileType;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.upa.UPA;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

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
@VrController
public class DocumentsUploadDialogCtrl {

    private static final Logger log = Logger.getLogger(DocumentsUploadDialogCtrl.class.getName());

    private Model model = new Model();

    public void openDialog(String config) {
        openDialog(VrUtils.parseJSONObject(config, Config.class));
    }


    public void openDialog(Config config) {
        initContent(config);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);

        RequestContext.getCurrentInstance().openDialog("/modules/files/documents-upload-dialog", options, null);

    }

    public void fireEventExtraDialogClosed() {
        RequestContext.getCurrentInstance().closeDialog(
                new DialogResult(null, getModel().getConfig().getUserInfo())
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
    }

    public void handleNewFile(FileUploadEvent event) {
        try {
            CorePlugin fsp = VrApp.getBean(CorePlugin.class);
            VFile ufs = fsp.getUserFolder(fsp.getCurrentUserLogin());
            VFile folder = ufs.get("/Upload");
            folder.mkdirs();
            VFile file=CorePlugin.get().uploadFile(folder, new FileUploadEventHandler(event) );
            String baseFile = file.getBaseFile("vrfs").getPath();
            RequestContext.getCurrentInstance().closeDialog(
                    new DialogResult(baseFile, getModel().getConfig().getUserInfo())
            );
            FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully uploaded.");
        } catch (Exception ex) {
            Logger.getLogger(DocumentsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(event.getFile().getFileName() + " uploading failed.", ex.getMessage());
        }
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

        private String title;
        private String current;
        private Config config;

        public String getCurrent() {
            return current;
        }

        public void setCurrent(String current) {
            this.current = current;
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
