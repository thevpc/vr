package net.thevpc.app.vainruling.plugins.academic.service.stat.impl.groups;

import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.app.vainruling.core.service.stats.KPIGroup;
import net.thevpc.app.vainruling.core.service.stats.KPIGroupBy;
import net.thevpc.app.vainruling.core.service.stats.StringArrayKPIGroup;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfo;

import java.util.*;


/**
 * Created by vpc on 8/29/16.
 */
public class TeacherDepartmentGroupBy implements KPIGroupBy<AcademicCourseAssignmentInfo> {
    private static StringArrayKPIGroup NON_VALUE = new StringArrayKPIGroup("<<No Dept>>", null);
    private boolean intents;

    public TeacherDepartmentGroupBy(boolean intents) {
        this.intents = intents;
    }

    @Override
    public List<KPIGroup> createGroups(AcademicCourseAssignmentInfo assignment) {
        Map<Integer, AppDepartment> departments = new HashMap<>();
        for (AcademicTeacher teacher : TeacherGroupBy.findTeachers(assignment, intents).values()) {
            AppDepartment d = teacher.getUser().getDepartment();
            if(d!=null){
                departments.put(d.getId(),d);
            }
        }
        if (departments.size() == 0) {
            return Arrays.asList(NON_VALUE);
        }
        List<KPIGroup> g = new ArrayList<>();
        for (AppDepartment value : departments.values()) {
            g.add(new StringArrayKPIGroup(value.getName(), value, value.getId()));
        }
        return g;
    }
}
