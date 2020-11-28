/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.inventory.service.extensions.data;

import net.thevpc.app.vainruling.plugins.equipments.inventory.model.InventoryRow;
import net.thevpc.app.vainruling.plugins.equipments.inventory.model.InventoryUser;
import net.thevpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.thevpc.upa.Entity;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.events.PersistEvent;
import net.thevpc.upa.config.*;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class InventoryUserDaoExtension {

    @OnPersist
    public void onPersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (entity.getName().equals("InventoryUser")) {
            InventoryUser eu = (InventoryUser) event.getPersistedObject();
            List<Equipment> equipments = pu.createQuery("Select o from Equipment o").getResultList();
            for (Equipment equipment : equipments) {
                InventoryRow r = new InventoryRow();
                r.setInventory(eu.getInventory());
                r.setUser(eu.getUser());
                r.setEquipment(equipment);
                r.setExpectedQuantity(equipment.getQuantity());
                r.setArea(equipment.getLocation());
                pu.persist(r);
            }

        }
    }
}
