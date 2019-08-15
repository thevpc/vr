package net.vpc.app.vainruling.plugins.academic.service.stat.impl.groups;

import net.vpc.app.vainruling.core.service.stats.KPIGroup;
import net.vpc.app.vainruling.core.service.stats.KPIGroupBy;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfo;
import net.vpc.app.vainruling.core.service.stats.StringArrayKPIGroup;
import net.vpc.app.vainruling.plugins.academic.model.current.TeacherAssignmentChunck;

import java.util.*;

/**
 * Created by vpc on 8/29/16.
 */
public class TeacherGroupBy implements KPIGroupBy<AcademicCourseAssignmentInfo> {
    private static StringArrayKPIGroup NON_ASSIGNED = new StringArrayKPIGroup("<<Non Assigned>>", null);
    private boolean intents;

    public TeacherGroupBy(boolean intents) {
        this.intents = intents;
    }
    public static Map<Integer, AcademicTeacher> findTeachers(AcademicCourseAssignmentInfo assignment,boolean intents){
        Map<Integer, AcademicTeacher> teachers = new HashMap<>();
        AcademicTeacher t = assignment.resolveTeacher();
        if (t != null) {
            teachers.put(t.getId(), t);
        }
        if(intents) {
            Collection<TeacherAssignmentChunck> set = assignment.getAssignmentChunck().getChuncks().values();
            for (TeacherAssignmentChunck tchunk : set) {
                if (tchunk != null && !teachers.containsKey(tchunk.getTeacherId())) {
                    t = AcademicPlugin.get().findTeacher(tchunk.getTeacherId());
                    if (t != null) {
                        teachers.put(t.getId(), t);
                    }
                }
            }
        }
        return teachers;
    }

    @Override
    public List<KPIGroup> createGroups(AcademicCourseAssignmentInfo assignment) {
        Map<Integer, AcademicTeacher> teachers = findTeachers(assignment,intents);
        if (teachers.size() == 0) {
            return Arrays.asList(NON_ASSIGNED);
        }
        List<KPIGroup> g = new ArrayList<>();
        for (AcademicTeacher academicTeacher : teachers.values()) {
            g.add(new StringArrayKPIGroup(academicTeacher.resolveFullName(), academicTeacher, academicTeacher.getId()));
        }
        return g;
    }
}
