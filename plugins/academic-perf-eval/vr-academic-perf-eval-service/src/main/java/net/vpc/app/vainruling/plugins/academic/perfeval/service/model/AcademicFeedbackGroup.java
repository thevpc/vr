/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

/**
 * @author vpc
 */
@Entity
@Path("/Education/Evaluation")
public class AcademicFeedbackGroup {

    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;
    private int position;
    @Hierarchy
    @Summary
    private AcademicFeedbackGroup parent;
    @Summary
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicFeedbackModel model;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public AcademicFeedbackGroup getParent() {
        return parent;
    }

    public void setParent(AcademicFeedbackGroup parent) {
        this.parent = parent;
    }

    public AcademicFeedbackModel getModel() {
        return model;
    }

    public void setModel(AcademicFeedbackModel model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "AcademicFeedbackGroup{" + "name=" + name + '}';
    }

}
