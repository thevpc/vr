/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningDay;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningHour;
import net.vpc.upa.impl.util.Strings;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Emplois du Temps",
        url = "modules/academic/teacherplanning",
        menu = "/Education/Load", securityKey = "Custom.Education.TeacherPlanning"
)
@ManagedBean
public class TeacherPlanningCtrl {

    private static final String[] colorsCourse = new String[]{
        "bisque",
        "#DDE6CB",
        "aliceblue",
        "#FDD5E0",
        "beige",
        "#E6E7F9",
        "#FBFFBE",
        "#C0F7BA",
        "#EAD3F9"
    };
    private static final String[] colorsClass = new String[]{
        "darkkhaki",
        "darkorange",
        "darksalmon",
        "deeppink",
        "mediumseagreen",
        "darkcyan"
    };

    public static class Model {

        String teacherId;
        List<SelectItem> teachers = new ArrayList<SelectItem>();
        List<PlanningDay> planning = new ArrayList<>();
        List<String> courseNames = new ArrayList<>();
        List<String> classNames = new ArrayList<>();

        public List<PlanningDay> getPlanning() {
            return planning;
        }

        public void setPlanning(List<PlanningDay> planning) {
            this.planning = planning;
        }

        public List<String> getCourseNames() {
            return courseNames;
        }

        public void setCourseNames(List<String> courseNames) {
            this.courseNames = courseNames;
        }

        public List<String> getClassNames() {
            return classNames;
        }

        public void setClassNames(List<String> classNames) {
            this.classNames = classNames;
        }

        public String getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(String teacherId) {
            this.teacherId = teacherId;
        }

        public List<SelectItem> getTeachers() {
            return teachers;
        }

        public void setTeachers(List<SelectItem> teachers) {
            this.teachers = teachers;
        }

    }
    private Model model = new Model();

    public TeacherPlanningCtrl() {
    }

    public Model getModel() {
        return model;
    }

    public void onTeacherChanged() {
        onRefresh();
    }

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        getModel().setTeachers(new ArrayList<SelectItem>());
        for (AcademicTeacher t : p.findTeachersWithAssignements()) {
            if (t.isEnabled()) {
                getModel().getTeachers().add(new SelectItem(String.valueOf(t.getId()), t.getName()));
            }
        }
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        List<PlanningDay> plannings = a == null ? null : a.loadTeacherPlanning(t == null ? -1 : t.getId());
        if (plannings == null) {
            plannings = new ArrayList<>();
        }
        getModel().setPlanning(plannings);
        Set<String> courses = new HashSet<>();
        Set<String> classes = new HashSet<>();
        for (PlanningDay pp : plannings) {
            for (PlanningHour h : pp.getHours()) {
                if (!Strings.isNullOrEmpty(h.getSubject())) {
                    courses.add(h.getSubject().trim());
                }
                if (!Strings.isNullOrEmpty(h.getStudents())) {
                    classes.add(h.getStudents().trim());
                }
            }
        }
        getModel().setClassNames(new ArrayList<>(classes));
        getModel().setCourseNames(new ArrayList<>(courses));
    }

    @OnPageLoad
    public void onPageLoad() {
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
    }

    public String resolveCssStyleForClass(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        return "background-color: " + resolveColorForClass(value) + ";"
                + "    border-color: darkgray;\n"
                + "    border-width: thin;"
                + "    border-style: groove;"
                + "    border-radius: 7px;"
                + "    padding: 2px;";
    }

    public String resolveColorForClass(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        return colorsClass[Math.abs(getModel().getClassNames().indexOf(value.trim())) % colorsClass.length];
    }

    public String resolveCssStyleForCourse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        return "background-color:" + resolveColorForCourse(value);
    }

    public String resolveColorForCourse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        return colorsCourse[Math.abs(getModel().getCourseNames().indexOf(value.trim())) % colorsCourse.length];

    }
}
