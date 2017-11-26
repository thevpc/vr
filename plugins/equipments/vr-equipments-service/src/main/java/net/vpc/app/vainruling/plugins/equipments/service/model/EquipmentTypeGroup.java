/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Equipment/Config")
public class EquipmentTypeGroup {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    @Hierarchy @Summary
    private EquipmentTypeGroup parent;

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

    public EquipmentTypeGroup getParent() {
        return parent;
    }

    public void setParent(EquipmentTypeGroup parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
