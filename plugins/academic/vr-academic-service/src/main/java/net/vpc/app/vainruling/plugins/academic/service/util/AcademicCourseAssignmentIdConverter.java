package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.common.util.Converter;

public class AcademicCourseAssignmentIdConverter implements Converter<AcademicCourseAssignment,Integer> {
    public static final AcademicCourseAssignmentIdConverter INSTANCE=new AcademicCourseAssignmentIdConverter();
    @Override
    public Integer convert(AcademicCourseAssignment value) {
        return value.getId();
    }
}
