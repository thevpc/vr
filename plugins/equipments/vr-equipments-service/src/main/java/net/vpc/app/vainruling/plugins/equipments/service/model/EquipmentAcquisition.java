/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.util.Date;

/**
 * @author vpc
 */
@Entity(listOrder = "date desc")
@Path("Equipment")
public class EquipmentAcquisition {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private Date date;
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;
    private AppCompany provider;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AppUser user;
    private AppUser orderUser;
    private Date orderDate;
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.TEXTAREA))
    @Field(max = "4000")
    private String orderObs;
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.TEXTAREA))
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

    @Override
    public String toString() {
        return String.valueOf(name);
    }

}
