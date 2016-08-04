/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.history;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education/History")
public class AcademicHistCourseIntent {

    @Id
    @Sequence
    private int id;
    @Main
    private AcademicHistCourseAssignment assignment;
    @Summary
    private AcademicTeacher teacher;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicHistCourseAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(AcademicHistCourseAssignment assignment) {
        this.assignment = assignment;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

}
