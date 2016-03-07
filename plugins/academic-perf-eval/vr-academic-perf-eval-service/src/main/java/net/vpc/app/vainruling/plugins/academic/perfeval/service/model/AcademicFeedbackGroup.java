/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.model;

import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Hierarchy;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity
@Path("/Education/Eval")
public class AcademicFeedbackGroup {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;
    @Field(max = "4000")
    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.TEXTAREA)
    private String description;
    private int position;
    @Hierarchy
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicFeedbackGroup parent;
    @Field(modifiers = UserFieldModifier.SUMMARY)
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

}
