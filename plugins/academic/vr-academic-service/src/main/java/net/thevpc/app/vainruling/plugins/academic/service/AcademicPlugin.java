/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service;

import net.thevpc.app.vainruling.VrInstall;
import net.thevpc.app.vainruling.VrPlugin;
import net.thevpc.app.vainruling.VrStart;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.CorePluginSecurity;
import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.app.vainruling.core.service.model.AppDepartmentPeriod;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.plugins.academic.service.stat.*;
import net.thevpc.app.vainruling.plugins.academic.service.util.DefaultCourseAssignmentFilter;
import net.thevpc.app.vainruling.plugins.academic.service.util.TeacherPeriodFilter;
import net.thevpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternshipBoard;
import net.thevpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipBoardMessage;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipDuration;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseLevel;
import net.thevpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternship;
import net.thevpc.app.vainruling.plugins.academic.model.history.AcademicHistTeacherSemestrialLoad;
import net.thevpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipBoardTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherSituationType;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicBac;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicPreClass;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudentStage;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionTable;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseType;
import net.thevpc.app.vainruling.plugins.academic.model.history.AcademicHistCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicDiscipline;
import net.thevpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternshipSessionType;
import net.thevpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternshipGroup;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseIntent;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicPreClassType;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherDegree;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicProgramType;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherSituation;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicFormerStudent;
import net.thevpc.app.vainruling.plugins.academic.model.history.AcademicHistCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.model.history.AcademicHistTeacherAnnualLoad;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherPeriod;
import net.thevpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipVariant;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicProgram;
import net.thevpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternshipSupervisorIntent;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionRule;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicOfficialDiscipline;
import net.thevpc.app.vainruling.plugins.academic.model.current.TeacherAssignmentChunck;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicSemester;
import net.thevpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipType;
import net.thevpc.app.vainruling.plugins.academic.model.internship.config.AcademicInternshipStatus;
import net.thevpc.app.vainruling.plugins.academic.model.history.AcademicHistTeacherDegree;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicTeacherSemestrialLoad;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfo;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseGroup;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacherStrict;
import net.thevpc.app.vainruling.plugins.academic.model.history.AcademicHistProgram;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicLoadConversionRow;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignmentInfoByVisitor;
import net.thevpc.app.vainruling.core.service.TraceService;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.cache.CacheService;
import net.thevpc.app.vainruling.core.service.model.*;
import net.thevpc.app.vainruling.core.service.stats.KPI;
import net.thevpc.app.vainruling.core.service.stats.KPIGroupBy;
import net.thevpc.app.vainruling.core.service.stats.KPIProcessProcessor;
import net.thevpc.app.vainruling.core.service.stats.KPIResult;
import net.thevpc.app.vainruling.core.service.util.NamedValueCount;
import net.thevpc.app.vainruling.plugins.academic.service.integration.AcademicConversionTableHelper;
import net.thevpc.app.vainruling.plugins.academic.service.integration.CopyAcademicDataHelper;
import net.thevpc.app.vainruling.plugins.academic.model.imp.AcademicStudentImport;
import net.thevpc.app.vainruling.plugins.academic.model.imp.AcademicTeacherImport;
import net.thevpc.app.vainruling.plugins.academic.model.internship.ext.AcademicInternshipExtList;
import net.thevpc.app.vainruling.plugins.academic.service.stat.*;
import net.thevpc.app.vainruling.plugins.academic.service.util.CourseAssignmentFilter;
import net.thevpc.app.vainruling.plugins.academic.service.integration.ImportOptions;
import net.thevpc.common.strings.StringComparator;
import net.thevpc.common.util.*;
import net.thevpc.common.vfs.VFile;
import net.thevpc.upa.Document;
import net.thevpc.upa.NamedId;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import net.thevpc.app.vainruling.core.service.ProfileRightBuilder;

import net.thevpc.app.vainruling.core.service.util.DefaultObjectToMapConverter;
import net.thevpc.app.vainruling.core.service.util.ObjectToMapConverter;
import net.thevpc.app.vainruling.core.service.util.TextSearchFilter;
import net.thevpc.app.vainruling.plugins.academic.service.dto.TeacherLoadInfoFilter;
import net.thevpc.app.vainruling.plugins.academic.service.dto.TeacherLoadInfo;
import net.thevpc.app.vainruling.plugins.academic.service.dto.TeacherPeriodStatExt;
import net.thevpc.common.mon.ProgressMonitor;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseCore;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin()
public class AcademicPlugin {

    public static final Function<AcademicTeacher, Integer> AcademicTeacherIdConverter = AcademicTeacher::getId;

    private static final Logger log = Logger.getLogger(AcademicPlugin.class.getName());
    private AcademicPluginBodyContext bodyContext = new AcademicPluginBodyContext(this);
    @Autowired
    TraceService trace;
    @Autowired
    CorePlugin core;
    @Autowired
    CacheService cacheService;
    private CopyAcademicDataHelper copyAcademicDataHelper = new CopyAcademicDataHelper();

    private AcademicPluginBodyAssignments assignments = new AcademicPluginBodyAssignments();
    private AcademicPluginBodyConfig config = new AcademicPluginBodyConfig();
    private AcademicPluginBodyHistory historys = new AcademicPluginBodyHistory();
    private AcademicPluginBodyImports imports = new AcademicPluginBodyImports();
    private AcademicPluginBodyInternships internships = new AcademicPluginBodyInternships();
    private AcademicPluginBodyLoad loads = new AcademicPluginBodyLoad();
    private AcademicPluginBodyStudents students = new AcademicPluginBodyStudents();
    private AcademicPluginBodyTeachers teachers = new AcademicPluginBodyTeachers();
    private AcademicPluginBody[] bodies = new AcademicPluginBody[]{config, assignments, students, teachers, imports, internships, loads, historys};

    public AcademicPlugin() {
        System.out.println("");
    }

    public static AcademicPlugin get() {
        return VrApp.getBean(AcademicPlugin.class);
    }

    @VrInstall
    private void onInstall() {
        //this is workaround, because
        if (core == null) {
            core = CorePlugin.get();
        }
        ProfileRightBuilder b = new ProfileRightBuilder();
        for (String r : AcademicPluginSecurity.RIGHTS_ACADEMIC) {
            b.addName(r);
        }
        b.execute();

        for (AcademicPluginBody body : bodies) {
            body.setContext(bodyContext);
            body.install();
        }
    }

    @VrStart
    private void onStart() {
        //this is workaround, because
        if (core == null) {
            core = CorePlugin.get();
        }
        for (AcademicPluginBody body : bodies) {
            body.setContext(bodyContext);
            body.start();
        }
    }

    public int getSemesterMaxWeeks() {
        return config.getSemesterMaxWeeks();
    }

    public void generateTeacherAssignmentDocumentsFolder(int periodId) {
        assignments.generateTeacherAssignmentDocumentsFolder(periodId);
    }

    public void generateTeacherAssignmentDocumentsFolder(int periodId, String path) {
        assignments.generateTeacherAssignmentDocumentsFolder(periodId, path);
    }

    public ModuleStat evalModuleStat(int periodId, int courseAssignmentId, Integer forTeacherId) {
        return loads.evalModuleStat(periodId, courseAssignmentId, forTeacherId);
    }

    public AcademicCourseAssignment findCourseAssignment(int courseAssignmentId) {
        return assignments.findCourseAssignment(courseAssignmentId);
    }

    public AcademicCourseAssignment findCourseAssignment(int coursePlanId, Integer subClassId, String discriminator) {
        return assignments.findCourseAssignment(coursePlanId, subClassId, discriminator);
    }

    public List<AcademicCourseAssignment> findCourseAssignmentsByCoursePlan(int planId) {
        return assignments.findCourseAssignmentsByCoursePlan(planId);
    }

    public List<AcademicCourseAssignment> findCourseAssignmentsByClass(int periodId, int classId) {
        return assignments.findCourseAssignmentsByClass(periodId, classId);
    }

    public List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoadsByTeacher(int periodId, int teacherId) {
        return assignments.findTeacherSemestrialLoadsByTeacher(periodId, teacherId);
    }

    public List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoadsByPeriod(int periodId) {
        return assignments.findTeacherSemestrialLoadsByPeriod(periodId);
    }

    public KeyValueList<Integer, AcademicCourseAssignment> findCourseAssignments(int periodId, CourseAssignmentFilter filter) {
        return assignments.findCourseAssignments(periodId, filter);
    }

    public KeyValueList<Integer, AcademicCourseAssignment> findCourseAssignments(int periodId) {
        return assignments.findCourseAssignments(periodId);
    }

    public List<AcademicCourseIntent> findCourseIntents(int periodId) {
        return assignments.findCourseIntents(periodId);
    }

    public List<AcademicCourseIntent> findCourseIntentsByCoursePlan(int coursePlanId) {
        return assignments.findCourseIntentsByCoursePlan(coursePlanId);
    }

    public void dupCourseAssignment(int assignmentId) {
        assignments.dupCourseAssignment(assignmentId);
    }

    public void splitGroupCourseAssignment(int assignmentId) {
        assignments.splitGroupCourseAssignment(assignmentId);
    }

    public void addCourseAssignment(AcademicCourseAssignment assignment) {
        assignments.addCourseAssignment(assignment);
    }

    public void updateCourseAssignment(AcademicCourseAssignment assignment) {
        assignments.updateCourseAssignment(assignment);
    }

    public void addCourseAssignment(int teacherId, int assignementId) {
        assignments.addCourseAssignment(teacherId, assignementId);
    }

    public void removeCourseAssignment(int assignementId, boolean hardRemoval, boolean switchToIntent) {
        assignments.removeCourseAssignment(assignementId, hardRemoval, switchToIntent);
    }

    public void addIntent(int teacherId, int assignementId) {
        assignments.addIntent(teacherId, assignementId);
    }

    public void addProposal(int teacherId, int assignementId) {
        assignments.addProposal(teacherId, assignementId);
    }

    public void removeIntent(int teacherId, int assignementId) {
        assignments.removeIntent(teacherId, assignementId);
    }

    public void removeProposal(int teacherId, int assignementId) {
        assignments.removeProposal(teacherId, assignementId);
    }

    public void addWish(int teacherId, int assignementId) {
        assignments.addWish(teacherId, assignementId);
    }

    public void removeWish(int teacherId, int assignementId) {
        assignments.removeWish(teacherId, assignementId);
    }

    public void removeAllIntents(int assignementId) {
        assignments.removeAllIntents(assignementId);
    }

    public void removeAllProposal(int assignementId) {
        assignments.removeAllProposals(assignementId);
    }

    public void removeAllWishes(int assignementId) {
        assignments.removeAllWishes(assignementId);
    }

    public AcademicCourseAssignment findAcademicCourseAssignment(int assignmentId) {
        return assignments.findAcademicCourseAssignment(assignmentId);
    }

    public List<AcademicCourseAssignment> findAcademicCourseAssignments(int periodId) {
        return assignments.findAcademicCourseAssignments(periodId);
    }

    public List<AcademicCourseIntent> findCourseIntentsByAssignment(int periodId, int assignment, Integer semester, CourseAssignmentFilter filter) {
        return assignments.findCourseIntentsByAssignment(periodId, assignment, semester, filter);
    }

    public List<AcademicCourseIntent> findCourseIntentsByAssignment(int assignment) {
        return assignments.findCourseIntentsByAssignment(assignment);
    }

    public List<AcademicCourseIntent> findCourseIntentsByTeacher(int periodId, Integer teacher, CourseAssignmentFilter filter) {
        return assignments.findCourseIntentsByTeacher(periodId, teacher, filter);
    }

    public List<AcademicCourseAssignment> findCourseAssignments(int periodId, Integer teacher, CourseAssignmentFilter filter) {
        return assignments.findCourseAssignments(periodId, teacher, filter);
    }

    public List<AcademicCourseAssignmentInfo> findCourseAssignmentsAndIntents(int periodId, Integer teacher, CourseAssignmentFilter filter) {
        return assignments.findCourseAssignmentsAndIntents(periodId, teacher, filter);
    }

    public List<TeacherSemesterStat> evalTeacherSemesterStatList(int periodId, Integer semesterId, TeacherPeriodFilter teacherFilter, CourseAssignmentFilter filter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        return loads.evalTeacherSemesterStatList(periodId, semesterId, teacherFilter, filter, deviationConfig, mon);
    }

    public KeyValueList<Integer, TeacherPeriodStat> evalTeacherStatList(final int periodId, TeacherPeriodFilter teacherFilter, CourseAssignmentFilter courseAssignmentFilter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        return loads.evalTeacherStatList(periodId, teacherFilter, courseAssignmentFilter, deviationConfig, mon);
    }

    public GlobalStat evalGlobalStat(int periodId, TeacherPeriodFilter teacherFilter, CourseAssignmentFilter filter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        return loads.evalGlobalStat(periodId, teacherFilter, filter, deviationConfig, mon);
    }

    public AcademicTeacher findTeacherByUser(int userId) {
        return teachers.findTeacherByUser(userId);
    }

    public AcademicTeacher findTeacher(StringComparator t) {
        return teachers.findTeacher(t);
    }

    public List<AcademicTeacher> findTeachers(int period, TeacherPeriodFilter teacherFilter) {
        return teachers.findTeachers(period, teacherFilter);
    }

    public List<AcademicTeacherStrict> findActiveTeachersStrict() {
        return teachers.findActiveTeachersStrict();
    }

    public List<AcademicTeacher> findTeachers() {
        return teachers.findTeachers();
    }

    public List<AcademicTeacher> findEnabledTeachers(int periodId) {
        return teachers.findEnabledTeachers(periodId);
    }

    public List<NamedId> findEnabledTeacherNames(int periodId) {
        return teachers.findEnabledTeacherNames(periodId);
    }

    public AcademicTeacher findTeacher(int t) {
        return teachers.findTeacher(t);
    }

    public List<AcademicTeacher> findTeachers(String teacherProfileFilter) {
        return teachers.findTeachers(teacherProfileFilter);
    }

    public List<AcademicTeacher> filterTeachers(List<AcademicTeacher> objects, String studentProfileFilter) {
        return teachers.filterTeachers(objects, studentProfileFilter);
    }

    //    public List<AcademicTeacher> findTeachersWithAssignmentsOrIntent(int periodId) {
//        return teachers.findTeachersWithAssignmentsOrIntent(periodId);
//    }
//
//    public List<AcademicTeacher> findTeachersWithAssignments(int periodId) {
//        return teachers.findTeachersWithAssignments(periodId);
//    }
//
//    public List<AcademicTeacher> findTeachersWithIntents(int periodId) {
//        return teachers.findTeachersWithIntents(periodId);
//    }
    public AcademicTeacher findTeacher(String t) {
        return teachers.findTeacher(t);
    }

    public List<AcademicCoursePlan> findCoursePlansWithAssignmentsOrIntents(int periodId, int semesterId, int programId, boolean includeAssignments, boolean includeIntents, int assignmentDepId) {
        return teachers.findCoursePlansWithAssignmentsOrIntents(periodId, semesterId, programId, includeAssignments, includeIntents, assignmentDepId);
    }

    public List<AcademicTeacher> findTeachersWithAssignmentsOrIntents(int periodId, int semesterId, boolean includeAssignments, boolean includeIntents, int teacherDepId, int assignmentDepId) {
        return teachers.findTeachersWithAssignmentsOrIntents(periodId, semesterId, includeAssignments, includeIntents, teacherDepId, assignmentDepId);
    }

    public AcademicStudent findStudentByUser(Integer userId) {
        return students.findStudentByUser(userId);
    }

    //    public AcademicStudent findStudentByContact(int contactId) {
//        return students.findStudentByContact(contactId);
//    }
    public List<AcademicStudent> findStudents(Integer department, AcademicStudentStage stage) {
        return students.findStudents(department, stage);
    }

    public List<AcademicStudent> findStudents() {
        return students.findStudents();
    }

    /**
     * @param studentUpqlFilter ql expression x based. example "x.fullName like
     * '%R%'"
     * @return
     */
    public List<AcademicStudent> findStudents(String studentProfileFilter, AcademicStudentStage stage, String studentUpqlFilter) {
        return students.findStudents(studentProfileFilter, stage, studentUpqlFilter);
    }

    public List<AcademicStudent> filterStudents(List<AcademicStudent> base, String studentProfileFilter) {
        return students.filterStudents(base, studentProfileFilter);
    }

    public List<AcademicFormerStudent> findGraduatedStudents() {
        return students.findGraduatedStudents();
    }

    public AcademicStudent findStudentByFullName(String name) {
        return students.findStudentByFullName(name);
    }

    public AcademicFormerStudent findFormerStudent(int studentId) {
        return students.findFormerStudent(studentId);
    }

    public AcademicFormerStudent moveToFormerStudent(int studentId, boolean lenient) {
        return students.moveToFormerStudent(studentId, lenient);
    }

    public List<AcademicProgramType> findProgramTypes() {
        return config.findProgramTypes();
    }

    public Set<String> findCoursePlanLabels(int periodId) {
        return config.findCoursePlanLabels(periodId);
    }

    public AcademicTeacher findCurrentHeadOfDepartment() {
        return config.findCurrentHeadOfDepartment();
    }

    public AcademicTeacher findHeadOfDepartment(int depId) {
        return config.findHeadOfDepartment(depId);
    }

    ////////////////////////////////////////////////////////////////////////////
    public AcademicTeacher getCurrentTeacher() {
        AppUser user = core.getCurrentUser();
        if (user != null) {
            return findTeacherByUser(user.getId());
        }
        return null;
    }

    public AcademicStudent getCurrentStudent() {
        AppUser user = core.getCurrentUser();
        if (user != null) {
            return findStudentByUser(user.getId());
        }
        return null;
    }

    public void copyAcademicData(int fromPeriodId, int toPeriodId) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_WRITE);
        copyAcademicDataHelper.copyAcademicData(fromPeriodId, toPeriodId);
    }

    public void importStudent(int periodId, AcademicStudentImport s) throws IOException {
        imports.importStudent(periodId, s);
    }

    public void importTeacher(int periodId, AcademicTeacherImport t) throws IOException {
        imports.importTeacher(periodId, t);
    }

    public int importFile(int periodId, VFile folder, ImportOptions importOptions) throws IOException {
        return imports.importFile(periodId, folder, importOptions);
    }

    //    public void add(Object t) {
//        if (t instanceof AppPeriod) {
//            AppPeriod a = (AppPeriod) t;
//            a.setCreationTime(new DateTime());
//            a.setSnapshotName(null);
//        }
//        UPA.getPersistenceUnit().persist(t);
//    }
    public void generateTeachingLoad(int periodId, CourseAssignmentFilter courseAssignmentFilter, String version0, String oldVersion, ProgressMonitor monitor) throws IOException {
        assignments.generateTeachingLoad(periodId, courseAssignmentFilter, version0, oldVersion, monitor);
    }

    public AcademicConversionTableHelper findConversionTableById(int id) {
        return assignments.findConversionTableById(id);
    }

    public Map<Integer, List<AcademicCourseAssignment>> findAcademicCourseAssignmentListGroupByTeacherId(int periodId) {
        return assignments.findAcademicCourseAssignmentListGroupByByTeacherId(periodId);
    }

    public List<AcademicCourseAssignment> findAssignments(Integer periodId, Integer coursePlanId, Integer clazzId, Integer teacherId, Integer programId, Integer semesterId, Integer courseTypeId) {
        return assignments.findAssignments(periodId, coursePlanId, clazzId, teacherId, programId, semesterId, courseTypeId);
    }

    public List<AcademicCoursePlan> findCoursePlans(Integer periodId, Integer programId, Integer clazzId, Integer semesterId) {
        return assignments.findCoursePlans(periodId, programId, clazzId, semesterId);
    }

    public Map<Integer, List<AcademicCourseIntent>> getAcademicCourseIntentByAssignmentId(int periodId) {
        return assignments.getAcademicCourseIntentByAssignmentId(periodId);
    }

    public List<AcademicCourseIntent> getAcademicCourseIntentByAssignmentAndSemester(int periodId, Integer assignmentId, Integer semester) {
        return assignments.getAcademicCourseIntentByAssignmentAndSemester(periodId, assignmentId, semester);
    }

    public Map<Integer, List<AcademicCourseIntent>> getAcademicCourseIntentByTeacherId(int periodId) {
        return assignments.getAcademicCourseIntentByTeacherId(periodId);
    }

    public AcademicConversionTableHelper findConversionTableByPeriodId(int periodId) {
        return assignments.findConversionTableByPeriodId(periodId);
    }

    public List<KPI> resolveAssignmentKPIs() {
        return VrApp.getBeansForType(KPI.class);
    }

    public KPIResult evalAssignmentKPIs(List<AcademicCourseAssignmentInfo> assignments, KPIGroupBy<AcademicCourseAssignmentInfo>[] groupBy, KPI<AcademicCourseAssignmentInfo>... kpis) {
        return KPIProcessProcessor.INSTANCE.run(assignments, groupBy, kpis);
    }

    public KPIResult evalAssignmentKPIs(List<AcademicCourseAssignmentInfo> assignments, KPIGroupBy<AcademicCourseAssignmentInfo> groupBy, KPI<AcademicCourseAssignmentInfo>... kpis) {
        return KPIProcessProcessor.INSTANCE.run(assignments, new KPIGroupBy[]{groupBy}, kpis);
    }

    public Map<String, Number> statEvalInternshipAssignmentCount(List<AcademicInternship> internships) {
        return this.internships.statEvalInternshipAssignmentCount(internships);
    }

    public Map<String, Number> statEvalInternshipJuryExaminerCount(List<AcademicInternship> internships) {
        return this.internships.statEvalInternshipJuryExaminerCount(internships);
    }

    public Map<String, Number> statEvalInternshipJuryChairCount(List<AcademicInternship> internships) {
        return this.internships.statEvalInternshipJuryChairCount(internships);
    }

    public List<NamedValueCount> statEvalInternshipRegion(List<AcademicInternship> internships) {
        return this.internships.statEvalInternshipRegion(internships);
    }

    public List<NamedValueCount> statEvalInternshipGovernorate(List<AcademicInternship> internships) {
        return this.internships.statEvalInternshipGovernorate(internships);
    }

    public List<NamedValueCount> statEvalInternshipDiscipline(List<AcademicInternship> internships) {
        return this.internships.statEvalInternshipDiscipline(internships);
    }

    public List<NamedValueCount> statEvalInternshipTechnologies(List<AcademicInternship> internships) {
        return this.internships.statEvalInternshipTechnologies(internships);
    }

    public List<NamedValueCount> statEvalInternshipVariant(List<AcademicInternship> internships) {
        return this.internships.statEvalInternshipVariant(internships);
    }

    public List<NamedValueCount> statEvalInternshipPeriod(List<AcademicInternship> internships) {
        return this.internships.statEvalInternshipPeriod(internships);
    }

    public Map<String, Number> statEvalInternshipStatus(List<AcademicInternship> internships) {
        return this.internships.statEvalInternshipStatus(internships);
    }

    public int generateInternships(AcademicInternship internship, String studentProfiles) {
        return internships.generateInternships(internship, studentProfiles);
    }

    public void addSupervisorIntent(int internship, int teacherId) {
        internships.addSupervisorIntent(internship, teacherId);
    }

    public void removeSupervisorIntent(int internship, int teacherId) {
        internships.removeSupervisorIntent(internship, teacherId);
    }

    public List<AcademicTeacher> findInternshipSupervisorIntents(int internship) {
        return internships.findInternshipSupervisorIntents(internship);
    }

    public void generateInternships(int internshipId, String studentProfiles) {
        internships.generateInternships(internshipId, studentProfiles);
    }

    public AcademicInternship findInternship(int id) {
        return internships.findInternship(id);
    }

    public AcademicInternshipStatus findInternshipStatus(int id) {
        return internships.findInternshipStatus(id);
    }

    public AcademicInternshipVariant findInternshipVariant(int id) {
        return internships.findInternshipVariant(id);
    }

    public AcademicInternshipDuration findInternshipDuration(int id) {
        return internships.findInternshipDuration(id);
    }

    public AcademicInternshipBoard findInternshipBoard(int id) {
        return internships.findInternshipBoard(id);
    }

    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByDepartment(int periodId, int departmentId, Boolean enabled) {
        return internships.findEnabledInternshipBoardsByDepartment(periodId, departmentId, enabled);
    }

    public List<AcademicInternshipGroup> findEnabledInternshipGroupsByDepartment(int departmentId) {
        return internships.findEnabledInternshipGroupsByDepartment(departmentId);
    }

    public List<AcademicInternshipSessionType> findAcademicInternshipSessionType() {
        return internships.findAcademicInternshipSessionType();
    }

    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByTeacher(int teacherId) {
        return internships.findEnabledInternshipBoardsByTeacher(teacherId);
    }

    public List<AcademicInternshipVariant> findInternshipVariantsByType(int typeId) {
        return internships.findInternshipVariantsByType(typeId);
    }

    public double findInternshipTeacherInternshipsCount(int teacherId, int yearId, int internshipTypeId) {
        return internships.findInternshipTeacherInternshipsCount(teacherId, yearId, internshipTypeId);
    }

    public Map<Integer, Number> findInternshipTeachersInternshipsCounts(int yearId, int internshipTypeId) {
        return internships.findInternshipTeachersInternshipsCounts(yearId, internshipTypeId);
    }

    public List<AcademicInternshipBoardTeacher> findInternshipTeachersByBoard(int boardId) {
        return internships.findInternshipTeachersByBoard(boardId);
    }

    public void addBoardMessage(AcademicInternshipBoardMessage m) {
        internships.addBoardMessage(m);
    }

    public void removeBoardMessage(int messageId) {
        internships.removeBoardMessage(messageId);
    }

    public List<AcademicInternshipBoardMessage> findInternshipMessagesByInternship(int internshipId) {
        return internships.findInternshipMessagesByInternship(internshipId);
    }

    public List<AcademicInternshipType> findInternshipTypes() {
        return internships.findInternshipTypes();
    }

    public List<AcademicInternshipStatus> findInternshipStatusesByType(int typeId) {
        return internships.findInternshipStatusesByType(typeId);
    }

    public List<AcademicInternshipDuration> findInternshipDurationsByType(int typeId) {
        return internships.findInternshipDurationsByType(typeId);
    }

    public AcademicInternship findStudentPFE(int studentId) {
        return internships.findStudentPFE(studentId);
    }

    public List<AcademicInternship> findActualInternshipsByStudent(int studentId) {
        return internships.findActualInternshipsByStudent(studentId);
    }

    public List<AcademicInternship> findActualInternshipsBySupervisor(int teacherId) {
        return internships.findActualInternshipsBySupervisor(teacherId);
    }

    public List<AcademicInternship> findInternshipsByDepartment(int departmentId, boolean activeOnly) {
        return internships.findInternshipsByDepartment(departmentId, activeOnly);
    }

    public AcademicInternshipExtList findInternshipsByDepartmentExt(int departmentId, boolean openOnly) {
        return internships.findInternshipsByDepartmentExt(departmentId, openOnly);
    }

    public List<AcademicInternship> findActualInternshipsByTeacher(int teacherId, int boardId) {
        return internships.findActualInternshipsByTeacher(teacherId, boardId);
    }

    public AcademicInternshipExtList findInternshipsByTeacherExt(int periodId, int deptId, int teacherId, int internshipTypeId, int boardId, boolean openOnly) {
        return internships.findInternshipsByTeacherExt(periodId, deptId, teacherId, internshipTypeId, boardId, openOnly);
    }

    public List<AcademicInternship> findInternships(int teacherId, int groupId, int boardId, int deptId, int internshipTypeId, boolean openOnly) {
        return internships.findInternships(teacherId, groupId, boardId, deptId, internshipTypeId, openOnly);
    }

    public AcademicInternshipSupervisorIntent findInternshipTeacherIntent(int internship, int teacherId) {
        return internships.findInternshipTeacherIntent(internship, teacherId);
    }

    public AcademicTeacherPeriod findTeacherPeriod(int periodId, int teacherId) {
        return teachers.findAcademicTeacherPeriod(periodId, teacherId);
    }

    public String getValidName(AcademicTeacher t) {
        return teachers.getValidName(t);
    }

    public void validateAcademicData_Teacher(int teacherId, int periodId) {
        teachers.validateAcademicData_Teacher(teacherId, periodId);
    }

    public void updateTeacherPeriod(int periodId, int teacherId, int copyFromPeriod) {
        teachers.updateTeacherPeriod(periodId, teacherId, copyFromPeriod);
    }

    public List<AcademicStudent> findStudentsByClass(int classId, int... classNumber) {
        return students.findStudentsByClass(classId, classNumber);
    }

    public void updateStudentClassByClass(int classNumber, int fromClassId, int toClassId) {
        students.updateStudentClassByClass(classNumber, fromClassId, toClassId);
    }

    public AcademicStudent updateStudentClass(int studentId, int classNumber, int classId) {
        return students.updateStudentClass(studentId, classNumber, classId);
    }

    public AcademicStudent findStudent(int t) {
        return students.findStudent(t);
    }

    public boolean addUserForStudent(AcademicStudent academicStudent) {
        return students.addUserForStudent(academicStudent);
    }

    public String getValidName(AcademicStudent t) {
        return students.getValidName(t);
    }

    public void validateAcademicData_Student(int studentId, int periodId) {
        students.validateAcademicData_Student(studentId, periodId);
    }

    public List<AcademicClass> findStudentClasses(int studentId, boolean down, boolean up) {
        return students.findStudentClasses(studentId, down, up);
    }

    public double evalHistValueEquiv(int yearId, LoadValue value, String degree) {
        return loads.evalHistValueEquiv(yearId, value, degree);
    }

    public double evalHistValueEquiv(LoadValue v, AcademicHistTeacherDegree dd) {
        return loads.evalHistValueEquiv(v, dd);
    }

    public void addAcademicTeacherSemestrialLoad(int semester, int weeksLoad, int teacherId, int periodId) {
        loads.addAcademicTeacherSemestrialLoad(semester, weeksLoad, teacherId, periodId);
    }

    public void updateAllCoursePlanValuesByLoadValues(int periodId) {
        loads.updateAllCoursePlanValuesByLoadValues(periodId);
    }

    public void updateCoursePlanValuesByLoadValues(int coursePlanId) {
        loads.updateCoursePlanValuesByLoadValues(coursePlanId);
    }

    public AcademicLoadConversionRule findLoadConversionRule(String t) {
        return loads.findLoadConversionRule(t);
    }

    public AcademicLoadConversionTable findLoadConversionTable(String t) {
        return loads.findLoadConversionTable(t);
    }

    public AcademicLoadConversionRow findLoadConversionRow(int tableId, int ruleId) {
        return loads.findLoadConversionRow(tableId, ruleId);
    }

    public void importTeachingLoad(int periodId) {
        imports.importTeachingLoad(periodId);
    }

    public List<AcademicHistCourseAssignment> findHistCourseAssignments(int yearId, Integer teacher, Integer semester) {
        return historys.findHistCourseAssignments(yearId, teacher, semester);
    }

    public List<AcademicHistProgram> findHistPrograms(int yearId) {
        return historys.findHistPrograms(yearId);
    }

    public List<AcademicHistTeacherDegree> findHistTeacherDegrees(int yearId) {
        return historys.findHistTeacherDegrees(yearId);
    }

    public AcademicHistTeacherDegree findHistTeacherDegree(int yearId, String t) {
        return historys.findHistTeacherDegree(yearId, t);
    }

    public void resetHistAcademicYear(int year) {
        historys.resetHistAcademicYear(year);
    }

    public void resetHistAcademicYears() {
        historys.resetHistAcademicYears();
    }

    public List<AcademicHistTeacherAnnualLoad> findHistTeacherAnnualLoads(int year) {
        return historys.findHistTeacherAnnualLoads(year);
    }

    public AcademicHistTeacherAnnualLoad findHistTeacherAnnualLoad(int year, int teacherId) {
        return historys.findHistTeacherAnnualLoad(year, teacherId);
    }

    public List<AcademicHistTeacherSemestrialLoad> findHistTeacherSemestrialLoads(int year, int teacherId) {
        return historys.findHistTeacherSemestrialLoads(year, teacherId);
    }

    public List<AcademicHistTeacherSemestrialLoad> findHistTeacherSemestrialLoads(int year) {
        return historys.findHistTeacherSemestrialLoads(year);
    }

    public List<AcademicHistCourseAssignment> findHistCourseAssignments(int yearId) {
        return historys.findHistCourseAssignments(yearId);
    }

    public List<AcademicHistCoursePlan> findHistCoursePlans(int year) {
        return historys.findHistCoursePlans(year);
    }

    public AppPeriod addAcademicYearSnapshot(String year, String snapshotName) {
        return historys.addAcademicYearSnapshot(year, snapshotName);
    }

    public void resetModuleTeaching() {
        historys.resetModuleTeaching();
    }

    public void resetTeachers() {
        historys.resetTeachers();
    }

    public void resetAssignments() {
        historys.resetAssignments();
    }

    public void resetCourses() {
        historys.resetCourses();
    }

    public void resetCurrentYear() {
        historys.resetCurrentYear();
    }

    public List<AcademicProgram> findPrograms() {
        return config.findPrograms();
    }

    public List<AcademicTeacherDegree> findTeacherDegrees() {
        return config.findTeacherDegrees();
    }

    public List<AcademicTeacherSituation> findTeacherSituations() {
        return config.findTeacherSituations();
    }

    public List<AcademicPreClass> findAcademicPreClasses() {
        return config.findAcademicPreClasses();
    }

    public List<AcademicBac> findAcademicBacs() {
        return config.findAcademicBacs();
    }

    public Map<Integer, AcademicClass> findAcademicClassesMap() {
        return config.findAcademicClassesMap();
    }

    public Map<Integer, AcademicPreClass> findAcademicPreClassesMap() {
        return config.findAcademicPreClassesMap();
    }

    public Map<Integer, AcademicBac> findAcademicBacsMap() {
        return config.findAcademicBacsMap();
    }

    public List<AcademicClass> findAcademicClasses() {
        return config.findAcademicClasses();
    }

    public List<AcademicCourseLevel> findCourseLevels() {
        return config.findCourseLevels();
    }

    public AcademicCourseLevel findCourseLevel(int academicClassId, int semesterId) {
        return config.findCourseLevel(academicClassId, semesterId);
    }

    public List<AcademicSemester> findSemesters() {
        return config.findSemesters();
    }

    public AcademicProgram findProgram(int departmentId, String t) {
        return config.findProgram(departmentId, t);
    }

    public List<AcademicProgram> findPrograms(String t) {
        return config.findPrograms(t);
    }

    public AcademicSemester findSemester(String code) {
        return config.findSemester(code);
    }

    public AcademicSemester findSemester(int id) {
        return config.findSemester(id);
    }

    public AcademicCoursePlan findCoursePlan(int periodId, int courseLevelId, String courseName) {
        return config.findCoursePlan(periodId, courseLevelId, courseName);
    }

    public AcademicTeacherDegree findTeacherDegree(String t) {
        return config.findTeacherDegree(t);
    }

    public AcademicTeacherDegree findTeacherDegree(int id) {
        return config.findTeacherDegree(id);
    }

    public AcademicTeacherSituation findTeacherSituation(String t) {
        return config.findTeacherSituation(t);
    }

    public List<AcademicTeacherSituation> findTeacherSituations(AcademicTeacherSituationType type) {
        return config.findTeacherSituations(type);
    }

    public AcademicTeacherSituation findTeacherSituation(int id) {
        return config.findTeacherSituation(id);
    }

    public AcademicCourseLevel findCourseLevel(int programId, String name) {
        return config.findCourseLevel(programId, name);
    }

    public AcademicCourseGroup findCourseGroup(int periodId, int classId, String name) {
        return config.findCourseGroup(periodId, classId, name);
    }

    public List<AcademicCourseGroup> findCourseGroups(int periodId) {
        return config.findCourseGroups(periodId);
    }

    public List<AcademicDiscipline> findDisciplines() {
        return config.findDisciplines();
    }

    public AcademicOfficialDiscipline findOfficialDiscipline(int id) {
        return config.findOfficialDiscipline(id);
    }

    public AcademicOfficialDiscipline findOfficialDiscipline(String name) {
        return config.findOfficialDiscipline(name);
    }

    public List<AcademicOfficialDiscipline> findOfficialDisciplines() {
        return config.findOfficialDisciplines();
    }

    public List<AcademicCourseCore> findAcademicCourseCores(Integer periodId,Integer programId) {
        return config.findAcademicCourseCores(periodId,programId);
    }

    public AcademicDiscipline findDiscipline(String nameOrCode) {
        return config.findDiscipline(nameOrCode);
    }

    public AcademicCourseType findCourseType(int id) {
        return config.findCourseType(id);
    }

    public AcademicCourseType findCourseType(String name) {
        return config.findCourseType(name);
    }

    public List<AcademicCourseType> findCourseTypes() {
        return config.findCourseTypes();
    }

    public List<AcademicCoursePlan> findCoursePlans(int periodId, int semesterId, int programId) {
        return config.findCoursePlans(periodId, semesterId, programId);
    }

    public List<AcademicCoursePlan> findCoursePlans(int periodId) {
        return config.findCoursePlans(periodId);
    }

    public AcademicClass findAcademicClass(String t) {
        return config.findAcademicClass(t);
    }

    public AcademicClass findAcademicClass(int id) {
        return config.findAcademicClass(id);
    }

    public AcademicClass findAcademicClass(int programId, String t) {
        return config.findAcademicClass(programId, t);
    }

    public List<AcademicClass> findAcademicClasses(String t) {
        return config.findAcademicClasses(t);
    }

    public Set<String> parseDisciplinesNames(String value, boolean autoCreate) {
        return config.parseDisciplinesNames(value, autoCreate);
    }

    public List<AcademicDiscipline> parseDisciplines(String value, boolean autoCreate) {
        return config.parseDisciplines(value, autoCreate);
    }

    public String formatDisciplinesForLocale(String value, String locale) {
        return config.formatDisciplinesForLocale(value, locale);
    }

    public List<String> parseDisciplinesForLocale(String value, String locale) {
        return config.parseDisciplinesForLocale(value, locale);
    }

    public List<AcademicDiscipline> parseDisciplinesZombies(String value) {
        return config.parseDisciplinesZombies(value);
    }

    public AppPeriod findAcademicYear(String name, String snapshot) {
        return config.findAcademicYear(name, snapshot);
    }

    public AppPeriod findAcademicYear(int id) {
        return config.findAcademicYear(id);
    }

    public AcademicBac findAcademicBac(int id) {
        return config.findAcademicBac(id);
    }

    public AcademicBac findAcademicBac(String name) {
        return config.findAcademicBac(name);
    }

    public AcademicPreClass findAcademicPreClass(int id) {
        return config.findAcademicPreClass(id);
    }

    public AcademicInternshipGroup findInternshipGroup(int id) {
        return config.findInternshipGroup(id);
    }

    public AcademicPreClassType findAcademicPreClassType(int id) {
        return config.findAcademicPreClassType(id);
    }

    public AcademicPreClass findAcademicPreClass(String name) {
        return config.findAcademicPreClass(name);
    }

    public AcademicPreClassType findAcademicPreClassType(String name) {
        return config.findAcademicPreClassType(name);
    }

    public List<AcademicPreClassType> findAcademicPreClassTypes() {
        return config.findAcademicPreClassTypes();
    }

    public AppPeriod findAcademicYearSnapshot(String t, String snapshotName) {
        return config.findAcademicYearSnapshot(t, snapshotName);
    }

    public List<AppPeriod> findAcademicYearSnapshots(String t) {
        return config.findAcademicYearSnapshots(t);
    }

    public AppPeriod findOrCreateAcademicYear(String academicYearName, String snapshot) {
        return config.findOrCreateAcademicYear(academicYearName, snapshot);
    }

    public AppPeriod createAcademicYear(String academicYearName, String snapshot) {
        return config.createAcademicYear(academicYearName, snapshot);
    }

    public void validateAcademicData(int periodId) {
        config.validateAcademicData(periodId);
    }

    public Document getDepartmentPeriodDocument(int periodId, int departmentId) {
        return config.getAppDepartmentPeriodRecord(periodId, departmentId);
    }

    public AppDepartmentPeriod getDepartmentPeriod(int periodId, int departmentId) {
        return config.getAppDepartmentPeriod(periodId, departmentId);
    }

    public String formatDisciplinesNames(String value, boolean autoCreate) {
        return config.formatDisciplinesNames(value, autoCreate);
    }

    public AcademicCoursePlan findCoursePlan(int id) {
        return config.findCoursePlan(id);
    }

    public AcademicSemester getCurrentSemester() {
        return config.getCurrentSemester();
    }

    public void academicCoursePlan_validationErrors_Formula_fix(int academicPlanId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCoursePlan d = (AcademicCoursePlan) pu.findById(AcademicCoursePlan.class, academicPlanId);
        if (d == null) {
            return;
        }
        AcademicPlugin t = VrApp.getBean(AcademicPlugin.class);
        List<AcademicCourseAssignment> assignments = t.findAssignments(null, d.getId(), null, null, null, null, null);
//        Set<String> errors = new TreeSet<String>();
        boolean saveMe = false;
        if (d.getValueC() < 0) {
            d.setValueC(0);
            saveMe = true;
        } else if (d.getValueC() > 0 && d.getGroupCountC() == 0) {
            d.setGroupCountC(1);
            saveMe = true;
        } else if (d.getGroupCountC() < 0) {
            d.setGroupCountC(0);
            saveMe = true;
        }

        if (d.getValueTD() < 0) {
            d.setValueTD(0);
            saveMe = true;
        } else if (d.getValueTD() > 0 && d.getGroupCountTD() == 0) {
            d.setGroupCountTD(1);
            saveMe = true;
        } else if (d.getGroupCountTD() < 0) {
            d.setGroupCountTD(0);
            saveMe = true;
        }

        if (d.getValueTP() < 0) {
            d.setValueTP(0);
            saveMe = true;
        } else if (d.getValueTP() > 0 && d.getGroupCountTP() == 0) {
            d.setGroupCountTP(2);
            saveMe = true;
        } else if (d.getGroupCountTP() < 0) {
            d.setGroupCountTP(0);
            saveMe = true;
        }

        if (d.getValuePM() < 0) {
            d.setValuePM(0);
            saveMe = true;
        } else if (d.getValuePM() > 0 && d.getGroupCountPM() == 0) {
            d.setGroupCountPM(2);
            saveMe = true;
        } else if (d.getGroupCountPM() < 0) {
            d.setGroupCountPM(2);
            saveMe = true;
        }
        
//        if (d.getGroupCountC() > 0) {
//            if (d.getGroupCountTP() > 0 && d.getGroupCountTP() != d.getGroupCountC() * 2) {
//                d.setGroupCountTP(d.getGroupCountC() * 2);
//            }
//            if (d.getGroupCountPM() > 0 && d.getGroupCountPM() != d.getGroupCountC() * 2) {
//                d.setGroupCountPM(d.getGroupCountC() * 2);
//            }
//        }

        if (saveMe) {
            pu.merge(d);
        }
        double c = 0;
        double td = 0;
        double tp = 0;
        double pm = 0;
        for (AcademicCourseAssignment assignment : assignments) {
            saveMe = false;
            double ac = assignment.getValueC();
            double atd = assignment.getValueTD();
            double atp = assignment.getValueTP();
            double apm = assignment.getValuePM();
            double g = assignment.getGroupCount();
            if (g < 0) {
                g = 0;
                assignment.setGroupCount(g);
                saveMe = true;
            }
            if (g <= 0) {
                if (assignment.getValueC() > 0 || assignment.getValueTD() > 0) {
                    assignment.setGroupCount(1);
                    saveMe = true;
                } else if (assignment.getValueTP() > 0 || assignment.getValuePM() > 0) {
                    assignment.setGroupCount(2);
                    saveMe = true;
                }
            }
            c += ac * g;
            td += atd * g;
            tp += atp * g;
            pm += apm * g;
            if (saveMe) {
                pu.merge(assignment);
            }
        }
//        double epsilon = 1E-3;
//        double err = 1E-3;
//        for (AcademicCourseAssignment assignment : assignments) {
//            saveMe = false;
//            if ((err = VrUtils.compareLenientV(d.getValueC() * d.getGroupCountC(), c, epsilon)) != 0) {
//                if (c != 0) {
//                    double v2 = assignment.getValueC() * assignment.getGroupCount() / assignment.getShareCount() / c * d.getValueC();
//                    assignment.setValueC(v2);
//                    saveMe=true;
//                }
//            }
//            if ((err = VrUtils.compareLenientV(d.getValueTD() * d.getGroupCountTD(), c, epsilon)) != 0) {
//                if (c != 0) {
//                    double v2 = assignment.getValueTD() * assignment.getGroupCount() / assignment.getShareCount() / c * d.getValueTD();
//                    assignment.setValueTD(v2);
//                    saveMe=true;
//                }
//            }
//            if ((err = VrUtils.compareLenientV(d.getValueTP() * d.getGroupCountTP(), c, epsilon)) != 0) {
//                if (c != 0) {
//                    double v2 = assignment.getValueTP() * assignment.getGroupCount() / assignment.getShareCount() / c * d.getValueTP();
//                    assignment.setValueTP(v2);
//                    saveMe=true;
//                }
//            }
//            if ((err = VrUtils.compareLenientV(d.getValuePM() * d.getGroupCountTP(), c, epsilon)) != 0) {
//                if (c != 0) {
//                    double v2 = assignment.getValuePM() * assignment.getGroupCount() / assignment.getShareCount() / c * d.getValuePM();
//                    assignment.setValuePM(v2);
//                    saveMe=true;
//                }
//            }
//            if(saveMe){
//                pu.merge(assignment);
//            }
//        }
    }

    public boolean isCurrentTeacher(int teacherId) {
        Integer uid = core.getCurrentUserId();
        if (uid == null) {
            return false;
        }
        AcademicPlugin ac = AcademicPlugin.get();
        AcademicTeacher teacher = ac.findTeacher(teacherId);
        Integer tid = (teacher == null || teacher.getUser() == null) ? null : teacher.getUser().getId();
        if (tid == null) {
            return false;
        }
        return uid.equals(tid);
    }

    public int getTeacherWeeks(int periodId, int teacherId) {
        int maxWeeks = 0;
        List<AcademicTeacherSemestrialLoad> aa = findTeacherSemestrialLoadsByTeacher(periodId, teacherId);
        for (AcademicTeacherSemestrialLoad a : aa) {
            if (a.getWeeksLoad() > 0) {
                maxWeeks += a.getWeeksLoad();
            }
        }
        if (maxWeeks <= 0) {
            maxWeeks = getSemesterMaxWeeks();
        }
        if (maxWeeks <= 0) {
            maxWeeks = 14;
        }
        return maxWeeks;
    }

    public TeacherLoadInfo getTeacherLoadInfo(TeacherLoadInfoFilter f) {
        TeacherLoadInfo result = new TeacherLoadInfo(f.getPeriodId());

        Map<Integer, AcademicCourseAssignmentInfoByVisitor> all = new HashMap<>();
        DefaultCourseAssignmentFilter otherCourseAssignmentsFilter = f.getOtherCourseAssignmentFilter();
        otherCourseAssignmentsFilter.setAcceptAssignments(null).setAcceptIntents(true).setAcceptNoTeacher(true);
//        boolean includeIntents = !isFiltered("no-current-intents");
        List<AcademicCourseAssignmentInfo> othersAssignmentsAndIntents = findCourseAssignmentsAndIntents(f.getPeriodId(), null, otherCourseAssignmentsFilter);
        AcademicConversionTableHelper conversionTable = findConversionTableByPeriodId(f.getPeriodId());
        AcademicTeacherPeriod tp = findTeacherPeriod(f.getPeriodId(), f.getTeacherId());
        int maxWeeks = getTeacherWeeks(f.getPeriodId(), f.getTeacherId());
//        AcademicTeacherDegree dd = findTeacherDegree("MA");
        for (AcademicCourseAssignmentInfo b : othersAssignmentsAndIntents) {
            all.put(b.getAssignment().getId(), new AcademicCourseAssignmentInfoByVisitor(b, f.getTeacherId(), tp == null ? 0 : evalValueEquiv(b.getLoadValue(), tp.getDegree(), conversionTable) / maxWeeks));
        }
        result.setAll(all);
        HashSet<Integer> visitedAssignmentId = new HashSet<Integer>();
        if (f.getTeacherId() != -1) {
            DefaultCourseAssignmentFilter courseAssignmentFilter = f.getTeacherCourseAssignmentFilter();
            DeviationConfig deviationConfig = f.getDeviationConfig();
            List<AcademicCourseAssignmentInfo> teacherAssignmentsAndIntents = findCourseAssignmentsAndIntents(f.getPeriodId(), f.getTeacherId(), courseAssignmentFilter);
            for (AcademicCourseAssignmentInfo b : teacherAssignmentsAndIntents) {
                if (!all.containsKey(b.getAssignment().getId())) {
                    all.put(b.getAssignment().getId(), new AcademicCourseAssignmentInfoByVisitor(b, f.getTeacherId(), tp == null ? 0 : evalValueEquiv(b.getLoadValue(), tp.getDegree(), conversionTable) / maxWeeks));
                }
            }

            TeacherPeriodStat stat = evalTeacherStat(f.getPeriodId(), f.getTeacherId(), courseAssignmentFilter, deviationConfig, null);
            for (TeacherSemesterStat teacherSemesterStat : stat.getSemesters()) {
                for (AcademicCourseAssignmentInfo m : teacherSemesterStat.getAssignments()) {
                    visitedAssignmentId.add(m.getAssignment().getId());
                }
            }
            result.setStat(new TeacherPeriodStatExt(stat, all));
        }

        boolean conflict = f.isFilterConflict();
        List<AcademicCourseAssignmentInfo> others = new ArrayList<>();

        boolean filterAssigned = f.isFilterAssigned();
        boolean filterNonassigned = f.isFilterNonAssigned();
        boolean filterIntended = f.isFilterIntended();
        boolean filterNonintended = f.isFilterNonIntended();
        boolean filterLocked = f.isFilterLocked();
        boolean filterUnlocked = f.isFilterUnlocked();

        if (!filterAssigned && !filterNonassigned) {
            filterAssigned = true;
            filterNonassigned = true;
        }
        if (!filterIntended && !filterNonintended) {
            filterIntended = true;
            filterNonintended = true;
        }
        if (!filterLocked && !filterUnlocked) {
            filterLocked = true;
            filterUnlocked = true;
        }

        for (AcademicCourseAssignmentInfo c : othersAssignmentsAndIntents) {
            if (!visitedAssignmentId.contains(c.getAssignment().getId())) {
                boolean _assigned = c.isAssigned();
                boolean _locked = c.getAssignment().isLocked();
                Map<Integer, TeacherAssignmentChunck> chuncks = c.getAssignmentChunck().getChuncks();
                int chunk_size = chuncks.size();
                boolean _intended = chunk_size > 0;
                boolean accepted = true;
                if (((filterAssigned && _assigned) || (filterNonassigned && !_assigned))
                        && ((filterIntended && _intended) || (filterNonintended && !_intended))
                        && ((filterLocked && _locked) || (filterUnlocked && !_locked))) {
                    //ok
                } else {
                    accepted = false;
                }
                if (accepted && conflict) {
                    //show only with conflicts
                    if (chuncks.isEmpty()) {
                        accepted = false;
                    } else if (c.getAssignment().getTeacher() != null) {
                        if (chunk_size > 1) {
                            accepted = true;
                        } else if (chunk_size == 1) {
                            TeacherAssignmentChunck first = (TeacherAssignmentChunck) chuncks.values().toArray()[0];
                            accepted = c.getAssignment().getTeacher().getId() != first.getTeacherId();
                        }
                    } else {
                        accepted = chunk_size > 1;
                    }
                }
                if (accepted) {
                    boolean powerUser = CorePlugin.get().isCurrentSessionAdminOrManager();
                    if ((c.getAssignment().isLocked() || c.getAssignment().getCoursePlan().isLocked()) && !powerUser) {
                        //dont add
                    } else {
                        others.add(c);
                    }
                }
            }
        }

        result.setNonFilteredOthers(wrap(others, all));
        return result;
    }

    public static List<AcademicCourseAssignmentInfoByVisitor> wrap(List<AcademicCourseAssignmentInfo> val, Map<Integer, AcademicCourseAssignmentInfoByVisitor> all) {
        List<AcademicCourseAssignmentInfoByVisitor> assignments;
        assignments = new ArrayList<>();
        for (AcademicCourseAssignmentInfo a : val) {
            AcademicCourseAssignmentInfoByVisitor e = all.get(a.getAssignment().getId());
            if (e == null) {
                throw new RuntimeException();
            }
            assignments.add(e);
        }
        return assignments;
    }

    public TeacherLoadInfo teacherLoadInfoAssignmentsToIntentsAll(TeacherLoadInfo info) {
        ArrayList<AcademicCourseAssignmentInfoByVisitor> assignmentInfos = new ArrayList<>();
        assignmentInfos.addAll(info.getStat().getAssignments());
        assignmentInfos.addAll(info.getOthers());
        for (AcademicCourseAssignmentInfoByVisitor aa : assignmentInfos) {
            AcademicCourseAssignment assignment = aa.getValue().getAssignment();
            AcademicTeacher t = assignment.getTeacher();
            if (t != null) {
                if (teacherLoadInfoIsAllowedUpdateMineIntents(info, assignment.getId())) {
                    this.addIntent(t.getId(), assignment.getId());
                    this.removeCourseAssignment(assignment.getId(), false, false);
                }
            }
        }
        return info;
    }

    public boolean teacherLoadInfoIsAllowedUpdateMineIntents(TeacherLoadInfo info, Integer assignmentId) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        Integer userId = core.getCurrentUserId();
        if (userId == null) {
            return false;
        }
        if (core.isCurrentSessionAdmin()) {
            return true;
        }

        AppPeriod period = core.findPeriod(info.getPeriodId());
        if (period == null || period.isReadOnly()) {
            return false;
        }
        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher teacher = a.findTeacherByUser(userId);
        if (teacher == null) {
            return false;
        }
        if (assignmentId != null) {
            AcademicCourseAssignmentInfoByVisitor t0 = info.getAll().get(assignmentId);
            AcademicCourseAssignment t = t0 == null ? null : t0.getValue().getAssignment();
            if (t != null) {
                AppUser user = core.findUser(userId);
                if (user != null) {
                    AppDepartment d = t.getOwnerDepartment();
                    if (d != null) {
                        if (core.isCurrentAllowed(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_INTENTS)) {
                            AppDepartment d2 = user.getDepartment();
                            if (d2 != null && d2.getId() == d.getId()) {
                                return true;
                            }
                        }
                    }
                    d = t.resolveDepartment();
                    if (d != null) {
                        if (core.isCurrentAllowed(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_INTENTS)) {
                            AppDepartment d2 = user.getDepartment();
                            if (d2 != null && d2.getId() == d.getId()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public TeacherLoadInfo teacherLoadInfoApplyTextFilter(TeacherLoadInfo info, String filter) {
        if (StringUtils.isBlank(filter)) {
            info.setOthers(new ArrayList<>(info.getNonFilteredOthers()));
        } else {
            info.setOthers(new TextSearchFilter(filter,
                    new ObjectToMapConverter() {
                @Override
                public Map<String, Object> convert(Object o) {
                    Map<String, Object> m = new HashMap();
//                        m.putAll(DefaultObjectToMapConverter.INSTANCE.convert(o));
                    AcademicCourseAssignmentInfoByVisitor sa = (AcademicCourseAssignmentInfoByVisitor) o;
                    m.putAll(DefaultObjectToMapConverter.INSTANCE.convert(sa.getValue().getAssignment()));
                    int keyIndex = 1;
                    for (TeacherAssignmentChunck chunck : sa.getValue().getAssignmentChunck().getChuncks().values()) {
                        m.put("key" + keyIndex, chunck.getTeacherName());
                        keyIndex++;
                    }
                    for (TeacherAssignmentChunck chunck : sa.getValue().getCourseChunck().getChuncks().values()) {
                        m.put("key" + keyIndex, chunck.getTeacherName());
                        keyIndex++;
                    }
                    return m;
                }
            }
            ).filterList(info.getNonFilteredOthers()));
        }
        info.setLoadSum(new LoadValue());
        AcademicConversionTableHelper conversionTableByPeriodId = findConversionTableByPeriodId(info.getPeriodId());
        AcademicTeacherDegree dd = findTeacherDegree("MA");
        for (AcademicCourseAssignmentInfoByVisitor other : info.getOthers()) {
            AcademicCourseAssignment a = other.getAssignment();
            info.getLoadSum().add(
                    new LoadValue(a.getValueC(), a.getValueTD(), a.getValueTP(), a.getValuePM())
            );

        }
        info.setMaLoad(evalValueEquiv(info.getLoadSum(), dd, conversionTableByPeriodId) / 9.5 / 28);
        return info;
    }

    public boolean teacherLoadInfoIsAllowedUpdateMineAssignments(TeacherLoadInfo info, Integer assignementId) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        Integer userId = core.getCurrentUserId();
        if (userId == null) {
            return false;
        }
        AppPeriod period = core.findPeriod(info.getPeriodId());

        if (core.isCurrentSessionAdmin()) {
            return true;
        }

        if (period == null || period.isReadOnly()) {
            return false;
        }

        if (assignementId != null) {
            AcademicCourseAssignmentInfoByVisitor t0 = info.getAll().get(assignementId);
            AcademicCourseAssignment t = t0 == null ? null : t0.getValue().getAssignment();
            AppUser u = core.findUser(userId);
            if (u != null && t != null) {
                AppDepartment d = t.getOwnerDepartment();
                if (d != null) {
                    if (core.isCurrentAllowed(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_ASSIGNMENTS)) {
                        AppDepartment d2 = u.getDepartment();
                        if (d2 != null && d2.getId() == d.getId()) {
                            return true;
                        }
                    }
                }
                d = t.resolveDepartment();
                if (d != null) {
                    if (core.isCurrentAllowed(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_ASSIGNMENTS)) {
                        AppDepartment d2 = u.getDepartment();
                        if (d2 != null && d2.getId() == d.getId()) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    public boolean teacherLoadInfoDoAssignByIntent(TeacherLoadInfo info, Integer assignementId) {
        if (assignementId != null) {
            AcademicCourseAssignmentInfoByVisitor rr = info.getAll().get(assignementId);
            if (rr != null) {
                final Set<Integer> s0 = rr.getValue().getAssignmentChunck().getChuncks().keySet();
                if (s0.size() > 0) {
                    List<Integer> s = new ArrayList<>(s0);
                    AcademicTeacher oldTeacher = rr.getValue().getAssignment().getTeacher();
                    int newTeacherId = -1;
                    if (oldTeacher == null) {
                        newTeacherId = s.get(0);
                    } else {
                        int lastPos = s.indexOf(oldTeacher.getId());
                        if (lastPos < 0) {
                            lastPos = 0;
                        } else {
                            lastPos = (lastPos + 1) % s.size();
                        }
                        newTeacherId = s.get(lastPos);
                    }
                    this.addCourseAssignment(newTeacherId, assignementId);
                }
                return true;
            }
        }
        return false;
    }

    public boolean teacherLoadInfoDoAssignByIntentSelected(TeacherLoadInfo info) {
        for (AcademicCourseAssignmentInfoByVisitor s : info.getAll().values()) {
            if (s.isSelected()) {
                int assignementId = s.getValue().getAssignment().getId();
                AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);

                AcademicCourseAssignmentInfoByVisitor rr = info.getAll().get(assignementId);
                if (rr != null) {
                    final Set<Integer> s0 = rr.getValue().getAssignmentChunck().getChuncks().keySet();
                    if (s0.size() > 0) {
                        List<Integer> selId = new ArrayList<>(s0);
                        AcademicTeacher oldTeacher = rr.getValue().getAssignment().getTeacher();
                        int newTeacherId = -1;
                        if (oldTeacher == null) {
                            newTeacherId = selId.get(0);
                        } else {
                            int lastPos = selId.indexOf(oldTeacher.getId());
                            if (lastPos < 0) {
                                lastPos = 0;
                            } else {
                                lastPos = (lastPos + 1) % selId.size();
                            }
                            newTeacherId = selId.get(lastPos);
                        }
                        a.addCourseAssignment(newTeacherId, assignementId);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public List<AcademicCourseAssignment> findNonUniqueAcademicCourseAssignments(int fromPeriodId) {
        final AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        List<AcademicCourseAssignment> courseAssignments = p.findCourseAssignments(fromPeriodId);
        HashMap<String, AcademicCourseAssignment> visited = new HashMap<>();
        Function<AcademicCourseAssignment, String> converter = AcademicCourseAssignment::getFullName;
        Map<Integer, AcademicCourseAssignment> toUpdate = new HashMap<>();
        for (AcademicCourseAssignment assignment : courseAssignments) {
            String item = converter.apply(assignment);
            if (visited.containsKey(item)) {
                if (!toUpdate.containsKey(visited.get(item).getId())) {
                    toUpdate.put(visited.get(item).getId(), visited.get(item));
                }
                if (toUpdate.containsKey(assignment.getId())) {
                    toUpdate.put(assignment.getId(), assignment);
                }
            } else {
                visited.put(item, assignment);
            }
        }
        return new ArrayList<>(toUpdate.values());
    }

    public void forceUniquenessAcademicCourseAssignment(int fromPeriodId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (AcademicCourseAssignment a : findNonUniqueAcademicCourseAssignments(fromPeriodId)) {
            if (StringUtils.isBlank(a.getDiscriminator())) {
                a.setDiscriminator(String.valueOf(a.getId()));
            } else {
                a.setDiscriminator(a.getDiscriminator() + "-" + String.valueOf(a.getId()));
            }
            pu.merge(a);
        }
    }

    public void generateAssignments(int coursePlanId) {
//        AcademicCourseType td = findCourseType("TD");
        AcademicCourseType tp = findCourseType("TP");
        AcademicCourseType pm = findCourseType("PM");
        AcademicCourseType c = findCourseType("C");
        AcademicCourseType cm = findCourseType("CM");
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCoursePlan coursePlan = findCoursePlan(coursePlanId);
        if (coursePlan != null) {
            List<AcademicCourseAssignment> courseAssignmentsByCoursePlan = findCourseAssignmentsByCoursePlan(coursePlan.getId());
            if (courseAssignmentsByCoursePlan.isEmpty()) {
                if (coursePlan.getValueC() != 0 && coursePlan.getValueTD() != 0 && c != null) {
                    AcademicCourseAssignment a = new AcademicCourseAssignment();
                    a.setCoursePlan(coursePlan);
                    a.setCourseType(c);
                    pu.persist(a);
                } else if (coursePlan.getValueC() != 0 && cm != null) {
                    AcademicCourseAssignment a = new AcademicCourseAssignment();
                    a.setCoursePlan(coursePlan);
                    a.setCourseType(cm);
                    pu.persist(a);
                }
                if (coursePlan.getValueTP() != 0 && tp != null) {
                    AcademicCourseAssignment a = new AcademicCourseAssignment();
                    a.setCoursePlan(coursePlan);
                    a.setCourseType(tp);
                    pu.persist(a);
                }

                if (coursePlan.getValuePM() != 0 && pm != null) {
                    AcademicCourseAssignment a = new AcademicCourseAssignment();
                    a.setCoursePlan(coursePlan);
                    a.setCourseType(pm);
                    pu.persist(a);
                }
            }
        }
    }

    public static class Extra {

        public static List<AcademicCourseAssignment> filterNonUniqueAcademicCourseAssignments(List<AcademicCourseAssignment> courseAssignments) {
            final AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            HashMap<String, AcademicCourseAssignment> visited = new HashMap<>();
            Function<AcademicCourseAssignment, String> converter = AcademicCourseAssignment::getFullName;
            Map<Integer, AcademicCourseAssignment> toUpdate = new HashMap<>();
            for (AcademicCourseAssignment assignment : courseAssignments) {
                String item = converter.apply(assignment);
                if (visited.containsKey(item)) {
                    if (!toUpdate.containsKey(visited.get(item).getId())) {
                        toUpdate.put(visited.get(item).getId(), visited.get(item));
                    }
                    if (!toUpdate.containsKey(assignment.getId())) {
                        toUpdate.put(assignment.getId(), assignment);
                    }
                } else {
                    visited.put(item, assignment);
                }
            }
            return new ArrayList<>(toUpdate.values());
        }

        public static List<Document> filterNonUniqueAcademicCourseAssignmentDocuments(List<Document> courseAssignments) {
            final AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            HashMap<String, Document> visited = new HashMap<>();
            Function<Document, String> converter = (Document value) -> value.getString("fullName");
            Map<Integer, Document> toUpdate = new HashMap<>();
            for (Document assignment : courseAssignments) {
                String item = converter.apply(assignment);
                if (visited.containsKey(item)) {
                    Document aa = visited.get(item);
                    if (!toUpdate.containsKey(aa.getInt("id"))) {
                        toUpdate.put(aa.getInt("id"), aa);
                    }
                    if (!toUpdate.containsKey(assignment.getInt("id"))) {
                        toUpdate.put(assignment.getInt("id"), assignment);
                    }
                } else {
                    visited.put(item, assignment);
                }
            }
            return new ArrayList<>(toUpdate.values());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    public List<AcademicClass> findAcademicUpHierarchyList(AcademicClass[] classes, Map<Integer, AcademicClass> allClasses) {
        return config.findAcademicUpHierarchyList(classes, allClasses);
    }

    public Set<Integer> findClassDownHierarchyIdList(int classId, Map<Integer, AcademicClass> allClasses) {
        return config.findClassDownHierarchyIdList(classId, allClasses);
    }

    public Set<Integer> findClassDownHierarchyIdList0(int classId, Map<Integer, AcademicClass> allClasses) {
        return config.findClassDownHierarchyIdList0(classId, allClasses);
    }

    public List<AcademicClass> findClassDownHierarchyList(AcademicClass[] classes, Map<Integer, AcademicClass> allClasses) {
        return config.findClassDownHierarchyList(classes, allClasses);
    }

    public List<AcademicPreClass> findPreClassDownHierarchyList(AcademicPreClass[] classes, Map<Integer, AcademicPreClass> allClasses) {
        return config.findPreClassDownHierarchyList(classes, allClasses);
    }

    public List<AcademicBac> findBacDownHierarchyList(AcademicBac[] classes, Map<Integer, AcademicBac> allClasses) {
        return config.findBacDownHierarchyList(classes, allClasses);
    }

    public LoadValue getAssignmentLoadValue(AcademicCourseAssignment assignment, AcademicTeacherDegree degree, AcademicConversionTableHelper conversionTable) {
        return loads.getAssignmentLoadValue(assignment, degree, conversionTable);
    }

    public double evalValueEquiv(LoadValue value, String degree, AcademicConversionTableHelper table) {
        return loads.evalValueEquiv(value, degree, table);
    }

    public double evalValueEquiv(LoadValue v, AcademicTeacherDegree dd, AcademicConversionTableHelper table) {
        return loads.evalValueEquiv(v, dd, table);
    }

    public LoadValue evalValueEquiv(double v, AcademicTeacherDegree dd, AcademicConversionTableHelper table) {
        return loads.evalValueEquiv(v, dd, table);
    }

    public TeacherPeriodStat evalTeacherStat(
            int periodId,
            int teacherId,
            CourseAssignmentFilter courseAssignmentFilter,
            DeviationConfig deviationConfig,
            ProgressMonitor mon
    ) {
        return loads.evalTeacherStat(periodId, teacherId, courseAssignmentFilter, deviationConfig, mon);
    }

    public TeacherPeriodStat evalTeacherStat0(
            int periodId,
            int teacherId,
            AcademicTeacher teacher,
            List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoads,
            List<AcademicCourseAssignmentInfo> assignments,
            CourseAssignmentFilter filter,
            DeviationConfig deviationConfig
    ) {
        return this.loads.evalTeacherStat0(periodId, teacherId, teacher, findTeacherSemestrialLoads, assignments, filter, deviationConfig);
    }
}
