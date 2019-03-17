/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.model;

import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Formula;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Main;
import net.vpc.upa.config.ManyToOne;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.config.Summary;

/**
 *
 * @author vpc
 */
@Entity()
@Path("/Education/Evaluation")
public class AcademicFeedbackModelGroupBinding {

    @Sequence
    @Id
    private int id;
    @Main
    @Formula("concat(coalesce(this.modelGroup.name,'?'),'-',coalesce(this.model.name,'?'))")
    private String name;
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    @Summary
    private AcademicFeedbackModelGroup modelGroup;
    @Summary
    private AcademicFeedbackModel model;
    @Summary
    private AcademicCourseType courseType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicFeedbackModelGroup getModelGroup() {
        return modelGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModelGroup(AcademicFeedbackModelGroup modelGroup) {
        this.modelGroup = modelGroup;
    }

    public AcademicFeedbackModel getModel() {
        return model;
    }

    public void setModel(AcademicFeedbackModel model) {
        this.model = model;
    }

    public AcademicCourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(AcademicCourseType courseType) {
        this.courseType = courseType;
    }

}
