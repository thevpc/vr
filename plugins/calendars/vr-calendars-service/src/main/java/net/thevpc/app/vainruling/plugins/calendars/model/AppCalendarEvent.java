package net.thevpc.app.vainruling.plugins.calendars.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.config.Entity;
import net.thevpc.upa.config.Id;
import net.thevpc.upa.config.Path;

import java.util.Date;
import java.util.Objects;
import net.thevpc.app.vainruling.core.service.model.AppArea;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.upa.config.Main;
import net.thevpc.upa.config.ManyToOne;
import net.thevpc.upa.config.Property;
import net.thevpc.upa.config.Sequence;
import net.thevpc.upa.config.Summary;

/**
 * @author oussama
 */
@Entity
@Path("/Repository/General")
public class AppCalendarEvent {

    @Id
    @Sequence
    private int id;
    @Main
    private String title;
    @Summary
    private AppUser owner;
    @Summary
    private AppCalendarEventType eventType;
    @Summary
    private AppCalendar calendar;
    @Summary
    private Date startDate;
    @Summary
    private Date endDate;
    private boolean allDay = false;
    @ManyToOne(filter = "that.type.roomSpace=true")
    private AppArea location;
    private String otherLocation;
    private String styleClass;
    private String referenceType;
    private String referenceValue;
    private Object data;
    private boolean editable = true;
    private String url;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;

    public AppCalendarEvent() {
    }

    public AppCalendarEvent(String title, Date start, Date end) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
    }

    public AppCalendarEvent(String title, Date start, Date end, boolean allDay) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.allDay = allDay;
    }

    public AppCalendarEvent(String title, Date start, Date end, String styleClass) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.styleClass = styleClass;
    }

    public AppCalendarEvent(String title, Date start, Date end, Object data) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isAllDay() {
        return this.allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getStyleClass() {
        return this.styleClass;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppCalendarEvent that = (AppCalendarEvent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public AppCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(AppCalendar calendar) {
        this.calendar = calendar;
    }

    public AppCalendarEventType getEventType() {
        return eventType;
    }

    public void setEventType(AppCalendarEventType eventType) {
        this.eventType = eventType;
    }

    public AppArea getLocation() {
        return location;
    }

    public void setLocation(AppArea location) {
        this.location = location;
    }

    public String getOtherLocation() {
        return otherLocation;
    }

    public void setOtherLocation(String otherLocation) {
        this.otherLocation = otherLocation;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceValue() {
        return referenceValue;
    }

    public void setReferenceValue(String referenceValue) {
        this.referenceValue = referenceValue;
    }

}
