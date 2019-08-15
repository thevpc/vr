package net.vpc.app.vr.plugins.academicprofile.model;

import net.vpc.app.vainruling.core.service.model.AppSkill;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 7/19/17.
 */
@Entity
@Path("Education")
public class AcademicTeacherSkill {
    @Id
    @Sequence
    private int id;
    @Summary
    private AcademicTeacher teacher;
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

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }
}
