/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.internship.current;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education/Projects/Internships")
@Properties(
        {
                @Property(name = "ui.auto-filter.department", value = "{expr='teacher.department',order=1}"),
                @Property(name = "ui.auto-filter.teacher", value = "{expr='teacher',order=2}"),
        })
public class AcademicInternshipSupervisorIntent {

    @Id
    @Sequence

    private int id;
    @ManyToOne(type = RelationshipType.COMPOSITION)
    @Main
    private AcademicInternship internship;
    @Summary
    private AcademicTeacher teacher;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicInternship getInternship() {
        return internship;
    }

    public void setInternship(AcademicInternship internship) {
        this.internship = internship;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

}
