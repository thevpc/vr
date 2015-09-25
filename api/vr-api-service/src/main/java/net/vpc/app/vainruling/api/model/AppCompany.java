/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.model;

import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Admin/Config")
public class AppCompany {

    @Id
    @Sequence

    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;
    @Property(name = UIConstants.FIELD_FORM_SPAN, value = "MAX_VALUE")
    private String name2;
    private String firstContactName;
    private String firstContactEmail;
    private String firstContactPhone;
    private String firstContactPhone2;
    private String secondContactName;
    private String secondContactEmail;
    private String secondContactPhone;
    private String secondContactPhone2;
    private AppUser localContact;
    private AppUser contactListUser;
    private String address;
    private AppGovernorate governorate;
    private AppCountry country;
    private AppIndustry industry;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getFirstContactName() {
        return firstContactName;
    }

    public void setFirstContactName(String firstContactName) {
        this.firstContactName = firstContactName;
    }

    public String getFirstContactEmail() {
        return firstContactEmail;
    }

    public void setFirstContactEmail(String firstContactEmail) {
        this.firstContactEmail = firstContactEmail;
    }

    public String getFirstContactPhone() {
        return firstContactPhone;
    }

    public void setFirstContactPhone(String firstContactPhone) {
        this.firstContactPhone = firstContactPhone;
    }

    public String getFirstContactPhone2() {
        return firstContactPhone2;
    }

    public void setFirstContactPhone2(String firstContactPhone2) {
        this.firstContactPhone2 = firstContactPhone2;
    }

    public String getSecondContactName() {
        return secondContactName;
    }

    public void setSecondContactName(String secondContactName) {
        this.secondContactName = secondContactName;
    }

    public String getSecondContactEmail() {
        return secondContactEmail;
    }

    public void setSecondContactEmail(String secondContactEmail) {
        this.secondContactEmail = secondContactEmail;
    }

    public String getSecondContactPhone() {
        return secondContactPhone;
    }

    public void setSecondContactPhone(String secondContactPhone) {
        this.secondContactPhone = secondContactPhone;
    }

    public String getSecondContactPhone2() {
        return secondContactPhone2;
    }

    public void setSecondContactPhone2(String secondContactPhone2) {
        this.secondContactPhone2 = secondContactPhone2;
    }

    public AppGovernorate getGovernorate() {
        return governorate;
    }

    public void setGovernorate(AppGovernorate governorate) {
        this.governorate = governorate;
    }

    public AppIndustry getIndustry() {
        return industry;
    }

    public void setIndustry(AppIndustry industry) {
        this.industry = industry;
    }

    public AppUser getLocalContact() {
        return localContact;
    }

    public void setLocalContact(AppUser localContact) {
        this.localContact = localContact;
    }

    public AppUser getContactListUser() {
        return contactListUser;
    }

    public void setContactListUser(AppUser contactListUser) {
        this.contactListUser = contactListUser;
    }

    public AppCountry getCountry() {
        return country;
    }

    public void setCountry(AppCountry country) {
        this.country = country;
    }
    
    
}
