/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import java.sql.Timestamp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.config.ToString;

/**
 *
 * @author vpc
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
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;
    
    @Field(modifiers = UserFieldModifier.SUMMARY,max = "1024")
    @Property(name = UIConstants.FIELD_FORM_CONTROL,value = UIConstants.ControlType.TEXTAREA)
    private String description;
    
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Timestamp startDate;

    /**
     * may be borrow return date if status=borrowed
     */
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Timestamp endDate;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Equipment equipment;

    @Field(modifiers = UserFieldModifier.SUMMARY)
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
