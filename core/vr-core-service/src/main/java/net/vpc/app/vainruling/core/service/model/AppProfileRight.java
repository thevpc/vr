/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

/**
 * @author vpc
 */
@Entity
@Path("Admin/Security")
public class AppProfileRight {

    @Id
    @Sequence
    private int id;

    @Field(modifiers = {UserFieldModifier.MAIN})
    private AppRightName right;

    @Field(modifiers = {UserFieldModifier.SUMMARY})
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AppProfile profile;

    @Field(max = "4000")
    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.TEXTAREA)
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppProfile getProfile() {
        return profile;
    }

    public void setProfile(AppProfile profile) {
        this.profile = profile;
    }

    public AppRightName getRight() {
        return right;
    }

    public void setRight(AppRightName right) {
        this.right = right;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
