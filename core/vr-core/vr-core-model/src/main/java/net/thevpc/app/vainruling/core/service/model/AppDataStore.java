/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.model;

import net.thevpc.upa.FormulaType;
import net.thevpc.upa.UserFieldModifier;
import net.thevpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.idName")
@Path("Admin/Config")
public class AppDataStore {

    @Path("Main")
    @Id
    @Main
    @Field(max = "255")
    private String idName;
    @Field(max = "maximum")
    private String propertyValue;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST})
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AppDataStore() {
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
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

        AppDataStore that = (AppDataStore) o;

        return idName != null ? idName.equals(that.idName) : that.idName == null;
    }

    @Override
    public int hashCode() {
        return idName != null ? idName.hashCode() : 0;
    }
}
