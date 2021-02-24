/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Admin/Security")
@Properties(
        {
            @Property(name = "ui.auto-filter.user", value = "{expr='this.user',order=1}")
            ,
                @Property(name = "ui.auto-filter.profile", value = "{expr='this.profile',order=2}")
        })
public class AppUserProfileBinding {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    @Summary
    @Property(name = UIConstants.Form.COMPOSITION_LIST_FIELD, value = "profile")
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private AppUser user;

    @Summary
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppUserProfileBinding that = (AppUserProfileBinding) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
