package net.vpc.app.vr.wpm.plugins.payroll.service.model;

import net.vpc.upa.config.*;

/**
 * Created by vpc on 6/13/16.
 */
@Entity
@Path("/Payroll")
public class EmployeeSalaryBonus {
    @Id
    @Sequence
    private int id;
    @Summary
    private Employee employee;
    @Summary
    private EmployeeSalaryPeriod period;
    @Summary
    private EmployeeSalaryBonusType bonusType;
    @Summary
    private double value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public EmployeeSalaryPeriod getPeriod() {
        return period;
    }

    public void setPeriod(EmployeeSalaryPeriod period) {
        this.period = period;
    }

    public EmployeeSalaryBonusType getBonusType() {
        return bonusType;
    }

    public void setBonusType(EmployeeSalaryBonusType bonusType) {
        this.bonusType = bonusType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
