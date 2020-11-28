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
//public class CourseTypeCCountEduKPI implements EduKPI {
//    final DefaultEduKPIValueDef COL1 = new DefaultEduKPIValueDef("CourseTypeCCount");
//    final EduKPIValueDef[] COLS = {COL1};
//
//    @Override
//    public EduKPIEvaluator createEvaluator() {
//        return new EduKPIEvaluator() {
//            private int count;
//
//            @Override
//            public void start() {
//
//            }
//
//            @Override
//            public void visit(AcademicCourseAssignmentInfo assignment) {
//
//                if ("C".equalsIgnoreCase(assignment.getAssignment().getCourseType().getName())) {
//                    count++;
//                }
//            }
//
//            @Override
//            public EduKPIValue[] evaluate() {
//                return new EduKPIValue[]{
//                        new DefaultEduKPIValue(COL1, count)
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
