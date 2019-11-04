/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.current;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Repository/Education")
public class AcademicTeacherDegree {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Summary
    private String code;
    @Main
    private String name;
    private String name2;
    private String name3;
    @Summary
    private AcademicLoadConversionRule conversionRule;

    @Summary
    private double valueDU;

    //    @Summary
//    private double valueC;
//    @Summary
//    private double valueTD;
//    @Summary
//    private double valueTP;
//    @Summary
//    private double valuePM;
//    @Summary
    private int position;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    public AcademicTeacherDegree() {
    }

//    public AcademicTeacherDegree(String name, double valueC, double valueTD, double valueTP, double valuePM, double valueDU, int position) {
//        this.name = name;
//        this.valueC = valueC;
//        this.valueTD = valueTD;
//        this.valueTP = valueTP;
//        this.valueDU = valueDU;
//        this.valuePM = valuePM;
//        this.position = position;
//    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

//    public double getValueC() {
//        return valueC;
//    }
//
//    public void setValueC(double valueC) {
//        this.valueC = valueC;
//    }
//
//    public double getValueTD() {
//        return valueTD;
//    }
//
//    public void setValueTD(double valueTD) {
//        this.valueTD = valueTD;
//    }
//
//    public double getValueTP() {
//        return valueTP;
//    }
//
//    public void setValueTP(double valueTP) {
//        this.valueTP = valueTP;
//    }

    public double getValueDU() {
        return valueDU;
    }

    public void setValueDU(double valueDU) {
        this.valueDU = valueDU;
    }

//    public double getValuePM() {
//        return valuePM;
//    }
//
//    public void setValuePM(double valuePM) {
//        this.valuePM = valuePM;
//    }


    @Override
    public String toString() {
        return "AcademicTeacherDegree{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", name2='" + name2 + '\'' +
                ", name3='" + name3 + '\'' +
                ", conversionRule=" + conversionRule +
                ", valueDU=" + valueDU +
                ", position=" + position +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AcademicTeacherDegree)) return false;

        AcademicTeacherDegree that = (AcademicTeacherDegree) o;

        if (id != that.id) return false;
        if (Double.compare(that.valueDU, valueDU) != 0) return false;
        if (position != that.position) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (name2 != null ? !name2.equals(that.name2) : that.name2 != null) return false;
        if (name3 != null ? !name3.equals(that.name3) : that.name3 != null) return false;
        if (conversionRule != null ? !conversionRule.equals(that.conversionRule) : that.conversionRule != null)
            return false;
        if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null) return false;
        return !(updateDate != null ? !updateDate.equals(that.updateDate) : that.updateDate != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (name2 != null ? name2.hashCode() : 0);
        result = 31 * result + (name3 != null ? name3.hashCode() : 0);
        result = 31 * result + (conversionRule != null ? conversionRule.hashCode() : 0);
        temp = Double.doubleToLongBits(valueDU);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + position;
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        return result;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
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

    public AcademicLoadConversionRule getConversionRule() {
        return conversionRule;
    }

    public void setConversionRule(AcademicLoadConversionRule conversionRule) {
        this.conversionRule = conversionRule;
    }
}
