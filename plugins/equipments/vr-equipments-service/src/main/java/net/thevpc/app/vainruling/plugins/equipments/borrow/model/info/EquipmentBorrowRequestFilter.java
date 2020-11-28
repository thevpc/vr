/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.borrow.model.info;

import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequestStatus;

/**
 *
 * @author vpc
 */
public class EquipmentBorrowRequestFilter {
    private EquipmentBorrowRequestStatus[] visaStatus;
    private EquipmentBorrowRequestStatus[] superOperatorStatus;
    private EquipmentBorrowRequestStatus[] operatorStatus;
    private EquipmentBorrowRequestStatus[] finalStatus;

    private Integer borrowerUserId;
    private Integer visaUserId;
    private Integer operatorUserId;
    private Integer superOperatorUserId;
    private Integer equipmentTypeId;
    private Integer departmentId;
    private Boolean archive;
    private Boolean cancelled;

    public EquipmentBorrowRequestStatus[] getVisaStatus() {
        return visaStatus;
    }

    public EquipmentBorrowRequestFilter setVisaStatus(EquipmentBorrowRequestStatus... visaStatus) {
        this.visaStatus = visaStatus;
        return this;
    }

    public EquipmentBorrowRequestStatus[] getSuperOperatorStatus() {
        return superOperatorStatus;
    }

    public EquipmentBorrowRequestFilter setSuperOperatorStatus(EquipmentBorrowRequestStatus... superOperatorStatus) {
        this.superOperatorStatus = superOperatorStatus;
        return this;
    }

    public EquipmentBorrowRequestStatus[] getOperatorStatus() {
        return operatorStatus;
    }

    public EquipmentBorrowRequestFilter setOperatorStatus(EquipmentBorrowRequestStatus... operatorStatus) {
        this.operatorStatus = operatorStatus;
        return this;
    }

    public Integer getBorrowerUserId() {
        return borrowerUserId;
    }

    public EquipmentBorrowRequestFilter setBorrowerUserId(Integer userId) {
        this.borrowerUserId = userId;
        return this;
    }

    public Integer getOperatorUserId() {
        return operatorUserId;
    }

    public EquipmentBorrowRequestFilter setOperatorUserId(Integer operatorUserId) {
        this.operatorUserId = operatorUserId;
        return this;
    }

    public Integer getSuperOperatorUserId() {
        return superOperatorUserId;
    }

    public EquipmentBorrowRequestFilter setSuperOperatorUserId(Integer superOperatorUserId) {
        this.superOperatorUserId = superOperatorUserId;
        return this;
    }

    public Integer getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public EquipmentBorrowRequestFilter setEquipmentTypeId(Integer equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
        return this;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public EquipmentBorrowRequestFilter setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
        return this;
    }

    public Boolean getArchive() {
        return archive;
    }

    public EquipmentBorrowRequestFilter setArchive(Boolean archive) {
        this.archive = archive;
        return this;
    }

    public Integer getVisaUserId() {
        return visaUserId;
    }

    public EquipmentBorrowRequestFilter setVisaUserId(Integer visaUserId) {
        this.visaUserId = visaUserId;
        return this;
    }

    public EquipmentBorrowRequestStatus[] getFinalStatus() {
        return finalStatus;
    }

    public EquipmentBorrowRequestFilter setFinalStatus(EquipmentBorrowRequestStatus... finalStatus) {
        this.finalStatus = finalStatus;
        return this;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public EquipmentBorrowRequestFilter setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }
    
    
}
