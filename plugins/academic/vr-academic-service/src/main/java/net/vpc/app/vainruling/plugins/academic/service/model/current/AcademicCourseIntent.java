/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.config.*;
import net.vpc.upa.types.DateTime;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education/Load")
@Properties(
        {
                @Property(name = "ui.auto-filter.period", value = "{expr='this.assignment.coursePlan.period',order=1}"),
                @Property(name = "ui.auto-filter.department", value = "{expr='this.assignment.coursePlan.courseLevel.academicClass.program.department',order=2}"),
                @Property(name = "ui.auto-filter.ownerDepartment", value = "{expr='this.assignment.ownerDepartment',order=3}"),
                @Property(name = "ui.auto-filter.program", value = "{expr='this.assignment.coursePlan.courseLevel.academicClass.program',order=4}"),
                @Property(name = "ui.auto-filter.programType", value = "{expr='this.assignment.coursePlan.courseLevel.academicClass.program.programType',order=5}"),
                @Property(name = "ui.auto-filter.class", value = "{expr='this.assignment.coursePlan.courseLevel.academicClass',order=6}"),
                @Property(name = "ui.auto-filter.teacher", value = "{expr='this.teacher',order=7}"),
                @Property(name = "ui.auto-filter.proposal", value = "{expr='this.proposal',order=8}"),
                @Property(name = "ui.auto-filter.wish", value = "{expr='this.wish',order=9}")
        }
)
public class AcademicCourseIntent {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    private AcademicCourseAssignment assignment;
    @Summary
    private AcademicTeacher teacher;
    @Summary
    private boolean proposal;
    @Summary
    private DateTime proposalDate;
    @Summary
    private boolean acceptedProposal;
    @Summary
    private boolean wish;
    @Summary
    private DateTime wishDate;
    @Summary
    private boolean acceptedWish;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicCourseAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(AcademicCourseAssignment assignment) {
        this.assignment = assignment;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public boolean isProposal() {
        return proposal;
    }

    public void setProposal(boolean proposal) {
        this.proposal = proposal;
    }

    public DateTime getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(DateTime proposalDate) {
        this.proposalDate = proposalDate;
    }

    public boolean isAcceptedProposal() {
        return acceptedProposal;
    }

    public void setAcceptedProposal(boolean acceptedProposal) {
        this.acceptedProposal = acceptedProposal;
    }

    public boolean isWish() {
        return wish;
    }

    public void setWish(boolean wish) {
        this.wish = wish;
    }

    public DateTime getWishDate() {
        return wishDate;
    }

    public void setWishDate(DateTime wishDate) {
        this.wishDate = wishDate;
    }

    public boolean isAcceptedWish() {
        return acceptedWish;
    }

    public void setAcceptedWish(boolean acceptedWish) {
        this.acceptedWish = acceptedWish;
    }

    public AcademicSemester resolveSemester() {
        AcademicCourseAssignment assignment = getAssignment();
        if(assignment!=null){
            return assignment.resolveSemester();
        }
        return null;
    }

    public AcademicProgram resolveProgram() {
        AcademicCourseAssignment assignment = getAssignment();
        if(assignment!=null){
            return assignment.resolveProgram();
        }
        return null;
    }

    public AcademicProgramType resolveProgramType() {
        AcademicCourseAssignment assignment = getAssignment();
        if(assignment!=null){
            return assignment.resolveProgramType();
        }
        return null;
    }

    public AppDepartment resolveDepartment() {
        AcademicCourseAssignment assignment = getAssignment();
        if(assignment!=null){
            return assignment.resolveDepartment();
        }
        return null;
    }

    public AppDepartment resolveOwnerDepartment() {
        AcademicCourseAssignment assignment = getAssignment();
        if(assignment!=null){
            return assignment.resolveOwnerDepartment();
        }
        return null;
    }

    public AppPeriod resolvePeriod() {
        AcademicCourseAssignment assignment = getAssignment();
        if(assignment!=null){
            return assignment.resolvePeriod();
        }
        return null;
    }

    public AcademicClass resolveAcademicClass() {
        AcademicCourseAssignment assignment = getAssignment();
        if(assignment!=null){
            return assignment.resolveAcademicClass();
        }
        return null;
    }

    public AppContact resolveContact() {
        AcademicCourseAssignment assignment = getAssignment();
        if(assignment!=null){
            return assignment.resolveContact();
        }
        return null;
    }
}
