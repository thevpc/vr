/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.config;

import java.sql.Timestamp;
import net.vpc.app.vainruling.api.model.AppPeriod;
import java.util.Objects;
import net.vpc.app.vainruling.api.model.AppCivility;
import net.vpc.app.vainruling.api.model.AppDepartment;
import net.vpc.app.vainruling.api.model.AppGender;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
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
@Path("Education")
public class AcademicStudent {

    @Id
    @Sequence

    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private String name;
    @Property(name = UIConstants.FIELD_FORM_SPAN, value = "MAX_VALUE")
    private String name2;
    /**
     * national identity Number
     */
    private String nin;
    private String subscriptionNumber;
    private String firstName;
    private String lastName;
    private String firstName2;
    private String lastName2;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private String email;
    private String secondEmail;
    private String phone;
    private String secondPhone;
    private String otherContact;
    private AppGender gender;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AppDepartment department;
    private AppCivility civitity;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AppPeriod firstSubscription;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AcademicClass lastClass1;
    private AcademicClass lastClass2;
    private AcademicClass lastClass3;
    private AppUser user;

    private String phone1;
    private String phone2;
    private String phone3;

    @Field(defaultValue = "false")
    private boolean deleted;
    private String deletedBy;
    private Timestamp deletedOn;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AppGender getGender() {
        return gender;
    }

    public void setGender(AppGender gender) {
        this.gender = gender;
    }

    public AppCivility getCivitity() {
        return civitity;
    }

    public void setCivitity(AppCivility civitity) {
        this.civitity = civitity;
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

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    @Override
    public String toString() {
        return "Teacher{" + "id=" + id + ", name=" + name + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", gender=" + gender + ", civitity=" + civitity + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.id;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.firstName);
        hash = 59 * hash + Objects.hashCode(this.lastName);
        hash = 59 * hash + Objects.hashCode(this.email);
        hash = 59 * hash + Objects.hashCode(this.gender);
        hash = 59 * hash + Objects.hashCode(this.civitity);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AcademicStudent other = (AcademicStudent) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.gender, other.gender)) {
            return false;
        }
        if (!Objects.equals(this.civitity, other.civitity)) {
            return false;
        }
        return true;
    }

    public static String getName(AcademicStudent t) {
        String n = t.getName();
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
            s.append("SANS NOM");
        }
        return s.toString();
    }

    public static String getName2(AcademicStudent t) {
        String n = t.getName();
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

    public AppPeriod getFirstSubscription() {
        return firstSubscription;
    }

    public void setFirstSubscription(AppPeriod firstSubscription) {
        this.firstSubscription = firstSubscription;
    }

    public String getSecondEmail() {
        return secondEmail;
    }

    public void setSecondEmail(String secondEmail) {
        this.secondEmail = secondEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecondPhone() {
        return secondPhone;
    }

    public void setSecondPhone(String secondPhone) {
        this.secondPhone = secondPhone;
    }

    public String getOtherContact() {
        return otherContact;
    }

    public void setOtherContact(String otherContact) {
        this.otherContact = otherContact;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
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

    public void setLastClass1(AcademicClass lastLevel1) {
        this.lastClass1 = lastLevel1;
    }

    public void setLastClass2(AcademicClass lastLevel2) {
        this.lastClass2 = lastLevel2;
    }

    public void setLastClass3(AcademicClass lastLevel3) {
        this.lastClass3 = lastLevel3;
    }

    public AcademicClass getLastClass1() {
        return lastClass1;
    }

    public AcademicClass getLastClass2() {
        return lastClass2;
    }

    public AcademicClass getLastClass3() {
        return lastClass3;
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

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

    public String getSubscriptionNumber() {
        return subscriptionNumber;
    }

    public void setSubscriptionNumber(String subscriptionNumber) {
        this.subscriptionNumber = subscriptionNumber;
    }

}
