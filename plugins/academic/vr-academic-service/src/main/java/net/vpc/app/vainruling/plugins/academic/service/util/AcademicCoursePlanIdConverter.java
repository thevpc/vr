package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.vpc.common.util.Converter;

public class AcademicCoursePlanIdConverter implements Converter<AcademicCoursePlan,Integer> {
    @Override
    public Integer convert(AcademicCoursePlan value) {
        return value.getId();
    }
}
