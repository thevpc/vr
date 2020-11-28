/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.internship.current;

import net.thevpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipTeacherRoleType;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education/Projects/Internships")
public class AcademicInternshipTeacherRole {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private AcademicInternship internship;
    @Summary
    private AcademicTeacher teacher;
    @ToString
    private AcademicInternshipTeacherRoleType role;

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

    public AcademicInternshipTeacherRoleType getRole() {
        return role;
    }

    public void setRole(AcademicInternshipTeacherRoleType role) {
        this.role = role;
    }

}
