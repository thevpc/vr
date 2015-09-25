/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service.model.history;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
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
public class AcademicHistInternshipStudentRole {

    @Id
    @Sequence

    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private AcademicHistInternship internship;
    @Field(modifiers = UserFieldModifier.SUMMARY)
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
