package net.vpc.app.vainruling.plugins.academic.pbl.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Projects/Apbl")
@Properties(
        {
                @Property(name = "ui.auto-filter.session", value = "{expr='this.session',order=1}")
        }
)
public class ApblTeamConstraint {
    @Id
    @Sequence

    private int id;
    @Main
    private String name;
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private ApblSession session;


    @Properties(
            {
                    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.PROFILE_EXPRESSION)
                    , @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private String profiles;

    private int minCount;
    private int maxCount;

    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "4000")
    private String description;

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

    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    public int getMinCount() {
        return minCount;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
