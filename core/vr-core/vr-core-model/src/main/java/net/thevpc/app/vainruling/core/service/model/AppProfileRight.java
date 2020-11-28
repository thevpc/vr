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
@Path("/Repository/Security")
public class AppProfileRight {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    @Main
    @Field(nullable = BoolEnum.FALSE)
    private AppRightName right;

    @Summary
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    @Field(nullable = BoolEnum.FALSE)
    private AppProfile profile;

    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppProfileRight that = (AppProfileRight) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
