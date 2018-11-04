/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.vpc.app.vainruling.plugins.calendars.model.RuntimeAppCalendarEvent;
import net.vpc.app.vainruling.plugins.calendars.model.RuntimeAppCalendar;

/**
 *
 * @author vpc
 */
public interface AppCalendarService {

    default RuntimeAppCalendar getMyCalendar(String name) {
        for (RuntimeAppCalendar myCalendar : getMyCalendars()) {
            if (myCalendar.getCode() != null && myCalendar.getCode().equals(name)) {
                return myCalendar;
            }
        }
        return null;
    }

    default List<RuntimeAppCalendar> getPublicCalendars() {
        return Collections.emptyList();
    }

    List<RuntimeAppCalendar> getMyCalendars();

    List<RuntimeAppCalendarEvent> getMyEvents(String calendarCode, Date fromDate, Date toDate);

    default List<RuntimeAppCalendarEvent> getPublicEvents(String calendarCode, Date fromDate, Date toDate){
        return Collections.emptyList();
    }

    default int getMyEventsCount(String calendarCode, Date fromDate, Date toDate) {
        List<RuntimeAppCalendarEvent> e = getMyEvents(calendarCode, fromDate, toDate);
        return e == null ? 0 : e.size();
    }
}
