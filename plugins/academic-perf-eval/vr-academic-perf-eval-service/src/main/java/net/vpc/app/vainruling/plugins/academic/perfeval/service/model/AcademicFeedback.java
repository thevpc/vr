/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.model;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("/Education/Evaluation")
public class AcademicFeedback {

    @Id
    @Sequence
    private int id;
    @Summary
    private AcademicFeedbackSession session;
    @Summary
    private AcademicFeedbackModel model;
    @Main
    @Formula(value = "concat(coalesce(this.session.name),' - ',this.course.fullName,' - ',Coalesce(this.student.contact.fullTitle,''))",formulaOrder = 1)
    private String name;
    @Summary
    private AcademicCourseAssignment course;
    @Summary
    private AcademicStudent student;
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

    public AcademicFeedbackSession getSession() {
        return session;
    }

    public void setSession(AcademicFeedbackSession session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
