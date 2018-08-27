/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.dto;

import net.vpc.app.vainruling.plugins.academic.service.stat.DeviationConfig;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultCourseAssignmentFilter;

/**
 *
 * @author vpc
 */
public class TeacherLoadInfoFilter {

    private DefaultCourseAssignmentFilter teacherCourseAssignmentFilter;
    private DefaultCourseAssignmentFilter otherCourseAssignmentFilter;
    private DeviationConfig deviationConfig;
    private int teacherId;
    private int periodId;
    private boolean filterConflict;
    boolean filterAssigned;
    boolean filterNonAssigned;
    boolean filterIntended;
    boolean filterNonIntended;
    boolean filterLocked;
    boolean filterUnlocked;

    public boolean isFilterAssigned() {
        return filterAssigned;
    }

    public void setFilterAssigned(boolean filterAssigned) {
        this.filterAssigned = filterAssigned;
    }

    public boolean isFilterNonAssigned() {
        return filterNonAssigned;
    }

    public void setFilterNonAssigned(boolean filterNonAssigned) {
        this.filterNonAssigned = filterNonAssigned;
    }

    public boolean isFilterIntended() {
        return filterIntended;
    }

    public void setFilterIntended(boolean filterIntended) {
        this.filterIntended = filterIntended;
    }

    public boolean isFilterNonIntended() {
        return filterNonIntended;
    }

    public void setFilterNonIntended(boolean filterNonIntended) {
        this.filterNonIntended = filterNonIntended;
    }

    public boolean isFilterLocked() {
        return filterLocked;
    }

    public void setFilterLocked(boolean filterLocked) {
        this.filterLocked = filterLocked;
    }

    public boolean isFilterUnlocked() {
        return filterUnlocked;
    }

    public void setFilterUnlocked(boolean filterUnlocked) {
        this.filterUnlocked = filterUnlocked;
    }

    public boolean isFilterConflict() {
        return filterConflict;
    }

    public void setFilterConflict(boolean filterConflict) {
        this.filterConflict = filterConflict;
    }

    public DefaultCourseAssignmentFilter getOtherCourseAssignmentFilter() {
        return otherCourseAssignmentFilter;
    }

    public void setOtherCourseAssignmentFilter(DefaultCourseAssignmentFilter otherCourseAssignmentFilter) {
        this.otherCourseAssignmentFilter = otherCourseAssignmentFilter;
    }

    public DefaultCourseAssignmentFilter getTeacherCourseAssignmentFilter() {
        return teacherCourseAssignmentFilter;
    }

    public void setTeacherCourseAssignmentFilter(DefaultCourseAssignmentFilter teacherCourseAssignmentFilter) {
        this.teacherCourseAssignmentFilter = teacherCourseAssignmentFilter;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getPeriodId() {
        return periodId;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

    public DeviationConfig getDeviationConfig() {
        return deviationConfig;
    }

    public void setDeviationConfig(DeviationConfig deviationConfig) {
        this.deviationConfig = deviationConfig;
    }
}
