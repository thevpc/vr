/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education/Load")
@Properties(
        {
                @Property(name = "ui.auto-filter.period", value = "{expr='assignment.coursePlan.period',order=1}"),
                @Property(name = "ui.auto-filter.department", value = "{expr='assignment.coursePlan.courseLevel.academicClass.program.department',order=2}"),
                @Property(name = "ui.auto-filter.ownerDepartment", value = "{expr='assignment.ownerDepartment',order=3}"),
                @Property(name = "ui.auto-filter.program", value = "{expr='assignment.coursePlan.courseLevel.academicClass.program',order=4}"),
                @Property(name = "ui.auto-filter.programType", value = "{expr='assignment.coursePlan.courseLevel.academicClass.program.programType',order=5}"),
                @Property(name = "ui.auto-filter.class", value = "{expr='assignment.coursePlan.courseLevel.academicClass',order=6}"),
                @Property(name = "ui.auto-filter.teacher", value = "{expr='teacher',order=7}")
        }
)
public class AcademicCourseIntent {

    @Id
    @Sequence
    private int id;
    @Main
    private AcademicCourseAssignment assignment;
    @Summary
    private AcademicTeacher teacher;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicCourseAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(AcademicCourseAssignment assignment) {
        this.assignment = assignment;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

}
