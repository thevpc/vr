package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherPeriodStat;
import net.vpc.common.util.Converter;

public class TeacherPeriodStatMapListConverter implements Converter<TeacherPeriodStat,Integer> {
    @Override
    public Integer convert(TeacherPeriodStat value) {
        return value.getTeacher().getId();
    }
}
