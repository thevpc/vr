/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.dto;

import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.plugins.academic.model.internship.planning.PlanningActivity;
import net.vpc.app.vainruling.plugins.academic.model.internship.planning.PlanningActivityTable;

/**
 *
 * @author vpc
 */
public class PlanningActivityTableSerializable {

    private List<PlanningActivitySerializable> activities = new ArrayList<>();

    public List<PlanningActivitySerializable> getActivities() {
        return activities;
    }

    public void setActivities(List<PlanningActivitySerializable> activities) {
        this.activities = activities;
    }

    public PlanningActivityTableSerializable loadPlanningActivityTable(PlanningActivityTable t) {
        for (PlanningActivity activity : t.getActivities()) {
            getActivities().add(new PlanningActivitySerializable().loadActivity(activity));
        }
        return this;
    }

    public PlanningActivityTable toPlanningActivityTable() {
        PlanningActivityTable t = new PlanningActivityTable();
        for (PlanningActivitySerializable activity : activities) {
            t.addActivity(activity.toActivity());
        }
        t.setDefaultChairsAndExaminers();
        return t;
    }
}
