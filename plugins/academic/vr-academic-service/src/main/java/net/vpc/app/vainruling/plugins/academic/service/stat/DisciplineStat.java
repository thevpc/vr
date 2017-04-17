package net.vpc.app.vainruling.plugins.academic.service.stat;

import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.common.util.DefaultMapList;
import net.vpc.common.util.MapList;

/**
 * Created by vpc on 3/9/17.
 */
public class DisciplineStat {
    private String discipline;

    public DisciplineStat(String discipline) {
        this.discipline = discipline;
    }

    public String getDiscipline() {
        return discipline;
    }

    private double coursePlanCount;
    private LoadValue value=new LoadValue();
    private MapList<Integer,AcademicCourseAssignment> assignments=new DefaultMapList<Integer, AcademicCourseAssignment>(AcademicPlugin.AcademicCourseAssignmentIdConverter);
    private MapList<Integer,AcademicTeacher> teachers=new DefaultMapList<Integer, AcademicTeacher>(AcademicPlugin.AcademicTeacherIdConverter);

    public MapList<Integer, AcademicCourseAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(MapList<Integer, AcademicCourseAssignment> assignments) {
        this.assignments = assignments;
    }

    public MapList<Integer, AcademicTeacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(MapList<Integer, AcademicTeacher> teachers) {
        this.teachers = teachers;
    }

    public double getTeachersCount() {
        return getTeachers().size();
    }

    public double getCourseAssignmentCount() {
        return getAssignments().size();
    }

    public double getCoursePlanCount() {
        return coursePlanCount;
    }

    public void setCoursePlanCount(double coursePlanCount) {
        this.coursePlanCount = coursePlanCount;
    }

    public LoadValue getValue() {
        return value;
    }

    public void setValue(LoadValue value) {
        this.value = value;
    }
}
