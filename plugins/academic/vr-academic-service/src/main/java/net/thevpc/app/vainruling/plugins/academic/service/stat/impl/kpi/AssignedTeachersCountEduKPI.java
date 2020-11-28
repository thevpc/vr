package net.thevpc.app.vainruling.plugins.academic.service.stat.impl.kpi;

import net.thevpc.app.vainruling.core.service.stats.*;
import net.thevpc.app.vainruling.core.service.stats.*;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 8/29/16.
 */
public class AssignedTeachersCountEduKPI implements KPI<AcademicCourseAssignmentInfo> {
    public static final AssignedTeachersCountEduKPI INSTANCE=new AssignedTeachersCountEduKPI();
    final DefaultKPIValueDef ASSIGNED_TEACHER_COUNT_COL1 = new DefaultKPIValueDef("ASSIGNED_TEACHER_COUNT");
    final KPIValueDef[] COLS = {ASSIGNED_TEACHER_COUNT_COL1};

    @Override
    public KPIEvaluator<AcademicCourseAssignmentInfo> createEvaluator() {
        return new KPIEvaluator<AcademicCourseAssignmentInfo>() {
            private Set<Integer> distinctValues = new HashSet<>();

            @Override
            public void start() {

            }

            @Override
            public void visit(AcademicCourseAssignmentInfo assignment) {
                AcademicTeacher teacher = assignment.resolveTeacher();
                if (teacher != null) {
                    distinctValues.add(teacher.getId());
                }
            }

            @Override
            public KPIValue[] evaluate() {
                return new KPIValue[]{
                        new DefaultKPIValue(ASSIGNED_TEACHER_COUNT_COL1, distinctValues.size())
                };
            }
        };
    }

    @Override
    public KPIValueDef[] getValueDefinitions() {
        return COLS;
    }
}
