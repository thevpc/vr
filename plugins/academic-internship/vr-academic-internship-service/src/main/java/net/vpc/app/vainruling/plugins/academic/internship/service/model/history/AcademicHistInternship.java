/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service.model.history;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.api.model.AppPeriod;
import net.vpc.app.vainruling.api.model.AppCompany;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.types.Date;
import net.vpc.upa.types.DateTime;
import net.vpc.upa.vfs.FileContent;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Education/History")
public class AcademicHistInternship {

    @Id
    @Sequence

    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;

    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = "textarea")
    private String description;
    private String technologies;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private String mainDiscipline;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AcademicStudent student;
    private AcademicStudent secondStudent;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AcademicTeacher supervisor;
    private AcademicTeacher secondSupervisor;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AppCompany company;
    private String companyMentorName;
    private String companyMentorEmail;
    private String companyMentorSecondEmail;
    private String companyMentorPhone;
    private String companyMentorSecondPhone;
    private String companyMentorOffice;
    private String duration;
    private Date startDate;
    private Date endDate;
    private FileContent specFile;
    private FileContent midTermReportFile;
    private FileContent reportFile;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AcademicTeacher firstExaminer;
    private AcademicTeacher secondExaminer;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AcademicTeacher chairExaminer;
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

    public AcademicStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicStudent student) {
        this.student = student;
    }

    public AppCompany getCompany() {
        return company;
    }

    public void setCompany(AppCompany company) {
        this.company = company;
    }

    public FileContent getSpecFile() {
        return specFile;
    }

    public void setSpecFile(FileContent specFile) {
        this.specFile = specFile;
    }

    public FileContent getMidTermReportFile() {
        return midTermReportFile;
    }

    public void setMidTermReportFile(FileContent midTermReportFile) {
        this.midTermReportFile = midTermReportFile;
    }

    public FileContent getReportFile() {
        return reportFile;
    }

    public void setReportFile(FileContent reportFile) {
        this.reportFile = reportFile;
    }

    public AcademicStudent getSecondStudent() {
        return secondStudent;
    }

    public void setSecondStudent(AcademicStudent secondStudent) {
        this.secondStudent = secondStudent;
    }

    public AcademicTeacher getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(AcademicTeacher supervisor) {
        this.supervisor = supervisor;
    }

    public AcademicTeacher getSecondSupervisor() {
        return secondSupervisor;
    }

    public void setSecondSupervisor(AcademicTeacher secondSupervisor) {
        this.secondSupervisor = secondSupervisor;
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

    public AcademicTeacher getFirstExaminer() {
        return firstExaminer;
    }

    public void setFirstExaminer(AcademicTeacher firstExaminer) {
        this.firstExaminer = firstExaminer;
    }

    public AcademicTeacher getSecondExaminer() {
        return secondExaminer;
    }

    public void setSecondExaminer(AcademicTeacher secondExaminer) {
        this.secondExaminer = secondExaminer;
    }

    public AcademicTeacher getChairExaminer() {
        return chairExaminer;
    }

    public void setChairExaminer(AcademicTeacher chairExaminer) {
        this.chairExaminer = chairExaminer;
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

}
