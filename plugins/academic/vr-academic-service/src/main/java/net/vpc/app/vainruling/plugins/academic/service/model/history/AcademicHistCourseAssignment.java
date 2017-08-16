/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.history;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Education/History")
public class AcademicHistCourseAssignment {

    @Id
    @Sequence

    private int id;
    @Main
    private String name;
    private String name2;
    @Summary
    private AcademicTeacher teacher;
    @Summary
    private double valueC;
    @Summary
    private double valueTD;
    @Summary
    private double valueTP;
    @Summary
    private double valuePM;
    //    private double valueDU;
    @Summary
    private double valueEffWeek;
    @Summary
    private double groupCount = 1;
    @Summary
    private AcademicCourseType courseType;
    /**
     * number of teachers or Masters sharing the same Course load
     */
    private double shareCount = 1;

    private AcademicCoursePlan coursePlan;
    @Summary
    private AppPeriod academicYear;

    private AppDepartment ownerDepartment;

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
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

    public AcademicCoursePlan getCoursePlan() {
        return coursePlan;
    }

    public void setCoursePlan(AcademicCoursePlan coursePlan) {
        this.coursePlan = coursePlan;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
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

    //    public double getValueDU() {
//        return valueDU;
//    }
//
//    public void setValueDU(double valueDU) {
//        this.valueDU = valueDU;
//    }
    public double getValuePM() {
        return valuePM;
    }

    public void setValuePM(double valuePM) {
        this.valuePM = valuePM;
    }

    public double getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(double groupCount) {
        this.groupCount = groupCount;
    }

    @Override
    public String toString() {
        return "Module{" + "name=" + name + ", cours=" + coursePlan + ", teacher=" + teacher + ", valueC=" + valueC + ", valueTD=" + valueTD + ", valueTP=" + valueTP + ", valuePM=" + valuePM + ", valueWeek=" + valueEffWeek + ", nbGroups=" + groupCount + '}';
    }

    public double getValueEffWeek() {
        return valueEffWeek;
    }

    public void setValueEffWeek(double valueEffWeek) {
        this.valueEffWeek = valueEffWeek;
    }

    public double getShareCount() {
        return shareCount;
    }

    public void setShareCount(double shareCount) {
        this.shareCount = shareCount;
    }

    public AcademicCourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(AcademicCourseType courseType) {
        this.courseType = courseType;
    }

    public AppPeriod getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AppPeriod academicYear) {
        this.academicYear = academicYear;
    }

    public AppDepartment getOwnerDepartment() {
        return ownerDepartment;
    }

    public void setOwnerDepartment(AppDepartment ownerDepartment) {
        this.ownerDepartment = ownerDepartment;
    }

    public AcademicSemester resolveSemester() {
        AcademicCoursePlan coursePlan = getCoursePlan();
        if(coursePlan!=null){
            return coursePlan.resolveSemester();
        }
        return null;
    }
}
