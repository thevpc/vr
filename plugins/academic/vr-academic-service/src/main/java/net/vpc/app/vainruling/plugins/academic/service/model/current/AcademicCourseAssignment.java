/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/Load")
@Properties(
        {
            @Property(name = "ui.auto-filter.period", value = "{expr='this.coursePlan.period',order=1}")
            ,
                @Property(name = "ui.auto-filter.department", value = "{expr='this.coursePlan.courseLevel.academicClass.program.department',order=2}")
            ,
                @Property(name = "ui.auto-filter.ownerDepartment", value = "{expr='this.ownerDepartment',order=3}")
            ,
                @Property(name = "ui.auto-filter.programType", value = "{expr='this.coursePlan.courseLevel.academicClass.program.programType',order=4}")
            ,
                @Property(name = "ui.auto-filter.program", value = "{expr='this.coursePlan.courseLevel.academicClass.program',order=5}")
            ,
                @Property(name = "ui.auto-filter.courseType", value = "{expr='this.courseType',order=6}")
            ,
                @Property(name = "ui.auto-filter.class", value = "{expr='this.coursePlan.courseLevel.academicClass',order=7}")
            ,
                @Property(name = "ui.auto-filter.semester", value = "{expr='this.coursePlan.courseLevel.semester',order=8}")
            ,
                @Property(name = "ui.auto-filter.teacher", value = "{expr='this.teacher',order=9}")
        }
)
public class AcademicCourseAssignment {

    @Path("Main")
    @Id
    @Sequence

    private int id;

    @Summary
    @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:10%")
    @Formula(
            value = "(this.coursePlan.period.name)", formulaOrder = 1,
            formulaType = {FormulaType.PERSIST, FormulaType.UPDATE}
    )
    private String periodLabel;

    @Formula(
            value = "concat(coalesce((select a.name from AcademicCoursePlan a where a.id=this.coursePlanId),'?'),'-',coalesce((select a.name from AcademicCourseType a where a.id=this.courseTypeId),'?'))", formulaOrder = 1,
            formulaType = {FormulaType.PERSIST, FormulaType.UPDATE}
    )
    private String name;

    @Main
    @Formula(
            value = "concat("
            + "(select a.fullName from AcademicCoursePlan a where a.id=this.coursePlanId)"
            + ",'-',Coalesce((select a.Name from AcademicClass a where a.id=this.subClassId),'?')"
            + ",Coalesce(concat('-',this.discriminator),'')"
            + ",'-',coalesce((select a.name from AcademicCourseType a where a.id=this.courseTypeId),'?')"
            + ")", formulaOrder = 1,
            formulaType = {FormulaType.PERSIST, FormulaType.UPDATE}
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
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    private AcademicCourseType courseType;

    @Summary
    @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:10%")
    private AcademicClass subClass;

    private boolean locked;

    /**
     * this field helps differentiating assignments with same properties
     */
    private String discriminator;

    private String labels;

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

    /**
     * number of teachers or Masters sharing the same Course load
     */
    private double shareCount = 1;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:10%")
    )
    private AppDepartment ownerDepartment;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
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

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public AcademicProgram resolveProgram() {
        return getCoursePlan() == null ? null : getCoursePlan().resolveProgram();
    }

    public AcademicProgramType resolveProgramType() {
        return getCoursePlan() == null ? null : getCoursePlan().resolveProgramType();
    }

    public AppDepartment resolveDepartment() {
        return getCoursePlan() == null ? null : getCoursePlan().resolveDepartment();
    }

    public AppDepartment resolveOwnerDepartment() {
        AppDepartment d = getOwnerDepartment();
        if (d != null) {
            return d;
        }
        return resolveDepartment();
    }

    public AcademicSemester resolveSemester() {
        return getCoursePlan() == null ? null : getCoursePlan().resolveSemester();
    }

    public AcademicCourseLevel resolveCourseLevel() {
        return getCoursePlan() == null ? null : getCoursePlan().getCourseLevel();
    }

    public AcademicCourseGroup resolveCourseGroup() {
        return getCoursePlan() == null ? null : getCoursePlan().getCourseGroup();
    }

    public AppPeriod resolvePeriod() {
        AcademicCoursePlan coursePlan = getCoursePlan();
        if(coursePlan!=null){
            return coursePlan.getPeriod();
        }
        return null;
    }

    public AcademicClass resolveAcademicClass() {
        AcademicClass sc = getSubClass();
        if (sc != null) {
            return sc;
        }
        return getCoursePlan().resolveAcademicClass();
    }

    public AppUser resolveUser() {
        AcademicTeacher sc = getTeacher();
        if (sc != null) {
            return sc.getUser();
        }
        return null;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
