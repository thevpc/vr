/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import java.util.Date;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.AccessLevel;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.config.Summary;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "this.opDate desc")
@Path("Equipment")
public class EquipmentIssueLog {

    @Id
    @Sequence
    private int id;

    @Summary
    private Date opDate;
    @Summary
    private AppUser resolutionUser;

    @Field(updateAccessLevel = AccessLevel.READ_ONLY)
    @Summary
    private EquipmentIssue issue;
    @Summary
    private EquipmentIssueSeverity actualSeverity;
    @Field(updateAccessLevel = AccessLevel.READ_ONLY)
    @Summary
    private EquipmentIssueStatus resolutionStatus;
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "400")
    private String resolutionObservations;
    @Summary
    private int resolutionTimeMinutes;
    @Summary
    private EquipmentIssueLogList list;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }

    public EquipmentIssue getIssue() {
        return issue;
    }

    public void setIssue(EquipmentIssue issue) {
        this.issue = issue;
    }

    public EquipmentIssueSeverity getActualSeverity() {
        return actualSeverity;
    }

    public void setActualSeverity(EquipmentIssueSeverity actualSeverity) {
        this.actualSeverity = actualSeverity;
    }

    public AppUser getResolutionUser() {
        return resolutionUser;
    }

    public void setResolutionUser(AppUser resolutionUser) {
        this.resolutionUser = resolutionUser;
    }

    public EquipmentIssueStatus getResolutionStatus() {
        return resolutionStatus;
    }

    public void setResolutionStatus(EquipmentIssueStatus resolutionStatus) {
        this.resolutionStatus = resolutionStatus;
    }

    public String getResolutionObservations() {
        return resolutionObservations;
    }

    public void setResolutionObservations(String resolutionObservations) {
        this.resolutionObservations = resolutionObservations;
    }

    public int getResolutionTimeMinutes() {
        return resolutionTimeMinutes;
    }

    public void setResolutionTimeMinutes(int resolutionTimeMinutes) {
        this.resolutionTimeMinutes = resolutionTimeMinutes;
    }

    public EquipmentIssueLogList getList() {
        return list;
    }

    public void setList(EquipmentIssueLogList list) {
        this.list = list;
    }

}
