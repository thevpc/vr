/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.stat;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.util.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherSemestrialLoad;

/**
 * @author taha.bensalah@gmail.com
 */
public class TeacherPeriodStat extends TeacherBaseStat {

    private AcademicTeacher teacher;
    private AcademicTeacherPeriod teacherPeriod;
    private AcademicTeacherSemestrialLoad[] semestrialLoad=new AcademicTeacherSemestrialLoad[0];
    private CourseAssignmentFilter courseAssignmentFilter;
    private TeacherSemesterStat[] semesters=new TeacherSemesterStat[0];
    private int confirmedTeacherAssignmentCount = 0;
    private LoadValue confirmedTeacherAssignment = new LoadValue();

    public AcademicTeacherSituation getTeacherSituation() {
        if(teacherPeriod!=null && teacherPeriod.getSituation()!=null){
            return teacherPeriod.getSituation();
        }
        if(teacher!=null && teacher.getSituation()!=null){
            return teacher.getSituation();
        }
        return null;
    }

    public AcademicTeacherDegree getTeacherDegree() {
        if(teacherPeriod!=null && teacherPeriod.getDegree()!=null){
            return teacherPeriod.getDegree();
        }
        if(teacher!=null && teacher.getDegree()!=null){
            return teacher.getDegree();
        }
        return null;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public TeacherSemesterStat[] getSemesters() {
        return semesters;
    }

    public void setSemesters(TeacherSemesterStat[] semesters) {
        this.semesters = semesters;
    }

    public AcademicTeacherSemestrialLoad[] getSemestrialLoad() {
        return semestrialLoad;
    }

    public void setSemestrialLoad(AcademicTeacherSemestrialLoad[] semestrialLoad) {
        this.semestrialLoad = semestrialLoad;
    }

    public CourseAssignmentFilter getCourseAssignmentFilter() {
        return courseAssignmentFilter;
    }

    public void setCourseAssignmentFilter(CourseAssignmentFilter courseAssignmentFilter) {
        this.courseAssignmentFilter = courseAssignmentFilter;
    }

    public AcademicTeacherPeriod getTeacherPeriod() {
        return teacherPeriod;
    }

    public void setTeacherPeriod(AcademicTeacherPeriod teacherPeriod) {
        this.teacherPeriod = teacherPeriod;
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
