package net.vpc.app.vainruling.plugins.academic.pbl.service.model;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Internship")
@Properties(
        {
                @Property(name = "ui.auto-filter.session", value = "{expr='session',order=1}"),
                @Property(name = "ui.auto-filter.teacher", value = "{expr='teacher',order=2}"),
        }
)
public class ApblSessionManager {
    @Id
    @Sequence
    private int id;
    @Main
    private AcademicTeacher teacher;
    @Summary
    private ApblSession session;

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

    public ApblSession getSession() {
        return session;
    }

    public void setSession(ApblSession session) {
        this.session = session;
    }

}
