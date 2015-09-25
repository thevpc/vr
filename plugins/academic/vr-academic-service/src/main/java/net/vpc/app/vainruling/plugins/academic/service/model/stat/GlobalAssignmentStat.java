/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.stat;

import java.util.HashMap;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;

/**
 *
 * @author vpc
 */
public class GlobalAssignmentStat {

    private AcademicTeacherSituation situation;
    private AcademicTeacherDegree degree;
    private AcademicSemester semester;
    private LoadValue value = new LoadValue();
    private LoadValue due = new LoadValue();
    private LoadValue extra = new LoadValue();

    private LoadValue dueWeek = new LoadValue();
    private LoadValue valueWeek = new LoadValue();
    private LoadValue extraWeek = new LoadValue();

    private LoadValue avgValue = new LoadValue();
    private LoadValue avgValueWeek = new LoadValue();
    private LoadValue avgExtra = new LoadValue();
    private LoadValue avgExtraWeek = new LoadValue();

    private LoadValue targetAssignments = new LoadValue();
    private LoadValue missingAssignments = new LoadValue();
    private double weeks;
    private double maxWeeks;
    private int teachersCount;
    private HashMap<Integer, AcademicTeacher> teachers = new HashMap<Integer, AcademicTeacher>();

    public GlobalAssignmentStat() {
    }

    public LoadValue getTargetAssignments() {
        return targetAssignments;
    }

    public LoadValue getMissingAssignments() {
        return missingAssignments;
    }

    public LoadValue getDue() {
        return due;
    }

    public LoadValue getExtra() {
        return extra;
    }

    public LoadValue getDueWeek() {
        return dueWeek;
    }

    public AcademicSemester getSemester() {
        return semester;
    }

    public void setSemester(AcademicSemester semester) {
        this.semester = semester;
    }

    public AcademicTeacherDegree getDegree() {
        return degree;
    }

    public void setDegree(AcademicTeacherDegree degree) {
        this.degree = degree;
    }

    public AcademicTeacherSituation getSituation() {
        return situation;
    }

    public void setSituation(AcademicTeacherSituation situation) {
        this.situation = situation;
    }

    public LoadValue getExtraWeek() {
        return extraWeek;
    }

//    public void setExtraWeek(LoadValue extraWeek) {
//        this.extraWeek = extraWeek;
//    }

    public LoadValue getValue() {
        return value;
    }

//    public void setValue(LoadValue value) {
//        this.value = value;
//    }

    public LoadValue getValueWeek() {
        return valueWeek;
    }

//    public void setValueWeek(LoadValue valueWeek) {
//        this.valueWeek = valueWeek;
//    }

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

    public int getTeachersCount() {
        return teachersCount;
    }

    public void setTeachersCount(int teachersCount) {
        this.teachersCount = teachersCount;
    }

    public HashMap<Integer, AcademicTeacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(HashMap<Integer, AcademicTeacher> teachers) {
        this.teachers = teachers;
    }

    public LoadValue getAvgValue() {
        return avgValue;
    }

    public LoadValue getAvgExtra() {
        return avgExtra;
    }

    public LoadValue getAvgValueWeek() {
        return avgValueWeek;
    }

    public LoadValue getAvgExtraWeek() {
        return avgExtraWeek;
    }

//    public void setAvgExtraWeek(LoadValue avgExtraWeek) {
//        this.avgExtraWeek = avgExtraWeek;
//    }

}
