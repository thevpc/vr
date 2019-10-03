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
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.CorePluginSSE;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.extensions.VrImportFileOptions;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;

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

    public Model getModel() {
        return model;
    }

    public void handleFileUpload(FileUploadEvent event) {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                try {
                    MirroredPath temp = CorePlugin.get().createTempUploadFolder();
                    File f = new File(temp.getNativePath(), event.getFile().getFileName());
                    try {
                        event.getFile().write(f.getPath());
                        long count = CorePluginSSE.get().importFile(VFS.createNativeFS().get(f.getPath()), new VrImportFileOptions().setMaxDepth(3));
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

    public class Model {

    }
}
