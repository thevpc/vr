/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Admin/Security")
public class AppProfileRight {

    @Id
    @Sequence
    private int id;

    @Main
    @Field(nullable = BoolEnum.FALSE)
    private AppRightName right;

    @Summary
    @ManyToOne(type = RelationshipType.COMPOSITION)
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

}
