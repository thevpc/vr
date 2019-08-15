/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.aquisition.model;

import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentAcquisition;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentStatusLog;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.ProtectionLevel;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.opDate desc")
@Path("Equipment")
@Properties(
        {
            @Property(name = "ui.auto-filter.department", value = "{expr='this.statusLog.equipment.department',order=1}"),
            @Property(name = "ui.auto-filter.equipment", value = "{expr='this.statusLog.equipment',order=2}"),
            @Property(name = "ui.auto-filter.actor", value = "{expr='this.statusLog.actor',order=4}")
        })
public class EquipmentAquisitionLog {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    private EquipmentStatusLog statusLog;

    @Field(defaultValue = "1")
    @Summary
    private double quantity;

    private EquipmentAcquisition acquisition;

    @Summary
    private Timestamp opDate;

    public Timestamp getOpDate() {
        return opDate;
    }

    public void setOpDate(Timestamp opDate) {
        this.opDate = opDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public EquipmentAcquisition getAcquisition() {
        return acquisition;
    }

    public void setAcquisition(EquipmentAcquisition acquisition) {
        this.acquisition = acquisition;
    }

    public EquipmentStatusLog getStatusLog() {
        return statusLog;
    }

    public void setStatusLog(EquipmentStatusLog statusLog) {
        this.statusLog = statusLog;
    }

}
