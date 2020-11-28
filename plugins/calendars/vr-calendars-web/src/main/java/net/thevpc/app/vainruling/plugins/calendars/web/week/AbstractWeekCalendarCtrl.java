/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.calendars.web.week;

import java.util.ArrayList;

import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarActivity;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarDay;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarHour;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.thevpc.app.vainruling.core.web.VrColorTable;

import java.util.Collections;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class AbstractWeekCalendarCtrl {

    protected Model model;

    public Model getModel() {
        return model;
    }

    public String resolveCssStyleForClass(CalendarActivity hour) {
        if (hour.getStudentsIndex() < 0) {
            return "";
        }
        return "background-color: " + VrColorTable.get().getFgColor(hour.getStudentsIndex()) + ";"
                + "    border-color: darkgray;\n"
                + "    border-width: thin;"
                + "    border-style: groove;"
                + "    border-radius: 7px;"
                + "    padding: 2px;";
    }

    public String resolveCssStyleForCourse(CalendarActivity hour) {
        if (hour.getSubjectIndex() < 0) {
            return "";
        }
        return "background-color:" + VrColorTable.get().getBgColor(hour.getSubjectIndex());
    }

    public static class Model {

        WeekCalendar calendar;
        List<String> hours = new ArrayList<>();

        public List<String> getHours() {
            return hours;
        }

        public String getHour(int index) {
            return hours.get(index);
        }

        public WeekCalendar getCalendar() {
            return calendar;
        }

        public void setCalendar(WeekCalendar calendar) {
            this.calendar = calendar;
            this.hours = new ArrayList<>();
            if (calendar != null) {
                for (CalendarHour hour : getPlanning().get(0).getHours()) {
                    this.hours.add(hour.getHour());
                }
            }
        }

        public List<CalendarDay> getPlanning() {
            return calendar == null ? Collections.emptyList() : calendar.getDays();
        }
    }

}
