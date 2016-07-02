/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.stat;

import net.vpc.app.vainruling.plugins.academic.service.CourseFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherSemestrialLoad;

/**
 * @author vpc
 */
public abstract class TeacherBaseStat {
    private LoadValue value = new LoadValue();
    private LoadValue valueWeek = new LoadValue();
    private LoadValue extraWeek = new LoadValue();
    private LoadValue extra = new LoadValue();
    private LoadValue due = new LoadValue();
    private LoadValue dueWeek = new LoadValue();
    private double weeks;
    private double maxWeeks;


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

    public abstract AcademicTeacher getTeacher();
}
