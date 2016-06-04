/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.model;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Formula;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity
@Path("/Education/Evaluation")
public class AcademicFeedback {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    @Formula("concat(this.course.fullName,' - ',Coalesce(this.student.contact.fullTitle,''))")
    private String name;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicCourseAssignment course;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicStudent student;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicFeedbackModel model;
    @Field(defaultValue = "true")
    private boolean validated;
    private boolean archived;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicCourseAssignment getCourse() {
        return course;
    }

    public void setCourse(AcademicCourseAssignment course) {
        this.course = course;
    }

    public AcademicStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicStudent student) {
        this.student = student;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public AcademicFeedbackModel getModel() {
        return model;
    }

    public void setModel(AcademicFeedbackModel model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
