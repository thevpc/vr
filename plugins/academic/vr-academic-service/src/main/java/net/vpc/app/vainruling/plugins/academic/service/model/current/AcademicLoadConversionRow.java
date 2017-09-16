/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.AccessLevel;
import net.vpc.upa.FormulaType;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Education/Config")
@Properties(
        {
                @Property(name = "ui.auto-filter.conversionTable", value = "{expr='this.conversionTable',order=1}")
        }
)
public class AcademicLoadConversionRow {

    @Id
    @Sequence
    private int id;

    @Main
    @Formula(value = "concat(coalesce(this.conversionTable.name),'-',coalesce(this.rule.name))",formulaOrder = 1)
    private String name;

    @Summary
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicLoadConversionTable conversionTable;
    @Summary
    private AcademicLoadConversionRule rule;
    @Summary
    private double valueC;
    @Summary
    private double valueTD;
    @Summary
    private double valueTP;
    @Summary
    private double valuePM;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Field(updateAccessLevel = AccessLevel.PRIVATE,excludeModifiers = UserFieldModifier.UPDATE)
    @Formula(value = "CurrentTimestamp()", type = FormulaType.PERSIST)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicLoadConversionRow() {
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

    public AcademicLoadConversionTable getConversionTable() {
        return conversionTable;
    }

    public void setConversionTable(AcademicLoadConversionTable conversionTable) {
        this.conversionTable = conversionTable;
    }

    public AcademicLoadConversionRule getRule() {
        return rule;
    }

    public void setRule(AcademicLoadConversionRule rule) {
        this.rule = rule;
    }

    public double getValueC() {
        return valueC;
    }

    public void setValueC(double valueC) {
        this.valueC = valueC;
    }

    public double getValueTD() {
        return valueTD;
    }

    public void setValueTD(double valueTD) {
        this.valueTD = valueTD;
    }

    public double getValueTP() {
        return valueTP;
    }

    public void setValueTP(double valueTP) {
        this.valueTP = valueTP;
    }

    public double getValuePM() {
        return valuePM;
    }

    public void setValuePM(double valuePM) {
        this.valuePM = valuePM;
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
        return "AcademicLoadConversionRow{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", conversionTable=" + conversionTable +
                ", rule=" + rule +
                ", valueC=" + valueC +
                ", valueTD=" + valueTD +
                ", valueTP=" + valueTP +
                ", valuePM=" + valuePM +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AcademicLoadConversionRow)) return false;

        AcademicLoadConversionRow that = (AcademicLoadConversionRow) o;

        if (id != that.id) return false;
        if (Double.compare(that.valueC, valueC) != 0) return false;
        if (Double.compare(that.valueTD, valueTD) != 0) return false;
        if (Double.compare(that.valueTP, valueTP) != 0) return false;
        if (Double.compare(that.valuePM, valuePM) != 0) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (conversionTable != null ? !conversionTable.equals(that.conversionTable) : that.conversionTable != null)
            return false;
        if (rule != null ? !rule.equals(that.rule) : that.rule != null) return false;
        if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null) return false;
        return !(updateDate != null ? !updateDate.equals(that.updateDate) : that.updateDate != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (conversionTable != null ? conversionTable.hashCode() : 0);
        result = 31 * result + (rule != null ? rule.hashCode() : 0);
        temp = Double.doubleToLongBits(valueC);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(valueTD);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(valueTP);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(valuePM);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        return result;
    }
}
