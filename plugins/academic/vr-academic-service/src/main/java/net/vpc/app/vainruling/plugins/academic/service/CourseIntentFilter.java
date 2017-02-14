package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseIntent;

/**
 * Created by vpc on 6/30/16.
 */
public interface CourseIntentFilter {
    boolean acceptIntent(AcademicCourseIntent academicCourseIntent) ;
}
