/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.web;

import net.thevpc.app.vainruling.VrActionEnabler;
import net.thevpc.app.vainruling.VrActionInfo;
import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        //        title = "Inscription Etudiant",
        url = "modules/academic/syllabus",
        menu = "/Education/StudyPlan",
        securityKey = "Custom.Education.Syllabus"
)
public class SyllabusCtrl implements VrActionEnabler {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @Override
    public void checkEnabled(VrActionInfo data) {
    }

    @VrOnPageLoad
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
