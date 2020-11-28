/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.perfeval.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("/Education/Evaluation")
public class AcademicFeedbackQuestion {

    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String observations;
    private int position;
    @Summary
    private boolean mandatory = true;
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
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

    @Override
    public String toString() {
        return String.valueOf(name);
    }


}
