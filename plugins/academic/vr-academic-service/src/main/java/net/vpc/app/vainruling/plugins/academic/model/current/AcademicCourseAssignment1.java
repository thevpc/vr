package net.vpc.app.vainruling.plugins.academic.model.current;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

public class AcademicCourseAssignment1 implements IAcademicCourseAssignment {

    private AcademicCourseAssignment a;

    public AcademicCourseAssignment1(AcademicCourseAssignment a) {
        this.a = a;
    }

    @Override
    public AcademicCoursePlan getCoursePlan() {
        return a.getCoursePlan();
    }

    @Override
    public String getDiscriminator() {
        return a.getDiscriminator();
    }

    @Override
    public String getLabels() {
        return a.getLabels();
    }

    @Override
    public AcademicTeacher getTeacher() {
        return a.getTeacher();
    }

    @Override
    public AppDepartment resolveDepartment() {
        return a.resolveDepartment();
    }

    @Override
    public AppDepartment getOwnerDepartment() {
        return a.getOwnerDepartment();
    }

    @Override
    public AcademicProgramType resolveProgramType() {
        return a.resolveProgramType();
    }

    @Override
    public AcademicSemester resolveSemester() {
        return a.resolveSemester();
    }

    @Override
    public AcademicCourseType getCourseType() {
        return a.getCourseType();
    }

    @Override
    public AcademicClass resolveAcademicClass() {
        return a.resolveAcademicClass();
    }

    @Override
    public boolean isWish() {
        return false;
    }

    @Override
    public boolean isProposal() {
        return false;
    }

    @Override
    public boolean isAssigned() {
        return getTeacher() != null;
    }

    @Override
    public boolean supportsProposals() {
        return false;
    }

    @Override
    public boolean supportsWish() {
        return false;
    }
    
    

}
