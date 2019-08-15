/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.current;

import java.util.List;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

/**
 * @author taha.bensalah@gmail.com
 */
public class AcademicCourseAssignmentInfoByVisitor {

    private AcademicCourseAssignmentInfo value;
    private TeacherAssignmentChunck currentIntent;
    private List<TeacherAssignmentChunck> intents;
    private String rooms;
    private List<TeacherAssignmentChunck> courseIntents;
    private AcademicClass academicClass;
    private boolean currentAssigned;
    private boolean currentWish;
    private boolean currentProposal;
    private boolean currentAcceptedProposal;
    private boolean currentAcceptedWish;
    private boolean otherAssigned;
    private boolean noneAssigned;
    private boolean selected;
    private String currentStatusLocalizedString;
    private String currentStatusString;
    private boolean anyWish;
    private boolean anyProposal;
    private double equivValue;
    private int visitor;

    public AcademicCourseAssignmentInfoByVisitor(AcademicCourseAssignmentInfo value, int visitor, double equivValue) {
        this.value = value;
        this.equivValue = equivValue;
        this.currentIntent = value.getAssignmentChunck().getForTeacher(visitor);
        this.intents = value.getAssignmentChunck().getAllButTeacher(visitor);
        this.courseIntents = value.getCourseChunck().getAllButTeacher(visitor);
        this.currentWish = this.currentIntent != null && this.currentIntent.isWish();
        this.currentProposal = this.currentIntent != null && this.currentIntent.isProposal();
        this.currentAcceptedWish = this.currentIntent != null && this.currentIntent.isAcceptedWish();
        this.currentAcceptedProposal = this.currentIntent != null && this.currentIntent.isAcceptedProposal();
        this.currentStatusString = this.currentIntent == null ? "?" : this.currentIntent.getStatusString();
        this.currentStatusLocalizedString = this.currentIntent == null ? "?" : this.currentIntent.getStatusLocalizedString();
        academicClass = value.resolveAcademicClass();
        AcademicCourseType courseType = value.getAssignment().getCourseType();
        if (courseType != null && courseType.getName().equalsIgnoreCase("C")) {
            rooms = value.getAssignment().getCoursePlan().getRoomConstraintsC();
        } else if (courseType != null && courseType.getName().equalsIgnoreCase("TP")) {
            rooms = value.getAssignment().getCoursePlan().getRoomConstraintsTP();
        }
        if (value.isAssigned() && value.getAssignment().getTeacher() != null) {
            if (value.getAssignment().getTeacher().getId() == visitor) {
                currentAssigned = true;
            } else {
                otherAssigned = true;
            }
        } else {
            noneAssigned = true;
        }
        for (TeacherAssignmentChunck intent : intents) {
            if (intent.isWish()) {
                anyWish = true;
            }
            if (intent.isProposal()) {
                anyProposal = true;
            }
        }
    }

    public double getEquivValue() {
        return equivValue;
    }

    public String getCurrentStatusLocalizedString() {
        return currentStatusLocalizedString;
    }

    public String getCurrentStatusString() {
        return currentStatusString;
    }

    public AcademicCourseAssignment getAssignment() {
        return value.getAssignment();
    }

    public boolean isCurrentWish() {
        return currentWish;
    }

    public boolean isCurrentProposal() {
        return currentProposal;
    }

    public boolean isAssigned() {
        return value.isAssigned();
    }

    public AssignmentChuck getAssignmentChunck() {
        return value.getAssignmentChunck();
    }

    public AssignmentChuck getCourseChunck() {
        return value.getCourseChunck();
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

    public AcademicCourseAssignmentInfo getValue() {
        return value;
    }

    public TeacherAssignmentChunck getCurrentIntent() {
        return currentIntent;
    }

    public List<TeacherAssignmentChunck> getIntents() {
        return intents;
    }

    public String getRooms() {
        return rooms;
    }

    public List<TeacherAssignmentChunck> getCourseIntents() {
        return courseIntents;
    }

    public AcademicClass getAcademicClass() {
        return academicClass;
    }

    public boolean isCurrentAssigned() {
        return currentAssigned;
    }

    public boolean isCurrentAcceptedProposal() {
        return currentAcceptedProposal;
    }

    public boolean isCurrentAcceptedWish() {
        return currentAcceptedWish;
    }

    public boolean isOtherAssigned() {
        return otherAssigned;
    }

    public boolean isNoneAssigned() {
        return noneAssigned;
    }

    public boolean isAnyWish() {
        return anyWish;
    }

    public boolean isAnyProposal() {
        return anyProposal;
    }

    public int getVisitor() {
        return visitor;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
