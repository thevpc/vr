/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.planning.service.model;

import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;

/**
 *
 * @author vpc
 */
public class CalendarCoursePlan {

    private String coursePlanName;
    private AcademicCoursePlan coursePlan;

    public CalendarCoursePlan(String coursePlanName, AcademicCoursePlan coursePlan) {
        this.coursePlanName = coursePlanName;
        this.coursePlan = coursePlan;
    }

    public boolean isMissingEntity() {
        return coursePlan == null;
    }

    public boolean isMissingPlanning() {
        return coursePlanName == null;
    }

    public String getName() {
        if (coursePlan != null) {
            return coursePlan.getName();
        }
        if (coursePlanName != null) {
            return coursePlanName;
        }
        return null;
    }

    public String getCoursePlanName() {
        return coursePlanName;
    }

    public void setCoursePlanName(String coursePlanName) {
        this.coursePlanName = coursePlanName;
    }

    public AcademicCoursePlan getCoursePlan() {
        return coursePlan;
    }

    public void setCoursePlan(AcademicCoursePlan coursePlan) {
        this.coursePlan = coursePlan;
    }

}
