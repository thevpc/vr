/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.stat;

import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;

/**
 * @author taha.bensalah@gmail.com
 */
public class ModuleSemesterStat {

    private AcademicSemester semester;
    private LoadValue value = new LoadValue();
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
