/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.report.service.model;

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
public class AcademicReportProperty {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN}, max = "512")
    private String propertyName;
    @Field(modifiers = {UserFieldModifier.SUMMARY}, max = "4096")
    private String propertyValue;
    private AppPropertyTypeKind propertyType=AppPropertyTypeKind.STRING;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AcademicReport report;

    public AcademicReportProperty() {
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

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public AppPropertyTypeKind getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(AppPropertyTypeKind propertyType) {
        this.propertyType = propertyType;
    }

    public AcademicReport getReport() {
        return report;
    }

    public void setReport(AcademicReport report) {
        this.report = report;
    }

}
