/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.current;

import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.config.*;


/**
 * noyau pedagogique
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.period.name desc, this.fullName, this.name")
@Path("Education/StudyPlan")
@Properties(
        {
            @Property(name = "ui.auto-filter.period", value = "{expr='this.core.period',order=1}"),
            @Property(name = "ui.auto-filter.department", value = "{expr='this.core.program.department',order=2}"),
            @Property(name = "ui.auto-filter.program", value = "{expr='this.program',order=3}"),
            @Property(name = "ui.auto-filter.programType", value = "{expr='this.program.programType',order=4}"),
            @Property(name = "ui.auto-filter.teacher", value = "{expr='this.teacher',order=7}")
        }
)
public class AcademicCourseCoreTeacher {

    @Path("Main")
    @Id
    @Sequence

    private int id;

    private AcademicCourseCore core;
    private AcademicTeacher teacher;

   

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AcademicCourseCoreTeacher{" + "core=" + core
                + ", teacher=" + teacher + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AcademicCourseCoreTeacher that = (AcademicCourseCoreTeacher) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public AcademicCourseCore getCore() {
        return core;
    }

    public void setCore(AcademicCourseCore core) {
        this.core = core;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }
    
    

}
