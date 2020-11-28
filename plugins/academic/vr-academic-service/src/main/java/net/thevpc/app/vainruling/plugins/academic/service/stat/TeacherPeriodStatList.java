package net.thevpc.app.vainruling.plugins.academic.service.stat;

import java.util.List;

/**
 * Created by vpc on 8/21/16.
 */
public class TeacherPeriodStatList {
    private List<TeacherPeriodStat> teacherPeriodStats;
    private List<TeacherSemesterStat> teacherSemesterStats;

    public List<TeacherPeriodStat> getTeacherPeriodStats() {
        return teacherPeriodStats;
    }

    public void setTeacherPeriodStats(List<TeacherPeriodStat> teacherPeriodStats) {
        this.teacherPeriodStats = teacherPeriodStats;
    }

    public List<TeacherSemesterStat> getTeacherSemesterStats() {
        return teacherSemesterStats;
    }

    public void setTeacherSemesterStats(List<TeacherSemesterStat> teacherSemesterStats) {
        this.teacherSemesterStats = teacherSemesterStats;
    }
}
