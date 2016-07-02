/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.stat;

import net.vpc.app.vainruling.plugins.academic.service.CourseFilter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherSemestrialLoad;

/**
 * @author vpc
 */
public class TeacherPeriodStat extends TeacherBaseStat{

    private AcademicTeacher teacher;
    private AcademicTeacherPeriod teacherPeriod;
    private AcademicTeacherSemestrialLoad[] semestrialLoad;
    private CourseFilter courseFilter;
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

    public CourseFilter getCourseFilter() {
        return courseFilter;
    }

    public void setCourseFilter(CourseFilter courseFilter) {
        this.courseFilter = courseFilter;
    }

    public AcademicTeacherPeriod getTeacherPeriod() {
        return teacherPeriod;
    }

    public void setTeacherPeriod(AcademicTeacherPeriod teacherPeriod) {
        this.teacherPeriod = teacherPeriod;
    }

}
