package net.vpc.app.vainruling.plugins.academic.internship.service.model.planning;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 5/22/16.
 */
public class PlanningTeacherStats {
    public String teacherName;
    public double supervisor;
    public int chair;
    public int examiner;
    public int activities;
    public double chairBalance;
    public double examinerBalance;
    public int days;
    public List<PlanningTime> times = new ArrayList<>();

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public double getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(double supervisor) {
        this.supervisor = supervisor;
    }

    public int getChair() {
        return chair;
    }

    public void setChair(int chair) {
        this.chair = chair;
    }

    public int getExaminer() {
        return examiner;
    }

    public void setExaminer(int examiner) {
        this.examiner = examiner;
    }

    public int getActivities() {
        return activities;
    }

    public void setActivities(int activities) {
        this.activities = activities;
    }

    public double getChairBalance() {
        return chairBalance;
    }

    public void setChairBalance(double chairBalance) {
        this.chairBalance = chairBalance;
    }

    public double getExaminerBalance() {
        return examinerBalance;
    }

    public void setExaminerBalance(double examinerBalance) {
        this.examinerBalance = examinerBalance;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public List<PlanningTime> getTimes() {
        return times;
    }

    public void setTimes(List<PlanningTime> times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "TeacherStats{" +
                "supervisor=" + supervisor +
                ", chair=" + chair +
                ", examiner=" + examiner +
                ", activities=" + activities +
                ", chairBalance=" + chairBalance +
                ", examinerBalance=" + examinerBalance +
                ", days=" + days +
                ", times=" + times +
                '}';
    }
}
