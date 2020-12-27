package net.thevpc.app.vainruling.plugins.academic.perfeval.model;

import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.thevpc.upa.config.*;

/**
 * Created by vpc on 6/26/17.
 */
@Entity(listOrder = "this.name desc")
@Path("/Education/Evaluation")
public class AcademicFeedbackSession {

    @Id
    @Sequence
    private int id;
    @Main
    @Formula("concat(coalesce(this.period.name,'?'),'-',coalesce(this.semester.code,'?'))")
    private String name;
    @Summary
    private AppPeriod period;
//    private AppDepartment department;
    @Summary
    private AcademicSemester semester;
    @Summary
    private String studentsFilter;

    @Summary
    private boolean read;
    @Summary
    private boolean write;
    private AcademicFeedbackModelGroup modelGroup;

    @Summary
    @Formula("Select count(distinct(f.studentId)) from AcademicFeedback f where f.sessionId=this.id")
    private int countStudents;

    @Summary
    @Formula("Select count(distinct(f.course.teacherId)) from AcademicFeedback f where f.sessionId=this.id")
    private int countTeachers;

    @Summary
    @Formula("Select count(distinct(f.courseId)) from AcademicFeedback f where f.sessionId=this.id")
    private int countAssignments;

    @Summary
    @Formula("Select count(distinct(f.course.coursePlanId)) from AcademicFeedback f where f.sessionId=this.id")
    private int countCourses;

    public AcademicFeedbackSession() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }

    public AcademicSemester getSemester() {
        return semester;
    }

    public void setSemester(AcademicSemester semester) {
        this.semester = semester;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

//    public AppDepartment getDepartment() {
//        return department;
//    }
//
//    public void setDepartment(AppDepartment department) {
//        this.department = department;
//    }
    public int getCountStudents() {
        return countStudents;
    }

    public void setCountStudents(int countStudents) {
        this.countStudents = countStudents;
    }

    public int getCountTeachers() {
        return countTeachers;
    }

    public void setCountTeachers(int countTeachers) {
        this.countTeachers = countTeachers;
    }

    public int getCountAssignments() {
        return countAssignments;
    }

    public void setCountAssignments(int countAssignments) {
        this.countAssignments = countAssignments;
    }

    public int getCountCourses() {
        return countCourses;
    }

    public void setCountCourses(int countCourses) {
        this.countCourses = countCourses;
    }

    public AcademicFeedbackModelGroup getModelGroup() {
        return modelGroup;
    }

    public void setModelGroup(AcademicFeedbackModelGroup modelGroup) {
        this.modelGroup = modelGroup;
    }

    public String getStudentsFilter() {
        return studentsFilter;
    }

    public void setStudentsFilter(String studentsFilter) {
        this.studentsFilter = studentsFilter;
    }
    
    
}
