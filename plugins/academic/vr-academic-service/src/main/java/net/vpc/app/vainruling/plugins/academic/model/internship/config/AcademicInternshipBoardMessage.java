/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.internship.config;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternship;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

import java.util.Date;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/Config")
public class AcademicInternshipBoardMessage {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    private AcademicInternshipBoardTeacher boardTeacher;
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private AcademicInternship internship;
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String privateObservations;
    private Date obsUpdateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrivateObservations() {
        return privateObservations;
    }

    public void setPrivateObservations(String privateObservations) {
        this.privateObservations = privateObservations;
    }

    public Date getObsUpdateDate() {
        return obsUpdateDate;
    }

    public void setObsUpdateDate(Date obsUpdateDate) {
        this.obsUpdateDate = obsUpdateDate;
    }

    public AcademicInternshipBoardTeacher getBoardTeacher() {
        return boardTeacher;
    }

    public void setBoardTeacher(AcademicInternshipBoardTeacher boardTeacher) {
        this.boardTeacher = boardTeacher;
    }

    public AcademicInternship getInternship() {
        return internship;
    }

    public void setInternship(AcademicInternship internship) {
        this.internship = internship;
    }

}
