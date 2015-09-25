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
        title = "Mon Emploi du Temps",
        url = "modules/academic/myplanning",
        menu = "/Education"
//,securityKey = "Custom.Education.MyCourseLoad"
)
@ManagedBean
public class MyPlanningCtrl {

    private static final String[] colorsCourse = new String[]{
        "#DDE6CB",
        "#C0F7BA",
        "aliceblue",
        "#E6E7F9",
        "#EAD3F9",
        "#FBFFBE",
        "bisque",
        "beige",
        "#FDD5E0"
    };
    private static final String[] colorsClass = new String[]{
        "mediumseagreen",
        "#3FB3B3",
        "#B563FF",
        "darkorange",
        "darksalmon",
        "deeppink"
    };

    public static class Model {

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

    }
    private Model model = new Model();

    public MyPlanningCtrl() {
    }

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    public void onPageLoad() {
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = a.getCurrentTeacher();
        List<PlanningDay> plannings = a.loadTeacherPlanning(t == null ? -1 : t.getId());
        if(plannings==null){
            plannings=new ArrayList<>();
        }
        getModel().setPlanning(plannings);
        Set<String> courses = new HashSet<>();
        Set<String> classes = new HashSet<>();
        for (PlanningDay p : plannings) {
            for (PlanningHour h : p.getHours()) {
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

    public void onRefresh() {
        onPageLoad();
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
