/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.ManyToOne;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity
@Path("Admin/Security")
public class AppUserProfileBinding {

    @Id
    @Sequence
    private int id;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AppUser user;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AppProfile profile;

    public AppUserProfileBinding() {
    }

    public AppUserProfileBinding(AppUser user, AppProfile profile) {
        this.user = user;
        this.profile = profile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public AppProfile getProfile() {
        return profile;
    }

    public void setProfile(AppProfile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return String.valueOf(user)+";"+String.valueOf(profile);
    }
}
