/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.current;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.FormulaType;
import net.thevpc.upa.config.*;


/**
 * noyau pedagogique
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.period.name desc, this.fullName, this.name")
@Path("Education/StudyPlan")
@Properties(
        {
            @Property(name = "ui.auto-filter.period", value = "{expr='this.period',order=1}"),
            @Property(name = "ui.auto-filter.department", value = "{expr='this.program.department',order=2}"),
            @Property(name = "ui.auto-filter.program", value = "{expr='this.program',order=3}"),
            @Property(name = "ui.auto-filter.programType", value = "{expr='this.program.programType',order=4}"),
            @Property(name = "ui.auto-filter.responsible", value = "{expr='this.responsible',order=7}")
        }
)
public class AcademicCourseCore {

    @Path("Main")
    @Id
    @Sequence

    private int id;

    @Field(defaultValue = "?")
    private String code;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:120px")
    )
    private AppPeriod period;
    
    private AcademicProgram program;

    @Properties(
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    )
    private String name;
    @Main
    @Formula(
            value = "concat((select a.name from AppPeriod a where a.id=this.periodId),'-',(select a.name from AcademicProgram a where a.id=this.programId),'-',Coalesce(this.code,'X'),'-',this.name)",
            formulaType = {FormulaType.PERSIST, FormulaType.UPDATE},
            formulaOrder = 1
    )
    private String fullName;

    @Properties(
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    )
    private String otherNames;

    @Properties(
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    )
    private String name2;

    @Summary
    private AcademicTeacher responsible;
    private AcademicTeacher responsible2;
    @Properties(
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    )
    private String responsibleAvailability;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AcademicCourseCore{" + "name=" + name
                + ", program=" + program + '}';
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AcademicCourseCore that = (AcademicCourseCore) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public AcademicTeacher getResponsible() {
        return responsible;
    }

    public void setResponsible(AcademicTeacher responsible) {
        this.responsible = responsible;
    }

    public AcademicTeacher getResponsible2() {
        return responsible2;
    }

    public void setResponsible2(AcademicTeacher responsible2) {
        this.responsible2 = responsible2;
    }

    public String getResponsibleAvailability() {
        return responsibleAvailability;
    }

    public void setResponsibleAvailability(String responsibleAvailability) {
        this.responsibleAvailability = responsibleAvailability;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

}
