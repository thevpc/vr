package net.thevpc.app.vainruling.plugins.calendars.web;

import java.util.ArrayList;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.plugins.calendars.model.AppCalendar;
import net.thevpc.app.vainruling.plugins.calendars.model.AppCalendarEvent;
import net.thevpc.app.vainruling.plugins.calendars.model.RuntimeAppCalendarEvent;
import net.thevpc.app.vainruling.plugins.calendars.model.RuntimeAppCalendarProperty;
import net.thevpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

import net.thevpc.app.vainruling.VrPage;

@VrPage(
        url = "modules/calendars/public-events-calendars"
)
@Controller
public class PublicEventsCalendarsCtrl {

    @Autowired
    private CalendarsPlugin cals;
    @Autowired
    private CorePlugin core;
    private final Model model = new Model();

    public Model getModel() {
        return model;
    }

    @VrOnPageLoad
    public void init() {
        List<AppCalendarEvent> l = cals.findMyEventCalendarEvents(null);
        getModel().setEventModel(new CalendarEventsModel());
        for (AppCalendarEvent e : l) {
            getModel().getEventModel().addEvent(e, false);
        }
    }

    private boolean isAllCalendarsSelected() {
        String c = getModel().getCalendar();
        return c == null || c.equals("*");
    }

    protected AppCalendar getMySelectedWriteCalendar() {
        boolean allCalendars = isAllCalendarsSelected();
        if (allCalendars) {
            return cals.findMyPrivateEventCalendar();
        }
        AppCalendar cal = cals.findMyEventCalendar(getModel().getCalendar());
        if (cal == null) {
            cal = cals.findMyPrivateEventCalendar();
        }
        if (!cals.isEventCalendarWriteAllowed(cal)) {
            cal = cals.findMyPrivateEventCalendar();
        }
        return cal;
    }

    public void onEventSelect(AppCalendarEvent e) {
        getModel().setEvent(e);
        if (e instanceof RuntimeAppCalendarEvent) {
            getModel().setEventProperties(((RuntimeAppCalendarEvent) e).getProperties());
            if (getModel().getEventProperties() == null) {
                getModel().setEventProperties(new ArrayList<>());
            }
        } else {
            getModel().setEventProperties(new ArrayList<>());
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        AppCalendarEvent e = CalendarEventAdapter.toCalendarEvent((CalendarEventExt) selectEvent.getObject());
        onEventSelect(e);
    }

    public void onDateSelect(SelectEvent selectEvent) {
    }

    public class Model {

        private CalendarEventsModel eventModel;
        private String calendar;
        private AppCalendarEvent event;
        private List<RuntimeAppCalendarProperty> eventProperties;


        public List<RuntimeAppCalendarProperty> getEventProperties() {
            return eventProperties;
        }

        public void setEventProperties(List<RuntimeAppCalendarProperty> eventProperties) {
            this.eventProperties = eventProperties;
        }

        public CalendarEventsModel getEventModel() {
            return eventModel;
        }

        public void setEventModel(CalendarEventsModel eventModel) {
            this.eventModel = eventModel;
        }

        public AppCalendarEvent getEvent() {
            return event;
        }

        public void setEvent(AppCalendarEvent event) {
            this.event = event;
        }

        public String getCalendar() {
            return calendar;
        }

        public void setCalendar(String calendar) {
            this.calendar = calendar;
        }
    }
}
