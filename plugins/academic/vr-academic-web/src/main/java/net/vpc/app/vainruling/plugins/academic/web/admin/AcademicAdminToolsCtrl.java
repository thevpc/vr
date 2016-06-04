/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.admin;

import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.core.service.fs.FileSystemService;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.vfs.VFS;
import net.vpc.upa.UPA;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Admin Tools",
        url = "modules/academic/admin-tools",
        menu = "/Education/Config",
        securityKey = "Custom.Education.AdminTools"
)
@ManagedBean
public class AcademicAdminToolsCtrl {

    private Model model = new Model();

    public class Model {

    }

    public Model getModel() {
        return model;
    }

    public void generateTeachingLoad(String version) {
        try {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            p.generateTeachingLoad(version);
            FacesUtils.addInfoMessage("Successful Operation");
        } catch (Exception ex) {
            Logger.getLogger(AcademicAdminToolsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex.getMessage());
        }

    }

    public void handleTeachingLoadFileUpload(FileUploadEvent event) {
        try {
            String p = VrApp.getBean(FileSystemService.class).getNativeFileSystemPath()
                    + "/Temp/Import/" + VrHelper.date(new Date(), "yyyy-MM-dd-HH-mm")
                    + "-" + VrApp.getBean(UserSession.class).getUser().getLogin();
            new File(p).mkdirs();
            File f = new File(p, event.getFile().getFileName());
            try {
                event.getFile().write(f.getPath());
                AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
                int count = a.importFile(VFS.createNativeFS().get(f.getPath()), null);
                if (count > 0) {
                    FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully imported.");
                } else {
                    FacesUtils.addWarnMessage(null, event.getFile().getFileName() + " is uploaded but nothing is imported.");
                }
            } finally {
                //should not delete the file!
            }
        } catch (Exception ex) {
            Logger.getLogger(AcademicAdminToolsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(event.getFile().getFileName() + " uploading failed.", ex.getMessage());
        }
    }

    public void importTeachingLoad() {
        try {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            p.importTeachingLoad();
            FacesUtils.addInfoMessage("Successful Operation");
        } catch (Exception ex) {
            Logger.getLogger(AcademicAdminToolsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex.getMessage());
        }
    }

    public void updateData() {
        try {
//            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
//            p.validateAcademicData();
            UPA.getPersistenceUnit().updateFormulas();
            FacesUtils.addInfoMessage("Successful Operation");
        } catch (Exception ex) {
            Logger.getLogger(AcademicAdminToolsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex.getMessage());
        }
    }
}
