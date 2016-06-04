/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

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
@Entity(listOrder = "name")
@Path("Equipment/Config")
public class EquipmentType {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private EquipmentTypeGroup typeGroup;

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

    public EquipmentTypeGroup getTypeGroup() {
        return typeGroup;
    }

    public void setTypeGroup(EquipmentTypeGroup typeGroup) {
        this.typeGroup = typeGroup;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
