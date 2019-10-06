package net.vpc.app.vainruling.plugins.academic.service.util;

import java.util.function.Function;
import net.vpc.app.vainruling.plugins.academic.service.stat.TeacherPeriodStat;

public class TeacherPeriodStatMapListConverter implements Function<TeacherPeriodStat,Integer> {
    @Override
    public Integer apply(TeacherPeriodStat value) {
        return value.getTeacher().getId();
    }
}
