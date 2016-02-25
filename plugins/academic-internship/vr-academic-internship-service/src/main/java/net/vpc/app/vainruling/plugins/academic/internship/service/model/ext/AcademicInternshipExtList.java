/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.service.model.ext;

import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipBoardMessage;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipSuperviserIntent;

/**
 *
 * @author vpc
 */
public class AcademicInternshipExtList {

    private List<AcademicInternshipExt> internshipExts=new ArrayList<>();
    private List<AcademicInternship> internships=new ArrayList<>();
    private List<AcademicInternshipBoardMessage> messages=new ArrayList<>();
    private List<AcademicInternshipSuperviserIntent> superviserIntents=new ArrayList<>();

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

    public List<AcademicInternshipSuperviserIntent> getSuperviserIntents() {
        return superviserIntents;
    }

    public void setSuperviserIntents(List<AcademicInternshipSuperviserIntent> superviserIntents) {
        this.superviserIntents = superviserIntents;
    }
    
}
