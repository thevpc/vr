package net.thevpc.app.vainruling.plugins.academic.service.util;

import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherPeriod;

/**
 * Created by vpc on 8/17/16.
 */
public interface TeacherPeriodFilter {
    boolean acceptTeacher(AcademicTeacherPeriod t);
}
