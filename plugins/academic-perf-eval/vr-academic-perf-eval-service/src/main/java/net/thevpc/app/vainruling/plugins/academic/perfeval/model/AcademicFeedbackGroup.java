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
public class AcademicFeedbackGroup {

    @Path("Main")
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
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
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
