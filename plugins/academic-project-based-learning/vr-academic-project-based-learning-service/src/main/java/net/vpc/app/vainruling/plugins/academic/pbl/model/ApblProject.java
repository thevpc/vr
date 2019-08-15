package net.vpc.app.vainruling.plugins.academic.pbl.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
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
            @Property(name = "ui.auto-filter.period", value = "{expr='this.session.period',order=1}")
            ,
                @Property(name = "ui.auto-filter.session", value = "{expr='this.session',order=2}")
            ,
                @Property(name = "ui.auto-filter.owner", value = "{expr='this.owner',order=3}"),}
)
public class ApblProject {

    @Id
    @Sequence
    private int id;

    @Formula("concat('P',this.id)")
    @Summary
    private String code;

    @Main
    private String name;

    @Summary
    private ApblSession session;

    @Summary
    private AppUser owner;

    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    @Field(max = "maximum")
    private String description;

    @Properties({
        //            @Property(name = UIConstants.Form.SEPARATOR, value = "Specifications"),
        @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE)
        ,
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    @Path("Specifications")
    private String specFilePath;

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

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecFilePath() {
        return specFilePath;
    }

    public void setSpecFilePath(String specFilePath) {
        this.specFilePath = specFilePath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
