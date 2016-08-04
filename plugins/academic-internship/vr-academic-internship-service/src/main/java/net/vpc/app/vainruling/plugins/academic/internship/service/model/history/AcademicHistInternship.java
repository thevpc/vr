/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service.model.history;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipStatus;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipType;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgram;
import net.vpc.upa.config.*;
import net.vpc.upa.types.Date;
import net.vpc.upa.types.DateTime;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Education/History")
public class AcademicHistInternship {

    @Id
    @Sequence

    private int id;
    @Main
    private String name;

    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;
    private String technologies;
    @Field(max = "max")
    private String mainDiscipline;
    @Field(max = "max")
    private String student;
    private String secondStudent;
    @Field(max = "max")
    private String supervisorName;
    private String secondSupervisorName;
    @Field(max = "max")
    private String companyName;
    private String companyMentorName;
    private String companyMentorEmail;
    private String companyMentorSecondEmail;
    private String companyMentorPhone;
    private String companyMentorSecondPhone;
    private String companyMentorOffice;
    private AppDepartment department;
    private AcademicProgram program;
    private AcademicInternshipType internshipType;
    private AcademicInternshipStatus internshipStatus;
    private String duration;
    private Date startDate;
    private Date endDate;
    @Summary
    @Field(max = "4000")
    private String firstExaminerName;
    private String secondExaminerName;
    @Summary
    @Field(max = "4000")
    private String chairExaminerName;
    private AppPeriod academicYear;
    private DateTime examDate;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public String getMainDiscipline() {
        return mainDiscipline;
    }

    public void setMainDiscipline(String mainDiscipline) {
        this.mainDiscipline = mainDiscipline;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getSecondStudent() {
        return secondStudent;
    }

    public void setSecondStudent(String secondStudent) {
        this.secondStudent = secondStudent;
    }

    public String getCompanyMentorName() {
        return companyMentorName;
    }

    public void setCompanyMentorName(String companyMentorName) {
        this.companyMentorName = companyMentorName;
    }

    public String getCompanyMentorEmail() {
        return companyMentorEmail;
    }

    public void setCompanyMentorEmail(String companyMentorEmail) {
        this.companyMentorEmail = companyMentorEmail;
    }

    public String getCompanyMentorSecondEmail() {
        return companyMentorSecondEmail;
    }

    public void setCompanyMentorSecondEmail(String companyMentorSecondEmail) {
        this.companyMentorSecondEmail = companyMentorSecondEmail;
    }

    public String getCompanyMentorPhone() {
        return companyMentorPhone;
    }

    public void setCompanyMentorPhone(String companyMentorPhone) {
        this.companyMentorPhone = companyMentorPhone;
    }

    public String getCompanyMentorSecondPhone() {
        return companyMentorSecondPhone;
    }

    public void setCompanyMentorSecondPhone(String companyMentorSecondPhone) {
        this.companyMentorSecondPhone = companyMentorSecondPhone;
    }

    public String getCompanyMentorOffice() {
        return companyMentorOffice;
    }

    public void setCompanyMentorOffice(String companyMentorOffice) {
        this.companyMentorOffice = companyMentorOffice;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public AppPeriod getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AppPeriod academicYear) {
        this.academicYear = academicYear;
    }

    public DateTime getExamDate() {
        return examDate;
    }

    public void setExamDate(DateTime examDate) {
        this.examDate = examDate;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getSecondSupervisorName() {
        return secondSupervisorName;
    }

    public void setSecondSupervisorName(String secondSupervisorName) {
        this.secondSupervisorName = secondSupervisorName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFirstExaminerName() {
        return firstExaminerName;
    }

    public void setFirstExaminerName(String firstExaminerName) {
        this.firstExaminerName = firstExaminerName;
    }

    public String getSecondExaminerName() {
        return secondExaminerName;
    }

    public void setSecondExaminerName(String secondExaminerName) {
        this.secondExaminerName = secondExaminerName;
    }

    public String getChairExaminerName() {
        return chairExaminerName;
    }

    public void setChairExaminerName(String chairExaminerName) {
        this.chairExaminerName = chairExaminerName;
    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

    public AcademicProgram getProgram() {
        return program;
    }

    public void setProgram(AcademicProgram program) {
        this.program = program;
    }

    public AcademicInternshipType getInternshipType() {
        return internshipType;
    }

    public void setInternshipType(AcademicInternshipType internshipType) {
        this.internshipType = internshipType;
    }

    public AcademicInternshipStatus getInternshipStatus() {
        return internshipStatus;
    }

    public void setInternshipStatus(AcademicInternshipStatus internshipStatus) {
        this.internshipStatus = internshipStatus;
    }

}
