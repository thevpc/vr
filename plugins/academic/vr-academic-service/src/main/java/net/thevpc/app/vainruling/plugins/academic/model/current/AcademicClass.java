/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.current;

import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.FormulaType;
import net.thevpc.upa.UserFieldModifier;
import net.thevpc.upa.config.*;

import java.sql.Timestamp;
import net.thevpc.app.vainruling.core.service.model.AppDepartment;

/**
 * Classe
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/StudyPlan")
public class AcademicClass {

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
    private AcademicClass parent;

    @Summary
    private AcademicProgram program;

    @Path("Trace")
//    @Properties(@Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
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
        return p;
    }

    public AppDepartment resolveDepartment(){
        AcademicProgram p = resolveProgram();
        if(p!=null){
            return p.getDepartment();
        }
        return null;
    }


}
