package net.vpc.app.vr.wpm.plugins.payroll.service.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 6/13/16.
 */
@Entity
@Path("/Payroll")
public class EmployeeSalary {
    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Employee employee;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private EmployeeSalaryPeriod period;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double baseSalary;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double hAbs;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double hSup75;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double hSup100;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double grossWithdrawal;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double advancePayment;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double deductionMisc;

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

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public double gethAbs() {
        return hAbs;
    }

    public void sethAbs(double hAbs) {
        this.hAbs = hAbs;
    }

    public double gethSup75() {
        return hSup75;
    }

    public void sethSup75(double hSup75) {
        this.hSup75 = hSup75;
    }

    public double gethSup100() {
        return hSup100;
    }

    public void sethSup100(double hSup100) {
        this.hSup100 = hSup100;
    }

    public double getGrossWithdrawal() {
        return grossWithdrawal;
    }

    public void setGrossWithdrawal(double grossWithdrawal) {
        this.grossWithdrawal = grossWithdrawal;
    }

    public double getAdvancePayment() {
        return advancePayment;
    }

    public void setAdvancePayment(double advancePayment) {
        this.advancePayment = advancePayment;
    }

    public double getDeductionMisc() {
        return deductionMisc;
    }

    public void setDeductionMisc(double deductionMisc) {
        this.deductionMisc = deductionMisc;
    }
}
