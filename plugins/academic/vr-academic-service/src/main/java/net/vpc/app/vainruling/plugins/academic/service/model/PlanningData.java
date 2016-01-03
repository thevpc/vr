/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model;

import java.util.List;

/**
 *
 * @author vpc
 */
public class PlanningData {

    private String planningName;
    private String planningUniformName;
    private List<PlanningDay> days;

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
