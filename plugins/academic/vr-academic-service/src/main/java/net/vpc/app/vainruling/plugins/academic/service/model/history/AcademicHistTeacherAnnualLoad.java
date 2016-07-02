/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.history;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.util.Objects;

/**
 * @author vpc
 */
@Entity
@Path("Education/History")
public class AcademicHistTeacherAnnualLoad {

    @Id
    @Sequence

    private int id;

    @Summary
    private AcademicTeacher teacher;

    @Summary
    private AcademicHistTeacherDegree degree;

    @Summary
    private AcademicTeacherSituation situation;

    @Summary
    private AppPeriod academicYear;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicHistTeacherDegree getDegree() {
        return degree;
    }

    public void setDegree(AcademicHistTeacherDegree degree) {
        this.degree = degree;
    }

    public AcademicTeacherSituation getSituation() {
        return situation;
    }

    public void setSituation(AcademicTeacherSituation situation) {
        this.situation = situation;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public AppPeriod getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AppPeriod academicYear) {
        this.academicYear = academicYear;
    }

    @Override
    public String toString() {
        return "Teacher{" + "id=" + id + ", teacher=" + teacher + ", degree=" + degree + ", situation=" + situation + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.id;
        hash = 31 * hash + Objects.hashCode(this.teacher);
        hash = 31 * hash + Objects.hashCode(this.degree);
        hash = 31 * hash + Objects.hashCode(this.situation);
        hash = 31 * hash + Objects.hashCode(this.academicYear);
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
        final AcademicHistTeacherAnnualLoad other = (AcademicHistTeacherAnnualLoad) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.teacher, other.teacher)) {
            return false;
        }
        if (!Objects.equals(this.degree, other.degree)) {
            return false;
        }
        if (!Objects.equals(this.situation, other.situation)) {
            return false;
        }
        if (!Objects.equals(this.academicYear, other.academicYear)) {
            return false;
        }
        return true;
    }


}
