/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service.model.ext;

import java.util.List;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipBoardMessage;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipSuperviserIntent;

/**
 *
 * @author vpc
 */
public class AcademicInternshipExt {

    private AcademicInternship internship;
    private List<AcademicInternshipBoardMessage> messages;
    private List<AcademicInternshipSuperviserIntent> superviserIntents;

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

    public List<AcademicInternshipSuperviserIntent> getSuperviserIntents() {
        return superviserIntents;
    }

    public void setSuperviserIntents(List<AcademicInternshipSuperviserIntent> superviserIntents) {
        this.superviserIntents = superviserIntents;
    }

}
