/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppGovernorate;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * Classe prépa ou autre
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/Config")
public class AcademicBac {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    private String name2;
    private String otherNames;
    @Hierarchy
    @Summary
    private AcademicBac parent;
    private AppGovernorate governorate;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)

    private Timestamp creationDate;

    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicBac() {
    }

    public AcademicBac(int id, String name) {
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

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

    public AcademicBac getParent() {
        return parent;
    }

    public void setParent(AcademicBac parent) {
        this.parent = parent;
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

    public AppGovernorate getGovernorate() {
        return governorate;
    }

    public void setGovernorate(AppGovernorate governorate) {
        this.governorate = governorate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AcademicBac that = (AcademicBac) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (name2 != null ? !name2.equals(that.name2) : that.name2 != null) return false;
        if (otherNames != null ? !otherNames.equals(that.otherNames) : that.otherNames != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (governorate != null ? !governorate.equals(that.governorate) : that.governorate != null) return false;
        if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null) return false;
        return updateDate != null ? updateDate.equals(that.updateDate) : that.updateDate == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (name2 != null ? name2.hashCode() : 0);
        result = 31 * result + (otherNames != null ? otherNames.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (governorate != null ? governorate.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        return result;
    }
}
