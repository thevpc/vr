package net.vpc.app.vainruling.plugins.academic.service.stat;

import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.util.AcademicCourseAssignmentIdConverter;
import net.vpc.common.util.DefaultMapList;
import net.vpc.common.util.MapList;

/**
 * Created by vpc on 3/9/17.
 */
public class SituationTypeStat {
    private double teachersCount;
    private double courseAssignmentCount;
    private double coursePlanCount;
    private LoadValue value=new LoadValue();
    private LoadValue due=new LoadValue();
    private MapList<Integer,AcademicCourseAssignment> assignments=new DefaultMapList<Integer, AcademicCourseAssignment>(AcademicCourseAssignmentIdConverter.INSTANCE);
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
        return teachersCount;
    }

    public void setTeachersCount(double teachersCount) {
        this.teachersCount = teachersCount;
    }

    public double getCourseAssignmentCount() {
        return courseAssignmentCount;
    }

    public void setCourseAssignmentCount(double courseAssignmentCount) {
        this.courseAssignmentCount = courseAssignmentCount;
    }

    public double getCoursePlanCount() {
        return coursePlanCount;
    }

    public void setCoursePlanCount(double coursePlanCount) {
        this.coursePlanCount = coursePlanCount;
    }

    public LoadValue getDue() {
        return due;
    }

    public LoadValue getValue() {
        return value;
    }

    public void setValue(LoadValue value) {
        this.value = value;
    }
}
