package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.history.*;
import net.vpc.upa.UPA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.vpc.app.vainruling.core.service.util.JsonUtils;
import net.vpc.common.util.MapUtils;

public class AcademicPluginBodyHistory extends AcademicPluginBody {

    private CorePlugin core;
    private TraceService trace;
    private AcademicPlugin academic;

    @Override
    public void onStart() {
        core = CorePlugin.get();
        trace = TraceService.get();
        academic = getContext().getPlugin();
    }

    public List<AcademicHistCourseAssignment> findHistCourseAssignments(int yearId, Integer teacher, Integer semester) {
        List<AcademicHistCourseAssignment> m = new ArrayList<>();
        for (AcademicHistCourseAssignment value : findHistCourseAssignments(yearId)) {
            if (teacher == null || (value.getTeacher() != null && value.getTeacher().getId() == (teacher))) {
                AcademicSemester semester1 = value.resolveSemester();
                if (semester == null || (semester1 != null && semester1.getId() == (semester))) {
                    m.add(value);
                }
            }
        }
        return m;
    }

    public List<AcademicHistProgram> findHistPrograms(int yearId) {
        return UPA.getPersistenceUnit()
                .createQuery(
                        "Select a from AcademicHistProgram a where a.academicYearId=:x")
                .setParameter("x", yearId)
                .getResultList();
    }

    public List<AcademicHistTeacherDegree> findHistTeacherDegrees(int yearId) {
        return UPA.getPersistenceUnit()
                .createQuery(
                        "Select a from AcademicHistTeacherDegree a where a.academicYearId=:x")
                .setParameter("x", yearId)
                .getResultList();
    }

    public AcademicHistTeacherDegree findHistTeacherDegree(int yearId, String t) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicHistTeacherDegree a where a.name=:t and a.academicYearId=:y")
                .setParameter("t", t)
                .setParameter("y", yearId)
                .getFirstResultOrNull();
    }

    public void resetHistAcademicYear(int year) {

        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_HISTORY_RESET);
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherSemestrialLoad where academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherAnnualLoad where academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherDegree where academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseAssignment a where a.coursePlan.academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCoursePlan a where a.academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseGroup a where a.courseLevel.program.academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistProgram a where a.academiYear=:y").setParameter("y", year).executeNonQuery();
        trace.trace("Academic.history-reset-year", "success", MapUtils.map("year", year), JsonUtils.jsonMap("year", year), "/Education", Level.FINE);
    }

    public void resetHistAcademicYears() {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_HISTORY_RESET);
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherSemestrialLoad").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherAnnualLoad").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherDegree").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseAssignment").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCoursePlan").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseGroup").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistProgram").executeNonQuery();
        trace.trace("Academic.history-reset-years", "success", null, "{}", "/Education", Level.FINE);
    }

    public List<AcademicHistTeacherAnnualLoad> findHistTeacherAnnualLoads(int year) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_HISTORY_READ);
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistTeacherAnnualLoad a where a.academicYearId=:v")
                .setParameter("v", year)
                .getResultList();
    }

    public AcademicHistTeacherAnnualLoad findHistTeacherAnnualLoad(int year, int teacherId) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_HISTORY_READ);
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistTeacherAnnualLoad a where a.academicYearId=:v and a.teacherId=:t")
                .setParameter("v", year)
                .setParameter("t", teacherId)
                .getFirstResultOrNull();
    }

    public List<AcademicHistTeacherSemestrialLoad> findHistTeacherSemestrialLoads(int year, int teacherId) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_HISTORY_READ);
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicTeacherSemestrialLoad a where a.annualLoad.academicYearId=:v and a.annualLoad.teacherId=:t")
                .setParameter("v", year)
                .setParameter("t", teacherId)
                .getResultList();
    }

    public List<AcademicHistTeacherSemestrialLoad> findHistTeacherSemestrialLoads(int year) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_HISTORY_READ);
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistTeacherSemestrialLoad a where a.annualLoad.academicYearId=:v")
                .setParameter("v", year)
                .getResultList();
    }

    public List<AcademicHistCourseAssignment> findHistCourseAssignments(int yearId) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_HISTORY_READ);
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistCourseAssignment a where a.coursePlan.academicYearId=:v")
                .setParameter("v", yearId).getResultList();
    }

    public List<AcademicHistCoursePlan> findHistCoursePlans(int year) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_HISTORY_READ);
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistCoursePlan a where a.academicYearId=:v")
                .setParameter("v", year).getResultList();
    }

    public AppPeriod addAcademicYearSnapshot(String year, String snapshotName) {
        AppPeriod s = academic.createAcademicYear(year, snapshotName);
//        AppPeriod s = new AppPeriod();
//        s.setCreationTime(new DateTime());
//        s.setName(y.getName());
//        s.setSnapshotName(snapshotName);
//        UPA.getPersistenceUnit().persist(s);
        int periodId = s.getId();
        Map<String, AcademicHistTeacherDegree> histTeacherDegreeMap = new HashMap<>();
        for (AcademicTeacherDegree m : academic.findTeacherDegrees()) {
            AcademicHistTeacherDegree h = new AcademicHistTeacherDegree();
            h.setCode(m.getCode());
            h.setName(m.getName());
            h.setName2(m.getName2());
            h.setPosition(m.getPosition());
            h.setConversionRule(m.getConversionRule());
            h.setAcademicYear(s);
            histTeacherDegreeMap.put(h.getCode(), h);
            CorePlugin.get().save(null, h);
        }
        Map<Integer, AcademicHistTeacherAnnualLoad> teacherToLoad = new HashMap<>();
        for (AcademicTeacher m : academic.findTeachers()) {
            AcademicHistTeacherAnnualLoad h = new AcademicHistTeacherAnnualLoad();
            h.setAcademicYear(s);
            AcademicTeacherPeriod ts = academic.findAcademicTeacherPeriod(periodId, m);
            h.setDegree(histTeacherDegreeMap.get(ts.getDegree() == null ? null : ts.getDegree().getName()));
            h.setSituation(ts.getSituation());
            h.setTeacher(m);
            CorePlugin.get().save(null, h);
            teacherToLoad.put(m.getId(), h);
        }
        for (AcademicTeacherSemestrialLoad m : academic.findTeacherSemestrialLoadsByPeriod(periodId)) {
            AcademicHistTeacherSemestrialLoad h = new AcademicHistTeacherSemestrialLoad();
            h.setAcademicYear(s);
            h.setAnnualLoad(teacherToLoad.get(m.getTeacher().getId()));
            h.setSemester(m.getSemester());
            h.setWeeksLoad(m.getWeeksLoad());
            CorePlugin.get().save(null, h);
        }

        Map<Integer, AcademicHistProgram> academicHistCoursePrograms = new HashMap<>();
        for (AcademicProgram m : academic.findPrograms()) {
            AcademicHistProgram h = new AcademicHistProgram();
            h.setAcademicYear(s);
            h.setName(m.getName());
            h.setDepartment(m.getDepartment());
            h.setName(m.getName());
            h.setName2(m.getName2());
            CorePlugin.get().save(null, h);
            academicHistCoursePrograms.put(m.getId(), h);
        }

        Map<Integer, AcademicHistCourseGroup> academicHistCourseGroups = new HashMap<>();
        for (AcademicCourseGroup m : academic.findCourseGroups(periodId)) {
            AcademicHistCourseGroup h = new AcademicHistCourseGroup();
            h.setAcademicYear(s);
            h.setAcademicClass(m.getAcademicClass());
            CorePlugin.get().save(null, h);
            academicHistCourseGroups.put(m.getId(), h);
        }

        for (AcademicCoursePlan m : academic.findCoursePlans(periodId)) {
            AcademicHistCoursePlan h = new AcademicHistCoursePlan();

            h.setAcademicYear(s);
            h.setProgram(m.getCourseLevel().getAcademicClass().getProgram() == null ? null : academicHistCoursePrograms.get(m.getCourseLevel().getAcademicClass().getProgram().getId()));
            h.setCourseGroup(m.getCourseGroup() == null ? null : academicHistCourseGroups.get(m.getCourseGroup().getId()));
            h.setCourseLevel(m.getCourseLevel());
            h.setDiscipline(m.getDiscipline());
            h.setName(m.getName());
            h.setName2(m.getName2());
            h.setPosition(m.getPosition());

            h.setGroupCountC(m.getGroupCountC());
            h.setGroupCountPM(m.getGroupCountPM());
            h.setGroupCountTD(m.getGroupCountTD());
            h.setGroupCountTP(m.getGroupCountTP());
//            h.setGroupCountTPPM(m.getGroupCountTPPM());

            h.setWeeksC(m.getWeeksC());
            h.setWeeksPM(m.getWeeksPM());
            h.setWeeksTD(m.getWeeksTD());
            h.setWeeksTP(m.getWeeksTP());
            h.setWeeksTPPM(m.getWeeksTPPM());

            h.setValueC(m.getValueC());
            h.setValuePM(m.getValuePM());
            h.setValueTD(m.getValueTD());
            h.setValueTP(m.getValueTP());
//            h.setValueTPPM(m.getValueTPPM());

            h.setStudentClass(m.getCourseLevel().getAcademicClass());
            h.setSemester(m.getCourseLevel().getSemester());
            CorePlugin.get().save(null, h);
        }
        return s;
    }

    /**
     *
     */
    public void resetModuleTeaching() {
        CorePluginSecurity.requireRight("Admin.ResetEducation");
        resetCurrentYear();
        resetHistAcademicYears();
    }

    public void resetTeachers() {
        CorePluginSecurity.requireRight("Admin.ResetEducation");
        resetAssignments();
        //IGNORE ME
//        UPA.getPersistenceUnit().createQuery("delete from AcademicTeacherSemestrialLoad").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicTeacher").executeNonQuery();
    }

    public void resetAssignments() {
        CorePluginSecurity.requireRight("Admin.ResetEducation");
        //IGNORE ME
        //UPA.getPersistenceUnit().createQuery("delete from AcademicCourseIntent").executeNonQuery();
        //UPA.getPersistenceUnit().createQuery("delete from AcademicCourseAssignment").executeNonQuery();
    }

    public void resetCourses() {
        CorePluginSecurity.requireRight("Admin.ResetEducation");
        //IGNORE ME
        //UPA.getPersistenceUnit().createQuery("delete from AcademicCoursePlan").executeNonQuery();
        //UPA.getPersistenceUnit().createQuery("delete from AcademicCourseGroup").executeNonQuery();
    }

    public void resetCurrentYear() {
        CorePluginSecurity.requireRight("Admin.ResetEducation");
        resetAssignments();
        resetCourses();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicCourseType").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicClass").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicCourseLevel").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicProgram").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicSemester").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicTeacherSituation").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicTeacherDegree").executeNonQuery();
//        trace.trace("Academic-hsitory-reset-current-year", "success","reset Module Academic", null, "/Education", Level.FINE);
    }

}
