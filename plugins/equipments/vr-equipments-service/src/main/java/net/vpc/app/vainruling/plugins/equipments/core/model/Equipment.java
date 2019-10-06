/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.model;

import net.vpc.app.vainruling.core.service.model.AppArea;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Equipment")
@Properties({
    @Property(name = UIConstants.Grid.ROW_STYLE,
            value = "(i.object.deleted or i.object.archived) ?'vr-row-deleted':(i.object.location eq null) ?'vr-row-invalid': ''"),
    @Property(name = UIConstants.ENTITY_ID_HIERARCHY, value = "brandLine"),
    @Property(name = "ui.auto-filter.department", value = "{expr='this.department',order=1}"),
    @Property(name = "ui.auto-filter.acquisition", value = "{expr='this.acquisition',order=2}"),
    @Property(name = "ui.auto-filter.brandLine", value = "{expr='this.brandLine',order=3}"),
    @Property(name = "ui.auto-filter.location", value = "{expr='this.location',order=4}"),
    @Property(name = "ui.auto-filter.type", value = "{expr='this.type',order=5}"),
    @Property(name = "ui.auto-filter.statusType", value = "{expr='this.statusType',order=6}"),
    @Property(name = "ui.auto-filter.actor", value = "{expr='this.actor',order=7}"),
    @Property(name = "ui.auto-filter.responsible", value = "{expr='this.responsible',order=8}"),
    @Property(name = "ui.auto-filter.borrowable", value = "{expr='this.borrowable',order=9}")
})
public class Equipment {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    private String serial;
    private String stockSerial;
    private String name;
    @Main
    @Formula(value = "concat(this.name,'-',this.serial)", formulaOrder = 1)
    private String fullName;
    @Summary
    @Formula(value = "this.quantity+Coalesce((Select sum(a.inQty-a.outQty) from EquipmentStatusLog a where a.equipmentId=this.id),0)", formulaOrder = 1)
    private double actualQuantity;
    @Formula(value = "Coalesce((Select sum(a.quantity) from EquipmentBorrowRequest a where a.equipmentId=this.id and (a.finalStatus='PENDING' or a.finalStatus='ACCEPTED')),0)", formulaOrder = 1)
    private double requestedQuantity;
    private double quantity;
    @Summary
    @ToString
    @Property(name = UIConstants.Grid.COLUMN_STYLE_CLASS, value = "#{hashCssColor(this.statusType)}")
    private EquipmentStatusType statusType = EquipmentStatusType.AVAILABLE;
    @Summary
    private AppUser responsible;
    @Summary
    private Timestamp logStartDate;

    @Summary
    private Timestamp logEndDate;

    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "400")
    private String description;
    @Properties(
            {
                @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
                @Property(name = UIConstants.Form.CONTROL_FILE_TYPE, value = "root"),
                @Property(name = UIConstants.Form.CONTROL_FILE_PATH, value = "/Data/Equipment"),
                @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            })
    private String photo;

    @Summary
    @Path("Category")
//    @Properties(
//            {@Property(name = UIConstants.Form.SEPARATOR, value = "Category"),
//
//            }
//    )
    private EquipmentType type;
    private AppDepartment department;

    @Summary
    private AppArea location;

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

    @Summary
    private AppUser actor;

    @Field(defaultValue = "true")
    private boolean borrowable = true;

    @Field(defaultValue = "false")
    private boolean inStore = false;

    @Field(defaultValue = "fragile")
    private boolean fragile = false;

    @Field(defaultValue = "true")
    private boolean usable = true;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    private boolean archived;
    private boolean deleted;
    private String deletedBy;
    private Timestamp deletedOn;

    @Summary
    @Formula(value = "currentTimestamp()", formulaType = FormulaType.PERSIST)
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

    public AppUser getActor() {
        return actor;
    }

    public void setActor(AppUser actor) {
        this.actor = actor;
    }

    public AppUser getResponsible() {
        return responsible;
    }

    public void setResponsible(AppUser responsible) {
        this.responsible = responsible;
    }

    public Timestamp getLogStartDate() {
        return logStartDate;
    }

    public void setLogStartDate(Timestamp logStartDate) {
        this.logStartDate = logStartDate;
    }

    public Timestamp getLogEndDate() {
        return logEndDate;
    }

    public void setLogEndDate(Timestamp logEndDate) {
        this.logEndDate = logEndDate;
    }

    public boolean isBorrowable() {
        return borrowable;
    }

    public void setBorrowable(boolean borrowable) {
        this.borrowable = borrowable;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Equipment other = (Equipment) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    public double getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(double requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public boolean isInStore() {
        return inStore;
    }

    public void setInStore(boolean inStore) {
        this.inStore = inStore;
    }

    public boolean isUsable() {
        return usable;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }

    public boolean isFragile() {
        return fragile;
    }

    public void setFragile(boolean fragile) {
        this.fragile = fragile;
    }

}
