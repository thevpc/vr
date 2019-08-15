/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.web.week;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.VrColorTable;
import net.vpc.app.vainruling.plugins.calendars.service.dto.CalendarDay;
import net.vpc.app.vainruling.plugins.calendars.service.dto.CalendarHour;
import net.vpc.common.strings.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author taha.bensalah@gmail.com
 */
public class AbstractWeekCalendarCtrl {



    protected Model model;

    public Model getModel() {
        return model;
    }

    public void updateModel(List<CalendarDay> plannings) {
        if (plannings == null) {
            plannings = new ArrayList<>();
        }
        getModel().setPlanning(plannings);
        Set<String> courses = new HashSet<>();
        Set<String> classes = new HashSet<>();
        for (CalendarDay p : plannings) {
            for (CalendarHour h : p.getHours()) {
                if (!StringUtils.isBlank(h.getSubject())) {
                    courses.add(h.getSubject().trim());
                }
                if (!StringUtils.isBlank(h.getStudents())) {
                    classes.add(h.getStudents().trim());
                }
            }
        }
        getModel().setClassNames(new ArrayList<>(classes));
        getModel().setCourseNames(new ArrayList<>(courses));
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
        VrColorTable table = VrApp.getBean(VrColorTable.class);
        return table.getFgColor(getModel().getClassNames().indexOf(value.trim()));
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
        VrColorTable table = VrApp.getBean(VrColorTable.class);
        return table.getBgColor(getModel().getCourseNames().indexOf(value.trim()));
    }

    public static class Model {

        List<CalendarDay> planning = new ArrayList<>();
        List<String> courseNames = new ArrayList<>();
        List<String> classNames = new ArrayList<>();

        public CalendarHour getHour(int index) {
            return planning.get(0).getHours().get(index);
        }

        public List<CalendarDay> getPlanning() {
            return planning;
        }

        public void setPlanning(List<CalendarDay> planning) {
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

}
