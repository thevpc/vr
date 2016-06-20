/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.stat;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherSemestrialLoad;

/**
 * @author vpc
 */
public class TeacherStat {

    private AcademicTeacher teacher;
    private AcademicTeacherPeriod teacherPeriod;
    private AcademicTeacherSemestrialLoad[] semestrialLoad;
    private LoadValue value = new LoadValue();
    private LoadValue extra = new LoadValue();
    private LoadValue due = new LoadValue();
    private LoadValue valueWeek = new LoadValue();
    private LoadValue extraWeek = new LoadValue();
    private LoadValue dueWeek = new LoadValue();
    //    private double valueWeekEquiv;
    private double weeks;
    private double maxWeeks;
    private boolean includeIntents;
    private TeacherSemesterStat[] semesters;

    public LoadValue getExtra() {
        return extra;
    }

    public void setExtra(LoadValue extra) {
        this.extra = extra;
    }

    public LoadValue getDue() {
        return due;
    }

    public void setDue(LoadValue due) {
        this.due = due;
    }

    //    public double getValueWeekEquiv() {
//        return valueWeekEquiv;
//    }
//
//    public void setValueWeekEquiv(double valueWeekEquiv) {
//        this.valueWeekEquiv = valueWeekEquiv;
//    }
    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public TeacherSemesterStat[] getSemesters() {
        return semesters;
    }

    public void setSemesters(TeacherSemesterStat[] semesters) {
        this.semesters = semesters;
    }

    public LoadValue getValue() {
        return value;
    }

    public void setValue(LoadValue value) {
        this.value = value;
    }

    public double getWeeks() {
        return weeks;
    }

    public void setWeeks(double weeks) {
        this.weeks = weeks;
    }

    public double getMaxWeeks() {
        return maxWeeks;
    }

    public void setMaxWeeks(double maxWeeks) {
        this.maxWeeks = maxWeeks;
    }

    public AcademicTeacherSemestrialLoad[] getSemestrialLoad() {
        return semestrialLoad;
    }

    public void setSemestrialLoad(AcademicTeacherSemestrialLoad[] semestrialLoad) {
        this.semestrialLoad = semestrialLoad;
    }

    public LoadValue getExtraWeek() {
        return extraWeek;
    }

    public void setExtraWeek(LoadValue extraWeek) {
        this.extraWeek = extraWeek;
    }

    public LoadValue getDueWeek() {
        return dueWeek;
    }

    public void setDueWeek(LoadValue dueWeek) {
        this.dueWeek = dueWeek;
    }

    public LoadValue getValueWeek() {
        return valueWeek;
    }

    public boolean isIncludeIntents() {
        return includeIntents;
    }

    public void setIncludeIntents(boolean includeIntents) {
        this.includeIntents = includeIntents;
    }

    public AcademicTeacherPeriod getTeacherPeriod() {
        return teacherPeriod;
    }

    public void setTeacherPeriod(AcademicTeacherPeriod teacherPeriod) {
        this.teacherPeriod = teacherPeriod;
    }
}
