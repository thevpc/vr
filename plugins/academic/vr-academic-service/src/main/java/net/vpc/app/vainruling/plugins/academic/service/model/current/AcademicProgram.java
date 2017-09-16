/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * filiere
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Education/StudyPlan")
@Properties(
        {
                @Property(name = "ui.auto-filter.department", value = "{expr='this.department',order=1}"),
                @Property(name = "ui.auto-filter.programType", value = "{expr='this.programType',order=2}")
        }
)
public class AcademicProgram {

    @Id
    @Sequence

    private int id;
    @Main
    private String name;
    private String name2;
    private String name3;


    @Summary
    private AppDepartment department;
    @Summary
    private AcademicProgramType programType;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", type = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicProgram() {
    }

    public AcademicProgram(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
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

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    @Override
    public String toString() {
        return StringUtils.nonNull(name);
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

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public AcademicProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(AcademicProgramType programType) {
        this.programType = programType;
    }
}
