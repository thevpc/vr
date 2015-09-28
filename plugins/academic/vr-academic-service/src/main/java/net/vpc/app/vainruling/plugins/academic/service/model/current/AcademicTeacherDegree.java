/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import java.util.Objects;
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
@Entity(listOrder = "name")
@Path("Education/Config")
public class AcademicTeacherDegree {

    @Id
    @Sequence

    private int id;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private String code;
    @Field(modifiers = {UserFieldModifier.MAIN})
    private String name;
    private String name2;
    private String name3;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private double valueC;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private double valueTD;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private double valueTP;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private double valueDU;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private double valuePM;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private int position;

    public AcademicTeacherDegree() {
    }

    public AcademicTeacherDegree(String name, double valueC, double valueTD, double valueTP, double valuePM, double valueDU, int position) {
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
        final AcademicTeacherDegree other = (AcademicTeacherDegree) obj;
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
        return true;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

}
