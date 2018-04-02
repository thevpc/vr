/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.stat;

import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.common.util.VMap;
import net.vpc.common.util.VMapValueFactory;

import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
public class GlobalStat {

    public static final VMapValueFactory<String, DisciplineStat> V_MAP_VALUE_FACTORY_LOAD_VALUE = new VMapValueFactory<String, DisciplineStat>() {
        @Override
        public DisciplineStat create(String key) {
            return new DisciplineStat(key);
        }
    };
    private double teachersCount;
    private SituationTypeStat teachersLeaveStat = new SituationTypeStat();
    private SituationTypeStat teachersPermanentStat = new SituationTypeStat();
    private SituationTypeStat teachersTemporaryStat = new SituationTypeStat();
    private SituationTypeStat teachersContractualStat = new SituationTypeStat();
    private SituationTypeStat teachersOtherStat = new SituationTypeStat();
    private SituationTypeStat unassignedStat = new SituationTypeStat();
    //    private int teachersLeaveCount;
//    private int teachersPermanentCount;
//    private int teachersTemporaryCount;
//    private int teachersContractualCount;
//    private int teachersOtherCount;

    private double nonPermanentLoad;
    private double nonPermanentLoadTeacherCount;
    private double overload;
    private double overloadTeacherCount;
    private double permanentOverload;
    private double referenceTeacherDueLoad;
    private double permanentOverloadTeacherCount;
    private double relativeOverload;
    private double relativeOverloadTeacherCount;
    private double unassignedLoadTeacherCount;

    private int courseAssignmentCount;
    private int coursePlanCount;
    private VMap<String, DisciplineStat> nonPermanentLoadValueByNonOfficialDiscipline = new VMap<String, DisciplineStat>(V_MAP_VALUE_FACTORY_LOAD_VALUE);
    private VMap<String, DisciplineStat> nonPermanentLoadValueByOfficialDiscipline = new VMap<String, DisciplineStat>(V_MAP_VALUE_FACTORY_LOAD_VALUE);
    private VMap<String, DisciplineStat> permanentLoadValueByNonOfficialDiscipline = new VMap<String, DisciplineStat>(V_MAP_VALUE_FACTORY_LOAD_VALUE);
    private VMap<String, DisciplineStat> permanentLoadValueByOfficialDiscipline = new VMap<String, DisciplineStat>(V_MAP_VALUE_FACTORY_LOAD_VALUE);
    /**
     * les charge manquante independament des vacataire et contractuel basee sur
     * la charge dun assistant
     */
    private GlobalAssignmentStat neededAbsolute = new GlobalAssignmentStat();
    /**
     * les charge manquante apres comptabilisation des vacataires et
     * contractuels basee sur la charge d'un assistant
     */
    private GlobalAssignmentStat neededRelative = new GlobalAssignmentStat();
    private GlobalAssignmentStat missing = new GlobalAssignmentStat();
    private List<GlobalAssignmentStat> details = new ArrayList<>();
    private Map<String, GlobalAssignmentStat> detailsMap = new HashMap<>();

    public double getNonPermanentLoad() {
        return nonPermanentLoad;
    }

    public void setNonPermanentLoad(double nonPermanentLoad) {
        this.nonPermanentLoad = nonPermanentLoad;
    }

    public double getNonPermanentLoadTeacherCount() {
        return nonPermanentLoadTeacherCount;
    }

    public void setNonPermanentLoadTeacherCount(double nonPermanentLoadTeacherCount) {
        this.nonPermanentLoadTeacherCount = nonPermanentLoadTeacherCount;
    }

    public List<GlobalAssignmentStat> getAssignments() {
        return details;
    }

    public List<GlobalAssignmentStat> getSituationDetails(AcademicSemester semester, AcademicTeacherDegree degree) {
        List<GlobalAssignmentStat> all = new ArrayList<>();
        for (GlobalAssignmentStat detail : details) {
            if (detail.getSituation() != null && Objects.equals(detail.getDegree(), degree) && Objects.equals(detail.getSemester(), semester)) {
                all.add(detail);
            }
        }
        return all;
    }

    public GlobalAssignmentStat getTotalAssignment() {
        return getAssignment(null, null, null);
    }

    public LoadValue getAssignmentSumValue(AcademicSemester semester, List<AcademicTeacherSituation> situations, AcademicTeacherDegree degree) {
        LoadValue v = new LoadValue();
        for (AcademicTeacherSituation situation : situations) {
            GlobalAssignmentStat a = getAssignment(semester, situation, degree);
            if (a != null) {
                v.add(a.getValue());
            }
        }
        return v;
    }

    public LoadValue getAssignmentSumDue(AcademicSemester semester, List<AcademicTeacherSituation> situations, AcademicTeacherDegree degree) {
        LoadValue v = new LoadValue();
        for (AcademicTeacherSituation situation : situations) {
            GlobalAssignmentStat a = getAssignment(semester, situation, degree);
            if (a != null) {
                v.add(a.getDue());
            }
        }
        return v;
    }

    public int getAssignmentTeacherCount(AcademicSemester semester, List<AcademicTeacherSituation> situations, AcademicTeacherDegree degree) {
        int count = 0;
        for (AcademicTeacherSituation situation : situations) {
            GlobalAssignmentStat a = getAssignment(semester, situation, degree);
            if (a != null) {
                count += a.getTeachersCount();
            }
        }
        return count;
    }

    public GlobalAssignmentStat getAssignment(AcademicSemester semester, AcademicTeacherSituation situation, AcademicTeacherDegree degree) {
        return getAssignment(semester, situation, degree, false);
    }

    public GlobalAssignmentStat getAssignment(AcademicSemester semester, AcademicTeacherSituation situation, AcademicTeacherDegree degree, boolean create) {
        String s = (situation == null ? "%" : situation.getId())
                + ";" + (degree == null ? "%" : degree.getId())
                + ";" + (semester == null ? "%" : semester.getId());
        GlobalAssignmentStat r = detailsMap.get(s);
        if (r == null && create) {
            r = new GlobalAssignmentStat();
            r.setDegree(degree);
            r.setSituation(situation);
            r.setSemester(semester);
            detailsMap.put(s, r);
            details.add(r);
        }
        return r;
    }

    public int getCourseAssignmentCount() {
        return courseAssignmentCount;
    }

    public void setCourseAssignmentCount(int courseAssignmentCount) {
        this.courseAssignmentCount = courseAssignmentCount;
    }

    public int getCoursePlanCount() {
        return coursePlanCount;
    }

    public void setCoursePlanCount(int coursePlanCount) {
        this.coursePlanCount = coursePlanCount;
    }

    public double getTeachersCount() {
        return teachersCount;
    }

    public void setTeachersCount(double teachersCount) {
        this.teachersCount = teachersCount;
    }

    public double getOverload() {
        return overload;
    }

    public void setOverload(double overload) {
        this.overload = overload;
    }

    public double getOverloadTeacherCount() {
        return overloadTeacherCount;
    }

    public void setOverloadTeacherCount(double overloadTeacherCount) {
        this.overloadTeacherCount = overloadTeacherCount;
    }

    public double getPermanentOverload() {
        return permanentOverload;
    }

    public void setPermanentOverload(double permanentOverload) {
        this.permanentOverload = permanentOverload;
    }

    public double getPermanentOverloadTeacherCount() {
        return permanentOverloadTeacherCount;
    }

    public void setPermanentOverloadTeacherCount(double permanentOverloadTeacherCount) {
        this.permanentOverloadTeacherCount = permanentOverloadTeacherCount;
    }

    public double getUnassignedLoadTeacherCount() {
        return unassignedLoadTeacherCount;
    }

    public void setUnassignedLoadTeacherCount(double unassignedLoadTeacherCount) {
        this.unassignedLoadTeacherCount = unassignedLoadTeacherCount;
    }

    public double getRelativeOverload() {
        return relativeOverload;
    }

    public void setRelativeOverload(double relativeOverload) {
        this.relativeOverload = relativeOverload;
    }

    public double getRelativeOverloadTeacherCount() {
        return relativeOverloadTeacherCount;
    }

    public void setRelativeOverloadTeacherCount(double relativeOverloadTeacherCount) {
        this.relativeOverloadTeacherCount = relativeOverloadTeacherCount;
    }

    /**
     * la charge nécessaire selon le du des enseignant permanents donc en gros
     * combien on a besoin d'assistants pour ne plus recruter des contractuels
     * et vacataires
     *
     * @return
     */
    public GlobalAssignmentStat getNeededAbsolute() {
        return neededAbsolute;
    }

    public void setNeededAbsolute(GlobalAssignmentStat neededAbsolute) {
        this.neededAbsolute = neededAbsolute;
    }

    public GlobalAssignmentStat getNeededRelative() {
        return neededRelative;
    }

    public void setNeededRelative(GlobalAssignmentStat neededRelative) {
        this.neededRelative = neededRelative;
    }

    public GlobalAssignmentStat getMissing() {
        return missing;
    }

    public SituationTypeStat getTeachersLeaveStat() {
        return teachersLeaveStat;
    }

    public SituationTypeStat getTeachersPermanentStat() {
        return teachersPermanentStat;
    }

    public SituationTypeStat getTeachersTemporaryStat() {
        return teachersTemporaryStat;
    }

    public SituationTypeStat getTeachersContractualStat() {
        return teachersContractualStat;
    }

    public SituationTypeStat getTeachersOtherStat() {
        return teachersOtherStat;
    }

    public SituationTypeStat getUnassignedStat() {
        return unassignedStat;
    }

    public double getReferenceTeacherDueLoad() {
        return referenceTeacherDueLoad;
    }

    public void setReferenceTeacherDueLoad(double referenceTeacherDueLoad) {
        this.referenceTeacherDueLoad = referenceTeacherDueLoad;
    }

    public VMap<String, DisciplineStat> getNonPermanentLoadValueByNonOfficialDiscipline() {
        return nonPermanentLoadValueByNonOfficialDiscipline;
    }

    public VMap<String, DisciplineStat> getNonPermanentLoadValueByOfficialDiscipline() {
        return nonPermanentLoadValueByOfficialDiscipline;
    }

    public VMap<String, DisciplineStat> getPermanentLoadValueByNonOfficialDiscipline() {
        return permanentLoadValueByNonOfficialDiscipline;
    }

    public VMap<String, DisciplineStat> getPermanentLoadValueByOfficialDiscipline() {
        return permanentLoadValueByOfficialDiscipline;
    }
}
