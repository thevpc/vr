package net.vpc.app.vainruling.plugins.academic.pbl.service.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Projects/Apbl")
@Properties(
        {
                @Property(name = "ui.auto-filter.department", value = "{expr='this.student.department',order=1}"),
                @Property(name = "ui.auto-filter.session", value = "{expr='this.team.session',order=2}"),
                @Property(name = "ui.auto-filter.team", value = "{expr='this.team',order=3}"),
                @Property(name = "ui.auto-filter.student", value = "{expr='this.student',order=4}"),
                @Property(name = "ui.auto-filter.program", value = "{expr='this.student.lastClass1.program',order=5}"),
                @Property(name = "ui.auto-filter.lastClass1", value = "{expr='this.student.lastClass1',order=5}"),
        }
)
public class ApblTeamMember {
    @Id
    @Sequence
    private int id;
    @Main
    private AcademicStudent student;
    @Summary
    private ApblTeam team;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicStudent student) {
        this.student = student;
    }

    public ApblTeam getTeam() {
        return team;
    }

    public void setTeam(ApblTeam team) {
        this.team = team;
    }

}
