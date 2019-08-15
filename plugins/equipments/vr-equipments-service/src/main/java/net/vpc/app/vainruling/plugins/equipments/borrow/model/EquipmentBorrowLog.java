/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.borrow.model;

import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusLog;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.ProtectionLevel;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.startDate desc")
@Path("Equipment")
@Properties(
        {
            @Property(name = "ui.auto-filter.department", value = "{expr='this.statusLog.equipment.department',order=1}"),
            @Property(name = "ui.auto-filter.equipment", value = "{expr='this.statusLog.equipment',order=2}"),
            @Property(name = "ui.auto-filter.actor", value = "{expr='this.statusLog.actor',order=4}"),
            @Property(name = "ui.auto-filter.borrower", value = "{expr='this.borrower',order=5}"),})
public class EquipmentBorrowLog {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    private EquipmentStatusLog statusLog;

    @Field(defaultValue = "1")
    @Summary
    private double quantity;

    @Formula("(this.quantity-(Select coalesce(sum(x.quantity)) from EquipmentReturnBorrowedLog x where x.borrowLogId=this.id))")
    private double remainingQuantity;

    @Summary
    private Timestamp startDate;

    /**
     * may be borrow return date if status=borrowed
     */
    //@Properties(@Property(name = UIConstants.Form.VISIBLE_CONDITION, value = "x!=null && (vr.same(x.get('type'),'BORROWED') || vr.same(x.get('type'),'TEMPORARILY_UNAVAILABLE'))"))
    @Summary
    private Timestamp endDate;

    @Summary
    private AppUser borrower;

    @Summary
    private boolean archive;

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public AppUser getBorrower() {
        return borrower;
    }

    public void setBorrower(AppUser borrower) {
        this.borrower = borrower;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public EquipmentStatusLog getStatusLog() {
        return statusLog;
    }

    public void setStatusLog(EquipmentStatusLog statusLog) {
        this.statusLog = statusLog;
    }

    public double getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(double remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

}
