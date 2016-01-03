/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.cr;

import net.vpc.app.vainruling.api.AppPropertyTypeKind;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "propertyName")
@Path("Education/HomeWork")
public class AcademicReportTitleProperty {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN}, max = "512")
    private String propertyName;
    private AppPropertyTypeKind propertyType = AppPropertyTypeKind.STRING;
    @Field(modifiers = {UserFieldModifier.SUMMARY}, max = "4096")
    private String propertyConstraints;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AcademicReportTitle reportTitle;

    public AcademicReportTitleProperty() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public AppPropertyTypeKind getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(AppPropertyTypeKind propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyConstraints() {
        return propertyConstraints;
    }

    public void setPropertyConstraints(String propertyConstraints) {
        this.propertyConstraints = propertyConstraints;
    }

    public AcademicReportTitle getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(AcademicReportTitle reportTitle) {
        this.reportTitle = reportTitle;
    }

}
