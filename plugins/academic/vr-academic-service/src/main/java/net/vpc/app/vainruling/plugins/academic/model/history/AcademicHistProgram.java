/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.history;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.config.*;

/**
 * filiere
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/History")
public class AcademicHistProgram {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private String name;
    private String name2;

    @Summary
    private AppPeriod academicYear;

    @Summary
    private AppDepartment department;

    public AcademicHistProgram() {
    }

    public AcademicHistProgram(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public AppPeriod getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AppPeriod academicYear) {
        this.academicYear = academicYear;
    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    @Override
    public String toString() {
        return StringUtils.nonNull(name);
    }

}
