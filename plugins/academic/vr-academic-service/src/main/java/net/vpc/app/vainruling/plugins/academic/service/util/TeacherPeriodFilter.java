package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;

/**
 * Created by vpc on 8/17/16.
 */
public interface TeacherPeriodFilter {
    boolean acceptTeacher(AcademicTeacherPeriod t);
}
