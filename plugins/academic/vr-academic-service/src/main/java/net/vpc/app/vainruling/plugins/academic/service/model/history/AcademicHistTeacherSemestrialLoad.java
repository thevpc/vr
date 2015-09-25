/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.history;

import net.vpc.app.vainruling.api.model.AppPeriod;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.ManyToOne;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity
@Path("Education/History")
public class AcademicHistTeacherSemestrialLoad {

    @Id
    @Sequence
    private int id;

    @Field(modifiers = {UserFieldModifier.SUMMARY})
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicHistTeacherAnnualLoad annualLoad;

    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private int semester;

    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private int weeksLoad;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
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
