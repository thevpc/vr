package net.vpc.app.vainruling.plugins.academic.pbl.model;

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
                @Property(name = "ui.auto-filter.session", value = "{expr='this.coaching.team.session',order=1}"),
                @Property(name = "ui.auto-filter.teacher", value = "{expr='this.coaching.teacher',order=2}"),
                @Property(name = "ui.auto-filter.team", value = "{expr='this.coaching.team',order=3}"),
        }
)
public class ApblCoachingLog {
    @Id
    @Sequence
    private int id;
    @Main
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private ApblCoaching coaching;
    @Summary
    private Date appointmentDate;
    @Summary
    private int durationMinutes;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "4000")
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ApblCoaching getCoaching() {
        return coaching;
    }

    public void setCoaching(ApblCoaching coaching) {
        this.coaching = coaching;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
