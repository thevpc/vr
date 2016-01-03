/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "propertyName")
@Path("Admin")
public class AppProperty {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN}, max = "512")
    private String propertyName;
    @Field(/*modifiers = {UserFieldModifier.SUMMARY},*/ max = "4096")
    private String propertyValue;
    private String propertyType;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AppUser user;
    private boolean enabled = true;

    public AppProperty() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
