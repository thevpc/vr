/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.borrow.model.info;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
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

    private String infoId;
    private Equipment equipment;
    private AppUser borrowerUser;
    private EquipmentBorrowRequest request;
    private boolean cancelEnabled;
    private boolean requestEnabled;
    private double borrowQuantity;
    private Timestamp operationDate;
    private Date fromDate;
    private Date toDate;
    private double actualQuantity;
    private double requestedQuantity;
    private double availableQuantity;
    private EquipmentBorrowStatusExt status;
    private EquipmentBorrowOperatorType operatorType;

    public EquipmentForResponsibleInfo() {

    }

    public EquipmentForResponsibleInfo(EquipmentBorrowOperatorType operatorType, EquipmentBorrowRequest request) {
        this.request = request;
        this.operatorType = operatorType;
        this.equipment = request.getEquipment();
        this.operationDate = request.getFinalStatusDate() == null ? null : new Timestamp(request.getFinalStatusDate().getTime());
        if (request.getFinalStatus() == null) {
            this.status = EquipmentBorrowStatusExt.PENDING;
        } else {
            switch (request.getFinalStatus()) {
                case ACCEPTED: {
                    if (request.getBorrow() != null) {
                        this.status = evalStatus(request.getBorrow());
                    } else {
                        this.status = EquipmentBorrowStatusExt.ACCEPTED;
                    }
                    break;
                }
                case PENDING: {
                    this.status = EquipmentBorrowStatusExt.PENDING;
                    break;
                }
                case REJECTED: {
                    this.status = EquipmentBorrowStatusExt.REJECTED;
                    break;
                }
                default: {
                    this.status = EquipmentBorrowStatusExt.PENDING;
                    break;
                }
            }
        }
        this.borrowQuantity = Math.abs(request.getQuantity());
        this.actualQuantity = equipment.getActualQuantity();
        this.requestedQuantity = equipment.getRequestedQuantity();
        this.availableQuantity = equipment.getActualQuantity()-equipment.getRequestedQuantity();
        this.fromDate = request.getFromDate();
        this.toDate = request.getToDate();
        this.borrowerUser = request.getBorrowerUser();
        this.infoId = "Request-" + request.getId();
    }

    public EquipmentForResponsibleInfo(EquipmentBorrowOperatorType operatorType, EquipmentBorrowLog log) {
        this.operatorType = operatorType;
        this.equipment = log.getStatusLog().getEquipment();
        this.operationDate = log.getStatusLog().getCreationDate();
        this.status = evalStatus(log);
        this.borrowQuantity = Math.abs(log.getQuantity());
        this.actualQuantity = equipment.getActualQuantity();
        this.requestedQuantity = equipment.getRequestedQuantity();
        this.availableQuantity = equipment.getActualQuantity()-equipment.getRequestedQuantity();
        this.fromDate = log.getStartDate();
        this.toDate = log.getEndDate();
        this.borrowerUser = log.getBorrower();
        this.infoId = "Borrow-" + log.getId();
    }

    public EquipmentForResponsibleInfo(EquipmentBorrowOperatorType operatorType, EquipmentStatusLog log) {
        this.operatorType = operatorType;
        this.equipment = log.getEquipment();
        this.operationDate = log.getCreationDate();
        this.status = evalStatus(log);
        this.borrowQuantity = Math.abs(log.getQuantity());
        this.actualQuantity = equipment.getActualQuantity();
        this.requestedQuantity = equipment.getRequestedQuantity();
        this.availableQuantity = equipment.getActualQuantity()-equipment.getRequestedQuantity();
        this.fromDate = log.getStartDate();
        this.toDate = log.getEndDate();
        this.borrowerUser = log.getActor();
        this.infoId = "Status-" + log.getId();
    }

    public EquipmentForResponsibleInfo(EquipmentBorrowOperatorType operatorType, Equipment equipment) {
        this.operationDate = equipment.getLogStartDate();
        if (this.operationDate == null) {
            this.operationDate = equipment.getCreatedOn();
        }
        this.operatorType = operatorType;
        this.equipment = equipment;
        this.status = EquipmentBorrowStatusExt.BORROWABLE;
        this.borrowQuantity = 0;
        this.actualQuantity = equipment.getActualQuantity();
        this.requestedQuantity = equipment.getRequestedQuantity();
        this.availableQuantity = equipment.getActualQuantity()-equipment.getRequestedQuantity();
        this.fromDate = equipment.getLogStartDate();
        this.toDate = equipment.getLogEndDate();
        this.borrowerUser = equipment.getActor();
        this.infoId = "Equipment-" + equipment.getId();
    }

    private final EquipmentBorrowStatusExt evalStatus(EquipmentBorrowLog log) {
        if (log != null) {
            return evalStatus(log.getRemainingQuantity(), log.getEndDate());
        }
        return EquipmentBorrowStatusExt.ACCEPTED;
    }

    private final EquipmentBorrowStatusExt evalStatus(EquipmentStatusLog log) {
        if (log != null) {
            return evalStatus(Double.NaN, log.getEndDate());
        }
        return EquipmentBorrowStatusExt.ACCEPTED;
    }

    private final EquipmentBorrowStatusExt evalStatus(double qty, Date dte) {
        if (!Double.isNaN(qty) && qty <= 0) {
            return EquipmentBorrowStatusExt.RETURNED;
        } else if (dte == null) {
            return EquipmentBorrowStatusExt.BORROWED;
        } else {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (now.compareTo(dte) >= 0) {
                return EquipmentBorrowStatusExt.RETURN_IMMEDIATELY;
            } else {
                long remains = (dte.getTime() - now.getTime()) / 1000 / 3600 / 24;
                if (remains < 3) {
                    return EquipmentBorrowStatusExt.RETURN_LATER;
                } else {
                    return EquipmentBorrowStatusExt.BORROWED;
                }
            }
        }
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
        int x = Integer.compare(equipment.getId(), o.equipment.getId());
        if (x != 0) {
            return x;
        }
        x = infoId.compareTo(o.infoId);
        if (x != 0) {
            return x;
        }
        return equipment.getName().compareTo(o.equipment.getName());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.infoId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EquipmentForResponsibleInfo other = (EquipmentForResponsibleInfo) obj;
        if (!Objects.equals(this.infoId, other.infoId)) {
            return false;
        }
        return true;
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

    public AppUser getBorrowerUser() {
        return borrowerUser;
    }

    public void setBorrowerUser(AppUser borrowerUser) {
        this.borrowerUser = borrowerUser;
    }

    public String getInfoId() {
        return infoId;
    }

    public Timestamp getOperationDate() {
        return operationDate;
    }

    public double getRequestedQuantity() {
        return requestedQuantity;
    }

    public double getAvailableQuantity() {
        return availableQuantity;
    }
    public double getAvailableQuantity2() {
        switch(this.status){
            case PENDING:{
                return availableQuantity-borrowQuantity;
            } 
        }
        return availableQuantity;
    }

    
}
