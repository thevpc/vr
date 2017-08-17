/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.admin;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.vfs.VFS;
import net.vpc.upa.UPA;
import org.primefaces.event.FileUploadEvent;

import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Admin Tools",
        url = "modules/academic/admin-tools",
        menu = "/Education/Config",
        securityKey = "Custom.Education.AdminTools"
)
public class AcademicAdminToolsCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public void generateTeachingLoad(int periodId, String version, String oldVersion) {
        try {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            p.generateTeachingLoad(periodId, CourseAssignmentFilter.NO_INTENTS, version,oldVersion,null);
            FacesUtils.addInfoMessage("Successful Operation");
        } catch (Exception ex) {
            Logger.getLogger(AcademicAdminToolsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex.getMessage());
        }

    }

    public void handleTeachingLoadFileUpload(FileUploadEvent event) {
        try {
            String p = VrApp.getBean(CorePlugin.class).getNativeFileSystemPath()
                    + CorePlugin.PATH_TEMP + "/Import/" + VrUtils.date(new Date(), "yyyy-MM-dd-HH-mm")
                    + "-" + UserSession.getCurrentLogin();
            new File(p).mkdirs();
            File f = new File(p, event.getFile().getFileName());
            try {
                event.getFile().write(f.getPath());
                AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
                CorePlugin core = VrApp.getBean(CorePlugin.class);
                AppConfig appConfig = core.getCurrentConfig();
                int count=0;
                if(appConfig!=null && appConfig.getMainPeriod()!=null) {
                    int periodId = appConfig.getMainPeriod().getId();
                    count = a.importFile(periodId, VFS.createNativeFS().get(f.getPath()), null);
                }
                if (count > 0) {
                    FacesUtils.addInfoMessage(event.getFile().getFileName() + " successfully imported.");
                } else {
                    FacesUtils.addWarnMessage(event.getFile().getFileName() + " is uploaded but nothing is imported.");
                }
            } finally {
                //should not delete the file!
            }
        } catch (Exception ex) {
            Logger.getLogger(AcademicAdminToolsCtrl.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage(ex,event.getFile().getFileName() + " uploading failed.");
        }
    }

    public void importTeachingLoad() {
        try {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            p.importTeachingLoad(-1);
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

    public class Model {

    }
}
