package net.vpc.app.vainruling.plugins.academic.service.util;

import net.vpc.app.vainruling.plugins.academic.service.TeacherFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

/**
 * Created by vpc on 8/18/16.
 */
public class TeacherFilterByDisciplineId implements TeacherFilter {
    private final int _disciplineId;

    public TeacherFilterByDisciplineId(int _disciplineId) {
        this._disciplineId = _disciplineId;
    }

    @Override
    public boolean acceptTeacher(AcademicTeacher t) {
        return t.getOfficialDiscipline()!=null && t.getOfficialDiscipline().getId()== _disciplineId;
    }
}
