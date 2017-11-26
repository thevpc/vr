/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.AccessLevel;
import net.vpc.upa.ProtectionLevel;
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
                @Property(name = "ui.auto-filter.department", value = "{expr='this.equipment.department',order=1}"),
                @Property(name = "ui.auto-filter.equipment", value = "{expr='this.equipment',order=2}"),
                @Property(name = "ui.auto-filter.type", value = "{expr='this.type',order=3}"),
                @Property(name = "ui.auto-filter.actor", value = "{expr='this.actor',order=4}"),
                @Property(name = "ui.auto-filter.responsible", value = "{expr='this.responsible',order=5}"),
        })
public class EquipmentStatusLog {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    @Summary
    private Equipment equipment;

    /**
     * technician
     */
    @Summary
    @Field(protectionLevel = ProtectionLevel.PROTECTED)
    private AppUser actor;


    @Summary
    @ToString
    //@Properties({@Property(name = UIConstants.Form.SUBMIT_ON_CHANGE, value = "true")})
    private EquipmentStatusType type=EquipmentStatusType.AVAILABLE;

    @Field(defaultValue = "1")
    @Summary
    private double quantity;

    //@Properties(@Property(name = UIConstants.Form.VISIBLE_CONDITION, value = "x!=null && vr.same(x.get('type'),'ACQUISITION')"))
    private EquipmentAcquisition acquisition;

    @Summary
    private Timestamp startDate;

    /**
     * may be borrow return date if status=borrowed
     */
    //@Properties(@Property(name = UIConstants.Form.VISIBLE_CONDITION, value = "x!=null && (vr.same(x.get('type'),'BORROWED') || vr.same(x.get('type'),'TEMPORARILY_UNAVAILABLE'))"))
    @Summary
    private Timestamp endDate;


    //@Properties(@Property(name = UIConstants.Form.VISIBLE_CONDITION, value = "x!=null && !vr.same(x.get('type'),'ACQUISITION') && !vr.same(x.get('type'),'AVAILABLE') && !vr.same(x.get('type'),'TEMPORARILY_UNAVAILABLE')"))
    @Summary
    private AppUser responsible;

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
}
