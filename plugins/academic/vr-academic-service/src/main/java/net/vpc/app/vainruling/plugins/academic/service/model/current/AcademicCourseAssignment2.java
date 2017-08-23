package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

public class AcademicCourseAssignment2 implements IAcademicCourseAssignment{
    private AcademicCourseIntent a;

    public AcademicCourseAssignment2(AcademicCourseIntent a) {
        this.a = a;
    }

    @Override
    public AcademicCoursePlan getCoursePlan() {
        return a.getAssignment().getCoursePlan();
    }

    @Override
    public String getDiscriminator() {
        return a.getAssignment().getDiscriminator();
    }

    @Override
    public String getLabels() {
        return a.getAssignment().getLabels();
    }

    @Override
    public AcademicTeacher getTeacher() {
        return a.getTeacher();
    }

    @Override
    public AppDepartment resolveDepartment() {
        return a.getAssignment().resolveDepartment();
    }

    @Override
    public AppDepartment getOwnerDepartment() {
        return a.getAssignment().getOwnerDepartment();
    }

    @Override
    public AcademicProgramType resolveProgramType() {
        return a.getAssignment().resolveProgramType();
    }

    @Override
    public AcademicSemester resolveSemester() {
        return a.getAssignment().resolveSemester();
    }

    @Override
    public AcademicCourseType getCourseType() {
        return a.getAssignment().getCourseType();
    }

    @Override
    public AcademicClass resolveAcademicClass() {
        return a.resolveAcademicClass();
    }
}
