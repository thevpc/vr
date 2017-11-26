/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.internship.config;

import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity()
@Path("Education/Config")
public class AcademicInternshipBoardTeacher {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @ManyToOne(type = RelationshipType.COMPOSITION)
    @Summary
    private AcademicInternshipBoard board;
    @Summary
    private AcademicTeacher teacher;
    @Summary
    private boolean manager;

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

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

}
