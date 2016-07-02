/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.history;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

/**
 * @author vpc
 */
@Entity
@Path("Education/History")
public class AcademicHistTeacherSemestrialLoad {

    @Id
    @Sequence
    private int id;

    @Summary
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicHistTeacherAnnualLoad annualLoad;

    @Summary
    private int semester;

    @Summary
    private int weeksLoad;
    @Summary
    private AppPeriod academicYear;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicHistTeacherAnnualLoad getAnnualLoad() {
        return annualLoad;
    }

    public void setAnnualLoad(AcademicHistTeacherAnnualLoad annualLoad) {
        this.annualLoad = annualLoad;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getWeeksLoad() {
        return weeksLoad;
    }

    public void setWeeksLoad(int weeksLoad) {
        this.weeksLoad = weeksLoad;
    }

    public AppPeriod getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AppPeriod academicYear) {
        this.academicYear = academicYear;
    }

}
