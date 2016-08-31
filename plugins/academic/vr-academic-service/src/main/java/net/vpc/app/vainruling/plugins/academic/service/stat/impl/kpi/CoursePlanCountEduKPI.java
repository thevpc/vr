package net.vpc.app.vainruling.plugins.academic.service.stat.impl.kpi;

import net.vpc.app.vainruling.core.service.stats.*;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignmentInfo;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 8/29/16.
 */
public class CoursePlanCountEduKPI implements KPI<AcademicCourseAssignmentInfo> {
    public static final CoursePlanCountEduKPI INSTANCE=new CoursePlanCountEduKPI();
    final DefaultKPIValueDef COL1 = new DefaultKPIValueDef("CourseCount");
    final KPIValueDef[] COLS = {COL1};

    @Override
    public KPIEvaluator<AcademicCourseAssignmentInfo> createEvaluator() {
        return new KPIEvaluator<AcademicCourseAssignmentInfo>() {
            private Set<Integer> distinctValues = new HashSet<>();

            @Override
            public void start() {

            }

            @Override
            public void visit(AcademicCourseAssignmentInfo assignment) {
                AcademicCoursePlan v = assignment.getAssignment().getCoursePlan();
                if (v != null) {
                    distinctValues.add(v.getId());
                }
            }

            @Override
            public KPIValue[] evaluate() {
                return new KPIValue[]{
                        new DefaultKPIValue(COL1, distinctValues.size())
                };
            }
        };
    }

    @Override
    public KPIValueDef[] getValueDefinitions() {
        return COLS;
    }
}
