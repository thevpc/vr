/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import java.sql.Timestamp;
import net.vpc.app.vainruling.api.model.AppGovernorate;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Formula;
import net.vpc.upa.config.Hierarchy;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 * Classe prépa ou autre
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Education/Config")
public class AcademicPreClass {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN})
    private String name;
    private String name2;
    private String otherNames;
    @Hierarchy
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AcademicPreClass parent;
    private AppGovernorate governorate;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", type = FormulaType.PERSIST)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicPreClass() {
    }

    public AcademicPreClass(int id, String name) {
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

    public AcademicPreClass getParent() {
        return parent;
    }

    public void setParent(AcademicPreClass parent) {
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

}
