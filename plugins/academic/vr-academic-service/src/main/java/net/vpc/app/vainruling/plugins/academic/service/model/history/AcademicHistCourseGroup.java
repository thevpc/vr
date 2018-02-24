/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.history;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.config.*;

/**
 * Unite enseignement
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/History")
public class AcademicHistCourseGroup {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private String name;

    private AcademicClass academicClass;
    @Summary
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

    public AcademicClass getAcademicClass() {
        return academicClass;
    }

    public void setAcademicClass(AcademicClass academicClass) {
        this.academicClass = academicClass;
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
