/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.model;

import java.sql.Timestamp;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "fullName")
@Path("Contact")
public class AppContact {

    @Id
    @Sequence
    private int id;

    /**
     * National Identity Number
     */
    private String nin;

    private AppCivility civility;

    @Field(modifiers = {UserFieldModifier.MAIN})
    private String fullName;
    private String firstName;
    private String lastName;

    @Property(name = UIConstants.FIELD_FORM_SPAN, value = "MAX_VALUE")
    private String fullName2;
    private String firstName2;
    private String lastName2;
    private AppGender gender;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Details"))
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private String email;
    private AppCompany company;

    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private String phone1;
    private String phone2;
    private String phone3;

    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private String positionTitle1;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private String positionTitle2;
    private String positionTitle3;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Trace"))
    @Field(defaultValue = "true",modifiers = {UserFieldModifier.SUMMARY})
    private boolean enabled;
    @Field(defaultValue = "false",modifiers = {UserFieldModifier.SUMMARY})
    private boolean deleted;
    private String deletedBy;
    private Timestamp deletedOn;

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
        if (!StringUtils.isEmpty(fullName)) {
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

    public static String getName(AppContact t) {
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

    public static String getName2(AppContact t) {
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

}
