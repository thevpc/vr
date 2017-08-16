/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.FormulaType;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;

/**
 * 1ere, deuxieme, mastere ....
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Education/StudyPlan")
public class AcademicCourseLevel {

    @Id
    @Sequence

    private int id;
    @Formula(value = "concat(this.academicClass.name,'-',this.semester.code)",formulaOrder = 1)
    @Main
    private String name;
    @Summary
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicClass academicClass;
    @Summary
    private AcademicSemester semester;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", type = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicCourseLevel() {
    }

    public AcademicCourseLevel(int id, String name) {
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
    
    public AcademicProgram resolveProgram(){
        return getAcademicClass()==null ?null : getAcademicClass().resolveProgram();
    }

    public AcademicProgramType resolveProgramType(){
        return getAcademicClass()==null ?null : getAcademicClass().resolveProgramType();
    }

    public AppDepartment resolveDepartment(){
        AcademicProgram p = resolveProgram();
        if(p!=null){
            return p.getDepartment();
        }
        return null;
    }
    
}
