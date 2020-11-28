/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.inventory.model;

import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.upa.config.*;

import java.util.Date;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.startDate desc, name")
@Path("Equipment/Inventory")
public class Inventory {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    @Unique
    private String name;
    private Date startDate;
    private Date endDate;
    private AppDepartment department;
    @ToString
    private InventoryStatus status = InventoryStatus.CONFIG;

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

}
