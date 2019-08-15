package net.vpc.app.vainruling.plugins.academic.service.stat;

import net.vpc.app.vainruling.core.service.stats.KPI;
import net.vpc.app.vainruling.core.service.stats.KPIGroupBy;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfo;
import net.vpc.app.vainruling.plugins.academic.service.stat.impl.groups.*;
import net.vpc.app.vainruling.plugins.academic.service.stat.impl.kpi.AssignedTeachersCountEduKPI;
import net.vpc.app.vainruling.plugins.academic.service.stat.impl.kpi.ClassCountEduKPI;
import net.vpc.app.vainruling.plugins.academic.service.stat.impl.kpi.CoursePlanCountEduKPI;
import net.vpc.app.vainruling.plugins.academic.service.stat.impl.kpi.CourseTypeCountEduKPI;

/**
 * Created by vpc on 8/29/16.
 */
public class EduKPIFactory {
    public static KPI ASSIGNED_TEACHER_COUNT= AssignedTeachersCountEduKPI.INSTANCE;
    public static KPI CLASS_COUNT_COUNT= ClassCountEduKPI.INSTANCE;
    public static KPI COURSE_PLAN_COUNT= CoursePlanCountEduKPI.INSTANCE;
    public static KPI COURSE_TYPE_COUNT= CourseTypeCountEduKPI.INSTANCE;
    public static KPIGroupBy<AcademicCourseAssignmentInfo> BY_DEPARTMENT= new DepartmentGroupBy();
    public static KPIGroupBy<AcademicCourseAssignmentInfo> BY_OWNER_DEPARTMENT= new OwnerDepartmentGroupBy();
    public static KPIGroupBy<AcademicCourseAssignmentInfo> BY_SEMESTER= new SemesterGroupBy();
}
