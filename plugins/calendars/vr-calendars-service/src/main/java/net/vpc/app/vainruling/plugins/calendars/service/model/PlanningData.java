/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.service.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@XmlRootElement
public class PlanningData {

    private String id;
    private String planningName;
    private String planningUniformName;
    private List<PlanningDay> days;

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

    public List<PlanningDay> getDays() {
        return days;
    }

    public void setDays(List<PlanningDay> days) {
        this.days = days;
    }

    @Override
    public String toString() {
        return "PlanningData{" + "planningName=" + planningName + ", days=" + days + '}';
    }

    public String getPlanningUniformName() {
        return planningUniformName;
    }

    public void setPlanningUniformName(String planningUniformName) {
        this.planningUniformName = planningUniformName;
    }

}
