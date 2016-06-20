package net.vpc.app.vr.wpm.plugins.payroll.service.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;
import net.vpc.upa.types.Date;

/**
 * Created by vpc on 6/13/16.
 */
@Entity
@Path("/Payroll")
public class EmployeeLoan {
    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Date opDate;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Employee employee;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private EmployeeSalaryPeriod period;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double loadValueAdvance;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double loadValueLending;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double loadValueOpposition;

    private double monthRefundValueAdvance;
    private double monthRefundValueLending;
    private double monthRefundValueOpposition;

    private double sumRefundValueAdvance;
    private double sumRefundValueLending;
    private double sumRefundValueOpposition;


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

    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }

    public double getLoadValueAdvance() {
        return loadValueAdvance;
    }

    public void setLoadValueAdvance(double loadValueAdvance) {
        this.loadValueAdvance = loadValueAdvance;
    }

    public double getLoadValueLending() {
        return loadValueLending;
    }

    public void setLoadValueLending(double loadValueLending) {
        this.loadValueLending = loadValueLending;
    }

    public double getLoadValueOpposition() {
        return loadValueOpposition;
    }

    public void setLoadValueOpposition(double loadValueOpposition) {
        this.loadValueOpposition = loadValueOpposition;
    }

    public double getMonthRefundValueAdvance() {
        return monthRefundValueAdvance;
    }

    public void setMonthRefundValueAdvance(double monthRefundValueAdvance) {
        this.monthRefundValueAdvance = monthRefundValueAdvance;
    }

    public double getMonthRefundValueLending() {
        return monthRefundValueLending;
    }

    public void setMonthRefundValueLending(double monthRefundValueLending) {
        this.monthRefundValueLending = monthRefundValueLending;
    }

    public double getMonthRefundValueOpposition() {
        return monthRefundValueOpposition;
    }

    public void setMonthRefundValueOpposition(double monthRefundValueOpposition) {
        this.monthRefundValueOpposition = monthRefundValueOpposition;
    }

    public double getSumRefundValueAdvance() {
        return sumRefundValueAdvance;
    }

    public void setSumRefundValueAdvance(double sumRefundValueAdvance) {
        this.sumRefundValueAdvance = sumRefundValueAdvance;
    }

    public double getSumRefundValueLending() {
        return sumRefundValueLending;
    }

    public void setSumRefundValueLending(double sumRefundValueLending) {
        this.sumRefundValueLending = sumRefundValueLending;
    }

    public double getSumRefundValueOpposition() {
        return sumRefundValueOpposition;
    }

    public void setSumRefundValueOpposition(double sumRefundValueOpposition) {
        this.sumRefundValueOpposition = sumRefundValueOpposition;
    }
}
