package net.vpc.app.vainruling.plugins.academic.pbl.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

import java.util.Date;
import net.vpc.upa.RelationshipType;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Projects/Apbl")
@Properties(
        {
                @Property(name = "ui.auto-filter.session", value = "{expr='this.team.session',order=1}"),
                @Property(name = "ui.auto-filter.team", value = "{expr='this.team',order=3}"),
        }
)
public class ApblProgressionLog {
    @Id
    @Sequence
    private int id;
    @Main
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private ApblTeam team;
    private AppUser user;
    @Summary
    private Date progressionDate;
    @Summary
    private int progressionPercent;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "4000")
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApblTeam getTeam() {
        return team;
    }

    public void setTeam(ApblTeam team) {
        this.team = team;
    }

    public Date getProgressionDate() {
        return progressionDate;
    }

    public void setProgressionDate(Date progressionDate) {
        this.progressionDate = progressionDate;
    }

    public int getProgressionPercent() {
        return progressionPercent;
    }

    public void setProgressionPercent(int progressionPercent) {
        this.progressionPercent = progressionPercent;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }
}
