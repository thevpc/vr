/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import java.util.Collections;
import java.util.Set;

/**
 * @author taha.bensalah@gmail.com
 */
public class AcademicCourseAssignmentInfo {

    private AcademicCourseAssignment assignment;
    private boolean assigned;
//    private String intents;
    private AssignmentChuck assignmentChunck = new AssignmentChuck();
    private AssignmentChuck courseChunck = new AssignmentChuck();
//    private Set<String> intentsSet = Collections.EMPTY_SET;
//    private Set<Integer> intentsTeacherIdsSet = Collections.EMPTY_SET;

    public AcademicCourseAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(AcademicCourseAssignment assignment) {
        this.assignment = assignment;
    }

//    public String getIntents() {
//        return intents;
//    }
//
//    public void setIntents(String intents) {
//        this.intents = intents;
//    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public AssignmentChuck getAssignmentChunck() {
        return assignmentChunck;
    }

    public AssignmentChuck getCourseChunck() {
        return courseChunck;
    }
}
