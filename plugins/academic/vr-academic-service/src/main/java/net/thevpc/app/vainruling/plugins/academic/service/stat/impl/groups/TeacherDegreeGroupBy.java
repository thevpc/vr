package net.thevpc.app.vainruling.plugins.academic.service.stat.impl.groups;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherDegree;
import net.thevpc.app.vainruling.core.service.stats.KPIGroup;
import net.thevpc.app.vainruling.core.service.stats.KPIGroupBy;
import net.thevpc.app.vainruling.core.service.stats.StringArrayKPIGroup;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfo;

import java.util.*;

/**
 * Created by vpc on 8/29/16.
 */
public class TeacherDegreeGroupBy implements KPIGroupBy<AcademicCourseAssignmentInfo> {
    private static StringArrayKPIGroup NON_VALUE = new StringArrayKPIGroup("<<No Discipline>>", null);
    private boolean intents;

    public TeacherDegreeGroupBy(boolean intents) {
        this.intents = intents;
    }

    @Override
    public List<KPIGroup> createGroups(AcademicCourseAssignmentInfo assignment) {
        Map<Integer, AcademicTeacherDegree> values = new HashMap<>();
        for (AcademicTeacher teacher : TeacherGroupBy.findTeachers(assignment, intents).values()) {
            AcademicTeacherDegree d = teacher.getDegree();
            if(d!=null){
                values.put(d.getId(),d);
            }
        }
        if (values.size() == 0) {
            return Arrays.asList(NON_VALUE);
        }
        List<KPIGroup> g = new ArrayList<>();
        for (AcademicTeacherDegree value : values.values()) {
            g.add(new StringArrayKPIGroup(value.getName(), value, value.getId()));
        }
        return g;
    }
}
