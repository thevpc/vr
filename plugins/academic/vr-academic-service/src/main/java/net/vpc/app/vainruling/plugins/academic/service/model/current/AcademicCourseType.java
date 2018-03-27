/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/Config")
public class AcademicCourseType {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    @Unique
    private String name;
    @Summary
    private int weeks;
    @Summary
    private boolean schedulable;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicCourseType() {
    }

    public AcademicCourseType(int id, String name) {
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

    @Override
    public String toString() {
        return StringUtils.nonNull(name);
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
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

    public boolean isSchedulable() {
        return schedulable;
    }

    public void setSchedulable(boolean schedulable) {
        this.schedulable = schedulable;
    }
}
