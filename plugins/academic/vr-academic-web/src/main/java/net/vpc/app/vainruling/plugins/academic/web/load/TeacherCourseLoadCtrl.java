/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;

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
        title = "Charge par Enseignant",
        url = "modules/academic/teacher-course-load",
        menu = "/Education/Load",
        securityKey = "Custom.Education.TeacherCourseLoad"
)
public class TeacherCourseLoadCtrl extends AbstractCourseLoadCtrl {

    public TeacherCourseLoadCtrl() {
        super();
        model = new ModelExt();
    }

    public void onTeacherChanged() {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        getModel().setTeachers(new ArrayList<SelectItem>());
        int periodId = getPeriodId();
        for (AcademicTeacher t : p.findTeachers()) {
            AcademicTeacherPeriod tp = p.findAcademicTeacherPeriod(periodId, t);
            if (tp.isEnabled()) {
                getModel().getTeachers().add(new SelectItem(String.valueOf(t.getId()), t.getContact().getFullName()));
            }
        }
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
