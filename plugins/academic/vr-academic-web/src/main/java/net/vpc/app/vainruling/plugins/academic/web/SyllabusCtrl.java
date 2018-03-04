/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppCivility;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppGender;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicBac;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicPreClass;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicStudentImport;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.core.web.VrActionEnabler;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Inscription Etudiant",
        url = "modules/academic/syllabus",
        menu = "/Education/Config",
        securityKey = "Custom.Education.Syllabus"
)
public class SyllabusCtrl implements VrActionEnabler{

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @Override
    public boolean isEnabled(net.vpc.app.vainruling.core.web.VrActionInfo data) {
        return true;
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();

    }

    public void onRefresh() {
    }

    public void onFiltersChanged() {
        //onRefresh();
    }


    public class Model {
        private AcademicCoursePlan academicCoursePlan;

        public AcademicCoursePlan getAcademicCoursePlan() {
            return academicCoursePlan;
        }

        public void setAcademicCoursePlan(AcademicCoursePlan academicCoursePlan) {
            this.academicCoursePlan = academicCoursePlan;
        }
    }
}
