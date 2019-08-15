/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.borrow.model.info;

import java.sql.Timestamp;
import java.util.Date;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowLog;
import net.vpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequest;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusLog;

/**
 *
 * @author vpc
 */
public class EquipmentForResponsibleInfo implements Comparable<EquipmentForResponsibleInfo> {

    private Equipment equipment;
    private EquipmentBorrowRequest request;
    private boolean cancelEnabled;
    private boolean requestEnabled;
    private double borrowQuantity;
    private Date fromDate;
    private Date toDate;
    private double actualQuantity;
    private EquipmentBorrowStatusExt status;
    private EquipmentBorrowOperatorType operatorType;

    public EquipmentForResponsibleInfo() {

    }

    public EquipmentForResponsibleInfo(EquipmentBorrowOperatorType operatorType,EquipmentBorrowRequest request) {
        this.request = request;
        this.operatorType = operatorType;
        this.equipment = request.getEquipment();
        if (request.getFinalStatus() == null) {
            this.status = EquipmentBorrowStatusExt.REQUEST_NEW;
        } else {
            switch (request.getFinalStatus()) {
                case ACCEPTED: {
                    this.status = EquipmentBorrowStatusExt.REQUEST_ACCEPTED;
                    break;
                }
                case NEW: {
                    this.status = EquipmentBorrowStatusExt.REQUEST_NEW;
                    break;
                }
                case PENDING: {
                    this.status = EquipmentBorrowStatusExt.REQUEST_PENDING;
                    break;
                }
                case REJECTED: {
                    this.status = EquipmentBorrowStatusExt.REQUEST_REJECTED;
                    break;
                }
                default: {
                    this.status = EquipmentBorrowStatusExt.REQUEST_NEW;
                    break;
                }
            }
        }
        this.borrowQuantity = Math.abs(request.getQuantity());
        this.actualQuantity = equipment.getActualQuantity();
        this.fromDate = request.getFromDate();
        this.toDate = request.getToDate();
    }
    


    public EquipmentForResponsibleInfo(EquipmentBorrowOperatorType operatorType,EquipmentBorrowLog log) {
        this.operatorType=operatorType;
        this.equipment = log.getStatusLog().getEquipment();
        Timestamp endDate = log.getEndDate();
        if (endDate == null) {
            this.status = EquipmentBorrowStatusExt.BORROWED;
        } else {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.compareTo(endDate) >= 0) {
                this.status = EquipmentBorrowStatusExt.MUST_RETURN;
            } else {
                long remains = (endDate.getTime() - now.getTime()) / 1000 / 3600 / 24;
                if (remains < 3) {
                    this.status = EquipmentBorrowStatusExt.RETURN_LATER;
                } else {
                    this.status = EquipmentBorrowStatusExt.BORROWED;
                }
            }
        }
        this.borrowQuantity = Math.abs(log.getQuantity());
        this.actualQuantity = equipment.getActualQuantity();
        this.fromDate = log.getStartDate();
        this.toDate = log.getEndDate();
    }
    public EquipmentForResponsibleInfo(EquipmentBorrowOperatorType operatorType,EquipmentStatusLog log) {
        this.operatorType=operatorType;
        this.equipment = log.getEquipment();
        Timestamp endDate = log.getEndDate();
        if (endDate == null) {
            this.status = EquipmentBorrowStatusExt.BORROWED;
        } else {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.compareTo(endDate) >= 0) {
                this.status = EquipmentBorrowStatusExt.MUST_RETURN;
            } else {
                long remains = (endDate.getTime() - now.getTime()) / 1000 / 3600 / 24;
                if (remains < 3) {
                    this.status = EquipmentBorrowStatusExt.RETURN_LATER;
                } else {
                    this.status = EquipmentBorrowStatusExt.BORROWED;
                }
            }
        }
        this.borrowQuantity = Math.abs(log.getQuantity());
        this.actualQuantity = equipment.getActualQuantity();
        this.fromDate = log.getStartDate();
        this.toDate = log.getEndDate();
    }

    public EquipmentForResponsibleInfo(EquipmentBorrowOperatorType operatorType,Equipment equipment) {
        this.operatorType = operatorType;
        this.equipment = equipment;
        this.status = EquipmentBorrowStatusExt.BORROWABLE;
        this.borrowQuantity = 0;
        this.actualQuantity = equipment.getActualQuantity();
        this.fromDate = equipment.getLogStartDate();
        this.toDate = equipment.getLogEndDate();
    }
    public EquipmentBorrowOperatorType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(EquipmentBorrowOperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public double getBorrowQuantity() {
        return borrowQuantity;
    }

    public void setBorrowQuantity(double borrowQuantity) {
        this.borrowQuantity = borrowQuantity;
    }

    public int getId() {
        return equipment.getId();
    }

    public String getPhoto() {
        return equipment.getPhoto();
    }

    public String getSerial() {
        return equipment.getSerial();
    }

    public double getActualQuantity() {
        return actualQuantity;
    }

    public String getName() {
        return equipment.getName();
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public AppUser getVisaUser() {
        if (request == null) {
            return null;
        }
        return request.getVisaUser();
    }

    public EquipmentBorrowStatusExt getStatus() {
        return status;
    }

    public void setStatus(EquipmentBorrowStatusExt status) {
        this.status = status;
    }

    @Override
    public int compareTo(EquipmentForResponsibleInfo o) {
        if (this.request != null && o.request == null) {
            return -1;
        }
        if (this.request == null && o.request != null) {
            return 1;
        }
        return equipment.getName().compareTo(o.equipment.getName());
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public EquipmentBorrowRequest getRequest() {
        return request;
    }

    public boolean isCancelEnabled() {
        return cancelEnabled;
    }

    public void setCancelEnabled(boolean cancelEnabled) {
        this.cancelEnabled = cancelEnabled;
    }

    public boolean isRequestEnabled() {
        return requestEnabled;
    }

    public void setRequestEnabled(boolean requestEnabled) {
        this.requestEnabled = requestEnabled;
    }

}
