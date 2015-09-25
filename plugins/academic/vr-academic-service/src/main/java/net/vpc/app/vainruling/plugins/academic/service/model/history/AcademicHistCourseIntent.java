/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.history;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity
@Path("Education/History")
public class AcademicHistCourseIntent {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN})
    private AcademicHistCourseAssignment assignment;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
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
