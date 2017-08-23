package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.plugins.academic.service.model.current.IAcademicCourseAssignment;

/**
 * Created by vpc on 6/30/16.
 */
public interface CourseAssignmentFilter {
    boolean acceptAssignment(IAcademicCourseAssignment academicCourseAssignment) ;
    boolean lookupIntents() ;
    CourseAssignmentFilter ALL=new CourseAssignmentFilter() {
        @Override
        public boolean acceptAssignment(IAcademicCourseAssignment academicCourseAssignment) {
            return true;
        }

        @Override
        public boolean lookupIntents() {
            return true;
        }
    };
    CourseAssignmentFilter NO_INTENTS=new CourseAssignmentFilter() {
        @Override
        public boolean acceptAssignment(IAcademicCourseAssignment academicCourseAssignment) {
            return true;
        }

        @Override
        public boolean lookupIntents() {
            return false;
        }
    };
}
