/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service.model.ext;

import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipBoardMessage;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipSupervisorIntent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class AcademicInternshipExtList {

    private List<AcademicInternshipExt> internshipExts = new ArrayList<>();
    private List<AcademicInternship> internships = new ArrayList<>();
    private List<AcademicInternshipBoardMessage> messages = new ArrayList<>();
    private List<AcademicInternshipSupervisorIntent> supervisorIntents = new ArrayList<>();

    public List<AcademicInternshipExt> getInternshipExts() {
        return internshipExts;
    }

    public void setInternshipExts(List<AcademicInternshipExt> internshipExts) {
        this.internshipExts = internshipExts;
    }

    public List<AcademicInternship> getInternships() {
        return internships;
    }

    public void setInternships(List<AcademicInternship> internships) {
        this.internships = internships;
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

}
