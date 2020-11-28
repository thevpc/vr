package net.thevpc.app.vainruling.plugins.academic.pbl.model;

import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;

/**
 * Created by vpc on 9/19/16.
 */
@Entity
@Path("Education/Projects/Apbl")
@Properties(
        {
                @Property(name = "ui.auto-filter.session", value = "{expr='this.session',order=1}"),
                @Property(name = "ui.auto-filter.teacher", value = "{expr='this.teacher',order=2}"),
        }
)
public class ApblSessionManager {
    @Id
    @Sequence
    private int id;
    @Main
    private AcademicTeacher teacher;
    @Summary
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
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
