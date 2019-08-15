/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.report.model;

import net.vpc.app.vainruling.core.service.model.AppPropertyTypeKind;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.propertyName")
@Path("Education/Evaluation")
public class AcademicReportTitleProperty {

    @Id
    @Sequence
    private int id;
    @Main
    @Field(max = "512")
    private String propertyName;
    private AppPropertyTypeKind propertyType = AppPropertyTypeKind.STRING;
    @Summary
    @Field(max = "4096")
    private String propertyConstraints;
    @Summary
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
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
