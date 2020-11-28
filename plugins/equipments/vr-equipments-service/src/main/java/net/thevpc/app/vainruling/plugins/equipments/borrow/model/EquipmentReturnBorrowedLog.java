/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.borrow.model;

import net.thevpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusLog;
import net.thevpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.returnDate desc")
@Path("Equipment/Details/Borrow")
@Properties(
        {
            @Property(name = "ui.auto-filter.department", value = "{expr='this.statusLog.equipment.department',order=1}"),
            @Property(name = "ui.auto-filter.equipment", value = "{expr='this.statusLog.equipment',order=2}"),
            @Property(name = "ui.auto-filter.actor", value = "{expr='this.statusLog.actor',order=4}"),
            @Property(name = "ui.auto-filter.borrower", value = "{expr='this.borrowLog.borrower',order=5}"),})
public class EquipmentReturnBorrowedLog {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    @Summary
    private EquipmentBorrowLog borrowLog;

    private EquipmentStatusLog statusLog;

    @Field(defaultValue = "1")
    @Summary
    private double quantity;

    @Summary
    private Timestamp returnDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EquipmentBorrowLog getBorrowLog() {
        return borrowLog;
    }

    public void setBorrowLog(EquipmentBorrowLog borrowLog) {
        this.borrowLog = borrowLog;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
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

}
