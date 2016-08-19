package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.TeacherFilter;

/**
 * Created by vpc on 8/18/16.
 */
public final class TeacherFilterFactory {
    private TeacherFilterFactory() {
    }

    public static DefaultTeacherFilter custom(){
        return new DefaultTeacherFilter();
    }

    public DefaultTeacherFilter teacherIds(Integer[] teacherIds) {
        DefaultTeacherFilter custom = custom();
        for (Integer teacherId : teacherIds) {
            custom.addAcceptedTeacher(teacherId);
        }
        return custom;
    }

    public DefaultTeacherFilter teacherIds(int[] teacherIds) {
        DefaultTeacherFilter custom = custom();
        for (Integer teacherId : teacherIds) {
            custom.addAcceptedTeacher(teacherId);
        }
        return custom;
    }

    public static TeacherFilter and(TeacherFilter a,TeacherFilter b){
        return new TeacherFilterAnd().and(a).and(b);
    }
}
