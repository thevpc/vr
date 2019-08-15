/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.report.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.vpc.upa.config.*;

import java.util.Date;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education/Evaluation")
public class AcademicReport {

    @Id
    @Sequence
    private int id;
    @Main
    private String subject;
    @Summary
    private AcademicReportTitle title;
    private AppUser user;
    @Summary
    private AcademicStudent student;
    @Summary
    private AcademicClass academicClass;
    private String owner;

    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String ownerDetails;

    private String observationPrivateShort;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String observationPrivateLong;

    private String observationPublicShort;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String observationPublicLong;
    @Properties({
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    private String mainAttachment;

    private Date createDateStart;
    private Date createDateEnd;
    private boolean createAllowed;
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.PROFILE_EXPRESSION))
    private String createProfiles;

    private Date updateDateStart;
    private Date updateDateEnd;
    private boolean updateAllowed;
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.PROFILE_EXPRESSION))
    private String updateProfiles;

    private String evaluationPrivateShort;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String evaluationPrivateLong;

    private String evaluationPublicShort;
    private double evaluationPublicNumber;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String evaluationPublicLong;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicReportTitle getTitle() {
        return title;
    }

    public void setTitle(AcademicReportTitle title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public AcademicStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicStudent student) {
        this.student = student;
    }

    public AcademicClass getAcademicClass() {
        return academicClass;
    }

    public void setAcademicClass(AcademicClass academicClass) {
        this.academicClass = academicClass;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerDetails() {
        return ownerDetails;
    }

    public void setOwnerDetails(String ownerDetails) {
        this.ownerDetails = ownerDetails;
    }


    public String getObservationPrivateShort() {
        return observationPrivateShort;
    }

    public void setObservationPrivateShort(String observationPrivateShort) {
        this.observationPrivateShort = observationPrivateShort;
    }

    public String getObservationPrivateLong() {
        return observationPrivateLong;
    }

    public void setObservationPrivateLong(String observationPrivateLong) {
        this.observationPrivateLong = observationPrivateLong;
    }

    public String getObservationPublicShort() {
        return observationPublicShort;
    }

    public void setObservationPublicShort(String observationPublicShort) {
        this.observationPublicShort = observationPublicShort;
    }

    public String getObservationPublicLong() {
        return observationPublicLong;
    }

    public void setObservationPublicLong(String observationPublicLong) {
        this.observationPublicLong = observationPublicLong;
    }

    public String getMainAttachment() {
        return mainAttachment;
    }

    public void setMainAttachment(String mainAttachment) {
        this.mainAttachment = mainAttachment;
    }

    public Date getCreateDateStart() {
        return createDateStart;
    }

    public void setCreateDateStart(Date createDateStart) {
        this.createDateStart = createDateStart;
    }

    public Date getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(Date createDateEnd) {
        this.createDateEnd = createDateEnd;
    }

    public boolean isCreateAllowed() {
        return createAllowed;
    }

    public void setCreateAllowed(boolean createAllowed) {
        this.createAllowed = createAllowed;
    }

    public String getCreateProfiles() {
        return createProfiles;
    }

    public void setCreateProfiles(String createProfiles) {
        this.createProfiles = createProfiles;
    }

    public Date getUpdateDateStart() {
        return updateDateStart;
    }

    public void setUpdateDateStart(Date updateDateStart) {
        this.updateDateStart = updateDateStart;
    }

    public Date getUpdateDateEnd() {
        return updateDateEnd;
    }

    public void setUpdateDateEnd(Date updateDateEnd) {
        this.updateDateEnd = updateDateEnd;
    }

    public boolean isUpdateAllowed() {
        return updateAllowed;
    }

    public void setUpdateAllowed(boolean updateAllowed) {
        this.updateAllowed = updateAllowed;
    }

    public String getUpdateProfiles() {
        return updateProfiles;
    }

    public void setUpdateProfiles(String updateProfiles) {
        this.updateProfiles = updateProfiles;
    }

    public String getEvaluationPrivateShort() {
        return evaluationPrivateShort;
    }

    public void setEvaluationPrivateShort(String evaluationPrivateShort) {
        this.evaluationPrivateShort = evaluationPrivateShort;
    }

    public String getEvaluationPrivateLong() {
        return evaluationPrivateLong;
    }

    public void setEvaluationPrivateLong(String evaluationPrivateLong) {
        this.evaluationPrivateLong = evaluationPrivateLong;
    }

    public String getEvaluationPublicShort() {
        return evaluationPublicShort;
    }

    public void setEvaluationPublicShort(String evaluationPublicShort) {
        this.evaluationPublicShort = evaluationPublicShort;
    }

    public String getEvaluationPublicLong() {
        return evaluationPublicLong;
    }

    public void setEvaluationPublicLong(String evaluationPublicLong) {
        this.evaluationPublicLong = evaluationPublicLong;
    }

    public double getEvaluationPublicNumber() {
        return evaluationPublicNumber;
    }

    public void setEvaluationPublicNumber(double evaluationPublicNumber) {
        this.evaluationPublicNumber = evaluationPublicNumber;
    }

}
