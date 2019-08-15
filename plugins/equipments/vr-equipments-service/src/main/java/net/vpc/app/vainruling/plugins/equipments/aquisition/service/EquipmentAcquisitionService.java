/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.aquisition.service;

import net.vpc.app.vainruling.plugins.equipments.aquisition.model.EquipmentAquisitionLog;
import net.vpc.upa.UPA;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class EquipmentAcquisitionService {

    public EquipmentAquisitionLog findAquisitionLog(int statusLogId) {
        return UPA.getPersistenceUnit().createQuery("Select a from EquipmentAquisitionLog a where a.statusLogId=:statusLogId")
                .setParameter("statusLogId", statusLogId).getSingleResultOrNull();
    }
}
