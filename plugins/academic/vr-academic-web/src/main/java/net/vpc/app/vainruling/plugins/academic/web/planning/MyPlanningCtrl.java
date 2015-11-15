/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.planning;

import java.util.List;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningDay;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Mon Emploi du Temps",
        url = "modules/academic/myplanning",
        menu = "/Education"
//,securityKey = "Custom.Education.MyCourseLoad"
)
@ManagedBean
public class MyPlanningCtrl extends AbstractPlanningCtrl {

    public MyPlanningCtrl() {
        model = new Model();
    }

    @OnPageLoad
    public void onPageLoad() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = a.getCurrentTeacher();
        List<PlanningDay> plannings = a.loadTeacherPlanning(t == null ? -1 : t.getId());
        updateModel(plannings);
    }

    public void onRefresh() {
        onPageLoad();
    }


}
