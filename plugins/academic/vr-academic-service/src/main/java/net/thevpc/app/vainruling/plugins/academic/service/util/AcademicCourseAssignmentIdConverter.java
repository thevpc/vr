package net.thevpc.app.vainruling.plugins.academic.service.util;

import java.util.function.Function;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;

public class AcademicCourseAssignmentIdConverter implements Function<AcademicCourseAssignment,Integer> {
    public static final AcademicCourseAssignmentIdConverter INSTANCE=new AcademicCourseAssignmentIdConverter();
    @Override
    public Integer apply(AcademicCourseAssignment value) {
        return value.getId();
    }
}
