package net.vpc.app.vainruling.plugins.academic.service.stat.impl.groups;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.stats.KPIGroup;
import net.vpc.app.vainruling.core.service.stats.KPIGroupBy;
import net.vpc.app.vainruling.core.service.stats.StringArrayKPIGroup;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignmentInfo;

import java.util.*;

/**
 * Created by vpc on 8/29/16.
 */
public class TeacherOfficialDisciplineGroupBy implements KPIGroupBy<AcademicCourseAssignmentInfo> {
    private static StringArrayKPIGroup NON_VALUE = new StringArrayKPIGroup("<<No Discipline>>", null);
    private boolean intents;

    public TeacherOfficialDisciplineGroupBy(boolean intents) {
        this.intents = intents;
    }

    @Override
    public List<KPIGroup> createGroups(AcademicCourseAssignmentInfo assignment) {
        Map<Integer, AcademicOfficialDiscipline> values = new HashMap<>();
        for (AcademicTeacher teacher : TeacherGroupBy.findTeachers(assignment, intents).values()) {
            AcademicOfficialDiscipline d = teacher.getOfficialDiscipline();
            if(d!=null){
                values.put(d.getId(),d);
            }
        }
        if (values.size() == 0) {
            return Arrays.asList(NON_VALUE);
        }
        List<KPIGroup> g = new ArrayList<>();
        for (AcademicOfficialDiscipline value : values.values()) {
            g.add(new StringArrayKPIGroup(value.getName(), value, value.getId()));
        }
        return g;
    }
}
