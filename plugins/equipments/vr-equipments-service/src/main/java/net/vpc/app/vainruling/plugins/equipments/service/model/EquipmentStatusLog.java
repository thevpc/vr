/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Equipment")
public class EquipmentStatusLog {

    @Id
    @Sequence
    private int id;
    /**
     * description of the status, for instance when borrowed tell why
     */
    @Main
    private String name;

    @Summary
    @Field(max = "1024")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;

    @Summary
    private Timestamp startDate;

    /**
     * may be borrow return date if status=borrowed
     */
    @Summary
    private Timestamp endDate;

    @Summary
    private Equipment equipment;

    @Summary
    @ToString
    private EquipmentStatusType type;

    /**
     * may be borrower if status=borrowed
     */
    private AppUser responsible;

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public EquipmentStatusType getType() {
        return type;
    }

    public void setType(EquipmentStatusType type) {
        this.type = type;
    }

    public AppUser getResponsible() {
        return responsible;
    }

    public void setResponsible(AppUser responsible) {
        this.responsible = responsible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }


}
