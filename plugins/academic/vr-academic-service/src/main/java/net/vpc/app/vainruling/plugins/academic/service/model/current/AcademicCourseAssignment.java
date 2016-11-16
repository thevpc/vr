/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Education/Load")
@Properties(
        {
                @Property(name = "ui.auto-filter.period", value = "{expr='coursePlan.period',order=1}"),
                @Property(name = "ui.auto-filter.department", value = "{expr='coursePlan.courseLevel.academicClass.program.department',order=2}"),
                @Property(name = "ui.auto-filter.ownerDepartment", value = "{expr='ownerDepartment',order=3}"),
                @Property(name = "ui.auto-filter.programType", value = "{expr='coursePlan.courseLevel.academicClass.program.programType',order=4}"),
                @Property(name = "ui.auto-filter.program", value = "{expr='coursePlan.courseLevel.academicClass.program',order=5}"),
                @Property(name = "ui.auto-filter.courseType", value = "{expr='courseType',order=6}"),
                @Property(name = "ui.auto-filter.class", value = "{expr='coursePlan.courseLevel.academicClass',order=7}"),
                @Property(name = "ui.auto-filter.teacher", value = "{expr='teacher',order=8}")
        }
)
public class AcademicCourseAssignment {

    @Id
    @Sequence

    private int id;

    @Summary
    @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:10%")
    @Formula(
            value = "(this.coursePlan.period.name)",
            type = {FormulaType.PERSIST, FormulaType.UPDATE}
    )
    private String periodLabel;

    @Formula(
            value = "concat((select a.name from AcademicCoursePlan a where a.id=this.coursePlanId),'-',(select a.name from AcademicCourseType a where a.id=this.courseTypeId))",
            type = {FormulaType.PERSIST, FormulaType.UPDATE}
    )
    private String name;

    @Main
    @Formula(
            value = "concat(" +
                    "(select a.fullName from AcademicCoursePlan a where a.id=this.coursePlanId)" +
                    ",'-',Coalesce((select a.Name from AcademicClass a where a.id=this.subClassId),'?')" +
                    ",Coalesce(concat('-',this.discriminator),'')" +
                    ",'-',(select a.name from AcademicCourseType a where a.id=this.courseTypeId)" +
                    ")",
            type = {FormulaType.PERSIST, FormulaType.UPDATE}
    )
    private String fullName;

    @Properties(
            {
                    @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private String name2;

    @Properties(
            {
                    @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private AcademicCoursePlan coursePlan;

    @Summary
    @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:10%")
    private AcademicClass subClass;
    /**
     * this field helps differentiating assignments with same properties
     */
    private String discriminator;


    @Summary
    @Properties(
            {
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:10%")
                    , @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private AcademicTeacher teacher;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private double valueC;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private double valueTD;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private double valueTP;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private double valuePM;
//    private double valueDU;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private double valueEffWeek;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private double groupCount = 1;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private AcademicCourseType courseType;
    /**
     * number of teachers or Masters sharing the same Course load
     */
    private double shareCount = 1;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:10%")
    )
    private AppDepartment ownerDepartment;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", type = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    private boolean confirmedTeacher;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
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

    public double getValueEffWeek() {
        return valueEffWeek;
    }

    public void setValueEffWeek(double valueEffWeek) {
        this.valueEffWeek = valueEffWeek;
    }

    public double getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(double groupCount) {
        this.groupCount = groupCount;
    }

    public AcademicCourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(AcademicCourseType courseType) {
        this.courseType = courseType;
    }

    public double getShareCount() {
        return shareCount;
    }

    public void setShareCount(double shareCount) {
        this.shareCount = shareCount;
    }

    public AcademicCoursePlan getCoursePlan() {
        return coursePlan;
    }

    public void setCoursePlan(AcademicCoursePlan coursePlan) {
        this.coursePlan = coursePlan;
    }

    @Override
    public String toString() {
        return "AcademicCourseAssignment{" + "id=" + id + ", name=" + name + ", name2=" + name2 + ", teacher=" + teacher + ", valueC=" + valueC + ", valueTD=" + valueTD + ", valueTP=" + valueTP + ", valuePM=" + valuePM + ", valueEffWeek=" + valueEffWeek + ", groupCount=" + groupCount + ", courseType=" + courseType + ", shareCount=" + shareCount + ", coursePlan=" + coursePlan + '}';
    }

    public AppDepartment getOwnerDepartment() {
        return ownerDepartment;
    }

    public void setOwnerDepartment(AppDepartment ownerDepartment) {
        this.ownerDepartment = ownerDepartment;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public AcademicClass getSubClass() {
        return subClass;
    }

    public void setSubClass(AcademicClass subClass) {
        this.subClass = subClass;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
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

    public boolean isConfirmedTeacher() {
        return confirmedTeacher;
    }

    public void setConfirmedTeacher(boolean confirmedTeacher) {
        this.confirmedTeacher = confirmedTeacher;
    }
}
