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
public class ModuleSemesterStat {

    private AcademicSemester semester;
    private LoadValue value=new LoadValue();
    private double valueEffWeek;

    public AcademicSemester getSemester() {
        return semester;
    }

    public void setSemester(AcademicSemester semester) {
        this.semester = semester;
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
