/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.config;

import net.vpc.common.strings.StringUtils;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * semester 1 or 2 ...
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.fullName, this.name")
@Path("Education/Config")
public class AcademicOfficialDiscipline {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    private String name;
    private AcademicOfficialDisciplineArea area;
    
    @Main
    @Formula("concat(coalesce(this.area.name,''),' - ',coalesce(this.name,''))")
    private String fullName;
    
    @Summary
    private String name2;
    @Summary
    private String name3;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;

    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicOfficialDiscipline() {
    }

    public AcademicOfficialDiscipline(int id, String name) {
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

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AcademicOfficialDiscipline that = (AcademicOfficialDiscipline) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public AcademicOfficialDisciplineArea getArea() {
        return area;
    }

    public void setArea(AcademicOfficialDisciplineArea area) {
        this.area = area;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
}
