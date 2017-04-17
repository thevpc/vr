/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.equipments.service.model.*;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPersist;
import net.vpc.upa.config.OnPreUpdate;
import net.vpc.upa.config.OnUpdate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class InventoryPluginCallback {

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
        } else if (entity.getName().equals("InventoryUser")) {
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

        } else if (entity.getName().equals("Equipment")) {
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
        }else if (entity.getName().equals("EquipmentStatusLog")) {
            EquipmentStatusLog eq = (EquipmentStatusLog) event.getPersistedObject();
            if(eq.getEquipment()!=null){
                eq.getEquipment().setStatusType(eq.getType());
                pu.merge(eq.getEquipment());
            }
        }
    }
    @OnPreUpdate
    public void onPreUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (entity.getName().equals("EquipmentStatusLog")) {
            EquipmentStatusLog e = (EquipmentStatusLog) event.getUpdatesObject();
            if(e!=null && e.getEquipment()!=null){
                event.getContext().setObject("EquipmentStatusLog.Updated",pu.createQueryBuilder("EquipmentStatusLog").byExpression(event.getFilterExpression()).getIdList());
            }else{
                event.getContext().setObject("EquipmentStatusLog.Updated",new ArrayList<>());
            }
        }
    }
    @OnUpdate
    public void onUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (entity.getName().equals("EquipmentStatusLog")) {
            List ids=event.getContext().getObject("EquipmentStatusLog.Updated");
            for (Object id : ids) {
                List<EquipmentStatusLog> currents = pu.createQuery("Select Top 1 o from EquipmentStatusLog o where o.equipmentId=(Select x.equipmentId from EquipmentStatusLog x where x.id=:id) order by o.startDate desc, o.id asc")
                        .setParameter("id", id)
                        .getResultList();
                for (EquipmentStatusLog current : currents) {
                    current.getEquipment().setStatusType(current.getType());
                    pu.merge(current.getEquipment());
                }
            }
        }
    }

}
