package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.TeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 8/18/16.
 */
public class TeacherFilterByIdArray implements TeacherFilter {
    private final Set<Integer> accepted=new HashSet<>();

    public TeacherFilterByIdArray(Integer[] teacherIds) {
        for (Integer teacherId : teacherIds) {
            this.accepted.add(teacherId);
        }
    }
    public TeacherFilterByIdArray(int[] teacherIds) {
        for (int teacherId : teacherIds) {
            this.accepted.add(teacherId);
        }
    }

    @Override
    public boolean acceptTeacher(AcademicTeacher t) {
        return accepted.contains(t.getId());
    }
}
