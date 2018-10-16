/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import java.util.Date;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
public class EquipmentBorrowRequest {

    @Id
    @Sequence
    private int id;
    private AppUser borrowerUser;
    private Equipment equipment;
    private Date fromDate;
    private Date toDate;
    private boolean borrowerArchive;
    private Date borrowerDate;

    private AppUser visaUser;
    private EquipmentBorrowStatus visaUserStatus;
    private Date visaUserStatusDate;
    private boolean visaArchive;

    private AppUser operatorUser;
    private EquipmentBorrowStatus operatorUserStatus;
    private Date operatorUserStatusDate;
    private boolean operatorArchive;

    private AppUser superOperatorUser;
    private EquipmentBorrowStatus superOperatorUserStatus;
    private Date superOperatorUserStatusDate;
    private boolean superOperatorArchive;

    private EquipmentBorrowStatus finalStatus;
    private Date finalStatusDate;
    private EquipmentStatusLog assignment;

    private String description;

    public EquipmentBorrowStatus getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(EquipmentBorrowStatus finalStatus) {
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

    public EquipmentBorrowStatus getVisaUserStatus() {
        return visaUserStatus;
    }

    public void setVisaUserStatus(EquipmentBorrowStatus visaUserStatus) {
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

    

    public EquipmentStatusLog getAssignment() {
        return assignment;
    }

    public void setAssignment(EquipmentStatusLog assignment) {
        this.assignment = assignment;
    }

    public boolean isVisaArchive() {
        return visaArchive;
    }

    public void setVisaArchive(boolean visaArchive) {
        this.visaArchive = visaArchive;
    }

    public AppUser getBorrowerUser() {
        return borrowerUser;
    }

    public void setBorrowerUser(AppUser borrowerUser) {
        this.borrowerUser = borrowerUser;
    }

    public boolean isBorrowerArchive() {
        return borrowerArchive;
    }

    public void setBorrowerArchive(boolean borrowerArchive) {
        this.borrowerArchive = borrowerArchive;
    }

    public EquipmentBorrowStatus getOperatorUserStatus() {
        return operatorUserStatus;
    }

    public void setOperatorUserStatus(EquipmentBorrowStatus operatorUserStatus) {
        this.operatorUserStatus = operatorUserStatus;
    }

    public Date getOperatorUserStatusDate() {
        return operatorUserStatusDate;
    }

    public void setOperatorUserStatusDate(Date operatorUserStatusDate) {
        this.operatorUserStatusDate = operatorUserStatusDate;
    }

    public boolean isOperatorArchive() {
        return operatorArchive;
    }

    public void setOperatorArchive(boolean operatorArchive) {
        this.operatorArchive = operatorArchive;
    }

    public AppUser getSuperOperatorUser() {
        return superOperatorUser;
    }

    public void setSuperOperatorUser(AppUser superOperatorUser) {
        this.superOperatorUser = superOperatorUser;
    }

    public EquipmentBorrowStatus getSuperOperatorUserStatus() {
        return superOperatorUserStatus;
    }

    public void setSuperOperatorUserStatus(EquipmentBorrowStatus superOperatorUserStatus) {
        this.superOperatorUserStatus = superOperatorUserStatus;
    }

    public Date getSuperOperatorUserStatusDate() {
        return superOperatorUserStatusDate;
    }

    public void setSuperOperatorUserStatusDate(Date superOperatorUserStatusDate) {
        this.superOperatorUserStatusDate = superOperatorUserStatusDate;
    }

    public boolean isSuperOperatorArchive() {
        return superOperatorArchive;
    }

    public void setSuperOperatorArchive(boolean superOperatorArchive) {
        this.superOperatorArchive = superOperatorArchive;
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
    

}
