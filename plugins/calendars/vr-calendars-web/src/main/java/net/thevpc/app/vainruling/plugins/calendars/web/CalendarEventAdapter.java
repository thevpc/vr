/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.calendars.web;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;

import net.thevpc.app.vainruling.plugins.calendars.model.AppCalendarEvent;
import net.thevpc.app.vainruling.plugins.calendars.model.RuntimeAppCalendarEvent;
import net.thevpc.app.vainruling.core.service.model.AppArea;
import net.thevpc.common.strings.StringUtils;
import org.primefaces.model.ScheduleRenderingMode;
//import org.primefaces.model.ScheduleRenderingMode;

/**
 *
 * @author vpc
 */
public class CalendarEventAdapter implements CalendarEventExt {

    private AppCalendarEvent event;
    private String typeName;

    public static AppCalendarEvent toCalendarEvent(CalendarEventExt e) {
        if (e == null) {
            return null;
        }
        if (e instanceof CalendarEventAdapter) {
            return ((CalendarEventAdapter) e).event;
        }
        String t = e.getTypeName();
        AppCalendarEvent ce = null;
        if ("AppCalendarEvent".equals(t)) {
            ce = new AppCalendarEvent();
        } else if ("RuntimeAppCalendarEvent".equals(t)) {
            ce = new RuntimeAppCalendarEvent();
        } else {
            throw new IllegalArgumentException("Unsupported");
        }
        ce.setAllDay(e.isAllDay());
        ce.setData(e.getData());
        ce.setDescription(e.getDescription());
        ce.setEditable(e.isEditable());
        ce.setEndDate(e.getEndDate()==null?null:java.util.Date.from(e.getEndDate().atZone(ZoneId.systemDefault()).toInstant()));
        ce.setId(Integer.parseInt(e.getId()));
        ce.setStartDate(e.getStartDate()==null?null:java.util.Date.from(e.getStartDate().atZone(ZoneId.systemDefault()).toInstant()));
        ce.setStyleClass(e.getStyleClass());
        ce.setTitle(e.getSimpleTitle());
        ce.setUrl(e.getUrl());
        return ce;
    }

    public CalendarEventAdapter(AppCalendarEvent event) {
        this.event = event;
        if (event instanceof AppCalendarEvent) {
            typeName = "AppCalendarEvent";
        } else if (event instanceof RuntimeAppCalendarEvent) {
            typeName = "RuntimeAppCalendarEvent";
        } else {
            throw new IllegalArgumentException("Unsupported");
        }
    }

    @Override
    public String getId() {
        return String.valueOf(event.getId());
    }

    @Override
    public void setId(String id) {
        event.setId(Integer.parseInt(id));
    }

    @Override
    public Object getData() {
        return event.getData();
    }

    public String getSimpleTitle() {
        return event.getTitle();
    }

    @Override
    public String getTitle() {
        String t = event.getTitle();
        if (StringUtils.isBlank(t)) {
            t = "NO_NAME";
        }
        AppArea loc = event.getLocation();
        if (loc != null) {
            t += " (" + loc.getName() + ")";
        } else if (!StringUtils.isBlank(event.getOtherLocation())) {
            t += " (" + StringUtils.trim(event.getOtherLocation()) + ")";
        }
        return t;
    }

    @Override
    public LocalDateTime getStartDate() {
        return event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public LocalDateTime getEndDate() {
        return event.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public String getGroupId() {
        return null;
    }

    @Override
    public void setStartDate(LocalDateTime start) {
        event.setStartDate(start==null?null:java.sql.Timestamp.valueOf(start));
    }

    @Override
    public void setEndDate(LocalDateTime end) {
        event.setStartDate(end==null?null:java.sql.Timestamp.valueOf(end));
    }

    @Override
    public boolean isOverlapAllowed() {
        return true;
    }

    @Override
    public ScheduleRenderingMode getRenderingMode() {
        return ScheduleRenderingMode.BACKGROUND;
    }

    @Override
    public Map getDynamicProperties() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isAllDay() {
        return event.isAllDay();
    }

    @Override
    public String getStyleClass() {
        String s = event.getStyleClass();
        if (s == null) {
            s = "";
        }
        if (event instanceof AppCalendarEvent) {
            AppCalendarEvent t = (AppCalendarEvent) event;
            String s2 = t.getEventType() == null ? null : t.getEventType().getStyleClass();
            if (s2 == null) {
                s2 = "";
            }
            s = s2 + " " + s;
        }
        s = s.trim();
        return s;
    }

    @Override
    public boolean isEditable() {
        return event.isEditable();
    }

    @Override
    public String getDescription() {
        return event.getDescription();
    }

    @Override
    public String getUrl() {
        return event.getUrl();
    }

    public String getTypeName() {
        return typeName;
    }
//    v7.0
//    @Override
//    public ScheduleRenderingMode getRenderingMode() {
//        return ScheduleRenderingMode.BACKGROUND;
//    }

//    v7.0
//    @Override
//    public Map<String, Object> getDynamicProperties() {
//        return Collections.emptyMap();
//    }
}
