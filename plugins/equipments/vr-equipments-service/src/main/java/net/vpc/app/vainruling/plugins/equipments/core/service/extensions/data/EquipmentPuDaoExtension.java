/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.service.extensions.data;

import net.vpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusLog;
import java.util.List;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.vpc.upa.UPA;
import net.vpc.upa.config.*;
import net.vpc.upa.events.PersistenceUnitEvent;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class EquipmentPuDaoExtension {

    @OnPreUpdateFormula
    public void onPreUpdateFormulas(PersistenceUnitEvent event) {
        net.vpc.upa.PersistenceUnit pu = event.getPersistenceUnit();
        List<Equipment> equipents = pu.findAll(Equipment.class);
        for (Equipment equipent : equipents) {
            updateEquipmentLastLog(equipent.getId());
        }
    }

    protected void updateEquipmentLastLog(int eid) {
        net.vpc.upa.PersistenceUnit pu = UPA.getPersistenceUnit();
        EquipmentStatusLog current = VrApp.getBean(EquipmentPlugin.class).findEquipmentLatestLog(eid);
        if (current != null) {
            current.getEquipment().setStatusType(current.getType());
            current.getEquipment().setActor(current.getActor());
            current.getEquipment().setResponsible(current.getResponsible());
            current.getEquipment().setLogStartDate(current.getStartDate());
            current.getEquipment().setLogEndDate(current.getEndDate());
            pu.merge(current.getEquipment());
        }
    }
}
