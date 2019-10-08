/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.borrow.service;

import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequest;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowVisaStatus;

/**
 *
 * @author vpc
 */
public interface EquipmentBorrowWorkflowExtension {

    public EquipmentBorrowVisaStatus computeStatus(EquipmentBorrowRequest req);
    
}
