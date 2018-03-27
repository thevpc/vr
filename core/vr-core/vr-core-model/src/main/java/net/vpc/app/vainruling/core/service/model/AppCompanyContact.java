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
@Entity()
@Path("Contact")
@Properties(
        {
                @Property(name = "ui.auto-filter.company", value = "{expr='this.company',order=1}"),
                @Property(name = "ui.auto-filter.positionTitle1", value = "{expr='this.positionTitle1',order=2}"),
        })
public class AppCompanyContact {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private AppCompany company;
    private AppContact contact;
    private String positionTitle1;
    private String positionTitle2;
    private String positionTitle3;
    private boolean mainCompanyContact;
    private boolean mainContactCompany;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppCompanyContact that = (AppCompanyContact) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
