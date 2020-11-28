/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.current;

import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education/Load")
public class AcademicTeacherSemestrialLoad {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    @Summary
    private AppPeriod period;

    @Summary
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private AcademicTeacher teacher;

    @Summary
    private int semester;

    @Summary
    private int weeksLoad;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
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

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }
}
