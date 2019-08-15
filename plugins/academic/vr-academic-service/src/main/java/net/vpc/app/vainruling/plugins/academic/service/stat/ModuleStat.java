/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.stat;

import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;

import java.util.NoSuchElementException;

/**
 * @author taha.bensalah@gmail.com
 */
public class ModuleStat {

    private AcademicCourseAssignment module;
    private LoadValue value = new LoadValue();
    private double valueEffWeek;

    private ModuleSemesterStat[] semesters;

    public AcademicCourseAssignment getModule() {
        return module;
    }

    public void setModule(AcademicCourseAssignment module) {
        this.module = module;
    }

    public ModuleSemesterStat getSemester(String name) {
        for (ModuleSemesterStat mss : getSemesters()) {
//            if (mss == null || mss.getSemester() == null || mss.getSemester().getName() == null || name == null) {
//                System.out.println("?");
//            }
            if (mss.getSemester().getName().equals(name)) {
                return mss;
            }
        }
        throw new NoSuchElementException("semester " + name + " not found");
    }

    public ModuleSemesterStat[] getSemesters() {
        return semesters;
    }

    public void setSemesters(ModuleSemesterStat[] semesters) {
        this.semesters = semesters;
    }

    public double getValueEffWeek() {
        return valueEffWeek;
    }

    public void setValueEffWeek(double valueEffWeek) {
        this.valueEffWeek = valueEffWeek;
    }

    public LoadValue getValue() {
        return value;
    }

}
