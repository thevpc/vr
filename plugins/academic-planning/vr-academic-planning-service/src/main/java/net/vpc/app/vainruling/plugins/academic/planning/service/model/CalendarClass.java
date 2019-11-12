/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.planning.service.model;

import net.vpc.app.vainruling.plugins.academic.model.current.AcademicClass;

/**
 *
 * @author vpc
 */
public class CalendarClass {

    private String academicClassName;
    private AcademicClass academicClass;

    public CalendarClass() {
    }

    public CalendarClass(String academicClassName, AcademicClass academicClass) {
        this.academicClassName = academicClassName;
        this.academicClass = academicClass;
    }

    public boolean isMissingEntity() {
        return academicClass == null;
    }

    public boolean isMissingPlanning() {
        return academicClassName == null;
    }

    public String getName() {
        if (academicClass != null) {
            return academicClass.getName();
        }
        if (academicClassName != null) {
            return academicClassName;
        }
        return null;
    }
    

    public String getAcademicClassName() {
        return academicClassName;
    }

    public void setAcademicClassName(String academicClassName) {
        this.academicClassName = academicClassName;
    }

    public AcademicClass getAcademicClass() {
        return academicClass;
    }

    public void setAcademicClass(AcademicClass academicClass) {
        this.academicClass = academicClass;
    }

}
