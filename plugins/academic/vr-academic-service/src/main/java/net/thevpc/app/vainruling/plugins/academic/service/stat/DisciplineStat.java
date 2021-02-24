package net.thevpc.app.vainruling.plugins.academic.service.stat;

import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.service.util.AcademicCourseAssignmentIdConverter;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.common.collections.Collections2;
import net.thevpc.common.collections.KeyValueList;

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
    private KeyValueList<Integer, AcademicCourseAssignment> assignments=Collections2.keyValueList(AcademicCourseAssignmentIdConverter.INSTANCE);
    private KeyValueList<Integer,AcademicTeacher> teachers= Collections2.keyValueList(AcademicPlugin.AcademicTeacherIdConverter);

    public KeyValueList<Integer, AcademicCourseAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(KeyValueList<Integer, AcademicCourseAssignment> assignments) {
        this.assignments = assignments;
    }

    public KeyValueList<Integer, AcademicTeacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(KeyValueList<Integer, AcademicTeacher> teachers) {
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
