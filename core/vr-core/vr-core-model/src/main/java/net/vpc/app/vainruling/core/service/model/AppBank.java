/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("/Repository/General")
@Properties(
        {
                @Property(name = "ui.auto-filter.country", value = "{expr='this.country',order=1}"),
                @Property(name = "ui.auto-filter.governorate", value = "{expr='this.governorate',order=2}"),
        })
public class AppBank {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private String name;
    @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    private String name2;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String address;
    @Summary
    private AppCountry country;
    @Summary
    private AppGovernorate governorate;
    private String postalCode;
    private String phone;
    private String fax;
    private String mainContact;
    private String mainContactAddress;
    private String mainWebSite;
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String about;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
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

    public AppGovernorate getGovernorate() {
        return governorate;
    }

    public void setGovernorate(AppGovernorate governorate) {
        this.governorate = governorate;
    }

    public AppCountry getCountry() {
        return country;
    }

    public void setCountry(AppCountry country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMainContact() {
        return mainContact;
    }

    public void setMainContact(String mainContact) {
        this.mainContact = mainContact;
    }

    public String getMainContactAddress() {
        return mainContactAddress;
    }

    public void setMainContactAddress(String mainContactAddress) {
        this.mainContactAddress = mainContactAddress;
    }

    public String getMainWebSite() {
        return mainWebSite;
    }

    public void setMainWebSite(String mainWebSite) {
        this.mainWebSite = mainWebSite;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppBank that = (AppBank) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
