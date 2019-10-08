/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.model;

import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

import java.util.Date;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.date desc")
@Path("Equipment/Details")
@Properties(
        {
                @Property(name = "ui.auto-filter.department", value = "{expr='this.department',order=1}"),
                @Property(name = "ui.auto-filter.orderPeriod", value = "{expr='this.orderPeriod',order=2}"),
                @Property(name = "ui.auto-filter.acquisitionPeriod", value = "{expr='this.acquisitionPeriod',order=3}"),
        })
public class EquipmentAcquisition {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Summary
    private Date date;
    @Main
    private String name;
    private AppCompany provider;
    @Summary
    private AppUser user;
    @Summary
    private EquipmentAcquisitionStatus status;
    @Summary
    private AppDepartment department;
    private AppPeriod orderPeriod;
    private AppPeriod acquisitionPeriod;
    private AppUser orderUser;
    private Date orderDate;
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "4000")
    private String orderObs;
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "4000")
    private String admissionObs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public AppUser getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(AppUser orderUser) {
        this.orderUser = orderUser;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public AppCompany getProvider() {
        return provider;
    }

    public void setProvider(AppCompany provider) {
        this.provider = provider;
    }

    public String getOrderObs() {
        return orderObs;
    }

    public void setOrderObs(String orderObs) {
        this.orderObs = orderObs;
    }

    public String getAdmissionObs() {
        return admissionObs;
    }

    public void setAdmissionObs(String admissionObs) {
        this.admissionObs = admissionObs;
    }

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

    public AppPeriod getOrderPeriod() {
        return orderPeriod;
    }

    public void setOrderPeriod(AppPeriod orderPeriod) {
        this.orderPeriod = orderPeriod;
    }

    public AppPeriod getAcquisitionPeriod() {
        return acquisitionPeriod;
    }

    public void setAcquisitionPeriod(AppPeriod acquisitionPeriod) {
        this.acquisitionPeriod = acquisitionPeriod;
    }

    public EquipmentAcquisitionStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentAcquisitionStatus status) {
        this.status = status;
    }
}
