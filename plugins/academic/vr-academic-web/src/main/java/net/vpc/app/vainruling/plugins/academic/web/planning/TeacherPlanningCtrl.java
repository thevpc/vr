/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.planning;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningData;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningDay;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.strings.StringUtils;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Emploi par Enseignant",
        url = "modules/academic/teacherplanning",
        menu = "/Education",
        securityKey = "Custom.Education.TeacherPlanning"
)
@ManagedBean
public class TeacherPlanningCtrl extends AbstractPlanningCtrl {

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

    public TeacherPlanningCtrl() {
        super();
        model = new ModelExt();
    }

    public void onTeacherChanged() {
        onRefresh();
    }

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        getModel().setTeachers(new ArrayList<SelectItem>());
        for (AcademicTeacher t : p.findTeachers()) {
            if (t.isEnabled()) {
                getModel().getTeachers().add(new SelectItem(String.valueOf(t.getId()), t.getContact().getFullName()));
            }
        }
        int t = StringUtils.isEmpty(getModel().getTeacherId()) ? -1 : Integer.parseInt(getModel().getTeacherId());
        PlanningData plannings = p.loadTeacherPlanning(t);
        if (plannings == null) {
            updateModel(new ArrayList<PlanningDay>());
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
}
