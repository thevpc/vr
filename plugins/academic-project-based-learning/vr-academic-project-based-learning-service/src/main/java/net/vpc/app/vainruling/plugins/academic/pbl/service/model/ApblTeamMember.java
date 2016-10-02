package net.vpc.app.vainruling.plugins.academic.pbl.service.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Internship")
@Properties(
        {
                @Property(name = "ui.auto-filter.session", value = "{expr='team.session',order=1}"),
                @Property(name = "ui.auto-filter.team", value = "{expr='team',order=2}"),
                @Property(name = "ui.auto-filter.student", value = "{expr='student',order=3}"),
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
