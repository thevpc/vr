/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.commonmodel.service.model;

import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.config.Hierarchy;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Property;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Admin/Config")
public class AppArea {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN, UserFieldModifier.UNIQUE})
    private String name;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Property(name = UIConstants.FIELD_FORM_CONTROL,value = UIConstants.ControlType.TEXTAREA)
    private String description;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Hierarchy
    private AppArea parent;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AppAreaType type;

    public AppArea() {
    }

    public AppArea(String name, String description, AppAreaType type, AppArea parent) {
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.type = type;
    }

    public AppAreaType getType() {
        return type;
    }

    public void setType(AppAreaType type) {
        this.type = type;
    }

    public AppArea getParent() {
        return parent;
    }

    public void setParent(AppArea parent) {
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
