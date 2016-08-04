package net.vpc.app.vr.wpm.plugins.payroll.service.model;

import net.vpc.upa.config.*;
import net.vpc.upa.types.Date;

/**
 * Created by vpc on 6/13/16.
 */
@Entity
@Path("/Payroll")
public class EmployeeSalaryPeriod {
    @Id
    @Sequence
    private int id;
    @Main
    @Unique
    private String name;
    @Summary
    private Date periodDate;

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

    public Date getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(Date periodDate) {
        this.periodDate = periodDate;
    }
}
