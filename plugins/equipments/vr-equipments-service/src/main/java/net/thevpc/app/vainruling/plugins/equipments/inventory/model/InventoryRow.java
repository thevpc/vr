/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.inventory.model;

import net.thevpc.app.vainruling.core.service.model.AppArea;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.FormulaType;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;

import java.util.Date;

import net.thevpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.thevpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusType;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.date desc")
@Path("Equipment/Inventory")
public class InventoryRow {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    @Summary
    private Inventory inventory;
    @Summary
    private AppUser user;
    @Summary
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Date date;
    @Summary
    private Equipment equipment;
    @Summary
    private AppArea area;
    @Summary
    private double expectedQuantity;
    @Summary
    private Double quantity1;
    @Summary
    private Double quantity2;
    @Summary
    private Double quantity3;

    @Summary
    @ToString
    @Property(name = UIConstants.Grid.COLUMN_STYLE_CLASS, value = "#{hashCssColor(this.statusType)}")
    private EquipmentStatusType statusType = EquipmentStatusType.AVAILABLE;

    @Summary
    private AppUser statusUser = null;

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

    public EquipmentStatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(EquipmentStatusType statusType) {
        this.statusType = statusType;
    }

    public AppUser getStatusUser() {
        return statusUser;
    }

    public void setStatusUser(AppUser statusUser) {
        this.statusUser = statusUser;
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
