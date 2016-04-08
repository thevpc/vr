/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.history;

import net.vpc.app.vainruling.api.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseLevel;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 * Unite enseignement
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Education/History")
public class AcademicHistCourseGroup {

    @Id
    @Sequence

    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN})
    private String name;

    private AcademicCourseLevel courseLevel;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AppPeriod academicYear;

    public AcademicHistCourseGroup() {
    }

    public AcademicHistCourseGroup(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AcademicCourseLevel getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(AcademicCourseLevel courseLevel) {
        this.courseLevel = courseLevel;
    }

    @Override
    public String toString() {
        return StringUtils.nonNull(name);
    }

    public AppPeriod getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AppPeriod academicYear) {
        this.academicYear = academicYear;
    }

    
}
