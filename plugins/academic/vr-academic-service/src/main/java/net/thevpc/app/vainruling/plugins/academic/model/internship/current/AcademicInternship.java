/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.internship.current;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipDuration;
import net.thevpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipStatus;
import net.thevpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipVariant;
import net.thevpc.app.vainruling.core.service.model.AppCompany;
import net.thevpc.app.vainruling.core.service.model.AppContact;
import net.thevpc.app.vainruling.core.service.model.OpinionType;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.config.*;

import java.sql.Timestamp;
//import net.thevpc.upa.types.DateTime;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/Projects/Internships")
@Properties(
        {
                @Property(name = "ui.auto-filter.period", value = "{expr='this.board.period',order=1}"),
                @Property(name = "ui.auto-filter.department", value = "{expr='this.board.department',order=2}"),
                @Property(name = "ui.auto-filter.program", value = "{expr='this.board.program',order=3}"),
                @Property(name = "ui.auto-filter.academicClass", value = "{expr='this.board.academicClass',order=4}"),
                @Property(name = "ui.auto-filter.internshipType", value = "{expr='this.board.internshipType',order=5}"),
                @Property(name = "ui.auto-filter.internshipStatus", value = "{expr='this.internshipStatus',order=6}"),
                @Property(name = "ui.auto-filter.sessionType", value = "{expr='this.sessionType',order=7}"),
        }
)
public class AcademicInternship {

    @Path("Main")
    @Id
    @Sequence

    private int id;

    @Summary
    private AcademicInternshipGroup mainGroup;

    @Summary
    private AcademicInternshipBoard board;

    @Summary
    private String code;

    @Main
    private String name;

    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "4000")
    private String description;

    @Properties(@Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.DISCIPLINE))
    private String mainDiscipline;
    @Summary
    private AcademicInternshipStatus internshipStatus;

    @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    private String technologies;

    private AcademicInternshipVariant internshipVariant;

    @Path("Details")
//    @Properties({
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Details"),}
//    )
    @Summary
    @Field(max = "4000")
    private AcademicStudent student;

    private AcademicStudent secondStudent;

    @Summary
    @Field(max = "4000")
    private AcademicTeacher supervisor;
    private AcademicTeacher secondSupervisor;
    private AcademicTeacher recordedSupervisor;
    private AcademicTeacher recordedSecondSupervisor;

    @Summary
    @Field(max = "4000")
    private AppCompany company;

    @Summary
    private String companyOther;
    private AppContact companyMentor;
    private String companyMentorOther;
    private String companyMentorOtherEmail;
    private String companyMentorOtherPhone;
    private AcademicInternshipDuration duration;
    private java.util.Date startDate;
    private java.util.Date endDate;

    @Path("Evaluation")
    @Properties({
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Evaluation"),
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    private String specFilePath;
    @Properties({
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    private String midTermReportFilePath;
    private OpinionType midTermStudentIntMentoringOpinion;
    private OpinionType midTermStudentExtMentoringOpinion;
    private OpinionType midTermStudentMeetingOpinion;
    private OpinionType midTermStudentExtMeetingOpinion;
    private int midTermStudentPhysMeetingCount;
    private int midTermStudentVoiceMeetingCount;
    private int midTermStudentTextMeetingCount;
    private int midTermStudentPhysMeetingExtCount;
    private int midTermStudentVoiceMeetingExtCount;
    private int midTermStudentTextMeetingExtCount;
    private int midTermStudentProgress;

    @Summary
    @Field(max = "400")
    private String midTermStudentObs;

    private OpinionType midTermTeacherExtMentoringOpinion;
    private OpinionType midTermTeacherMeetingOpinion;
    private OpinionType midTermTeacherTechWorkOpinion;
    private OpinionType midTermTeacherSciWorkOpinion;
    private OpinionType midTermTeacherCommWorkOpinion;
    private OpinionType midTermTeacherAutonomyOpinion;
    private int midTermTeacherProgress;

    @Summary
    @Field(max = "400")
    private String midTermTeacherObs;

    @Properties({
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    private String reportFilePath;

    @Path("Defense")
    @Field(max = "4000")
    private AcademicTeacher firstExaminer;
    private AcademicTeacher secondExaminer;
    @Field(max = "4000")
    private AcademicTeacher chairExaminer;
    private Timestamp examDate;
    private String examLocation;
    private AcademicInternshipSessionType sessionType;
    private boolean preEmployment;


    @Path("Observations")
//    @Properties({
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Observations")}
//    )
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "512")
    private String studentObservations;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "512")
    private String validationObservations;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "512")
    private String evaluationObservations;

    @Path("Trace")
//    @Properties({
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace")}
//    )
    private Timestamp lastStudentUpdateTime;
    private Timestamp lastTeacherUpdateTime;
    @Summary
    private Timestamp lastUpdateTime;

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

    public String getExamLocation() {
        return examLocation;
    }

    public void setExamLocation(String examLocation) {
        this.examLocation = examLocation;
    }

    public OpinionType getMidTermStudentIntMentoringOpinion() {
        return midTermStudentIntMentoringOpinion;
    }

    public void setMidTermStudentIntMentoringOpinion(OpinionType midTermStudentIntMentoringOpinion) {
        this.midTermStudentIntMentoringOpinion = midTermStudentIntMentoringOpinion;
    }

    public OpinionType getMidTermStudentExtMentoringOpinion() {
        return midTermStudentExtMentoringOpinion;
    }

    public void setMidTermStudentExtMentoringOpinion(OpinionType midTermStudentExtMentoringOpinion) {
        this.midTermStudentExtMentoringOpinion = midTermStudentExtMentoringOpinion;
    }

    public OpinionType getMidTermStudentMeetingOpinion() {
        return midTermStudentMeetingOpinion;
    }

    public void setMidTermStudentMeetingOpinion(OpinionType midTermStudentMeetingOpinion) {
        this.midTermStudentMeetingOpinion = midTermStudentMeetingOpinion;
    }

    public int getMidTermStudentPhysMeetingCount() {
        return midTermStudentPhysMeetingCount;
    }

    public void setMidTermStudentPhysMeetingCount(int midTermStudentPhysMeetingCount) {
        this.midTermStudentPhysMeetingCount = midTermStudentPhysMeetingCount;
    }

    public int getMidTermStudentVoiceMeetingCount() {
        return midTermStudentVoiceMeetingCount;
    }

    public void setMidTermStudentVoiceMeetingCount(int midTermStudentVoiceMeetingCount) {
        this.midTermStudentVoiceMeetingCount = midTermStudentVoiceMeetingCount;
    }

    public int getMidTermStudentTextMeetingCount() {
        return midTermStudentTextMeetingCount;
    }

    public void setMidTermStudentTextMeetingCount(int midTermStudentTextMeetingCount) {
        this.midTermStudentTextMeetingCount = midTermStudentTextMeetingCount;
    }

    public String getMidTermStudentObs() {
        return midTermStudentObs;
    }

    public void setMidTermStudentObs(String midTermStudentObs) {
        this.midTermStudentObs = midTermStudentObs;
    }

    public OpinionType getMidTermTeacherExtMentoringOpinion() {
        return midTermTeacherExtMentoringOpinion;
    }

    public void setMidTermTeacherExtMentoringOpinion(OpinionType midTermTeacherExtMentoringOpinion) {
        this.midTermTeacherExtMentoringOpinion = midTermTeacherExtMentoringOpinion;
    }

    public OpinionType getMidTermTeacherMeetingOpinion() {
        return midTermTeacherMeetingOpinion;
    }

    public void setMidTermTeacherMeetingOpinion(OpinionType midTermTeacherMeetingOpinion) {
        this.midTermTeacherMeetingOpinion = midTermTeacherMeetingOpinion;
    }

    public OpinionType getMidTermTeacherTechWorkOpinion() {
        return midTermTeacherTechWorkOpinion;
    }

    public void setMidTermTeacherTechWorkOpinion(OpinionType midTermTeacherTechWorkOpinion) {
        this.midTermTeacherTechWorkOpinion = midTermTeacherTechWorkOpinion;
    }

    public OpinionType getMidTermTeacherSciWorkOpinion() {
        return midTermTeacherSciWorkOpinion;
    }

    public void setMidTermTeacherSciWorkOpinion(OpinionType midTermTeacherSciWorkOpinion) {
        this.midTermTeacherSciWorkOpinion = midTermTeacherSciWorkOpinion;
    }

    public OpinionType getMidTermTeacherCommWorkOpinion() {
        return midTermTeacherCommWorkOpinion;
    }

    public void setMidTermTeacherCommWorkOpinion(OpinionType midTermTeacherCommWorkOpinion) {
        this.midTermTeacherCommWorkOpinion = midTermTeacherCommWorkOpinion;
    }

    public OpinionType getMidTermTeacherAutonomyOpinion() {
        return midTermTeacherAutonomyOpinion;
    }

    public void setMidTermTeacherAutonomyOpinion(OpinionType midTermTeacherAutonomyOpinion) {
        this.midTermTeacherAutonomyOpinion = midTermTeacherAutonomyOpinion;
    }

    public String getMidTermTeacherObs() {
        return midTermTeacherObs;
    }

    public void setMidTermTeacherObs(String midTermTeacherObs) {
        this.midTermTeacherObs = midTermTeacherObs;
    }

    public int getMidTermStudentProgress() {
        return midTermStudentProgress;
    }

    public void setMidTermStudentProgress(int midTermStudentProgress) {
        this.midTermStudentProgress = midTermStudentProgress;
    }

    public int getMidTermTeacherProgress() {
        return midTermTeacherProgress;
    }

    public void setMidTermTeacherProgress(int midTermTeacherProgress) {
        this.midTermTeacherProgress = midTermTeacherProgress;
    }

    public int getMidTermStudentPhysMeetingExtCount() {
        return midTermStudentPhysMeetingExtCount;
    }

    public void setMidTermStudentPhysMeetingExtCount(int midTermStudentPhysMeetingExtCount) {
        this.midTermStudentPhysMeetingExtCount = midTermStudentPhysMeetingExtCount;
    }

    public int getMidTermStudentVoiceMeetingExtCount() {
        return midTermStudentVoiceMeetingExtCount;
    }

    public void setMidTermStudentVoiceMeetingExtCount(int midTermStudentVoiceMeetingExtCount) {
        this.midTermStudentVoiceMeetingExtCount = midTermStudentVoiceMeetingExtCount;
    }

    public int getMidTermStudentTextMeetingExtCount() {
        return midTermStudentTextMeetingExtCount;
    }

    public void setMidTermStudentTextMeetingExtCount(int midTermStudentTextMeetingExtCount) {
        this.midTermStudentTextMeetingExtCount = midTermStudentTextMeetingExtCount;
    }

    public OpinionType getMidTermStudentExtMeetingOpinion() {
        return midTermStudentExtMeetingOpinion;
    }

    public void setMidTermStudentExtMeetingOpinion(OpinionType midTermStudentExtMeetingOpinion) {
        this.midTermStudentExtMeetingOpinion = midTermStudentExtMeetingOpinion;
    }

    public AcademicInternshipSessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(AcademicInternshipSessionType sessionType) {
        this.sessionType = sessionType;
    }

    public boolean isPreEmployment() {
        return preEmployment;
    }

    public void setPreEmployment(boolean preEmployment) {
        this.preEmployment = preEmployment;
    }

    public AcademicInternshipGroup getMainGroup() {
        return mainGroup;
    }

    public void setMainGroup(AcademicInternshipGroup mainGroup) {
        this.mainGroup = mainGroup;
    }

    public AcademicTeacher getRecordedSupervisor() {
        return recordedSupervisor;
    }

    public void setRecordedSupervisor(AcademicTeacher recordedSupervisor) {
        this.recordedSupervisor = recordedSupervisor;
    }

    public AcademicTeacher getRecordedSecondSupervisor() {
        return recordedSecondSupervisor;
    }

    public void setRecordedSecondSupervisor(AcademicTeacher recordedSecondSupervisor) {
        this.recordedSecondSupervisor = recordedSecondSupervisor;
    }

    @Override
    public String toString() {
        return "AcademicInternship{" +
                "id=" + id +
                ", mainGroup=" + mainGroup +
                ", board=" + board +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", mainDiscipline='" + mainDiscipline + '\'' +
                ", internshipStatus=" + internshipStatus +
                ", technologies='" + technologies + '\'' +
                ", internshipVariant=" + internshipVariant +
                ", student=" + student +
                ", secondStudent=" + secondStudent +
                ", supervisor=" + supervisor +
                ", secondSupervisor=" + secondSupervisor +
                ", recordedSupervisor=" + recordedSupervisor +
                ", recordedSecondSupervisor=" + recordedSecondSupervisor +
                ", company=" + company +
                ", companyOther='" + companyOther + '\'' +
                ", companyMentor=" + companyMentor +
                ", companyMentorOther='" + companyMentorOther + '\'' +
                ", companyMentorOtherEmail='" + companyMentorOtherEmail + '\'' +
                ", companyMentorOtherPhone='" + companyMentorOtherPhone + '\'' +
                ", duration=" + duration +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", specFilePath='" + specFilePath + '\'' +
                ", midTermReportFilePath='" + midTermReportFilePath + '\'' +
                ", midTermStudentIntMentoringOpinion=" + midTermStudentIntMentoringOpinion +
                ", midTermStudentExtMentoringOpinion=" + midTermStudentExtMentoringOpinion +
                ", midTermStudentMeetingOpinion=" + midTermStudentMeetingOpinion +
                ", midTermStudentExtMeetingOpinion=" + midTermStudentExtMeetingOpinion +
                ", midTermStudentPhysMeetingCount=" + midTermStudentPhysMeetingCount +
                ", midTermStudentVoiceMeetingCount=" + midTermStudentVoiceMeetingCount +
                ", midTermStudentTextMeetingCount=" + midTermStudentTextMeetingCount +
                ", midTermStudentPhysMeetingExtCount=" + midTermStudentPhysMeetingExtCount +
                ", midTermStudentVoiceMeetingExtCount=" + midTermStudentVoiceMeetingExtCount +
                ", midTermStudentTextMeetingExtCount=" + midTermStudentTextMeetingExtCount +
                ", midTermStudentProgress=" + midTermStudentProgress +
                ", midTermStudentObs='" + midTermStudentObs + '\'' +
                ", midTermTeacherExtMentoringOpinion=" + midTermTeacherExtMentoringOpinion +
                ", midTermTeacherMeetingOpinion=" + midTermTeacherMeetingOpinion +
                ", midTermTeacherTechWorkOpinion=" + midTermTeacherTechWorkOpinion +
                ", midTermTeacherSciWorkOpinion=" + midTermTeacherSciWorkOpinion +
                ", midTermTeacherCommWorkOpinion=" + midTermTeacherCommWorkOpinion +
                ", midTermTeacherAutonomyOpinion=" + midTermTeacherAutonomyOpinion +
                ", midTermTeacherProgress=" + midTermTeacherProgress +
                ", midTermTeacherObs='" + midTermTeacherObs + '\'' +
                ", reportFilePath='" + reportFilePath + '\'' +
                ", firstExaminer=" + firstExaminer +
                ", secondExaminer=" + secondExaminer +
                ", chairExaminer=" + chairExaminer +
                ", examDate=" + examDate +
                ", examLocation='" + examLocation + '\'' +
                ", sessionType=" + sessionType +
                ", preEmployment=" + preEmployment +
                ", lastStudentUpdateTime=" + lastStudentUpdateTime +
                ", lastTeacherUpdateTime=" + lastTeacherUpdateTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", studentObservations='" + studentObservations + '\'' +
                ", validationObservations='" + validationObservations + '\'' +
                ", evaluationObservations='" + evaluationObservations + '\'' +
                '}';
    }
}
