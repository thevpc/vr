/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.borrow.service;

import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequest;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowVisaStatus;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class SuperOperatorEquipmentBorrowWorkflowExtension implements EquipmentBorrowWorkflowExtension {

    @Override
    public EquipmentBorrowVisaStatus computeStatus(EquipmentBorrowRequest req) {
        if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED
                || req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.REJECTED) {
            return req.getSuperOperatorUserStatus();
        }
        return null;
    }

}
