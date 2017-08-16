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
import net.vpc.app.vainruling.core.service.model.AppDepartment;

/**
 * Classe
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Education/StudyPlan")
public class AcademicClass {

    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    private String name2;
    private String otherNames;
    @Hierarchy
    @Summary
    private AcademicClass parent;

    @Summary
    private AcademicProgram program;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", type = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;


    public AcademicClass() {
    }

    public AcademicClass(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public AcademicProgram getProgram() {
        return program;
    }

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

    public AcademicClass getParent() {
        return parent;
    }

    public void setParent(AcademicClass parent) {
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

    public AcademicProgramType resolveProgramType(){
        AcademicProgram program = getProgram();
        if(program!=null){
            return program.getProgramType();
        }
        AcademicClass p = getParent();
        if(p!=null){
            return p.resolveProgramType();
        }
        return null;
    }

    public AcademicProgram resolveProgram(){
        AcademicProgram p=getProgram();
        if(p==null){
            AcademicClass pp = getParent();
            if(pp!=null){
                return pp.resolveProgram();
            }
        }
        return null;
    }

    public AppDepartment resolveDepartment(){
        AcademicProgram p = resolveProgram();
        if(p!=null){
            return p.getDepartment();
        }
        return null;
    }


}
