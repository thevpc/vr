/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.internship.current;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.AcademicInternshipType;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgram;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/Projects/Internships")
@Properties(
        {
                @Property(name = "ui.auto-filter.period", value = "{expr='this.period',order=1}"),
                @Property(name = "ui.auto-filter.department", value = "{expr='this.department',order=2}"),
                @Property(name = "ui.auto-filter.program", value = "{expr='this.program',order=3}"),
                @Property(name = "ui.auto-filter.academicClass", value = "{expr='this.academicClass',order=4}"),
                @Property(name = "ui.auto-filter.internshipType", value = "{expr='this.internshipType',order=5}"),
        })
public class AcademicInternshipBoard {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private String name;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String observations;
    private boolean enabled;
    @Summary
    private AppPeriod period;

    @Summary
    private AppDepartment department;

    @Summary
    private AcademicProgram program;

    @Summary
    private AcademicClass academicClass;

    @Summary
    private AcademicInternshipType internshipType;
    @Field(defaultValue = "true")
    private boolean multipleSupervisors;

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

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }

    public AcademicProgram getProgram() {
        return program;
    }

    public void setProgram(AcademicProgram program) {
        this.program = program;
    }

    public AcademicClass getAcademicClass() {
        return academicClass;
    }

    public void setAcademicClass(AcademicClass academicClass) {
        this.academicClass = academicClass;
    }

    public AcademicInternshipType getInternshipType() {
        return internshipType;
    }

    public void setInternshipType(AcademicInternshipType internshipType) {
        this.internshipType = internshipType;
    }

    public boolean isMultipleSupervisors() {
        return multipleSupervisors;
    }

    public void setMultipleSupervisors(boolean multipleSupervisors) {
        this.multipleSupervisors = multipleSupervisors;
    }

}
