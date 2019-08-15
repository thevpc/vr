/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web;

import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.core.service.pages.VrActionEnabler;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
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
    public boolean isEnabled(net.vpc.app.vainruling.core.service.pages.VrActionInfo data) {
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
