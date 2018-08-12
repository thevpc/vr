/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.FormulaType;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * Unite enseignement
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/StudyPlan")
@Properties(
        {
            @Property(name = "ui.auto-filter.period", value = "{expr='this.period',order=1}")
            ,
            @Property(name = "ui.auto-filter.department", value = "{expr='this.academicClass.program.department',order=2}")
            ,
            @Property(name = "ui.auto-filter.program", value = "{expr='this.academicClass.program',order=3}")
            ,
            @Property(name = "ui.auto-filter.programType", value = "{expr='this.academicClass.program.programType',order=4}")
            ,
            @Property(name = "ui.auto-filter.class", value = "{expr='this.courseLevel.academicClass',order=5}")
        }
)

public class AcademicCourseGroup {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    private String name;

    @Main
    @Formula(
            value = "Concat(Coalesce((select a.name from AcademicClass a where a.id=this.academicClassId),'?'),'-',Coalesce(this.name,'X'))",formulaOrder = 1,
            formulaType = {FormulaType.PERSIST, FormulaType.UPDATE}
    )
    private String fullName;

    @Summary
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;
    @Summary
    private AppPeriod period;

    @Summary
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private AcademicClass academicClass;

    @Summary
    private double credits;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicCourseGroup() {
    }

    public AcademicCourseGroup(int id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public AcademicClass getAcademicClass() {
        return academicClass;
    }

    public void setAcademicClass(AcademicClass academicClass) {
        this.academicClass = academicClass;
    }

    @Override
    public String toString() {
        return StringUtils.nonNull(name);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }
}
