/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inventory.service.model;

import java.util.Date;
import net.vpc.app.vainruling.plugins.commonmodel.service.model.AppArea;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.plugins.equipments.service.model.Equipment;
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
@Entity(listOrder = "date desc")
@Path("Equipment/Inventory")
public class InventoryRow {

    @Id @Sequence
    private int id;
    @ManyToOne(type = RelationshipType.COMPOSITION)
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Inventory inventory;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AppUser user;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Date date;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Equipment equipment;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AppArea area;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double expectedQuantity;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Double quantity1;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Double quantity2;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Double quantity3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public AppArea getArea() {
        return area;
    }

    public void setArea(AppArea area) {
        this.area = area;
    }

    public Double getQuantity1() {
        return quantity1;
    }

    public void setQuantity1(Double quantity1) {
        this.quantity1 = quantity1;
    }

    public Double getQuantity2() {
        return quantity2;
    }

    public void setQuantity2(Double quantity2) {
        this.quantity2 = quantity2;
    }

    public Double getQuantity3() {
        return quantity3;
    }

    public void setQuantity3(Double quantity3) {
        this.quantity3 = quantity3;
    }

    public double getExpectedQuantity() {
        return expectedQuantity;
    }

    public void setExpectedQuantity(double expectedQuantity) {
        this.expectedQuantity = expectedQuantity;
    }
    

}
