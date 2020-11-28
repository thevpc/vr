/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.inventory.model;

import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Equipment/Inventory")
public class InventoryUser {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    @Summary
    private Inventory inventory;
    @Summary
    private AppUser user;

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


}
