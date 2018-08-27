package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;

public interface IAcademicCourseAssignment {

    AcademicCoursePlan getCoursePlan();

    String getDiscriminator();

    String getLabels();

    AcademicTeacher getTeacher();

    AppDepartment resolveDepartment();

    AppDepartment getOwnerDepartment();

    AcademicProgramType resolveProgramType();

    AcademicSemester resolveSemester();

    AcademicCourseType getCourseType();

    AcademicClass resolveAcademicClass();

    boolean isWish();

    boolean isProposal();
    
    boolean isAssigned();
}
