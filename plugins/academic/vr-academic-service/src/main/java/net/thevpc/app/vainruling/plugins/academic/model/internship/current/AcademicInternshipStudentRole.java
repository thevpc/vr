/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.internship.current;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education/Projects/Internships")
public class AcademicInternshipStudentRole {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    @Property(name = UIConstants.Form.COMPOSITION_LIST_FIELD, value = "student")
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private AcademicInternship internship;
    @Summary
    @Property(name = UIConstants.Form.COMPOSITION_LIST_FIELD, value = "internship")
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private AcademicStudent student;

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

    public AcademicStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicStudent student) {
        this.student = student;
    }

}
