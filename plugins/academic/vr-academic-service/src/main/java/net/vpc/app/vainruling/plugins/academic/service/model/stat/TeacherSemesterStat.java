/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.stat;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;

/**
 *
 * @author vpc
 */
public class TeacherSemesterStat {

    private AcademicSemester semester;
    private LoadValue value = new LoadValue();
    private LoadValue valueWeek = new LoadValue();
    private LoadValue extraWeek = new LoadValue();
    private LoadValue extra = new LoadValue();
    private LoadValue due = new LoadValue();
    private LoadValue dueWeek = new LoadValue();
    private double weeks;
    private double maxWeeks;
    private TeacherStat teacherStat;

    public TeacherSemesterStat() {
    }

    public LoadValue getExtra() {
        return extra;
    }

    public LoadValue getDue() {
        return due;
    }

    public AcademicSemester getSemester() {
        return semester;
    }

    public void setSemester(AcademicSemester semester) {
        this.semester = semester;
    }

    public LoadValue getExtraWeek() {
        return extraWeek;
    }

    public LoadValue getDueWeek() {
        return dueWeek;
    }

    public void setExtraWeek(LoadValue extraWeek) {
        this.extraWeek = extraWeek;
    }

    public LoadValue getValue() {
        return value;
    }

    public void setValue(LoadValue value) {
        this.value = value;
    }

    public LoadValue getValueWeek() {
        return valueWeek;
    }

    public void setValueWeek(LoadValue valueWeek) {
        this.valueWeek = valueWeek;
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

    public TeacherStat getTeacherStat() {
        return teacherStat;
    }

    public void setTeacherStat(TeacherStat teacherStat) {
        this.teacherStat = teacherStat;
    }

}
