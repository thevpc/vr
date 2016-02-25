/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service.model.config;

import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.ManyToOne;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity()
@Path("Education/Config")
public class AcademicInternshipBoardTeacher {

    @Id
    @Sequence

    private int id;
    @ManyToOne(type = RelationshipType.COMPOSITION)
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicInternshipBoard board;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicTeacher teacher;
    @Field(defaultValue = "false",modifiers = UserFieldModifier.SUMMARY)
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
