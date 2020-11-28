/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.current;

import net.thevpc.upa.FormulaType;
import net.thevpc.upa.config.*;

import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicDiscipline;

/**
 * cours (dans un plan d'études) Module
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.period.name desc, this.fullName, this.name")
@Path("Education/StudyPlan")
@Properties(
        {
            @Property(name = "ui.auto-filter.course", value = "{expr='this.course',order=1}"),
            @Property(name = "ui.auto-filter.discipline", value = "{expr='this.discipline',order=2}"),
        }
)
public class AcademicCoursePlanDiscipline {

    @Path("Main")
    @Id
    @Sequence

    private int id;

    @Summary
    private AcademicCoursePlan course;
    @Summary
    private AcademicDiscipline discipline;

    @Main
    @Formula(
            value = "concat("
            + "(select a.fullName from AcademicCoursePlan a where a.id=this.courseId)"
            + ",'-',Coalesce((select a.Name from AcademicDiscipline a where a.id=this.disciplineId),'?')"
            + ")", formulaOrder = 1,
            formulaType = {FormulaType.PERSIST, FormulaType.UPDATE}
    )
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicCoursePlan getCourse() {
        return course;
    }

    public void setCourse(AcademicCoursePlan course) {
        this.course = course;
    }

    public AcademicDiscipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(AcademicDiscipline discipline) {
        this.discipline = discipline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AcademicCoursePlanDiscipline other = (AcademicCoursePlanDiscipline) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }


}
