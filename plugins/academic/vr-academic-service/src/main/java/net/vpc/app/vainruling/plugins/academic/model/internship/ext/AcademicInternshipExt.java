/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.internship.ext;

import net.vpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipBoardMessage;
import net.vpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternshipSupervisorIntent;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public class AcademicInternshipExt {

    private AcademicInternship internship;
    private List<AcademicInternshipBoardMessage> messages;
    private List<AcademicInternshipSupervisorIntent> supervisorIntents;

    public AcademicInternship getInternship() {
        return internship;
    }

    public void setInternship(AcademicInternship internship) {
        this.internship = internship;
    }

    public List<AcademicInternshipBoardMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<AcademicInternshipBoardMessage> messages) {
        this.messages = messages;
    }

    public List<AcademicInternshipSupervisorIntent> getSupervisorIntents() {
        return supervisorIntents;
    }

    public void setSupervisorIntents(List<AcademicInternshipSupervisorIntent> supervisorIntents) {
        this.supervisorIntents = supervisorIntents;
    }

    @Override
    public String toString() {
        return "AcademicInternshipExt{" +
                "internship=" + internship +
                '}';
    }
}
