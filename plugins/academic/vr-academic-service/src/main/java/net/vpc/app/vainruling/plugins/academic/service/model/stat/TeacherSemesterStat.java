/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.stat;

import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

/**
 * @author taha.bensalah@gmail.com
 */
public class TeacherSemesterStat extends TeacherBaseStat {

    private TeacherPeriodStat teacherStat;
    private AcademicSemester semester;
    private int confirmedTeacherAssignmentCount = 0;
    private LoadValue confirmedTeacherAssignment = new LoadValue();

    public TeacherSemesterStat() {
    }

    public AcademicSemester getSemester() {
        return semester;
    }

    public void setSemester(AcademicSemester semester) {
        this.semester = semester;
    }


    public TeacherPeriodStat getTeacherStat() {
        return teacherStat;
    }

    public void setTeacherStat(TeacherPeriodStat teacherStat) {
        this.teacherStat = teacherStat;
    }

    @Override
    public AcademicTeacher getTeacher() {
        return getTeacherStat().getTeacher();
    }

    public int getConfirmedTeacherAssignmentCount() {
        return confirmedTeacherAssignmentCount;
    }

    public void setConfirmedTeacherAssignmentCount(int confirmedTeacherAssignmentCount) {
        this.confirmedTeacherAssignmentCount = confirmedTeacherAssignmentCount;
    }

    public LoadValue getConfirmedTeacherAssignment() {
        return confirmedTeacherAssignment;
    }

    public void setConfirmedTeacherAssignment(LoadValue confirmedTeacherAssignment) {
        this.confirmedTeacherAssignment = confirmedTeacherAssignment;
    }
}
