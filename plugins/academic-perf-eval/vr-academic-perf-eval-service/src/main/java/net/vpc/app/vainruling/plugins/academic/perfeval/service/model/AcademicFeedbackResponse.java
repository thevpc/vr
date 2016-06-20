/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service.model;

import net.vpc.upa.FormulaType;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author vpc
 */
@Entity
@Path("/Education/Evaluation")
public class AcademicFeedbackResponse {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicFeedback feedback;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AcademicFeedbackQuestion question;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private String response;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private boolean valid;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(AcademicFeedback feedback) {
        this.feedback = feedback;
    }

    public AcademicFeedbackQuestion getQuestion() {
        return question;
    }

    public void setQuestion(AcademicFeedbackQuestion question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "AcademicFeedbackResponse{" + "feedback=" + feedback + ", question=" + question + ", response=" + response + '}';
    }


}
