package net.vpc.app.vainruling.plugins.calendars.web;

import java.util.ArrayList;
import java.util.Arrays;
import net.vpc.app.vainruling.core.service.CorePlugin;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.plugins.calendars.model.AppCalendar;
import net.vpc.app.vainruling.plugins.calendars.model.AppCalendarEvent;
import net.vpc.app.vainruling.plugins.calendars.model.RuntimeAppCalendarEvent;
import net.vpc.app.vainruling.plugins.calendars.model.RuntimeAppCalendarProperty;
import net.vpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import net.vpc.common.util.MutableDate;
import org.apache.commons.lang3.StringUtils;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrOnPageLoad;

@VrPage(
        menu = "/Calendars",
        url = "modules/calendars/my-events-calendars",
        securityKey = "vr-calendars.EventsCalendar"
)
//@SecurityKey("vr-calendars.EventsCalendar")
@Controller
public class EventsCalendarsCtrl {

    @Autowired
    private CalendarsPlugin cals;
    @Autowired
    private CorePlugin core;
    private final Model model = new Model();

    public Model getModel() {
        return model;
    }

//    @PostConstruct
    @VrOnPageLoad
    public void init() {
        onRefreshAll();
    }

    public void onRefreshAll() {
        onRefreshCalendarList();
        onRefreshCurrentCalendar();
    }

    public void onRefreshCalendarList() {
        //retrieve all events
        Map<String, AppCalendar> map = new HashMap<String, AppCalendar>();
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem("*", "Tous les Calendriers"));

        AppCalendar d = cals.findMyPrivateEventCalendar();
        for (AppCalendar ii : cals.findMyEventCalendars()) {
            map.put(ii.getCode(), ii);
            if (d != null && !d.getCode().equals(ii.getCode())) {
                list.add(new SelectItem(ii.getCode(), ii.getName()));
            } else {
                list.add(new SelectItem(ii.getCode(), "Mon Calendrier Privé"));
            }
        }
        if (getModel().getCalendar() == null || !map.containsKey(getModel().getCalendar())) {
            getModel().setCalendar("*");
//            getModel().setCalendar(d == null ? null : d.getCode());
        }
        getModel().setCalendarsMap(map);
        getModel().setCalendars(list);
    }

    public void onRefreshCurrentCalendar() {
        String c = getModel().getCalendar();
        boolean allCalendars = isAllCalendarsSelected();
        List<AppCalendarEvent> l = cals.findMyEventCalendarEvents(allCalendars ? null : c);
        getModel().setEventModel(new CalendarEventsModel());
        for (AppCalendarEvent e : l) {
            getModel().getEventModel().addEvent(e, false);
        }
        AppCalendar r = allCalendars ? cals.findMyPrivateEventCalendar() : cals.findMyEventCalendar(c);
        getModel().setCurrentCalendarEditable(allCalendars || (r != null && cals.isEventCalendarWriteAllowed(r)));
        setSelectedNewEvent();
    }

    private boolean isAllCalendarsSelected() {
        String c = getModel().getCalendar();
        return c == null || c.equals("*");
    }

    protected AppCalendar getMySelectedWriteCalendar() {
        boolean allCalendars = isAllCalendarsSelected();
        if (allCalendars) {
            return cals.findMyDefaultEditEventCalendar();
        }
        AppCalendar cal = cals.findMyEventCalendar(getModel().getCalendar());
        if (cal == null) {
            cal = cals.findMyDefaultEditEventCalendar();
        }
        if (!cals.isEventCalendarWriteAllowed(cal)) {
            cal = cals.findMyDefaultEditEventCalendar();
        }
        return cal;
    }

    protected void setSelectedNewEvent() {
        AppCalendarEvent e = new AppCalendarEvent();
        e.setCalendar(getMySelectedWriteCalendar());
        e.setOwner(core.getCurrentUser());
        getModel().setEvent(e);
        getModel().setCurrentCalendarEditable(true);
        getModel().setCurrentEventEditable(true);
        getModel().setEventProperties(new ArrayList<>());
        if (isAllCalendarsSelected() || !cals.isEventCalendarWriteAllowed(e.getCalendar())) {
            List<AppCalendar> list = cals.findMyWritableEventCalendars();
            AppCalendar d = cals.findMyPrivateEventCalendar();
            for (AppCalendar ii : list) {
                if (d != null && !d.getCode().equals(ii.getCode())) {
                    //
                } else if(d!=null){
                    ii.setName("Mon Calendrier Privé");
                }
            }
            getModel().setWriteCalendars(list);
        } else {
            getModel().setWriteCalendars(new ArrayList<>(Arrays.asList(e.getCalendar())));
        }
    }

    public void onSave() {
        try {
            AppCalendarEvent e = (AppCalendarEvent) getModel().getEvent();
            String cn = getModel().getCalendar();
            if (e.getCalendar() == null) {
                AppCalendar cal = (StringUtils.isBlank(cn)) ? cals.findMyPrivateEventCalendar() : cals.findEventCalendarByCode(cn);
                e.setCalendar(cal);
            }
            getModel().eventModel.addEvent(e, true);
            setSelectedNewEvent();
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage("Missing Dates");
            addMessage(message);
        }
    }

    public void onDelete() {
        if (!(core.getCurrentUser().equals(getModel().getEvent().getOwner()))) {
            FacesMessage message = new FacesMessage("Vous n'êtes pas le propriétaire de cet événement.Impossible de le supprimer.");
            addMessage(message);
        } else {
            getModel().eventModel.deleteEvent(getModel().getEvent().getId());
            setSelectedNewEvent();
        }
    }

    public void onEventSelect(AppCalendarEvent e) {
        getModel().setEvent(e);
        getModel().setCurrentEventEditable(e.getOwner() != null && e.getOwner().getId() == core.getCurrentUserId());
        if (e instanceof RuntimeAppCalendarEvent) {
            getModel().setCurrentEventEditable(false);
            getModel().setEventProperties(((RuntimeAppCalendarEvent) e).getProperties());
            if (getModel().getEventProperties() == null) {
                getModel().setEventProperties(new ArrayList<>());
            }
        } else {
            getModel().setWriteCalendars(cals.findMyWritableEventCalendars());
            if (e.getCalendar() != null) {
                boolean found = false;
                for (AppCalendar writeCalendar : getModel().getWriteCalendars()) {
                    if (writeCalendar.getCode() != null && writeCalendar.getCode().equals(e.getCalendar().getCode())) {
                        found = true;
                    }
                }
                //workaround when Calendar security changes by not allowed event to be updated
                if (!found) {
                    getModel().getWriteCalendars().add(e.getCalendar());
                }
            }
            getModel().setEventProperties(new ArrayList<>());
        }
        getModel().setCurrentEventNew(false);
    }

    public void onEventSelect(SelectEvent selectEvent) {
        AppCalendarEvent e = CalendarEventAdapter.toCalendarEvent((CalendarEventExt) selectEvent.getObject());
        onEventSelect(e);
        getModel().setCurrentEventNew(false);
    }

    public void onDateSelect(SelectEvent selectEvent) {
        Date d = (Date) selectEvent.getObject();
        MutableDate md = new MutableDate(d);
        Date s = md.getDateTime();
        md.addHours(1);
        Date d2 = md.getDateTime();
        AppCalendarEvent e = new AppCalendarEvent("TODO", s, d2);
        e.setOwner(core.getCurrentUser());
        e.setCalendar(getMySelectedWriteCalendar());
        onEventSelect(e);
        if (isAllCalendarsSelected() || !cals.isEventCalendarWriteAllowed(e.getCalendar())) {
            getModel().setWriteCalendars(cals.findMyWritableEventCalendars());
        } else {
            getModel().setWriteCalendars(new ArrayList<>(Arrays.asList(e.getCalendar())));
        }
        getModel().setCurrentEventNew(true);
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
        //TODO save ??
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
        //TODO save ??
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public class Model {

        private CalendarEventsModel eventModel;
        private String calendar;
        private Map<String, AppCalendar> calendarsMap;
        private List<SelectItem> calendars;
        private List<AppCalendar> writeCalendars;
        private boolean currentEventNew;
        private boolean currentEventEditable;
        private boolean currentCalendarEditable;
        private AppCalendarEvent event;
        private List<RuntimeAppCalendarProperty> eventProperties;

        public boolean isCurrentEventNew() {
            return currentEventNew;
        }

        public void setCurrentEventNew(boolean currentEventNew) {
            this.currentEventNew = currentEventNew;
        }

        public boolean isCurrentCalendarEditable() {
            return currentCalendarEditable;
        }

        public void setCurrentCalendarEditable(boolean currentCalendarEditable) {
            this.currentCalendarEditable = currentCalendarEditable;
        }

        public List<RuntimeAppCalendarProperty> getEventProperties() {
            return eventProperties;
        }

        public void setEventProperties(List<RuntimeAppCalendarProperty> eventProperties) {
            this.eventProperties = eventProperties;
        }

        public List<AppCalendar> getWriteCalendars() {
            return writeCalendars;
        }

        public void setWriteCalendars(List<AppCalendar> writeCalendars) {
            this.writeCalendars = writeCalendars;
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

        public Map<String, AppCalendar> getCalendarsMap() {
            return calendarsMap;
        }

        public void setCalendarsMap(Map<String, AppCalendar> calendarsMap) {
            this.calendarsMap = calendarsMap;
        }

        public List<SelectItem> getCalendars() {
            return calendars;
        }

        public void setCalendars(List<SelectItem> calendars) {
            this.calendars = calendars;
        }

        public boolean isCurrentEventEditable() {
            return currentEventEditable;
        }

        public void setCurrentEventEditable(boolean currentEventEditable) {
            this.currentEventEditable = currentEventEditable;
        }

    }
}
