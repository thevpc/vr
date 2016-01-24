/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service.model.current;

import java.sql.Timestamp;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.api.model.AppCompany;
import net.vpc.app.vainruling.api.model.AppContact;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipDuration;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipStatus;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipVariant;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;
//import net.vpc.upa.types.DateTime;

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

    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicInternshipBoard board;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    private String code;

    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;

    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = "textarea")
    @Field(max = "4000")
    private String description;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.DISCIPLINE))
    private String mainDiscipline;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicInternshipStatus internshipStatus;

    @Property(name = UIConstants.FIELD_FORM_SPAN, value = "MAX_VALUE")
    private String technologies;

    private AcademicInternshipVariant internshipVariant;

    @Properties({
        @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Details"),}
    )
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AcademicStudent student;
    private AcademicStudent secondStudent;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AcademicTeacher supervisor;
    private AcademicTeacher secondSupervisor;
    @Field(max = "4000", modifiers = UserFieldModifier.SUMMARY)
    private AppCompany company;
    private String companyOther;
    private AppContact companyMentor;
    private String companyMentorOther;
    private String companyMentorOtherEmail;
    private String companyMentorOtherPhone;
    private AcademicInternshipDuration duration;
    private java.util.Date startDate;
    private java.util.Date endDate;
    @Properties({
        @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Evaluation"),
        @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.FILE),
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
    @Field(max = "4000")
    private AcademicTeacher firstExaminer;
    private AcademicTeacher secondExaminer;
    @Field(max = "4000")
    private AcademicTeacher chairExaminer;
    private Timestamp examDate;

    @Properties({
        @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Trace")}
    )
    private Timestamp lastStudentUpdateTime;
    private Timestamp lastTeacherUpdateTime;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Timestamp lastUpdateTime;

    @Properties({
        @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Observations")}
    )
    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = "textarea")
    @Field(max = "512")
    private String studentObservations;
    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = "textarea")
    @Field(max = "512")
    private String validationObservations;
    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = "textarea")
    @Field(max = "512")
    private String evaluationObservations;

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

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
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

    public Timestamp getExamDate() {
        return examDate;
    }

    public void setExamDate(Timestamp examDate) {
        this.examDate = examDate;
    }

    public AcademicInternshipStatus getInternshipStatus() {
        return internshipStatus;
    }

    public void setInternshipStatus(AcademicInternshipStatus internshipStatus) {
        this.internshipStatus = internshipStatus;
    }

    public AcademicInternshipVariant getInternshipVariant() {
        return internshipVariant;
    }

    public void setInternshipVariant(AcademicInternshipVariant internshipVariant) {
        this.internshipVariant = internshipVariant;
    }

    public String getCompanyOther() {
        return companyOther;
    }

    public void setCompanyOther(String companyOther) {
        this.companyOther = companyOther;
    }

    public String getCompanyMentorOther() {
        return companyMentorOther;
    }

    public void setCompanyMentorOther(String companyMentorOther) {
        this.companyMentorOther = companyMentorOther;
    }

    public String getCompanyMentorOtherEmail() {
        return companyMentorOtherEmail;
    }

    public void setCompanyMentorOtherEmail(String companyMentorOtherEmail) {
        this.companyMentorOtherEmail = companyMentorOtherEmail;
    }

    public String getCompanyMentorOtherPhone() {
        return companyMentorOtherPhone;
    }

    public void setCompanyMentorOtherPhone(String companyMentorOtherPhone) {
        this.companyMentorOtherPhone = companyMentorOtherPhone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValidationObservations() {
        return validationObservations;
    }

    public void setValidationObservations(String validationObservations) {
        this.validationObservations = validationObservations;
    }

    public String getEvaluationObservations() {
        return evaluationObservations;
    }

    public void setEvaluationObservations(String evaluationObservations) {
        this.evaluationObservations = evaluationObservations;
    }

    public String getStudentObservations() {
        return studentObservations;
    }

    public void setStudentObservations(String studentObservations) {
        this.studentObservations = studentObservations;
    }

    public AcademicInternshipBoard getBoard() {
        return board;
    }

    public void setBoard(AcademicInternshipBoard board) {
        this.board = board;
    }

    public AcademicInternshipDuration getDuration() {
        return duration;
    }

    public void setDuration(AcademicInternshipDuration duration) {
        this.duration = duration;
    }

    public Timestamp getLastStudentUpdateTime() {
        return lastStudentUpdateTime;
    }

    public void setLastStudentUpdateTime(Timestamp lastStudentUpdateTime) {
        this.lastStudentUpdateTime = lastStudentUpdateTime;
    }

    public Timestamp getLastTeacherUpdateTime() {
        return lastTeacherUpdateTime;
    }

    public void setLastTeacherUpdateTime(Timestamp lastTeacherUpdateTime) {
        this.lastTeacherUpdateTime = lastTeacherUpdateTime;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

}
