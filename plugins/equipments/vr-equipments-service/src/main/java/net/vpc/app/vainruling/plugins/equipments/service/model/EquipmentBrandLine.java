/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Equipment/Config")
@Property(name = UIConstants.ENTITY_ID_HIERARCHY,value = "brand")
public class EquipmentBrandLine {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private EquipmentBrand brand;

    public int getId() {
        return id;
    }

    public EquipmentBrand getBrand() {
        return brand;
    }

    public void setBrand(EquipmentBrand brand) {
        this.brand = brand;
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

}
