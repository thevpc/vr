package net.thevpc.app.vainruling.plugins.academic.model.current;

import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;

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

    boolean supportsProposals();
    
    boolean supportsWish();
    
    boolean isWish();

    boolean isProposal();
    
    boolean isAssigned();
}
