package net.thevpc.app.vainruling.plugins.academic.service.util;

import net.thevpc.app.vainruling.plugins.academic.service.stat.GlobalAssignmentStat;

import java.util.Comparator;

public class GlobalAssignmentStatComparator implements Comparator<GlobalAssignmentStat> {

    @Override
    public int compare(GlobalAssignmentStat o1, GlobalAssignmentStat o2) {
        int d1 = o1.getDegree() == null ? 0 : 1;
        int s1 = o1.getSemester() == null ? 0 : 1;
        int si1 = o1.getSituation() == null ? 0 : 1;
        int d2 = o2.getDegree() == null ? 0 : 1;
        int s2 = o2.getSemester() == null ? 0 : 1;
        int si2 = o2.getSituation() == null ? 0 : 1;
        int x = (s1 - s2);
        if (x != 0) {
            return x;
        }
        x = (o1.getSemester() == null ? "" : o1.getSemester().getName()).compareTo(o2.getSemester() == null ? "" : o2.getSemester().getName());
        if (x != 0) {
            return x;
        }

        x = (d1 + s1 + si1) - (d2 + s2 + si2);
        if (x != 0) {
            return x;
        }
        x = (s1 + si1 * 2 + d1 * 4) - (s2 + si2 * 2 + d2 * 4);
        if (x != 0) {
            return x;
        }
        x = (o1.getSemester() == null ? "" : o1.getSemester().getName()).compareTo(o2.getSemester() == null ? "" : o2.getSemester().getName());
        if (x != 0) {
            return x;
        }
        x = (o1.getSituation() == null ? "" : o1.getSituation().getName()).compareTo(o2.getSituation() == null ? "" : o2.getSituation().getName());
        if (x != 0) {
            return x;
        }
        x = (o1.getDegree() == null ? "" : o1.getDegree().getName()).compareTo(o2.getDegree() == null ? "" : o2.getDegree().getName());
        if (x != 0) {
            return x;
        }
        return 0;
    }

}
