package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.plugins.academic.service.util.*;
import net.vpc.app.vainruling.plugins.academic.service.helper.AcademicConversionTableHelper;
import net.vpc.app.vainruling.plugins.academic.service.model.config.*;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.history.AcademicHistTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.stat.*;
import net.vpc.common.util.*;
import net.vpc.common.util.mon.ProgressMonitor;
import net.vpc.common.util.mon.ProgressMonitorFactory;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AcademicPluginBodyLoad extends AcademicPluginBody {

    private static final Logger log = Logger.getLogger(AcademicPluginBodyLoad.class.getName());
    public static final Set<DeviationGroup> DEFAULT_DEVIATION_GROUPS = Collections.unmodifiableSet(new java.util.HashSet<DeviationGroup>(Arrays.asList(
            DeviationGroup.DEPARTMENT, DeviationGroup.DEGREE, DeviationGroup.SITUATION, DeviationGroup.DISCIPLINE
    )));
    public static final Comparator<GlobalAssignmentStat> GLOBAL_ASSIGNMENT_STAT_COMPARATOR = new GlobalAssignmentStatComparator();
    public static final Converter<AcademicCoursePlan, Integer> academicCoursePlanIdConverter = new AcademicCoursePlanIdConverter();
    private static Converter<TeacherPeriodStat, Integer> teacherPeriodStatMapListConverter = new TeacherPeriodStatMapListConverter();

    private CorePlugin core;
    private AcademicPlugin academic;

    @Override
    public void onStart() {
        core = CorePlugin.get();
        academic = AcademicPlugin.get();
        core.createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ALL_TEACHERS_COURSE_LOAD, "Charge tous enseignats");
        core.createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_COURSE_LOAD, "Ma charge");
        core.createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_COURSE_LOAD, "Charge Detaillee");
        core.createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_GLOBAL_STAT, "Stat Charge");
        AppProfile director = core.findOrCreateCustomProfile("Director", "UserType");
        core.addProfileRight(director.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_COURSE_LOAD);
        core.addProfileRight(director.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_GLOBAL_STAT);
        core.addProfileRight(director.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ALL_TEACHERS_COURSE_LOAD);

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

    public LoadValue getAssignmentLoadValue(AcademicCourseAssignment assignment, AcademicTeacherDegree degree, AcademicConversionTableHelper conversionTable) {
        AcademicPluginSecurity.requireManageableCourseAssignment(assignment);
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
        MapList<Integer, AcademicCourseAssignment> courseAssignments = academic.findCourseAssignments(periodId);
        AcademicCourseAssignment module = courseAssignments.getByKey(courseAssignmentId);
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER);
        //requireManageableCourseAssignment(module);
        LoadValue mod_val = new LoadValue(module.getValueC(), module.getValueTD(), module.getValueTP(), module.getValuePM());
        ModuleStat ms = new ModuleStat();
        ms.setModule(module);
        List<AcademicSemester> semesters = getContext().getPlugin().findSemesters();
        ModuleSemesterStat[] sems = new ModuleSemesterStat[semesters.size()];
        ms.setSemesters(sems);
        AcademicTeacher teacher = null;
        if (forTeacherId != null) {
            teacher = getContext().getPlugin().findTeacher(forTeacherId);
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
            AcademicTeacherPeriod trs = getContext().getPlugin().findAcademicTeacherPeriod(periodId, teacher);
            AcademicConversionTableHelper conversionTableByPeriodId = getContext().getPlugin().findConversionTableByPeriodId(periodId);

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
        AcademicTeacherDegree dd = getContext().getPlugin().findTeacherDegree(degree);
        return evalValueEquiv(value, dd, table);
    }

    public double evalValueEquiv(LoadValue v, AcademicTeacherDegree dd, AcademicConversionTableHelper table) {
        if (dd == null || dd.getConversionRule()==null || table==null) {
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

    public TeacherPeriodStat evalTeacherStat0(
            int periodId,
            int teacherId,
            AcademicTeacher teacher,
            List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoads,
            List<AcademicCourseAssignmentInfo> assignments,
            CourseAssignmentFilter filter,
            DeviationConfig deviationConfig
    ) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER);
//        requireTeacherOrManager(teacherId);
        Chronometer ch = new Chronometer();
        if (teacher == null) {
            teacher = getContext().getPlugin().findTeacher(teacherId);
        }
        if (teacher == null) {
            return null;
        }
//        int teacherId = tal.getTeacher().getId();
        if (assignments == null) {
            assignments = academic.findCourseAssignmentsAndIntents(periodId, teacherId, filter);
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
        teacher_stat.setTeacherPeriod(getContext().getPlugin().findAcademicTeacherPeriod(periodId, teacher_stat.getTeacher()));

        AcademicTeacherDegree td = teacher_stat.getTeacherPeriod().getDegree();
        if (td == null) {
            td = teacher.getDegree();
        }
        if (td == null) {
            td = new AcademicTeacherDegree();
        }
        AcademicTeacherSituation ts = teacher_stat.getTeacherPeriod().getSituation();
        if (ts == null) {
            ts = teacher.getSituation();
        }
        if (ts == null) {
            ts = new AcademicTeacherSituation();
            ts.setType(AcademicTeacherSituationType.TEMPORARY);
        }
        double teacherDue = teacherDue(ts, td);

        teacher_stat.getDueWeek().setEquiv(teacherDue);

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
            findTeacherSemestrialLoads = academic.findTeacherSemestrialLoadsByTeacher(periodId, teacherId);
            if (findTeacherSemestrialLoads == null) {
                log.severe("teacherSemestrialLoads not found for teacherId=" + teacherId + " (" + teacher + ")");
                findTeacherSemestrialLoads = new ArrayList<>();
            }
        }

        teacher_stat.setSemestrialLoad(findTeacherSemestrialLoads.toArray(new AcademicTeacherSemestrialLoad[findTeacherSemestrialLoads.size()]));
        List<AcademicSemester> semesters = getContext().getPlugin().findSemesters();
        TeacherSemesterStat[] sems = new TeacherSemesterStat[semesters.size()];
        teacher_stat.setSemesters(sems);
        double sum_semester_weeks = 0;
        double sum_max_semester_weeks = 0;
        LoadValue teacher_extraWeek = teacher_stat.getExtraWeek();
        LoadValue teacher_extra = teacher_stat.getExtra();
        int maxWeeks = getContext().getPlugin().getSemesterMaxWeeks();
        AcademicConversionTableHelper conversionTable = getContext().getPlugin().findConversionTableByPeriodId(periodId);

        AcademicLoadConversionRow r = td.getConversionRule() == null ? null : conversionTable.get(td.getConversionRule().getId());
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
                        LoadValue v = getAssignmentLoadValue(academicCourseAssignment.getAssignment(), td, conversionTable);
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
                    sem_dueWeek.setC(teacherDue);
                    sem_due.setC(teacherDue * semesterWeeks);
                } else if (ruleValueTD == 1) {
                    sem_dueWeek.setTd(teacherDue);
                    sem_due.setTd(teacherDue * semesterWeeks);
                } else if (r.getValueTP() == 1) {
                    sem_dueWeek.setTp(teacherDue);
                    sem_due.setTp(teacherDue * semesterWeeks);
                } else if (r.getValuePM() == 1) {
                    sem_dueWeek.setPm(teacherDue);
                    sem_due.setPm(teacherDue * semesterWeeks);
                }
                if (hasDU) {
                    sem_extra.setEquiv(sem_value.getEquiv() - teacherDue * semesterWeeks);
                    sem_extra.setC(sem_value.getC() - sem_due.getC());
                    sem_extra.setTd(sem_value.getTd() - sem_due.getTd());
                    sem_extra.setTp(sem_value.getTp() - sem_due.getTp());
                    sem_extra.setPm(sem_value.getPm() - sem_due.getPm());

                    sem_extraWeek.setEquiv(sem_valueWeek.getEquiv() - teacherDue * (semesterWeeks / sem.getMaxWeeks()));
                    sem_extraWeek.setC(sem_valueWeek.getC() - sem_dueWeek.getC());
                    sem_extraWeek.setTd(sem_valueWeek.getTd() - sem_dueWeek.getTd());
                    sem_extraWeek.setTp(sem_valueWeek.getTp() - sem_dueWeek.getTp());
                    sem_extraWeek.setPm(sem_valueWeek.getPm() - sem_dueWeek.getPm());
                }
//                sem_value.setTppm(sem_value.getTp() + sem_value.getPm() * (r.getValuePM() / r.getValueTP()));

                teacher_stat.getValue().add(sem_value);
                sem_dueWeek.setEquiv(teacherDue);
                sem_due.setEquiv(teacherDue * semesterWeeks);

                if (hasDU) {
                    sem.getExtraEff().set(evalHeureSupp(sem_extra, r));

                    LoadValue sem_extraWeekEff = sem.getExtraWeekEff();
                    sem_extraWeekEff.set(sem.getExtraEff().copy().div(semesterWeeks));
                    teacher_stat.getExtraWeekEff().add(sem_extraWeekEff);

                    teacher_extra.add(sem_extraWeek.copy().mul(semesterWeeks));
                }
            }
            sem_value.setEquiv(evalValueEquiv(sem_value, td, conversionTable));
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
            teacher_stat.getValue().setEquiv(evalValueEquiv(teacher_stat.getValue(), td, conversionTable));
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
            AcademicLoadConversionRow cr = td.getConversionRule() == null ? null : conversionTable.get(td.getConversionRule().getId());
            if (cr != null && cr.getValueC() == 1) {
                //teacher_extraWeek.setC(teacher_extraWeek.getEquiv());
                //teacher_extra.setC(teacher_extra.getEquiv());
                teacher_stat.getDueWeek().setC(teacher_stat.getDueWeek().getEquiv());
                teacher_stat.getDue().setC(teacher_stat.getDue().getEquiv());
                for (TeacherSemesterStat sem : sems) {
                    //sem.getExtraWeek().setC(sem.getExtraWeek().getEquiv());
                }
            } else if (cr != null && cr.getValueTD() == 1) {
                //teacher_extraWeek.setTd(teacher_extraWeek.getEquiv());
                //teacher_extra.setTd(teacher_extra.getEquiv());
                teacher_stat.getDueWeek().setTd(teacher_stat.getDueWeek().getEquiv());
                teacher_stat.getDue().setTd(teacher_stat.getDue().getEquiv());
                for (TeacherSemesterStat sem : sems) {
                    //sem.getExtraWeek().setTd(sem.getExtraWeek().getEquiv());
                }
            } else if (cr != null && cr.getValueTP() == 1) {
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
        log.log(Level.FINE, "evalTeacherStat {0} in {1}", new Object[]{getContext().getPlugin().getValidName(teacher), ch.stop()});

        return teacher_stat;
    }

    private double teacherDue(AcademicTeacherSituation situation, AcademicTeacherDegree degree) {
        if (teacherHasDue(situation, degree)) {
            return degree.getValueDU();
        }
        return 0;
    }

    private boolean teacherHasDue(AcademicTeacherSituation situation, AcademicTeacherDegree degree) {
        return degree != null
                && degree.getValueDU() > 0
                && situation != null
                && situation.getType() != null
                && situation.getType().isWithDue();
    }

    public TeacherPeriodStat evalTeacherStat(
            int periodId,
            int teacherId,
            CourseAssignmentFilter courseAssignmentFilter,
            DeviationConfig deviationConfig,
            ProgressMonitor mon
    ) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER);
//        requireTeacherOrManager(teacherId);

//        TeacherPeriodStat teacherPeriodStat = evalTeacherStat0(periodId,
//                teacherId,
//                teacher,
//                findTeacherSemestrialLoads,
//                modules,
//                filter,
//                includeIntents,
//                deviationConfig,
//                cache);
        ProgressMonitor monitor = ProgressMonitorFactory.nonnull(mon);

        MapList<Integer, TeacherPeriodStat> all = evalTeacherStatList(periodId, null, courseAssignmentFilter, deviationConfig, monitor);
        TeacherPeriodStat teacherPeriodStat = all.getByKey(teacherId);
        if (teacherPeriodStat != null) {
            return teacherPeriodStat;
        }
        //ghost Teacher (with no load)
        TeacherPeriodStat ghost = new TeacherPeriodStat();
        AcademicTeacher teacher = getContext().getPlugin().findTeacher(teacherId);
        ghost.setTeacher(teacher);
        ghost.setConfig(deviationConfig);
        List<AcademicSemester> semesters = getContext().getPlugin().findSemesters();
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
        ghost.setTeacherPeriod(getContext().getPlugin().findAcademicTeacherPeriod(periodId, ghost.getTeacher()));
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

    public double evalHistValueEquiv(int yearId, LoadValue value, String degree) {
        AcademicHistTeacherDegree dd = academic.findHistTeacherDegree(yearId, degree);
        return evalHistValueEquiv(value, dd);
    }

    public double evalHistValueEquiv(LoadValue v, AcademicHistTeacherDegree dd) {
        return dd.getValueC() * v.getC()
                + dd.getValueTD() * v.getTd()
                + dd.getValueTP() * v.getTp()
                + dd.getValuePM() * v.getPm();
    }

    public void addAcademicTeacherSemestrialLoad(int semester, int weeksLoad, int teacherId, int periodId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicTeacherSemestrialLoad> allFound = pu.createQuery("Select u from AcademicTeacherSemestrialLoad u where u.teacherId=:teacherId and u.periodId=:periodId and u.semester=:semester")
                .setParameter("teacherId", teacherId)
                .setParameter("periodId", periodId)
                .setParameter("semester", semester)
                .getResultList();
        if (allFound.isEmpty()) {
            AcademicTeacher teacher = academic.findTeacher(teacherId);
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
            log.log(Level.SEVERE, "Some issue forced making multiple duplicates (" + allFound.size() + ") of AcademicTeacherSemestrialLoad for "
                    + sample.getPeriod() + "-"
                    + sample.getTeacher()
                    + ". Cleaning up this mess now...");
            allFound.remove(0);
            for (AcademicTeacherSemestrialLoad mess : allFound) {
                pu.remove(mess);
            }
        }
    }

    public void updateAllCoursePlanValuesByLoadValues(int periodId) {
        Chronometer ch = new Chronometer();
        for (AcademicCoursePlan coursePlan : academic.findCoursePlans(periodId)) {
            updateCoursePlanValuesByLoadValues(coursePlan);
        }
        log.log(Level.INFO, "updateAllCoursePlanValuesByLoadValues in {1}", new Object[]{ch.stop()});
    }

    public void updateCoursePlanValuesByLoadValues(int coursePlanId) {
        AcademicCoursePlan p = academic.findCoursePlan(coursePlanId);
        updateCoursePlanValuesByLoadValues(p);
    }

    private void updateCoursePlanValuesByLoadValues(AcademicCoursePlan coursePlan) {
//        Chronometer ch=new Chronometer();
        List<AcademicCourseAssignment> loads = academic.findCourseAssignmentsByCoursePlan(coursePlan.getId());
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

        core.save("AcademicCoursePlan", coursePlan);
//        log.log(Level.INFO,"updateCoursePlanValuesByLoadValues in {1}",new Object[]{ch.stop()});
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

    public GlobalStat evalGlobalStat(int periodId, TeacherPeriodFilter teacherFilter, CourseAssignmentFilter filter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        ProgressMonitor monitor = ProgressMonitorFactory.nonnull(mon);
        GlobalStat s = new GlobalStat();
        ProgressMonitor[] mons = monitor.split(new double[]{3, 1, 1});
        MapList<Integer, TeacherPeriodStat> ts = evalTeacherStatList(periodId, teacherFilter, filter, deviationConfig, mons[0]);
        int i = 0;
        int max = ts.size();
        for (TeacherPeriodStat t : ts) {
            mons[1].setProgress(i++, max);
            AcademicTeacherPeriod trs = getContext().getPlugin().findAcademicTeacherPeriod(periodId, t.getTeacher());
            AcademicTeacherSituation situation = trs.getSituation();
            AcademicTeacherDegree degree = trs.getDegree();
            GlobalAssignmentStat[] annStatAss = new GlobalAssignmentStat[]{
                s.getAssignment(null, situation, degree, true),
                s.getAssignment(null, situation, null, true),
                s.getAssignment(null, null, degree, true),
                s.getAssignment(null, null, null, true)};

            for (GlobalAssignmentStat y : annStatAss) {
                y.addTeacherStat(t);
            }
            for (TeacherSemesterStat semLoad : t.getSemesters()) {
                AcademicSemester semester = semLoad.getSemester();

                GlobalAssignmentStat[] semStatAss = new GlobalAssignmentStat[]{
                    s.getAssignment(semester, situation, degree, true),
                    s.getAssignment(semester, situation, null, true),
                    s.getAssignment(semester, null, degree, true),
                    s.getAssignment(semester, null, null, true)
                };

                for (GlobalAssignmentStat y : semStatAss) {
                    y.addTeacherStat(semLoad);
                }
            }
        }
        List<AcademicTeacherSituation> contractualsList = getContext().getPlugin().findTeacherSituations(AcademicTeacherSituationType.CONTRACTUAL);
        List<AcademicTeacherSituation> permanentsList = getContext().getPlugin().findTeacherSituations(AcademicTeacherSituationType.PERMANENT);
        List<AcademicTeacherSituation> temporaryList = getContext().getPlugin().findTeacherSituations(AcademicTeacherSituationType.TEMPORARY);
        List<AcademicTeacherSituation> leaveList = getContext().getPlugin().findTeacherSituations(AcademicTeacherSituationType.LEAVE);
        AcademicTeacherDegree maitre_assistant = getContext().getPlugin().findTeacherDegree("MA");
        AcademicConversionTableHelper conversionTableByPeriodId = getContext().getPlugin().findConversionTableByPeriodId(periodId);

        List<AcademicCourseAssignment> courseAssignments = academic.findCourseAssignments(periodId, new CourseAssignmentFilterAnd().and(filter).and(new CourseAssignmentFilter() {
            @Override
            public boolean acceptAssignment(IAcademicCourseAssignment academicCourseAssignment) {
                AcademicTeacher t = academicCourseAssignment.getTeacher();
                if (t != null) {
                    return teacherFilter == null || teacherFilter.acceptTeacher(getContext().getPlugin().findAcademicTeacherPeriod(periodId, t));
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
            double g = evalValueEquiv(loadValue, maitre_assistant, conversionTableByPeriodId);
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
                AcademicTeacherPeriod pp = getContext().getPlugin().findAcademicTeacherPeriod(periodId, value.getTeacher());
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
                        possibleGlobalAssignmentStats.add(s.getAssignment(sem, sit, deg, true));
                    }
                }
            }
            possibleGlobalAssignmentStats.add(s.getAssignment(null, null, null, true));
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
                Set<String> ood = getContext().getPlugin().parseDisciplinesNames(value.getCoursePlan().getDiscipline(), false);
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
                Set<String> ood = getContext().getPlugin().parseDisciplinesNames(value.getCoursePlan().getDiscipline(), false);
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

        int totalCount = (int) (s.getTeachersTemporaryStat().getTeachersCount()
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
        double contrEquivTot = evalValueEquiv(s.getAssignmentSumValue(null, contractualsList, null), maitre_assistant, conversionTableByPeriodId);
        double vacEquivTot = evalValueEquiv(s.getAssignmentSumValue(null, temporaryList, null), maitre_assistant, conversionTableByPeriodId);
        double missingAss = evalValueEquiv(ta.getMissingAssignmentsLoad(), maitre_assistant, conversionTableByPeriodId);
        int maxWeeks = getContext().getPlugin().getSemesterMaxWeeks();
        neededByDue.getValue().setEquiv(Math.max(0, permEquivTot - permEquivDu + contrEquivTot + vacEquivTot + missingAss));
        List<AcademicSemester> semesters = getContext().getPlugin().findSemesters();
        neededByDue.getValueWeek().setEquiv(neededByDue.getValue().getEquiv() / maxWeeks / semesters.size());
        neededByDue.setTeachersCount(
                /*(int) Math.ceil*/(neededByDue.getValueWeek().getEquiv() / maitre_assistant.getValueDU())
        );

        neededRelative.getValue().setEquiv(contrEquivTot + vacEquivTot + missingAss);
        neededRelative.getValueWeek().setEquiv(neededRelative.getValue().getEquiv() / maxWeeks / semesters.size());
        neededRelative.setTeachersCount(
                /*(int) Math.ceil*/(neededRelative.getValueWeek().getEquiv() / maitre_assistant.getValueDU())
        );

        double contractMiss = 6;
        GlobalAssignmentStat missingStat = s.getMissing();
        missingStat.getValue().set(ta.getMissingAssignmentsLoad());
        missingStat.getValueWeek().set(missingStat.getValue()).div(maxWeeks * semesters.size());
        missingStat.setTeachersCount(
                /*(int) Math.ceil*/(missingStat.getValueWeek().getEquiv() / contractMiss)
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
            s.getTeachersContractualStat(),}) {
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
        LoadValue refDue = evalValueEquiv(maitre_assistant.getValueDU(), maitre_assistant, conversionTableByPeriodId);

        s.setReferenceTeacherDueLoad(refDue.getEquivTD() * maxWeeks * semestersIds.size());
        /**
         * permanentLoad-permanentDue+Non overload of permanents with a
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

    public MapList<Integer, TeacherPeriodStat> evalTeacherStatList(final int periodId, TeacherPeriodFilter teacherFilter, CourseAssignmentFilter courseAssignmentFilter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        ProgressMonitor monitor = ProgressMonitorFactory.nonnull(mon);
        Chronometer ch = new Chronometer();

        TeacherIdByTeacherPeriodComparator teacherIdByTeacherPeriodComparator = new TeacherIdByTeacherPeriodComparator(getContext().getPlugin(), periodId);

        List<AcademicTeacher> teachersList = new ArrayList<>(getContext().getPlugin().findTeachers(periodId, teacherFilter));
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
        ProgressMonitor[] mons = monitor.split(3);

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

    private void evalTeacherSemesterStatListDeviation(List<TeacherSemesterStat> list, DeviationConfig deviationConfig, ProgressMonitor mon) {
        ProgressMonitor monitor = ProgressMonitorFactory.nonnull(mon);
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

    public List<TeacherSemesterStat> evalTeacherSemesterStatList(int periodId, Integer semesterId, TeacherPeriodFilter teacherFilter, CourseAssignmentFilter filter, DeviationConfig deviationConfig, ProgressMonitor mon) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        List<TeacherSemesterStat> all = new ArrayList<>();
        ProgressMonitor monitor = ProgressMonitorFactory.nonnull(mon);
        ProgressMonitor[] mons = monitor.split(new double[]{4, 1, 2});
        MapList<Integer, TeacherPeriodStat> teacherPeriodStats = evalTeacherStatList(periodId, teacherFilter,
                new CourseAssignmentFilterAnd().and(filter).and(new DefaultCourseAssignmentFilter().addAcceptedSemester(semesterId)),
                deviationConfig, mons[0]);

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

    private MapList<Integer, AcademicCoursePlan> extractCourses(List<AcademicCourseAssignment> all) {
        MapList<Integer, AcademicCoursePlan> m = new DefaultMapList<Integer, AcademicCoursePlan>(
                academicCoursePlanIdConverter
        );
        for (AcademicCourseAssignment academicCourseAssignment : all) {
            AcademicCoursePlan p = academicCourseAssignment.getCoursePlan();
            if (p != null) {
                m.add(p);
            }
        }
        return m;
    }

}
