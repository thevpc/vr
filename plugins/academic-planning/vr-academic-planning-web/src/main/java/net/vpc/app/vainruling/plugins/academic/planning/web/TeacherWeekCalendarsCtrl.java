/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.planning.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPlugin;
import net.vpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.vpc.app.vainruling.plugins.calendars.service.dto.CalendarDay;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.calendars.web.week.AbstractWeekCalendarCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.NamedId;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrPathItem;
import net.vpc.app.vainruling.VrOnPageLoad;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Emploi par Enseignant",
        url = "modules/academic/planning/teacher-week-calendars",
        menu = "/Calendars",
        securityKey = AcademicPlanningPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_PLANNING
)
@Controller
public class TeacherWeekCalendarsCtrl extends AbstractWeekCalendarCtrl {

    public TeacherWeekCalendarsCtrl() {
        super();
        model = new ModelExt();
    }

    public void onTeacherChanged() {
        onRefresh();
    }

    public int getPeriodId() {
        String p = "";//getModel().getSelectedPeriod();
        if (StringUtils.isBlank(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            AppConfig appConfig = core.getCurrentConfig();
            if(appConfig!=null) {
                AppPeriod mainPeriod = appConfig.getMainPeriod();
                if(mainPeriod!=null) {
                    return mainPeriod.getId();
                }
            }
            return -1;
        }
        return Integer.parseInt(p);
    }

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        AcademicPlanningPlugin pl = VrApp.getBean(AcademicPlanningPlugin.class);
        getModel().setTeachers(new ArrayList<SelectItem>());
        for (NamedId t : p.findEnabledTeacherNames(getPeriodId())) {
            getModel().getTeachers().add(FacesUtils.createSelectItem(t.getStringId(), t.getStringName()));
        }
        int t = StringUtils.isBlank(getModel().getTeacherId()) ? -1 : Integer.parseInt(getModel().getTeacherId());
        WeekCalendar plannings = pl.loadTeacherPlanning(t);
        if (plannings == null) {
            updateModel(new ArrayList<CalendarDay>());
        } else {
            updateModel(plannings.getDays());
        }
    }

    @VrOnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();
    }

    public AcademicTeacher getCurrentTeacher() {
        String ii = getModel().getTeacherId();
        if (ii != null && ii.length() > 0) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            AcademicTeacher tt = p.findTeacher(Integer.parseInt(ii));
            if (tt != null) {
                return tt;
            }
        }
        return null;
//        AcademicPlugin a = VRApp.getBean(AcademicPlugin.class);
//        return a.getCurrentTeacher();
    }

    public ModelExt getModel() {
        return (ModelExt) super.getModel();
    }

    public class ModelExt extends Model {

        String teacherId;
        List<SelectItem> teachers = new ArrayList<SelectItem>();

        public List<SelectItem> getTeachers() {
            return teachers;
        }

        public void setTeachers(List<SelectItem> teachers) {
            this.teachers = teachers;
        }

        public String getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(String teacherId) {
            this.teacherId = teacherId;
        }

    }
}
