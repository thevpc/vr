/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.service.dto;

//import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
//@XmlRootElement
public class WeekCalendar {

    private String id;
    private String sourceName;
    private String planningName;
    private String planningUniformName;
    private List<CalendarDay> days;

    public WeekCalendar() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanningName() {
        return planningName;
    }

    public void setPlanningName(String planningName) {
        this.planningName = planningName;
    }

    public List<CalendarDay> getDays() {
        return days;
    }

    public void setDays(List<CalendarDay> days) {
        this.days = days;
    }

    @Override
    public String toString() {
        return "CalendarWeek{" + "planningName=" + planningName + ", days=" + days + '}';
    }

    public String getPlanningUniformName() {
        return planningUniformName;
    }

    public void setPlanningUniformName(String planningUniformName) {
        this.planningUniformName = planningUniformName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
