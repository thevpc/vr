/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.dto;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import net.thevpc.app.vainruling.plugins.academic.model.internship.planning.PlanningActivity;
import net.thevpc.app.vainruling.plugins.academic.model.internship.planning.PlanningInternship;
import net.thevpc.app.vainruling.plugins.academic.model.internship.planning.PlanningRoom;
import net.thevpc.app.vainruling.plugins.academic.model.internship.planning.PlanningSpaceTime;
import net.thevpc.app.vainruling.plugins.academic.model.internship.planning.PlanningTime;

/**
 *
 * @author vpc
 */
public class PlanningActivitySerializable {

    private Date dateTime;
    private int dayIndex;
    private int hourIndex;
    private String code;
    private String room;
    private String student;
    private String supervisors;
    private String chair;
    private String examiner;
    private boolean enabled;

    public PlanningActivitySerializable() {
    }

    public PlanningActivitySerializable loadTime(PlanningTimeSerializable tt) {
        this.setDateTime(tt.getDateTime());
        this.setDayIndex(tt.getDayIndex());
        this.setHourIndex(tt.getHourIndex());
        return this;
    }

    public PlanningActivitySerializable loadActivity(PlanningActivity a) {
        this.dateTime = a.getSpaceTime().getTime().getDateTime();
        this.dayIndex = a.getSpaceTime().getTime().getDayIndex();
        this.hourIndex = a.getSpaceTime().getTime().getHourIndex();
        this.code = a.getInternship().getCode();
        this.room = a.getSpaceTime().getRoom().getName();
        this.student = a.getInternship().getStudent();
        this.supervisors = a.getInternship().getSupervisors().stream().collect(Collectors.joining(","));
        this.chair = a.getChair();
        this.examiner = a.getExaminer();
        this.enabled = a.isEnabled();
        return this;
    }

    public PlanningActivity toActivity() {
        PlanningActivity a = new PlanningActivity();
        PlanningInternship is = new PlanningInternship();
        is.setCode(code);
        is.setStudent(student);
        is.setSupervisors(Arrays.asList(supervisors));
        a.setInternship(is);
        a.setEnabled(enabled);
        a.setExaminer(examiner.trim());
        a.setChair(chair.trim());
        a.setSpaceTime(new PlanningSpaceTime(new PlanningRoom(room), new PlanningTime(dateTime, hourIndex, dayIndex)));
        return a;
    }

    public String getCode() {
        return code;
    }

    public String getRoom() {
        return room;
    }

    public String getStudent() {
        return student;
    }

    public String getSupervisors() {
        return supervisors;
    }

    public String getChair() {
        return chair;
    }

    public String getExaminer() {
        return examiner;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public void setHourIndex(int hourIndex) {
        this.hourIndex = hourIndex;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public void setSupervisors(String supervisors) {
        this.supervisors = supervisors;
    }

    public void setChair(String chair) {
        this.chair = chair;
    }

    public void setExaminer(String examiner) {
        this.examiner = examiner;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public int getHourIndex() {
        return hourIndex;
    }

}
