package net.vpc.app.vainruling.plugins.academic.pbl.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Projects/Apbl")
@Properties(
        {
                @Property(name = "ui.auto-filter.session", value = "{expr='this.team.session',order=1}"),
                @Property(name = "ui.auto-filter.teacher", value = "{expr='this.teacher',order=2}"),
                @Property(name = "ui.auto-filter.team", value = "{expr='this.team',order=3}"),
        }
)
public class ApblCoaching {
    @Id
    @Sequence
    private int id;
    @Main
    private AcademicTeacher teacher;
    @Summary
    private ApblTeam team;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "4000")
    private String description;

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

    public ApblTeam getTeam() {
        return team;
    }

    public void setTeam(ApblTeam team) {
        this.team = team;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
