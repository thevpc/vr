package net.thevpc.app.vainruling.plugins.academic.service.util;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherDegree;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

import java.util.Comparator;

public class TeacherIdByTeacherPeriodComparator implements Comparator<AcademicTeacher> {

    private final int periodId;
    private AcademicPlugin plugin;

    public TeacherIdByTeacherPeriodComparator(AcademicPlugin plugin, int periodId) {
        this.plugin = plugin;
        this.periodId = periodId;
    }

    public int compare(AcademicTeacher t1, AcademicTeacher t2) {
        if (t1.getId() == t2.getId()) {
            return 0;
        }
        if (t1 == null && t2 == null) {
            return 0;
        }
        if (t1 == null) {
            return -1;
        }
        if (t2 == null) {
            return 1;
        }

        AcademicTeacherDegree d1 = plugin.findTeacherPeriod(periodId, t1.getId()).getDegree();
        AcademicTeacherDegree d2 = plugin.findTeacherPeriod(periodId, t2.getId()).getDegree();
        if (d1 == null && d2 == null) {
            return 0;
        }
        if (d1 == null) {
            return -1;
        }
        if (d2 == null) {
            return 1;
        }

        int r = Integer.compare(d1.getPosition(), d2.getPosition());
        if (r != 0) {
            return r;
        }
        r = d1.getName().compareTo(d2.getName());
        if (r != 0) {
            return r;
        }
        r = plugin.getValidName(t1).compareTo(plugin.getValidName(t2));
        if (r != 0) {
            return r;
        }
        return r;
    }
}
