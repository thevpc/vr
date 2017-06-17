/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.planning.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarWeek;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarDay;
import net.vpc.app.vainruling.plugins.calendars.web.AbstractPlanningCtrl;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.NamedId;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Emploi par Groupe",
        url = "modules/academic/planning/class-planning",
        menu = "/Calendars",
        securityKey = "Custom.Education.ClassPlanning"
)
public class ClassPlanningCtrl extends AbstractPlanningCtrl {

    public ClassPlanningCtrl() {
        super();
        model = new ModelExt();
    }

    public void onGroupChanged() {
        onRefresh();
    }

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        getModel().setGroups(new ArrayList<SelectItem>());
        AcademicPlanningPlugin pl = VrApp.getBean(AcademicPlanningPlugin.class);
        for (NamedId t : pl.loadStudentPlanningListNames()) {
            getModel().getGroups().add(new SelectItem(String.valueOf(t.getId()), StringUtils.nonNull(t.getName())));
        }
        CalendarWeek plannings = pl.loadClassPlanning(getModel().getGroupName());
        if (plannings == null) {
            updateModel(new ArrayList<CalendarDay>());
        } else {
            updateModel(plannings.getDays());
        }
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();
    }

    public ModelExt getModel() {
        return (ModelExt) super.getModel();
    }

    public class ModelExt extends Model {

        String groupName;
        List<SelectItem> groups = new ArrayList<SelectItem>();

        public List<SelectItem> getGroups() {
            return groups;
        }

        public void setGroups(List<SelectItem> groups) {
            this.groups = groups;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

    }
}
