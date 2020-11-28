/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.internship.history;

import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education/History")
public class AcademicHistInternshipStudentRole {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private AcademicHistInternship internship;
    @Summary
    private AcademicStudent student;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicHistInternship getInternship() {
        return internship;
    }

    public void setInternship(AcademicHistInternship internship) {
        this.internship = internship;
    }

    public AcademicStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicStudent student) {
        this.student = student;
    }

}
