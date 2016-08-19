/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.stat;

import net.vpc.app.vainruling.plugins.academic.service.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherSemestrialLoad;

/**
 * @author taha.bensalah@gmail.com
 */
public class TeacherPeriodStat extends TeacherBaseStat {

    private AcademicTeacher teacher;
    private AcademicTeacherPeriod teacherPeriod;
    private AcademicTeacherSemestrialLoad[] semestrialLoad;
    private CourseAssignmentFilter courseAssignmentFilter;
    private boolean includeIntents;
    private TeacherSemesterStat[] semesters;

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

    public boolean isIncludeIntents() {
        return includeIntents;
    }

    public void setIncludeIntents(boolean includeIntents) {
        this.includeIntents = includeIntents;
    }

}
