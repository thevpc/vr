/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.model;

import java.sql.Timestamp;
import net.vpc.upa.PasswordStrategyType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Password;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.types.DateTime;

/**
 *
 * @author vpc
 */
@Entity(listOrder = "login")
@Path("Admin/Security")
public class AppUser {

    @Id
    @Sequence
    private int id;
    @Password(strategyType = PasswordStrategyType.MD5)
    private String password;

    @Field(modifiers = {UserFieldModifier.UNIQUE})
    private String login;

    @Field(modifiers = {UserFieldModifier.MAIN})
    private String fullName;
    private String firstName;
    private String lastName;
    /**
     * National Identity Number
     */
    private String nin;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private String email;
    private AppGender gender;
    private AppCivility civitity;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AppDepartment department;

    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AppUserType type;

    private DateTime lastConnexionDate;
    @Field(defaultValue = "0")
    private long connexionCount;
    private AppCompany company;
    private String positionTitle1;
    private String positionTitle2;
    private String positionTitle3;
    @Field(defaultValue = "false")
    private boolean deleted;
    @Field(defaultValue = "true")
    private boolean enabled;
    private String deletedBy;
    private Timestamp deletedOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public AppCivility getCivitity() {
        return civitity;
    }

    public void setCivitity(AppCivility civitity) {
        this.civitity = civitity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

    public AppUserType getType() {
        return type;
    }

    public void setType(AppUserType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AppUser{" + "id=" + id + ", password=" + password + ", login=" + login + ", fullName=" + fullName + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", gender=" + gender + ", civitity=" + civitity + ", department=" + department + ", type=" + type + '}';
    }

    public DateTime getLastConnexionDate() {
        return lastConnexionDate;
    }

    public void setLastConnexionDate(DateTime lastConnexionDate) {
        this.lastConnexionDate = lastConnexionDate;
    }

    public long getConnexionCount() {
        return connexionCount;
    }

    public void setConnexionCount(long connexionCount) {
        this.connexionCount = connexionCount;
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

}
