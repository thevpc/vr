/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.config;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "period.name desc")
@Path("Contact")
@Properties(
        @Property(name = "cache.navigationDepth", type = "int", value = "1")
)
public class AcademicTeacherPeriod {

    @Id
    @Sequence

    private int id;
    @Main
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AppPeriod period;

    @Summary @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicTeacher teacher;

    @Summary
    private AcademicTeacherDegree degree;

    @Summary
    private AcademicTeacherSituation situation;
    @Summary
    private AppDepartment department;
    @Summary
    @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    private boolean enabled = true;

    @Summary
    @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    private boolean loadConfirmed;

    public AcademicTeacherPeriod() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public AcademicTeacherDegree getDegree() {
        return degree;
    }

    public void setDegree(AcademicTeacherDegree degree) {
        this.degree = degree;
    }

    public AcademicTeacherSituation getSituation() {
        return situation;
    }

    public void setSituation(AcademicTeacherSituation situation) {
        this.situation = situation;
    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLoadConfirmed() {
        return loadConfirmed;
    }

    public void setLoadConfirmed(boolean loadConfirmed) {
        this.loadConfirmed = loadConfirmed;
    }
}
