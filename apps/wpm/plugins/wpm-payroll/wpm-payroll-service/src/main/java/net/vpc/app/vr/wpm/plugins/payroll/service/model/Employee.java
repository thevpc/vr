package net.vpc.app.vr.wpm.plugins.payroll.service.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;
import net.vpc.upa.types.Date;

/**
 * Created by vpc on 6/13/16.
 */
@Entity
@Path("/Payroll")
public class Employee {
    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private String firstName;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private String lastName;
    private String nin;
    private boolean securityAffiliationEnabled;
    private String securityAffiliationNumber;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private EmployeeFunction employeeFunction;
    private Date birthDate;
    private Date recruitmentDate;
    private AppFamilySituation familySituation;
    private int numberOfChildren;
    private int numberOfDisabledChildren;
    private String numberOfChildren25;
    private boolean dependentRelative;
    private boolean familyCare;
    private boolean groupInsurance;
    private EmployeeCategory category;
    private EmployeeGrade grade;
    private double baseSalary;
    private EmployeeSalaryType salaryType;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private boolean enabled;
    private AppFinancialInstitution financialInstitution;
    private String financialAccount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public boolean isSecurityAffiliationEnabled() {
        return securityAffiliationEnabled;
    }

    public void setSecurityAffiliationEnabled(boolean securityAffiliationEnabled) {
        this.securityAffiliationEnabled = securityAffiliationEnabled;
    }

    public String getSecurityAffiliationNumber() {
        return securityAffiliationNumber;
    }

    public void setSecurityAffiliationNumber(String securityAffiliationNumber) {
        this.securityAffiliationNumber = securityAffiliationNumber;
    }

    public EmployeeFunction getEmployeeFunction() {
        return employeeFunction;
    }

    public void setEmployeeFunction(EmployeeFunction employeeFunction) {
        this.employeeFunction = employeeFunction;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getRecruitmentDate() {
        return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
        this.recruitmentDate = recruitmentDate;
    }

    public AppFamilySituation getFamilySituation() {
        return familySituation;
    }

    public void setFamilySituation(AppFamilySituation familySituation) {
        this.familySituation = familySituation;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public int getNumberOfDisabledChildren() {
        return numberOfDisabledChildren;
    }

    public void setNumberOfDisabledChildren(int numberOfDisabledChildren) {
        this.numberOfDisabledChildren = numberOfDisabledChildren;
    }

    public String getNumberOfChildren25() {
        return numberOfChildren25;
    }

    public void setNumberOfChildren25(String numberOfChildren25) {
        this.numberOfChildren25 = numberOfChildren25;
    }

    public boolean isDependentRelative() {
        return dependentRelative;
    }

    public void setDependentRelative(boolean dependentRelative) {
        this.dependentRelative = dependentRelative;
    }

    public boolean isFamilyCare() {
        return familyCare;
    }

    public void setFamilyCare(boolean familyCare) {
        this.familyCare = familyCare;
    }

    public boolean isGroupInsurance() {
        return groupInsurance;
    }

    public void setGroupInsurance(boolean groupInsurance) {
        this.groupInsurance = groupInsurance;
    }

    public EmployeeCategory getCategory() {
        return category;
    }

    public void setCategory(EmployeeCategory category) {
        this.category = category;
    }

    public EmployeeGrade getGrade() {
        return grade;
    }

    public void setGrade(EmployeeGrade grade) {
        this.grade = grade;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public EmployeeSalaryType getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(EmployeeSalaryType salaryType) {
        this.salaryType = salaryType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AppFinancialInstitution getFinancialInstitution() {
        return financialInstitution;
    }

    public void setFinancialInstitution(AppFinancialInstitution financialInstitution) {
        this.financialInstitution = financialInstitution;
    }

    public String getFinancialAccount() {
        return financialAccount;
    }

    public void setFinancialAccount(String financialAccount) {
        this.financialAccount = financialAccount;
    }
}
