/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Admin/Security")
public class AppUserProfileBinding {

    @Id
    @Sequence
    private int id;

    @Summary
    private AppUser user;

    @Summary
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
        return String.valueOf(user) + ";" + String.valueOf(profile);
    }
}
