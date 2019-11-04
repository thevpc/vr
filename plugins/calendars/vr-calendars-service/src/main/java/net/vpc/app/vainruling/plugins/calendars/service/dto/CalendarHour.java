/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.service.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class CalendarHour {

    private String hour;
    private List<CalendarActivity> activities = new ArrayList<>();

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public List<CalendarActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<CalendarActivity> activities) {
        this.activities = activities;
    }

    @Override
    public String toString() {
        return "CalendarHour{" + "hour=" + hour + ", activities=" + activities + '}';
    }

}
