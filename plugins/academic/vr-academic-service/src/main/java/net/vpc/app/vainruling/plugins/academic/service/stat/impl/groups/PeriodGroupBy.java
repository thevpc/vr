package net.vpc.app.vainruling.plugins.academic.service.stat.impl.groups;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.stats.KPIGroup;
import net.vpc.app.vainruling.core.service.stats.KPIGroupBy;
import net.vpc.app.vainruling.core.service.stats.StringArrayKPIGroup;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignmentInfo;

import java.util.Arrays;
import java.util.List;

/**
 * Created by vpc on 8/29/16.
 */
public class PeriodGroupBy implements KPIGroupBy<AcademicCourseAssignmentInfo> {
    private static StringArrayKPIGroup NON_ASSIGNED = new StringArrayKPIGroup("<<No Period>>", null);
    @Override
    public List<KPIGroup> createGroups(AcademicCourseAssignmentInfo assignment) {
        AppPeriod t = assignment.getAssignment().getCoursePlan().getPeriod();
        if(t==null){
            return Arrays.asList(NON_ASSIGNED);
        }else {
            return Arrays.asList(new StringArrayKPIGroup(t.getName(),t,t.getId()));
        }
    }

}
