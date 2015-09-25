/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import net.vpc.app.vainruling.plugins.commonmodel.service.model.AppArea;
import java.sql.Timestamp;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.config.Hierarchy;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.ToString;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Equipment")
@Property(name = UIConstants.ENTITY_ID_HIERARCHY, value = "brandLine")
public class Equipment {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private String serial;
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.TEXTAREA))
    @Field(max = "400")
    private String description;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private double quantity;
    private AppArea location;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @ToString
    private EquipmentStatusType statusType;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Category"))
    private EquipmentType type;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_NEWLINE, value = "before,after"))
    private EquipmentBrandLine brandLine;
    @Hierarchy
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_NEWLINE, value = "before,after"))
    private Equipment relativeTo;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Trace"))
    private boolean archived;
    private boolean deleted;
    private String deletedBy;
    private Timestamp deletedOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public EquipmentType getType() {
        return type;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public EquipmentBrandLine getBrandLine() {
        return brandLine;
    }

    public void setBrandLine(EquipmentBrandLine brandLine) {
        this.brandLine = brandLine;
    }

    public EquipmentStatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(EquipmentStatusType statusType) {
        this.statusType = statusType;
    }

    public Equipment getRelativeTo() {
        return relativeTo;
    }

    public void setRelativeTo(Equipment relativeTo) {
        this.relativeTo = relativeTo;
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

    public AppArea getLocation() {
        return location;
    }

    public void setLocation(AppArea location) {
        this.location = location;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Timestamp getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Timestamp deletedOn) {
        this.deletedOn = deletedOn;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

}
