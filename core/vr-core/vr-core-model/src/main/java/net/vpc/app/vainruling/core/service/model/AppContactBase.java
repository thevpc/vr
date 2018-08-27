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
import java.util.Date;

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

    private String passportNumber;


    private AppCivility civility;


    private String fullName;

    private String firstName;

    private String lastName;

    @Main
    @Formula(value = "concat(Coalesce(this.fullName,''),' - ',Coalesce(this.positionSuffix,'?'))",formulaOrder = 1)

    private String fullTitle;


    private String positionSuffix;

    @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")

    private String fullName2;

    private String firstName2;

    private String lastName2;

    private AppGender gender;

    @Path("Details")
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

    @Summary

    private String officeLocationNumber;
    @Summary

    private String officePhoneNumber;

    @Path("Position")
    @Summary

    private String positionTitle1;
    @Summary

    private String positionTitle2;

    private String positionTitle3;

    private AppCompany company;

    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)

    private String description;


    @Path("AdminInfo")
    
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String address;
    
    private AppGovernorate addressGovernorate;
    private Date birthDate;

    private String birthLocation;

    private AppGovernorate birthGovernorate;

    @Path("Trace")
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

    public static String getName(AppContactBase t) {
        String n = t.getFullName();
        if (n != null && n.trim().length() > 0) {
            return n.trim();
        }
        StringBuilder s = new StringBuilder();
        if (t.getFirstName() != null && t.getFirstName().trim().length() > 0) {
            s.append(t.getFirstName().trim());
        }
        if (t.getLastName() != null && t.getLastName().trim().length() > 0) {
            if (s.length() > 0) {
                s.append(" ");
            }
            s.append(t.getLastName().trim());
        }
        if (s.length() == 0) {
            s.append("San Nom");
        }
        return s.toString();
    }

    public static String getName2(AppContactBase t) {
        String n = t.getFullName2();
        if (n != null && n.trim().length() > 0) {
            return n.trim();
        }
        StringBuilder s = new StringBuilder();
        if (t.getFirstName2() != null && t.getFirstName2().trim().length() > 0) {
            s.append(t.getFirstName2().trim());
        }
        if (t.getLastName2() != null && t.getLastName2().trim().length() > 0) {
            if (s.length() > 0) {
                s.append(" ");
            }
            s.append(t.getLastName2().trim());
        }
        if (s.length() == 0) {
            s.append(getName(t));
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AppGender getGender() {
        return gender;
    }

    public void setGender(AppGender gender) {
        this.gender = gender;
    }

    public AppCivility getCivility() {
        return civility;
    }

    public void setCivility(AppCivility civility) {
        this.civility = civility;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        if (fullName!=null) {
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

    public String getPositionTitle1() {
        return positionTitle1;
    }

    public void setPositionTitle1(String positionTitle1) {
        this.positionTitle1 = positionTitle1;
    }

    public String getPositionTitle2() {
        return positionTitle2;
    }

    public void setPositionTitle2(String positionTitle2) {
        this.positionTitle2 = positionTitle2;
    }

    public String getPositionTitle3() {
        return positionTitle3;
    }

    public void setPositionTitle3(String positionTitle3) {
        this.positionTitle3 = positionTitle3;
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

    public String getFirstName2() {
        return firstName2;
    }

    public void setFirstName2(String firstName2) {
        this.firstName2 = firstName2;
    }

    public String getLastName2() {
        return lastName2;
    }

    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public String getPositionSuffix() {
        return positionSuffix;
    }

    public void setPositionSuffix(String positionSuffix) {
        this.positionSuffix = positionSuffix;
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

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthLocation() {
        return birthLocation;
    }

    public void setBirthLocation(String birthLocation) {
        this.birthLocation = birthLocation;
    }

    public AppGovernorate getBirthGovernorate() {
        return birthGovernorate;
    }

    public void setBirthGovernorate(AppGovernorate birthGovernorate) {
        this.birthGovernorate = birthGovernorate;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppContactBase that = (AppContactBase) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
