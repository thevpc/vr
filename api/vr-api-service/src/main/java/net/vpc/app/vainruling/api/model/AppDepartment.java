/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.model;

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
public class AppDepartment {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.UNIQUE})
    private String code;
    @Field(modifiers = {UserFieldModifier.MAIN})
    private String name;
    private String name2;
    private String name3;
    @Hierarchy
    private AppDepartment parent;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.TEXTAREA)
    private String description;

    public AppDepartment getParent() {
        return parent;
    }

    public void setParent(AppDepartment parent) {
        this.parent = parent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

}
