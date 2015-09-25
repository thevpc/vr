/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inventory.service.model;

import java.util.Date;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.config.ToString;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "startDate desc, name")
@Path("Equipment/Inventory")
public class Inventory {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN, UserFieldModifier.UNIQUE})
    private String name;
    private Date startDate;
    private Date endDate;
    @ToString
    private InventoryStatus status = InventoryStatus.CONFIG;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

}
