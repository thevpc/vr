/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import net.vpc.app.vainruling.core.service.model.AppArea;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Equipment")
@Properties(
        {
                @Property(name = UIConstants.Grid.ROW_STYLE,
                        value = "(i.object.deleted or i.object.archived) ?'vr-row-deleted':(i.object.location eq null) ?'vr-row-invalid': ''"),
                @Property(name = UIConstants.ENTITY_ID_HIERARCHY, value = "brandLine"),
                @Property(name = "ui.auto-filter.department", value = "{expr='department',order=1}"),
                @Property(name = "ui.auto-filter.acquisition", value = "{expr='acquisition',order=2}"),
                @Property(name = "ui.auto-filter.brandLine", value = "{expr='brandLine',order=3}"),
                @Property(name = "ui.auto-filter.location", value = "{expr='location',order=4}"),
                @Property(name = "ui.auto-filter.type", value = "{expr='type',order=5}"),
                @Property(name = "ui.auto-filter.statusType", value = "{expr='statusType',order=6}"),
                @Property(name = "ui.main-photo-property", value = "photo"),
                @Property(name = "ui.main-photo-property.default", value = "theme-context://images/equipment.png"),
        })
public class Equipment {

    @Id
    @Sequence
    private int id;
    private String serial;
    private String stockSerial;
    private String name;
    @Summary
    @Formula(value = "this.quantity+Coalesce((Select sum(a.quantity) from EquipmentStatusLog a where a.equipmentId=this.id),0)",formulaOrder = 2)
    private double actualQuantity;
    private double quantity;
    @Main
    @Formula(value = "concat(this.name,'-',this.serial)")
    private String fullName;
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "400")
    private String description;
    @Properties(
            {
                    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
                    @Property(name = UIConstants.Form.CONTROL_FILE_TYPE, value = "root"),
                    @Property(name = UIConstants.Form.CONTROL_FILE_PATH, value = "/Data/Equipment")
            })
    private String photo;

    @Summary
    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Category"))
    private EquipmentType type;
    private AppDepartment department;

    @Summary
    @ToString
    private EquipmentStatusType statusType=EquipmentStatusType.AVAILABLE;

    @Summary
    private AppArea location;

//    @Summary
//    @Formula(value = "this.brandLine.brand", type = FormulaType.LIVE)
//    private EquipmentBrand brand;

    private EquipmentAcquisition acquisition;

    @Summary
    @Properties(
            @Property(name = UIConstants.Form.NEWLINE, value = "before,after"))
    private EquipmentBrandLine brandLine;
    @Hierarchy
    @Summary
    @Properties(
            @Property(name = UIConstants.Form.NEWLINE, value = "before,after"))
    private Equipment relativeTo;


    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    private boolean archived;
    private boolean deleted;
    private String deletedBy;
    private Timestamp deletedOn;

    @Summary
    @Formula(value = "currentTimestamp()", type = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp createdOn;

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

    public EquipmentAcquisition getAcquisition() {
        return acquisition;
    }

    public void setAcquisition(EquipmentAcquisition acquisition) {
        this.acquisition = acquisition;
    }

    public String getStockSerial() {
        return stockSerial;
    }

    public void setStockSerial(String stockSerial) {
        this.stockSerial = stockSerial;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

//    public EquipmentBrand getBrand() {
//        return brand;
//    }
//
//    public void setBrand(EquipmentBrand brand) {
//        this.brand = brand;
//    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(double actualQuantity) {
        this.actualQuantity = actualQuantity;
    }
}
