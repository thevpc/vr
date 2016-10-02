/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.service.model;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class CalendarDay {

    private String dayName;
    private List<CalendarHour> hours;

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public List<CalendarHour> getHours() {
        return hours;
    }

    public void setHours(List<CalendarHour> hours) {
        this.hours = hours;
    }

    @Override
    public String toString() {
        return "CalendarDay{" + "dayName=" + dayName + ", hours=" + String.valueOf(hours) + '}';
    }

}
