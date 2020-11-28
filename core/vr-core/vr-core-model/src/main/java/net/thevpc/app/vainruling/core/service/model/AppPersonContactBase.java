/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.model;

import java.util.Date;
import net.thevpc.upa.config.Formula;
import net.thevpc.upa.config.Main;
import net.thevpc.upa.config.Path;
import net.thevpc.upa.config.Summary;

/**
 *
 * @author vpc
 */
public class AppPersonContactBase extends AppContactBase {

    private AppCivility civility;

    private String passportNumber;

    private String firstName;

    private String lastName;

    private String firstName2;

    private String lastName2;

    private AppGender gender;

    @Main
    @Formula(value = "concat(Coalesce(this.fullName,''),' - ',Coalesce(this.positionSuffix,'?'))", formulaOrder = 1)

    private String fullTitle;

    private String positionSuffix;

    private Date birthDate;

    private String birthLocation;

    private AppGovernorate birthGovernorate;
    @Path("Position")
    @Summary

    private String positionTitle1;
    @Summary

    private String positionTitle2;

    private String positionTitle3;

    private String bankRib;

    private String socialSecurityId;

    @Override
    public void copyFrom(AppContactBase other) {
        if (other != null) {
            super.copyFrom(other);
            this.firstName = other.getFullName();
            this.lastName = "";
            this.fullTitle = other.getFullName();
            if (other instanceof AppPersonContactBase) {
                AppPersonContactBase o = (AppPersonContactBase) other;
                super.copyFrom(other);
                this.passportNumber = o.passportNumber;
                this.civility = o.civility;
                this.firstName = o.firstName;
                this.lastName = o.lastName;
                this.fullTitle = o.fullTitle;
                this.positionSuffix = o.positionSuffix;
                this.firstName2 = o.firstName2;
                this.lastName2 = o.lastName2;
                this.gender = o.gender;
                this.positionTitle1 = o.positionTitle1;
                this.positionTitle2 = o.positionTitle2;
                this.positionTitle3 = o.positionTitle3;
                this.birthDate = o.birthDate;
                this.birthLocation = o.birthLocation;
                this.birthGovernorate = o.birthGovernorate;
                this.bankRib = o.bankRib;
                this.socialSecurityId = o.socialSecurityId;
            }
        }
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

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String resolveName() {
        String n = getFullName();
        if (n != null && n.trim().length() > 0) {
            return n.trim();
        }
        StringBuilder s = new StringBuilder();
        if (getFirstName() != null && getFirstName().trim().length() > 0) {
            s.append(getFirstName().trim());
        }
        if (getLastName() != null && getLastName().trim().length() > 0) {
            if (s.length() > 0) {
                s.append(" ");
            }
            s.append(getLastName().trim());
        }
        if (s.length() == 0) {
            s.append("San Nom");
        }
        return s.toString();
    }

    public String resolvetName2() {
        String n = getFullName2();
        if (n != null && n.trim().length() > 0) {
            return n.trim();
        }
        StringBuilder s = new StringBuilder();
        if (getFirstName2() != null && getFirstName2().trim().length() > 0) {
            s.append(getFirstName2().trim());
        }
        if (getLastName2() != null && getLastName2().trim().length() > 0) {
            if (s.length() > 0) {
                s.append(" ");
            }
            s.append(getLastName2().trim());
        }
        if (s.length() == 0) {
            s.append(resolveName());
        }
        return s.toString();
    }

    public String getBankRib() {
        return bankRib;
    }

    public void setBankRib(String bankRib) {
        this.bankRib = bankRib;
    }

    public String getSocialSecurityId() {
        return socialSecurityId;
    }

    public void setSocialSecurityId(String socialSecurityId) {
        this.socialSecurityId = socialSecurityId;
    }

}
