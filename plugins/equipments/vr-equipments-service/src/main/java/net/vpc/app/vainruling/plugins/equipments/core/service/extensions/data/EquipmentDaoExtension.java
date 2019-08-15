/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.service.extensions.data;

import net.vpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.vpc.app.vainruling.plugins.equipments.inventory.model.InventoryStatus;
import net.vpc.app.vainruling.plugins.equipments.inventory.model.InventoryRow;
import net.vpc.app.vainruling.plugins.equipments.inventory.model.Inventory;
import java.util.List;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.upa.Entity;
import net.vpc.upa.events.PersistEvent;
import net.vpc.upa.config.*;


/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class EquipmentDaoExtension {

    @OnPersist
    public void onPersist(PersistEvent event) {
        net.vpc.upa.PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (entity.getName().equals("Equipment")) {
            List<Inventory> currents = pu.createQuery("Select o from Inventory o where o.status <> :closed").setParameter("closed", InventoryStatus.CLOSED)
                    .getResultList();
            Equipment eq = (Equipment) event.getPersistedObject();
            for (Inventory in : currents) {
                List<AppUser> inventoryUsers = pu.createQuery("Select o.user from InventoryUser o where o.inventory.id=:id").setParameter("id", in.getId()).getResultList();
                for (AppUser u : inventoryUsers) {
                    InventoryRow r = new InventoryRow();
                    r.setInventory(in);
                    r.setUser(u);
                    r.setEquipment(eq);
                    r.setExpectedQuantity(eq.getQuantity());
                    r.setArea(eq.getLocation());
                    pu.persist(r);
                }
            }
        }
    }

}
