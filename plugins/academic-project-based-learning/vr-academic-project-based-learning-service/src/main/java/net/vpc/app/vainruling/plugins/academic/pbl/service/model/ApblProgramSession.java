package net.vpc.app.vainruling.plugins.academic.pbl.service.model;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgram;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

import java.util.Date;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Projects/Apbl")
@Properties(
        {
                @Property(name = "ui.auto-filter.session", value = "{expr='this.session',order=1}"),
                @Property(name = "ui.auto-filter.program", value = "{expr='this.program',order=2}"),
        }
)
public class ApblProgramSession {
    @Id
    @Sequence

    private int id;
    @Main
    private String name;
    @ManyToOne(type = RelationshipType.COMPOSITION) @Summary
    private ApblSession session;
    @Summary
    private AcademicProgram program;
    @Summary
    @ManyToOne(filter = "(this.session.semester=null or that.courseLevel.semester.id=this.session.semester.id) and that.courseLevel.academicClass.program.id=this.program.id and that.period.id=this.session.period.id")
    private AcademicCoursePlan course;
    @Summary
    private double load;
    @Summary
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String observations;


    public AcademicCoursePlan getCourse() {
        return course;
    }

    public void setCourse(AcademicCoursePlan course) {
        this.course = course;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
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

    public ApblSession getSession() {
        return session;
    }

    public void setSession(ApblSession session) {
        this.session = session;
    }

    public AcademicProgram getProgram() {
        return program;
    }

    public void setProgram(AcademicProgram program) {
        this.program = program;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
