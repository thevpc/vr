/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.service.util.TeacherPeriodFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.common.jsf.FacesUtils;

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
//        title = "Charge par Enseignant",
        url = "modules/academic/teacher-course-load",
        menu = "/Education/Load",
        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_COURSE_LOAD
)
public class TeacherCourseLoadCtrl extends AbstractCourseLoadCtrl {

    public TeacherCourseLoadCtrl() {
        super();
        model = new ModelExt();
    }

    @Override
    public void onChangeOther() {
        super.onChangeOther();
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        getModel().setTeachers(new ArrayList<SelectItem>());
        int periodId = getTeacherFilter().getPeriodId();
        TeacherPeriodFilter teacherFilter = getTeacherFilter().getTeacherFilter();
        for (AcademicTeacher t : p.findTeachers()) {
            AcademicTeacherPeriod tp = p.findAcademicTeacherPeriod(periodId, t);
            if (tp.isEnabled() && teacherFilter.acceptTeacher(tp)) {
                getModel().getTeachers().add(FacesUtils.createSelectItem(String.valueOf(t.getId()), t.resolveFullName()));
            }
        }
        onRefresh();
    }

    public void onChangeAcademicTeacher() {
        onRefresh();
    }

    public void onInit() {
        super.onInit();
//        getCourseFilter().getModel().setRefreshFilterSelected(new String[]{"deviation-extra", "deviation-week"});
        getOthersCourseFilter().getModel().setRefreshFilterSelected(new String[]{
            //"deviation-extra", --removed default deviation-extra
            "deviation-week"});
        getModel().setOthersFilters(new String[]{"rooms","multiple-selection"});
    }
    public void onRefresh() {
        super.onRefresh();
        //
        getModel().getCurrentTeacherFiltersSelectItems().add(FacesUtils.createSelectItem("no-current-intents", "Mes Modules Affectés", "vr-checkbox"));
    }

    @OnPageLoad
    @Override
    public void onRefresh(String cmd) {
        super.onRefresh(cmd);

    }

    @Override
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
