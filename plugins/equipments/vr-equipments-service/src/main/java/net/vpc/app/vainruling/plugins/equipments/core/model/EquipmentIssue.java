/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.model;

import java.util.Date;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Main;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.config.Summary;

/**
 *
 * @author vpc
 */
@Entity
@Path("Equipment/Details")
@Properties(
        {
            //                @Property(name = UIConstants.Grid.ROW_STYLE,
            //                        value = "(i.object.deleted or i.object.archived) ?'vr-row-deleted':(i.object.location eq null) ?'vr-row-invalid': ''"),
            //                @Property(name = UIConstants.ENTITY_ID_HIERARCHY, value = "brandLine"),
            @Property(name = "ui.auto-filter.equipment", value = "{expr='this.equipment',order=1}"),
            @Property(name = "ui.auto-filter.reporter", value = "{expr='this.reporter',order=2}"),
            @Property(name = "ui.auto-filter.actualSeverity", value = "{expr='this.actualSeverity',order=3}"),
            @Property(name = "ui.auto-filter.resolutionUser", value = "{expr='this.resolutionUser',order=4}")
        })
public class EquipmentIssue {

    @Id
    @Sequence
    private int id;
    @Summary
    private Date date;
    @Main
    private String reportTitle;
    @Summary
    private Equipment equipment;

    @Summary
    private AppUser reporter;
    private EquipmentIssueSeverity reportSeverity;
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "400")
    private String reportObservations;

    @Summary
    private EquipmentIssueSeverity actualSeverity;
    private AppUser resolutionUser;
    @Summary
    private EquipmentIssueStatus resolutionStatus;
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "400")
    private String resolutionObservations;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public AppUser getReporter() {
        return reporter;
    }

    public void setReporter(AppUser reporter) {
        this.reporter = reporter;
    }

    public EquipmentIssueSeverity getReportSeverity() {
        return reportSeverity;
    }

    public void setReportSeverity(EquipmentIssueSeverity reportSeverity) {
        this.reportSeverity = reportSeverity;
    }

    public String getReportObservations() {
        return reportObservations;
    }

    public void setReportObservations(String reportObservations) {
        this.reportObservations = reportObservations;
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

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

}
