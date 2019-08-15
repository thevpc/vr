package net.vpc.app.vainruling.plugins.academic.pbl.model;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.pbl.service.dto.ApblSessionLoadStrategy;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.vpc.upa.config.*;

import java.util.Date;

/**
 * Created by vpc on 9/19/16.
 */
@Entity(listOrder = "this.startDate desc,this.name asc")
@Path("Education/Projects/Apbl")
@Properties(
        {
                @Property(name = "ui.auto-filter.period", value = "{expr='this.period',order=1}"),
                @Property(name = "ui.auto-filter.status", value = "{expr='this.status',order=2}"),
                @Property(name = "ui.auto-filter.semester", value = "{expr='this.semester',order=3}"),
        }
)
public class ApblSession {
    @Id
    @Sequence

    private int id;
    @Main
    @Unique
    private String name;
    @Summary
    private AppPeriod period;
    @Summary
    private AcademicSemester semester;
    @Summary
    private ApblSessionStatus status;
    @Summary
    private java.util.Date startDate;
    @Summary
    private java.util.Date endDate;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "4000")
    private String description;
    @Properties(
            {
                    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.PROFILE_EXPRESSION)
                    , @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private String teamOwnerProfiles;
    @Properties(
            {
                    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.PROFILE_EXPRESSION)
                    , @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private String memberProfiles;
    @Properties(
            {
                    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.PROFILE_EXPRESSION)
                    , @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private String projectOwnerProfiles;
    @Properties(
            {
                    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.PROFILE_EXPRESSION)
                    , @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private String coachProfiles;
    private int teamMemberMin;
    private int teamMemberMax;
    private int teamCoachMin;
    private int teamCoachMax;
    /**
     * if teamsBasedLoad only students in teams are considered otherwise all students in memberProfiles are
     */
    private ApblSessionLoadStrategy loadStrategy;
    private String strategyConfig;

    public ApblSessionLoadStrategy getLoadStrategy() {
        return loadStrategy;
    }

    public void setLoadStrategy(ApblSessionLoadStrategy loadStrategy) {
        this.loadStrategy = loadStrategy;
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

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }

    public ApblSessionStatus getStatus() {
        return status;
    }

    public void setStatus(ApblSessionStatus status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTeamMemberMin() {
        return teamMemberMin;
    }

    public void setTeamMemberMin(int teamMemberMin) {
        this.teamMemberMin = teamMemberMin;
    }

    public int getTeamMemberMax() {
        return teamMemberMax;
    }

    public void setTeamMemberMax(int teamMemberMax) {
        this.teamMemberMax = teamMemberMax;
    }

    public int getTeamCoachMin() {
        return teamCoachMin;
    }

    public void setTeamCoachMin(int teamCoachMin) {
        this.teamCoachMin = teamCoachMin;
    }

    public int getTeamCoachMax() {
        return teamCoachMax;
    }

    public void setTeamCoachMax(int teamCoachMax) {
        this.teamCoachMax = teamCoachMax;
    }

    public String getCoachProfiles() {
        return coachProfiles;
    }

    public void setCoachProfiles(String coachProfiles) {
        this.coachProfiles = coachProfiles;
    }

    public String getMemberProfiles() {
        return memberProfiles;
    }

    public void setMemberProfiles(String memberProfiles) {
        this.memberProfiles = memberProfiles;
    }

    public String getTeamOwnerProfiles() {
        return teamOwnerProfiles;
    }

    public void setTeamOwnerProfiles(String teamOwnerProfiles) {
        this.teamOwnerProfiles = teamOwnerProfiles;
    }

    public String getProjectOwnerProfiles() {
        return projectOwnerProfiles;
    }

    public void setProjectOwnerProfiles(String projectOwnerProfiles) {
        this.projectOwnerProfiles = projectOwnerProfiles;
    }

    public AcademicSemester getSemester() {
        return semester;
    }

    public void setSemester(AcademicSemester semester) {
        this.semester = semester;
    }

    public String getStrategyConfig() {
        return strategyConfig;
    }

    public void setStrategyConfig(String strategyConfig) {
        this.strategyConfig = strategyConfig;
    }
}
