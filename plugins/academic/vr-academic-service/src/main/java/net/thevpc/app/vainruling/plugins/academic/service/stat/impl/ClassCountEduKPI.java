//package net.thevpc.app.vainruling.plugins.academic.service.stat.impl;
//
//import net.thevpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignmentInfo;
//import net.thevpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
//import net.thevpc.app.vainruling.plugins.academic.service.stat.*;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * Created by vpc on 8/29/16.
// */
//public class CoursePlanCountEduKPI implements EduKPI {
//    final DefaultEduKPIValueDef COL1 = new DefaultEduKPIValueDef("CourseCount");
//    final EduKPIValueDef[] COLS = {COL1};
//
//    @Override
//    public EduKPIEvaluator createEvaluator() {
//        return new EduKPIEvaluator() {
//            private Set<Integer> distinctValues = new HashSet<>();
//
//            @Override
//            public void start() {
//
//            }
//
//            @Override
//            public void visit(AcademicCourseAssignmentInfo assignment) {
//                AcademicCoursePlan v = assignment.getAssignment().getCoursePlan();
//                if (v != null) {
//                    distinctValues.add(v.getId());
//                }
//            }
//
//            @Override
//            public EduKPIValue[] evaluate() {
//                return new EduKPIValue[]{
//                        new DefaultEduKPIValue(COL1, distinctValues.size())
//                };
//            }
//        };
//    }
//
//    @Override
//    public EduKPIValueDef[] getValueDefinitions() {
//        return COLS;
//    }
//}
