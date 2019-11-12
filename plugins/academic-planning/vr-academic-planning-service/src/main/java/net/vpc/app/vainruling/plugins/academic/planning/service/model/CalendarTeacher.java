/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.planning.service.model;

import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

/**
 *
 * @author vpc
 */
public class CalendarTeacher {

    private String teacherName;
    private AcademicTeacher teacher;

    public CalendarTeacher() {
    }

    public CalendarTeacher(String teacherName, AcademicTeacher teacher) {
        this.teacherName = teacherName;
        this.teacher = teacher;
    }

    public boolean isMissingEntity() {
        return teacher == null;
    }

    public boolean isMissingPlanning() {
        return teacherName == null;
    }

    public String getName() {
        if (teacher != null) {
            return teacher.getUser().getFullTitle();
        }
        if (teacherName != null) {
            return teacherName;
        }
        return null;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

}
