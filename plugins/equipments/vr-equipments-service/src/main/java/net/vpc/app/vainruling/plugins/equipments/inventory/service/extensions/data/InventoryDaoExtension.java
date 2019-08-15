/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.inventory.service.extensions.data;

import net.vpc.app.vainruling.plugins.equipments.inventory.model.InventoryUser;
import net.vpc.app.vainruling.plugins.equipments.inventory.model.Inventory;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.events.PersistEvent;
import net.vpc.upa.config.*;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class InventoryDaoExtension {

    @OnPersist
    public void onPersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (entity.getName().equals("Inventory")) {
            List<AppUser> inventoryUsers = pu.createQuery("Select o.user from AppUserProfileBinding o where o.profile.name=:name").setParameter("name", "inventory").getResultList();
            for (AppUser u : inventoryUsers) {
                InventoryUser r = new InventoryUser();
                r.setInventory((Inventory) event.getPersistedObject());
                r.setUser(u);
                pu.persist(r);
            }
        }
    }
}
