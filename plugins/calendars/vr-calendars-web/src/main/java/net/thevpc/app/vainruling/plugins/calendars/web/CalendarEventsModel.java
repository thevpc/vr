package net.thevpc.app.vainruling.plugins.calendars.web;

import net.thevpc.app.vainruling.plugins.calendars.model.AppCalendarEvent;
import net.thevpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.upa.UPA;

public class CalendarEventsModel implements ScheduleModel {

    private Map<String, AppCalendarEvent> events;
    private List<ScheduleEvent> cachedEventsList;
    private boolean eventLimit = false;

    public CalendarEventsModel() {
        this.events = new HashMap<>();
    }

//    public CalendarEventsModel(List<ScheduleEvent> events) {
//        this.events = events;
//    }
    public void addEvent(AppCalendarEvent event, boolean save) {
        if (save) {
            CalendarsPlugin it = VrApp.getBean(CalendarsPlugin.class);
            AppCalendarEvent e = (AppCalendarEvent) event;
            it.saveEventCalendarEvent(e);
        }
        this.events.put(String.valueOf(event.getId()), event);
        cachedEventsList = null;
    }

    public boolean deleteEvent(int eventId) {
        CalendarsPlugin it = VrApp.getBean(CalendarsPlugin.class);
        it.removeEventCalendarEvent(eventId);
        cachedEventsList = null;
        return this.events.remove(String.valueOf(eventId)) != null;
    }

    @Override
    public void addEvent(ScheduleEvent scheduleEvent) {
        addEvent(CalendarEventAdapter.toCalendarEvent((CalendarEventExt) scheduleEvent), false);
        cachedEventsList = null;
    }

    @Override
    public boolean deleteEvent(ScheduleEvent scheduleEvent) {
        return deleteEvent(scheduleEvent);
//        cachedEventsList = null;
//        return this.events.remove(scheduleEvent.getId()) != null;
    }

    public List<ScheduleEvent> getEvents() {
        if (cachedEventsList == null) {
            List<ScheduleEvent> list = new ArrayList<>();
            for (AppCalendarEvent value : events.values()) {
                list.add(new CalendarEventAdapter(value));
            }
            cachedEventsList = list;
        }
        return cachedEventsList;
    }

    public ScheduleEvent getEvent(String id) {
        AppCalendarEvent v = events.get(id);
        if (v != null) {
            return new CalendarEventAdapter(v);
        }
        return null;
    }

    @Override
    public void updateEvent(ScheduleEvent event) {
        AppCalendarEvent ce = CalendarEventAdapter.toCalendarEvent((CalendarEventExt) event);
        events.put(event.getId(), ce);
        cachedEventsList = null;
    }

    public void updateEvent(AppCalendarEvent event) {
        events.put(String.valueOf(event.getId()), event);
        if (event.getId() >= 0) {
            UPA.getPersistenceUnit().merge(event);
        }
        cachedEventsList = null;
    }

    public int getEventCount() {
        return this.events.size();
    }

    public void clear() {
        this.events.clear();
        cachedEventsList = null;
    }

    public boolean isEventLimit() {
        return this.eventLimit;
    }

    public void setEventLimit(boolean eventLimit) {
        this.eventLimit = eventLimit;
    }
}
