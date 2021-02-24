/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.internship.config;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternshipBoard;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity()
@Path("Repository/Education")
public class AcademicInternshipBoardClass {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Property(name = UIConstants.Form.COMPOSITION_LIST_FIELD, value = "academicClass")
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    @Summary
    private AcademicInternshipBoard board;
    @Summary
    private AcademicClass academicClass;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicInternshipBoard getBoard() {
        return board;
    }

    public void setBoard(AcademicInternshipBoard board) {
        this.board = board;
    }

    public AcademicClass getAcademicClass() {
        return academicClass;
    }

    public void setAcademicClass(AcademicClass academicClass) {
        this.academicClass = academicClass;
    }

}
