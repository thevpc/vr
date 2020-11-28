/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.integration.parsers;

/**
 *
 * @author vpc
 */
public class AcademicParsers {

    private final AppPeriodParser periods = new AppPeriodParser();
    private final AppGenderParser genders = new AppGenderParser();
    private final AppCivilityParser civilities = new AppCivilityParser();

    private final AcademicProgramParser programs = new AcademicProgramParser();
    private final AcademicClassParser classes = new AcademicClassParser();
    private final AcademicCoursePlanParser coursePlans = new AcademicCoursePlanParser();
    private final AcademicSemesterParser semesters = new AcademicSemesterParser();
    private final AcademicStudentParser students = new AcademicStudentParser();
    private final AcademicTeacherParser teachers = new AcademicTeacherParser();
    private final AcademicCourseTypeParser courseTypes = new AcademicCourseTypeParser();

    public AcademicCourseTypeParser getCourseTypes() {
        return courseTypes;
    }

    public AppPeriodParser getPeriods() {
        return periods;
    }

    public AppGenderParser getGenders() {
        return genders;
    }

    public AppCivilityParser getCivilities() {
        return civilities;
    }

    public AcademicProgramParser getPrograms() {
        return programs;
    }

    public AcademicClassParser getClasses() {
        return classes;
    }

    public AcademicCoursePlanParser getCoursePlans() {
        return coursePlans;
    }

    public AcademicSemesterParser getSemesters() {
        return semesters;
    }

    public AcademicStudentParser getStudents() {
        return students;
    }

    public AcademicTeacherParser getTeachers() {
        return teachers;
    }

}
