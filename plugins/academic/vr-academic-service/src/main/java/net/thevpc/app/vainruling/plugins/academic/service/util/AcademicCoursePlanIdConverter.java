package net.thevpc.app.vainruling.plugins.academic.service.util;

import java.util.function.Function;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;

public class AcademicCoursePlanIdConverter implements Function<AcademicCoursePlan,Integer> {
    @Override
    public Integer apply(AcademicCoursePlan value) {
        return value.getId();
    }
}
