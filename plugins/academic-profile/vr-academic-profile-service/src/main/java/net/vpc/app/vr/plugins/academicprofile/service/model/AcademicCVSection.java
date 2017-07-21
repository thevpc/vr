package net.vpc.app.vr.plugins.academicprofile.service.model;

import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;
import net.vpc.upa.types.Date;

/**
 * Created by vpc on 7/19/17.
 */
@Entity
@Path("Admin/Config")
public class AcademicCVSection {

    @Id
    @Sequence
    private int id;
    @Main
    private String title;

    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String details;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
