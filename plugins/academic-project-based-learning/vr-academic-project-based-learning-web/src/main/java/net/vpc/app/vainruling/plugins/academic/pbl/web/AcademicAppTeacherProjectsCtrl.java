/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.pbl.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
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
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = ""),
                @UPathItem(title = "APP", css = "fa-dashboard", ctrl = ""),
        },
        css = "fa-table",
        title = "Mes Projets APP",
        url = "modules/academic/pbl/teacher-projects",
        menu = "/Education/Internship",
        securityKey = "Custom.Education.Apbl.TeacherProjects"
)
public class AcademicAppTeacherProjectsCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }



    public class Model {

    }
}
