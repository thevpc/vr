/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.planning.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPlugin;
import net.vpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.vpc.app.vainruling.plugins.calendars.service.dto.CalendarDay;
import net.vpc.app.vainruling.plugins.calendars.web.week.AbstractWeekCalendarCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.NamedId;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Emploi par Groupe",
        url = "modules/academic/planning/class-week-calendars",
        menu = "/Calendars",
        securityKey = AcademicPlanningPluginSecurity.RIGHT_CUSTOM_EDUCATION_CLASS_PLANNING
)
@Controller
public class ClassWeekCalendarsCtrl extends AbstractWeekCalendarCtrl {

    public ClassWeekCalendarsCtrl() {
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
            getModel().getGroups().add(FacesUtils.createSelectItem(t.getStringId(), StringUtils.nonNull(t.getName())));
        }
        WeekCalendar plannings = pl.loadClassPlanning(getModel().getGroupName());
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
