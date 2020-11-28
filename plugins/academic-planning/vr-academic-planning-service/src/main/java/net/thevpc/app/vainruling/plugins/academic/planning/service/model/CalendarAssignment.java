/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.planning.service.model;

import java.util.Set;
import java.util.TreeSet;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseType;
import net.thevpc.app.vainruling.core.service.VrLabel;

/**
 *
 * @author vpc
 */
public class CalendarAssignment implements Cloneable {

    private CalendarClass academicClass;
    private CalendarCoursePlan coursePlan;
    private CalendarTeacher teacher;
    private AcademicCourseAssignment assignment;
    private AcademicCourseType courseType;
    private Set<VrLabel> labels = new TreeSet<>();
    private double grp;
    private double w;
    private double c;
    private double td;
    private double tp;

    public AcademicCourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(AcademicCourseType courseType) {
        this.courseType = courseType;
    }

    public Set<VrLabel> getLabels() {
        return labels;
    }

    public void setLabels(Set<VrLabel> labels) {
        this.labels = labels;
    }

    public boolean isWithLabels() {
        return labels.size() > 0;
    }

    public CalendarClass getAcademicClass() {
        return academicClass;
    }

    public void setAcademicClass(CalendarClass academicClass) {
        this.academicClass = academicClass;
    }

    public AcademicCourseAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(AcademicCourseAssignment assignment) {
        this.assignment = assignment;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getTd() {
        return td;
    }

    public void setTd(double td) {
        this.td = td;
    }

    public double getTp() {
        return tp;
    }

    public void setTp(double tp) {
        this.tp = tp;
    }

    public CalendarCoursePlan getCoursePlan() {
        return coursePlan;
    }

    public void setCoursePlan(CalendarCoursePlan coursePlan) {
        this.coursePlan = coursePlan;
    }

    public CalendarTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(CalendarTeacher teacher) {
        this.teacher = teacher;
    }

    public double getGrp() {
        return grp;
    }

    public void setGrp(double grp) {
        this.grp = grp;
    }

    public CalendarAssignment copy() {
        try {
            CalendarAssignment t = (CalendarAssignment) super.clone();
            t.labels = new TreeSet<>(t.labels);
            return t;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
