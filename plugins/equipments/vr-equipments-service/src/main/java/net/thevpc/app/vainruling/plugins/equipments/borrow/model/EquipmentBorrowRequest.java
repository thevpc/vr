/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.borrow.model;

import java.sql.Timestamp;
import java.util.Date;

import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.thevpc.upa.FormulaType;
import net.thevpc.upa.config.Entity;
import net.thevpc.upa.config.Formula;
import net.thevpc.upa.config.Id;
import net.thevpc.upa.config.Main;
import net.thevpc.upa.config.Path;
import net.thevpc.upa.config.Property;
import net.thevpc.upa.config.Sequence;
import net.thevpc.upa.config.Summary;

/**
 * This entity describes the request for booking an equipment. It will end up
 * with a valid instance of EquipmentBorrowLog (fields borrow) when the book is
 * confirmed.
 *
 * @author vpc
 */
@Entity(listOrder = "this.fromDate")
@Path("Equipment/Details/Borrow")
public class EquipmentBorrowRequest {

    @Id
    @Sequence
    private int id;
    private AppUser borrowerUser;
    @Main
    private Equipment equipment;

    @Summary
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    private Timestamp creationDate;

    @Path("EquipmentRequest")
    @Summary
    private Date fromDate;
    @Summary
    private Date toDate;
    @Summary
    private double quantity;
    private Date borrowerDate;

    @Path("EquipmentVisa")
    private AppUser visaUser;
    private EquipmentBorrowVisaStatus visaUserStatus;
    private Date visaUserStatusDate;

    @Path("EquipmentOperator")
    private AppUser operatorUser;
    private EquipmentBorrowVisaStatus operatorUserStatus;
    private Date operatorUserStatusDate;

    @Path("EquipmentSuperOperator")
    private AppUser superOperatorUser;
    private EquipmentBorrowVisaStatus superOperatorUserStatus;
    private Date superOperatorUserStatusDate;

    @Path("EquipmentCurrent")
    @Summary
    private EquipmentBorrowRequestStatus finalStatus;
    @Summary
    private Date finalStatusDate;
    @Summary
    private boolean archive;

    @Summary
    private boolean cancelled;

private EquipmentBorrowLog borrow;

    @Path("Observations")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;

    public EquipmentBorrowRequestStatus getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(EquipmentBorrowRequestStatus finalStatus) {
        this.finalStatus = finalStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public AppUser getVisaUser() {
        return visaUser;
    }

    public void setVisaUser(AppUser visaUser) {
        this.visaUser = visaUser;
    }

    public EquipmentBorrowVisaStatus getVisaUserStatus() {
        return visaUserStatus;
    }

    public void setVisaUserStatus(EquipmentBorrowVisaStatus visaUserStatus) {
        this.visaUserStatus = visaUserStatus;
    }

    public Date getVisaUserStatusDate() {
        return visaUserStatusDate;
    }

    public void setVisaUserStatusDate(Date visaUserStatusDate) {
        this.visaUserStatusDate = visaUserStatusDate;
    }

    public AppUser getOperatorUser() {
        return operatorUser;
    }

    public void setOperatorUser(AppUser operatorUser) {
        this.operatorUser = operatorUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AppUser getBorrowerUser() {
        return borrowerUser;
    }

    public void setBorrowerUser(AppUser borrowerUser) {
        this.borrowerUser = borrowerUser;
    }

    public EquipmentBorrowVisaStatus getOperatorUserStatus() {
        return operatorUserStatus;
    }

    public void setOperatorUserStatus(EquipmentBorrowVisaStatus operatorUserStatus) {
        this.operatorUserStatus = operatorUserStatus;
    }

    public Date getOperatorUserStatusDate() {
        return operatorUserStatusDate;
    }

    public void setOperatorUserStatusDate(Date operatorUserStatusDate) {
        this.operatorUserStatusDate = operatorUserStatusDate;
    }

    public AppUser getSuperOperatorUser() {
        return superOperatorUser;
    }

    public void setSuperOperatorUser(AppUser superOperatorUser) {
        this.superOperatorUser = superOperatorUser;
    }

    public EquipmentBorrowVisaStatus getSuperOperatorUserStatus() {
        return superOperatorUserStatus;
    }

    public void setSuperOperatorUserStatus(EquipmentBorrowVisaStatus superOperatorUserStatus) {
        this.superOperatorUserStatus = superOperatorUserStatus;
    }

    public Date getSuperOperatorUserStatusDate() {
        return superOperatorUserStatusDate;
    }

    public void setSuperOperatorUserStatusDate(Date superOperatorUserStatusDate) {
        this.superOperatorUserStatusDate = superOperatorUserStatusDate;
    }

    public Date getBorrowerDate() {
        return borrowerDate;
    }

    public void setBorrowerDate(Date borrowerDate) {
        this.borrowerDate = borrowerDate;
    }

    public Date getFinalStatusDate() {
        return finalStatusDate;
    }

    public void setFinalStatusDate(Date finalStatusDate) {
        this.finalStatusDate = finalStatusDate;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.id;
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
        final EquipmentBorrowRequest other = (EquipmentBorrowRequest) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    public EquipmentBorrowLog getBorrow() {
        return borrow;
    }

    public void setBorrow(EquipmentBorrowLog borrow) {
        this.borrow = borrow;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
