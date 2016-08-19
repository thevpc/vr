package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

/**
 * Created by vpc on 8/17/16.
 */
public interface TeacherFilter {
    boolean acceptTeacher(AcademicTeacher t);
}
