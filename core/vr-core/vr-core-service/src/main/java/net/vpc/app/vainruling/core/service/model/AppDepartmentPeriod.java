/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.period.name desc, department.name asc")
@Path("Admin/Config")
public class AppDepartmentPeriod {

    @Path("Main")
    @Id
    @Sequence
    private int id;


    @Summary
    private AppPeriod period;

    @Summary
    private AppDepartment department;

    public AppDepartmentPeriod() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppDepartmentPeriod that = (AppDepartmentPeriod) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
