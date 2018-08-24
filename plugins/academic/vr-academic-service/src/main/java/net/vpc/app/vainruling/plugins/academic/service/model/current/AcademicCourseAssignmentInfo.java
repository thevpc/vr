/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;


/**
 * @author taha.bensalah@gmail.com
 */
public class AcademicCourseAssignmentInfo {

    private AcademicCourseAssignment assignment;
    private boolean assigned;
    private boolean currentWish;
    private String currentStatusLocalizedString;
    private String currentStatusString;
    private boolean currentProposal;
//    private String intents;
    private AssignmentChuck assignmentChunck = new AssignmentChuck();
    private AssignmentChuck courseChunck = new AssignmentChuck();
//    private Set<String> intentsSet = Collections.EMPTY_SET;
//    private Set<Integer> intentsTeacherIdsSet = Collections.EMPTY_SET;

    public String getCurrentStatusLocalizedString() {
        return currentStatusLocalizedString;
    }

    public void setCurrentStatusLocalizedString(String currentStatusLocalizedString) {
        this.currentStatusLocalizedString = currentStatusLocalizedString;
    }

    public String getCurrentStatusString() {
        return currentStatusString;
    }

    public void setCurrentStatusString(String currentStatusString) {
        this.currentStatusString = currentStatusString;
    }

    
    public AcademicCourseAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(AcademicCourseAssignment assignment) {
        this.assignment = assignment;
    }

    public boolean isCurrentWish() {
        return currentWish;
    }

    public void setCurrentWish(boolean currentWish) {
        this.currentWish = currentWish;
    }

    public boolean isCurrentProposal() {
        return currentProposal;
    }

//    public String getIntents() {
//        return intents;
//    }
//
//    public void setIntents(String intents) {
//        this.intents = intents;
//    }
    public void setCurrentProposal(boolean currentProposal) {
        this.currentProposal = currentProposal;
    }

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


    public AcademicProgram resolveProgram() {
        return getAssignment().resolveProgram();
    }

    public AcademicProgramType resolveProgramType() {
        return getAssignment().resolveProgramType();
    }

    public AppDepartment resolveDepartment() {
        return getAssignment().resolveDepartment();
    }

    public AppDepartment resolveOwnerDepartment() {
        return getAssignment().resolveOwnerDepartment();
    }

    public AcademicSemester resolveSemester() {
        return getAssignment().resolveSemester();
    }
    public AcademicCourseLevel resolveCourseLevel() {
        return getAssignment().resolveCourseLevel();
    }

    public AcademicCourseGroup resolveCourseGroup() {
        return getAssignment().resolveCourseGroup();
    }
    public AppPeriod resolvePeriod() {
        return getAssignment().resolvePeriod();
    }

    public AcademicClass resolveAcademicClass() {
        return getAssignment().resolveAcademicClass();
    }

    public AppUser resolveUser() {
        return getAssignment().resolveUser();
    }

    public AcademicTeacher resolveTeacher() {
        return getAssignment().getTeacher();
    }

    public AcademicCoursePlan resolveCoursePlan() {
        return getAssignment().getCoursePlan();
    }

    public AcademicCourseType resolveCourseType() {
        return getAssignment().getCourseType();
    }
}
