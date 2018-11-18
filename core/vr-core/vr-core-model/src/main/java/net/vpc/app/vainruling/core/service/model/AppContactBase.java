/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.FormulaType;
import net.vpc.upa.ProtectionLevel;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
public class AppContactBase {

    @Id
    @Sequence
    @Path("Main")
    private int id;

    /**
     * National Identity Number
     */
    private String nin;

    private String fullName;

    @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")

    private String fullName2;

    @Path("Contacts")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Details"))
    @Summary

    private String email;

    private String email2;

    @Summary
    @Field(protectionLevel = ProtectionLevel.PROTECTED)

    private String phone1;

    @Field(protectionLevel = ProtectionLevel.PROTECTED)
    private String phone2;

    @Field(protectionLevel = ProtectionLevel.PROTECTED)
    private String phone3;
    
    private String fax1;
    
    @Field(protectionLevel = ProtectionLevel.PROTECTED)
    private String fax2;

    @Summary

    private String officeLocationNumber;
    @Summary

    private String officePhoneNumber;

    @Path("Position")
    private AppCompany company;

    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)

    private String description;

    @Path("AdminInfo")

    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String address;
    private String postalCode;

    private AppGovernorate addressGovernorate;
    
    private AppCountry country;

    @Path(value = "Trace", position = 100)
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

//    @Summary
    private boolean enabled;
    @Summary
    private boolean deleted;
    private String deletedBy;
    private Timestamp deletedOn;

    public AppContactBase() {
    }

    public AppContactBase(AppContactBase other) {
        copyFrom(other);
    }

    public void copyFrom(AppContactBase other) {
        if (other != null) {
            this.id = other.id;
            this.nin = other.nin;
            this.fullName = other.fullName;
            this.fullName2 = other.fullName2;
            this.email = other.email;
            this.email2 = other.email2;
            this.phone1 = other.phone1;
            this.phone2 = other.phone2;
            this.phone3 = other.phone3;
            this.officeLocationNumber = other.officeLocationNumber;
            this.officePhoneNumber = other.officePhoneNumber;
            this.company = other.company;
            this.description = other.description;
            this.address = other.address;
            this.addressGovernorate = other.addressGovernorate;
            this.creationDate = other.creationDate;
            this.updateDate = other.updateDate;
            this.enabled = other.enabled;
            this.deleted = other.deleted;
            this.deletedBy = other.deletedBy;
            this.deletedOn = other.deletedOn;
            this.postalCode = other.postalCode;
            this.country = other.country;
        }
    }

    public String resolveName() {
        String n = getFullName();
        if (n != null && n.trim().length() > 0) {
            return n.trim();
        }
        StringBuilder s = new StringBuilder();
        if (s.length() == 0) {
            s.append("Sans Nom");
        }
        return s.toString();
    }

    public static String getName(AppContactBase t) {
        return t == null ? "Sans Nom" : t.resolveName();
    }

    public static String getName2(AppContactBase t) {
        return t == null ? "Sans Nom" : t.resolveName2();
    }

    public String resolveName2() {
        String n = getFullName2();
        if (n != null && n.trim().length() > 0) {
            return n.trim();
        }
        StringBuilder s = new StringBuilder();
        if (s.length() == 0) {
            s.append(resolveName());
        }
        return s.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        if (fullName != null) {
            return fullName;
        }
        return "NO_NAME";//"AppContact{" + "id=" + id + ", fullName=" + fullName + ", firstName=" + firstName + ", lastName=" + lastName + ", nin=" + nin + ", email=" + email + ", gender=" + gender + ", civitity=" + civility + ", company=" + company + ", positionTitle1=" + positionTitle1 + ", positionTitle2=" + positionTitle2 + ", positionTitle3=" + positionTitle3 + ", deleted=" + deleted + ", enabled=" + enabled + ", deletedBy=" + deletedBy + ", deletedOn=" + deletedOn + '}';
    }

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public AppCompany getCompany() {
        return company;
    }

    public void setCompany(AppCompany company) {
        this.company = company;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public String getFullName2() {
        return fullName2;
    }

    public void setFullName2(String fullName2) {
        this.fullName2 = fullName2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getOfficeLocationNumber() {
        return officeLocationNumber;
    }

    public void setOfficeLocationNumber(String officeLocationNumber) {
        this.officeLocationNumber = officeLocationNumber;
    }

    public String getOfficePhoneNumber() {
        return officePhoneNumber;
    }

    public void setOfficePhoneNumber(String officePhoneNumber) {
        this.officePhoneNumber = officePhoneNumber;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AppGovernorate getAddressGovernorate() {
        return addressGovernorate;
    }

    public void setAddressGovernorate(AppGovernorate addressGovernorate) {
        this.addressGovernorate = addressGovernorate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AppContactBase that = (AppContactBase) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getFax1() {
        return fax1;
    }

    public void setFax1(String fax1) {
        this.fax1 = fax1;
    }

    public String getFax2() {
        return fax2;
    }

    public void setFax2(String fax2) {
        this.fax2 = fax2;
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

    
    
}
