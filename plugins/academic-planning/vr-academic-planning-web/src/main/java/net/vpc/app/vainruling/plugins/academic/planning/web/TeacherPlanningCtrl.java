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
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarWeek;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarDay;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.calendars.web.AbstractPlanningCtrl;
import net.vpc.common.strings.StringUtils;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Emploi par Enseignant",
        url = "modules/academic/planning/teacher-planning",
        menu = "/Calendars",
        securityKey = "Custom.Education.TeacherPlanning"
)
public class TeacherPlanningCtrl extends AbstractPlanningCtrl {

    public TeacherPlanningCtrl() {
        super();
        model = new ModelExt();
    }

    public void onTeacherChanged() {
        onRefresh();
    }

    public int getPeriodId() {
        String p = "";//getModel().getSelectedPeriod();
        if (StringUtils.isEmpty(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            AppConfig appConfig = core.findAppConfig();
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
        for (AcademicTeacher t : p.findTeachers()) {
            AcademicTeacherPeriod tp = p.findAcademicTeacherPeriod(getPeriodId(), t);
            if (tp.isEnabled()) {
                getModel().getTeachers().add(new SelectItem(String.valueOf(t.getId()), t.getContact().getFullName()));
            }
        }
        int t = StringUtils.isEmpty(getModel().getTeacherId()) ? -1 : Integer.parseInt(getModel().getTeacherId());
        CalendarWeek plannings = pl.loadTeacherPlanning(t);
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
