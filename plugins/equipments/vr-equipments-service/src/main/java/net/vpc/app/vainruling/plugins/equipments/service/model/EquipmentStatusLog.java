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
@Properties(
        {
                @Property(name = UIConstants.ENTITY_ID_HIERARCHY, value = "brandLine"),
                @Property(name = "ui.auto-filter.department", value = "{expr='equipment.department',order=1}"),
                @Property(name = "ui.auto-filter.equipment", value = "{expr='equipment',order=2}"),
                @Property(name = "ui.auto-filter.type", value = "{expr='type',order=3}"),
                @Property(name = "ui.auto-filter.actor", value = "{expr='actor',order=4}"),
                @Property(name = "ui.auto-filter.responsible", value = "{expr='responsible',order=5}"),
        })
public class EquipmentStatusLog {

    @Id
    @Sequence
    private int id;

    @Summary
    private Equipment equipment;

    @Summary
    @ToString
    private EquipmentStatusType type;

    /**
     * technician
     */
    @Summary
    private AppUser actor;

    /**
     * may be borrower if status=borrowed
     */
    @Summary
    private AppUser responsible;


    @Summary
    private Timestamp startDate;

    /**
     * may be borrow return date if status=borrowed
     */
    @Summary
    private Timestamp endDate;


    /**
     * description of the status, for instance when borrowed tell why
     */
    @Main
    private String name;

    @Field(max = "1024")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;



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

    public AppUser getActor() {
        return actor;
    }

    public void setActor(AppUser actor) {
        this.actor = actor;
    }
}
