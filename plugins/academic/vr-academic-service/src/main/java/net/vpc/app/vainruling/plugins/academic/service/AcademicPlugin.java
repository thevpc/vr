/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.core.service.stats.KPI;
import net.vpc.app.vainruling.core.service.stats.KPIGroupBy;
import net.vpc.app.vainruling.core.service.stats.KPIProcessProcessor;
import net.vpc.app.vainruling.core.service.stats.KPIResult;
import net.vpc.app.vainruling.core.service.util.NamedValueCount;
import net.vpc.app.vainruling.plugins.academic.service.helper.AcademicConversionTableHelper;
import net.vpc.app.vainruling.plugins.academic.service.helper.CopyAcademicDataHelper;
import net.vpc.app.vainruling.plugins.academic.service.model.config.*;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.history.*;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicStudentImport;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicTeacherImport;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.*;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExtList;
import net.vpc.app.vainruling.plugins.academic.service.stat.*;
import net.vpc.app.vainruling.plugins.academic.service.util.CourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.util.ImportOptions;
import net.vpc.app.vainruling.plugins.academic.service.util.TeacherPeriodFilter;
import net.vpc.common.strings.StringComparator;
import net.vpc.common.util.*;
import net.vpc.common.util.mon.ProgressMonitor;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.Document;
import net.vpc.upa.NamedId;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin()
public class AcademicPlugin {

    public static final Converter<AcademicTeacher, Integer> AcademicTeacherIdConverter = new Converter<AcademicTeacher, Integer>() {
        @Override
        public Integer convert(AcademicTeacher value) {
            return value.getId();
        }
    };

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

    @Install
    private void onInstall() {
        //this is workaround, because
        if (core == null) {
            core = CorePlugin.get();
        }
        for (String r : AcademicPluginSecurity.RIGHTS_ACADEMIC) {
            core.createRight(r, r);
        }

        for (AcademicPluginBody body : bodies) {
            body.setContext(bodyContext);
            body.install();
        }
    }

    @Start
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

    public LoadValue getAssignmentLoadValue(AcademicCourseAssignment assignment, AcademicTeacherDegree degree, AcademicConversionTableHelper conversionTable) {
        return loads.getAssignmentLoadValue(assignment, degree, conversionTable);
    }

    public ModuleStat evalModuleStat(int periodId, int courseAssignmentId, Integer forTeacherId) {
        return loads.evalModuleStat(periodId, courseAssignmentId, forTeacherId);
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

    public MapList<Integer, AcademicCourseAssignment> findCourseAssignments(int periodId, CourseAssignmentFilter filter) {
        return assignments.findCourseAssignments(periodId, filter);
    }

    public MapList<Integer, AcademicCourseAssignment> findCourseAssignments(int periodId) {
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

    public void splitShareCourseAssignment(int assignmentId) {
        assignments.splitShareCourseAssignment(assignmentId);
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

    public void removeIntent(int teacherId, int assignementId) {
        assignments.removeIntent(teacherId, assignementId);
    }

    public void removeAllIntents(int assignementId) {
        assignments.removeAllIntents(assignementId);
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

    public MapList<Integer, TeacherPeriodStat> evalTeacherStatList(final int periodId, TeacherPeriodFilter teacherFilter, CourseAssignmentFilter courseAssignmentFilter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        return loads.evalTeacherStatList(periodId, teacherFilter, courseAssignmentFilter, deviationConfig, mon);
    }

    public GlobalStat evalGlobalStat(int periodId, TeacherPeriodFilter teacherFilter, CourseAssignmentFilter filter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        return loads.evalGlobalStat(periodId, teacherFilter, filter, deviationConfig, mon);
    }

    public AcademicTeacher findTeacherByUser(int userId) {
        return teachers.findTeacherByUser(userId);
    }

    public AcademicTeacher findTeacherByContact(int contactId) {
        return teachers.findTeacherByContact(contactId);
    }

    public AcademicTeacher findTeacher(StringComparator t) {
        return teachers.findTeacher(t);
    }

    public List<AcademicTeacher> findTeachers(int period, TeacherPeriodFilter teacherFilter) {
        return teachers.findTeachers(period, teacherFilter);
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

    public List<AcademicTeacher> findTeachersWithAssignmentsOrIntents(int periodId, int semesterId, boolean includeAssignments, boolean includeIntents, int teacherDepId, int assignmentDepId) {
        return teachers.findTeachersWithAssignmentsOrIntents(periodId, semesterId, includeAssignments, includeIntents, teacherDepId, assignmentDepId);
    }

    public AcademicStudent findStudentByUser(Integer userId) {
        return students.findStudentByUser(userId);
    }

    public AcademicStudent findStudentByContact(int contactId) {
        return students.findStudentByContact(contactId);
    }

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

    public List<AcademicCourseAssignment> findAcademicCourseAssignmentListByCoursePlanId(int coursePlanId) {
        return assignments.findAcademicCourseAssignmentListByCoursePlanId(coursePlanId);
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

    public AcademicTeacherPeriod findAcademicTeacherPeriod(int periodId, AcademicTeacher t) {
        return teachers.findAcademicTeacherPeriod(periodId, t);
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

    public List<AcademicClass> findAcademicUpHierarchyList(AcademicClass[] classes, Map<Integer, AcademicClass> allClasses) {
        return config.findAcademicUpHierarchyList(classes, allClasses);
    }

    public Set<Integer> findAcademicDownHierarchyIdList(int classId, Map<Integer, AcademicClass> allClasses) {
        return config.findAcademicDownHierarchyIdList(classId, allClasses);
    }

    public Set<Integer> findAcademicDownHierarchyIdList0(int classId, Map<Integer, AcademicClass> allClasses) {
        return config.findAcademicDownHierarchyIdList0(classId, allClasses);
    }

    public List<AcademicClass> findAcademicDownHierarchyList(AcademicClass[] classes, Map<Integer, AcademicClass> allClasses) {
        return config.findAcademicDownHierarchyList(classes, allClasses);
    }

    public List<AcademicPreClass> findAcademicDownHierarchyList(AcademicPreClass[] classes, Map<Integer, AcademicPreClass> allClasses) {
        return config.findAcademicDownHierarchyList(classes, allClasses);
    }

    public List<AcademicBac> findAcademicDownHierarchyList(AcademicBac[] classes, Map<Integer, AcademicBac> allClasses) {
        return config.findAcademicDownHierarchyList(classes, allClasses);
    }

    public void validateAcademicData(int periodId) {
        config.validateAcademicData(periodId);
    }

    public Document getAppDepartmentPeriodRecord(int periodId, int departmentId) {
        return config.getAppDepartmentPeriodRecord(periodId, departmentId);
    }

    public AppDepartmentPeriod getAppDepartmentPeriod(int periodId, int departmentId) {
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
        List<AcademicCourseAssignment> assignments = t.findAcademicCourseAssignmentListByCoursePlanId(d.getId());
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
            double s = assignment.getShareCount();
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
            if (s < 1) {
                s = 1;
                assignment.setShareCount(s);
                saveMe = true;
            }
            if (s <= 1) {
                if (assignment.getValueC() > 0 || assignment.getValueTD() > 0) {
                    assignment.setShareCount(1);
                    saveMe = true;
                } else if (assignment.getValueTP() > 0 || assignment.getValuePM() > 0) {
                    assignment.setShareCount(1);
                    saveMe = true;
                }
            }
            c += ac * g / s;
            td += atd * g / s;
            tp += atp * g / s;
            pm += apm * g / s;
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

}
