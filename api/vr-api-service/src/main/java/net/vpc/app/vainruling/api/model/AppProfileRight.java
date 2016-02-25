/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.model;

import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.ManyToOne;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity
@Path("Admin/Security")
public class AppProfileRight {

    @Id @Sequence
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

    public AppRightName getRight() {
        return right;
    }

    public void setRight(AppRightName right) {
        this.right = right;
    }

    public void setProfile(AppProfile profile) {
        this.profile = profile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
