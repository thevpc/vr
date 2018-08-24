/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.config;

import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.upa.config.*;

import java.util.Date;
import java.util.Objects;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.student.user.fullName")
@Path("Contact")
@Properties(
        {
                @Property(name = "ui.auto-filter.department", value = "{expr='this.student.department',order=1}"),
                @Property(name = "ui.auto-filter.lastClass1", value = "{expr='this.lastClass1',order=2}"),
                @Property(name = "ui.auto-filter.graduationPeriod", value = "{expr='this.graduationPeriod',order=3}"),
                @Property(name = "ui.auto-filter.employmentDelay", value = "{expr='this.employmentDelay',order=4}"),
        }
)
public class AcademicFormerStudent {

    @Path("Main")
    @Id
    @Main
    private AcademicStudent student;
    @Summary
    private AppPeriod graduationPeriod;
    private Date graduationDate;
    @Summary
    private boolean eliminated;
    @Summary
    private String eliminationReason;
    @Summary
    private AcademicClass lastClass1;
    private AcademicClass lastClass2;
    private AcademicClass lastClass3;
    private String graduationProjectTitle;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "4000")
    private String graduationProjectSummary;
    private String graduationProjectSupervisor;
    private String graduationProjectJury;
    private String graduationScoreLetter;
    private double graduationScoreValue;
    private double graduationScoreRank;
    private Date firstJobDate;
    @Field(defaultValue = "0")
    private int firstSalary;
    @Field(defaultValue = "0")
    private int lastSalary;
    private String lastJobPosition;
    @Properties({
//            @Property(name = UIConstants.Form.SEPARATOR, value = "CurriculumVitae"),
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    @Path("CurriculumVitae")
    private String curriculumVitae;
    @Path("CurriculumVitae")
    private EmploymentDelay employmentDelay;
    @Summary
    @Path("CurriculumVitae")
    private AppCompany lastJobCompany;

    public String getLastJobPosition() {
        return lastJobPosition;
    }

    public void setLastJobPosition(String lastJobPosition) {
        this.lastJobPosition = lastJobPosition;
    }

    public AppCompany getLastJobCompany() {
        return lastJobCompany;
    }

    public void setLastJobCompany(AppCompany lastJobCompany) {
        this.lastJobCompany = lastJobCompany;
    }

    public Date getGraduationDate() {
        return graduationDate;
    }

    public void setGraduationDate(Date graduationDate) {
        this.graduationDate = graduationDate;
    }

    public String getEliminationReason() {
        return eliminationReason;
    }

    public void setEliminationReason(String eliminationReason) {
        this.eliminationReason = eliminationReason;
    }

    public Date getFirstJobDate() {
        return firstJobDate;
    }

    public void setFirstJobDate(Date firstJobDate) {
        this.firstJobDate = firstJobDate;
    }

    public int getFirstSalary() {
        return firstSalary;
    }

    public void setFirstSalary(int firstSalary) {
        this.firstSalary = firstSalary;
    }

    public int getLastSalary() {
        return lastSalary;
    }

    public void setLastSalary(int lastSalary) {
        this.lastSalary = lastSalary;
    }

    public String getGraduationProjectTitle() {
        return graduationProjectTitle;
    }

    public void setGraduationProjectTitle(String graduationProjectTitle) {
        this.graduationProjectTitle = graduationProjectTitle;
    }

    public String getGraduationProjectSummary() {
        return graduationProjectSummary;
    }

    public void setGraduationProjectSummary(String graduationProjectSummary) {
        this.graduationProjectSummary = graduationProjectSummary;
    }

    public String getGraduationProjectSupervisor() {
        return graduationProjectSupervisor;
    }

    public void setGraduationProjectSupervisor(String graduationProjectSupervisor) {
        this.graduationProjectSupervisor = graduationProjectSupervisor;
    }

    public String getGraduationScoreLetter() {
        return graduationScoreLetter;
    }

    public void setGraduationScoreLetter(String graduationScoreLetter) {
        this.graduationScoreLetter = graduationScoreLetter;
    }

    public double getGraduationScoreValue() {
        return graduationScoreValue;
    }

    public void setGraduationScoreValue(double graduationScoreValue) {
        this.graduationScoreValue = graduationScoreValue;
    }

    public double getGraduationScoreRank() {
        return graduationScoreRank;
    }

    public void setGraduationScoreRank(double graduationScoreRank) {
        this.graduationScoreRank = graduationScoreRank;
    }

    public String getGraduationProjectJury() {
        return graduationProjectJury;
    }

    public void setGraduationProjectJury(String graduationProjectJury) {
        this.graduationProjectJury = graduationProjectJury;
    }

    public AcademicStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicStudent student) {
        this.student = student;
    }

    public String getCurriculumVitae() {
        return curriculumVitae;
    }

    public void setCurriculumVitae(String curriculumVitae) {
        this.curriculumVitae = curriculumVitae;
    }

    public EmploymentDelay getEmploymentDelay() {
        return employmentDelay;
    }

    public void setEmploymentDelay(EmploymentDelay employmentDelay) {
        this.employmentDelay = employmentDelay;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.student);
        hash = 23 * hash + Objects.hashCode(this.graduationDate);
        hash = 23 * hash + Objects.hashCode(this.eliminationReason);
        hash = 23 * hash + Objects.hashCode(this.graduationProjectTitle);
        hash = 23 * hash + Objects.hashCode(this.graduationProjectSummary);
        hash = 23 * hash + Objects.hashCode(this.graduationProjectSupervisor);
        hash = 23 * hash + Objects.hashCode(this.graduationProjectJury);
        hash = 23 * hash + Objects.hashCode(this.graduationScoreLetter);
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.graduationScoreValue) ^ (Double.doubleToLongBits(this.graduationScoreValue) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.graduationScoreRank) ^ (Double.doubleToLongBits(this.graduationScoreRank) >>> 32));
        hash = 23 * hash + Objects.hashCode(this.firstJobDate);
        hash = 23 * hash + Objects.hashCode(this.curriculumVitae);
        hash = 23 * hash + this.firstSalary;
        hash = 23 * hash + this.lastSalary;
        hash = 23 * hash + Objects.hashCode(this.lastJobPosition);
        hash = 23 * hash + Objects.hashCode(this.lastJobCompany);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AcademicFormerStudent other = (AcademicFormerStudent) obj;

        if (!Objects.equals(this.student, other.student)) {
            return false;
        }
        if (!Objects.equals(this.graduationDate, other.graduationDate)) {
            return false;
        }
        if (!Objects.equals(this.eliminationReason, other.eliminationReason)) {
            return false;
        }
        if (!Objects.equals(this.graduationProjectTitle, other.graduationProjectTitle)) {
            return false;
        }
        if (!Objects.equals(this.graduationProjectSummary, other.graduationProjectSummary)) {
            return false;
        }
        if (!Objects.equals(this.graduationProjectSupervisor, other.graduationProjectSupervisor)) {
            return false;
        }
        if (!Objects.equals(this.graduationProjectJury, other.graduationProjectJury)) {
            return false;
        }
        if (!Objects.equals(this.graduationScoreLetter, other.graduationScoreLetter)) {
            return false;
        }
        if (Double.doubleToLongBits(this.graduationScoreValue) != Double.doubleToLongBits(other.graduationScoreValue)) {
            return false;
        }
        if (Double.doubleToLongBits(this.graduationScoreRank) != Double.doubleToLongBits(other.graduationScoreRank)) {
            return false;
        }
        if (!Objects.equals(this.firstJobDate, other.firstJobDate)) {
            return false;
        }
        if (!Objects.equals(this.curriculumVitae, other.curriculumVitae)) {
            return false;
        }
        if (this.firstSalary != other.firstSalary) {
            return false;
        }
        if (this.lastSalary != other.lastSalary) {
            return false;
        }
        if (!Objects.equals(this.lastJobPosition, other.lastJobPosition)) {
            return false;
        }
        return Objects.equals(this.lastJobCompany, other.lastJobCompany);
    }

    @Override
    public String toString() {
        return "AcademicFormerStudent{" + ", student=" + student + ", graduationDate=" + graduationDate + ", eliminationReason=" + eliminationReason + ", graduationProjectTitle=" + graduationProjectTitle + ", graduationProjectSummary=" + graduationProjectSummary + ", graduationProjectSupervisor=" + graduationProjectSupervisor + ", graduationProjectJury=" + graduationProjectJury + ", graduationScoreLetter=" + graduationScoreLetter + ", graduationScoreValue=" + graduationScoreValue + ", graduationScoreRank=" + graduationScoreRank + ", firstJobDate=" + firstJobDate + ", firstSalary=" + firstSalary + ", lastSalary=" + lastSalary + ", lastJobPosition=" + lastJobPosition + ", lastJobCompany=" + lastJobCompany + '}';
    }

    public AcademicClass getLastClass1() {
        return lastClass1;
    }

    public void setLastClass1(AcademicClass lastClass1) {
        this.lastClass1 = lastClass1;
    }

    public AcademicClass getLastClass2() {
        return lastClass2;
    }

    public void setLastClass2(AcademicClass lastClass2) {
        this.lastClass2 = lastClass2;
    }

    public AcademicClass getLastClass3() {
        return lastClass3;
    }

    public void setLastClass3(AcademicClass lastClass3) {
        this.lastClass3 = lastClass3;
    }

    public AppPeriod getGraduationPeriod() {
        return graduationPeriod;
    }

    public void setGraduationPeriod(AppPeriod graduationPeriod) {
        this.graduationPeriod = graduationPeriod;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
}
