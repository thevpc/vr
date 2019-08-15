/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.service.extensions.data;

import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.vpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusLog;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusType;
import net.vpc.upa.Document;
import net.vpc.upa.config.*;
import net.vpc.upa.events.PersistEvent;
import net.vpc.upa.events.UpdateEvent;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class EquipmentStatusLogDaoExtension {

//    @OnPrePersist
//    public void onPrePersist(PersistEvent event) {
//        net.vpc.upa.Entity entity = event.getEntity();
//        if (entity.getName().equals("EquipmentStatusLog")) {
//            EquipmentStatusLog eq = (EquipmentStatusLog) event.getPersistedObject();
//            EquipmentStatusType type = eq.getType();
//            eq.setActor(CorePlugin.get().getCurrentUser());
//            if (type != null) {
//                if (type.getSign() == 0) {
//                    eq.setQuantity(0);
//                    event.getPersistedDocument().setDouble("quantity", eq.getQuantity());
//                } else if (type.getSign() * eq.getQuantity() < 0) {
//                    eq.setQuantity(-eq.getQuantity());
//                    event.getPersistedDocument().setDouble("quantity", eq.getQuantity());
//                }
//            }
////            if (eq.getEquipment() != null && eq.getEquipment().isBorrowable() && eq.getType() == EquipmentStatusType.AVAILABLE) {
////                eq.setType(EquipmentStatusType.BORROWABLE);
////            }
//            if (eq.getType() == EquipmentStatusType.AVAILABLE) {
//                eq.setResponsible(null);
//            }
//            if (eq.getType() == EquipmentStatusType.BORROWED && eq.getResponsible() == null) {
//                throw new IllegalArgumentException("Missing Responsible");
//            }
//        }
//    }
//
//    @OnPersist
//    public void onPersist(PersistEvent event) {
//        net.vpc.upa.PersistenceUnit pu = event.getPersistenceUnit();
//        net.vpc.upa.Entity entity = event.getEntity();
//        if (entity.getName().equals("EquipmentStatusLog")) {
//            EquipmentStatusLog eq = (EquipmentStatusLog) event.getPersistedObject();
//            if (eq.getEquipment() != null) {
//                eq.getEquipment().setStatusType(eq.getType());
//                if (eq.getAcquisition() != null && eq.getType() == EquipmentStatusType.ACQUISITION) {
//                    eq.getEquipment().setAcquisition(eq.getAcquisition());
//                }
//                eq.getEquipment().setActor(eq.getActor());
//                eq.getEquipment().setResponsible(eq.getResponsible());
//                eq.getEquipment().setLogStartDate(eq.getStartDate());
//                eq.getEquipment().setLogEndDate(eq.getEndDate());
//                pu.merge(eq.getEquipment());
//            }
//        }
//    }
//
//    @OnPreUpdate
//    public void onPreUpdate(UpdateEvent event) {
//        net.vpc.upa.PersistenceUnit pu = event.getPersistenceUnit();
//        net.vpc.upa.Entity entity = event.getEntity();
//        if (entity.getName().equals("EquipmentStatusLog")) {
//            Document updatesDocument = event.getUpdatesDocument();
//            if (updatesDocument != null && updatesDocument.isSet("type") && updatesDocument.isSet("quantity")) {
//                EquipmentStatusType type = updatesDocument.get("type");
//                if (type != null) {
//                    if (type.getSign() == 0) {
//                        updatesDocument.setDouble("quantity", 0.0);
//                    } else if (type.getSign() * updatesDocument.getDouble("quantity", 0) < 0) {
//                        double quantity = -updatesDocument.getDouble("quantity", 0);
//                        updatesDocument.setDouble("quantity", quantity);
//                    }
//                }
//            }
//            if (updatesDocument != null && updatesDocument.isSet("equipment")) {
//                event.getContext().setObject("EquipmentStatusLog.Updated", pu.createQueryBuilder("EquipmentStatusLog").byExpression(event.getFilterExpression()).getIdList());
//            } else {
//                event.getContext().setObject("EquipmentStatusLog.Updated", new ArrayList<>());
//            }
//        }
//    }
//
//     @OnUpdate
//    public void onUpdate(UpdateEvent event) {
//        net.vpc.upa.PersistenceUnit pu = event.getPersistenceUnit();
//        net.vpc.upa.Entity entity = event.getEntity();
//        if (entity.getName().equals("EquipmentStatusLog")) {
//            List ids = event.getContext().getObject("EquipmentStatusLog.Updated");
//            for (Object id : ids) {
//                EquipmentStatusLog llog = pu.findById(EquipmentStatusLog.class, id);
//                if (llog != null) {
//                    EquipmentStatusLog latest = llog.getEquipment() == null ? null : VrApp.getBean(EquipmentPlugin.class).findEquipmentLatestLog(llog.getEquipment().getId());
//                    if (latest != null) {
//                        final Equipment equipment = latest.getEquipment();
//                        equipment.setStatusType(latest.getType());
//                        equipment.setActor(latest.getActor());
//                        equipment.setResponsible(latest.getResponsible());
//                        equipment.setLogStartDate(latest.getStartDate());
//                        equipment.setLogEndDate(latest.getEndDate());
//                        pu.merge(equipment);
//                    }
//                }
//            }
//        }
//    }
}
