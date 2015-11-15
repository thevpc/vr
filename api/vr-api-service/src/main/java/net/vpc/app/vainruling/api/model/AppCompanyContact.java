/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity()
@Path("Contact")
public class AppCompanyContact {

    @Id
    @Sequence

    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private AppCompany company;
    private AppContact contact;
    private String positionTitle1;
    private String positionTitle2;
    private String positionTitle3;
    private boolean mainCompanyContact;
    private boolean mainContactCompany;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppCompany getCompany() {
        return company;
    }

    public void setCompany(AppCompany company) {
        this.company = company;
    }

    public AppContact getContact() {
        return contact;
    }

    public void setContact(AppContact contact) {
        this.contact = contact;
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

    public boolean isMainCompanyContact() {
        return mainCompanyContact;
    }

    public void setMainCompanyContact(boolean mainCompanyContact) {
        this.mainCompanyContact = mainCompanyContact;
    }

    public boolean isMainContactCompany() {
        return mainContactCompany;
    }

    public void setMainContactCompany(boolean mainContactCompany) {
        this.mainContactCompany = mainContactCompany;
    }

}
