/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service.model.current;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.api.model.AppCompany;
import net.vpc.app.vainruling.api.model.AppContact;
import net.vpc.app.vainruling.api.model.AppDepartment;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipStatus;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipType;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgram;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.types.Date;
import net.vpc.upa.types.DateTime;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Education/Internship")
public class AcademicInternship {

    @Id
    @Sequence

    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;

    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = "textarea")
    private String description;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicInternshipType internshipType;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicInternshipStatus internshipStatus;
    private String technologies;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private String mainDiscipline;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AcademicStudent student;
    private AcademicStudent secondStudent;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AcademicTeacher supervisor;
    private AcademicTeacher secondSupervisor;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AppCompany company;
    private AppContact companyMentor;
    private AppDepartment department;
    private AcademicProgram program;
    private String duration;
    private Date startDate;
    private Date endDate;
    @Properties({
        @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Evaluation")
        ,@Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.FILE),
        @Property(name = UIConstants.FIELD_FORM_SPAN, value = "MAX_VALUE")
    }
    )
    private String specFilePath;
    @Properties({
        @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.FILE),
        @Property(name = UIConstants.FIELD_FORM_SPAN, value = "MAX_VALUE")
    }
    )
    private String midTermReportFilePath;
    @Properties({
        @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.FILE),
        @Property(name = UIConstants.FIELD_FORM_SPAN, value = "MAX_VALUE")
    }
    )
    private String reportFilePath;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AcademicTeacher firstExaminer;
    private AcademicTeacher secondExaminer;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AcademicTeacher chairExaminer;
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

    public String getSpecFilePath() {
        return specFilePath;
    }

    public void setSpecFilePath(String specFilePath) {
        this.specFilePath = specFilePath;
    }

    public String getMidTermReportFilePath() {
        return midTermReportFilePath;
    }

    public void setMidTermReportFilePath(String midTermReportFilePath) {
        this.midTermReportFilePath = midTermReportFilePath;
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
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

    public AppContact getCompanyMentor() {
        return companyMentor;
    }

    public void setCompanyMentor(AppContact companyMentor) {
        this.companyMentor = companyMentor;
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

    public DateTime getExamDate() {
        return examDate;
    }

    public void setExamDate(DateTime examDate) {
        this.examDate = examDate;
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
