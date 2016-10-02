package net.vpc.app.vainruling.plugins.academic.pbl.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;
import net.vpc.upa.types.DateTime;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Internship")
@Properties(
        {
                @Property(name = "ui.auto-filter.session", value = "{expr='coaching.team.session',order=1}"),
                @Property(name = "ui.auto-filter.teacher", value = "{expr='coaching.teacher',order=2}"),
                @Property(name = "ui.auto-filter.team", value = "{expr='coaching.team',order=3}"),
        }
)
public class ApblCoachingLog {
    @Id
    @Sequence
    private int id;
    @Main
    private ApblCoaching coaching;
    @Summary
    private DateTime appointmentDate;
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

    public DateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(DateTime appointmentDate) {
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
