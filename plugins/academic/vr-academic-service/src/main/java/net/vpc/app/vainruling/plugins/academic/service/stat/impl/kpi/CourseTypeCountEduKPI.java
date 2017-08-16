package net.vpc.app.vainruling.plugins.academic.service.stat.impl.kpi;

import net.vpc.app.vainruling.core.service.stats.*;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignmentInfo;

/**
 * Created by vpc on 8/29/16.
 */
public class CourseTypeCountEduKPI implements KPI<AcademicCourseAssignmentInfo> {
    public static final CourseTypeCountEduKPI INSTANCE=new CourseTypeCountEduKPI();
    final DefaultKPIValueDef COL1 = new DefaultKPIValueDef("CourseTypeC");
    final DefaultKPIValueDef COL2 = new DefaultKPIValueDef("CourseTypeTP");
    final DefaultKPIValueDef COL3 = new DefaultKPIValueDef("CourseTypePM");
    final DefaultKPIValueDef COL4 = new DefaultKPIValueDef("CourseTypePS");
    final KPIValueDef[] COLS = {COL1,COL2,COL3,COL4};

    @Override
    public KPIEvaluator<AcademicCourseAssignmentInfo> createEvaluator() {
        return new KPIEvaluator<AcademicCourseAssignmentInfo>() {
            private int c;
            private int tp;
            private int pm;
            private int ps;

            @Override
            public void start() {

            }

            @Override
            public void visit(AcademicCourseAssignmentInfo assignment) {

                String name = assignment.resolveCourseType().getName();
                if ("C".equalsIgnoreCase(name)) {
                    c++;
                }else
                if ("TP".equalsIgnoreCase(name)) {
                    tp++;
                }else
                if ("PM".equalsIgnoreCase(name)) {
                    pm++;
                }else
                if ("PS".equalsIgnoreCase(name)) {
                    ps++;
                }
            }

            @Override
            public KPIValue[] evaluate() {
                return new KPIValue[]{
                        new DefaultKPIValue(COL1, c),
                        new DefaultKPIValue(COL2, tp),
                        new DefaultKPIValue(COL3, pm),
                        new DefaultKPIValue(COL4, ps)
                };
            }
        };
    }

    @Override
    public KPIValueDef[] getValueDefinitions() {
        return COLS;
    }
}
