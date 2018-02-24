package net.vpc.app.vainruling.plugins.academic.service.util;

/**
 * Created by vpc on 8/18/16.
 */
public final class TeacherFilterFactory {
    private TeacherFilterFactory() {
    }

    public static DefaultTeacherPeriodFilter custom(){
        return new DefaultTeacherPeriodFilter();
    }

    public static DefaultTeacherPeriodFilter teacherIds(Integer[] teacherIds) {
        DefaultTeacherPeriodFilter custom = custom();
        if(teacherIds!=null) {
            for (Integer teacherId : teacherIds) {
                custom.addAcceptedTeacher(teacherId);
            }
        }
        return custom;
    }

    public static DefaultTeacherPeriodFilter teacherIds(int[] teacherIds) {
        DefaultTeacherPeriodFilter custom = custom();
        for (Integer teacherId : teacherIds) {
            custom.addAcceptedTeacher(teacherId);
        }
        return custom;
    }

    public static TeacherPeriodFilter and(TeacherPeriodFilter a, TeacherPeriodFilter b){
        return new TeacherPeriodFilterAnd().and(a).and(b);
    }
}
