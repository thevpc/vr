/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicOfficialDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;

/**
 * cours (dans un plan d'études) Module
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.period.name desc, this.fullName, this.name")
@Path("Education/StudyPlan")
@Properties(
        {
            @Property(name = "ui.auto-filter.period", value = "{expr='this.period',order=1}")
            ,
            @Property(name = "ui.auto-filter.department", value = "{expr='this.courseLevel.academicClass.program.department',order=2}")
            ,
            @Property(name = "ui.auto-filter.program", value = "{expr='this.courseLevel.academicClass.program',order=3}")
            ,
            @Property(name = "ui.auto-filter.programType", value = "{expr='this.courseLevel.academicClass.program.programType',order=4}")
            ,
            @Property(name = "ui.auto-filter.semester", value = "{expr='this.courseLevel.semester',order=5}")
            ,
            @Property(name = "ui.auto-filter.class", value = "{expr='this.courseLevel.academicClass',order=6}")
        }
)
public class AcademicCoursePlan {

    @Path("Main")
    @Id
    @Sequence

    private int id;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:120px")
    )
    private AppPeriod period;

    @Field(defaultValue = "?")
    private String code;
    @Properties(
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    )
    private String name;
    @Main
    @Formula(
            value = "concat((select a.name from AppPeriod a where a.id=this.periodId),'-',(select a.name from AcademicCourseLevel a where a.id=this.courseLevelId),'-',Coalesce(this.code,'X'),'-',this.name)",
            formulaType = {FormulaType.PERSIST, FormulaType.UPDATE},
            formulaOrder = 1
    )
    private String fullName;

    @Properties(
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    )
    private String name2;

//    @Summary
//    @Properties(
//            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
//    )
//    private AcademicProgram program;
    private String discipline;

    private AcademicOfficialDiscipline officialDiscipline;

//    @Summary
//    private AcademicClass studentClass;
    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:120px")
    )
    private AcademicCourseLevel courseLevel;

    @Summary
    private AcademicTeacher responsible;
    private AcademicTeacher responsible2;
    private String responsibleAvailability;

//    @Summary
//    @Properties(
//            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
//    )
//    private AcademicSemester semester;
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
//    private double valueTPPM;
    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    @Field(defaultValue = "0")
    private double credits;
    
    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
    )
    @Field(defaultValue = "0")
    private double ects;

    @Summary
    @Properties(
            @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "#{concat('width:60px;','background-color:',"
                    + " hashToStringArr(this.courseGroup.name,'{bgcolor1}','{bgcolor2}','{bgcolor3}','{bgcolor4}','{bgcolor5}','{bgcolor6}','{bgcolor7}','{bgcolor8}','{bgcolor9}','{bgcolor10}')"
                    + ")"
                    + "}")
    )
    @ManyToOne(filter = "that.period.id=this.period.id and that.academicClass.id=this.courseLevel.academicClass.id")
    private AcademicCourseGroup courseGroup;
    
    private int groupCountC = 1;
    private int groupCountTD = 1;
    private int groupCountTP = 2;
    private int groupCountPM = 1;
//    private int groupCountTPPM = 1;

    private int evalMidExams;
    private boolean evalFinalExam;
    private int evalRandomExams;
    private boolean evalTpExam;
    private int evalOralExams;
    private String evalCertif;
    private int maxPopulationC;
    private int maxPopulationTD;
    private int maxPopulationTP;

    //    private double valueDU;
    //Unite enseignement/UE
//    @Summary
    private int position;
    private int weeksC = 0;
    private int weeksTD = 0;
    private int weeksTP = 0;
    private int weeksPM = 0;
    private int weeksTPPM = 0;
    //@Summary
    private String labels;

    /**
     * when true this course could no be assigned by non director
     */
    private boolean locked;

    private String roomConstraintsC;
    @Summary
    private String roomConstraintsTP;

    @Properties(
            {
//                @Property(name = UIConstants.Form.SEPARATOR, value = "Contenu")
//                ,
                    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
            }
    )
    @Field(max = "max")
    @Path("Details")
    private String overviewDetails;
    @Field(max = "512")
    private String prerequisites;

    @Properties(
            {
                @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
            }
    )
    @Field(max = "max")
    private String cDetails;

    @Properties(
            {
                @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
            }
    )
    @Field(max = "max")
    private String tpDetails;

    @Properties(
            {
                @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
            }
    )
    @Field(max = "max")
    private String prDetails;

    @Properties(
            {
                @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
            }
    )
    @Field(max = "max")
    private String referenceDetails;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    @Properties(
            {
                @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
            }
    )
    @Formula(name = "academicCoursePlan_validationErrors_Formula")
    @Field(max = "1024")
    @Summary
    private String validationErrors;

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

//    public double getValueTPPM() {
//        return valueTPPM;
//    }
//
//    public void setValueTPPM(double valueTPPM) {
//        this.valueTPPM = valueTPPM;
//    }

//    public int getGroupCountTPPM() {
//        return groupCountTPPM;
//    }
//
//    public void setGroupCountTPPM(int groupCountTPPM) {
//        this.groupCountTPPM = groupCountTPPM;
//    }

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

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public AcademicOfficialDiscipline getOfficialDiscipline() {
        return officialDiscipline;
    }

    public void setOfficialDiscipline(AcademicOfficialDiscipline officialDiscipline) {
        this.officialDiscipline = officialDiscipline;
    }

    public String getOverviewDetails() {
        return overviewDetails;
    }

    public void setOverviewDetails(String overviewDetails) {
        this.overviewDetails = overviewDetails;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getcDetails() {
        return cDetails;
    }

    public void setcDetails(String cDetails) {
        this.cDetails = cDetails;
    }

    public String getTpDetails() {
        return tpDetails;
    }

    public void setTpDetails(String tpDetails) {
        this.tpDetails = tpDetails;
    }

    public String getPrDetails() {
        return prDetails;
    }

    public void setPrDetails(String prDetails) {
        this.prDetails = prDetails;
    }

    public String getReferenceDetails() {
        return referenceDetails;
    }

    public void setReferenceDetails(String referenceDetails) {
        this.referenceDetails = referenceDetails;
    }

    public AcademicClass resolveAcademicClass() {
        return getCourseLevel()== null ? null : getCourseLevel().getAcademicClass();
    }

    public AcademicProgram resolveProgram() {
        return getCourseLevel()== null ? null : getCourseLevel().resolveProgram();
    }

    public AcademicProgramType resolveProgramType() {
        return getCourseLevel()== null ? null : getCourseLevel().resolveProgramType();
    }

    public AppDepartment resolveDepartment() {
        return getCourseLevel()== null ? null : getCourseLevel().resolveDepartment();
    }
    
    public AcademicSemester resolveSemester() {
        return getCourseLevel()== null ? null : getCourseLevel().getSemester();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getEvalMidExams() {
        return evalMidExams;
    }

    public void setEvalMidExams(int evalMidExams) {
        this.evalMidExams = evalMidExams;
    }

    public boolean isEvalFinalExam() {
        return evalFinalExam;
    }

    public void setEvalFinalExam(boolean evalFinalExam) {
        this.evalFinalExam = evalFinalExam;
    }

    public int getEvalRandomExams() {
        return evalRandomExams;
    }

    public void setEvalRandomExams(int evalRandomExams) {
        this.evalRandomExams = evalRandomExams;
    }

    public boolean isEvalTpExam() {
        return evalTpExam;
    }

    public void setEvalTpExam(boolean evalTpExam) {
        this.evalTpExam = evalTpExam;
    }

    public int getEvalOralExams() {
        return evalOralExams;
    }

    public void setEvalOralExams(int evalOralExams) {
        this.evalOralExams = evalOralExams;
    }

    public String getEvalCertif() {
        return evalCertif;
    }

    public void setEvalCertif(String evalCertif) {
        this.evalCertif = evalCertif;
    }

    public int getMaxPopulationC() {
        return maxPopulationC;
    }

    public void setMaxPopulationC(int maxPopulationC) {
        this.maxPopulationC = maxPopulationC;
    }

    public int getMaxPopulationTD() {
        return maxPopulationTD;
    }

    public void setMaxPopulationTD(int maxPopulationTD) {
        this.maxPopulationTD = maxPopulationTD;
    }

    public int getMaxPopulationTP() {
        return maxPopulationTP;
    }

    public void setMaxPopulationTP(int maxPopulationTP) {
        this.maxPopulationTP = maxPopulationTP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AcademicCoursePlan that = (AcademicCoursePlan) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public AcademicTeacher getResponsible() {
        return responsible;
    }

    public void setResponsible(AcademicTeacher responsible) {
        this.responsible = responsible;
    }

    public AcademicTeacher getResponsible2() {
        return responsible2;
    }

    public void setResponsible2(AcademicTeacher responsible2) {
        this.responsible2 = responsible2;
    }

    public String getResponsibleAvailability() {
        return responsibleAvailability;
    }

    public void setResponsibleAvailability(String responsibleAvailability) {
        this.responsibleAvailability = responsibleAvailability;
    }

    public String getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(String validationErrors) {
        this.validationErrors = validationErrors;
    }
    
}
