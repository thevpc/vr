package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 6/30/16.
 */
public interface CourseAssignmentFilter {
    boolean acceptAssignment(AcademicCourseAssignment academicCourseAssignment) ;
    boolean lookupIntents() ;
}
