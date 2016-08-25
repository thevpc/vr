package net.vpc.app.vainruling.plugins.academic.service.model.stat;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

/**
 * Created by vpc on 8/20/16.
 */
public class TeacherStat {
    private AcademicTeacher teacher;
    private int confirmedTeacherAssignmentCount = 0;
    private LoadValue confirmedTeacherAssignment = new LoadValue();

    public TeacherStat(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public int getConfirmedTeacherAssignmentCount() {
        return confirmedTeacherAssignmentCount;
    }

    public void setConfirmedTeacherAssignmentCount(int confirmedTeacherAssignmentCount) {
        this.confirmedTeacherAssignmentCount = confirmedTeacherAssignmentCount;
    }

    public LoadValue getConfirmedTeacherAssignment() {
        return confirmedTeacherAssignment;
    }

    public void setConfirmedTeacherAssignment(LoadValue confirmedTeacherAssignment) {
        this.confirmedTeacherAssignment = confirmedTeacherAssignment;
    }
}
