/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import java.sql.Timestamp;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Formula;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.config.Sequence;

/**
 * cours (dans un plan d'études) Module
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Education")
public class AcademicCoursePlan {

    @Id
    @Sequence

    private int id;
    @Field(defaultValue = "?")
    private String code;
    private String name;
    @Field(modifiers = {UserFieldModifier.MAIN})
    @Formula(
            value = "concat((select a.name from AcademicCourseLevel a where a.id=this.courseLevelId),'-',Coalesce(this.code,'X'),'-',this.name)",
            type = {FormulaType.PERSIST, FormulaType.UPDATE}
    )
    private String fullName;

    private String name2;


//    @Field(modifiers = {UserFieldModifier.SUMMARY})
//    @Properties(
//            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
//    )
//    private AcademicProgram program;

    private String discipline;

//    @Field(modifiers = {UserFieldModifier.SUMMARY})
//    private AcademicClass studentClass;

    @Field(modifiers = {UserFieldModifier.SUMMARY})
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private AcademicCourseLevel courseLevel;

//    @Field(modifiers = {UserFieldModifier.SUMMARY})
//    @Properties(
//            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
//    )
//    private AcademicSemester semester;

    @Field(modifiers = {UserFieldModifier.SUMMARY})
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private double valueC;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private double valueTD;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private double valueTP;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private double valuePM;
    private double valueTPPM;
    @Field(defaultValue = "0")
    private double credits;
    @Field(defaultValue = "0")
    private double ects;
//    private double valueDU;
    //Unite enseignement/UE
//    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AcademicCourseGroup courseGroup;
    private int position;
    private int groupCountC = 1;
    private int groupCountTD = 1;
    private int groupCountTP = 0;
    private int groupCountPM = 0;
    private int groupCountTPPM = 0;
    private int weeksC = 0;
    private int weeksTD = 0;
    private int weeksTP = 0;
    private int weeksPM = 0;
    private int weeksTPPM = 0;
    //@Field(modifiers = UserFieldModifier.SUMMARY)
    private String roomConstraintsC;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private String roomConstraintsTP;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()",type = FormulaType.PERSIST)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()",type = {FormulaType.PERSIST,FormulaType.UPDATE})
    private Timestamp updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public AcademicCourseGroup getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(AcademicCourseGroup courseGroup) {
        this.courseGroup = courseGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public AcademicProgram getProgram() {
//        return program;
//    }
//
//    public void setProgram(AcademicProgram program) {
//        this.program = program;
//    }
//
//    public AcademicClass getStudentClass() {
//        return studentClass;
//    }
//
//    public void setStudentClass(AcademicClass studentClass) {
//        this.studentClass = studentClass;
//    }

    public AcademicCourseLevel getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(AcademicCourseLevel courseLevel) {
        this.courseLevel = courseLevel;
    }

//    public AcademicSemester getSemester() {
//        return semester;
//    }
//
//    public void setSemester(AcademicSemester semester) {
//        this.semester = semester;
//    }

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

    @Override
    public String toString() {
        return "CoursePlan{" + "name=" + name 
//                + ", department=" + program 
//                + ", studentClass=" + studentClass 
                + ", moduleLevel=" + courseLevel 
//                + ", semester=" + semester 
                + ", valueC=" + valueC + ", valueTD=" + valueTD + ", valueTP=" + valueTP + ", valuePM=" + valuePM + '}';
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getGroupCountC() {
        return groupCountC;
    }

    public void setGroupCountC(int groupCountC) {
        this.groupCountC = groupCountC;
    }

    public int getGroupCountTD() {
        return groupCountTD;
    }

    public void setGroupCountTD(int groupCountTD) {
        this.groupCountTD = groupCountTD;
    }

    public int getGroupCountTP() {
        return groupCountTP;
    }

    public void setGroupCountTP(int groupCountTP) {
        this.groupCountTP = groupCountTP;
    }

    public int getGroupCountPM() {
        return groupCountPM;
    }

    public void setGroupCountPM(int groupCountPM) {
        this.groupCountPM = groupCountPM;
    }

    public double getValueTPPM() {
        return valueTPPM;
    }

    public void setValueTPPM(double valueTPPM) {
        this.valueTPPM = valueTPPM;
    }

    public int getGroupCountTPPM() {
        return groupCountTPPM;
    }

    public void setGroupCountTPPM(int groupCountTPPM) {
        this.groupCountTPPM = groupCountTPPM;
    }

    public int getWeeksC() {
        return weeksC;
    }

    public void setWeeksC(int weeksC) {
        this.weeksC = weeksC;
    }

    public int getWeeksTD() {
        return weeksTD;
    }

    public void setWeeksTD(int weeksTD) {
        this.weeksTD = weeksTD;
    }

    public int getWeeksTP() {
        return weeksTP;
    }

    public void setWeeksTP(int weeksTP) {
        this.weeksTP = weeksTP;
    }

    public int getWeeksPM() {
        return weeksPM;
    }

    public void setWeeksPM(int weeksPM) {
        this.weeksPM = weeksPM;
    }

    public int getWeeksTPPM() {
        return weeksTPPM;
    }

    public void setWeeksTPPM(int weeksTPPM) {
        this.weeksTPPM = weeksTPPM;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRoomConstraintsC() {
        return roomConstraintsC;
    }

    public void setRoomConstraintsC(String roomConstraintsC) {
        this.roomConstraintsC = roomConstraintsC;
    }

    public String getRoomConstraintsTP() {
        return roomConstraintsTP;
    }

    public void setRoomConstraintsTP(String roomConstraintsTP) {
        this.roomConstraintsTP = roomConstraintsTP;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public double getEcts() {
        return ects;
    }

    public void setEcts(double ects) {
        this.ects = ects;
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
    

    
}
