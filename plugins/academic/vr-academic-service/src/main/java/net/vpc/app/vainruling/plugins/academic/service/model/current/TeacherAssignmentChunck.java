package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

/**
 * Created by vpc on 9/4/16.
 */
public class TeacherAssignmentChunck {
    int teacherId;
    String teacherName;
    boolean assigned;
    boolean intended;

    public TeacherAssignmentChunck(int teacherId, String teacherName) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }

    @Override
    public String toString() {
        return teacherName +(assigned?"(*)":"");
    }

    public boolean isAssigned() {
        return assigned;
    }

    public TeacherAssignmentChunck setAssigned(boolean assigned) {
        this.assigned = assigned;
        return this;
    }

    public boolean isIntended() {
        return intended;
    }

    public TeacherAssignmentChunck setIntended(boolean intended) {
        this.intended = intended;
        return this;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }
}
