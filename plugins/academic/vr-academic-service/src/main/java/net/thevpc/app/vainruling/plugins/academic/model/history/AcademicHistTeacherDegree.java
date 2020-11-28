/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.model.history;

import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionRule;
import net.thevpc.upa.config.*;

import java.util.Objects;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/History")
public class AcademicHistTeacherDegree {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private String code;
    @Summary
    private String name;
    private String name2;

    private AcademicLoadConversionRule conversionRule;

    @Summary
    private double valueC;
    @Summary
    private double valueTD;
    @Summary
    private double valueTP;
    @Summary
    private double valueDU;
    @Summary
    private double valuePM;
    @Summary
    private int position;
    @Summary
    private AppPeriod academicYear;

    public AcademicHistTeacherDegree() {
    }

    public AcademicHistTeacherDegree(AppPeriod academicYear, String name, double valueC, double valueTD, double valueTP, double valuePM, double valueDU, int position) {
        this.academicYear = academicYear;
        this.name = name;
        this.valueC = valueC;
        this.valueTD = valueTD;
        this.valueTP = valueTP;
        this.valueDU = valueDU;
        this.valuePM = valuePM;
        this.position = position;
    }

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

    public double getValueDU() {
        return valueDU;
    }

    public void setValueDU(double valueDU) {
        this.valueDU = valueDU;
    }

    public double getValuePM() {
        return valuePM;
    }

    public void setValuePM(double valuePM) {
        this.valuePM = valuePM;
    }

    public AppPeriod getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AppPeriod academicYear) {
        this.academicYear = academicYear;
    }

    @Override
    public String toString() {
        return "TeacherDegree{" + "name=" + name + ", valueC=" + valueC + ", valueTD=" + valueTD + ", valueTP=" + valueTP + ", valueDU=" + valueDU + ", valuePM=" + valuePM + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.id;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.valueC) ^ (Double.doubleToLongBits(this.valueC) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.valueTD) ^ (Double.doubleToLongBits(this.valueTD) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.valueTP) ^ (Double.doubleToLongBits(this.valueTP) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.valueDU) ^ (Double.doubleToLongBits(this.valueDU) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.valuePM) ^ (Double.doubleToLongBits(this.valuePM) >>> 32));
        hash = 59 * hash + Objects.hashCode(this.academicYear);
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
        final AcademicHistTeacherDegree other = (AcademicHistTeacherDegree) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valueC) != Double.doubleToLongBits(other.valueC)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valueTD) != Double.doubleToLongBits(other.valueTD)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valueTP) != Double.doubleToLongBits(other.valueTP)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valueDU) != Double.doubleToLongBits(other.valueDU)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valuePM) != Double.doubleToLongBits(other.valuePM)) {
            return false;
        }
        return Objects.equals(this.academicYear, other.academicYear);
    }

    public AcademicLoadConversionRule getConversionRule() {
        return conversionRule;
    }

    public void setConversionRule(AcademicLoadConversionRule conversionRule) {
        this.conversionRule = conversionRule;
    }
}
