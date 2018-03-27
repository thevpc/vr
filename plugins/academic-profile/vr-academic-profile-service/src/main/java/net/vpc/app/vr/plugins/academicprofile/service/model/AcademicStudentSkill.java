package net.vpc.app.vr.plugins.academicprofile.service.model;

import net.vpc.app.vainruling.core.service.model.AppSkill;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 7/19/17.
 */
@Entity
@Path("Education")
public class AcademicStudentSkill {
    @Id
    @Sequence
    private int id;
    @Summary
    private AcademicStudent student;
    @Main @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private AppSkill skill;
    @Summary
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.RATING))
    private int level;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppSkill getSkill() {
        return skill;
    }

    public void setSkill(AppSkill skill) {
        this.skill = skill;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public AcademicStudent getStudent() {
        return student;
    }

    public void setStudent(AcademicStudent student) {
        this.student = student;
    }
}
