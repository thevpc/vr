/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import java.sql.Timestamp;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Formula;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 * 1ere, deuxieme, mastere ....
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Education/Config")
public class AcademicCourseLevel {

    @Id
    @Sequence

    private int id;
    private String name;
    @Formula("concat(this.academicClass.name,'-',this.semester.code)")
    @Field(modifiers = {UserFieldModifier.MAIN})
    private String fullName;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    @Deprecated
    private AcademicProgram program;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AcademicClass academicClass;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AcademicSemester semester;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", type = FormulaType.PERSIST)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicCourseLevel() {
    }

    public AcademicCourseLevel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Deprecated
    public AcademicProgram getProgram() {
        return program;
    }

    @Deprecated
    public void setProgram(AcademicProgram program) {
        this.program = program;
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
        return StringUtils.nonnull(name);
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

    public AcademicClass getAcademicClass() {
        return academicClass;
    }

    public void setAcademicClass(AcademicClass academicClass) {
        this.academicClass = academicClass;
    }

    public AcademicSemester getSemester() {
        return semester;
    }

    public void setSemester(AcademicSemester semester) {
        this.semester = semester;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
