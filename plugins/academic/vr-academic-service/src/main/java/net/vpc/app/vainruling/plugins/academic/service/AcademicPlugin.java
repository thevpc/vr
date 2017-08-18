/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.cache.EntityCache;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.model.content.ArticlesDisposition;
import net.vpc.app.vainruling.core.service.obj.AppEntityExtendedPropertiesProvider;
import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.stats.KPI;
import net.vpc.app.vainruling.core.service.stats.KPIGroupBy;
import net.vpc.app.vainruling.core.service.stats.KPIProcessProcessor;
import net.vpc.app.vainruling.core.service.stats.KPIResult;
import net.vpc.app.vainruling.core.service.util.NamedValueCount;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.academic.service.helper.AcademicConversionTableHelper;
import net.vpc.app.vainruling.plugins.academic.service.helper.CopyAcademicDataHelper;
import net.vpc.app.vainruling.plugins.academic.service.helper.TeacherGenerationHelper;
import net.vpc.app.vainruling.plugins.academic.service.helper.XlsxLoadImporter;
import net.vpc.app.vainruling.plugins.academic.service.model.config.*;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.history.*;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicStudentImport;
import net.vpc.app.vainruling.plugins.academic.service.model.imp.AcademicTeacherImport;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.*;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExt;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExtList;
import net.vpc.app.vainruling.plugins.academic.service.stat.*;
import net.vpc.app.vainruling.plugins.academic.service.util.CourseAssignmentFilterAnd;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultCourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.util.LocationInfo;
import net.vpc.common.strings.MapStringConverter;
import net.vpc.common.strings.StringComparator;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.*;
import net.vpc.common.util.mon.EnhancedProgressMonitor;
import net.vpc.common.util.mon.ProgressMonitor;
import net.vpc.common.util.mon.ProgressMonitorFactory;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.*;
import net.vpc.upa.types.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@AppPlugin()
public class AcademicPlugin implements AppEntityExtendedPropertiesProvider {

    public static final int DEFAULT_SEMESTER_MAX_WEEKS = 14;
    public static final Set<DeviationGroup> DEFAULT_DEVIATION_GROUPS = Collections.unmodifiableSet(new java.util.HashSet<DeviationGroup>(Arrays.asList(DeviationGroup.DEPARTMENT, DeviationGroup.DEGREE, DeviationGroup.SITUATION, DeviationGroup.DISCIPLINE)));
    public static final Converter<Integer, AcademicCourseAssignment> AcademicCourseAssignmentIdConverter = new Converter<Integer, AcademicCourseAssignment>() {
        @Override
        public Integer convert(AcademicCourseAssignment value) {
            return value.getId();
        }
    };
    public static final Converter<Integer, AcademicTeacher> AcademicTeacherIdConverter = new Converter<Integer, AcademicTeacher>() {
        @Override
        public Integer convert(AcademicTeacher value) {
            return value.getId();
        }
    };
    public static final Converter<Integer, AcademicCoursePlan> AcademicCoursePlanIdConverter = new Converter<Integer, AcademicCoursePlan>() {
        @Override
        public Integer convert(AcademicCoursePlan value) {
            return value.getId();
        }
    };
    public static final Comparator<GlobalAssignmentStat> GLOBAL_ASSIGNMENT_STAT_COMPARATOR = new Comparator<GlobalAssignmentStat>() {

        @Override
        public int compare(GlobalAssignmentStat o1, GlobalAssignmentStat o2) {
            int d1 = o1.getDegree() == null ? 0 : 1;
            int s1 = o1.getSemester() == null ? 0 : 1;
            int si1 = o1.getSituation() == null ? 0 : 1;
            int d2 = o2.getDegree() == null ? 0 : 1;
            int s2 = o2.getSemester() == null ? 0 : 1;
            int si2 = o2.getSituation() == null ? 0 : 1;
            int x = (s1 - s2);
            if (x != 0) {
                return x;
            }
            x = (o1.getSemester() == null ? "" : o1.getSemester().getName()).compareTo(o2.getSemester() == null ? "" : o2.getSemester().getName());
            if (x != 0) {
                return x;
            }

            x = (d1 + s1 + si1) - (d2 + s2 + si2);
            if (x != 0) {
                return x;
            }
            x = (s1 + si1 * 2 + d1 * 4) - (s2 + si2 * 2 + d2 * 4);
            if (x != 0) {
                return x;
            }
            x = (o1.getSemester() == null ? "" : o1.getSemester().getName()).compareTo(o2.getSemester() == null ? "" : o2.getSemester().getName());
            if (x != 0) {
                return x;
            }
            x = (o1.getSituation() == null ? "" : o1.getSituation().getName()).compareTo(o2.getSituation() == null ? "" : o2.getSituation().getName());
            if (x != 0) {
                return x;
            }
            x = (o1.getDegree() == null ? "" : o1.getDegree().getName()).compareTo(o2.getDegree() == null ? "" : o2.getDegree().getName());
            if (x != 0) {
                return x;
            }
            return 0;
        }

    };
    private static final Logger log = Logger.getLogger(AcademicPlugin.class.getName());
    private static Converter<Integer, TeacherPeriodStat> teacherPeriodStatMapListConverter = new Converter<Integer, TeacherPeriodStat>() {
        @Override
        public Integer convert(TeacherPeriodStat value) {
            return value.getTeacher().getId();
        }
    };
    @Autowired
    TraceService trace;
    @Autowired
    CorePlugin core;
    @Autowired
    CacheService cacheService;
    private CopyAcademicDataHelper copyAcademicDataHelper = new CopyAcademicDataHelper();
    private TeacherGenerationHelper teacherGenerationHelper = new TeacherGenerationHelper();

    public AcademicPlugin() {
    }

    public static AcademicPlugin get() {
        return VrApp.getBean(AcademicPlugin.class);
    }

    //    protected void generatePrintableTeacherListLoadFile(int yearId, Integer[] teacherIds, String semester, String template, String output) throws IOException {
//        TeacherStat[] stats = evalTeachersStat(yearId, teacherIds, semester);
//        generateTeacherListAssignmentsSummaryFile(yearId, stats, template, output);
//    }
//    private void generatePrintableTeacherLoadSheet(int yearId, int teacherId, WritableSheet sheet) throws IOException {
//        ExcelTemplate.generateExcelSheet(sheet, preparePrintableTeacherLoadProperties(yearId,teacherId));
//    }

    public static void main(String[] args) {

        AcademicLoadConversionRow R_MA = new AcademicLoadConversionRow();
        R_MA.setValueC(1.83);
        R_MA.setValueTD(1);
        R_MA.setValueTP(0.73);
        R_MA.setValuePM(0.49);
        AcademicLoadConversionRow R_PR = new AcademicLoadConversionRow();
        R_PR.setValueC(1);
        R_PR.setValueTD(0.75);
        R_PR.setValueTP(0.50);
        R_PR.setValuePM(0.33);

        LoadValue extra = new LoadValue().setC(0.1).setTd(-3).setTp(5).setPm(1);
        System.out.println(extra);
        System.out.println("eff=" + evalHeureSupp(extra, R_MA));

//        LoadValue extra2=new LoadValue().setC(-3).setTd(3   ).setTp(2).setPm(1);
        LoadValue extra2 = new LoadValue().setC(-3).setTd(3).setTp(0).setPm(0);
        System.out.println(extra2);
        System.out.println("eff2=" + evalHeureSupp(extra2, R_PR));
    }

    private static LoadValue reevalLoadValueFormulas(LoadValue sem_value, AcademicLoadConversionRow r) {
        sem_value.setTdtppm(sem_value.getTd() + sem_value.getTp() * (r.getValueTP() / r.getValueTD()) + sem_value.getPm() * (r.getValuePM() / r.getValueTD()));
        sem_value.setTppm(sem_value.getTp() + sem_value.getPm() * (r.getValuePM() / r.getValueTP()));
        return sem_value;
    }

    private static LoadValue evalHeureSupp(LoadValue sem_extra, AcademicLoadConversionRow r) {
        LoadValue sem_extraEff = new LoadValue();
        double ruleValueTD = r.getValueTD();
        sem_extraEff.setC(sem_extra.getC());
        sem_extraEff.setTd(sem_extra.getTd());
        sem_extraEff.setTp(sem_extra.getTp());
        sem_extraEff.setPm(sem_extra.getPm());
        if (r.getValueC() == 1) {
            if (sem_extraEff.getC() < 0 && sem_extraEff.getTd() > 0) {
                double d = sem_extraEff.getTd() * r.getValueTD();
                if (d >= -sem_extraEff.getC()) {
                    sem_extraEff.setTd((d + sem_extraEff.getC()) / r.getValueTD());
                    sem_extraEff.setC(0);
                } else {
                    sem_extraEff.setTd(0);
                    sem_extraEff.setC(d + sem_extraEff.getC());
                }
            }
            if (sem_extraEff.getC() < 0 && sem_extraEff.getTp() > 0) {
                double d = sem_extraEff.getTp() * r.getValueTP();
                if (d >= -sem_extraEff.getC()) {
                    sem_extraEff.setTp((d + sem_extraEff.getC()) / r.getValueTP());
                    sem_extraEff.setC(0);
                } else {
                    sem_extraEff.setTp(0);
                    sem_extraEff.setC(d + sem_extraEff.getC());
                }
            }
            if (sem_extraEff.getC() < 0 && sem_extraEff.getPm() > 0) {
                double d = sem_extraEff.getPm() * r.getValuePM();
                if (d >= -sem_extraEff.getC()) {
                    sem_extraEff.setTp((d + sem_extraEff.getC()) / r.getValuePM());
                    sem_extraEff.setC(0);
                } else {
                    sem_extraEff.setTp(0);
                    sem_extraEff.setC(d + sem_extraEff.getC());
                }
            }
        } else if (ruleValueTD == 1) {
            if (sem_extraEff.getTd() < 0 && sem_extraEff.getC() > 0) {
                double d = sem_extraEff.getC() * r.getValueC();
                if (d >= -sem_extraEff.getTd()) {
                    sem_extraEff.setC((d + sem_extraEff.getTd()) / r.getValueC());
                    sem_extraEff.setTd(0);
                } else {
                    sem_extraEff.setC(0);
                    sem_extraEff.setTd(d + sem_extraEff.getTd());
                }
            }
            if (sem_extraEff.getTd() < 0 && sem_extraEff.getTp() > 0) {
                double d = sem_extraEff.getTp() * r.getValueTP();
                if (d >= -sem_extraEff.getTd()) {
                    sem_extraEff.setTp((d + sem_extraEff.getTd()) / r.getValueTP());
                    sem_extraEff.setTd(0);
                } else {
                    sem_extraEff.setTp(0);
                    sem_extraEff.setTd(d + sem_extraEff.getTd());
                }
            }
            if (sem_extraEff.getTd() < 0 && sem_extraEff.getPm() > 0) {
                double d = sem_extraEff.getPm() * r.getValuePM();
                if (d >= -sem_extraEff.getTd()) {
                    sem_extraEff.setPm((d + sem_extraEff.getTd()) / r.getValuePM());
                    sem_extraEff.setTd(0);
                } else {
                    sem_extraEff.setPm(0);
                    sem_extraEff.setTd(d + sem_extraEff.getTd());
                }
            }
//            //exemple hsup C  =1
//            //exemple hsup td =-1
//            if(sem_extraEff.getTdAndCo()<0){
//                //exemple td0 = 1 (manque à combler)
//                double td0 = -sem_extraEff.getTdAndCo();
//                //exemple td2 = 1.83 = 1* 1.83/1 (manque à combler)
//                double td2 = sem_extraEff.getC()*r.getValueC();
//                if(td2>=td0){
//                    //exemple 1.83 >1
//                    //donc prendre 1 de 1.83 la rajouter au TD et le reste le retransformer en cours
//                    //soit td=-1+1=0  et c= ancien c - manque td vu en C c=1- (1/1.83)
//                    sem_extraEff.setC((td2-td0)/r.getValueC());
//                    sem_extraEff.setTdAndCo(0);
//                }else if(sem_extraEff.getC()>0){
//                    sem_extraEff.setTdAndCo(td2-td0);
//                    sem_extraEff.setC(0);
//                }
//            }
        }
        reevalLoadValueFormulas(sem_extraEff, r);
        return sem_extraEff;
    }

//    public TeacherPeriodStat evalTeacherStat(
//            int periodId,
//            int teacherId,
//            CourseAssignmentFilter filter,
//            boolean includeIntents,
//            DeviationConfig deviationConfig,
//            StatCache cache) {
//        AcademicTeacher teacher = cache.getAcademicTeacherMap().get(teacherId);
//        if (teacher == null) {
//            return null;
//        }
//        return evalTeacherStat(periodId, teacherId, null, null, null, filter, includeIntents,deviationConfig,cache);
//    }

    public int getSemesterMaxWeeks() {
        return cacheService.get(AcademicTeacher.class).getProperty("getSemesterMaxWeeks", new Action<Integer>() {
            @Override
            public Integer run() {
                try {
                    return (Integer) core.getOrCreateAppPropertyValue("AcademicPlugin.SemesterMaxWeeks", null, DEFAULT_SEMESTER_MAX_WEEKS);
                } catch (Exception e) {
                    return DEFAULT_SEMESTER_MAX_WEEKS;
                }
            }
        });
    }

    public void generateTeacherAssignementDocumentsFolder(int periodId) {
        generateTeacherAssignementDocumentsFolder(periodId, "/Documents/Services/Supports Pedagogiques/Par Enseignant");
    }

    public void generateTeacherAssignementDocumentsFolder(int periodId, String path) {
        for (AcademicCourseAssignment a : findAcademicCourseAssignments(periodId)) {
            if (a.getTeacher() != null && a.getTeacher().getContact() != null) {
                String n = VrUtils.toValidFileName(a.getTeacher().getContact().getFullName());
                String c = VrUtils.toValidFileName(a.getFullName());
                VFile r = core.getFileSystem().get(path + "/" + n + "/" + c);
                r.mkdirs();
            }
        }
    }

    public TeacherPeriodStat evalTeacherStat(
            int periodId,
            int teacherId,
            CourseAssignmentFilter courseAssignmentFilter,
            DeviationConfig deviationConfig,
            ProgressMonitor mon
    ) {
//        TeacherPeriodStat teacherPeriodStat = evalTeacherStat0(periodId,
//                teacherId,
//                teacher,
//                findTeacherSemestrialLoads,
//                modules,
//                filter,
//                includeIntents,
//                deviationConfig,
//                cache);
        EnhancedProgressMonitor monitor = ProgressMonitorFactory.enhance(mon);

        MapList<Integer, TeacherPeriodStat> all = evalTeacherStatList(periodId, null, courseAssignmentFilter, deviationConfig, monitor);
        TeacherPeriodStat teacherPeriodStat = all.getByKey(teacherId);
        if (teacherPeriodStat != null) {
            return teacherPeriodStat;
        }
        //ghost Teacher (with no load)
        TeacherPeriodStat ghost = new TeacherPeriodStat();
        AcademicTeacher teacher = findTeacher(teacherId);
        ghost.setTeacher(teacher);
        ghost.setConfig(deviationConfig);
        List<AcademicSemester> semesters = findSemesters();
        List<TeacherSemesterStat> teacherSemesterStats = new ArrayList<>();
        TeacherPeriodStat teacherStat = new TeacherPeriodStat();
        teacherStat.setConfig(deviationConfig);
        for (AcademicSemester semester : semesters) {
            TeacherSemesterStat e = new TeacherSemesterStat();
            e.setAssignments(new ArrayList<>());
            e.setSemester(semester);
            e.setTeacherStat(teacherStat);
            //TODO
            teacherSemesterStats.add(e);
        }
        ghost.setSemesters(teacherSemesterStats.toArray(new TeacherSemesterStat[teacherSemesterStats.size()]));
        ghost.setTeacherPeriod(findAcademicTeacherPeriod(periodId, ghost.getTeacher()));
        ghost.setPopulation(new TeacherValuePopulation(
                teacher.getSituation(),
                teacher.getDegree(),
                teacher.getOfficialDiscipline()
        ));
        ghost.setCourseAssignmentFilter(courseAssignmentFilter);
        ghost.setAssignments(new ArrayList<>());
        return ghost;
//        copyDeviationFrom(teacherPeriodStat,all);
//        for (TeacherSemesterStat teacherSemesterStat : teacherPeriodStat.getSemesters()) {
//            List<TeacherSemesterStat> ss = evalTeacherSemesterStatList(periodId,
//                    teacherSemesterStat.getSemester().getId(),
//                    null, filter, includeIntents, deviationConfig, cache
//            );
//            copyDeviationFrom(teacherSemesterStat,ss);
//        }
//        return teacherPeriodStat;
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
        Chronometer ch = new Chronometer();
        if (teacher == null) {
            teacher = findTeacher(teacherId);
        }
        if (teacher == null) {
            return null;
        }
//        int teacherId = tal.getTeacher().getId();
        if (assignments == null) {
            assignments = findCourseAssignmentsAndIntents(periodId, teacherId, filter);
            if (assignments == null) {
                log.severe("No assignments found for teacherId=" + teacherId + " (" + teacher + ")");
                assignments = new ArrayList<>();
            }
        }
        TeacherPeriodStat teacher_stat = new TeacherPeriodStat();
        teacher_stat.setAssignments(assignments);
        teacher_stat.setConfig(deviationConfig);
        teacher_stat.setCourseAssignmentFilter(filter);
        teacher_stat.setTeacher(teacher);
        AcademicTeacherSituation situation = teacher.getSituation();
        teacher_stat.setTeacherPeriod(findAcademicTeacherPeriod(periodId, teacher_stat.getTeacher()));
        AcademicTeacherDegree degree = teacher_stat.getTeacherPeriod().getDegree();
        teacher_stat.getDueWeek().setEquiv(teacherHasDue(situation, degree) ? degree.getValueDU(): 0);

        boolean hasDU = teacher_stat.getDueWeek().getEquiv() > 0;
        if (!hasDU) {
            teacher_stat.getDueWeek().setEquiv(0);
            teacher_stat.getDueWeek().setEquivC(0);
            teacher_stat.getDueWeek().setEquivTD(0);
            teacher_stat.getDue().setEquiv(0);
            teacher_stat.getDue().setEquivC(0);
            teacher_stat.getDue().setEquivTD(0);
        }
//        StatCache.PeriodCache periodCache = cache.forPeriod(periodId);
        if (findTeacherSemestrialLoads == null) {
            findTeacherSemestrialLoads = findTeacherSemestrialLoadsByTeacher(periodId, teacherId);
            if (findTeacherSemestrialLoads == null) {
                log.severe("teacherSemestrialLoads not found for teacherId=" + teacherId + " (" + teacher + ")");
                findTeacherSemestrialLoads = new ArrayList<>();
            }
        }

        teacher_stat.setSemestrialLoad(findTeacherSemestrialLoads.toArray(new AcademicTeacherSemestrialLoad[findTeacherSemestrialLoads.size()]));
        List<AcademicSemester> semesters = findSemesters();
        TeacherSemesterStat[] sems = new TeacherSemesterStat[semesters.size()];
        teacher_stat.setSemesters(sems);
        double sum_semester_weeks = 0;
        double sum_max_semester_weeks = 0;
        LoadValue teacher_extraWeek = teacher_stat.getExtraWeek();
        LoadValue teacher_extra = teacher_stat.getExtra();
        int maxWeeks = getSemesterMaxWeeks();
        AcademicConversionTableHelper conversionTable = findConversionTableByPeriodId(periodId);
        AcademicTeacherDegree td = teacher_stat.getTeacherPeriod().getDegree();
        if (td == null) {
            td = new AcademicTeacherDegree();
        }
        AcademicLoadConversionRow r = conversionTable.get(td.getConversionRule().getId());
        if (r == null) {
            r = new AcademicLoadConversionRow();
            r.setValueC(0);
            r.setValueTD(0);
            r.setValueTP(0);
            r.setValuePM(0);
        }

        for (int i = 0; i < sems.length; i++) {
            AcademicSemester ss = semesters.get(i);
            TeacherSemesterStat sem = new TeacherSemesterStat();
            sem.setConfig(deviationConfig);
            sem.setTeacherStat(teacher_stat);
            sem.setSemester(ss);
            int semesterWeeks = findTeacherSemestrialLoads.size() > i ? (findTeacherSemestrialLoads.get(i).getWeeksLoad()) : 0;
            sem.setWeeks(semesterWeeks);
            sem.setMaxWeeks(maxWeeks);
            LoadValue sem_value = sem.getValue();
            LoadValue sem_due = sem.getDue();
            LoadValue sem_dueWeek = sem.getDueWeek();
            LoadValue sem_extra = sem.getExtra();
            LoadValue sem_valueWeek = sem.getValueWeek();
            LoadValue sem_extraWeek = sem.getExtraWeek();

            sum_semester_weeks += sem.getWeeks();
            sum_max_semester_weeks += sem.getMaxWeeks();
            List<AcademicCourseAssignmentInfo> semesterAssignments = new ArrayList<>();
            for (AcademicCourseAssignmentInfo academicCourseAssignment : assignments) {
                ModuleStat ms = evalModuleStat(periodId, academicCourseAssignment.getAssignment().getId(), teacherId);
                ModuleSemesterStat mss = ms.getSemester(ss.getName());
                sem_value.add(mss.getValue());
                if (academicCourseAssignment.resolveSemester().getId() == ss.getId()) {
                    semesterAssignments.add(academicCourseAssignment);
                    if (academicCourseAssignment.getAssignment().isConfirmedTeacher()) {
                        LoadValue v = getAssignmentLoadValue(academicCourseAssignment.getAssignment(), degree, conversionTable);
                        sem.getConfirmedTeacherAssignment().add(v);
                        sem.setConfirmedTeacherAssignmentCount(sem.getConfirmedTeacherAssignmentCount() + 1);
                        teacher_stat.getConfirmedTeacherAssignment().add(v);
                        teacher_stat.setConfirmedTeacherAssignmentCount(teacher_stat.getConfirmedTeacherAssignmentCount() + 1);
                    }
                }
            }
            reevalLoadValueFormulas(sem_value, r);
            double ruleValueTD = r.getValueTD();
            double tpToTd = ruleValueTD == 0 ? 0 : r.getValueTP() / ruleValueTD;
            double pmToTd = ruleValueTD == 0 ? 0 : r.getValuePM() / ruleValueTD;
            sem.setAssignments(semesterAssignments);
            if (semesterWeeks == 0) {
                LoadValue zeros = new LoadValue();
                sem_valueWeek.set(zeros);
                sem_extraWeek.set(zeros);
                sem_value.setTppm(0);
                sem_valueWeek.setTppm(0);
                sem_due.setEquiv(0);
            } else {
                sem_valueWeek.set(sem_value.copy().div(semesterWeeks));
                sem_valueWeek.setTppm(sem_value.getTppm() / semesterWeeks);
                sem_valueWeek.setTdtppm(sem_value.getTdtppm() / semesterWeeks);

                if (r.getValueC() == 1) {
                    sem_dueWeek.setC(td.getValueDU());
                    sem_due.setC(td.getValueDU() * semesterWeeks);
                } else if (ruleValueTD == 1) {
                    sem_dueWeek.setTd(td.getValueDU());
                    sem_due.setTd(td.getValueDU() * semesterWeeks);
                } else if (r.getValueTP() == 1) {
                    sem_dueWeek.setTp(td.getValueDU());
                    sem_due.setTp(td.getValueDU() * semesterWeeks);
                } else if (r.getValuePM() == 1) {
                    sem_dueWeek.setPm(td.getValueDU());
                    sem_due.setPm(td.getValueDU() * semesterWeeks);
                }
                if (hasDU) {
                    sem_extra.setEquiv(sem_value.getEquiv() - td.getValueDU() * semesterWeeks);
                    sem_extra.setC(sem_value.getC() - sem_due.getC());
                    sem_extra.setTd(sem_value.getTd() - sem_due.getTd());
                    sem_extra.setTp(sem_value.getTp() - sem_due.getTp());
                    sem_extra.setPm(sem_value.getPm() - sem_due.getPm());

                    sem_extraWeek.setEquiv(sem_valueWeek.getEquiv() - td.getValueDU() * (semesterWeeks / sem.getMaxWeeks()));
                    sem_extraWeek.setC(sem_valueWeek.getC() - sem_dueWeek.getC());
                    sem_extraWeek.setTd(sem_valueWeek.getTd() - sem_dueWeek.getTd());
                    sem_extraWeek.setTp(sem_valueWeek.getTp() - sem_dueWeek.getTp());
                    sem_extraWeek.setPm(sem_valueWeek.getPm() - sem_dueWeek.getPm());
                }
//                sem_value.setTppm(sem_value.getTp() + sem_value.getPm() * (r.getValuePM() / r.getValueTP()));

                teacher_stat.getValue().add(sem_value);
                sem_dueWeek.setEquiv(td.getValueDU());
                sem_due.setEquiv(td.getValueDU() * semesterWeeks);

                if (hasDU) {
                    sem.getExtraEff().set(evalHeureSupp(sem_extra, r));

                    LoadValue sem_extraWeekEff = sem.getExtraWeekEff();
                    sem_extraWeekEff.set(sem.getExtraEff().copy().div(semesterWeeks));
                    teacher_stat.getExtraWeekEff().add(sem_extraWeekEff);

                    teacher_extra.add(sem_extraWeek.copy().mul(semesterWeeks));
                }
            }
            sem_value.setEquiv(evalValueEquiv(sem_value, degree, conversionTable));
            sems[i] = sem;
        }
        if (sum_semester_weeks == 0) {
            teacher_stat.getValue().setEquiv(0);
            teacher_stat.getValueWeek().set(new LoadValue());
            teacher_stat.getDue().set(new LoadValue());
            teacher_stat.getDueWeek().set(new LoadValue());
            teacher_extraWeek.set(new LoadValue());
            teacher_extra.set(new LoadValue());
        } else {
            teacher_stat.getValue().setEquiv(evalValueEquiv(teacher_stat.getValue(), degree, conversionTable));
            teacher_stat.getValueWeek().set(teacher_stat.getValue().copy().div(sum_semester_weeks));
            if (hasDU) {
                teacher_stat.getDue().set(teacher_stat.getDueWeek().copy().mul(sum_semester_weeks));
                teacher_extraWeek.set(teacher_extra.copy().div(sum_semester_weeks));
                teacher_stat.getExtraEff().set(teacher_stat.getExtraEff().copy().div(sum_semester_weeks));
                teacher_stat.getExtraWeekEff().set(teacher_stat.getExtraWeekEff().copy().div(sum_semester_weeks));
            }
        }
        if (hasDU) {
//            teacher_extraWeek.setEquiv(teacher_stat.getValueWeek().getEquiv() - teacher_stat.getDueWeek().getEquiv());
//            teacher_extra.setEquiv(teacher_stat.getValue().getEquiv() - teacher_stat.getDueWeek().getEquiv() * sum_semester_weeks);
            AcademicLoadConversionRow cr = conversionTable.get(degree.getConversionRule().getId());
            if (cr.getValueC() == 1) {
                //teacher_extraWeek.setC(teacher_extraWeek.getEquiv());
                //teacher_extra.setC(teacher_extra.getEquiv());
                teacher_stat.getDueWeek().setC(teacher_stat.getDueWeek().getEquiv());
                teacher_stat.getDue().setC(teacher_stat.getDue().getEquiv());
                for (TeacherSemesterStat sem : sems) {
                    //sem.getExtraWeek().setC(sem.getExtraWeek().getEquiv());
                }
            } else if (cr.getValueTD() == 1) {
                //teacher_extraWeek.setTd(teacher_extraWeek.getEquiv());
                //teacher_extra.setTd(teacher_extra.getEquiv());
                teacher_stat.getDueWeek().setTd(teacher_stat.getDueWeek().getEquiv());
                teacher_stat.getDue().setTd(teacher_stat.getDue().getEquiv());
                for (TeacherSemesterStat sem : sems) {
                    //sem.getExtraWeek().setTd(sem.getExtraWeek().getEquiv());
                }
            } else if (cr.getValueTP() == 1) {
                //teacher_extraWeek.setTp(teacher_extraWeek.getEquiv());
                //teacher_extra.setTp(teacher_extra.getEquiv());
                teacher_stat.getDueWeek().setTp(teacher_stat.getDueWeek().getEquiv());
                teacher_stat.getDue().setTp(teacher_stat.getDue().getEquiv());
                for (TeacherSemesterStat sem : sems) {
                    //sem.getExtraWeek().setTp(sem.getExtraWeek().getEquiv());
                }
            } else {
                //teacher_extraWeek.setTd(teacher_extraWeek.getEquiv());
                //teacher_extra.setTd(teacher_extra.getEquiv());
                teacher_stat.getDueWeek().setTd(teacher_stat.getDueWeek().getEquiv());
                teacher_stat.getDue().setTd(teacher_stat.getDue().getEquiv());
            }
            teacher_stat.getExtraEff().set(evalHeureSupp(teacher_stat.getExtra(), r));
            teacher_stat.getExtraWeekEff().set(evalHeureSupp(teacher_stat.getExtraWeek(), r));
        } else {
            teacher_extraWeek.set(new LoadValue());
            teacher_extra.set(new LoadValue());
        }
        teacher_stat.setWeeks(sum_semester_weeks);
        teacher_stat.setMaxWeeks(sum_max_semester_weeks);
        log.log(Level.FINE, "evalTeacherStat {0} in {1}", new Object[]{getValidName(teacher), ch.stop()});

        return teacher_stat;
    }

    private boolean teacherHasDue(AcademicTeacherSituation situation, AcademicTeacherDegree degree) {
        return degree != null &&
                degree.getValueDU() >0 &&
                situation!=null &&
                situation.getType()!=null &&
                        situation.getType().isWithDue();
    }

    public LoadValue getAssignmentLoadValue(AcademicCourseAssignment assignment, AcademicTeacherDegree degree, AcademicConversionTableHelper conversionTable) {
        LoadValue loadValue = new LoadValue(assignment.getValueC(), assignment.getValueTD(), assignment.getValueTP(), assignment.getValuePM());
        double equiv = evalValueEquiv(loadValue, degree, conversionTable);
        loadValue = loadValue.setEquiv(equiv);
        AcademicLoadConversionRow r = conversionTable.get(degree.getConversionRule().getId());
        if (r.getValueC() == 1) {
            loadValue.setEquivC(loadValue.getEquiv());
        } else if (r.getValueTD() == 1) {
            loadValue.setEquivTD(loadValue.getEquiv());
        }
        return loadValue;
    }

    public ModuleStat evalModuleStat(int periodId, int courseAssignmentId, Integer forTeacherId) {
//        if (cache == null) {
//            cache = new StatCache();
//        }
//        StatCache.PeriodCache periodCache = cache.forPeriod(periodId);
        MapList<Integer, AcademicCourseAssignment> courseAssignments = findCourseAssignments(periodId);
        AcademicCourseAssignment module = courseAssignments.getByKey(courseAssignmentId);
        LoadValue mod_val = new LoadValue(module.getValueC(), module.getValueTD(), module.getValueTP(), module.getValuePM());
        ModuleStat ms = new ModuleStat();
        ms.setModule(module);
        List<AcademicSemester> semesters = findSemesters();
        ModuleSemesterStat[] sems = new ModuleSemesterStat[semesters.size()];
        ms.setSemesters(sems);
        AcademicTeacher teacher = null;
        if (forTeacherId != null) {
            teacher = findTeacher(forTeacherId);
            if (teacher == null) {
                throw new IllegalArgumentException("Teacher " + forTeacherId + " not found");
            }
        } else {
            teacher = module.getTeacher();
        }
        if (teacher == null) {
            for (int i = 0; i < semesters.size(); i++) {
                AcademicSemester ss = semesters.get(i);
                ModuleSemesterStat s = new ModuleSemesterStat();
                s.setSemester(ss);
                sems[i] = s;
            }
        } else {
            AcademicTeacherPeriod trs = findAcademicTeacherPeriod(periodId, teacher);
            AcademicConversionTableHelper conversionTableByPeriodId = findConversionTableByPeriodId(periodId);

            for (int i = 0; i < semesters.size(); i++) {
                AcademicSemester ss = semesters.get(i);
                ModuleSemesterStat s = new ModuleSemesterStat();
                s.setSemester(ss);
                if (module.resolveSemester().getName().equals(ss.getName())) {
                    s.getValue().set(mod_val.copy().mul(module.getGroupCount() * module.getShareCount()));
                    s.setValueEffWeek(module.getValueEffWeek() * module.getGroupCount() * module.getShareCount());
                    s.getValue().setEquiv(evalValueEquiv(s.getValue(), trs.getDegree(), conversionTableByPeriodId));
                    ms.getValue().set(s.getValue());
                    ms.setValueEffWeek(s.getValueEffWeek());

                } else {
                    //all zeros
                }
                sems[i] = s;
            }
        }
        return ms;
    }

    public double evalValueEquiv(LoadValue value, String degree, AcademicConversionTableHelper table) {
        AcademicTeacherDegree dd = findTeacherDegree(degree);
        return evalValueEquiv(value, dd, table);
    }

//    public void updateEquivCandTP(int periodId, LoadValue v, StatCache cache) {
//        AcademicConversionTableHelper conversionTable = cache.forPeriod(periodId).getConversionTable();
//        v.setEquivC(evalValueEquiv(v, cache.getAcademicTeacherDegreesByCodeMap().get("P"), conversionTable));
//        v.setEquivTD(evalValueEquiv(v, cache.getAcademicTeacherDegreesByCodeMap().get("MA"), conversionTable));
//    }

    public double evalValueEquiv(LoadValue v, AcademicTeacherDegree dd, AcademicConversionTableHelper table) {
        if (dd == null) {
            return 0;
        }
        AcademicLoadConversionRow r = table.get(dd.getConversionRule().getId());
        return r.getValueC() * v.getC()
                + r.getValueTD() * v.getTd()
                + r.getValueTP() * v.getTp()
                + r.getValuePM() * v.getPm();
    }

    public LoadValue evalValueEquiv(double v, AcademicTeacherDegree dd, AcademicConversionTableHelper table) {
        if (dd == null) {
            return new LoadValue();
        }
        AcademicLoadConversionRow r = table.get(dd.getConversionRule().getId());
        LoadValue loadValue = new LoadValue(
                r.getValueC() * v,
                r.getValueTD() * v,
                r.getValueTP() * v,
                r.getValuePM() * v
        ).setEquiv(v);
        if (r.getValueC() == 1) {
            loadValue.setEquivC(loadValue.getEquiv());
        } else if (r.getValueTD() == 1) {
            loadValue.setEquivTD(loadValue.getEquiv());
        }

        loadValue.setTppm(loadValue.getTp() + loadValue.getPm() * (r.getValuePM() / r.getValueTP()));
        return loadValue;
    }

    public AcademicTeacher findTeacherByUser(Integer userId) {
        return cacheService.get(AcademicTeacher.class).getProperty("findTeacherByUser", new Action<Map<Integer, AcademicTeacher>>() {
            @Override
            public Map<Integer, AcademicTeacher> run() {
                Map<Integer, AcademicTeacher> map = new HashMap<Integer, AcademicTeacher>();
                for (Object o : cacheService.get(AcademicTeacher.class).getValues()) {
                    AcademicTeacher t = (AcademicTeacher) o;
                    if (t.getUser() != null) {
                        map.put(t.getUser().getId(), t);
                    }
                }
                return map;
            }
        }).get(userId);
//        return UPA.getPersistenceUnit().createQuery("Select u from AcademicTeacher u where u.userId=:userId")
//                .setParameter("userId", userId)
//                .getFirstResultOrNull();
    }

    public AcademicStudent findStudentByUser(Integer userId) {
        return cacheService.get(AcademicStudent.class).getProperty("findStudentByUser", new Action<Map<Integer, AcademicStudent>>() {
            @Override
            public Map<Integer, AcademicStudent> run() {
                Map<Integer, AcademicStudent> map = new HashMap<Integer, AcademicStudent>();
                for (Object o : cacheService.get(AcademicStudent.class).getValues()) {
                    AcademicStudent t = (AcademicStudent) o;
                    if (t.getUser() != null) {
                        map.put(t.getUser().getId(), t);
                    }
                }
                return map;
            }
        }).get(userId);

//        return UPA.getPersistenceUnit().createQuery("Select u from AcademicStudent u where u.userId=:userId")
//                .setParameter("userId", userId)
//                .getFirstResultOrNull();
    }

    public AcademicTeacher findTeacherByContact(Integer contacId) {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicTeacher u where u.contactId=:contacId")
                .setParameter("contacId", contacId)
                .getFirstResultOrNull();
    }

    public AcademicStudent findStudentByContact(Integer contacId) {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicStudent u where u.contactId=:contacId")
                .setParameter("contacId", contacId)
                .getFirstResultOrNull();
    }

    //    public List<AcademicCourseAssignment> findCourseAssignments(Integer teacher, String semester, StatCache cache) {
//        List<AcademicCourseAssignment> m = new ArrayList<>();
//            for (AcademicCourseAssignment value : cache.getAcademicCourseAssignmentsByTeacherAndSemester(teacher, semester)) {
//                if (teacher == null || (value.getTeacher() != null && value.getTeacher().getId() == (teacher))) {
//                    if (semester == null || (value.getCoursePlan().getSemester() != null && value.getCoursePlan().getSemester().getName().equals(semester))) {
//                        m.add(value);
//                    }
//                }
//            }
//        return m;
//    }


    public void dupCourseAssignment(int assignmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = pu.findById(AcademicCourseAssignment.class, assignmentId);
        String d = a.getDiscriminator();
        if (StringUtils.isEmpty(d)) {
            d = "";
        } else {
            d = d + "-";
        }
        a.setDiscriminator(d + "1");
        pu.merge(a);

        a.setId(0);
        a.setDiscriminator(d + "2");
        pu.persist(a);
    }

    public void splitGroupCourseAssignment(int assignmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = pu.findById(AcademicCourseAssignment.class, assignmentId);
        if (a != null && a.getGroupCount() > 0) {
            String d = a.getDiscriminator();
            if (StringUtils.isEmpty(d)) {
                d = "";
            } else {
                d = d + "-";
            }
            double g = a.getGroupCount();
            a.setGroupCount(g / 2.0);
            a.setDiscriminator(d + "G1");
            pu.merge(a);

            //create a perfect copy!
            a.setId(0);
            a.setDiscriminator(d + "G2");
            pu.persist(a);
        }
    }

    public void splitShareCourseAssignment(int assignmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = pu.findById(AcademicCourseAssignment.class, assignmentId);
        if (a != null && a.getShareCount() > 0) {
            String d = a.getDiscriminator();
            if (StringUtils.isEmpty(d)) {
                d = "";
            } else {
                d = d + "-";
            }
            double g = a.getShareCount();
            a.setShareCount(g / 2.0);
            a.setDiscriminator(d + "SH1");
            pu.merge(a);

            //create a perfect copy!
            a.setId(0);
            a.setDiscriminator(d + "SH2");
            pu.persist(a);
        }
    }

    public void addCourseAssignment(AcademicCourseAssignment assignment) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.persist(assignment);
    }

    public void updateCourseAssignment(AcademicCourseAssignment assignment) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.merge(assignment);
    }

    public void addCourseAssignment(int teacherId, int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = pu.findById(AcademicCourseAssignment.class, assignementId);
        a.setTeacher(findTeacher(teacherId));
        pu.merge(a);
    }

    public void removeCourseAssignment(int assignementId,boolean hardRemoval) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = pu.findById(AcademicCourseAssignment.class, assignementId);
        if(a!=null) {
            if (hardRemoval) {
                pu.remove(a);
            } else {
                a.setTeacher(null);
                pu.merge(a);
            }
        }
//        cacheService.get(AcademicCourseAssignment.class).invalidate();
    }

    public void addIntent(int teacherId, int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseIntent i = pu.createQuery("Select a from AcademicCourseIntent a where a.teacherId=:teacherId and a.assignmentId=:assignementId")
                .setParameter("teacherId", teacherId)
                .setParameter("assignementId", assignementId)
                .getFirstResultOrNull();
        if (i == null) {
            i = new AcademicCourseIntent();
            i.setTeacher(findTeacher(teacherId));
            i.setAssignment((AcademicCourseAssignment) pu.findById(AcademicCourseAssignment.class, assignementId));
            if (i.getTeacher() == null || i.getAssignment() == null) {
                throw new RuntimeException("Error");
            }
            pu.persist(i);
        }
    }

    public void removeIntent(int teacherId, int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseIntent i = pu.createQuery("Select a from AcademicCourseIntent a where a.teacherId=:teacherId and a.assignmentId=:assignementId")
                .setParameter("teacherId", teacherId)
                .setParameter("assignementId", assignementId)
                .getFirstResultOrNull();
        if (i != null) {
            pu.remove(i);
        }
    }

    public void removeAllIntents(int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicCourseIntent> intentList = pu.createQuery("Select a from AcademicCourseIntent a where a.assignmentId=:assignementId")
                .setParameter("assignementId", assignementId)
                .getResultList();
        for (AcademicCourseIntent ii : intentList) {
            pu.remove(ii);
        }
    }

    public AcademicCourseAssignment findAcademicCourseAssignment(int assignmentId) {
        return UPA.getPersistenceUnit().findById(AcademicCourseAssignment.class, assignmentId);
    }

    public List<AcademicCourseAssignment> findAcademicCourseAssignments(int periodId) {
//        cacheService.get(AcademicCourseAssignment.class).invalidate();
        return cacheService.get(AcademicCourseAssignment.class).getProperty("findAcademicCourseAssignments:" + periodId, new Action<List<AcademicCourseAssignment>>() {
            @Override
            public List<AcademicCourseAssignment> run() {
                List<AcademicCourseAssignment> list = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where a.coursePlan.periodId=:periodId")
                        .setParameter("periodId", periodId)
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                        .getResultList();
                return list;
            }
        });
    }

    public List<AcademicCourseIntent> findCourseIntentsByAssignment(int periodId, int assignment, Integer semester, CourseAssignmentFilter filter) {
        return getAcademicCourseIntentByAssignmentAndSemester(periodId, assignment, semester);
//        List<AcademicCourseIntent> intents = null;
//        intents = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a where a.assignmentId=:assignment")
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
//                .setParameter("assignment", assignment)
//                .getResultList();
//        List<AcademicCourseIntent> m = new ArrayList<>();
//        for (AcademicCourseIntent value : intents) {
//            AcademicSemester semester1 = value.getAssignment().getCoursePlan().getCourseLevel().getSemester();
//            if (semester == null || (semester1 != null
//                    && semester1.getId() == (semester))) {
//                if (filter == null || filter.acceptAssignment(value.getAssignment())) {
//                    m.add(value);
//                }
//            }
//        }
//        return m;
    }

    public List<AcademicCourseIntent> findCourseIntentsByAssignment(int assignment) {
        List<AcademicCourseIntent> intents = null;
        intents = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a where a.assignmentId=:assignment")
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                .setParameter("assignment", assignment)
                .getResultList();
        return intents;
    }


    public List<AcademicCourseIntent> findCourseIntentsByTeacher(int periodId, Integer teacher, CourseAssignmentFilter filter) {
        List<AcademicCourseIntent> intents = null;
        //if (cache != null) {
        intents = getAcademicCourseIntentByTeacherAndSemester(periodId, teacher, new CourseIntentFilter() {
            @Override
            public boolean acceptIntent(AcademicCourseIntent academicCourseIntent) {
                return filter.acceptAssignment(academicCourseIntent.getAssignment());
            }
        });
//        } else {
//            if (teacher == null) {
//                intents = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a where a.assignment.coursePlan.periodId=:periodId")
//                        .setParameter("periodId", periodId)
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
//                        .getResultList();
//            } else {
//                intents = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a where a.assignment.coursePlan.periodId=:periodId and a.teacherId=:teacherId")
//                        .setParameter("periodId", periodId)
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
//                        .setParameter("teacherId", teacher)
//                        .getResultList();
//            }
//        }
        List<AcademicCourseIntent> m = new ArrayList<>();
        for (AcademicCourseIntent value : intents) {
            if (filter == null || filter.acceptAssignment(value.getAssignment())) {
                m.add(value);
            }
        }
        return m;
    }

    private List<AcademicCourseAssignment> filterAssignments(List<AcademicCourseAssignment> base, CourseAssignmentFilter filter) {
        if (filter == null) {
            return base;
        }
        List<AcademicCourseAssignment> ret = new ArrayList<>();
        for (AcademicCourseAssignment academicCourseAssignment : base) {
            if (filter.acceptAssignment(academicCourseAssignment)) {
                ret.add(academicCourseAssignment);
            }
        }
        return ret;
    }


    public List<AcademicCourseAssignment> findCourseAssignments(int periodId, Integer teacher, CourseAssignmentFilter filter) {
        List<AcademicCourseAssignment> base = null;
        if (filter == null || filter.lookupIntents()) {
            List<AcademicCourseAssignment> all = new ArrayList<>();
            for (AcademicCourseAssignmentInfo i : findCourseAssignmentsAndIntents(periodId, teacher, filter)) {
                all.add(i.getAssignment());
            }
            base = all;
        } else {
            List<AcademicCourseAssignment> m = new ArrayList<>();
            if (teacher == null) {
                for (AcademicCourseAssignment value : findAcademicCourseAssignments(periodId)) {
                    if (filter.acceptAssignment(value)) {
                        m.add(value);
                    }
                }
            } else {
                List<AcademicCourseAssignment> list = findAcademicCourseAssignmentListByTeacherId(periodId).get(teacher);
                AcademicTeacher tt = findTeacher(teacher);
                AcademicTeacherPeriod tp = tt == null ? null : findAcademicTeacherPeriod(periodId, tt);
                if (list == null) {
                    if (tt != null) {
                        if (!tp.isEnabled()) {
                            //this is okkay!
                        } else {
                            System.out.println("No assignments for teacherId=" + teacher + " : " + tt);
                        }
                    } else {
                        System.out.println("No assignments for teacherId=" + teacher + " : " + tt);
                    }
                } else {
                    if (tt != null && !tp.isEnabled()) {
                        System.out.println("Found assignments for teacherId=" + teacher + " : " + tt + " but he/she seems to be not enabled!");
                    }
                    for (AcademicCourseAssignment value : list) {
                        AcademicSemester semester1 = value.resolveSemester();
                        if (filter == null || filter.acceptAssignment(value)) {
                            m.add(value);
                        }
                    }
                }
            }
            return m;
        }
        return base;
    }

    public List<AcademicCourseAssignmentInfo> findCourseAssignmentsAndIntents(int periodId, Integer teacher, CourseAssignmentFilter filter) {
        if (filter != null && !filter.lookupIntents()) {
            List<AcademicCourseAssignmentInfo> m = new ArrayList<>();
            if (teacher == null) {
                for (AcademicCourseAssignment value : findAcademicCourseAssignments(periodId)) {
                    AcademicSemester semester1 = value.resolveSemester();
                    if (filter == null || filter.acceptAssignment(value)) {
                        AcademicCourseAssignmentInfo b = new AcademicCourseAssignmentInfo();
                        b.setAssigned(value.getTeacher() != null);
                        b.setAssignment(value);
                        m.add(b);
                    }
                }
            } else {
                List<AcademicCourseAssignment> list = findAcademicCourseAssignmentListByTeacherId(periodId).get(teacher);
                AcademicTeacher tt = findTeacher(teacher);
                AcademicTeacherPeriod tp = tt == null ? null : findAcademicTeacherPeriod(periodId, tt);
                if (list == null) {
                    if (tt != null) {
                        if (!tp.isEnabled()) {
                            //this is okkay!
                        } else {
                            System.out.println("No assignments for teacherId=" + teacher + " : " + tt);
                        }
                    } else {
                        System.out.println("No assignments for teacherId=" + teacher + " : " + tt);
                    }
                } else {
                    if (tt != null && !tp.isEnabled()) {
                        System.out.println("Found assignments for teacherId=" + teacher + " : " + tt + " but he/she seems to be not enabled!");
                    }
                    for (AcademicCourseAssignment value : list) {
                        AcademicSemester semester1 = value.resolveSemester();
                        if (filter == null || filter.acceptAssignment(value)) {
                            AcademicCourseAssignmentInfo b = new AcademicCourseAssignmentInfo();
                            b.setAssigned(value.getTeacher() != null);
                            b.setAssignment(value);
                            m.add(b);
                        }
                    }
                }
            }
            return m;
        }
        List<AcademicCourseAssignmentInfo> all = new ArrayList<>();
        HashSet<Integer> visited = new HashSet<>();
        for (AcademicCourseAssignment a : findCourseAssignments(periodId, teacher, new CourseAssignmentFilterAnd().and(filter).and(new DefaultCourseAssignmentFilter().setAcceptIntents(false)))) {
            if (!visited.contains(a.getId())) {
                visited.add(a.getId());
                AcademicCourseAssignmentInfo b = new AcademicCourseAssignmentInfo();
                b.setAssigned(a.getTeacher() != null);
                b.setAssignment(a);
                all.add(b);
            }
        }
        Map<Integer, List<AcademicCourseIntent>> intentsByAssignment = new HashMap<>();
        for (AcademicCourseIntent a : findCourseIntentsByTeacher(periodId, teacher, filter)) {
            int assId = a.getAssignment().getId();
            List<AcademicCourseIntent> other = intentsByAssignment.get(assId);
            if (other == null) {
                other = new ArrayList<>();
                intentsByAssignment.put(assId, other);
            }
            other.add(a);
            if (!visited.contains(assId)) {
                visited.add(assId);
                AcademicCourseAssignmentInfo b = new AcademicCourseAssignmentInfo();
                b.setAssigned(false);
                b.setAssignment(a.getAssignment());
                all.add(b);
            }
        }
        for (AcademicCourseAssignmentInfo a : all) {
            List<AcademicCourseIntent> intentObjects = intentsByAssignment.get(a.getAssignment().getId());
//            TreeSet<String> allIntents = new TreeSet<>();
//            TreeSet<Integer> allIntentIds = new TreeSet<>();
            if (intentObjects != null) {
                for (AcademicCourseIntent b1 : intentObjects) {
//                    if (teacher == null || (teacher.intValue() != b1.getTeacher().getId())) {
                    String n = getValidName(b1.getTeacher());
                    Map<Integer, TeacherAssignmentChunck> chuncks = a.getAssignmentChunck().getChuncks();
                    TeacherAssignmentChunck c = chuncks.get(b1.getTeacher().getId());
                    if (c == null) {
                        c = new TeacherAssignmentChunck(b1.getTeacher().getId(), n);
                        c.setIntended(true);
                        chuncks.put(b1.getTeacher().getId(), c);
                    } else {
                        c.setIntended(true);
                    }
//                    }
                }
            }
            if (a.getAssignment().getTeacher() != null) {
                AcademicTeacher teacher1 = a.getAssignment().getTeacher();
                String n = getValidName(teacher1);
                Map<Integer, TeacherAssignmentChunck> chuncks = a.getAssignmentChunck().getChuncks();
                TeacherAssignmentChunck c = chuncks.get(teacher1.getId());
                if (c == null) {
                    c = new TeacherAssignmentChunck(teacher1.getId(), n);
                    c.setAssigned(true);
                    chuncks.put(teacher1.getId(), c);
                } else {
                    c.setAssigned(true);
                }
            }

            AcademicCoursePlan coursePlan = a.getAssignment().getCoursePlan();
            List<AcademicCourseAssignment> other = coursePlan == null ? Collections.EMPTY_LIST : findCourseAssignmentsByPlan(coursePlan.getId());
            for (AcademicCourseAssignment academicCourseAssignment : other) {
                AcademicTeacher teacher1 = academicCourseAssignment.getTeacher();
                if (teacher1 != null) {
                    String n = getValidName(teacher1);
                    if (academicCourseAssignment.getId() != a.getAssignment().getId()) {
                        Map<Integer, TeacherAssignmentChunck> achuncks = a.getAssignmentChunck().getChuncks();
                        if (!achuncks.containsKey(teacher1.getId())) {
                            Map<Integer, TeacherAssignmentChunck> chuncks = a.getCourseChunck().getChuncks();
                            TeacherAssignmentChunck c = chuncks.get(teacher1.getId());
                            if (c == null) {
                                c = new TeacherAssignmentChunck(teacher1.getId(), n);
                                c.setAssigned(true);
                                chuncks.put(teacher1.getId(), c);
                            } else {
                                c.setAssigned(true);
                            }
                        }
                    }
                }
            }
            List<AcademicCourseIntent> otherIntents = coursePlan == null ? Collections.EMPTY_LIST : findCourseIntentsByCoursePlan(coursePlan.getId());
            for (AcademicCourseIntent academicCourseAssignment : otherIntents) {
                AcademicTeacher teacher1 = academicCourseAssignment.getTeacher();
                if (teacher1 != null) {
                    String n = getValidName(teacher1);
                    if (academicCourseAssignment.getId() != a.getAssignment().getId()) {
                        Map<Integer, TeacherAssignmentChunck> achuncks = a.getAssignmentChunck().getChuncks();
                        if (!achuncks.containsKey(teacher1.getId())) {
                            Map<Integer, TeacherAssignmentChunck> chuncks = a.getCourseChunck().getChuncks();
                            TeacherAssignmentChunck c = chuncks.get(teacher1.getId());
                            if (c == null) {
                                c = new TeacherAssignmentChunck(teacher1.getId(), n);
                                c.setIntended(true);
                                chuncks.put(teacher1.getId(), c);
                            } else {
                                c.setIntended(true);
                            }
                        }
                    }
                }
            }

//            a.setIntentsSet(allIntents);
//            a.setIntents(sb.toString());
//            a.setIntentsTeacherIdsSet(allIntentIds);
        }
        Collections.sort(all, new Comparator<AcademicCourseAssignmentInfo>() {

            @Override
            public int compare(AcademicCourseAssignmentInfo o1, AcademicCourseAssignmentInfo o2) {
                AcademicCourseAssignment a1 = o1.getAssignment();
                AcademicCourseAssignment a2 = o2.getAssignment();
//                String s1 = a1.getCoursePlan().getName();
//                String s2 = a2.getCoursePlan().getName();
                String s1 = StringUtils.nonNull(a1.getFullName());
                String s2 = StringUtils.nonNull(a2.getFullName());
                return s1.compareTo(s2);
            }
        });
        return all;
    }


    public List<AcademicProgramType> findProgramTypes() {
        return UPA.getPersistenceUnit().findAll(AcademicProgramType.class);
    }

    public Set<String> findCoursePlanLabels(int periodId) {
        HashSet<String> labels = new HashSet<>();
        for (AcademicCoursePlan plan : findCoursePlans(periodId)) {
            labels.addAll(buildCoursePlanLabelsFromString(plan.getLabels()));
        }
        return labels;
    }

    public Set<String> buildCoursePlanLabelsFromString(String string) {
        HashSet<String> labels = new HashSet<>();
        if (string != null) {
            for (String s : string.split(",|;| |:")) {
                if (s.length() > 0) {
                    labels.add(s);
                }
            }
        }
        return labels;
    }

    public void copyDeviationFrom(TeacherSemesterStat one, List<TeacherSemesterStat> list) {
        for (TeacherSemesterStat semesterStat : list) {
            if (semesterStat.getTeacher().getId() == one.getTeacher().getId() && semesterStat.getSemester().getId() == one.getSemester().getId()) {
                one.setPopulation(semesterStat.getPopulation());
                break;
            }
        }
    }

    public void copyDeviationFrom(TeacherPeriodStat one, List<TeacherPeriodStat> list) {
        for (TeacherPeriodStat semesterStat : list) {
            if (semesterStat.getTeacher().getId() == one.getTeacher().getId()) {
                one.setPopulation(semesterStat.getPopulation());
                break;
            }
        }
    }

    private void evalTeacherSemesterStatListDeviation(List<TeacherSemesterStat> list, DeviationConfig deviationConfig, ProgressMonitor mon) {
        EnhancedProgressMonitor monitor = ProgressMonitorFactory.enhance(mon);
        Map<String, TeacherValuePopulation> lists = new HashMap<String, TeacherValuePopulation>();
        Set<DeviationGroup> groups = deviationConfig.getGroups();
        if (groups.size() == 0) {
            groups = DEFAULT_DEVIATION_GROUPS;
        }
        TeacherValuePopulation emptyPopulation = new TeacherValuePopulation(null, null, null);
        int i = 0;
        int max = list.size();
        for (TeacherSemesterStat stat : list) {
            monitor.setProgress(i++, max);
            if (stat.getValue().getEquiv() == 0) {
                stat.setPopulation(emptyPopulation);
                emptyPopulation.addValue(stat.getTeacher(), 0);
                //should ignore it?
            } else {
                AcademicTeacherSituation situation = stat.getTeacher().getSituation();
                AcademicTeacherDegree degree = stat.getTeacher().getDegree();
                AppDepartment department = stat.getTeacher().getDepartment();
                AcademicOfficialDiscipline discipline = stat.getTeacher().getOfficialDiscipline();

                String k = "";
                if (groups.contains(DeviationGroup.DEGREE)) {
                    k += ":" + (degree == null ? "" : String.valueOf(degree.getId()));
                }
                if (groups.contains(DeviationGroup.SITUATION)) {
                    k += ":" + (situation == null ? "" : String.valueOf(situation.getId()));
                }
                if (groups.contains(DeviationGroup.SITUATION_TYPE)) {
                    k += ":" + ((situation == null || situation.getType() == null) ? "" : String.valueOf(situation.getType()));
                }
                if (groups.contains(DeviationGroup.DISCIPLINE)) {
                    k += ":" + (discipline == null ? "" : String.valueOf(discipline.getId()));
                }
                if (groups.contains(DeviationGroup.DEPARTMENT)) {
                    k += ":" + (department == null ? "" : String.valueOf(department.getId()));
                }
                TeacherValuePopulation p = lists.get(k);
                if (p == null) {
                    p = new TeacherValuePopulation(situation, degree, discipline);
                    lists.put(k, p);
                }
                stat.setPopulation(p);
                p.addValue(stat.getTeacher(), stat.getDeviationBaseValue());
            }
        }
        for (TeacherValuePopulation p : lists.values()) {
            p.build();
        }
    }

    public List<TeacherSemesterStat> evalTeacherSemesterStatList(int periodId, Integer semesterId, TeacherFilter teacherFilter, CourseAssignmentFilter filter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        List<TeacherSemesterStat> all = new ArrayList<>();
        EnhancedProgressMonitor monitor = ProgressMonitorFactory.enhance(mon);
        EnhancedProgressMonitor[] mons = monitor.split(new double[]{4, 1, 2});
        MapList<Integer, TeacherPeriodStat> teacherPeriodStats = evalTeacherStatList(periodId, teacherFilter
                , new CourseAssignmentFilterAnd().and(filter).and(new DefaultCourseAssignmentFilter().addAcceptedSemester(semesterId))
                , deviationConfig, mons[0]);

        int i = 0;
        int max = teacherPeriodStats.size();
        for (TeacherPeriodStat s : teacherPeriodStats) {
            mons[1].setProgress(i++, max);
            if (semesterId == null) {
                all.addAll(Arrays.asList(s.getSemesters()));
            } else {
                for (TeacherSemesterStat ss : s.getSemesters()) {
                    if (ss.getSemester().getId() == semesterId) {
                        all.add(ss);
                    }
                }
            }
        }
        evalTeacherSemesterStatListDeviation(all, deviationConfig, mons[2]);
        return all;
    }

    public MapList<Integer, TeacherPeriodStat> evalTeacherStatList(final int periodId, TeacherFilter teacherFilter, CourseAssignmentFilter courseAssignmentFilter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        EnhancedProgressMonitor monitor = ProgressMonitorFactory.enhance(mon);
        Chronometer ch = new Chronometer();

        TeacherIdByTeacherPeriodComparator teacherIdByTeacherPeriodComparator = new TeacherIdByTeacherPeriodComparator(periodId);

        List<AcademicTeacher> teachersList = new ArrayList<>(findTeachers(periodId, teacherFilter));
        Collections.sort(teachersList, teacherIdByTeacherPeriodComparator);

        MapList<Integer, TeacherPeriodStat> stats = new DefaultMapList<Integer, TeacherPeriodStat>(teacherPeriodStatMapListConverter);
        for (AcademicTeacher teacher : teachersList) {
            TeacherPeriodStat st = evalTeacherStat0(periodId, teacher.getId(), teacher, null, null, courseAssignmentFilter, deviationConfig);
            if (st != null) {
                if (st.getValue().getEquiv() > 0) {
                    stats.add(st);
                }
            }
        }
        //evalTeacherPeriodStatListDeviation(stats,deviationConfig);
        Map<Integer, List<TeacherSemesterStat>> semestersLists = new HashMap<>();

        Set<DeviationGroup> groups = deviationConfig.getGroups();
        if (groups.size() == 0) {
            groups = DEFAULT_DEVIATION_GROUPS;
        }
        EnhancedProgressMonitor[] mons = monitor.split(3);

        TeacherValuePopulation emptyPopulation = new TeacherValuePopulation(null, null, null);
        Map<String, TeacherValuePopulation> lists = new HashMap<String, TeacherValuePopulation>();
        for (int i = 0; i < stats.size(); i++) {
            TeacherPeriodStat stat = stats.get(i);
            mons[0].setProgress(i, stats.size());
            if (stat.getValue().getEquiv() == 0) {
                stat.setPopulation(emptyPopulation);
                emptyPopulation.addValue(stat.getTeacher(), 0);
                //should ignore it?
            } else {
                for (TeacherSemesterStat semesterStat : stat.getSemesters()) {
                    List<TeacherSemesterStat> teacherSemesterStats = semestersLists.get(semesterStat.getSemester().getId());
                    if (teacherSemesterStats == null) {
                        teacherSemesterStats = new ArrayList<>();
                        semestersLists.put(semesterStat.getSemester().getId(), teacherSemesterStats);
                    }
                    teacherSemesterStats.add(semesterStat);
                }
                AcademicTeacherSituation situation = stat.getTeacher().getSituation();
                AcademicTeacherDegree degree = stat.getTeacher().getDegree();
                AcademicOfficialDiscipline discipline = stat.getTeacher().getOfficialDiscipline();
                AppDepartment department = stat.getTeacher().getDepartment();
                String k = "";
                if (groups.contains(DeviationGroup.DEGREE)) {
                    k += ":" + (degree == null ? "" : String.valueOf(degree.getId()));
                }
                if (groups.contains(DeviationGroup.SITUATION)) {
                    k += ":" + (situation == null ? "" : String.valueOf(situation.getId()));
                }
                if (groups.contains(DeviationGroup.SITUATION_TYPE)) {
                    k += ":" + ((situation == null || situation.getType() == null) ? "" : String.valueOf(situation.getType()));
                }
                if (groups.contains(DeviationGroup.DISCIPLINE)) {
                    k += ":" + (discipline == null ? "" : String.valueOf(discipline.getId()));
                }
                if (groups.contains(DeviationGroup.DEPARTMENT)) {
                    k += ":" + (department == null ? "" : String.valueOf(department.getId()));
                }
                TeacherValuePopulation p = lists.get(k);
                if (p == null) {
                    p = new TeacherValuePopulation(situation, degree, discipline);
                    lists.put(k, p);
                }
                stat.setPopulation(p);
                p.addValue(stat.getTeacher(), stat.getDeviationBaseValue());
            }
        }
        int i = 0;
        int max = lists.size();
        for (TeacherValuePopulation p : lists.values()) {
            mons[1].setProgress(i++, max);
            p.build();
        }
        i = 0;
        max = semestersLists.size();
        for (List<TeacherSemesterStat> ll : semestersLists.values()) {
            mons[1].setProgress(i++, max);
            evalTeacherSemesterStatListDeviation(ll, deviationConfig, mons[2]);
        }

        log.log(Level.FINE, "evalTeachersStat {0} teachers in {1}", new Object[]{teachersList.size(), ch.stop()});
        return stats;//.toArray(new TeacherStat[stats.size()]);
    }

    public GlobalStat evalGlobalStat(int periodId, TeacherFilter teacherFilter, CourseAssignmentFilter filter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        EnhancedProgressMonitor monitor = ProgressMonitorFactory.enhance(mon);
        GlobalStat s = new GlobalStat();
        EnhancedProgressMonitor[] mons = monitor.split(new double[]{3, 1, 1});
        MapList<Integer, TeacherPeriodStat> ts = evalTeacherStatList(periodId, teacherFilter, filter, deviationConfig, mons[0]);
        int i = 0;
        int max = ts.size();
        for (TeacherPeriodStat t : ts) {
            mons[1].setProgress(i++, max);
            AcademicTeacherPeriod trs = findAcademicTeacherPeriod(periodId, t.getTeacher());
            AcademicTeacherSituation situation = trs.getSituation();
            AcademicTeacherDegree degree = trs.getDegree();
            GlobalAssignmentStat[] annStatAss = new GlobalAssignmentStat[]{
                    s.getAssignment(null, situation, degree),
                    s.getAssignment(null, situation, null),
                    s.getAssignment(null, null, null)};

            for (GlobalAssignmentStat y : annStatAss) {
                y.addTeacherStat(t);
            }
            for (TeacherSemesterStat semLoad : t.getSemesters()) {
                AcademicSemester semester = semLoad.getSemester();

                GlobalAssignmentStat[] semStatAss = new GlobalAssignmentStat[]{
                        s.getAssignment(semester, situation, degree),
                        s.getAssignment(semester, situation, null),
                        s.getAssignment(semester, null, degree),
                        s.getAssignment(semester, null, null)
                };

                for (GlobalAssignmentStat y : semStatAss) {
                    y.addTeacherStat(semLoad);
                }
            }
        }
        List<AcademicTeacherSituation> contractualsList = findTeacherSituations(AcademicTeacherSituationType.CONTRACTUAL);
        List<AcademicTeacherSituation> permanentsList = findTeacherSituations(AcademicTeacherSituationType.PERMANENT);
        List<AcademicTeacherSituation> temporaryList = findTeacherSituations(AcademicTeacherSituationType.TEMPORARY);
        List<AcademicTeacherSituation> leaveList = findTeacherSituations(AcademicTeacherSituationType.LEAVE);
        AcademicTeacherDegree assistant = findTeacherDegree("MA");
        AcademicConversionTableHelper conversionTableByPeriodId = findConversionTableByPeriodId(periodId);

        List<AcademicCourseAssignment> courseAssignments = findCourseAssignments(periodId, new CourseAssignmentFilterAnd().and(filter).and(new CourseAssignmentFilter() {
            @Override
            public boolean acceptAssignment(AcademicCourseAssignment academicCourseAssignment) {
                AcademicTeacher t = academicCourseAssignment.getTeacher();
                if (t != null) {
                    return teacherFilter == null || teacherFilter.acceptTeacher(findAcademicTeacherPeriod(periodId, t));
                }
                return true;
            }

            @Override
            public boolean lookupIntents() {
                return filter.lookupIntents();
            }
        }));
        List<AcademicCoursePlan> coursePlans = extractCourses(courseAssignments);

//        StatCache.PeriodCache periodCache = cache.forPeriod(periodId);
        s.setCoursePlanCount(coursePlans.size());
        s.setCourseAssignmentCount(courseAssignments.size());
        HashSet<Integer> semestersIds = new HashSet<>();
        i = 0;
        max = courseAssignments.size();
        for (AcademicCourseAssignment value : courseAssignments) {
            mons[1].setProgress(i++, max);
            AcademicSemester semester = value.resolveSemester();
            semestersIds.add(semester.getId());
            double grp = value.getGroupCount();
            double shr = value.getShareCount();
            LoadValue loadValue = new LoadValue(
                    value.getValueC() * grp * shr,
                    value.getValueTD() * grp * shr,
                    value.getValueTP() * grp * shr,
                    value.getValuePM() * grp * shr
            );
            double g = evalValueEquiv(loadValue, assistant, conversionTableByPeriodId);
            loadValue.setEquiv(g);
            List<GlobalAssignmentStat> possibleGlobalAssignmentStats = new ArrayList<>();
            List<AcademicTeacherSituation> sits = new ArrayList<>();
            List<AcademicSemester> sems = new ArrayList<>();
            List<AcademicTeacherDegree> degs = new ArrayList<>();
            sems.add(null);
            sems.add(semester);
            sits.add(null);
            degs.add(null);
            boolean permanent = false;
            AcademicTeacherSituation theSituation = null;
            if (value.getTeacher() != null) {
                AcademicTeacherPeriod pp = findAcademicTeacherPeriod(periodId, value.getTeacher());
                theSituation = pp.getSituation();
                if (theSituation != null) {
                    permanent = theSituation.getType() == AcademicTeacherSituationType.PERMANENT;
                    sits.add(theSituation);
                }
                if (pp.getDegree() != null) {
                    degs.add(pp.getDegree());
                }
            }
            for (AcademicSemester sem : sems) {
                for (AcademicTeacherSituation sit : sits) {
                    for (AcademicTeacherDegree deg : degs) {
                        possibleGlobalAssignmentStats.add(s.getAssignment(sem, sit, deg));
                    }
                }
            }
            possibleGlobalAssignmentStats.add(s.getAssignment(null, null, null));
            for (GlobalAssignmentStat globalAssignmentStat : possibleGlobalAssignmentStats) {
                if (value.getTeacher() == null) {
                    globalAssignmentStat.getMissingAssignmentsLoad().add(loadValue);
                    globalAssignmentStat.getMissingAssignmentsMap().add(value);
                }
                globalAssignmentStat.getTargetAssignmentsLoad().add(loadValue);
                globalAssignmentStat.getTargetAssignmentsMap().add(value);
            }
            AcademicOfficialDiscipline od = value.getCoursePlan().getOfficialDiscipline();
            String ods = od == null ? "" : od.getName();

            if (permanent) {
                DisciplineStat officialDiscipline = s.getPermanentLoadValueByOfficialDiscipline().getOrCreate(ods);
                officialDiscipline.getAssignments().add(value);
                if (value.getTeacher() != null) {
                    officialDiscipline.getTeachers().add(value.getTeacher());
                }
                officialDiscipline.getValue().add(loadValue);
                VMap<String, DisciplineStat> nonOfficialDisciplines = s.getPermanentLoadValueByNonOfficialDiscipline();
                Set<String> ood = parseDisciplinesNames(value.getCoursePlan().getDiscipline(), false);
                if (ood.isEmpty()) {
                    ood.add("");
                }
                for (String s1 : ood) {
                    DisciplineStat d = nonOfficialDisciplines.getOrCreate(s1);
                    d.getAssignments().add(value);
                    if (value.getTeacher() != null) {
                        d.getTeachers().add(value.getTeacher());
                    }
                    d.getValue().add(loadValue);
                }
            } else {
                DisciplineStat officialDiscipline = s.getNonPermanentLoadValueByOfficialDiscipline().getOrCreate(ods);
                officialDiscipline.getAssignments().add(value);
                if (value.getTeacher() != null) {
                    officialDiscipline.getTeachers().add(value.getTeacher());
                }
                officialDiscipline.getValue().add(loadValue);
                VMap<String, DisciplineStat> nonOfficialDisciplines = s.getNonPermanentLoadValueByNonOfficialDiscipline();
                Set<String> ood = parseDisciplinesNames(value.getCoursePlan().getDiscipline(), false);
                if (ood.isEmpty()) {
                    ood.add("");
                }
                for (String s1 : ood) {
                    DisciplineStat d = nonOfficialDisciplines.getOrCreate(s1);
                    d.getAssignments().add(value);
                    if (value.getTeacher() != null) {
                        d.getTeachers().add(value.getTeacher());
                    }
                    d.getValue().add(loadValue);
                }
            }
            SituationTypeStat teachersPermanentStat = null;
            if (theSituation != null) {
                switch (theSituation.getType()) {
                    case PERMANENT: {
                        teachersPermanentStat = s.getTeachersPermanentStat();
                        break;
                    }
                    case CONTRACTUAL: {
                        teachersPermanentStat = s.getTeachersContractualStat();
                        break;
                    }
                    case TEMPORARY: {
                        teachersPermanentStat = s.getTeachersTemporaryStat();
                        break;
                    }
                    default: {
                        teachersPermanentStat = s.getTeachersOtherStat();
                    }
                }
            } else {
                if (value.getTeacher() == null) {
                    teachersPermanentStat = s.getUnassignedStat();
                } else {
                    teachersPermanentStat = s.getTeachersOtherStat();
                }
            }
            if (teachersPermanentStat != null) {
                teachersPermanentStat.getValue().add(loadValue);
                teachersPermanentStat.getAssignments().add(value);
            }
            if (value.getTeacher() != null && teachersPermanentStat != null) {
                teachersPermanentStat.getTeachers().add(value.getTeacher());
            }
        }

//        GlobalAssignmentStat a = s.getAssignment(null, null, null);
//        a.getMissingAssignmentsLoad().set(a.getTargetAssignmentsLoad()).substruct(a.getValue());
//        for (AcademicSemester sem : findSemesters) {
//            a = s.getAssignment(sem, null, null);
////            a.getMissingAssignmentsLoad().set(a.getTargetAssignmentsLoad()).substruct(a.getValue());
//        }
        s.getAssignments().sort(GLOBAL_ASSIGNMENT_STAT_COMPARATOR);
        s.setTeachersCount(s.getTotalAssignment().getTeachersCount());

        s.getTeachersTemporaryStat().setTeachersCount(s.getAssignmentTeacherCount(null, temporaryList, null));
        s.getTeachersContractualStat().setTeachersCount(s.getAssignmentTeacherCount(null, contractualsList, null));
        s.getTeachersPermanentStat().setTeachersCount(s.getAssignmentTeacherCount(null, permanentsList, null));
        s.getTeachersLeaveStat().setTeachersCount(s.getAssignmentTeacherCount(null, leaveList, null));

        int totalCount = (int) (
                s.getTeachersTemporaryStat().getTeachersCount()
                        + s.getTeachersPermanentStat().getTeachersCount()
                        + s.getTeachersContractualStat().getTeachersCount()
                        + s.getTeachersLeaveStat().getTeachersCount());

        s.getTeachersOtherStat().setTeachersCount(s.getTeachersCount() - totalCount);
        GlobalAssignmentStat ta = s.getTotalAssignment();

        GlobalAssignmentStat neededRelative = s.getNeededRelative();
//        neededRelative
//                .getExtra()
//                .add(ta.getMissingAssignmentsLoad());
//        neededRelative
//                .getExtraWeek()
//                .add(ta.getMissingAssignmentsLoad().copy().div(maxWeeks));
//        neededRelative.setTeachersCount((int) Math.ceil(
//                evalValueEquiv(neededRelative.getExtra(), assistant)
//                / assistant.getValueDU()
//        )
//        );

        //calcul de la charge nécessaire selon le du des enseignant permanents
        //donc en gros combien on a besoin d'assistants pour ne plus recruter des contractuels et vacataires
        GlobalAssignmentStat neededByDue = s.getNeededAbsolute();
        double permEquivDu = s.getAssignmentSumDue(null, permanentsList, null).getEquiv();
        double permEquivTot = s.getAssignmentSumValue(null, permanentsList, null).getEquiv();

//        double contrEquivDu=s.getAssignment(null, Contractuel, null).getDue().getEquiv();
        double contrEquivTot = evalValueEquiv(s.getAssignmentSumValue(null, contractualsList, null), assistant, conversionTableByPeriodId);
        double vacEquivTot = evalValueEquiv(s.getAssignmentSumValue(null, temporaryList, null), assistant, conversionTableByPeriodId);
        double missingAss = evalValueEquiv(ta.getMissingAssignmentsLoad(), assistant, conversionTableByPeriodId);
        int maxWeeks = getSemesterMaxWeeks();
        neededByDue.getValue().setEquiv(Math.max(0, permEquivTot - permEquivDu + contrEquivTot + vacEquivTot + missingAss));
        List<AcademicSemester> semesters = findSemesters();
        neededByDue.getValueWeek().setEquiv(neededByDue.getValue().getEquiv() / maxWeeks / semesters.size());
        neededByDue.setTeachersCount(
                /*(int) Math.ceil*/(
                        neededByDue.getValueWeek().getEquiv() / assistant.getValueDU()
                )
        );

        neededRelative.getValue().setEquiv(contrEquivTot + vacEquivTot + missingAss);
        neededRelative.getValueWeek().setEquiv(neededRelative.getValue().getEquiv() / maxWeeks / semesters.size());
        neededRelative.setTeachersCount(
                /*(int) Math.ceil*/(
                        neededRelative.getValueWeek().getEquiv() / assistant.getValueDU()
                )
        );

        double contractMiss = 6;
        GlobalAssignmentStat missingStat = s.getMissing();
        missingStat.getValue().set(ta.getMissingAssignmentsLoad());
        missingStat.getValueWeek().set(missingStat.getValue()).div(maxWeeks * semesters.size());
        missingStat.setTeachersCount(
                /*(int) Math.ceil*/(
                        missingStat.getValueWeek().getEquiv() / contractMiss
                )
        );

//        double duAssistant = evalValueEquiv(s.getAssignment(null, Permanent, null).getDue(), assistant);
//        double targetAssistant = evalValueEquiv(s.getTotalAssignment().getTargetAssignmentsLoad(), assistant);
//        neededByDue.getValue().setEquiv(duAssistant);
//        neededByDue.getTargetEquiv().setEquiv(targetAssistant);
//        neededByDue.getExtra().setEquiv(targetAssistant - duAssistant);
//        neededByDue.getValueWeek().setEquiv(neededByDue.getValue().getEquiv() / maxWeeks);
//        neededByDue.getExtraWeek().setEquiv(neededByDue.getValueWeek().getEquiv() / maxWeeks);
//        neededByDue.setTeachersCount(
//                (int) Math.ceil(
//                        neededByDue.getExtra().getEquiv() / assistant.getValueDU()
//                )
//        );

        for (SituationTypeStat situationTypeStat : new SituationTypeStat[]{
                s.getTeachersPermanentStat(),
                s.getTeachersTemporaryStat(),
                s.getTeachersLeaveStat(),
                s.getTeachersOtherStat(),
                s.getTeachersContractualStat(),

        }) {
            situationTypeStat.setCourseAssignmentCount(situationTypeStat.getAssignments().size());
            situationTypeStat.setCoursePlanCount(extractCourses(situationTypeStat.getAssignments()).size());
            for (AcademicTeacher t : situationTypeStat.getTeachers()) {

                TeacherPeriodStat teacherPeriodStat = ts.getByKey(t.getId());
                if (teacherPeriodStat == null) {
                    //not loaded among teachers
                    System.out.println("evalGlobalStat bug... please fix me");
                } else {
                    LoadValue due = teacherPeriodStat.getDue();
                    situationTypeStat.getDue().add(due);
                }
            }
        }
        LoadValue refDue = evalValueEquiv(assistant.getValueDU(), assistant, conversionTableByPeriodId);

        s.setReferenceTeacherDueLoad(refDue.getEquivTD() * maxWeeks * semestersIds.size());
        /**
         * permanentLoad-permanentDue+Non
         * overload of permanents with a
         */
        s.setOverload(
                Math.max(0,
                        s.getTeachersPermanentStat().getValue().getEquiv() - s.getTeachersPermanentStat().getDue().getEquiv()
                                + s.getTeachersContractualStat().getValue().getEquiv()
                                + s.getTeachersTemporaryStat().getValue().getEquiv()
                                + s.getTeachersOtherStat().getValue().getEquiv()
                                + s.getMissing().getValue().getEquiv()
                )

        );
        s.setNonPermanentLoad(
                Math.max(0, s.getTeachersContractualStat().getValue().getEquiv()
                        + s.getTeachersTemporaryStat().getValue().getEquiv()
                        + s.getTeachersOtherStat().getValue().getEquiv()
                        + s.getMissing().getValue().getEquiv()
                )
        );

        s.setPermanentOverload(
                Math.max(0, s.getTeachersPermanentStat().getValue().getEquiv() - s.getTeachersPermanentStat().getDue().getEquiv())
        );

        s.setRelativeOverload(
                Math.max(0, s.getTeachersPermanentStat().getValue().getEquiv() - s.getTeachersContractualStat().getDue().getEquiv()
                        + s.getTeachersContractualStat().getValue().getEquiv() - s.getTeachersContractualStat().getDue().getEquiv()
                        + s.getTeachersTemporaryStat().getValue().getEquiv() - s.getTeachersTemporaryStat().getDue().getEquiv()
                        + s.getTeachersTemporaryStat().getValue().getEquiv() - s.getTeachersTemporaryStat().getDue().getEquiv())
        );

        if (s.getReferenceTeacherDueLoad() != 0) {
            s.setOverloadTeacherCount(/*(int) Math.ceil*/(s.getOverload() / s.getReferenceTeacherDueLoad()));
            s.setPermanentOverloadTeacherCount(/*(int) Math.ceil*/(s.getPermanentOverload() / s.getReferenceTeacherDueLoad()));
            s.setRelativeOverloadTeacherCount(/*(int) Math.ceil*/(s.getRelativeOverload() / s.getReferenceTeacherDueLoad()));
            s.setNonPermanentLoadTeacherCount(/*(int) Math.ceil*/(s.getNonPermanentLoad() / s.getReferenceTeacherDueLoad()));
            s.setUnassignedLoadTeacherCount(/*(int) Math.ceil*/(s.getUnassignedStat().getValue().getEquiv() / s.getReferenceTeacherDueLoad()));
        }

        mons[2].setProgress(1.0);
        return s;
    }


    public AcademicTeacher findCurrentHeadOfDepartment() {
        UserSession sm = core.getUserSession();
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user == null || user.getDepartment() == null) {
            return null;
        }
        return findHeadOfDepartment(user.getDepartment().getId());
    }

    public boolean isUserSessionManager() {
        UserSession sm = core.getUserSession();
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user == null || user.getDepartment() == null) {
            return false;
        }
        for (AppProfile u : core.findProfilesByUser(user.getId())) {
            String name = u.getName();
            if ("HeadOfDepartment".equals(name)) {
                //check if same department
                return true;
            }
            if ("DirectorOfStudies".equals(name)) {
                //check if same department
                return true;
            }
            if ("Director".equals(name)) {
                //check if same department
                return true;
            }
        }
        return false;
    }

//    public boolean isManagerOf(int userId) {
//        AppUser targetUser = core.findUser(userId);
//        int targetDept = targetUser==null || targetUser.getDepartment()==null?-1:targetUser.getDepartment().getId();
//        return isUserSessionManagerByDepartment(targetDept);
//    }

    public boolean isManagerOf(AppUser targetUser) {
        UserSession sm = core.getUserSession();
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user != null && targetUser != null && user.getId() == targetUser.getId()) {
            return true;
        }
        AppDepartment targetDept = (targetUser == null) ? null : targetUser.getDepartment();
        return isManagerOf(targetDept);
    }

    public boolean isManagerOf(AcademicTeacher teacher) {
        UserSession sm = core.getUserSession();
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user != null && teacher.getUser() != null && user.getId() == teacher.getUser().getId()) {
            return true;
        }
        AppDepartment targetDept = (teacher == null) ? null : teacher.getDepartment();
        if (targetDept == null) {
            targetDept = (teacher == null || teacher.getUser() == null) ? null : teacher.getUser().getDepartment();
        }
        return isManagerOf(targetDept);
    }

    public boolean isManagerOf(AcademicStudent student) {
        UserSession sm = core.getUserSession();
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user != null && student.getUser() != null && user.getId() == student.getUser().getId()) {
            return true;
        }

        AppDepartment targetDept = (student == null) ? null : student.getDepartment();
        if (targetDept == null) {
            targetDept = (student == null || student.getUser() == null) ? null : student.getUser().getDepartment();
        }
        return isManagerOf(targetDept);
    }

//    public boolean isUserSessionManagerByDepartment(int targetDept) {
//        UserSession sm = core.getUserSession();
//        AppUser user = (sm == null) ? null : sm.getUser();
//        if (user == null || user.getDepartment() == null) {
//            return false;
//        }
//        int dept = user==null || user.getDepartment()==null?-1:user.getDepartment().getId();
//        for (AppProfile u : core.findProfilesByUser(user.getId())) {
//            String name = u.getName();
//            if ("HeadOfDepartment".equals(name)) {
//                if(dept!=-1 && dept==targetDept) {
//                    //check if same department
//                    return true;
//                }
//            }
//            if ("DirectorOfStudies".equals(name)) {
//                //check if same department
//                return true;
//            }
//            if ("Director".equals(name)) {
//                //check if same department
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean isManagerOf(AppDepartment department) {
        UserSession sm = core.getUserSession();
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user == null || user.getDepartment() == null) {
            return false;
        }
        int dept = user == null || user.getDepartment() == null ? -1 : user.getDepartment().getId();
        for (AppProfile u : core.findProfilesByUser(user.getId())) {
            String name = u.getName();
            if ("HeadOfDepartment".equals(name)) {
                if (dept != -1 && department != null && dept == department.getId()) {
                    //check if same department
                    return true;
                }
            }
            if ("DirectorOfStudies".equals(name)) {
                //check if same department
                return true;
            }
            if ("Director".equals(name)) {
                //check if same department
                return true;
            }
        }
        return false;
    }

    public AcademicTeacher findHeadOfDepartment(int depId) {
        AppUser u = core.findHeadOfDepartment(depId);
        if (u != null) {
            return findTeacherByUser(u.getId());
        }
        return null;
    }


    public void importStudent(int periodId, AcademicStudentImport s) throws IOException {
        XlsxLoadImporter i = new XlsxLoadImporter();
        XlsxLoadImporter.ImportStudentContext ctx = new XlsxLoadImporter.ImportStudentContext();
        ctx.setMainPeriod(core.findPeriodOrMain(periodId));
        i.importStudent(s, ctx);
    }

    public void importTeacher(int periodId, AcademicTeacherImport t) throws IOException {
        XlsxLoadImporter i = new XlsxLoadImporter();
        XlsxLoadImporter.ImportTeacherContext ctx = new XlsxLoadImporter.ImportTeacherContext();
        ctx.setMainPeriod(core.findPeriodOrMain(periodId));
        i.importTeacher(t, ctx);
    }

    public int importFile(int periodId, VFile folder, ImportOptions importOptions) throws IOException {
        XlsxLoadImporter i = new XlsxLoadImporter();
        return i.importFile(periodId, folder, importOptions);
    }

    public AcademicTeacher findTeacher(StringComparator t) {
        for (AcademicTeacher teacher : findTeachers()) {
            if (t.matches(teacher.getContact() == null ? null : teacher.getContact().getFullName())) {
                return teacher;
            }
        }
        return null;
    }

    public void update(Object t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        pu.merge(t);
    }

    public List<AcademicTeacher> findTeachers(int period, TeacherFilter teacherFilter) {
        List<AcademicTeacher> teachers = findTeachers();
        if (teacherFilter == null) {
            return teachers;
        }
        List<AcademicTeacher> teachers2 = new ArrayList<>(teachers.size());
        for (AcademicTeacher teacher : teachers) {
            if (teacherFilter.acceptTeacher(findAcademicTeacherPeriod(period, teacher))) {
                teachers2.add(teacher);
            }
        }
        return teachers2;
    }

    public List<AcademicTeacher> findTeachers() {
        return cacheService.getList(AcademicTeacher.class);

        //return UPA.getPersistenceUnit().findAll(AcademicTeacher.class);
    }

    public List<AcademicTeacher> findEnabledTeachers(int periodId) {
        List<AcademicTeacher> periodId1 = UPA.getPersistenceUnit().createQuery("Select distinct u.teacher from AcademicTeacherPeriod u where u.teacher.deleted=false and u.enabled=true order by u.teacher.contact.fullName and u.periodId=:periodId")
                .setParameter("periodId", periodId)
                .getResultList();
        //there is a bug in distinct implementation in UPA, should fix it.
        //but waiting for that this is a workaround
        List<AcademicTeacher> vals = new ArrayList<>();
        HashSet<Integer> visited = new HashSet<>();
        for (AcademicTeacher academicTeacher : periodId1) {
            if (!visited.contains(academicTeacher.getId())) {
                visited.add(academicTeacher.getId());
                vals.add(academicTeacher);
            }
        }
        return vals;
    }

    public List<AcademicStudent> findStudents() {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicStudent u where u.deleted=false order by u.contact.fullName").getResultList();
    }


    /**
     * @param studentUpqlFilter ql expression x based. example "x.fullName like '%R%'"
     * @return
     */
    public List<AcademicStudent> findStudents(String studentProfileFilter, String studentUpqlFilter) {
        List<AcademicStudent> base = UPA.getPersistenceUnit().createQuery("Select x from AcademicStudent x " +
                " where " +
                " x.deleted=false " +
                ((StringUtils.isEmpty(studentUpqlFilter)) ? "" : (" and " + studentUpqlFilter)) +
                " order by x.contact.fullName").getResultList();
        return filterStudents(base,studentProfileFilter);
    }

    public List<AcademicStudent> filterStudents(List<AcademicStudent> base,String studentProfileFilter) {
        if (!StringUtils.isEmpty(studentProfileFilter)) {
            HashSet<Integer> goodUsers = new HashSet<Integer>();
            AppUserType studentType = core.findUserType("Student");
            List<AppUser> users = VrApp.getBean(CorePlugin.class).findUsersByProfileFilter(studentProfileFilter, studentType.getId());
            for (AppUser user : users) {
                goodUsers.add(user.getId());
            }
            List<AcademicStudent> goodStudents = new ArrayList<>(base.size());
            for (AcademicStudent s : base) {
                AppUser u = s.getUser();
                if (u != null && goodUsers.contains(u.getId())) {
                    goodStudents.add(s);
                }
            }
            return goodStudents;
        } else {
            return base;
        }
    }

    public List<AcademicTeacher> findTeachers(String teacherProfileFilter) {
        return filterTeachers(findTeachers(),teacherProfileFilter);
    }

    public List<AcademicTeacher> filterTeachers(List<AcademicTeacher> base,String teacherProfileFilter) {
        if (!StringUtils.isEmpty(teacherProfileFilter)) {
            List<AcademicTeacher> goodTeachers = new ArrayList<>();
            HashSet<Integer> goodUsers = new HashSet<Integer>();
            AppUserType teacherType = core.findUserType("Teacher");
            List<AppUser> users = VrApp.getBean(CorePlugin.class).findUsersByProfileFilter(teacherProfileFilter, teacherType.getId());
            for (AppUser user : users) {
                goodUsers.add(user.getId());
            }
            for (AcademicTeacher s : base) {
                AppUser u = s.getUser();
                if (u != null && goodUsers.contains(u.getId())) {
                    goodTeachers.add(s);
                }
            }
            return goodTeachers;
        } else {
            return base;
        }
    }

    public List<AcademicFormerStudent> findGraduatedStudents() {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicFormerStudent u where u.deleted=false and u.graduated=true").getResultList();
    }

    public List<AcademicTeacher> findTeachersWithAssignmentsOrIntents() {
        return UPA.getPersistenceUnit()
                .createQuery("Select u from AcademicTeacher u where u.id in ("
                        + " Select t.id from AcademicTeacher t "
                        + " left join AcademicCourseAssignment a on a.teacheId=t.id"
                        + " left join AcademicCourseIntent i on i.teacherId=t.id"
                        + " where (a is not null) or (i is not null)"
                        + ") order by u.contact.fullName")
                .getResultList();
    }

    public List<AcademicTeacher> findTeachersWithAssignements() {
        return UPA.getPersistenceUnit()
                .createQuery("Select u from AcademicTeacher u where u.id in ("
                        + " Select t.id from AcademicTeacher t "
                        + " inner join AcademicCourseAssignment a on a.teacherId=t.id"
                        + ") order by u.contact.fullName")
                .getResultList();
    }

    public List<AcademicProgram> findPrograms() {
        return UPA.getPersistenceUnit()
                .createQuery(
                        "Select a from AcademicProgram a order by a.name")
                .getResultList();
    }

    public List<AppGender> findGenders() {
        return UPA.getPersistenceUnit().findAll(AppGender.class);
    }

    public List<AcademicTeacherDegree> findTeacherDegrees() {
        return UPA.getPersistenceUnit()
                .createQuery(
                        "Select a from AcademicTeacherDegree a")
                .getResultList();
    }

    public List<AcademicTeacherSituation> findTeacherSituations() {
        return UPA.getPersistenceUnit().findAll(AcademicTeacherSituation.class);
    }

    public List<AcademicPreClass> findAcademicPreClasses() {
        return UPA.getPersistenceUnit().findAll(AcademicPreClass.class);
    }

    public List<AcademicBac> findAcademicBacs() {
        return UPA.getPersistenceUnit().findAll(AcademicBac.class);
    }

    public Map<Integer, AcademicClass> findAcademicClassesMap() {
        HashMap<Integer, AcademicClass> _allClasses = new HashMap<>();
        for (AcademicClass a : findAcademicClasses()) {
            _allClasses.put(a.getId(), a);
        }
        return _allClasses;
    }

    public Map<Integer, AcademicPreClass> findAcademicPreClassesMap() {
        HashMap<Integer, AcademicPreClass> _allClasses = new HashMap<>();
        for (AcademicPreClass a : findAcademicPreClasses()) {
            _allClasses.put(a.getId(), a);
        }
        return _allClasses;
    }

    public Map<Integer, AcademicBac> findAcademicBacsMap() {
        HashMap<Integer, AcademicBac> _allClasses = new HashMap<>();
        for (AcademicBac a : findAcademicBacs()) {
            _allClasses.put(a.getId(), a);
        }
        return _allClasses;
    }

    public List<AcademicClass> findAcademicClasses() {
        return UPA.getPersistenceUnit().findAll(AcademicClass.class);
    }

    public List<AcademicCourseLevel> findCourseLevels() {
        return UPA.getPersistenceUnit().findAll(AcademicCourseLevel.class);
    }

    public AcademicCourseLevel findCourseLevel(int academicClassId, int semesterId) {
        return UPA.getPersistenceUnit().createQuery("Select x from AcademicCourseLevel x where "
                + " x.academicClassId=:academicClassId"
                + " and x.semesterId=:semesterId"
        ).setParameter("academicClassId", academicClassId)
                .setParameter("semesterId", semesterId)
                .getFirstResultOrNull();
    }

    public List<AcademicSemester> findSemesters() {
        return cacheService.getList(AcademicSemester.class);
//        return UPA.getPersistenceUnit().createQueryBuilder(AcademicSemester.class).orderBy(new Order().addOrder(new Var("name"), true))
//                .getResultList();
    }

    public AcademicProgram findProgram(int departmentId, String t) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicProgram a where a.name=:t and a.departmentId=:departmentId")
                .setParameter("t", t)
                .setParameter("departmentId", departmentId)
                .getFirstResultOrNull();
    }

    public List<AcademicProgram> findPrograms(String t) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicProgram a where a.name=:t")
                .setParameter("t", t)
                .getResultList();
    }

    public AcademicSemester findSemester(String code) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicSemester a where a.code=:code or a.name=:code or a.name2=:code")
                .setParameter("code", code)
                .getFirstResultOrNull();
    }

    public AcademicCoursePlan findCoursePlan(int periodId, int courseLevelId, String courseName) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicCoursePlan a where " +
                        "a.name=:courseName " +
                        "and a.courseLevelId=:courseLevelId " +
                        "and a.periodId=:periodId")
                .setParameter("courseName", courseName)
                .setParameter("courseLevelId", courseLevelId)
                .setParameter("periodId", periodId)
                .getFirstResultOrNull();
    }

    public AppGender findGender(String t) {
        return (AppGender) UPA.getPersistenceUnit().findByMainField(AppGender.class, t);
    }

    public AcademicTeacher findTeacher(String t) {
        return (AcademicTeacher) UPA.getPersistenceUnit().createQuery("Select u from AcademicTeacher u where u.contact.fullName=:name").setParameter("name", t).getFirstResultOrNull();
    }

    public AcademicStudent findStudentByFullName(String name) {
        return (AcademicStudent) UPA.getPersistenceUnit().createQuery("Select u from AcademicStudent u where u.contact.fullName=:name").setParameter("name", name).getFirstResultOrNull();
    }

    public AcademicTeacherDegree findTeacherDegree(String t) {
        EntityCache entityCache = cacheService.get(AcademicTeacherDegree.class);
        return entityCache.getProperty("TeacherDegreesByCode", new Action<Map<String, AcademicTeacherDegree>>() {
            @Override
            public Map<String, AcademicTeacherDegree> run() {
                Map<String, AcademicTeacherDegree> map = new HashMap<String, AcademicTeacherDegree>();
                for (Object o : cacheService.get(AcademicTeacherDegree.class).getValues()) {
                    AcademicTeacherDegree d = (AcademicTeacherDegree) o;
                    map.put(d.getCode(), d);
                }
                return map;
            }
        })
                .get(t);
//        return UPA.getPersistenceUnit().
//                createQuery("Select a from AcademicTeacherDegree a where a.code=:t")
//                .setParameter("t", t)
//                .getFirstResultOrNull();
    }

    public AcademicLoadConversionRule findLoadConversionRule(String t) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicLoadConversionRule a where a.name=:t")
                .setParameter("t", t)
                .getFirstResultOrNull();
    }

    public AcademicLoadConversionTable findLoadConversionTable(String t) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicLoadConversionTable a where a.name=:t")
                .setParameter("t", t)
                .getFirstResultOrNull();
    }

    public AcademicLoadConversionRow findLoadConversionRow(int tableId, int ruleId) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicLoadConversionRow a where a.conversionTableId=:tid and a.ruleId=:rid")
                .setParameter("tid", tableId)
                .setParameter("rid", ruleId)
                .getFirstResultOrNull();
    }

    public AcademicTeacherDegree findTeacherDegree(int id) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicTeacherDegree a where a.id=:t")
                .setParameter("t", id)
                .getFirstResultOrNull();
    }

    public AcademicTeacherSituation findTeacherSituation(String t) {
        return (AcademicTeacherSituation) UPA.getPersistenceUnit().findByMainField(AcademicTeacherSituation.class, t);
    }

    public List<AcademicTeacherSituation> findTeacherSituations(AcademicTeacherSituationType type) {
        EntityCache entityCache = cacheService.get(AcademicTeacherSituation.class);
        return entityCache.getProperty("findTeacherSituationsByType", new Action<Map<AcademicTeacherSituationType, List<AcademicTeacherSituation>>>() {
            @Override
            public Map<AcademicTeacherSituationType, List<AcademicTeacherSituation>> run() {
                Map<AcademicTeacherSituationType, List<AcademicTeacherSituation>> map = new HashMap<AcademicTeacherSituationType, List<AcademicTeacherSituation>>();
                for (Object o : cacheService.get(AcademicTeacherSituation.class).getValues()) {
                    AcademicTeacherSituation d = (AcademicTeacherSituation) o;
                    List<AcademicTeacherSituation> academicTeacherSituations = map.get(d.getType());
                    if (academicTeacherSituations == null) {
                        academicTeacherSituations = new ArrayList<AcademicTeacherSituation>();
                        map.put(d.getType(), academicTeacherSituations);
                    }
                    academicTeacherSituations.add(d);
                }
                return map;
            }
        }).get(type);
    }

    public AcademicTeacherSituation findTeacherSituation(int id) {
        return (AcademicTeacherSituation) UPA.getPersistenceUnit().findById(AcademicTeacherSituation.class, id);
    }

    public AcademicCourseLevel findCourseLevel(int programId, String name) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicCourseLevel a where a.name=:name and a.programId=:programId")
                .setParameter("name", name)
                .setParameter("programId", programId)
                .getFirstResultOrNull();
    }

    //    public AcademicCourseLevel findCourseLevel(String name) {
//        return UPA.getPersistenceUnit().
//                createQuery("Select a from AcademicCourseLevel a where a.name=:name")
//                .setParameter("name", name)
//                .getEntity();
//    }
    public AcademicCourseGroup findCourseGroup(int periodId, int classId, String name) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicCourseGroup a where a.name=:name and a.periodId=:periodId and a.academicClassId=:classId")
                .setParameter("name", name)
                .setParameter("classId", classId)
                .setParameter("periodId", periodId)
                .getFirstResultOrNull();
    }

    public List<AcademicCourseGroup> findCourseGroups(int periodId) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicCourseGroup a where a.periodId=:periodId")
                .setParameter("periodId", periodId)
                .getResultList();
    }

    public List<AcademicDiscipline> findDisciplines() {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicDiscipline a")
                .getResultList();
    }

    public AcademicOfficialDiscipline findOfficialDiscipline(int id) {
        return UPA.getPersistenceUnit().findById(AcademicOfficialDiscipline.class, id);
    }

    public AcademicOfficialDiscipline findOfficialDiscipline(String name) {
        return UPA.getPersistenceUnit().findByMainField(AcademicOfficialDiscipline.class, name);
    }

    public List<AcademicOfficialDiscipline> findOfficialDisciplines() {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicOfficialDiscipline a")
                .getResultList();
    }

    public AcademicDiscipline findDiscipline(String nameOrCode) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicDiscipline a where a.code=:code or a.name=:code or a.name2=:code")
                .setParameter("code", nameOrCode)
                .getFirstResultOrNull();
    }

    public AcademicCourseType findCourseType(int id) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseType a where "
                + "a.id=:id")
                .setParameter("id", id)
                .getFirstResultOrNull();
    }

    public AcademicCourseType findCourseType(String name) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseType a where "
                + "a.name=:name")
                .setParameter("name", name)
                .getFirstResultOrNull();
    }

    public List<AcademicCourseType> findCourseTypes() {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseType a ")
                .getResultList();
    }

    /**
     *
     */
    public void resetModuleTeaching() {
        resetCurrentYear();
        resetHistAcademicYears();
        trace.trace("resetModuleTeaching", "reset Module Academic", null, "academicPlugin", Level.FINE);
    }

    public void resetTeachers() {
        resetAssignments();
        UPA.getPersistenceUnit().createQuery("delete from AcademicTeacherSemestrialLoad").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicTeacher").executeNonQuery();
    }

    public void resetAssignments() {
        UPA.getPersistenceUnit().createQuery("delete from AcademicCourseIntent").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicCourseAssignment").executeNonQuery();
    }

    public void resetCourses() {
        UPA.getPersistenceUnit().createQuery("delete from AcademicCoursePlan").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicCourseGroup").executeNonQuery();
    }

    public void resetCurrentYear() {
        resetAssignments();
        resetCourses();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicCourseType").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicClass").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicCourseLevel").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicProgram").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicSemester").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicTeacherSituation").executeNonQuery();
//        UPA.getPersistenceUnit().createQuery("delete from AcademicTeacherDegree").executeNonQuery();
        trace.trace("resetCurrentYear", "reset Module Academic", null, "academicPlugin", Level.FINE);
    }

    public AcademicCourseAssignment findCourseAssignment(int courseAssignmentId) {
        return (AcademicCourseAssignment) UPA.getPersistenceUnit().findById(AcademicCourseAssignment.class, courseAssignmentId);
    }

    public AcademicCourseAssignment findCourseAssignment(int coursePlanId, Integer subClassId, String discriminator) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where " +
                "a.coursePlanId=:coursePlanId " +
                "and a.subClassId=:subClassId " +
                "and a.discriminator=:discriminator "
        )
                .setParameter("coursePlanId", coursePlanId)
                .setParameter("subClassId", subClassId)
                .setParameter("discriminator", discriminator)
                .getSingleResultOrNull();
    }

    public AcademicTeacher findTeacher(int t) {
        return cacheService.getList(AcademicTeacher.class).getByKey(t);
//        return (AcademicTeacher) UPA.getPersistenceUnit()
//                .createQuery("Select u from AcademicTeacher u where u.id=:id")
//                .setParameter("id", t)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
//                .getEntity();
//                .findById(AcademicTeacher.class, t);
    }

    public List<AcademicStudent> findStudentsByClass(int classId, int... classNumber) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        HashSet<Integer> ns = new HashSet<>();
        for (int i : classNumber) {
            ns.add(i);
        }
        if (ns.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Select a from AcademicStudent a where 1=1 ");
        if (ns.contains(1)) {
            ns.remove(1);
            sb.append("and a.lasClass1Id=:clsId");
        }
        if (ns.contains(2)) {
            ns.remove(2);
            sb.append("and a.lasClass2Id=:clsId");
        }
        if (ns.contains(3)) {
            ns.remove(3);
            sb.append("and a.lasClass3Id=:clsId");
        }
        if (!ns.isEmpty()) {
            throw new IllegalArgumentException("Invalid class Number " + ns);
        }
        return pu.createQuery(sb.toString()).setParameter("classId", classId).getResultList();
    }

    public void updateStudentClassByClass(int classNumber, int fromClassId, int toClassId) {
        for (AcademicStudent academicStudent : findStudentsByClass(fromClassId, classNumber)) {
            updateStudentClass(academicStudent.getId(), classNumber, toClassId);
        }
    }

    public AcademicStudent updateStudentClass(int studentId, int classNumber, int classId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicClass cls = findAcademicClass(classId);
        AcademicStudent student = findStudent(studentId);
        if (student != null) {
            switch (classNumber) {
                case 1: {
                    student.setLastClass1(cls);
                    break;
                }
                case 2: {
                    student.setLastClass2(cls);
                    break;
                }
                case 3: {
                    student.setLastClass3(cls);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid class Number");
                }
            }
            pu.merge(student);
        }
        return student;
    }

    public AcademicStudent findStudent(int t) {
        return (AcademicStudent) UPA.getPersistenceUnit().findById(AcademicStudent.class, t);
    }

    public MapList<Integer, AcademicCoursePlan> extractCourses(List<AcademicCourseAssignment> all) {
        MapList<Integer, AcademicCoursePlan> m = new DefaultMapList<Integer, AcademicCoursePlan>(
                AcademicCoursePlanIdConverter
        );
        for (AcademicCourseAssignment academicCourseAssignment : all) {
            AcademicCoursePlan p = academicCourseAssignment.getCoursePlan();
            if (p != null) {
                m.add(p);
            }
        }
        return m;
    }

    public MapList<Integer, AcademicCourseAssignment> findCourseAssignments(int periodId, CourseAssignmentFilter filter) {
        MapList<Integer, AcademicCourseAssignment> courseAssignments = findCourseAssignments(periodId);
        if (filter == null) {
            return courseAssignments;
        }
        MapList<Integer, AcademicCourseAssignment> m = new DefaultMapList<Integer, AcademicCourseAssignment>(
                AcademicCourseAssignmentIdConverter
        );
        for (AcademicCourseAssignment academicCourseAssignment : courseAssignments) {
            if (filter.acceptAssignment(academicCourseAssignment)) {
                m.add(academicCourseAssignment);
            }
        }
        return m;
    }

    public MapList<Integer, AcademicCourseAssignment> findCourseAssignments(int periodId) {
        return cacheService.get(AcademicCourseAssignment.class)
                .getProperty("findCourseAssignments:" + periodId, new Action<MapList<Integer, AcademicCourseAssignment>>() {
                    @Override
                    public MapList<Integer, AcademicCourseAssignment> run() {
                        List<AcademicCourseAssignment> assignments = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where a.coursePlan.periodId=:periodId " +
                                " order by a.coursePlan.courseLevel.semester.code,a.coursePlan.courseLevel.academicClass.program.name,a.name,a.courseType.name")
                                .setParameter("periodId", periodId)
//                                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                                .getResultList();

                        return Utils.unmodifiableMapList(new DefaultMapList<Integer, AcademicCourseAssignment>(

                                assignments,
                                AcademicCourseAssignmentIdConverter
                        ));
                    }
                });
    }

//    public List<AcademicCourseIntent> findCourseIntents(int periodId) {
//        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a where a.assignment.coursePlan.periodId=:periodId")
//                .setParameter("periodId", periodId)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
//                .getResultList();
//    }

    public List<AcademicCourseIntent> findCourseIntents(int periodId) {
        return cacheService.get(AcademicCourseIntent.class)
                .getProperty("findCourseIntents:" + periodId, new Action<List<AcademicCourseIntent>>() {
                    @Override
                    public List<AcademicCourseIntent> run() {
                        return UPA.getPersistenceUnit().createQuery("Select u from AcademicCourseIntent u where u.assignment.coursePlan.periodId=:periodId")
//                                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 4)
                                .setParameter("periodId", periodId)
                                .getResultList();
                    }
                });
    }

    public List<AcademicCourseIntent> findCourseIntentsByCoursePlan(int coursePlanId) {
        return cacheService.get(AcademicCourseIntent.class)
                .getProperty("findCourseIntentsByCoursePlan:" + coursePlanId, new Action<List<AcademicCourseIntent>>() {
                    @Override
                    public List<AcademicCourseIntent> run() {
                        return UPA.getPersistenceUnit().createQuery("Select u from AcademicCourseIntent u where u.assignment.coursePlanId=:coursePlanId")
//                                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 4)
                                .setParameter("coursePlanId", coursePlanId)
                                .getResultList();
                    }
                });
    }

    public List<AcademicCoursePlan> findCoursePlans(int periodId) {
        return cacheService.get(AcademicCoursePlan.class)
                .getProperty("findCoursePlans:" + periodId, new Action<List<AcademicCoursePlan>>() {
                    @Override
                    public List<AcademicCoursePlan> run() {
                        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCoursePlan a where a.periodId=:periodId ")
//                                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                                .setParameter("periodId", periodId)
                                .getResultList();
                    }
                });
    }

    public List<AcademicCourseAssignment> findCourseAssignmentsByPlan(int planId) {
        return cacheService.get(AcademicCourseAssignment.class)
                .getProperty("findCourseAssignmentsByPlan:" + planId, new Action<List<AcademicCourseAssignment>>() {
                    @Override
                    public List<AcademicCourseAssignment> run() {
                        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where a.coursePlanId=:v")
                                .setParameter("v", planId)
                                .getResultList();
                    }
                });
    }

    public List<AcademicCourseAssignment> findCourseAssignmentsByClass(int periodId, int classId) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where " +
                "(a.subClassId=:v or a.coursePlan.courseLevel.academicClassId=:v)"
                + " and a.coursePlan.periodId=:periodId"
        )
                .setParameter("periodId", periodId)
                .setParameter("v", classId).getResultList();
    }

    public List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoadsByTeacher(int periodId, int teacherId) {
        Map<Integer, List<AcademicTeacherSemestrialLoad>> map = cacheService.get(AcademicTeacherSemestrialLoad.class)
                .getProperty("findTeacherSemestrialLoadsByTeacher:" + periodId, new Action<Map<Integer, List<AcademicTeacherSemestrialLoad>>>() {
                    @Override
                    public Map<Integer, List<AcademicTeacherSemestrialLoad>> run() {
                        Map<Integer, List<AcademicTeacherSemestrialLoad>> map = new HashMap<Integer, List<AcademicTeacherSemestrialLoad>>();
                        for (AcademicTeacherSemestrialLoad load : findTeacherSemestrialLoadsByPeriod(periodId)) {
                            List<AcademicTeacherSemestrialLoad> list = map.get(load.getTeacher().getId());
                            if (list == null) {
                                list = new ArrayList<AcademicTeacherSemestrialLoad>();
                                map.put(load.getTeacher().getId(), list);
                            }
                            list.add(load);
                        }
                        return map;
                    }
                });
        List<AcademicTeacherSemestrialLoad> list = map.get(teacherId);
        if (list == null) {
            return Collections.EMPTY_LIST;
        }
        return list;
//
//
//        return UPA.getPersistenceUnit().createQuery("Select a from AcademicTeacherSemestrialLoad a where a.teacherId=:t and a.periodId=:periodId")
//                .setParameter("t", teacherId)
//                .setParameter("periodId", periodId)
//                .getResultList();
    }

    public List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoadsByPeriod(int periodId) {
        return cacheService.get(AcademicTeacherSemestrialLoad.class)
                .getProperty("findTeacherSemestrialLoadsByPeriod:" + periodId, new Action<List<AcademicTeacherSemestrialLoad>>() {
                    @Override
                    public List<AcademicTeacherSemestrialLoad> run() {
                        return UPA.getPersistenceUnit().createQuery("Select a from AcademicTeacherSemestrialLoad a where a.periodId=:periodId")
                                .setParameter("periodId", periodId)
                                .getResultList();
                    }
                });
    }

    public AcademicClass findAcademicClass(String t) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicClass a where a.name=:t")
                .setParameter("t", t)
                .getFirstResultOrNull();
    }

    public AcademicClass findAcademicClass(int id) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicClass a where a.id=:t")
                .setParameter("t", id)
                .getFirstResultOrNull();
    }

    public AcademicClass findAcademicClass(int programId, String t) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicClass a where a.name=:t and a.programId=:programId")
                .setParameter("t", t)
                .setParameter("programId", programId)
                .getFirstResultOrNull();
    }

    public List<AcademicClass> findAcademicClasses(String t) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicClass a where a.name=:t")
                .setParameter("t", t)
                .getResultList();
    }

    public void updateAllCoursePlanValuesByLoadValues(int periodId) {
        Chronometer ch = new Chronometer();
        for (AcademicCoursePlan coursePlan : findCoursePlans(periodId)) {
            updateCoursePlanValuesByLoadValues(coursePlan);
        }
        log.log(Level.INFO, "updateAllCoursePlanValuesByLoadValues in {1}", new Object[]{ch.stop()});
    }

    public void updateCoursePlanValuesByLoadValues(int coursePlanId) {
        AcademicCoursePlan p = findCoursePlan(coursePlanId);
        updateCoursePlanValuesByLoadValues(p);
    }

    private void updateCoursePlanValuesByLoadValues(AcademicCoursePlan coursePlan) {
//        Chronometer ch=new Chronometer();
        List<AcademicCourseAssignment> loads = findCourseAssignmentsByPlan(coursePlan.getId());
        double c = 0;
        double td = 0;
        double tp = 0;
        double pm = 0;

        int gc = 0;
        int gtd = 0;
        int gtp = 0;
        int gpm = 0;

        int wc = 0;
        int wtd = 0;
        int wtp = 0;
        int wpm = 0;

        for (AcademicCourseAssignment load : loads) {
            double c0 = load.getValueC();
            double td0 = load.getValueTD();
            double tp0 = load.getValueTP();
            double pm0 = load.getValuePM();
            double g0 = load.getGroupCount() * load.getShareCount();
            int w0 = load.getCourseType().getWeeks();
            c += c0 * g0;
            td += td0 * g0;
            tp += tp0 * g0;
            pm += pm0 * g0;
            if (c0 > 0) {
                gc += g0;
                wc += w0 * g0;
            }
            if (td0 > 0) {
                gtd += g0;
                wtd += w0 * g0;
            }
            if (tp0 > 0) {
                gtp += g0;
                wtp += w0 * g0;
            }
            if (pm0 > 0) {
                gpm += g0;
                wpm += w0 * g0;
            }
        }
        if (gc > 0) {
            c /= gc;
            wc /= gc;
        }
        if (gtd > 0) {
            td /= gtd;
            wtd /= gc;
        }
        if (gtp > 0) {
            tp /= gtp;
            wtp /= gtp;
        }
        if (gpm > 0) {
            pm /= gpm;
            wpm /= gpm;
        }
        double coeff = 2.0 / 3;
        double tppm = tp + pm * coeff;
        int wtppm = (int) (wtp + wpm * coeff);

        coursePlan.setValueC(c);
        coursePlan.setValueTD(td);
        coursePlan.setValueTP(tp);
        coursePlan.setValuePM(pm);
        coursePlan.setValueTPPM(tppm);

        coursePlan.setGroupCountC(gc);
        coursePlan.setGroupCountTD(gtd);
        coursePlan.setGroupCountTP(gtp);
        coursePlan.setGroupCountPM(gpm);
        coursePlan.setGroupCountTPPM(gtp + gpm);

        coursePlan.setWeeksC(wc);
        coursePlan.setWeeksTD(wtd);
        coursePlan.setWeeksTP(wtp);
        coursePlan.setWeeksPM(wpm);
        coursePlan.setWeeksTPPM(wtppm);

        update(coursePlan);
//        log.log(Level.INFO,"updateCoursePlanValuesByLoadValues in {1}",new Object[]{ch.stop()});
    }

    public AcademicCoursePlan findCoursePlan(int id) {
        return (AcademicCoursePlan) UPA.getPersistenceUnit()
                .createQueryBuilder(AcademicCoursePlan.class)
                .byField("id", id)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                .getFirstResultOrNull();
    }

    ////////////////////////////////////////////////////////////////////////////
    public AppPeriod addAcademicYearSnapshot(String year, String snapshotName) {
        AppPeriod s = createAcademicYear(year, snapshotName);
//        AppPeriod s = new AppPeriod();
//        s.setCreationTime(new DateTime());
//        s.setName(y.getName());
//        s.setSnapshotName(snapshotName);
//        UPA.getPersistenceUnit().persist(s);
        int periodId = s.getId();
        Map<String, AcademicHistTeacherDegree> histTeacherDegreeMap = new HashMap<>();
        for (AcademicTeacherDegree m : findTeacherDegrees()) {
            AcademicHistTeacherDegree h = new AcademicHistTeacherDegree();
            h.setCode(m.getCode());
            h.setName(m.getName());
            h.setName2(m.getName2());
            h.setPosition(m.getPosition());
            h.setConversionRule(m.getConversionRule());
            h.setAcademicYear(s);
            histTeacherDegreeMap.put(h.getCode(), h);
            add(h);
        }
        Map<Integer, AcademicHistTeacherAnnualLoad> teacherToLoad = new HashMap<>();
        for (AcademicTeacher m : findTeachers()) {
            AcademicHistTeacherAnnualLoad h = new AcademicHistTeacherAnnualLoad();
            h.setAcademicYear(s);
            AcademicTeacherPeriod ts = findAcademicTeacherPeriod(periodId, m);
            h.setDegree(histTeacherDegreeMap.get(ts.getDegree() == null ? null : ts.getDegree().getName()));
            h.setSituation(ts.getSituation());
            h.setTeacher(m);
            add(h);
            teacherToLoad.put(m.getId(), h);
        }
        for (AcademicTeacherSemestrialLoad m : findTeacherSemestrialLoadsByPeriod(periodId)) {
            AcademicHistTeacherSemestrialLoad h = new AcademicHistTeacherSemestrialLoad();
            h.setAcademicYear(s);
            h.setAnnualLoad(teacherToLoad.get(m.getTeacher().getId()));
            h.setSemester(m.getSemester());
            h.setWeeksLoad(m.getWeeksLoad());
            add(h);
        }

        Map<Integer, AcademicHistProgram> academicHistCoursePrograms = new HashMap<>();
        for (AcademicProgram m : findPrograms()) {
            AcademicHistProgram h = new AcademicHistProgram();
            h.setAcademicYear(s);
            h.setName(m.getName());
            h.setDepartment(m.getDepartment());
            h.setName(m.getName());
            h.setName2(m.getName2());
            add(h);
            academicHistCoursePrograms.put(m.getId(), h);
        }

        Map<Integer, AcademicHistCourseGroup> academicHistCourseGroups = new HashMap<>();
        for (AcademicCourseGroup m : findCourseGroups(periodId)) {
            AcademicHistCourseGroup h = new AcademicHistCourseGroup();
            h.setAcademicYear(s);
            h.setAcademicClass(m.getAcademicClass());
            add(h);
            academicHistCourseGroups.put(m.getId(), h);
        }

        for (AcademicCoursePlan m : findCoursePlans(periodId)) {
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
            h.setGroupCountTPPM(m.getGroupCountTPPM());

            h.setWeeksC(m.getWeeksC());
            h.setWeeksPM(m.getWeeksPM());
            h.setWeeksTD(m.getWeeksTD());
            h.setWeeksTP(m.getWeeksTP());
            h.setWeeksPM(m.getWeeksPM());
            h.setWeeksTPPM(m.getWeeksTPPM());

            h.setValueC(m.getValueC());
            h.setValuePM(m.getValuePM());
            h.setValueTD(m.getValueTD());
            h.setValueTP(m.getValueTP());
            h.setValuePM(m.getValuePM());
            h.setValueTPPM(m.getValueTPPM());

            h.setStudentClass(m.getCourseLevel().getAcademicClass());
            h.setSemester(m.getCourseLevel().getSemester());
            add(h);
        }
        return s;
    }

    public AcademicTeacherPeriod findAcademicTeacherPeriod(final int periodId, AcademicTeacher t) {
        Map<Integer, AcademicTeacherPeriod> m = cacheService.get(AcademicTeacherPeriod.class).getProperty("findAcademicTeacherPeriodByTeacher:" + periodId, new Action<Map<Integer, AcademicTeacherPeriod>>() {
            @Override
            public Map<Integer, AcademicTeacherPeriod> run() {

                List<AcademicTeacherPeriod> ret =
                        UPA.getPersistenceUnit()
                                .createQueryBuilder(AcademicTeacherPeriod.class)
                                .setEntityAlias("o")
                                .byExpression("o.periodId=:periodId")
//                                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 4)
                                .setParameter("periodId", periodId)
                                .getResultList();
                Map<Integer, AcademicTeacherPeriod> t = new HashMap<Integer, AcademicTeacherPeriod>();
                for (AcademicTeacherPeriod o : ret) {
                    t.put(o.getTeacher().getId(), o);
                }
                return t;
            }
        });
        AcademicTeacherPeriod p = m.get(t.getId());
        if (p != null) {
            return p;
        }

        AcademicTeacherPeriod a = new AcademicTeacherPeriod();
        a.setId(-1);
        a.setTeacher(t);
        a.setSituation(t.getSituation());
        a.setDegree(t.getDegree());
        a.setDepartment(t.getDepartment());
//        a.setEnabled(t.isEnabled());
        a.setPeriod(VrApp.getBean(CorePlugin.class).findPeriod(periodId));
        return a;
    }

    public double evalHistValueEquiv(int yearId, LoadValue value, String degree) {
        AcademicHistTeacherDegree dd = findHistTeacherDegree(yearId, degree);
        return evalHistValueEquiv(value, dd);
    }

    public double evalHistValueEquiv(LoadValue v, AcademicHistTeacherDegree dd) {
        return dd.getValueC() * v.getC()
                + dd.getValueTD() * v.getTd()
                + dd.getValueTP() * v.getTp()
                + dd.getValuePM() * v.getPm();
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
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherSemestrialLoad where academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherAnnualLoad where academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherDegree where academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseAssignment a where a.coursePlan.academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCoursePlan a where a.academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseGroup a where a.courseLevel.program.academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistProgram a where a.academiYear=:y").setParameter("y", year).executeNonQuery();
        trace.trace("resetAcademicYear", "reset Academic Year", String.valueOf(year), "academicPlugin", Level.FINE);
    }

    public void resetHistAcademicYears() {
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherSemestrialLoad").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherAnnualLoad").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherDegree").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseAssignment").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCoursePlan").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseGroup").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistProgram").executeNonQuery();
        trace.trace("resetHistAcademicYears", "reset Academic Years", "", "academicPlugin", Level.FINE);
    }

    public List<AcademicHistTeacherAnnualLoad> findHistTeacherAnnualLoads(int year) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistTeacherAnnualLoad a where a.academicYearId=:v")
                .setParameter("v", year)
                .getResultList();
    }

    public AcademicHistTeacherAnnualLoad findHistTeacherAnnualLoad(int year, int teacherId) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistTeacherAnnualLoad a where a.academicYearId=:v and a.teacherId=:t")
                .setParameter("v", year)
                .setParameter("t", teacherId)
                .getFirstResultOrNull();
    }

    public List<AcademicHistTeacherSemestrialLoad> findHistTeacherSemestrialLoads(int year, int teacherId) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicTeacherSemestrialLoad a where a.annualLoad.academicYearId=:v and a.annualLoad.teacherId=:t")
                .setParameter("v", year)
                .setParameter("t", teacherId)
                .getResultList();
    }

    public List<AcademicHistTeacherSemestrialLoad> findHistTeacherSemestrialLoads(int year) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistTeacherSemestrialLoad a where a.annualLoad.academicYearId=:v")
                .setParameter("v", year)
                .getResultList();
    }

    public List<AcademicHistCourseAssignment> findHistCourseAssignments(int yearId) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistCourseAssignment a where a.coursePlan.academicYearId=:v")
                .setParameter("v", yearId).getResultList();
    }

    public List<AcademicHistCoursePlan> findHistCoursePlans(int year) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistCoursePlan a where a.academicYearId=:v")
                .setParameter("v", year).getResultList();
    }

    public String formatDisciplinesNames(String value, boolean autoCreate) {
        StringBuilder s = new StringBuilder();
        for (String n : parseDisciplinesNames(value, autoCreate)) {
            if (s.length() > 0) {
                s.append(", ");
            }
            s.append(n);
        }
        return s.toString();
    }

    public Set<String> parseDisciplinesNames(String value, boolean autoCreate) {
        TreeSet<String> vals = new TreeSet<>();
        for (AcademicDiscipline d : parseDisciplines(value, autoCreate)) {
            vals.add(d.getName());
        }
        return vals;
    }

    public List<AcademicDiscipline> parseDisciplines(String value, boolean autoCreate) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (value == null) {
            value = "";
        }
        List<AcademicDiscipline> ok = new ArrayList<>();
        for (String n : value.split(",|;| ")) {
            String[] cn = codeAndName(n);
            String code = cn[0].toLowerCase();
            AcademicDiscipline t = pu.findByMainField(AcademicDiscipline.class, code);
            if (t == null && autoCreate) {
                if (!StringUtils.isEmpty(code)) {
                    t = new AcademicDiscipline();
                    t.setCode(code);
                    t.setName(cn[1]);
                    pu.persist(t);
                }
            }
            if (t != null) {
                ok.add(t);
            }
        }
        return ok;
    }

    public String formatDisciplinesForLocale(String value, String locale) {
        StringBuilder sb = new StringBuilder();
        for (String s : parseDisciplinesForLocale(value, locale)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public List<String> parseDisciplinesForLocale(String value, String locale) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (value == null) {
            value = "";
        }
        List<String> ok = new ArrayList<>();
        for (String n : value.split(",|;| ")) {
            String[] cn = codeAndName(n);
            String code = cn[0].toLowerCase();
            AcademicDiscipline t = pu.findByMainField(AcademicDiscipline.class, code);
            if (t == null) {
                if (!StringUtils.isEmpty(code)) {
                    ok.add(cn[1]);
                }
            } else {
                ok.add(VrUtils.getValidString(locale, t.getName(), t.getName2(), t.getName3()));
            }
        }
        return ok;
    }

    public List<AcademicDiscipline> parseDisciplinesZombies(String value) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (value == null) {
            value = "";
        }
        List<AcademicDiscipline> ok = new ArrayList<>();
        for (String n : value.split(",|;| ")) {
            String[] cn = codeAndName(n);
            String code = cn[0].toLowerCase();
            AcademicDiscipline t = pu.findByMainField(AcademicDiscipline.class, code);
            if (t == null) {
                if (!StringUtils.isEmpty(code)) {
                    t = new AcademicDiscipline();
                    t.setId(-1);
                    t.setCode(code);
                    t.setName(cn[1]);
                }
            }
            if (t != null) {
                ok.add(t);
            }
        }
        return ok;
    }

    public List<String> parseWords(String value) {
        if (value == null) {
            value = "";
        }
        List<String> ok = new ArrayList<>();
        for (String n : value.split(",|;| ")) {
            String[] cn = codeAndName(n);
            String code = cn[0].toLowerCase();
            if (!StringUtils.isEmpty(code)) {
                ok.add(code);
            }
        }
        return ok;
    }

    public AcademicTeacher getCurrentTeacher() {
        UserSession sm = core.getUserSession();
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user != null) {
            return findTeacherByUser(user.getId());
        }
        return null;
    }

    public AcademicStudent getCurrentStudent() {
        UserSession sm = core.getUserSession();
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user != null) {
            return findStudentByUser(user.getId());
        }
        return null;
    }

    @Start
    private void startService() {
        core.getManagerProfiles().add("Director");
        core.getManagerProfiles().add("DirectorOfStudies");
    }

    @Install
    private void installService() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        core.createRight("Custom.Education.CourseLoadUpdateIntents", "Mettre à jours les voeux de autres");
        core.createRight("Custom.Education.CourseLoadUpdateAssignments", "Mettre à jours les affectations");
        core.createRight("Custom.Education.AllTeachersCourseLoad", "Charge tous enseignats");
        core.createRight("Custom.Education.MyCourseLoad", "Ma charge");
        core.createRight("Custom.Education.TeacherCourseLoad", "Charge Detaillee");
        core.createRight("Custom.Education.GlobalStat", "Stat Charge");
        core.createRight("Custom.Education.CourseLoadUpdateIntents", "Mettre à jours les voeux de autres");
        core.createRight("Custom.Education.CourseLoadUpdateAssignments", "Mettre à jours les affectations");
        core.createRight("Custom.FileSystem.RootFileSystem", "Systeme de Fichier Racine");
        core.createRight("Custom.FileSystem.MyFileSystem", "Systeme de Fichier Utilisateur");
        core.createRight("Custom.Education.TeacherPlanning", "TeacherPlanning");
        core.createRight("Custom.Education.MyPlanning", "MyPlanning");
        core.createRight("Custom.Education.ClassPlanning", "ClassPlanning");

        core.getOrCreateAppPropertyValue("System.App.Title",null,"Eniso.info");
        core.getOrCreateAppPropertyValue("System.App.Description",null,"ENISo Computer Science Department Web Site");
        core.getOrCreateAppPropertyValue("System.App.Keywords",null,"eniso");
        core.getOrCreateAppPropertyValue("System.App.Title.Major.Main",null,"Eniso");
        core.getOrCreateAppPropertyValue("System.App.Title.Major.Secondary",null,"info");
        core.getOrCreateAppPropertyValue("System.App.Title.Minor.Main",null,"Eniso");
        core.getOrCreateAppPropertyValue("System.App.Title.Minor.Secondary",null,"info");
        core.getOrCreateAppPropertyValue("System.App.Copyrights.Date",null,"2015-2017");
        core.getOrCreateAppPropertyValue("System.App.Copyrights.Author.Name",null,"Taha Ben Salah");
        core.getOrCreateAppPropertyValue("System.App.Copyrights.Author.URL",null,"http://tahabensalah.net");
        core.getOrCreateAppPropertyValue("System.App.Copyrights.Author.Affiliation",null,"ENISo");
        core.getOrCreateAppPropertyValue("System.App.GotoWelcomeText",null,"My Space");
        core.getOrCreateAppPropertyValue("System.App.GotoLoginText",null,"My Space");

        ArticlesDisposition a = core.findArticleDisposition("Activities");
        if(a!=null){
            a.setActionName("Activities");
            a.setEnabled(true);
            pu.merge(a);
        }
        a = core.findArticleDisposition("News");
        if(a!=null){
            a.setActionName("Press");
            a.setEnabled(true);
            pu.merge(a);
        }

        a = core.findArticleDisposition("Testimonials");
        if(a!=null){
            a.setActionName("Testimonials");
            a.setEnabled(true);
            pu.merge(a);
        }

        a = core.findArticleDisposition("Education");
        if(a!=null){
            a.setActionName("Studies");
            a.setEnabled(true);
            pu.merge(a);
        }

        a = core.findArticleDisposition("Featured");
        if(a!=null){
            a.setActionName("Featured");
            a.setEnabled(true);
            pu.merge(a);
        }

        a = core.findArticleDisposition("About");
        if(a!=null){
            a.setActionName("About");
            a.setEnabled(true);
            pu.merge(a);
        }


        AppUserType teacherType;
        teacherType = new AppUserType();
        teacherType.setName("Teacher");
        teacherType = core.findOrCreate(teacherType);

        AppUserType studentType;
        studentType = new AppUserType();
        studentType.setName("Student");
        studentType = core.findOrCreate(studentType);

        AppProfile teacherProfile = core.findOrCreateCustomProfile("Teacher", "UserType");

        core.addProfileRight(teacherProfile.getId(), "Custom.Education.MyCourseLoad");
        core.addProfileRight(teacherProfile.getId(), "AcademicCourseIntent.Persist");
        core.addProfileRight(teacherProfile.getId(), "AcademicCourseIntent.Remove");
//        core.addProfileRight(teacherProfile.getId(), "AppContact.DefaultEditor");
        core.addProfileRight(teacherProfile.getId(), "AppContact.Load");
        core.addProfileRight(teacherProfile.getId(), "AcademicCoursePlan.Navigate");
        core.addProfileRight(teacherProfile.getId(), "Custom.FileSystem.MyFileSystem");
        for (String navigateOnlyEntity : new String[]{"AppContact"}) {
            core.addProfileRight(teacherProfile.getId(), navigateOnlyEntity + ".Navigate");
        }
        for (String readOnlyEntity : new String[]{"AcademicTeacher", "AcademicClass", "AcademicCoursePlan", "AcademicCourseLevel", "AcademicCourseGroup", "AcademicCourseType", "AcademicProgram", "AcademicDiscipline", "AcademicStudent"
                //,"AcademicCourseAssignment"
        }) {
            core.addProfileRight(teacherProfile.getId(), readOnlyEntity + ".Navigate");
            core.addProfileRight(teacherProfile.getId(), readOnlyEntity + ".DefaultEditor");
        }
        AppProfile studentProfile = core.findOrCreateCustomProfile("Student", "UserType");

        core.addProfileRight(studentProfile.getId(), "Custom.FileSystem.MyFileSystem");

        AppProfile headOfDepartment;
        headOfDepartment = core.findOrCreateCustomProfile(CorePlugin.PROFILE_HEAD_OF_DEPARTMENT, "UserType");

        core.addProfileRight(headOfDepartment.getId(), "Custom.Education.TeacherCourseLoad");
        core.addProfileRight(headOfDepartment.getId(), "Custom.Education.GlobalStat");
        core.addProfileRight(headOfDepartment.getId(), "Custom.Education.AllTeachersCourseLoad");
        core.addProfileRight(headOfDepartment.getId(), "Custom.Education.CourseLoadUpdateIntents");
        core.addProfileRight(headOfDepartment.getId(), "Custom.Education.CourseLoadUpdateAssignments");
        core.addProfileRight(headOfDepartment.getId(), "Custom.FileSystem.MyFileSystem");

        for (net.vpc.upa.Entity ee : pu.getPackage("Education").getEntities(true)) {
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Persist");
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Remove");
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Update");
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Navigate");
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Load");
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".DefaultEditor");
        }

        AppProfile directorOfStudies = core.findOrCreateProfile("DirectorOfStudies");
        directorOfStudies.setCustom(true);
        directorOfStudies.setCustomType("UserType");
        pu.merge(directorOfStudies);

        core.addProfileRight(directorOfStudies.getId(), "Custom.Education.TeacherCourseLoad");
        core.addProfileRight(directorOfStudies.getId(), "Custom.Education.GlobalStat");
        core.addProfileRight(directorOfStudies.getId(), "Custom.Education.AllTeachersCourseLoad");
        core.addProfileRight(directorOfStudies.getId(), "Custom.FileSystem.MyFileSystem");

        for (net.vpc.upa.Entity ee : pu.getPackage("Education").getEntities(true)) {
            core.addProfileRight(directorOfStudies.getId(), ee.getAbsoluteName() + ".Navigate");
            core.addProfileRight(directorOfStudies.getId(), ee.getAbsoluteName() + ".Load");
            core.addProfileRight(directorOfStudies.getId(), ee.getAbsoluteName() + ".DefaultEditor");
        }

        AppProfile director = core.findOrCreateCustomProfile("Director", "UserType");

        core.addProfileRight(director.getId(), "Custom.Education.TeacherCourseLoad");
        core.addProfileRight(director.getId(), "Custom.Education.GlobalStat");
        core.addProfileRight(director.getId(), "Custom.Education.AllTeachersCourseLoad");
        core.addProfileRight(director.getId(), "Custom.FileSystem.MyFileSystem");

        for (net.vpc.upa.Entity ee : pu.getPackage("Education").getEntities(true)) {
            core.addProfileRight(director.getId(), ee.getAbsoluteName() + ".Navigate");
            core.addProfileRight(director.getId(), ee.getAbsoluteName() + ".Load");
            core.addProfileRight(director.getId(), ee.getAbsoluteName() + ".DefaultEditor");
        }
        AppConfig appConfig = core.getCurrentConfig();
        if (appConfig != null) {
            AppPeriod mainPeriod = appConfig.getMainPeriod();
            if (mainPeriod != null) {
                List<AcademicCoursePlan> academicCoursePlanList = pu.findAll(AcademicCoursePlan.class);
                for (AcademicCoursePlan p : academicCoursePlanList) {
                    if (p.getPeriod() == null) {
                        p.setPeriod(mainPeriod);
                        pu.createUpdateQuery(p).update("period").execute();
                    }
                }
                List<AcademicTeacherSemestrialLoad> academicTeacherSemestrialLoadList = pu.findAll(AcademicTeacherSemestrialLoad.class);
                for (AcademicTeacherSemestrialLoad p : academicTeacherSemestrialLoadList) {
                    if (p.getPeriod() == null) {
                        p.setPeriod(mainPeriod);
                        pu.createUpdateQuery(p).update("period").execute();
                    }
                }
                for (AcademicTeacher academicTeacher : findTeachers()) {
                    updateTeacherPeriod(mainPeriod.getId(), academicTeacher.getId(), -1);
                }
            }
        }
    }


    public void copyAcademicData(int fromPeriodId, int toPeriodId) {
        copyAcademicDataHelper.copyAcademicData(fromPeriodId, toPeriodId);
    }

    private String[] codeAndName(String s) {
        if (s == null) {
            s = "";
        }
        String code = null;
        String name = null;
        int eq = s.indexOf('=');
        if (eq >= 0) {
            code = s.substring(0, eq);
            name = s.substring(eq + 1);
        } else {
            code = s;
            name = s;
        }
        return new String[]{code, name};
    }

    public void importTeachingLoad(int periodId) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        try {
            AppPeriod mainPeriod = core.findPeriodOrMain(periodId);
            String year = mainPeriod.getName();
            String version = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.version", null, "v01");
            String dir = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.configFolder", null, "/Config/Import/${year}");
            Map<String, String> vars = new HashMap<>();
            vars.put("home", System.getProperty("user.home"));
            vars.put("year", year);
            vars.put("version", version);

            dir = StringUtils.replaceDollarPlaceHolders(dir, new MapStringConverter(vars));

            String dataFolder = dir + "/data";

            AcademicPlugin s = VrApp.getBean(AcademicPlugin.class);

            net.vpc.common.vfs.VirtualFileSystem fs = core.getFileSystem();
            s.resetModuleTeaching();
            s.importFile(mainPeriod.getId(),
                    fs.get(dataFolder),
                    new ImportOptions()
            );
        } catch (Exception ex) {
            Logger.getLogger(XlsxLoadImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addUserForTeacher(AcademicTeacher academicTeacher) {
        AppUserType teacherType = VrApp.getBean(CorePlugin.class).findUserType("Teacher");
        AppUser u = core.createUser(academicTeacher.getContact(), teacherType.getId(), academicTeacher.getDepartment().getId(), false, new String[]{"Teacher"});
        academicTeacher.setUser(u);
        UPA.getPersistenceUnit().merge(academicTeacher);
    }

    public boolean addUserForStudent(AcademicStudent academicStudent) {
        AppUserType teacherType = VrApp.getBean(CorePlugin.class).findUserType("Student");
        AppUser u = core.createUser(academicStudent.getContact(), teacherType.getId(), academicStudent.getDepartment().getId(), false, new String[]{"Student"});
        academicStudent.setUser(u);
        UPA.getPersistenceUnit().merge(academicStudent);
        for (AcademicClass c : new AcademicClass[]{academicStudent.getLastClass1(), academicStudent.getLastClass2(), academicStudent.getLastClass3()}) {
            if (c != null) {
                String s = c.getName();
                s = core.validateProfileName(s);
                AppProfile p = core.findOrCreateCustomProfile(s, "AcademicClass");
                core.userAddProfile(u.getId(), p.getCode());
            }

            AcademicProgram pr = academicStudent.getLastClass1() == null ? null : academicStudent.getLastClass1().getProgram();
            if (pr != null) {
                String s = pr.getName();
                s = core.validateProfileName(s);
                AppProfile p = core.findOrCreateCustomProfile(s, "AcademicClass");
                core.userAddProfile(u.getId(), p.getCode());
            }
        }
        AppDepartment d = academicStudent.getDepartment();
        if (d != null) {
            String s = d.getCode();
            s = core.validateProfileName(s);
            AppProfile p = core.findOrCreateCustomProfile(s, "Department");
            core.userAddProfile(u.getId(), p.getCode());
        }

        return true;
    }

    public AppPeriod findAcademicYear(String name, String snapshot) {
        return (AppPeriod) UPA.getPersistenceUnit()
                .createQuery("Select a from AppPeriod a where a.name=:t and a.snapshotName=:s")
                .setParameter("t", name)
                .setParameter("s", snapshot)
                .getFirstResultOrNull();
    }

    public AppPeriod findAcademicYear(int id) {
        return (AppPeriod) UPA.getPersistenceUnit()
                .findById(AppPeriod.class, id);
    }

    public AcademicBac findAcademicBac(int id) {
        return (AcademicBac) UPA.getPersistenceUnit()
                .findById(AcademicBac.class, id);
    }

    public AcademicBac findAcademicBac(String name) {
        return (AcademicBac) UPA.getPersistenceUnit()
                .findByField(AcademicBac.class, "name", name);
    }

    public AcademicPreClass findAcademicPreClass(int id) {
        return (AcademicPreClass) UPA.getPersistenceUnit()
                .findById(AcademicPreClass.class, id);
    }

    public AcademicInternshipGroup findInternshipGroup(int id) {
        return (AcademicInternshipGroup) UPA.getPersistenceUnit()
                .findById(AcademicInternshipGroup.class, id);
    }

    public AcademicPreClassType findAcademicPreClassType(int id) {
        return (AcademicPreClassType) UPA.getPersistenceUnit()
                .findById(AcademicPreClassType.class, id);
    }

    public AcademicPreClass findAcademicPreClass(String name) {
        return (AcademicPreClass) UPA.getPersistenceUnit()
                .findByField(AcademicPreClass.class, "name", name);
    }

    public AcademicPreClassType findAcademicPreClassType(String name) {
        return (AcademicPreClassType) UPA.getPersistenceUnit()
                .findByField(AcademicPreClassType.class, "name", name);
    }

    public AppPeriod findAcademicYearSnapshot(String t, String snapshotName) {
        return (AppPeriod) UPA.getPersistenceUnit()
                .createQuery("Select a from AppPeriod a where a.name=:t and a.snapshotName=:s")
                .setParameter("t", t)
                .setParameter("s", snapshotName)
                .getFirstResultOrNull();
    }

    public List<AppPeriod> findAcademicYearSnapshots(String t) {
        return UPA.getPersistenceUnit()
                .createQuery("Select a from AppPeriod a where a.name=:t and a.snapshotName!=null")
                .setParameter("t", t)
                .getResultList();
    }

    public AppPeriod findOrCreateAcademicYear(String academicYearName, String snapshot) {
        AppPeriod z = findAcademicYear(academicYearName, snapshot);
        if (z == null) {
            z = new AppPeriod();
            z.setName(academicYearName);
            z.setSnapshotName(snapshot);
            add(z);
        }
        return z;
    }

    public AppPeriod createAcademicYear(String academicYearName, String snapshot) {
        AppPeriod z = findAcademicYear(academicYearName, snapshot);
        if (z != null) {
            throw new IllegalArgumentException("Already exists");
        }
        z = new AppPeriod();
        z.setName(academicYearName);
        z.setSnapshotName(snapshot);
        add(z);

        return z;
    }

    public void add(Object t) {
        if (t instanceof AppPeriod) {
            AppPeriod a = (AppPeriod) t;
            a.setCreationTime(new DateTime());
            a.setSnapshotName(null);
        }
        UPA.getPersistenceUnit().persist(t);
    }

    @Override
    public Map<String, Object> getExtendedPropertyValues(Object o) {
        if (o instanceof AppUser) {
            AcademicTeacher t = findTeacherByUser(((AppUser) o).getId());
            if (t != null) {
                AppConfig appConfig = core.getCurrentConfig();
                if (appConfig != null && appConfig.getMainPeriod() != null) {
                    AcademicTeacherPeriod pp = findAcademicTeacherPeriod(appConfig.getMainPeriod().getId(), t);
                    HashMap<String, Object> m = new HashMap<>();
                    m.put("discipline", t.getDiscipline());
                    m.put("degree", pp.getDegree() == null ? null : pp.getDegree().getName());
                    m.put("degreeCode", pp.getDegree() == null ? null : pp.getDegree().getCode());
                    m.put("situation", pp.getSituation() == null ? null : pp.getSituation().getName());
                    m.put("enabled", pp.isEnabled());
                    return m;
                }
            }
        }
        return null;
    }

    @Override
    public Set<String> getExtendedPropertyNames(Class o) {
        if (o.equals(AppUser.class)) {
            return new HashSet<>(Arrays.asList("discipline", "degree", "degreeCode", "situation", "enabled"));
        }
        return null;
    }

    public String getValidName(AcademicTeacher t) {
        if (t == null) {
            return "";
        }
        String name = null;
        if (t.getContact() != null) {
            name = t.getContact().getFullName();
        }
        if (StringUtils.isEmpty(name) && t.getUser() != null) {
            name = t.getUser().getLogin();
        }
        if (StringUtils.isEmpty(name)) {
            name = "Teacher #" + t.getId();
        }
        return (name);
    }

    public String getValidName(AcademicStudent t) {
        if (t == null) {
            return "";
        }
        String name = null;
        if (t.getContact() != null) {
            name = t.getContact().getFullName();
        }
        if (StringUtils.isEmpty(name) && t.getUser() != null) {
            name = t.getUser().getLogin();
        }
        if (StringUtils.isEmpty(name)) {
            name = "Teacher #" + t.getId();
        }
        return (name);
    }

    public List<AcademicClass> findAcademicUpHierarchyList(AcademicClass[] classes, Map<Integer, AcademicClass> allClasses) {
        HashSet<Integer> visited = new HashSet<>();
        if (allClasses == null) {
            allClasses = findAcademicClassesMap();
        }
        List<AcademicClass> result = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        for (AcademicClass c : classes) {
            if (c != null) {
                stack.push(c.getId());
            }
        }
        while (!stack.isEmpty()) {
            int i = stack.pop();
            if (!visited.contains(i)) {
                AcademicClass c = allClasses.get(i);
                if (c != null) {
                    result.add(c);
                    AcademicClass p = c.getParent();
                    if (p != null) {
                        stack.push(p.getId());
                    }
                }
            }
        }
        return result;
    }

    public Set<Integer> findAcademicDownHierarchyIdList(int classId, Map<Integer, AcademicClass> allClasses) {
        return cacheService.get(AcademicClass.class)
                .getProperty("AcademicDownHierarchyIdList." + classId, new Action<Set<Integer>>() {
                    @Override
                    public Set<Integer> run() {
                        return findAcademicDownHierarchyIdList0(classId, allClasses);
                    }
                });
    }

    public Set<Integer> findAcademicDownHierarchyIdList0(int classId, Map<Integer, AcademicClass> allClasses) {
        HashSet<Integer> visited = new HashSet<>();

        Set<Integer> result = new HashSet<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(classId);
        while (!stack.isEmpty()) {
            int i = stack.pop();
            if (!visited.contains(i)) {
                visited.add(i);
                if (allClasses == null) {
                    allClasses = findAcademicClassesMap();
                }
                AcademicClass c = allClasses.get(i);
                if (c != null) {
                    result.add(c.getId());
                    for (AcademicClass p : allClasses.values()) {
                        if (p.getParent() != null && p.getParent().getId() == c.getId()) {
                            stack.push(p.getId());
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<AcademicClass> findAcademicDownHierarchyList(AcademicClass[] classes, Map<Integer, AcademicClass> allClasses) {
        if (allClasses == null) {
            allClasses = findAcademicClassesMap();
        }
        List<AcademicClass> result = new ArrayList<>();
        Set<Integer> baseIds = new HashSet<>();
        for (AcademicClass aClass : classes) {
            if (aClass != null) {
                baseIds.addAll(findAcademicDownHierarchyIdList(aClass.getId(), allClasses));
            }
        }
        for (Integer baseId : baseIds) {
            result.add(allClasses.get(baseId));
        }
        return result;
    }

    public List<AcademicPreClass> findAcademicDownHierarchyList(AcademicPreClass[] classes, Map<Integer, AcademicPreClass> allClasses) {
        HashSet<Integer> visited = new HashSet<>();
        if (allClasses == null) {
            allClasses = findAcademicPreClassesMap();
        }
        List<AcademicPreClass> result = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        for (AcademicPreClass c : classes) {
            if (c != null) {
                stack.push(c.getId());
            }
        }
        while (!stack.isEmpty()) {
            int i = stack.pop();
            if (!visited.contains(i)) {
                AcademicPreClass c = allClasses.get(i);
                if (c != null) {
                    result.add(c);
                    for (AcademicPreClass p : allClasses.values()) {
                        if (p.getParent() != null && p.getParent().getId() == c.getId()) {
                            stack.push(p.getId());
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<AcademicBac> findAcademicDownHierarchyList(AcademicBac[] classes, Map<Integer, AcademicBac> allClasses) {
        HashSet<Integer> visited = new HashSet<>();
        if (allClasses == null) {
            allClasses = findAcademicBacsMap();
        }
        List<AcademicBac> result = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        for (AcademicBac c : classes) {
            if (c != null) {
                stack.push(c.getId());
            }
        }
        while (!stack.isEmpty()) {
            int i = stack.pop();
            if (!visited.contains(i)) {
                AcademicBac c = allClasses.get(i);
                if (c != null) {
                    result.add(c);
                    for (AcademicBac p : allClasses.values()) {
                        if (p.getParent() != null && p.getParent().getId() == c.getId()) {
                            stack.push(p.getId());
                        }
                    }
                }
            }
        }
        return result;
    }

    public void validateAcademicData_Teacher(int teacherId, int periodId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicTeacher s = findTeacher(teacherId);
        Map<Integer, AcademicClass> academicClasses = findAcademicClassesMap();
        AppUser u = s.getUser();
        AppContact c = s.getContact();
        AppDepartment d = s.getDepartment();

        if (c == null && u != null) {
            c = u.getContact();
        }
        if (d == null && u != null) {
            d = u.getDepartment();
        }
        if (s.getDepartment() == null && d != null) {
            s.setDepartment(d);
            UPA.getPersistenceUnit().merge(s);
        }
        if (s.getContact() == null && c != null) {
            s.setContact(c);
            UPA.getPersistenceUnit().merge(s);
        }
        if (u != null) {

            if (u.getDepartment() == null && d != null) {
                u.setDepartment(d);
                UPA.getPersistenceUnit().merge(u);
            }
            if (u.getContact() == null && c != null) {
                u.setContact(c);
                UPA.getPersistenceUnit().merge(u);
            }
        }


        int ss = findSemesters().size();
        for (int i = 1; i < ss + 1; i++) {
            addAcademicTeacherSemestrialLoad(
                    i, getSemesterMaxWeeks()
                    , s.getId()
                    , periodId
            );
        }


        if (c != null) {
            HashSet<Integer> goodProfiles = new HashSet<>();
            String depCode = null;
            {
                if (d != null) {
                    String n = core.validateProfileName(d.getCode());
                    depCode = d.getCode();
                    AppProfile p = core.findOrCreateCustomProfile(n, "Department");
                    goodProfiles.add(p.getId());
                }
            }
            {
                AppProfile p = core.findOrCreateCustomProfile("Teacher", "UserType");
                goodProfiles.add(p.getId());
            }
            //find classes teached by  teacher
            List<AcademicClass> myClasses = new ArrayList<>();
            Set<String> myPrograms = new HashSet<>();
            for (AcademicCourseAssignment a : findCourseAssignments(periodId, s.getId(), new DefaultCourseAssignmentFilter().setAcceptIntents(false))) {
                AcademicProgram academicProgram = a.resolveProgram();
                if(academicProgram!=null){
                    myPrograms.add(academicProgram.getName());
                }
                myClasses.add(a.resolveAcademicClass());//not to force loading of sub class!
            }

            for (AcademicClass ac : findAcademicUpHierarchyList(myClasses.toArray(new AcademicClass[myClasses.size()]), academicClasses)) {
                if (ac != null) {
                    String n = core.validateProfileName(ac.getName());
                    //ignore inherited profiles in suffix
//                        classNames.add(n);
                    AppProfile p = core.findOrCreateCustomProfile(n, "AcademicClass");
                    goodProfiles.add(p.getId());

                    AcademicProgram pr = ac.getProgram();
                    if (pr != null) {
                        myPrograms.add(pr.getName());
                    }
                }
            }
            for (String myProgram : myPrograms) {
                String n = core.validateProfileName(myProgram);
                AppProfile p = core.findOrCreateCustomProfile(n, "AcademicProgram");
                goodProfiles.add(p.getId());
            }

//                                    n = a.getCoursePlan().getStudentClass().getName();
//                    p = core.findOrCreateCustomProfile(n, "AcademicClass");
//                    goodProfiles.add(p.getId());
            boolean perm = false;
            List<AppProfile> oldProfiles = u == null ? new ArrayList<AppProfile>() : core.findProfilesByUser(u.getId());
            for (AppProfile op : oldProfiles) {
                if ("Permanent".equals(op.getName())) {
                    perm = true;
                    break;
                }
            }

            AcademicTeacherPeriod academicTeacherPeriod = findAcademicTeacherPeriod(periodId, s);
            String degreeCode = academicTeacherPeriod.getDegree() == null ? null : academicTeacherPeriod.getDegree().getCode();
            StringBuilder goodSuffix = new StringBuilder();
            goodSuffix.append("Ens.");
            if (perm) {
                goodSuffix.append(" ").append("Perm");
            }
            if (degreeCode != null) {
                goodSuffix.append(" ").append(degreeCode);
            }
            if (depCode != null) {
                goodSuffix.append(" ").append(depCode);
            }
            c.setPositionSuffix(goodSuffix.toString());
            pu.merge(c);

            if (u != null) {
                for (AppProfile p : oldProfiles) {
                    if (goodProfiles.contains(p.getId())) {
                        goodProfiles.remove(p.getId());
                        //ok
                    } else if (p.isCustom() && ("Department".equals(p.getCustomType()) || "AcademicClass".equals(p.getCustomType()) || "AcademicProgram".equals(p.getCustomType()))) {
                        core.userRemoveProfile(u.getId(), p.getId());
                    }
                }
                for (Integer toAdd : goodProfiles) {
                    core.userAddProfile(u.getId(), toAdd);
                }
            }
        }
    }

    public void validateAcademicData_Student(int studentId, int periodId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicStudent s = findStudent(studentId);
        Map<Integer, AcademicClass> academicClasses = findAcademicClassesMap();
        AppUser u = s.getUser();
        AppContact c = s.getContact();
        AppDepartment d = s.getDepartment();
        Set<String> managedProfileTypes = new HashSet<>(Arrays.asList("Department", "StatusType", "AcademicClass", "AcademicProgram"));
        if (c == null && u != null) {
            c = u.getContact();
        }
        if (d == null && u != null) {
            d = u.getDepartment();
        }
        if (s.getDepartment() == null && d != null) {
            s.setDepartment(d);
            UPA.getPersistenceUnit().merge(s);
        }
        if (s.getContact() == null && c != null) {
            s.setContact(c);
            UPA.getPersistenceUnit().merge(s);
        }
        if (u != null) {

            if (u.getDepartment() == null && d != null) {
                u.setDepartment(d);
                UPA.getPersistenceUnit().merge(u);
            }
            if (u.getContact() == null && c != null) {
                u.setContact(c);
                UPA.getPersistenceUnit().merge(u);
            }
        }
        if (c != null) {
            HashSet<Integer> goodProfiles = new HashSet<>();

            {
                if (d != null) {
                    String n = core.validateProfileName(d.getCode());
                    AppProfile p = core.findOrCreateCustomProfile(n, "Department");
                    goodProfiles.add(p.getId());
                }
            }
            {
                if (s.getStage() != null) {
                    if (s.getStage() == AcademicStudentStage.ATTENDING) {
                        AppProfile p = core.findOrCreateCustomProfile("Student", "UserType");
                        goodProfiles.add(p.getId());
                    } else if (s.getStage() == AcademicStudentStage.GAP_YEAR) {
                        AppProfile p = core.findOrCreateCustomProfile("GapYearStudent", "StatusType");
                        goodProfiles.add(p.getId());
                    } else {
                        AppProfile p = core.findOrCreateCustomProfile("FormerStudent", "StatusType");
                        goodProfiles.add(p.getId());
                        if (s.getStage() == AcademicStudentStage.GRADUATED) {
                            p = core.findOrCreateCustomProfile("Graduated", "StatusType");
                            goodProfiles.add(p.getId());

                        }
                    }
                }
            }


            for (AcademicClass ac : findAcademicUpHierarchyList(new AcademicClass[]{s.getLastClass1(), s.getLastClass2(), s.getLastClass3()}, academicClasses)) {
                if (ac != null) {
                    String n = core.validateProfileName(ac.getName());
                    //ignore inherited profiles in suffix
//                        classNames.add(n);
                    AppProfile p = core.findOrCreateCustomProfile(n, "AcademicClass");
                    goodProfiles.add(p.getId());

                    AcademicProgram pr = ac.getProgram();
                    if (pr != null) {
                        n = core.validateProfileName(ac.getName());
                        p = core.findOrCreateCustomProfile(n, "AcademicProgram");
                        goodProfiles.add(p.getId());
                    }
                }
            }
            StringBuilder goodSuffix = new StringBuilder();
            TreeSet<String> classNames = new TreeSet<>();
            List<AcademicClass> clsArr = new ArrayList<>();
            clsArr.addAll(Arrays.asList(s.getLastClass1(), s.getLastClass2(), s.getLastClass3()));
            if (s.getStage() == AcademicStudentStage.GRADUATED || s.getStage() == AcademicStudentStage.ELIMINATED) {
                AcademicFormerStudent formerStudent = findFormerStudent(s.getId());
                if (formerStudent != null) {
                    clsArr.addAll(Arrays.asList(formerStudent.getLastClass1(), formerStudent.getLastClass2(), formerStudent.getLastClass3()));
                }
            }

            for (AcademicClass ac : clsArr) {
                if (ac != null) {
                    String n = core.validateProfileName(ac.getName());
                    classNames.add(n);
                }
            }

            for (String cn : classNames) {
                if (goodSuffix.length() > 0) {
                    goodSuffix.append("/");
                }
                goodSuffix.append(cn);
            }

            if (s.getStage() != null) {
                if (s.getStage() == AcademicStudentStage.ATTENDING) {
                    //
                } else if (s.getStage() == AcademicStudentStage.GAP_YEAR) {
                    goodSuffix.insert(0, "G.Y. " + (s.getLastSubscription() == null ? "" : s.getLastSubscription().getName()) + " ");
                } else if (s.getStage() == AcademicStudentStage.GRADUATED) {
                    goodSuffix.insert(0, "Grad. " + (s.getLastSubscription() == null ? "" : s.getLastSubscription().getName()) + " ");
                } else if (s.getStage() == AcademicStudentStage.ELIMINATED) {
                    goodSuffix.insert(0, "Elim. " + (s.getLastSubscription() == null ? "" : s.getLastSubscription().getName()) + " ");
                } else {
                    //
                }
            }

            c.setPositionSuffix(goodSuffix.toString());

            c.setPositionTitle1("Student " + goodSuffix);
            pu.merge(c);

            if (u != null) {
                List<AppProfile> oldProfiles = core.findProfilesByUser(u.getId());
                for (AppProfile p : oldProfiles) {
                    if (goodProfiles.contains(p.getId())) {
                        goodProfiles.remove(p.getId());
                        //ok
                    } else if (p.isCustom() && (p.getCustomType() != null && managedProfileTypes.contains(p.getCustomType()))) {
                        core.userRemoveProfile(u.getId(), p.getId());
                    }
                }
                for (Integer toAdd : goodProfiles) {
                    core.userAddProfile(u.getId(), toAdd);
                }
            }
        }
    }

    public void validateAcademicData(int periodId) {
        Map<Integer, AcademicClass> academicClasses = findAcademicClassesMap();

        //should remove me!
//        for (AcademicCoursePlan s : findCoursePlans()) {
//            if (s.getCourseLevel().getAcademicClass() != null && s.getCourseLevel().getSemester() != null) {
//                AcademicCourseLevel lvl = findCourseLevels(s.getStudentClass().getId(), s.getSemester().getId());
//                if (lvl != null) {
//                    s.setCourseLevel(lvl);
//                    pu.merge(s);
//                }
//            }
//        }
        for (AcademicStudent s : findStudents()) {
            validateAcademicData_Student(s.getId(), periodId);
        }
        for (AcademicTeacher s : findTeachers()) {
            validateAcademicData_Teacher(s.getId(), periodId);
        }
        generateTeacherAssignementDocumentsFolder(periodId);
    }

    public List<AcademicClass> findStudentClasses(int studentId, boolean down, boolean up) {
        AcademicStudent student = findStudent(studentId);
        if (student == null) {
            return Collections.EMPTY_LIST;
        }
        AcademicClass[] refs = new AcademicClass[]{student.getLastClass1(), student.getLastClass2(), student.getLastClass3()};
        List<AcademicClass> upList = null;
        List<AcademicClass> downList = null;
        Map<Integer, AcademicClass> mm = findAcademicClassesMap();
        if (down) {
            upList = findAcademicDownHierarchyList(refs, mm);
        }
        if (up) {
            upList = findAcademicUpHierarchyList(refs, mm);
        }
        HashSet<Integer> visited = new HashSet<>();
        List<AcademicClass> all = new ArrayList<>();
        for (List<AcademicClass> cls : Arrays.asList(upList, Arrays.asList(refs), downList)) {
            if (cls != null) {
                for (AcademicClass a : cls) {
                    if (a != null) {
                        if (!visited.contains(a.getId())) {
                            visited.add(a.getId());
                            all.add(a);
                        }
                    }
                }
            }
        }
        return all;

    }



    public void updateTeacherPeriod(int periodId, int teacherId, int copyFromPeriod) {
//        AppPeriod p = core.getCurrentPeriod();
        AcademicTeacher teacher = findTeacher(teacherId);
        AppPeriod period = core.findPeriod(periodId);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicTeacherPeriod> items = pu.createQuery("Select u from AcademicTeacherPeriod u where u.teacherId=:teacherId and u.periodId=:periodId")
                .setParameter("periodId", periodId)
                .setParameter("teacherId", teacherId)
                .getResultList();
        boolean toPersist = items.size() == 0;
        while (items.size() > 1) {
            AcademicTeacherPeriod i = items.get(0);
            pu.remove(i);
            log.severe("Duplicated AcademicTeacherPeriod " + items.size());
            items.remove(0);
        }
        AcademicTeacherPeriod item;
        if (toPersist) {
            item = new AcademicTeacherPeriod();
            item.setPeriod(period);
            item.setTeacher(teacher);
        } else {
            item = items.get(0);
        }
        if (copyFromPeriod <= 0 || copyFromPeriod == periodId) {
            item.setDegree(teacher.getDegree());
            item.setSituation(teacher.getSituation());
//            item.setEnabled(teacher.isEnabled());
            item.setDepartment(teacher.getDepartment());
        } else {
            AcademicTeacherPeriod other = findAcademicTeacherPeriod(copyFromPeriod, teacher);
            item.setDegree(other.getDegree());
            item.setSituation(other.getSituation());
//            item.setEnabled(other.isEnabled());
            item.setDepartment(other.getDepartment());
        }
        if (toPersist) {
            pu.persist(item);
        } else {
            pu.merge(item);
        }
    }

    public void generateTeachingLoad(int periodId, CourseAssignmentFilter courseAssignmentFilter, String version0, String oldVersion, ProgressMonitor monitor) throws IOException {
        teacherGenerationHelper.generateTeachingLoad(periodId, courseAssignmentFilter, version0, oldVersion, monitor);
    }

    public Document getAppDepartmentPeriodRecord(int periodId, int departmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findDocumentById(AppDepartmentPeriod.class, getAppDepartmentPeriod(periodId, departmentId).getId());
    }

    public AppDepartmentPeriod getAppDepartmentPeriod(int periodId, int departmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppDepartmentPeriod d = pu.createQueryBuilder(AppDepartmentPeriod.class)
                .byField("periodId", periodId)
                .byField("departmentId", departmentId).getFirstResultOrNull();
        if (d == null) {
            d = new AppDepartmentPeriod();
            d.setDepartment(core.findDepartment(departmentId));
            d.setPeriod(core.findPeriod(periodId));
            if (d.getDepartment() == null || d.getPeriod() == null) {
                throw new RuntimeException("Invalid");
            }
            pu.persist(d);
        }
        return d;
    }

    public AcademicConversionTableHelper findConversionTableById(int id) {
        return cacheService.get(AcademicTeacherSemestrialLoad.class)
                .getProperty("findConversionTableById:" + id, new Action<AcademicConversionTableHelper>() {
                    @Override
                    public AcademicConversionTableHelper run() {
                        PersistenceUnit pu = UPA.getPersistenceUnit();
                        AcademicLoadConversionTable t = pu.findById(AcademicLoadConversionTable.class, id);
                        if (t == null) {
                            return null;
                        }
                        List<AcademicLoadConversionRow> rows = pu
                                .createQueryBuilder(AcademicLoadConversionRow.class)
                                .byField("conversionTableId", t.getId())
                                .getResultList();
                        AcademicConversionTableHelper h = new AcademicConversionTableHelper(t);
                        for (AcademicLoadConversionRow row : rows) {
                            h.add(row);
                        }
                        return h;
                    }
                });


    }

    public Map<Integer, List<AcademicCourseAssignment>> findAcademicCourseAssignmentListByTeacherId(int periodId) {
        return cacheService.get(AcademicCourseAssignment.class).getProperty("findAcademicCourseAssignmentListByTeacherId:" + periodId
                , new Action<Map<Integer, List<AcademicCourseAssignment>>>() {
                    @Override
                    public Map<Integer, List<AcademicCourseAssignment>> run() {
                        Map<Integer, List<AcademicCourseAssignment>> m = new HashMap<>();
                        for (AcademicCourseAssignment a : findAcademicCourseAssignments(periodId)) {
                            if (a.getTeacher() == null) {
                                //ignore
                                System.out.println("No assignment for " + a);
                            } else {
                                int t = a.getTeacher().getId();
                                List<AcademicCourseAssignment> list = m.get(t);
                                if (list == null) {
                                    list = new ArrayList<AcademicCourseAssignment>();
                                    m.put(t, list);
                                }
                                list.add(a);
                            }
                        }
                        return m;
                    }
                }
        );
    }

    public Map<Integer, List<AcademicCourseIntent>> getAcademicCourseIntentByAssignmentId(int periodId) {

        return cacheService.get(AcademicCourseIntent.class).getProperty("getAcademicCourseIntentByAssignmentId:" + periodId
                , new Action<Map<Integer, List<AcademicCourseIntent>>>() {
                    @Override
                    public Map<Integer, List<AcademicCourseIntent>> run() {
                        Map<Integer, List<AcademicCourseIntent>> m = new HashMap<Integer, List<AcademicCourseIntent>>();
                        for (AcademicCourseIntent e : findCourseIntents(periodId)) {
                            int t = e.getAssignment().getId();
                            List<AcademicCourseIntent> list = m.get(t);
                            if (list == null) {
                                list = new ArrayList<AcademicCourseIntent>();
                                m.put(t, list);
                            }
                            list.add(e);
                        }
                        return m;
                    }
                }
        );
    }

    public List<AcademicCourseIntent> getAcademicCourseIntentByAssignmentAndSemester(int periodId, Integer assignmentId, Integer semester) {
        return cacheService.get(AcademicCourseIntent.class).getProperty("getAcademicCourseIntentByAssignmentAndSemester:" + periodId + ":" + assignmentId + ":" + semester
                , new Action<List<AcademicCourseIntent>>() {
                    @Override
                    public List<AcademicCourseIntent> run() {
                        List<AcademicCourseIntent> m = new ArrayList<>();
                        if (assignmentId == null) {
                            for (AcademicCourseIntent value : findCourseIntents(periodId)) {
                                AcademicSemester semester1 = value.resolveSemester();
                                if (semester == null || (semester1 != null && semester1.getId() == (semester))) {
                                    m.add(value);
                                }
                            }
                        } else {
                            List<AcademicCourseIntent> list = getAcademicCourseIntentByAssignmentId(periodId).get(assignmentId);
                            if (list == null) {
//                System.out.println("No intents for " + assignmentId + " : assignment=" + assignmentId);
                            } else {
                                for (AcademicCourseIntent value : list) {
                                    AcademicSemester semester1 = value.resolveSemester();
                                    if (semester == null || (semester1 != null
                                            && semester1.getId() == (semester))) {
                                        m.add(value);
                                    }
                                }
                            }
                        }
                        return m;
                    }
                }
        );


    }

    public List<AcademicCourseIntent> getAcademicCourseIntentByTeacherAndSemester(int periodId, Integer teacher, CourseIntentFilter filter) {
        List<AcademicCourseIntent> m = new ArrayList<>();
        if (teacher == null) {
            for (AcademicCourseIntent value : findCourseIntents(periodId)) {
                AcademicSemester semester1 = value.resolveSemester();
                if (filter == null || filter.acceptIntent(value)) {
                    m.add(value);
                }
            }
        } else {
            List<AcademicCourseIntent> list = getAcademicCourseIntentByTeacherId(periodId).get(teacher);
            if (list == null) {
                //System.out.println("No intents for " + teacher + " : " + getAcademicTeacherMap().get(teacher));
            } else {
                for (AcademicCourseIntent value : list) {
                    if (filter == null || filter.acceptIntent(value)) {
                        m.add(value);
                    }
                }
            }
        }
        return m;
    }

    public Map<Integer, List<AcademicCourseIntent>> getAcademicCourseIntentByTeacherId(int periodId) {

        return cacheService.get(AcademicCourseIntent.class).getProperty("getAcademicCourseIntentByTeacherId:" + periodId
                , new Action<Map<Integer, List<AcademicCourseIntent>>>() {
                    @Override
                    public Map<Integer, List<AcademicCourseIntent>> run() {
                        Map<Integer, List<AcademicCourseIntent>> m = new HashMap<Integer, List<AcademicCourseIntent>>();
                        for (AcademicCourseIntent e : findCourseIntents(periodId)) {
                            int t = e.getTeacher().getId();
                            List<AcademicCourseIntent> list = m.get(t);
                            if (list == null) {
                                list = new ArrayList<AcademicCourseIntent>();
                                m.put(t, list);
                            }
                            list.add(e);
                        }
                        return m;
                    }
                }
        );
    }

    public AcademicConversionTableHelper findConversionTableByPeriodId(int periodId) {
        Integer id = VrApp.getBean(CacheService.class).get(AppPeriod.class)
                .getProperty("loadConversionTableId", new Action<Integer>() {
                    @Override
                    public Integer run() {
                        return UPA.getPersistenceUnit()
                                .createQuery("Select a.loadConversionTableId from AppPeriod a where a.id=:id")
                                .setParameter("id", periodId)
                                .getInteger();
                    }
                });
//                if(a.getL)

        if (id == null) {
            throw new IllegalArgumentException("Missing Conversion Table for Period " + periodId + " : " + VrApp.getBean(CorePlugin.class).findPeriod(periodId));
        }
        return findConversionTableById(id);
    }

    public List<KPI> resolveAssignmentKPIs() {
        return new ArrayList<KPI>(VrApp.getContext().getBeansOfType(KPI.class).values());
    }

    public KPIResult evalAssignmentKPIs(List<AcademicCourseAssignmentInfo> assignments, KPIGroupBy<AcademicCourseAssignmentInfo>[] groupBy, KPI<AcademicCourseAssignmentInfo>... kpis) {
        return KPIProcessProcessor.INSTANCE.run(assignments, groupBy, kpis);
    }

    public KPIResult evalAssignmentKPIs(List<AcademicCourseAssignmentInfo> assignments, KPIGroupBy<AcademicCourseAssignmentInfo> groupBy, KPI<AcademicCourseAssignmentInfo>... kpis) {
        return KPIProcessProcessor.INSTANCE.run(assignments, new KPIGroupBy[]{groupBy}, kpis);
    }

    public AcademicInternship findInternship(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicInternship.class, id);

    }

    public AcademicInternshipStatus findInternshipStatus(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicInternshipStatus.class, id);

    }

    public AcademicInternshipVariant findInternshipVariant(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicInternshipVariant.class, id);
    }

    public AcademicInternshipDuration findInternshipDuration(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicInternshipDuration.class, id);
    }

    public AcademicInternshipBoard findInternshipBoard(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(AcademicInternshipBoard.class, id);
    }

    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByDepartment(int periodId, int departmentId, Boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        QueryBuilder b = pu.createQueryBuilder("AcademicInternshipBoard");
        if (enabled != null) {
            b.byField("enabled", true);
        }
        if (departmentId >= 0) {
            b.byField("departmentId", departmentId);
        }
        if (periodId >= 0) {
            b.byField("periodId", periodId);
        }
        return b.getResultList();
    }

    public List<AcademicInternshipGroup> findEnabledInternshipGroupsByDepartment(int departmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipGroup u where u.enabled=true and (u.departmentId=:departmentId or u.departmentId=null)")
                .setParameter("departmentId", departmentId)
                .getResultList();
    }

    public List<AcademicInternshipSessionType> findAcademicInternshipSessionType() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipSessionType u order by u.name")
                .getResultList();
    }

    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByTeacher(int teacherId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u.board from AcademicInternshipBoardTeacher u where u.board.enabled=true and u.teacherId=:teacherId")
                .setParameter("teacherId", teacherId)
                .getResultList();
    }

    public List<AcademicInternshipVariant> findInternshipVariantsByType(int typeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipVariant u where u.internshipTypeId=:typeId")
                .setParameter("typeId", typeId)
                .getResultList();
    }

    public double findInternshipTeacherInternshipsCount(int teacherId, int yearId, int internshipTypeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicInternship> list = pu.createQuery("Select u from AcademicInternship u where u.board.periodId=:periodId and u.board.internshipTypeId=:internshipTypeId and (u.supervisorId=:teacherId or u.secondSupervisorId=:teacherId) and u.internshipStatus.closed=false")
                .setParameter("teacherId", teacherId)
                .setParameter("periodId", yearId)
                .setParameter("internshipTypeId", internshipTypeId)
                .getResultList();
        double count = 0;
        for (AcademicInternship a : list) {
            double m = 1;
            if (a.getSupervisor() != null && a.getSecondSupervisor() != null) {
                m = 0.5;
            }
            count += m;
        }
        return count;
    }

    public Map<Integer, Number> findInternshipTeachersInternshipsCounts(int yearId, int internshipTypeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicInternship> list = pu.createQuery("Select u from AcademicInternship u where u.board.periodId=:periodId and u.board.internshipTypeId=:internshipTypeId and u.internshipStatus.closed=false")
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 1)
                .setParameter("periodId", yearId)
                .setParameter("internshipTypeId", internshipTypeId)
                .getResultList();
        Map<Integer, Number> map = new HashMap<>();
        for (AcademicInternship a : list) {
            if (a.getSupervisor() == null && a.getSecondSupervisor() == null) {

            } else if (a.getSupervisor() != null) {
                Number count = map.get(a.getSupervisor().getId());
                if (count == null) {
                    count = 0;
                }
                count = (count.doubleValue() + 1.0);
                map.put(a.getSupervisor().getId(), count);

            } else if (a.getSecondSupervisor() != null) {
                Number count = map.get(a.getSecondSupervisor().getId());
                if (count == null) {
                    count = 0;
                }
                count = (count.doubleValue() + 1.0);
                map.put(a.getSecondSupervisor().getId(), count);

            } else {
                Number count = map.get(a.getSupervisor().getId());
                if (count == null) {
                    count = 0;
                }
                count = (count.doubleValue() + 0.5);
                map.put(a.getSupervisor().getId(), count);

                count = map.get(a.getSecondSupervisor().getId());
                if (count == null) {
                    count = 0;
                }
                count = (count.doubleValue() + 0.5);
                map.put(a.getSecondSupervisor().getId(), count);
            }
        }
        return map;
    }

    public List<AcademicInternshipBoardTeacher> findInternshipTeachersByBoard(int boardId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipBoardTeacher u where u.boardId=:boardId")
                .setParameter("boardId", boardId)
                .getResultList();
    }

    public void addBoardMessage(AcademicInternshipBoardMessage m) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        m.setObsUpdateDate(new Date());
        pu.persist(m);
    }

    public void removeBoardMessage(int messageId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicInternshipBoardMessage m = pu.findById(AcademicInternshipBoardMessage.class, messageId);
        pu.remove(m);
    }

    public List<AcademicInternshipBoardMessage> findInternshipMessagesByInternship(int internshipId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipBoardMessage u where u.internshipId=:internshipId order by u.obsUpdateDate desc")
                .setParameter("internshipId", internshipId)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                .getResultList();
    }

    public List<AcademicInternshipType> findInternshipTypes() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipType u")
                .getResultList();
    }

    public List<AcademicInternshipStatus> findInternshipStatusesByType(int typeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipStatus u where u.internshipTypeId=:typeId")
                .setParameter("typeId", typeId)
                .getResultList();
    }

    public List<AcademicInternshipDuration> findInternshipDurationsByType(int typeId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipDuration u where u.internshipTypeId=:typeId")
                .setParameter("typeId", typeId)
                .getResultList();
    }

    public AcademicInternship findStudentPFE(int studentId) {
        for (AcademicInternship aa : findActualInternshipsByStudent(studentId)) {
            if (aa.getMainGroup() != null && aa.getMainGroup().getName().toUpperCase().startsWith("PFE")) {
                //this is the PFE
                return aa;
            }
        }
        return null;

    }

    public List<AcademicInternship> findActualInternshipsByStudent(int studentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternship u where u.studentId=:studentId or u.secondStudentId=:studentId and u.internshipStatus.closed=false")
                .setParameter("studentId", studentId)
//                .setHint(QueryHints.FETCH_STRATEGY, "select")
                .getResultList();
    }

    public List<AcademicInternship> findActualInternshipsBySupervisor(int teacherId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternship u where u.supervisorId=:teacherId or u.secondSupervisorId=:teacherId and u.internshipStatus.closed=false")
                .setParameter("teacherId", teacherId)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
//                .setHint(QueryHints.FETCH_STRATEGY, QueryFetchStrategy.SELECT)
                .getResultList();
    }

    public List<AcademicInternship> findInternshipsByDepartment(int departmentId, boolean activeOnly) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternship u where u.board.departmentId=:departmentId " + (activeOnly ? "and u.internshipStatus.closed=false" : ""))
                .setParameter("departmentId", departmentId)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
//                .setHint(QueryHints.FETCH_STRATEGY, QueryFetchStrategy.SELECT)
                .getResultList();
    }

    public AcademicInternshipExtList findInternshipsByDepartmentExt(int departmentId, boolean openOnly) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where u.board.departmentId=:departmentId " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
                .setParameter("departmentId", departmentId)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
//                .setHint(QueryHints.FETCH_STRATEGY, QueryFetchStrategy.SELECT)
                .getResultList();
        List<AcademicInternshipSupervisorIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSupervisorIntent u where (u.internship.board.departmentId=:departmentId)" + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                .setParameter("departmentId", departmentId)
                .getResultList();
        List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u where (u.internship.board.departmentId=:departmentId) " + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                .setParameter("departmentId", departmentId)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
//                .setHint(QueryHints.FETCH_STRATEGY, QueryFetchStrategy.SELECT)
                .getResultList();
        return mergeAcademicInternshipExt(internships, supervisorIntents, messages);
    }

    public List<AcademicInternship> findActualInternshipsByTeacher(int teacherId, int boardId) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = ap.findTeacher(teacherId);
        AppDepartment d = t == null ? null : t.getDepartment();
        AcademicTeacher h = d == null ? null : ap.findHeadOfDepartment(d.getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (h != null && d != null && h.getId() == teacherId) {
            if (boardId <= 0) {
                return pu.createQuery("Select u from AcademicInternship u where (u.boardId==null or u.board.departmentId=:departmentId) and u.internshipStatus.closed=false")
                        .setParameter("departmentId", d.getId())
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
//                        .setHint(QueryHints.FETCH_STRATEGY, QueryFetchStrategy.SELECT)
                        .getResultList();
            } else {
                return pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
                        .setParameter("boardId", boardId)
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
//                        .setHint(QueryHints.FETCH_STRATEGY, QueryFetchStrategy.SELECT)
                        .getResultList();
            }
        }

        if (boardId <= 0) {
            ArrayList<AcademicInternship> all = new ArrayList<AcademicInternship>();
            for (AcademicInternshipBoard b : findEnabledInternshipBoardsByTeacher(teacherId)) {
                List<AcademicInternship> curr = pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
                        .setParameter("boardId", b.getId())
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
//                        .setHint(QueryHints.FETCH_STRATEGY, QueryFetchStrategy.SELECT)
                        .getResultList();
                all.addAll(curr);
            }
            return all;
        } else {
            return pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
                    .setParameter("boardId", boardId)
//                    .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
//                    .setHint(QueryHints.FETCH_STRATEGY, QueryFetchStrategy.SELECT)
                    .getResultList();
        }
    }

    private AcademicInternshipExtList mergeAcademicInternshipExt(
            List<AcademicInternship> internships,
            List<AcademicInternshipSupervisorIntent> supervisorIntents,
            List<AcademicInternshipBoardMessage> messages
    ) {
        if (internships == null) {
            internships = new ArrayList<>();
        }
        if (supervisorIntents == null) {
            supervisorIntents = new ArrayList<>();
        }
        if (messages == null) {
            messages = new ArrayList<>();
        }
        List<AcademicInternshipExt> exts = new ArrayList<>();
        Map<Integer, AcademicInternshipExt> map = new HashMap<>();
        for (AcademicInternship i : internships) {
            AcademicInternshipExt e = new AcademicInternshipExt();
            e.setInternship(i);
            e.setMessages(new ArrayList<AcademicInternshipBoardMessage>());
            e.setSupervisorIntents(new ArrayList<AcademicInternshipSupervisorIntent>());
            map.put(i.getId(), e);
            exts.add(e);
        }
        for (AcademicInternshipSupervisorIntent s : supervisorIntents) {
            map.get(s.getInternship().getId()).getSupervisorIntents().add(s);
        }
        for (AcademicInternshipBoardMessage s : messages) {
            map.get(s.getInternship().getId()).getMessages().add(s);
        }

        AcademicInternshipExtList list = new AcademicInternshipExtList();
        list.setInternshipExts(exts);
        list.setInternships(internships);
        list.setSupervisorIntents(supervisorIntents);
        list.setMessages(messages);
        return list;
    }

    public AcademicInternshipExtList findInternshipsByTeacherExt(int periodId, int deptId, int teacherId, int internshipTypeId, int boardId, boolean openOnly) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        CorePlugin cp = VrApp.getBean(CorePlugin.class);
        AcademicTeacher t = teacherId < 0 ? null : ap.findTeacher(teacherId);
        AppDepartment d = deptId < 0 ? (t == null ? null : t.getDepartment()) : cp.findDepartment(deptId);
        AcademicTeacher h = d == null ? null : ap.findHeadOfDepartment(d.getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (h != null && d != null && h.getId() == teacherId) {
            if (boardId <= 0) {
                List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where " +
                        "(u.boardId==null or (u.board.departmentId=:departmentId "
                        + (periodId >= 0 ? " and u.board.periodId=:periodId " : "")
                        + (internshipTypeId >= 0 ? " and u.board.internshipTypeId=:internshipTypeId " : "")
                        + ")) "
                        + (openOnly ? " and u.internshipStatus.closed=false" : "")
                )
                        .setParameter("departmentId", d.getId())
                        .setParameter("periodId", periodId, periodId >= 0)
                        .setParameter("internshipTypeId", internshipTypeId, internshipTypeId >= 0)
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
                        .getResultList();
                List<AcademicInternshipSupervisorIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSupervisorIntent u where " +
                        "(u.internship.boardId==null or (u.internship.board.departmentId=:departmentId"
                        + (periodId >= 0 ? " and u.internship.board.periodId=:periodId " : "")
                        + (internshipTypeId >= 0 ? " and u.internship.board.internshipTypeId=:internshipTypeId " : "")
                        + " ))"
                        + (openOnly ? " and u.internship.internshipStatus.closed=false" : "")
                )
                        .setParameter("departmentId", d.getId())
                        .setParameter("periodId", periodId, periodId >= 0)
                        .setParameter("internshipTypeId", internshipTypeId, internshipTypeId >= 0)
                        .getResultList();
                List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u " +
                        "where (u.internship.boardId==null or (u.internship.board.departmentId=:departmentId "
                        + (periodId >= 0 ? " and u.internship.board.periodId=:periodId " : "")
                        + (internshipTypeId >= 0 ? " and u.internship.board.internshipTypeId=:internshipTypeId " : "")
                        + " ))"
                        + (openOnly ? " and u.internship.internshipStatus.closed=false" : "")
                )
                        .setParameter("departmentId", d.getId())
                        .setParameter("periodId", periodId, periodId >= 0)
                        .setParameter("internshipTypeId", internshipTypeId, internshipTypeId >= 0)
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                        .getResultList();
                return mergeAcademicInternshipExt(internships, supervisorIntents, messages);

            } else {
                List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId "
                        + (openOnly ? " and u.internshipStatus.closed=false" : ""))
                        .setParameter("boardId", boardId)
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
                        .getResultList();
                List<AcademicInternshipSupervisorIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSupervisorIntent u where  u.internship.boardId=:boardId " + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                        .setParameter("boardId", boardId)
                        .getResultList();
                List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u where u.internship.boardId=:boardId  " + (openOnly ? " and u.internship.internshipStatus.closed=false" : ""))
                        .setParameter("boardId", boardId)
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                        .getResultList();
                return mergeAcademicInternshipExt(internships, supervisorIntents, messages);
            }
        }

        if (boardId <= 0) {
            StringBuilder boardList = new StringBuilder();
            List<AcademicInternshipBoard> goodBoards = pu.createQuery("Select u.board from AcademicInternshipBoardTeacher u where "
                    + " 1=1"
                    + ((openOnly) ? " and u.board.enabled=true" : "")
                    + ((deptId > 0) ? (" and u.board.departmentId=" + deptId) : "")
                    + ((periodId > 0) ? (" and u.board.periodId=" + periodId) : "")
                    + ((teacherId > 0) ? (" and u.teacherId=" + teacherId) : "")
                    + ((internshipTypeId > 0) ? (" and u.board.internshipTypeId=" + internshipTypeId) : "")
            )
                    .getResultList();
            for (AcademicInternshipBoard b : goodBoards) {
                if (boardList.length() > 0) {
                    boardList.append(",");
                }
                boardList.append(b.getId());
            }
            if (boardList.length() == 0) {
                List<AcademicInternship> internships = new ArrayList<>();
                List<AcademicInternshipSupervisorIntent> supervisorIntents = new ArrayList<>();
                List<AcademicInternshipBoardMessage> messages = new ArrayList<>();
                return mergeAcademicInternshipExt(internships, supervisorIntents, messages);
            }
            List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where u.boardId in (" + boardList + ") " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
//                    .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
                    .getResultList();
            List<AcademicInternshipSupervisorIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSupervisorIntent u where  u.internship.boardId in (" + boardList + ")  " + (openOnly ? "and u.internship.internshipStatus.closed=false" : ""))
                    .getResultList();
            List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u where u.internship.boardId in (" + boardList + ") " + (openOnly ? "and u.internship.internshipStatus.closed=false" : ""))
//                    .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                    .getResultList();
            return mergeAcademicInternshipExt(internships, supervisorIntents, messages);

        } else {
            List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
                    .setParameter("boardId", boardId)
//                    .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
                    .getResultList();
            List<AcademicInternshipSupervisorIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSupervisorIntent u where  u.internship.boardId=:boardId  " + (openOnly ? "and u.internship.internshipStatus.closed=false" : ""))
                    .setParameter("boardId", boardId)
                    .getResultList();
            List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u where u.internship.boardId=:boardId " + (openOnly ? "and u.internship.internshipStatus.closed=false" : ""))
                    .setParameter("boardId", boardId)
//                    .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                    .getResultList();
            return mergeAcademicInternshipExt(internships, supervisorIntents, messages);
        }
    }

    public List<AcademicInternship> findInternships(int teacherId, int groupId, int boardId, int deptId, int internshipTypeId, boolean openOnly) {
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        CorePlugin cp = VrApp.getBean(CorePlugin.class);
        AcademicTeacher t = teacherId < 0 ? null : ap.findTeacher(teacherId);
        AppDepartment d = deptId < 0 ? (t == null ? null : t.getDepartment()) : cp.findDepartment(deptId);
        AcademicTeacher teacher = d == null ? null : ap.findHeadOfDepartment(d.getId());
        PersistenceUnit pu = UPA.getPersistenceUnit();

        QueryBuilder q = pu.createQueryBuilder("AcademicInternship").setEntityAlias("u");
        if (boardId > 0) {
            q.byExpression("u.boardId=:boardId").setParameter("boardId", boardId);
        }
        if (groupId > 0) {
            q.byExpression("u.mainGroupId=:groupId").setParameter("groupId", groupId);
        }

        if (deptId > 0) {
            q.byExpression("(u.boardId==null or u.board.departmentId=:departmentId)").setParameter("departmentId", deptId);
        }
        if (internshipTypeId > 0) {
            q.byExpression("(u.boardId==null or u.board.internshipTypeId=:internshipTypeId)").setParameter("internshipTypeId", internshipTypeId);
        }
        if (openOnly) {
            q.byExpression("u.internshipStatus.closed=false");
        }

        if (teacher == null || teacher.getId() == teacherId) {
            //this is head of department
            //no other filter
        } else {
            //
            if (boardId <= 0) {
                StringBuilder boardList = new StringBuilder();
                List<AcademicInternshipBoard> goodBoards = pu.createQuery("Select u.board from AcademicInternshipBoardTeacher u where "
                        + " 1=1"
                        + ((openOnly) ? " and u.board.enabled=true" : "")
                        + ((deptId > 0) ? (" and u.board.departmentId=" + deptId) : "")
                        + ((teacherId > 0) ? (" and u.teacherId=" + teacherId) : "")
                        + ((internshipTypeId > 0) ? (" and u.board.internshipTypeId=" + internshipTypeId) : "")
                )
                        .getResultList();
                for (AcademicInternshipBoard b : goodBoards) {
                    if (boardList.length() > 0) {
                        boardList.append(",");
                    }
                    boardList.append(b.getId());
                }
                if (boardList.length() == 0) {
                    return new ArrayList<>();
                }
                q.byExpression("u.boardId in (" + boardList + ")");
            }
        }

        return q
//                .setHint(QueryHints.FETCH_STRATEGY, QueryFetchStrategy.SELECT)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
                .getResultList();

//        if (teacher != null && d != null && teacher.getId() == teacherId) {
//            if (boardId > 0) {
//                return pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId "
//                        + (openOnly ? " and u.internshipStatus.closed=false" : ""))
//                        .setParameter("boardId", boardId)
//                        .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
//                        .getResultList();
//            }
//            if (boardId <= 0) {
//                if (internshipTypeId < 0) {
//                    return  pu.createQuery("Select u from AcademicInternship u where (u.boardId==null or u.board.departmentId=:departmentId) " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
//                            .setParameter("departmentId", d.getId())
//                            .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
//                            .getResultList();
//                } else {
//                    return pu.createQuery("Select u from AcademicInternship u where (u.boardId==null or (u.board.departmentId=:departmentId and u.board.internshipTypeId=:internshipTypeId)) " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
//                            .setParameter("departmentId", d.getId())
//                            .setParameter("internshipTypeId", internshipTypeId)
//                            .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
//                            .getResultList();
//                }
//            }
//        }
//
//        if (boardId <= 0) {
//            StringBuilder boardList = new StringBuilder();
//            List<AcademicInternshipBoard> goodBoards = pu.createQuery("Select u.board from AcademicInternshipBoardTeacher u where "
//                    + " 1=1"
//                    + ((openOnly) ? " and u.board.enabled=true" : "")
//                    + ((deptId > 0) ? (" and u.board.departmentId=" + deptId) : "")
//                    + ((teacherId > 0) ? (" and u.teacherId=" + teacherId) : "")
//                    + ((internshipTypeId > 0) ? (" and u.board.internshipTypeId=" + internshipTypeId) : "")
//            )
//                    .getResultList();
//            for (AcademicInternshipBoard b : goodBoards) {
//                if (boardList.length() > 0) {
//                    boardList.append(",");
//                }
//                boardList.append(b.getId());
//            }
//            if (boardList.length() == 0) {
//                return new ArrayList<>();
//            }
//            return pu.createQuery("Select u from AcademicInternship u where u.boardId in (" + boardList + ") " + (openOnly ? "and u.internshipStatus.closed=false" : ""))
//                    .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
//                    .setHint(QueryHints.FETCH_STRATEGY, "select")
//                    .getResultList();
//
//        } else {
//            return pu.createQuery("Select u from AcademicInternship u where u.boardId=:boardId and u.internshipStatus.closed=false")
//                    .setParameter("boardId", boardId)
//                    .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 2)
//                    .setHint(QueryHints.FETCH_STRATEGY, "select")
//                    .getResultList();
//        }
    }

    @Install
    public void install() {
        VrApp.getBean(CorePlugin.class).createRight("Custom.Education.MyInternships", "Custom.Education.MyInternships");
        VrApp.getBean(CorePlugin.class).createRight("Custom.Education.MyInternshipBoards", "Custom.Education.MyInternshipBoards");
        VrApp.getBean(CorePlugin.class).createRight("Custom.Education.AllInternships", "Custom.Education.AllInternships");
        VrApp.getBean(CorePlugin.class).createRight("Custom.Education.InternshipBoardsStat", "Custom.Education.InternshipBoardsStat");
    }

    public void generateInternships(int internshipId, String studentProfiles) {
        AcademicInternship internship = findInternship(internshipId);
        if (internship == null) {
            throw new RuntimeException("Internship with id " + internshipId + " not found");
        }
        generateInternships(internship, studentProfiles);
    }

    public LocationInfo resolveLocation(AppCompany c) {
        c = c == null ? null : VrApp.getBean(CorePlugin.class).findCompany(c.getId());
        LocationInfo info = new LocationInfo();
        info.setCompany(c);
        info.setGovernorate(info.getCompany() == null ? null : info.getCompany().getGovernorate());
        info.setRegion(info.getGovernorate() == null ? null : info.getGovernorate().getRegion());
        info.setCountry(info.getRegion() == null ? null : info.getRegion().getCountry());
        if (info.getCountry() == null) {
            info.setCountry(info.getCompany() == null ? null : info.getCompany().getCountry());
        }
        info.setCompanyName(info.getCompany() == null ? "?" : info.getCompany().getName());
        info.setGovernorateName(info.getGovernorate() == null ? "?" : info.getGovernorate().getName());
        info.setRegionName(info.getRegion() == null ? "?" : info.getRegion().getName());
        info.setCountryName(info.getCountry() == null ? "?" : info.getCountry().getName());
        return info;
    }

    public void generateInternships(AcademicInternship internship, String studentProfiles) {
        if (internship.getBoard() == null) {
            throw new RuntimeException("Internship board missing");
        }
        if (internship.getMainGroup() == null) {
            throw new RuntimeException("Internship group missing");
        }
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AcademicPlugin acad = VrApp.getBean(AcademicPlugin.class);
        DecimalFormat df = new DecimalFormat("000");
        int pos = 1;
        PersistenceUnit pu = UPA.getPersistenceUnit();

        List<AcademicInternship> allFound = pu.createQuery("Select u from AcademicInternship u where "
                + "u.boardId=:boardId "
        )
                .setParameter("boardId", internship.getBoard().getId())
                .getResultList();
        HashSet<String> validCodes = new HashSet<>();
        for (AcademicInternship vc : allFound) {
            validCodes.add(vc.getCode());
        }

        AppUserType studentType = core.findUserType("Student");
        for (AppUser appUser : core.findUsersByProfileFilter(studentProfiles, studentType.getId())) {
            AcademicStudent student = acad.findStudentByUser(appUser.getId());
            if (student != null) {
                AcademicInternship i = pu.createQuery("Select u from AcademicInternship u where "
                        + "(u.studentId=:studentId or u.secondStudentId==:studentId) "
                        + "and u.boardId=:boardId "
                )
                        .setParameter("studentId", student.getId())
                        .setParameter("boardId", internship.getBoard().getId())
                        .getFirstResultOrNull();

                if (i == null) {
                    i = new AcademicInternship();
                    i.setStudent(student);
                    i.setBoard(internship.getBoard());
                    i.setMainGroup(internship.getMainGroup());
                    i.setDescription(internship.getDescription());
                    i.setStartDate(internship.getStartDate());
                    i.setEndDate(internship.getEndDate());
                    i.setExamDate(internship.getExamDate());
                    i.setInternshipStatus(internship.getInternshipStatus());
                    i.setMainDiscipline(internship.getMainDiscipline());
                    i.setName(internship.getName());
                    i.setValidationObservations(internship.getValidationObservations());
                    i.setTechnologies(internship.getTechnologies());
//                    i.setSecondStudent(student);
                    while (validCodes.contains(df.format(pos))) {
                        pos++;
                    }
                    i.setCode(df.format(pos));
                    validCodes.add(i.getCode());
                    pu.persist(i);
                    pos++;
                } else {
//                    i.setStudent(student);
//                    i.setDepartment(internship.getDepartment());
//                    i.setDescription(internship.getDescription());
//                    i.setStartDate(internship.getStartDate());
//                    i.setEndDate(internship.getEndDate());
//                    i.setExamDate(internship.getExamDate());
//                    i.setInternshipStatus(internship.getInternshipStatus());
//                    i.setInternshipType(internship.getInternshipType());
//                    i.setMainDiscipline(internship.getMainDiscipline());
//                    i.setName(internship.getName());
//                    i.setPeriod(internship.getPeriod());
//                    i.setProgram(internship.getProgram());
//                    i.setValidationObservations(internship.getValidationObservations());
//                    i.setTechnologies(internship.getTechnologies());
//                    i.setSecondStudent(student);
//                    while (validCodes.contains(df.format(pos))) {
//                        pos++;
//                    }
//                    i.setCode(df.format(pos));
//                    validCodes.add(i.getCode());
//                    pu.persist(i);
                }
            }
        }
    }

    public void addSupervisorIntent(int internship, int teacherId) {
        AcademicInternshipSupervisorIntent a = findInternshipTeacherIntent(internship, teacherId);
        if (a == null) {
            a = new AcademicInternshipSupervisorIntent();
            AcademicInternship i = findInternship(internship);
            AcademicTeacher t = VrApp.getBean(AcademicPlugin.class).findTeacher(teacherId);
            if (i != null && t != null) {
                a.setInternship(i);
                a.setTeacher(t);
                PersistenceUnit pu = UPA.getPersistenceUnit();
                pu.persist(a);
            }
        }
    }

    public void removeSupervisorIntent(int internship, int teacherId) {
        AcademicInternshipSupervisorIntent a = findInternshipTeacherIntent(internship, teacherId);
        if (a != null) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            pu.remove(a);
        }
    }

    public List<AcademicTeacher> findInternshipSupervisorIntents(int internship) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicInternshipSupervisorIntent> intents = pu.createQuery("Select u from AcademicInternshipSupervisorIntent u where u.internshipId=:id")
                .setParameter("id", internship)
                .getResultList();
        List<AcademicTeacher> all = new ArrayList<>();
        for (AcademicInternshipSupervisorIntent aa : intents) {
            all.add(aa.getTeacher());
        }
        return all;
    }

    public AcademicInternshipSupervisorIntent findInternshipTeacherIntent(int internship, int teacherId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipSupervisorIntent u where u.internshipId=:id and u.teacherId=:teacherId")
                .setParameter("id", internship)
                .setParameter("teacherId", teacherId)
                .getFirstResultOrNull();
    }

    public AcademicFormerStudent findFormerStudent(int studentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a from AcademicFormerStudent a where a.studentId=:studentId")
                .setParameter("studentId", studentId)
                .getSingleResultOrNull();
    }

    public AcademicFormerStudent moveToFormerStudent(int studentId, boolean lenient) {
        AcademicStudent student = findStudent(studentId);
        if (student == null) {
            return null;
        }
        if (student.getStage() == null) {
            return null;
        }
        if (student.getStage() != AcademicStudentStage.GRADUATED && student.getStage() != AcademicStudentStage.ELIMINATED) {
            return null;
        }
        if (student.getLastSubscription() == null) {
            return null;
        }
        if (student.getLastClass1() == null) {
            return null;
        }
        AcademicFormerStudent formerStudent = findFormerStudent(studentId);
        if (formerStudent != null) {
            return formerStudent;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        formerStudent = new AcademicFormerStudent();
        formerStudent.setStudent(student);

        formerStudent.setGraduationDate(null);
        formerStudent.setEliminationReason(null);
        formerStudent.setLastJobPosition(null);
        formerStudent.setLastJobCompany(null);
        formerStudent.setFirstSalary(0);
        formerStudent.setFirstJobDate(null);
        formerStudent.setLastJobPosition(null);

        formerStudent.setGraduationPeriod(student.getLastSubscription());
        formerStudent.setEliminated(student.getStage() == AcademicStudentStage.ELIMINATED);
        formerStudent.setLastClass1(student.getLastClass1());
        formerStudent.setLastClass2(student.getLastClass2());
        formerStudent.setLastClass3(student.getLastClass3());
        formerStudent.setCurriculumVitae(student.getCurriculumVitae());


        formerStudent.setEmploymentDelay(EmploymentDelay.UNEMPLOYED);


        AcademicInternship pfe = findStudentPFE(studentId);
        if (pfe != null) {
            formerStudent.setGraduationProjectTitle(pfe.getName());
            formerStudent.setGraduationProjectSummary(pfe.getDescription());
            formerStudent.setGraduationProjectSupervisor(pfe.getSupervisor() == null ? null : pfe.getSupervisor().getContact().getFullTitle());
            {
                StringBuilder sb = new StringBuilder();
                if (pfe.getSupervisor() != null) {
                    sb.append(pfe.getSupervisor().getContact().getFullName());
                }
                if (pfe.getSecondSupervisor() != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(pfe.getSecondSupervisor().getContact().getFullName());
                }
                formerStudent.setGraduationProjectSupervisor(sb.toString());
            }

            {
                StringBuilder sb = new StringBuilder();
                if (pfe.getChairExaminer() != null) {
                    sb.append("Pr:").append(pfe.getChairExaminer().getContact().getFullName());
                }
                if (pfe.getFirstExaminer() != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append("R:").append(pfe.getFirstExaminer().getContact().getFullName());
                }
                if (pfe.getSecondExaminer() != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append("R:").append(pfe.getSecondExaminer().getContact().getFullName());
                }
                formerStudent.setGraduationProjectJury(sb.toString());
                if (pfe.isPreEmployment()) {
                    formerStudent.setEmploymentDelay(EmploymentDelay.INTERNSHIP);
                }
            }
        }
        pu.persist(formerStudent);

        student.setLastClass1(null);
        student.setLastClass2(null);
        student.setLastClass3(null);
        pu.merge(student);
        return formerStudent;
    }

    public void addAcademicTeacherSemestrialLoad(int semester, int weeksLoad, int teacherId, int periodId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicTeacherSemestrialLoad> allFound = pu.createQuery("Select u from AcademicTeacherSemestrialLoad u where u.teacherId=:teacherId and u.periodId=:periodId and u.semester=:semester")
                .setParameter("teacherId", teacherId)
                .setParameter("periodId", periodId)
                .setParameter("semester", semester)
                .getResultList();
        if (allFound.isEmpty()) {
            AcademicTeacher teacher = findTeacher(teacherId);
            AppPeriod period = core.findPeriod(periodId);
            if (teacher != null && period != null) {
                AcademicTeacherSemestrialLoad load = new AcademicTeacherSemestrialLoad();
                load.setSemester(semester);
                load.setWeeksLoad(weeksLoad);
                load.setTeacher(teacher);
                load.setPeriod(period);
                pu.persist(load);
            }
        } else if (allFound.size() > 1) {
            AcademicTeacherSemestrialLoad sample = allFound.get(0);
            log.log(Level.SEVERE, "Some issue forced making multiple duplicates (" + allFound.size() + ") of AcademicTeacherSemestrialLoad for " +
                    sample.getPeriod() + "-"
                    + sample.getTeacher()
                    + ". Cleaning up this mess now...");
            allFound.remove(0);
            for (AcademicTeacherSemestrialLoad mess : allFound) {
                pu.remove(mess);
            }
        }
    }

    public Map<String, Number> statEvalInternshipStatus(List<AcademicInternship> internships) {
        Map<String, Number> circle1 = new LinkedHashMap<String, Number>();

        circle1.put("Encad Affectés", 0);
        circle1.put("Encad Non Affectés", 0);

        for (AcademicInternship ii : internships) {
            String nn = ii.getInternshipStatus().getName();
            if (!circle1.containsKey(nn)) {
                circle1.put(nn, 0);
            }
        }

        for (AcademicInternship ii : internships) {
            AcademicInternshipStatus s = ii.getInternshipStatus();
            String ss = s == null ? "?" : s.getName();
            Number v = circle1.get(ss);
            if (v == null) {
                v = 1;
            } else {
                v = v.intValue() + 1;
            }
            circle1.put(ss, v);
        }
        return circle1;
    }

//    public static void main(String[] args) {
//        String str = "P62:FBA+AD+KK	P14:OBK+IM+TBS	P4:ABA+MLA+WHA	P42:AB+NJ+SBA\n"
//                + "P29:KK+NJ+MAH	P18:IM+TA+AD	P13:NS+SBJ+TBS	P26:IB+NK+WC\n"
//                + "P27:AD+KK+AB	P1:WC+LH+ABA	P10:IM+SBA+BB	P61:IB+HM+MLA\n"
//                + "	P45:HM+AD+MLA	P28:TBS+JBT+NK	\n"
//                + "			\n"
//                + "P68:NJ+FBA+MAH	P39:LH+AM+HM	P34:AB+IS+BB	\n"
//                + "P21:NJ+WAL+FBA	P57:SBJ+IS+OBK	P9:BB+HM+NK	\n"
//                + "P56:NJ+AB+AM	P48:HM+KK+IS+NK		\n"
//                + "P31:KK+FBA+AB			";
//
//        try {
//            BufferedReader r = new BufferedReader(new StringReader(str));
//            String line = null;
//            int row = 0;
//            while ((line = r.readLine()) != null) {
//                HashSet<String> rowSet = new HashSet<>();
//                row++;
//                for (String s : line.split("[ :+\t]+")) {
//                    if (s.length() > 0) {
//                        if (rowSet.contains(s)) {
//                            System.err.println("**** r=" + row + " Duplicate " + s + "    *********************************** : " + line);
//                        } else {
//                            rowSet.add(s);
//                        }
//                    }
//                }
////                System.out.println(rowSet);
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(AcademicPlugin.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public Map<String, Number> statEvalInternshipAssignmentCount(List<AcademicInternship> internships) {
        Map<String, Number> circle2 = new LinkedHashMap<String, Number>();
        circle2.put("Encad Affectés", 0);
        circle2.put("Encad Non Affectés", 0);
        for (AcademicInternship ii : internships) {
            String nn = ii.getInternshipStatus().getName();
            if (!circle2.containsKey(nn)) {
                circle2.put(nn, 0);
            }
        }

        for (AcademicInternship ii : internships) {
            String ss = (ii.getSupervisor() != null) ? "Encad Affectés" : "Encad Non Affectés";
            Number v = circle2.get(ss);
            if (v == null) {
                v = 1;
            } else {
                v = v.intValue() + 1;
            }
            circle2.put(ss, v);
        }


        return circle2;

    }

    public Map<String, Number> statEvalInternshipJuryExaminerCount(List<AcademicInternship> internships) {
        Map<String, Number> circle3 = new LinkedHashMap<String, Number>();
        circle3.put("Rapporteur Affectés", 0);
        circle3.put("Rapporteur Non Affectés", 0);
        for (AcademicInternship ii : internships) {
            String ss;
            Number v;

            ss = (ii.getChairExaminer() != null) ? "Président Affectés" : "Président Non Affectés";
            v = circle3.get(ss);
            if (v == null) {
                v = 1;
            } else {
                v = v.intValue() + 1;
            }
            circle3.put(ss, v);
        }
        return circle3;
    }

    public Map<String, Number> statEvalInternshipJuryChairCount(List<AcademicInternship> internships) {
        Map<String, Number> circle4 = new LinkedHashMap<String, Number>();
        circle4.put("Président Affectés", 0);
        circle4.put("Président Non Affectés", 0);

        for (AcademicInternship ii : internships) {
            String ss;
            Number v;
            ss = (ii.getFirstExaminer() != null) ? "Rapporteur Affectés" : "Rapporteur Non Affectés";
            v = circle4.get(ss);
            if (v == null) {
                v = 1;
            } else {
                v = v.intValue() + 1;
            }
            circle4.put(ss, v);
        }
        return circle4;
    }

    public List<NamedValueCount> statEvalInternshipRegion(List<AcademicInternship> internships) {
        Map<String, Number> circle1 = new LinkedHashMap<String, Number>();

        CorePlugin cp = VrApp.getBean(CorePlugin.class);
        AppCompany currentCompany = cp.getCurrentConfig().getMainCompany();
        LocationInfo currentLocation = resolveLocation(currentCompany);
        String id_company = currentLocation.getCompanyName();
        String id_governorate = currentLocation.getGovernorateName() + " Sauf " + currentLocation.getCompanyName();
        String id_region = currentLocation.getRegionName() + " Sauf " + currentLocation.getGovernorateName();
        String id_country = currentLocation.getCountryName() + " Sauf " + currentLocation.getRegionName();
        String id_International = "Intenational";
        String id_Unknown = "Inconnu";
        circle1.put(id_company, 0);
        circle1.put(id_governorate, 0);
        circle1.put(id_region, 0);
        circle1.put(id_country, 0);
        circle1.put(id_International, 0);
        circle1.put(id_Unknown, 0);
        ListValueMap<String, AcademicInternship> circle1_internships = new ListValueMap<>();

        for (AcademicInternship ii : internships) {
            AppCompany c = ii.getCompany();
            LocationInfo lc = resolveLocation(c);
            String gouvernorateName = lc.getGovernorate() == null ? "Gouvernorat inconnu" : lc.getGovernorate().getName();

            Boolean same_company = null;
            Boolean same_governorate = null;
            Boolean same_region = null;
            Boolean same_country = null;
            String best_id = null;
            String first_diff = null;

            if (lc.getCountry() != null && currentLocation.getCountry() != null) {
                same_country = lc.getCountry().getId() == currentLocation.getCountry().getId();
            }
            if (lc.getRegion() != null && currentLocation.getRegion() != null) {
                same_region = lc.getRegion().getId() == currentLocation.getRegion().getId();
            }
            if (lc.getGovernorate() != null && currentLocation.getGovernorate() != null) {
                same_governorate = lc.getGovernorate().getId() == currentLocation.getGovernorate().getId();
            }
            if (lc.getCompany() != null && currentLocation.getCompany() != null) {
                same_company = lc.getCompany().getId() == currentLocation.getCompany().getId();
            }

            if (same_company != null) {
                if (same_company) {
                    best_id = id_company;
                } else {
                    first_diff = id_company;
                }
            }

            if (best_id == null) {
                if (same_governorate != null) {
                    if (same_governorate) {
                        best_id = id_governorate;
                    } else {
                        first_diff = id_governorate;
                    }
                }
            }

            if (best_id == null) {
                if (same_region != null) {
                    if (same_region) {
                        best_id = id_region;
                    } else {
                        first_diff = id_region;
                    }
                }
            }

            if (best_id == null) {
                if (same_country != null) {
                    if (same_country) {
                        best_id = id_country;
                    } else {
                        first_diff = id_country;
                    }
                }
            }
            if (best_id != null) {
                //good found it
            } else if (first_diff == null) {
                best_id = id_Unknown;
            } else if (first_diff.equals(id_country)) {
                best_id = id_International;
            } else if (first_diff.equals(id_region)) {
                best_id = id_country;
            } else if (first_diff.equals(id_governorate)) {
                if (currentLocation.getRegion() != null) {
                    best_id = id_region;
                } else {
                    best_id = id_country;
                }
            } else if (first_diff.equals(id_company)) {
                best_id = id_governorate;
            }
            if (best_id == null) {
                if (same_country != null) {
                    if (same_country) {
                        if (same_region != null) {
                            if (same_region) {
                                if (same_region != null) {
                                    if (same_region) {

                                    }
                                } else {
                                    best_id = id_country;
                                }

                            }
                        } else {
                            best_id = id_country;
                        }
                    } else {
                        best_id = id_International;
                    }
                }
            }
            if (best_id != null) {
                Integer old = ((Integer) circle1.get(best_id));
                if (old == null) {
                    old = 0;
                }
                circle1.put(best_id, old + 1);
                circle1_internships.put(best_id, ii);
            }
        }
        return buildNamedValueCountList(circle1, circle1_internships);
    }

    public List<NamedValueCount> statEvalInternshipGovernorate(List<AcademicInternship> internships) {
        Map<String, Number> circle2 = new LinkedHashMap<String, Number>();
        CorePlugin cp = VrApp.getBean(CorePlugin.class);
        AppCompany currentCompany = cp.getCurrentConfig().getMainCompany();
        LocationInfo currentLocation = resolveLocation(currentCompany);
        ListValueMap<String, AcademicInternship> circle2_internships = new ListValueMap<>();

        for (AcademicInternship ii : internships) {
            AppCompany c = ii.getCompany();
            LocationInfo lc = resolveLocation(c);
            String gouvernorateName = lc.getGovernorate() == null ? "Gouvernorat inconnu" : lc.getGovernorate().getName();
            Number v2 = circle2.get(gouvernorateName);
            if (v2 == null) {
                v2 = 1;
//                        circle1.put(gouvernorateName, 0);
                circle2.put(gouvernorateName, v2);
                circle2_internships.put(gouvernorateName, ii);
            } else {
                circle2.put(gouvernorateName, v2.intValue() + 1);
                circle2_internships.put(gouvernorateName, ii);
            }
        }
        return buildNamedValueCountList(circle2, circle2_internships);
    }

    public List<NamedValueCount> statEvalInternshipDiscipline(List<AcademicInternship> internships) {
        Map<String, Number> circle1 = new LinkedHashMap<String, Number>();

        ListValueMap<String, AcademicInternship> circle1_internships = new ListValueMap<>();

        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        for (AcademicInternship ii : internships) {

            HashSet<String> set = new HashSet<String>(academicPlugin.parseDisciplinesNames(ii.getMainDiscipline(), false));
            for (String s0 : set) {
                Number y = circle1.get(s0);
                if (y == null) {
                    y = 1;
                } else {
                    y = y.intValue() + 1;
                }
                circle1.put(s0, y);
                circle1_internships.put(s0, ii);
            }
        }
        return buildNamedValueCountList(circle1, circle1_internships);
    }

    public List<NamedValueCount> statEvalInternshipTechnologies(List<AcademicInternship> internships) {
        Map<String, Number> circle2 = new LinkedHashMap<String, Number>();

        ListValueMap<String, AcademicInternship> circle2_internships = new ListValueMap<>();

        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        for (AcademicInternship ii : internships) {

            HashSet<String> set = new HashSet<String>(academicPlugin.parseWords(ii.getTechnologies()));
            for (String s0 : set) {
                Number y = circle2.get(s0);
                if (y == null) {
                    y = 1;
                } else {
                    y = y.intValue() + 1;
                }
                circle2.put(s0, y);
                circle2_internships.put(s0, ii);
            }
        }
        return buildNamedValueCountList(circle2, circle2_internships);
    }

    public List<NamedValueCount> statEvalInternshipVariant(List<AcademicInternship> internships) {
        Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
        ListValueMap<String, AcademicInternship> circle1_internships = new ListValueMap<>();

        for (AcademicInternship ii : internships) {
            AcademicInternshipVariant v = ii.getInternshipVariant();
            String s0 = v == null ? "Autre Variante" : v.getName();
            Number y = circle1.get(s0);
            if (y == null) {
                y = 1;
            } else {
                y = y.intValue() + 1;
            }
            circle1.put(s0, y);
            circle1_internships.put(s0, ii);
        }
        return buildNamedValueCountList(circle1, circle1_internships);
    }

    public List<NamedValueCount> statEvalInternshipPeriod(List<AcademicInternship> internships) {
        Map<String, Number> circle2 = new LinkedHashMap<String, Number>();
        ListValueMap<String, AcademicInternship> circle2_internships = new ListValueMap<>();

        for (AcademicInternship ii : internships) {
            AcademicInternshipDuration v2 = ii.getDuration();
            String s0 = v2 == null ? "Autre Durée" : v2.getName();
            Number y = circle2.get(s0);
            if (y == null) {
                y = 1;
            } else {
                y = y.intValue() + 1;
            }
            circle2.put(s0, y);
            circle2_internships.put(s0, ii);
        }
        return buildNamedValueCountList(circle2, circle2_internships);
    }

    protected List<NamedValueCount> buildNamedValueCountList(Map<String, Number> circle1, ListValueMap<String, AcademicInternship> circle1_internships) {
        List<NamedValueCount> circle1_values = VrUtils.reverseSortCountNamedValueCountList(circle1);
        for (NamedValueCount circle1_value : circle1_values) {
            circle1_value.setUserValue(circle1_internships.get((String) circle1_value.getName()));
        }
        for (int i = circle1_values.size() - 1; i >= 0; i--) {
            if (circle1_values.get(i).getCount() == 0) {
                circle1_values.remove(i);
            }
        }
        return circle1_values;
    }

    private class TeacherIdByTeacherPeriodComparator implements Comparator<AcademicTeacher> {

        private final int periodId;

        public TeacherIdByTeacherPeriodComparator(int periodId) {
            this.periodId = periodId;
        }

        public int compare(AcademicTeacher t1, AcademicTeacher t2) {
            if (t1.getId() == t2.getId()) {
                return 0;
            }
            if (t1 == null && t2 == null) {
                return 0;
            }
            if (t1 == null) {
                return -1;
            }
            if (t2 == null) {
                return 1;
            }

            AcademicTeacherDegree d1 = findAcademicTeacherPeriod(periodId, t1).getDegree();
            AcademicTeacherDegree d2 = findAcademicTeacherPeriod(periodId, t2).getDegree();
            if (d1 == null && d2 == null) {
                return 0;
            }
            if (d1 == null) {
                return -1;
            }
            if (d2 == null) {
                return 1;
            }

            int r = Integer.compare(d1.getPosition(), d2.getPosition());
            if (r != 0) {
                return r;
            }
            r = d1.getName().compareTo(d2.getName());
            if (r != 0) {
                return r;
            }
            r = getValidName(t1).compareTo(getValidName(t2));
            if (r != 0) {
                return r;
            }
            return r;
        }
    }

}
