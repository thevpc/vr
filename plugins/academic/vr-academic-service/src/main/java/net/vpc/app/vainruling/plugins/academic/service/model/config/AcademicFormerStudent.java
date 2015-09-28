/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.config;

import java.util.Date;
import java.util.Objects;
import net.vpc.app.vainruling.api.model.AppCompany;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "student.contact.fullName")
@Path("Education/History")
public class AcademicFormerStudent {

    @Id
    @Field(modifiers = UserFieldModifier.MAIN)
    private AcademicStudent student;
    private Date graduationDate;
    private String eliminationReason;
    private String graduationProjectTitle;
    private String graduationProjectSummary;
    private String graduationProjectSuperviser;
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
    @Field(modifiers = {UserFieldModifier.SUMMARY})
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

    public String getGraduationProjectSuperviser() {
        return graduationProjectSuperviser;
    }

    public void setGraduationProjectSuperviser(String graduationProjectSuperviser) {
        this.graduationProjectSuperviser = graduationProjectSuperviser;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.student);
        hash = 23 * hash + Objects.hashCode(this.graduationDate);
        hash = 23 * hash + Objects.hashCode(this.eliminationReason);
        hash = 23 * hash + Objects.hashCode(this.graduationProjectTitle);
        hash = 23 * hash + Objects.hashCode(this.graduationProjectSummary);
        hash = 23 * hash + Objects.hashCode(this.graduationProjectSuperviser);
        hash = 23 * hash + Objects.hashCode(this.graduationProjectJury);
        hash = 23 * hash + Objects.hashCode(this.graduationScoreLetter);
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.graduationScoreValue) ^ (Double.doubleToLongBits(this.graduationScoreValue) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.graduationScoreRank) ^ (Double.doubleToLongBits(this.graduationScoreRank) >>> 32));
        hash = 23 * hash + Objects.hashCode(this.firstJobDate);
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
        if (!Objects.equals(this.graduationProjectSuperviser, other.graduationProjectSuperviser)) {
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
        if (this.firstSalary != other.firstSalary) {
            return false;
        }
        if (this.lastSalary != other.lastSalary) {
            return false;
        }
        if (!Objects.equals(this.lastJobPosition, other.lastJobPosition)) {
            return false;
        }
        if (!Objects.equals(this.lastJobCompany, other.lastJobCompany)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AcademicFormerStudent{" + ", student=" + student + ", graduationDate=" + graduationDate + ", eliminationReason=" + eliminationReason + ", graduationProjectTitle=" + graduationProjectTitle + ", graduationProjectSummary=" + graduationProjectSummary + ", graduationProjectSuperviser=" + graduationProjectSuperviser + ", graduationProjectJury=" + graduationProjectJury + ", graduationScoreLetter=" + graduationScoreLetter + ", graduationScoreValue=" + graduationScoreValue + ", graduationScoreRank=" + graduationScoreRank + ", firstJobDate=" + firstJobDate + ", firstSalary=" + firstSalary + ", lastSalary=" + lastSalary + ", lastJobPosition=" + lastJobPosition + ", lastJobCompany=" + lastJobCompany + '}';
    }

}
