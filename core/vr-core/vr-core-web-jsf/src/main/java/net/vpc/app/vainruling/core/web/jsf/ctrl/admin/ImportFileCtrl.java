/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.admin;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.fs.MirroredPath;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.vfs.VFS;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.primefaces.event.FileUploadEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.VrImportFileAction;
import net.vpc.app.vainruling.core.service.CorePluginSSE;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.VrImportFileOptions;
import net.vpc.app.vainruling.VrOnPageLoad;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrPathItem;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.web.jsf.VrJsf;
import net.vpc.common.strings.StringUtils;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        //        title = "Admin Tools",
        url = "modules/admin/import-file",
        menu = "/Admin",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_ADMIN_IMPORT_FILE
)
public class ImportFileCtrl {

    private Model model = new Model();

    @VrOnPageLoad
    public void init() {
        String icon = "/images/icons/file-xls16.png";
        getModel().getTemplates().clear();
        for (VrImportFileAction a : VrApp.getBeansForType(VrImportFileAction.class)) {
            String n = a.getName();
            getModel().getTemplates().add(new FileTemplate(
                    I18n.get().get("VrImportFileAction." + n),
                    I18n.get().get("VrImportFileAction." + n + ".description"), icon,
                    a.getExampleFilePath()
            ));
            getModel().getTemplates().sort((FileTemplate o1, FileTemplate o2) -> StringUtils.trim(o1.getName()).compareTo(o2.getName()));
        }
    }

    public Model getModel() {
        return model;
    }

    public void handleFileUpload(FileUploadEvent event) {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    try {
                        long count = CorePluginSSE.get().importFile(VrJsf.createTempFile(event), new VrImportFileOptions().setMaxDepth(3));
                        if (count > 0) {
                            FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully imported.");
                        } else {
                            FacesUtils.addWarnMessage(event.getFile().getFileName() + " is uploaded but nothing is imported.");
                        }
                    } finally {
                        //should not delete the file!
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ImportFileCtrl.class.getName()).log(Level.SEVERE, null, ex);
                    FacesUtils.addErrorMessage(ex, event.getFile().getFileName() + " uploading failed : " + ex.toString());
                }
            }
        });
    }

    public static class Model {

        private List<FileTemplate> templates = new ArrayList<>();

        public List<FileTemplate> getTemplates() {
            return templates;
        }

        public void setTemplates(List<FileTemplate> templates) {
            this.templates = templates;
        }

    }

    public static class FileTemplate {

        private String name;
        private String description;
        private String icon;
        private String path;

        public FileTemplate(String name, String description, String icon, String path) {
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.path = path;
        }

        public FileTemplate() {
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }

        public String getPath() {
            return path;
        }

    }
}
