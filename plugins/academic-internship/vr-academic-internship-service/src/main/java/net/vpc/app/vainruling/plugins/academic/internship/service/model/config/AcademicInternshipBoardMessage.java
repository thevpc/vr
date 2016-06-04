/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service.model.config;

import java.util.Date;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.ManyToOne;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Education/Config")
public class AcademicInternshipBoardMessage {

    @Id
    @Sequence
    private int id;
    private AcademicInternshipBoardTeacher boardTeacher;
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicInternship internship;
    @Field(max = "4000")
    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = "textarea")
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
