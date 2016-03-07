/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity
@Path("/Education/Eval")
public class AcademicFeedbackQuestion {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;
    private String observations;
    private int position;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private boolean mandatory;
    private AcademicFeedbackGroup parent;

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

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public AcademicFeedbackGroup getParent() {
        return parent;
    }

    public void setParent(AcademicFeedbackGroup parent) {
        this.parent = parent;
    }

}
