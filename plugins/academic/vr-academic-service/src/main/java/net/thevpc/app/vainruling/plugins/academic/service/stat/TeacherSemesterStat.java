/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.stat;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherDegree;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherSituation;

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

    public AcademicTeacherSituation getTeacherSituation() {
        return teacherStat.getTeacherSituation();
    }

    public AcademicTeacherDegree getTeacherDegree() {
        return teacherStat.getTeacherDegree();
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
