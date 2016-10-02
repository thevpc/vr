package net.vpc.app.vainruling.plugins.academic.pbl.service.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Internship")
@Properties(
        {
                @Property(name = "ui.auto-filter.session", value = "{expr='session',order=1}"),
        }
)
public class ApblTeam {
    @Id
    @Sequence
    private int id;
    @Main
    private String name;

    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "4000")
    private String description;

    @Summary
    private ApblSession session;

    @Summary
    private ApblProject project;

    /**
     * owner may be student or teacher
     */
    @Summary
    private AppUser owner;

    @Properties({
            @Property(name = UIConstants.Form.SEPARATOR, value = "Evaluation"),
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    private String report;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public ApblProject getProject() {
        return project;
    }

    public void setProject(ApblProject project) {
        this.project = project;
    }
}
