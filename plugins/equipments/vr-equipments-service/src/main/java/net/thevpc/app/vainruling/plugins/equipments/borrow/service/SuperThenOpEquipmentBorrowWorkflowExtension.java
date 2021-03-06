/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.borrow.service;

import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequest;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowVisaStatus;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
public class SuperThenOpEquipmentBorrowWorkflowExtension implements EquipmentBorrowWorkflowExtension {

    @Override
    public EquipmentBorrowVisaStatus computeStatus(EquipmentBorrowRequest req) {
        if (req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED
                || req.getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.REJECTED) {
            return req.getSuperOperatorUserStatus();
        }
        if (req.getOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED
                || req.getOperatorUserStatus() == EquipmentBorrowVisaStatus.REJECTED) {
            return req.getOperatorUserStatus();
        }
        return null;
    }

}
