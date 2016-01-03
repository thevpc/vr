/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.vpc.app.vainruling.api.AppEntityExtendedPropertiesProvider;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseGroup;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseLevel;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseType;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgram;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicDiscipline;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherSemestrialLoad;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherSituation;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignmentInfo;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseIntent;
import net.vpc.app.vainruling.plugins.academic.service.model.history.AcademicHistCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.history.AcademicHistCourseGroup;
import net.vpc.app.vainruling.plugins.academic.service.model.history.AcademicHistCoursePlan;
import net.vpc.app.vainruling.plugins.academic.service.model.history.AcademicHistProgram;
import net.vpc.app.vainruling.plugins.academic.service.model.history.AcademicHistTeacherAnnualLoad;
import net.vpc.app.vainruling.plugins.academic.service.model.history.AcademicHistTeacherDegree;
import net.vpc.app.vainruling.plugins.academic.service.model.history.AcademicHistTeacherSemestrialLoad;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.LoadValue;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.ModuleSemesterStat;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.ModuleStat;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherSemesterStat;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.TeacherStat;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.Install;
import net.vpc.app.vainruling.api.Start;
import net.vpc.app.vainruling.api.TraceService;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppCivility;
import net.vpc.app.vainruling.api.model.AppContact;
import net.vpc.app.vainruling.api.model.AppDepartment;
import net.vpc.app.vainruling.api.model.AppGender;
import net.vpc.app.vainruling.api.model.AppProfile;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.model.AppUserType;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.api.util.ExcelTemplate;
import net.vpc.app.vainruling.api.util.NamedDoubles;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningData;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningDay;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningHour;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicFormerStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.content.AcademicTeacherCV;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.GlobalAssignmentStat;
import net.vpc.app.vainruling.plugins.academic.service.model.stat.GlobalStat;
import net.vpc.app.vainruling.plugins.filesystem.service.FileSystemPlugin;
import net.vpc.common.streams.PathInfo;
import net.vpc.common.strings.MapStringConverter;
import net.vpc.common.strings.StringComparator;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.utils.Chronometer;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.expressions.Order;
import net.vpc.upa.expressions.Var;
import net.vpc.upa.types.DateTime;
import net.vpc.common.vfs.VFS;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileFilter;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.common.vfs.impl.NativeVFS;
import net.vpc.common.vfs.impl.VZipOptions;
import net.vpc.common.vfs.impl.VZipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.PropertyPlaceholderHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author vpc
 */
@AppPlugin(version = "1.8", dependsOn = {"fileSystemPlugin", "commonModel"})
public class AcademicPlugin implements AppEntityExtendedPropertiesProvider {

    private static final Logger log = Logger.getLogger(AcademicPlugin.class.getName());

    @Autowired
    TraceService trace;
    @Autowired
    CorePlugin core;
    @Autowired
    FileSystemPlugin fileSystemPlugin;

    private static PropertyPlaceholderHelper h = new PropertyPlaceholderHelper("${", "}");
    private static DecimalFormat FF = new DecimalFormat("0.0#");
    int maxWeeks = 14;//should be in config

    public TeacherStat evalTeacherStat(int teacherId,
            boolean includeIntent,
            StatCache cache) {
        AcademicTeacher teacher = cache.getAcademicTeacherMap().get(teacherId);
        if (teacher == null) {
            return null;
        }
        return evalTeacherStat(teacherId, null, null, null, includeIntent, cache);
    }

    public TeacherStat evalTeacherStat(
            int teacherId,
            AcademicTeacher teacher,
            List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoads,
            List<AcademicCourseAssignment> modules,
            boolean includeIntent,
            StatCache cache
    ) {
        Chronometer ch = new Chronometer();
        if (teacher == null) {
            teacher = cache.getAcademicTeacherMap().get(teacherId);
        }
        if (teacher == null) {
            return null;
        }
//        int teacherId = tal.getTeacher().getId();
        if (modules == null) {
            modules = findCourseAssignments(teacherId, null, includeIntent, cache);
            if (modules == null) {
                log.severe("No assignements found for teacherId=" + teacherId + " (" + teacher + ")");
            }
        }
        TeacherStat teacher_stat = new TeacherStat();
        teacher_stat.setIncludeIntents(includeIntent);
        teacher_stat.setTeacher(teacher);
        AcademicTeacherDegree degree = teacher_stat.getTeacher().getDegree();
        teacher_stat.getDueWeek().setEquiv(degree == null ? 0 : degree.getValueDU());
        boolean hasDU = teacher_stat.getDueWeek().getEquiv() > 0;
        if (!hasDU) {
            teacher_stat.getDueWeek().setEquiv(0);
            teacher_stat.getDue().setEquiv(0);
        }
        if (findTeacherSemestrialLoads == null) {
            findTeacherSemestrialLoads = cache.getAcademicTeacherSemestrialLoadByTeacherIdMap().get(teacherId);
            if (findTeacherSemestrialLoads == null) {
                log.severe("reacherSemestrialLoads not found for teacherId=" + teacherId + " (" + teacher + ")");
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
        for (int i = 0; i < sems.length; i++) {
            AcademicSemester ss = semesters.get(i);
            TeacherSemesterStat sem = new TeacherSemesterStat();
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

            for (AcademicCourseAssignment academicCourseAssignment : modules) {
                ModuleStat ms = evalModuleStat(academicCourseAssignment.getId(), teacherId, cache);
                ModuleSemesterStat mss = ms.getSemester(ss.getName());
                sem_value.add(mss.getValue());
            }
            if (semesterWeeks == 0) {
                LoadValue zeros = new LoadValue();
                sem_valueWeek.set(zeros);
                sem_extraWeek.set(zeros);
                sem_value.setTppm(0);
                sem_valueWeek.setTppm(0);
                sem_due.setEquiv(0);
            } else {
                sem_valueWeek.set(sem_value.copy().div(semesterWeeks));
                AcademicTeacherDegree td = teacher.getDegree();
                if (td == null) {
                    td = new AcademicTeacherDegree();
                }
                sem_extraWeek.setEquiv(sem_valueWeek.getEquiv() - td.getValueDU() * (semesterWeeks / sem.getMaxWeeks()));
                sem_extra.setEquiv(sem_value.getEquiv() - td.getValueDU() * semesterWeeks);
                AcademicTeacherDegree dd = td;
                sem_value.setTppm(sem_value.getTp() + sem_value.getPm() * (dd.getValuePM() / dd.getValueTP()));
                sem_valueWeek.setTppm(sem_value.getTppm() / semesterWeeks);

                teacher_stat.getValue().add(sem_value);
                sem_dueWeek.setEquiv(td.getValueDU());
                sem_due.setEquiv(td.getValueDU() * semesterWeeks);

                if (hasDU) {
                    teacher_extra.add(sem_extraWeek.copy().mul(semesterWeeks));
                }
            }
            sem_value.setEquiv(evalValueEquiv(sem_value, degree));
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
            teacher_stat.getValue().setEquiv(evalValueEquiv(teacher_stat.getValue(), degree));
            teacher_stat.getValueWeek().set(teacher_stat.getValue().copy().div(sum_semester_weeks));
            if (hasDU) {
                teacher_stat.getDue().set(teacher_stat.getDueWeek().copy().mul(sum_semester_weeks));
                teacher_extraWeek.set(teacher_extra.copy().div(sum_semester_weeks));
            }
        }
        if (hasDU) {
            teacher_extraWeek.setEquiv(teacher_stat.getValueWeek().getEquiv() - teacher_stat.getDueWeek().getEquiv());
            teacher_extra.setEquiv(teacher_stat.getValue().getEquiv() - teacher_stat.getDueWeek().getEquiv() * sum_semester_weeks);

            if (degree.getValueC() == 1) {
                teacher_extraWeek.setC(teacher_extraWeek.getEquiv());
                teacher_extra.setC(teacher_extra.getEquiv());
                teacher_stat.getDueWeek().setC(teacher_stat.getDueWeek().getEquiv());
                teacher_stat.getDue().setC(teacher_stat.getDue().getEquiv());
                for (TeacherSemesterStat sem : sems) {
                    sem.getExtraWeek().setC(sem.getExtraWeek().getEquiv());
                }
            } else if (degree.getValueTD() == 1) {
                teacher_extraWeek.setTd(teacher_extraWeek.getEquiv());
                teacher_extra.setTd(teacher_extra.getEquiv());
                teacher_stat.getDueWeek().setTd(teacher_stat.getDueWeek().getEquiv());
                teacher_stat.getDue().setTd(teacher_stat.getDue().getEquiv());
                for (TeacherSemesterStat sem : sems) {
                    sem.getExtraWeek().setTd(sem.getExtraWeek().getEquiv());
                }
            } else if (degree.getValueTP() == 1) {
                teacher_extraWeek.setTp(teacher_extraWeek.getEquiv());
                teacher_extra.setTp(teacher_extra.getEquiv());
                teacher_stat.getDueWeek().setTp(teacher_stat.getDueWeek().getEquiv());
                teacher_stat.getDue().setTp(teacher_stat.getDue().getEquiv());
                for (TeacherSemesterStat sem : sems) {
                    sem.getExtraWeek().setTp(sem.getExtraWeek().getEquiv());
                }
            } else {
                teacher_extraWeek.setTd(teacher_extraWeek.getEquiv());
                teacher_extra.setTd(teacher_extra.getEquiv());
                teacher_stat.getDueWeek().setTd(teacher_stat.getDueWeek().getEquiv());
                teacher_stat.getDue().setTd(teacher_stat.getDue().getEquiv());
            }
        } else {
            teacher_extraWeek.set(new LoadValue());
            teacher_extra.set(new LoadValue());
        }
        teacher_stat.setWeeks(sum_semester_weeks);
        teacher_stat.setMaxWeeks(sum_max_semester_weeks);
        log.log(Level.FINE, "evalTeacherStat {0} in {1}", new Object[]{getValidName(teacher), ch.stop()});

        return teacher_stat;
    }

    public ModuleStat evalModuleStat(int courseAssignmentId, Integer forTeacherId, StatCache cache) {
        if (cache == null) {
            cache = new StatCache();
        }
        AcademicCourseAssignment module = cache.getAcademicCourseAssignmentMap().get(courseAssignmentId);
        LoadValue mod_val = new LoadValue(module.getValueC(), module.getValueTD(), module.getValueTP(), module.getValuePM(), 0, 0);
        ModuleStat ms = new ModuleStat();
        ms.setModule(module);
        List<AcademicSemester> semesters = cache.getAcademicSemesterList();
        ModuleSemesterStat[] sems = new ModuleSemesterStat[semesters.size()];
        ms.setSemesters(sems);
        AcademicTeacher teacher = null;
        if (forTeacherId != null) {
            teacher = cache.getAcademicTeacherMap().get(forTeacherId);
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
            AcademicTeacher tal = teacher;//cache.getAcademicTeacherMap().get(teacher.getId());
            for (int i = 0; i < semesters.size(); i++) {
                AcademicSemester ss = semesters.get(i);
                ModuleSemesterStat s = new ModuleSemesterStat();
                s.setSemester(ss);
                if (module.getCoursePlan().getSemester().getName().equals(ss.getName())) {
                    s.getValue().set(mod_val.copy().mul(module.getGroupCount() * module.getShareCount()));
                    s.setValueEffWeek(module.getValueEffWeek() * module.getGroupCount() * module.getShareCount());
                    s.getValue().setEquiv(evalValueEquiv(s.getValue(), tal.getDegree()));
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

    public double evalValueEquiv(LoadValue value, String degree) {
        AcademicTeacherDegree dd = findTeacherDegree(degree);
        return evalValueEquiv(value, dd);
    }

    public double evalValueEquiv(LoadValue v, AcademicTeacherDegree dd) {
        if (dd == null) {
            return 0;
        }
        return dd.getValueC() * v.getC()
                + dd.getValueTD() * v.getTd()
                + dd.getValueTP() * v.getTp()
                + dd.getValuePM() * v.getPm();
    }

    public AcademicTeacher findTeacherByUser(Integer userId) {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicTeacher u where u.userId=:userId")
                .setParameter("userId", userId)
                .getEntity();
    }

    public AcademicStudent findStudentByUser(Integer userId) {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicStudent u where u.userId=:userId")
                .setParameter("userId", userId)
                .getEntity();
    }

    public AcademicTeacher findTeacherByContact(Integer contacId) {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicTeacher u where u.contactId=:contacId")
                .setParameter("contacId", contacId)
                .getEntity();
    }

    public AcademicStudent findStudentByContact(Integer contacId) {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicStudent u where u.contactId=:contacId")
                .setParameter("contacId", contacId)
                .getEntity();
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
    public void addTeacherAcademicCourseAssignment(int teacherId, int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = (AcademicCourseAssignment) pu.findById(AcademicCourseAssignment.class, assignementId);
        a.setTeacher(findTeacher(teacherId));
        pu.merge(a);
    }

    public void removeTeacherAcademicCourseAssignment(int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseAssignment a = (AcademicCourseAssignment) pu.findById(AcademicCourseAssignment.class, assignementId);
        a.setTeacher(null);
        pu.merge(a);
    }

    public void addIntent(int teacherId, int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicCourseIntent i = pu.createQuery("Select a from AcademicCourseIntent a where a.teacherId=:teacherId and a.assignmentId=:assignementId")
                .setParameter("teacherId", teacherId)
                .setParameter("assignementId", assignementId)
                .getEntity();
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
                .getEntity();
        if (i != null) {
            pu.remove(i);
        }
    }

    public void removeAllIntents(int assignementId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicCourseIntent> intentList = pu.createQuery("Select a from AcademicCourseIntent a where a.assignmentId=:assignementId")
                .setParameter("assignementId", assignementId)
                .getEntityList();
        for (AcademicCourseIntent ii : intentList) {
            pu.remove(ii);
        }
    }

    public List<AcademicCourseIntent> findCourseIntentsByAssignment(int assignment, String semester, StatCache cache) {
        if (cache != null) {
            return cache.getAcademicCourseIntentByAssignmentAndSemester(assignment, semester);
        }
        List<AcademicCourseIntent> intents = null;
        intents = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a where a.assignmentId=:assignment")
                .setHint("navigationDepth", 5)
                .setParameter("assignment", assignment)
                .getEntityList();
        List<AcademicCourseIntent> m = new ArrayList<>();
        for (AcademicCourseIntent value : intents) {
            if (semester == null || (value.getAssignment().getCoursePlan().getSemester() != null && value.getAssignment().getCoursePlan().getSemester().getName().equals(semester))) {
                m.add(value);
            }
        }
        return m;
    }

    public List<AcademicCourseIntent> findCourseIntentsByTeacher(Integer teacher, String semester, StatCache cache) {
        if (cache != null) {
            return cache.getAcademicCourseIntentByTeacherAndSemester(teacher, semester);
        }
        List<AcademicCourseIntent> intents = null;
        if (teacher == null) {
            intents = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a")
                    .setHint("navigationDepth", 5)
                    .getEntityList();
        } else {
            intents = UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseIntent a where a.teacherId=:teacherId")
                    .setHint("navigationDepth", 5)
                    .setParameter("teacherId", teacher)
                    .getEntityList();
        }
        List<AcademicCourseIntent> m = new ArrayList<>();
        for (AcademicCourseIntent value : intents) {
            if (semester == null || (value.getAssignment().getCoursePlan().getSemester() != null && value.getAssignment().getCoursePlan().getSemester().getName().equals(semester))) {
                m.add(value);
            }
        }
        return m;
    }

    public List<AcademicCourseAssignment> findCourseAssignments(Integer teacher, String semester, boolean includeIntents, StatCache cache) {
        if (includeIntents) {
            List<AcademicCourseAssignment> all = new ArrayList<>();
            for (AcademicCourseAssignmentInfo i : findCourseAssignmentsAndIntents(teacher, semester, cache)) {
                all.add(i.getAssignment());
            }
            return all;
        }
        return cache.getAcademicCourseAssignmentsByTeacherAndSemester(teacher, semester);
    }

    public List<AcademicCourseAssignmentInfo> findCourseAssignmentsAndIntents(Integer teacher, String semester, StatCache cache) {
        List<AcademicCourseAssignmentInfo> all = new ArrayList<>();
        HashSet<Integer> visited = new HashSet<>();
        for (AcademicCourseAssignment a : cache.getAcademicCourseAssignmentsByTeacherAndSemester(teacher, semester)) {
            if (!visited.contains(a.getId())) {
                visited.add(a.getId());
                AcademicCourseAssignmentInfo b = new AcademicCourseAssignmentInfo();
                b.setAssigned(a.getTeacher() != null);
                b.setAssignment(a);
                all.add(b);
            }
        }
        for (AcademicCourseIntent a : findCourseIntentsByTeacher(teacher, semester, cache)) {
            if (!visited.contains(a.getAssignment().getId())) {
                visited.add(a.getAssignment().getId());
                AcademicCourseAssignmentInfo b = new AcademicCourseAssignmentInfo();
                b.setAssigned(false);
                b.setAssignment(a.getAssignment());
                all.add(b);
            }
        }
        for (AcademicCourseAssignmentInfo a : all) {
            List<AcademicCourseIntent> b = findCourseIntentsByAssignment(a.getAssignment().getId(), semester, cache);
            TreeSet<String> allIntents = new TreeSet<>();
            TreeSet<Integer> allIntentIds = new TreeSet<>();
            for (AcademicCourseIntent b1 : b) {
                if (teacher == null || (teacher.intValue() != b1.getTeacher().getId())) {
                    String n = getValidName(b1.getTeacher());
                    allIntents.add(n);
                }
                allIntentIds.add(b1.getTeacher().getId());
            }
            StringBuilder sb = new StringBuilder();
            if (a.getAssignment().getTeacher() != null) {
                AcademicTeacher t = a.getAssignment().getTeacher();
                String name = getValidName(t);
                sb.append(name + " (*)");
            }
            for (String i : allIntents) {
                if (a.getAssignment().getTeacher() != null
                        && a.getAssignment().getTeacher().getContact() != null
                        && i.equals(a.getAssignment().getTeacher().getContact().getFullName())) {
                    //ignore  
                } else {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(i);
                }
            }
            a.setIntentsSet(allIntents);
            a.setIntents(sb.toString());
            a.setIntentsUserIdsSet(allIntentIds);
        }
        Collections.sort(all, new Comparator<AcademicCourseAssignmentInfo>() {

            @Override
            public int compare(AcademicCourseAssignmentInfo o1, AcademicCourseAssignmentInfo o2) {
                AcademicCourseAssignment a1 = o1.getAssignment();
                AcademicCourseAssignment a2 = o2.getAssignment();
//                String s1 = a1.getCoursePlan().getName();
//                String s2 = a2.getCoursePlan().getName();
                String s1 = a1.getFullName();
                String s2 = a2.getFullName();
                return s1.compareTo(s2);
            }
        });
        return all;
    }

//    protected void generatePrintableTeacherLoadFolder(int yearId, Integer[] teacherIds, String semester, String template, String output) throws IOException {
//        TeacherStat[] stats = evalTeachersStat(yearId, teacherIds, semester);
//        generateTeacherAssignmentsFolder(stats, template, output);
//    }
    private void generateTeacherAssignmentsFolder(TeacherStat[] stats, StatCache cache, VFile template, VFile output, boolean writeVars) throws IOException {
//        if (!output.endsWith(File.separator) && !output.endsWith("/")) {
//            output = output + File.separator;
//        }
        String soutput = output.getPath();
        if (!soutput.contains("*")) {
            soutput = soutput + "*";
        }
        if (!soutput.toLowerCase().endsWith(".xls")) {
            soutput = soutput + ".xls";
        }
        for (TeacherStat st : stats) {
            String pp = soutput.replace("*", AppContact.getName(st.getTeacher().getContact()));
            VFile f2 = fileSystemPlugin.getFileSystem().get(pp);
            f2.getParentFile().mkdirs();
            Map<String, Object> p = preparePrintableTeacherLoadProperties(st, cache);
            if (writeVars) {
                writeVars(p, f2);
            }
            AppUser u = st.getTeacher().getUser();
            if (u != null) {
                VirtualFileSystem ufs = fileSystemPlugin.getUserFileSystem(u.getLogin());
                ufs.get("/MyDocuments/Charges").mkdirs();
                ExcelTemplate.generateExcel(template, f2, p);
                f2.copyTo(ufs.get("/MyDocuments/Charges", f2.getName()));
            }
        }
    }

    public List<TeacherSemesterStat> evalTeacherSemesterStatList(Integer[] teachers, String semester, boolean includeIntents, final StatCache cache) {
        List<TeacherSemesterStat> all = new ArrayList<>();
        for (TeacherStat s : evalTeacherStatList(teachers, semester, includeIntents, cache)) {
            if (semester == null) {
                all.addAll(Arrays.asList(s.getSemesters()));
            } else {
                for (TeacherSemesterStat ss : s.getSemesters()) {
                    if (ss.getSemester().getCode().equals(semester)) {
                        all.add(ss);
                    }
                }
            }
        }
        return all;
    }

    public List<TeacherStat> evalTeacherStatList(Integer[] teachers, String semester, boolean includeIntents, final StatCache cache) {
        Chronometer ch = new Chronometer();

        if (teachers == null || teachers.length == 0) {
            Set<Integer> all = cache.getAcademicTeacherMap().keySet();
            teachers = all.toArray(new Integer[all.size()]);
        } else {
            HashSet<Integer> teachersSetRequested = new HashSet<>();
            for (Integer tt : teachers) {
                if (tt != null && cache.getAcademicTeacherMap().containsKey(tt)) {
                    teachersSetRequested.add(tt);
                } else {
                    System.err.println("Teacher id ignored " + tt);
                }
            }
            teachers = teachersSetRequested.toArray(new Integer[teachersSetRequested.size()]);
        }
        TreeSet<Integer> teachersSet = new TreeSet<Integer>(new Comparator<Integer>() {

            public int compare(Integer o1, Integer o2) {
                if (o1.equals(o2)) {
                    return 0;
                }
                AcademicTeacher t1 = cache.getAcademicTeacherMap().get(o1);
                AcademicTeacher t2 = cache.getAcademicTeacherMap().get(o2);
                if (t1 == null && t2 == null) {
                    return 0;
                }
                if (t1 == null) {
                    return -1;
                }
                if (t2 == null) {
                    return 1;
                }
                AcademicTeacherDegree d1 = t1.getDegree();
                AcademicTeacherDegree d2 = t2.getDegree();
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
        });
        teachersSet.addAll(Arrays.asList(teachers));
        List<TeacherStat> stats = new ArrayList<>();
        for (Integer teacherId : teachersSet) {
            TeacherStat st = evalTeacherStat(teacherId, null, null, null, includeIntents, cache);
            if (st != null) {
                boolean ok = false;
                if (st.getValue().getEquiv() > 0) {
                    if (semester != null) {
                        for (TeacherSemesterStat ss : st.getSemesters()) {
                            if (ss.getSemester().getName().equals(semester)) {
                                if (ss.getValue().getEquiv() > 0) {
                                    ok = true;
                                }
                            }
                        }
                    } else {
                        ok = true;
                    }
                }
                if (ok) {
                    stats.add(st);
                }
            }
        }
//        Collections.sort(stats, new Comparator<TeacherStat>() {
//
//            
//            public int compare(TeacherStat o1, TeacherStat o2) {
//                return Teacher.getName(o1.getTeacher()).compareTo(Teacher.getName(o2.getTeacher()));
//            }
//        });
        log.log(Level.FINE, "evalTeachersStat {0} teachers in {1}", new Object[]{teachersSet.size(), ch.stop()});
        return stats;//.toArray(new TeacherStat[stats.size()]);
    }

    public GlobalStat evalGlobalStat(boolean includeIntents, StatCache cache) {
        GlobalStat s = new GlobalStat();
        if (cache == null) {
            cache = new StatCache();
        }
        List<AcademicTeacher> allTeachers = findTeachers();
        List<Integer> teachersIds = new ArrayList<>();
        for (AcademicTeacher t : allTeachers) {
            teachersIds.add(t.getId());
        }
        List<AcademicSemester> findSemesters = cache.getAcademicSemesterList();

        List<TeacherStat> ts = evalTeacherStatList(teachersIds.toArray(new Integer[teachersIds.size()]), null, includeIntents, cache);
        for (TeacherStat t : ts) {
            AcademicTeacherSituation situation = t.getTeacher().getSituation();
            AcademicTeacherDegree degree = t.getTeacher().getDegree();
            GlobalAssignmentStat[] annStatAss = new GlobalAssignmentStat[]{
                s.getAssignment(null, situation, degree),
                s.getAssignment(null, situation, null),
                s.getAssignment(null, null, null)};

            for (GlobalAssignmentStat y : annStatAss) {

                y.getValue().add(t.getValue());
                y.getExtra().add(t.getExtra());
                y.getDue().add(t.getDue());

                y.getDueWeek().add(t.getDueWeek());
                y.getValueWeek().add(t.getValueWeek());
                y.getExtraWeek().add(t.getExtraWeek());

                y.setMaxWeeks(y.getMaxWeeks() + t.getMaxWeeks());
                y.setWeeks(y.getWeeks() + t.getWeeks());
                if (!y.getTeachers().containsKey(t.getTeacher().getId())) {
                    y.getTeachers().put(t.getTeacher().getId(), t.getTeacher());
                    y.setTeachersCount(y.getTeachers().size());
                }
                int teachersSize = y.getTeachers().size();
                y.getAvgValue().set(t.getValue().copy().div(teachersSize));
                y.getAvgExtra().set(t.getExtraWeek().copy().div(teachersSize));
                y.getAvgValueWeek().set(t.getValueWeek().copy().div(teachersSize));
                y.getAvgExtraWeek().set(t.getExtraWeek().copy().div(teachersSize));
            }
            for (TeacherSemesterStat semLoad : t.getSemesters()) {
                AcademicSemester semester = semLoad.getSemester();

                GlobalAssignmentStat[] semStatAss = new GlobalAssignmentStat[]{
                    s.getAssignment(semester, situation, degree),
                    s.getAssignment(semester, situation, null),
                    s.getAssignment(semester, null, degree),
                    s.getAssignment(semester, null, null),
                    s.getAssignment(semester, situation, null),};

                for (GlobalAssignmentStat y : semStatAss) {

                    y.getValue().add(semLoad.getValue());
                    y.getExtra().add(semLoad.getExtra());
                    y.getDue().add(semLoad.getDue());

                    y.getDueWeek().add(semLoad.getDueWeek());
                    y.getValueWeek().add(semLoad.getValueWeek());
                    y.getExtraWeek().add(semLoad.getExtraWeek());

                    y.setMaxWeeks(y.getMaxWeeks() + semLoad.getMaxWeeks());
                    y.setWeeks(y.getWeeks() + semLoad.getWeeks());
                    if (!y.getTeachers().containsKey(t.getTeacher().getId())) {
                        y.getTeachers().put(t.getTeacher().getId(), t.getTeacher());
                        y.setTeachersCount(y.getTeachers().size());
                    }
                    int teachersSize = y.getTeachers().size();
                    y.getAvgValue().set(semLoad.getValue().copy().div(teachersSize));
                    y.getAvgExtra().set(semLoad.getExtraWeek().copy().div(teachersSize));
                    y.getAvgValueWeek().set(semLoad.getValueWeek().copy().div(teachersSize));
                    y.getAvgExtraWeek().set(semLoad.getExtraWeek().copy().div(teachersSize));
                }
            }
        }

        List<AcademicCoursePlan> coursePlans = findCoursePlans();
        List<AcademicCourseAssignment> courseAssignments = cache.getAcademicCourseAssignmentList();
        s.setCoursePlanCount(coursePlans.size());
        s.setCourseAssignmentCount(courseAssignments.size());
        for (AcademicCourseAssignment value : courseAssignments) {
            AcademicSemester semester = value.getCoursePlan().getSemester();
            double grp = value.getGroupCount();
            double shr = value.getShareCount();
            s.getAssignment(semester, null, null).getTargetAssignments().add(new LoadValue(
                    value.getValueC() * grp * shr,
                    value.getValueTD() * grp * shr,
                    value.getValueTP() * grp * shr,
                    value.getValuePM() * grp * shr,
                    0, 0
            ));
            s.getAssignment(null, null, null).getTargetAssignments().add(new LoadValue(
                    value.getValueC() * grp * shr,
                    value.getValueTD() * grp * shr,
                    value.getValueTP() * grp * shr,
                    value.getValuePM() * grp * shr,
                    0, 0
            ));
        }
        GlobalAssignmentStat a = s.getAssignment(null, null, null);
        a.getMissingAssignments().set(a.getTargetAssignments()).substruct(a.getValue());
        for (AcademicSemester sem : findSemesters) {
            a = s.getAssignment(sem, null, null);
            a.getMissingAssignments().set(a.getTargetAssignments()).substruct(a.getValue());
        }
        s.getAssignments().sort(new Comparator<GlobalAssignmentStat>() {

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

        });
        AcademicTeacherSituation Contractuel = findTeacherSituation("Contractuel");
        AcademicTeacherSituation Permanent = findTeacherSituation("Permanent");
        AcademicTeacherSituation Vacataire = findTeacherSituation("Vacataire");
        s.setTeachersCount(s.getTotalAssignment().getTeachersCount());
        AcademicTeacherDegree assistant = findTeacherDegree("A");
        GlobalAssignmentStat vacataireAssignment = s.getAssignment(null, Vacataire, null);

        s.setTeachersTemporaryCount(vacataireAssignment.getTeachersCount());
        GlobalAssignmentStat contractuelAssignment = s.getAssignment(null, Contractuel, null);
        s.setTeachersContractualCount(contractuelAssignment.getTeachersCount());
        s.setTeachersPermanentCount(s.getAssignment(null, Permanent, null).getTeachersCount());
        GlobalAssignmentStat ta = s.getTotalAssignment();

        GlobalAssignmentStat neededRelative = s.getNeededRelative();
        neededRelative
                .getExtra()
                .add(ta.getMissingAssignments());
        neededRelative
                .getExtraWeek()
                .add(ta.getMissingAssignments().copy().div(maxWeeks));
        neededRelative.setTeachersCount((int) Math.ceil(
                evalValueEquiv(neededRelative.getExtra(), assistant)
                / assistant.getValueDU()
        )
        );

        //calcul de la charge nécessaire selon le du des enseignant permanents
        //donc en gros combien on a besoin d'assistants pour ne plus recruter des contractuels et vacatires
        GlobalAssignmentStat neededByDue = s.getNeededAbsolute();
        double duAssistant = evalValueEquiv(s.getAssignment(null, Permanent, null).getDue(), assistant);
        double targetAssistant = evalValueEquiv(s.getTotalAssignment().getTargetAssignments(), assistant);
        neededByDue.getValue().setEquiv(duAssistant);
        neededByDue.getExtra().setEquiv(targetAssistant - duAssistant);
        neededByDue.getValueWeek().setEquiv(neededByDue.getValue().getEquiv() / maxWeeks);
        neededByDue.getExtraWeek().setEquiv(neededByDue.getValueWeek().getEquiv() / maxWeeks);
        neededByDue.setTeachersCount(
                (int) Math.ceil(
                        neededByDue.getExtra().getEquiv() / assistant.getValueDU()
                )
        );

        return s;
    }

    public void generate(TeacherGenerationOptions filter) throws IOException {
        generate(filter, null);
    }

    public void generate(TeacherGenerationOptions options, StatCache cache) throws IOException {
        if (cache == null) {
            cache = new StatCache();
        }
        Chronometer ch = new Chronometer();
        if (options == null) {
            options = new TeacherGenerationOptions();
        }
        Integer[] teacherIds = options.getTeacherIds();
        String semester = options.getSemester();
        String templateFolder = options.getTemplateFolder();
        String outputFolder = options.getOutputFolder();
        String outputNamePattern = options.getOutputNamePattern();

        if (!outputNamePattern.contains("*")) {
            outputNamePattern = "*-" + outputNamePattern;
        }
        //remove extensions!, remove leading path, retain just name part
        {
            PathInfo uu = PathInfo.create(outputNamePattern);
            outputNamePattern = uu.getNamePart();
        }
        List<TeacherStat> stats = null;
        Set<GeneratedContent> requestedContents = options.getContents() == null ? new HashSet() : new HashSet<>(Arrays.asList(options.getContents()));

        if (requestedContents.isEmpty()) {
            requestedContents.addAll(Arrays.asList(GeneratedContent.values()));
        }
        boolean writeVars = requestedContents.contains(GeneratedContent.Vars);
        final VirtualFileSystem fs = getFileSystem();
        if (requestedContents.contains(GeneratedContent.TeacherListAssignmentsSummary)) {
            if (stats == null) {
                stats = evalTeacherStatList(teacherIds, semester, options.isIncludeIntents(), cache);
            }
            String teacherListAssignmentsSummaryFile_template = templateFolder + "/teacher-list-assignments-summary-template.xls";

            generateTeacherListAssignmentsSummaryFile(stats.toArray(new TeacherStat[stats.size()]),
                    cache, fs.get(teacherListAssignmentsSummaryFile_template), fs.get(outputFolder + File.separator + outputNamePattern.replace("*", "teacher-list-assignments-summary") + ".xls"), writeVars);
        }
        String teacherAssignments_template = templateFolder + "/teacher-assignments-template.xls";
        String teacherAssignments_filename = outputFolder + File.separator + outputNamePattern.replace("*", "teacher-assignments") + ".xls";
        PathInfo uu = PathInfo.create(teacherAssignments_filename);
        if (requestedContents.contains(GeneratedContent.TeacherAssignments)) {
            if (stats == null) {
                stats = evalTeacherStatList(teacherIds, semester, options.isIncludeIntents(), cache);
            }
            generateTeacherAssignmentsFolder(stats.toArray(new TeacherStat[stats.size()]), cache, fs.get(teacherAssignments_template), fs.get(uu.getDirName() + File.separator + uu.getNamePart() + "-details/" + outputNamePattern + ".xls"), writeVars);
        }
        if (requestedContents.contains(GeneratedContent.GroupedTeacherAssignments)) {
            if (stats == null) {
                stats = evalTeacherStatList(teacherIds, semester, options.isIncludeIntents(), cache);
            }
            generateTeacherAssignmentsFile(stats.toArray(new TeacherStat[stats.size()]), cache, fs.get(teacherAssignments_template), fs.get(teacherAssignments_filename)
            );
        }
        if (requestedContents.contains(GeneratedContent.CourseListLoads)) {
            String courseListLoads_template = templateFolder + "/course-list-loads-template.xls";
            VFile t = fs.get(courseListLoads_template);
            generateCourseListLoadsFile(t, fs.get(outputFolder + File.separator + outputNamePattern.replace("*", "course-list-loads") + ".xls"));

            courseListLoads_template = templateFolder + "/course-assignments-template.xls";
            t = fs.get(courseListLoads_template);
            generateCourseListAssignmentsFile(t, fs.get(outputFolder + File.separator + outputNamePattern.replace("*", "course-assignments") + ".xls"));
        }
        if (requestedContents.contains(GeneratedContent.Bundle)) {
            Chronometer c2 = new Chronometer();
            String zipFile = outputFolder + File.separator + outputNamePattern.replace("*", "bundle") + ".zip";
            log.log(Level.FINE, "creating bundle {0} from {1}", new Object[]{(zipFile), outputFolder});
            VZipUtils.zip(fs.get(zipFile), new VZipOptions()
                    .setSkipRoots(true)
                    .setTempFileSystem(new NativeVFS())
                    .setTempFile(true),
                    fs.get(outputFolder)
            );
            log.log(Level.FINE, "created bundle in {0}", c2.stop());
        }
        System.out.println("data generated in " + ch.stop());
    }

    private void generateTeacherAssignmentsFile(TeacherStat[] stats, StatCache cache, VFile template, VFile output) throws IOException {
        if (stats.length == 0) {
            throw new IllegalArgumentException("No valid Teacher found");
        }
        try {
            Workbook workbook;
            InputStream in = null;
            try {
                try {
                    in = template.getInputStream();
                    workbook = Workbook.getWorkbook(in);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            } catch (BiffException ex) {
                throw new IOException(ex);
            }
            final VFile p = output.getParentFile();
            if (p != null) {
                p.mkdirs();
            }
            File ff = null;
            try {
                ff = File.createTempFile("tmp", "tmp." + output.getFileName().getShortExtension());
                WritableWorkbook copy = Workbook.createWorkbook(ff, workbook);

                int count0 = copy.getSheets().length;
                int count = count0;
                for (TeacherStat st : stats) {
                    copy.copySheet(0, AppContact.getName(st.getTeacher().getContact()), count);
                    WritableSheet sheet2 = copy.getSheet(count);
//                    sheet2.setName(st.getTeacher().getName());
                    ExcelTemplate.generateExcelSheet(sheet2, preparePrintableTeacherLoadProperties(st, cache));
                    count++;
                }
                while (count0 > 0) {
                    count0--;
                    copy.removeSheet(0);
                }
                copy.write();
                workbook.close();
                copy.close();

                VFS.createNativeFS().copyTo(ff.getPath(), output);
            } finally {
                if (ff != null) {
                    ff.delete();
                }
//                if (out != null) {
//                    out.close();
//                }
            }
        } catch (WriteException ex) {
            throw new IOException(ex);
        }
    }

    protected void generatePrintableTeacherLoadFile(int teacherId, boolean includeIntents, VFile template, VFile output, boolean writeVars, StatCache cache) throws IOException {
        Map<String, Object> p = preparePrintableTeacherLoadProperties(teacherId, includeIntents, cache);
        if (writeVars) {
            writeVars(p, output);
        }
        ExcelTemplate.generateExcel(template, output, p);
    }

    private void writeVars(Map<String, Object> p, VFile baseOutput) throws IOException {
        PathInfo uu = PathInfo.create(baseOutput.getPath());
        baseOutput.getFileSystem().get(uu.getDirName() + "/vars/").mkdirs();
        String varsFile = uu.getDirName() + "/vars/" + uu.getNamePart() + ".config";
        TreeSet<String> keys = new TreeSet<>(p.keySet());
        PrintStream out = null;
        try {
            out = new PrintStream(baseOutput.getFileSystem().getOutputStream(varsFile));
            for (String key : keys) {
                out.println(key + "=" + p.get(key));
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public void generateTeacherListAssignmentsSummaryFile(TeacherStat[] stats, StatCache cache, VFile template, VFile output, boolean writeVarFiles) throws IOException {
        Chronometer ch = new Chronometer();
        Map<String, Object> p = new HashMap<String, Object>();
        for (int i = 0; i < stats.length; i++) {
            TeacherStat stat = stats[i];
            Map<String, Object> p0 = preparePrintableTeacherLoadProperties(stat, cache);
            copyPrefixed(p0, p, "row(" + (i + 1) + ")");
        }
        if (writeVarFiles) {
            writeVars(p, output);
        }
        ExcelTemplate.generateExcel(template, output, p);
        log.log(Level.FINE, "generateTeacherListAssignmentsSummaryFile {0} teachers in {1}", new Object[]{stats.length, ch.stop()});
    }

    public void generateCourseListLoadsFile(VFile template, VFile output) throws IOException {
        Chronometer ch = new Chronometer();
        Map<String, Object> p = new HashMap<String, Object>();
        NamedDoubles inc = new NamedDoubles();

        for (AcademicCoursePlan c : findCoursePlans()) {

            String prefix;
//            prefix = "all.row";
//            prefix = prefix+"(" + (int)inc.inc(prefix) + ").";
//            fillCoursePlanProps(c, p, prefix);

            prefix = c.getProgram().getName() + "." + c.getCourseLevel().getName();
            prefix = prefix + "(" + (int) inc.inc(prefix) + ").";
            fillCoursePlanProps(c, p, prefix);
        }
        writeVars(p, output);
        ExcelTemplate.generateExcel(template, output, p);
        log.log(Level.FINE, "generateCourseListLoadsFile in {0}", new Object[]{ch.stop()});
    }

    public AcademicTeacher findHeadOfDepartment(int depId) throws IOException {
        for (AppUser u : core.findUsersByProfile(CorePlugin.PROFILE_HEAD_OF_DEPARTMENT)) {
            if (u.getDepartment() != null && u.getDepartment().getId() == depId) {
                return findTeacherByUser(u.getId());
            }
        }
        return null;
    }

    public void generateCourseListAssignmentsFile(VFile template, VFile output) throws IOException {
        Chronometer ch = new Chronometer();
        Map<String, Object> p = new HashMap<String, Object>();
        NamedDoubles inc = new NamedDoubles();

        for (AcademicCourseAssignment c : findCourseAssignments()) {

            String prefix;
//            prefix = "all.row";
//            prefix = prefix+"(" + (int)inc.inc(prefix) + ").";
//            fillCoursePlanProps(c, p, prefix);

            prefix = "row(" + (int) inc.inc("x") + ").";
            fillCourseAssignementPlanProps(c, p, prefix);
        }
        String year = (String) core.getOrCreateAppPropertyValue("academicPlugin.year", null, "2015-2016");
        String version = (String) core.getOrCreateAppPropertyValue("academicPlugin.import.version", null, "v01");
        p.put("version", version);
        p.put("year", year);
        writeVars(p, output);
        ExcelTemplate.generateExcel(template, output, p);
        log.log(Level.FINE, "generateCourseListLoadsFile in {0}", new Object[]{ch.stop()});
    }

    private void fillCourseAssignementPlanProps(AcademicCourseAssignment c, Map<String, Object> p, String prefix) {
        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        double wc = c.getCoursePlan().getWeeksC();
        if (wc == 0) {
            wc = 1;
        }
        double wtd = c.getCoursePlan().getWeeksTD();
        if (wtd == 0) {
            wtd = 1;
        }
        double wtp = c.getCoursePlan().getWeeksTP();
        if (wtp == 0) {
            wtp = 1;
        }
        double wpm = c.getCoursePlan().getWeeksPM();
        if (wpm == 0) {
            wpm = 1;
        }
        double wtppm = c.getCoursePlan().getWeeksTPPM();
        if (wtppm == 0) {
            wtppm = 1;
        }
        p.put(prefix + "name", c.getName());
        p.put(prefix + "name2", c.getName2());
        p.put(prefix + "type", StringUtils.nonnull(c.getCourseType() == null ? null : c.getCourseType().getName()));
        p.put(prefix + "discipline", c.getCoursePlan().getDiscipline());
        p.put(prefix + "roomConstraintsC", c.getCoursePlan().getRoomConstraintsC());
        p.put(prefix + "roomConstraintsTP", c.getCoursePlan().getRoomConstraintsTP());
        if (c.getValueC() != 0 && c.getValueTP() == 0) {
            p.put(prefix + "roomConstraints", c.getCoursePlan().getRoomConstraintsC());
        } else if (c.getValueC() == 0 && c.getValueTP() != 0) {
            p.put(prefix + "roomConstraints", c.getCoursePlan().getRoomConstraintsTP());
        } else {
            StringBuilder s = new StringBuilder();
            if (c.getValueC() != 0 && !StringUtils.isEmpty(c.getCoursePlan().getRoomConstraintsC())) {
                s.append("C:").append(c.getCoursePlan().getRoomConstraintsC().trim());
            }
            if (c.getValueTP() != 0 && !StringUtils.isEmpty(c.getCoursePlan().getRoomConstraintsTP())) {
                if (s.length() > 0) {
                    s.append(", ");
                }
                s.append("TP:").append(c.getCoursePlan().getRoomConstraintsTP().trim());
            }
            p.put(prefix + "roomConstraints", s.toString());
        }
        p.put(prefix + "sem.code", c.getCoursePlan().getSemester().getCode());
        p.put(prefix + "sem.name", c.getCoursePlan().getSemester().getName());
        p.put(prefix + "sem.name2", c.getCoursePlan().getSemester().getName2());
        p.put(prefix + "ue", c.getCoursePlan().getCourseGroup() == null ? "" : c.getCoursePlan().getCourseGroup().getName());
        p.put(prefix + "lvl", c.getCoursePlan().getCourseLevel().getName());
        p.put(prefix + "grp", c.getGroupCount());
        p.put(prefix + "share", c.getShareCount());
        p.put(prefix + "c.grp", c.getCoursePlan().getGroupCountC());
        p.put(prefix + "td.grp", c.getCoursePlan().getGroupCountTD());
        p.put(prefix + "tp.grp", c.getCoursePlan().getGroupCountTP());
        p.put(prefix + "pm.grp", c.getCoursePlan().getGroupCountPM());
        p.put(prefix + "tppm.grp", c.getCoursePlan().getGroupCountTPPM());
        p.put(prefix + "c", c.getValueC());
        p.put(prefix + "td", c.getValueTD());
        p.put(prefix + "tp", c.getValueTP());
        p.put(prefix + "pm", c.getValuePM());

        p.put(prefix + "w.c", wc);
        p.put(prefix + "w.td", wtd);
        p.put(prefix + "w.tp", wtp);
        p.put(prefix + "w.pm", wpm);
        p.put(prefix + "w.tppm", wtppm);

        p.put(prefix + "w1.c", c.getValueC() / wc);
        p.put(prefix + "w1.td", c.getValueTD() / wtd);
        p.put(prefix + "w1.tp", c.getValueTP() / wtp);
        p.put(prefix + "w1.pm", c.getValuePM() / wpm);
        p.put(prefix + "w1.tppm", c.getCoursePlan().getValueTPPM() / wtppm);

        p.put(prefix + "class.name", c.getCoursePlan().getStudentClass().getName());
        p.put(prefix + "class.name2", c.getCoursePlan().getStudentClass().getName2());
        p.put(prefix + "program.name", c.getCoursePlan().getProgram().getName());
        p.put(prefix + "program.name2", c.getCoursePlan().getProgram().getName2());
        p.put(prefix + "department.code", c.getCoursePlan().getProgram().getDepartment().getCode());
        p.put(prefix + "department.name", c.getCoursePlan().getProgram().getDepartment().getName());
        p.put(prefix + "department.name2", c.getCoursePlan().getProgram().getDepartment().getName2());
        p.put(prefix + "teacher.name", c.getTeacher() == null ? null : getValidName(c.getTeacher()));
        p.put(prefix + "teacher.name2", (c.getTeacher() == null || c.getTeacher().getContact() == null) ? null : c.getTeacher().getContact().getFullName2());
        p.put(prefix + "teacher.discipline", c.getTeacher() == null ? null : c.getTeacher().getDiscipline());
    }

    private void fillCoursePlanProps(AcademicCoursePlan c, Map<String, Object> p, String prefix) {
        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        double wc = c.getWeeksC();
        if (wc == 0) {
            wc = 1;
        }
        double wtd = c.getWeeksTD();
        if (wtd == 0) {
            wtd = 1;
        }
        double wtp = c.getWeeksTP();
        if (wtp == 0) {
            wtp = 1;
        }
        double wpm = c.getWeeksPM();
        if (wpm == 0) {
            wpm = 1;
        }
        double wtppm = c.getWeeksTPPM();
        if (wtppm == 0) {
            wtppm = 1;
        }
        p.put(prefix + "name", c.getName());
        p.put(prefix + "name2", c.getName2());
        p.put(prefix + "sem.code", c.getSemester().getCode());
        p.put(prefix + "sem.name", c.getSemester().getName());
        p.put(prefix + "sem.name2", c.getSemester().getName2());
        p.put(prefix + "ue", c.getCourseGroup() == null ? "" : c.getCourseGroup().getName());
        p.put(prefix + "lvl", c.getCourseLevel().getName());
        p.put(prefix + "c.grp", c.getGroupCountC());
        p.put(prefix + "td.grp", c.getGroupCountTD());
        p.put(prefix + "tp.grp", c.getGroupCountTP());
        p.put(prefix + "pm.grp", c.getGroupCountPM());
        p.put(prefix + "tppm.grp", c.getGroupCountTPPM());
        p.put(prefix + "c", c.getValueC());
        p.put(prefix + "td", c.getValueTD());
        p.put(prefix + "tp", c.getValueTP());
        p.put(prefix + "pm", c.getValuePM());

        p.put(prefix + "w.c", wc);
        p.put(prefix + "w.td", wtd);
        p.put(prefix + "w.tp", wtp);
        p.put(prefix + "w.pm", wpm);
        p.put(prefix + "w.tppm", wtppm);

        p.put(prefix + "w1.c", c.getValueC() / wc);
        p.put(prefix + "w1.td", c.getValueTD() / wtd);
        p.put(prefix + "w1.tp", c.getValueTP() / wtp);
        p.put(prefix + "w1.pm", c.getValuePM() / wpm);
        p.put(prefix + "w1.tppm", c.getValueTPPM() / wtppm);

        p.put(prefix + "class.name", c.getStudentClass().getName());
        p.put(prefix + "class.name2", c.getStudentClass().getName2());
        p.put(prefix + "program.name", c.getProgram().getName());
        p.put(prefix + "class.name2", c.getProgram().getName2());
        p.put(prefix + "department.name", c.getProgram().getDepartment().getName());
        p.put(prefix + "department.name2", c.getProgram().getDepartment().getName2());
    }

//    protected void generatePrintableTeacherListLoadFile(int yearId, Integer[] teacherIds, String semester, String template, String output) throws IOException {
//        TeacherStat[] stats = evalTeachersStat(yearId, teacherIds, semester);
//        generateTeacherListAssignmentsSummaryFile(yearId, stats, template, output);
//    }
//    private void generatePrintableTeacherLoadSheet(int yearId, int teacherId, WritableSheet sheet) throws IOException {
//        ExcelTemplate.generateExcelSheet(sheet, preparePrintableTeacherLoadProperties(yearId,teacherId));
//    }
    private static String dformat(double d) {
        return FF.format(d);
    }

    private static String iformat(double d) {
        if (Math.floor(d) == d) {
            return String.valueOf((int) d);
        }
        return FF.format(d);
    }

    private Map<String, Object> copyPrefixed(Map<String, Object> p, Map<String, Object> p2, String prefix) {
        if (p2 == null) {
            p2 = new HashMap<String, Object>();
        }
        if (prefix == null || prefix.isEmpty()) {
            prefix = "";
        } else if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        for (Map.Entry<String, Object> entrySet : p.entrySet()) {
            p2.put(prefix + entrySet.getKey(), entrySet.getValue());
        }
        return p2;
    }

    public Map<String, Object> preparePrintableTeacherLoadProperties(int teacher, boolean includeIntents, StatCache cache) throws IOException {
        return preparePrintableTeacherLoadProperties(evalTeacherStat(teacher, null, null, null, includeIntents, cache), cache);
    }

    private Map<String, Object> preparePrintableTeacherLoadProperties(TeacherStat stat, StatCache cache) throws IOException {
        AcademicTeacher t = stat.getTeacher();
        if (t == null) {
            throw new IllegalArgumentException("Teacher not found ");
        }
        AcademicTeacher tal = stat.getTeacher();
        Map<String, Object> p = new HashMap<>();
        p.put("teacher.name", AppContact.getName(t.getContact()));
        p.put("teacher.firstName", t.getContact().getFirstName());
        p.put("teacher.lastName", t.getContact().getLastName());
        p.put("teacher.degree", tal.getDegree() == null ? null : tal.getDegree().getName());
        p.put("teacher.situation", tal.getSituation().getName());
        for (TeacherSemesterStat semester : stat.getSemesters()) {
            AcademicSemester sem = semester.getSemester();
            int moduleIndex = 1;
            String semesterPrefix = "sem(" + sem.getName() + ")";
            for (AcademicCourseAssignment m : findCourseAssignments(t.getId(), sem.getName(), stat.isIncludeIntents(), cache)) {
                if ((Math.abs(m.getValueC()) + Math.abs(m.getValuePM()) + Math.abs(m.getValueTD()) + Math.abs(m.getValueTP())) * Math.abs(m.getGroupCount() * m.getShareCount()) != 0) {
                    String modulePrefix = semesterPrefix + ".mod(" + moduleIndex + ")";
                    p.put(modulePrefix + ".name", m.getName());
                    p.put(modulePrefix + ".c", m.getValueC() != 0 ? dformat(m.getValueC()) : "");
                    p.put(modulePrefix + ".td", m.getValueTD() != 0 ? dformat(m.getValueTD()) : "");
                    p.put(modulePrefix + ".tp", m.getValueTP() != 0 ? dformat(m.getValueTP()) : "");
                    p.put(modulePrefix + ".pm", m.getValuePM() != 0 ? dformat(m.getValuePM()) : "");
                    p.put(modulePrefix + ".grp", iformat(m.getGroupCount()));
                    p.put(modulePrefix + ".sh", iformat(m.getShareCount()));
                    p.put(modulePrefix + ".grpsh", iformat(m.getGroupCount() * m.getShareCount()));
                    AcademicCoursePlan coursePlan = m.getCoursePlan();
                    p.put(modulePrefix + ".class", StringUtils.nonnull(coursePlan.getStudentClass()));
                    p.put(modulePrefix + ".level", StringUtils.nonnull(coursePlan.getCourseLevel()));
                    p.put(modulePrefix + ".type", StringUtils.nonnull(m.getCourseType()));
                    p.put(modulePrefix + ".program", StringUtils.nonnull(coursePlan.getProgram()));
                    moduleIndex++;
                }
            }
            p.put(semesterPrefix + ".c", dformat(semester.getValue().getC()));
            p.put(semesterPrefix + ".td", dformat(semester.getValue().getTd()));
            p.put(semesterPrefix + ".tp", dformat(semester.getValue().getTp()));
            p.put(semesterPrefix + ".pm", dformat(semester.getValue().getPm()));
            p.put(semesterPrefix + ".tppm", dformat(semester.getValue().getTppm()));
            p.put(semesterPrefix + ".c.w", dformat(semester.getValueWeek().getC()));
            p.put(semesterPrefix + ".td.w", dformat(semester.getValueWeek().getTd()));
            p.put(semesterPrefix + ".tp.w", dformat(semester.getValueWeek().getTp()));
            p.put(semesterPrefix + ".pm.w", dformat(semester.getValueWeek().getPm()));
            p.put(semesterPrefix + ".tppm.w", dformat(semester.getValueWeek().getTppm()));
            p.put(semesterPrefix + ".load", dformat(semester.getValue().getEquiv()));
            p.put(semesterPrefix + ".load.w", dformat(semester.getValueWeek().getEquiv()));
            p.put(semesterPrefix + ".extra", dformat(semester.getExtraWeek().getEquiv()));
            p.put(semesterPrefix + ".extra.c", dformat(semester.getExtraWeek().getC()));
            p.put(semesterPrefix + ".extra.td", dformat(semester.getExtraWeek().getTd()));
            p.put(semesterPrefix + ".extra.tp", dformat(semester.getExtraWeek().getTp()));
            p.put(semesterPrefix + ".extra.tppm", dformat(semester.getExtraWeek().getTppm()));
            p.put(semesterPrefix + ".w", iformat(semester.getWeeks()));
            p.put(semesterPrefix + ".mw", iformat(semester.getMaxWeeks()));
        }
        p.put("tot.load", dformat(stat.getValue().getEquiv()));
        p.put("tot.load.w", dformat(stat.getValueWeek().getEquiv()));
        p.put("tot.w", dformat(stat.getWeeks()));
        p.put("tot.mw", dformat(stat.getMaxWeeks()));
        p.put("tot.extra", dformat(stat.getExtraWeek().getEquiv()));
        p.put("tot.extra.c", dformat(stat.getExtraWeek().getC()));
        p.put("tot.extra.td", dformat(stat.getExtraWeek().getTd()));
        p.put("tot.extra.tp", dformat(stat.getExtraWeek().getTp()));
        p.put("tot.extra.tppm", dformat(stat.getExtraWeek().getTppm()));
        p.put("du.c", dformat(stat.getDueWeek().getC()));
        p.put("du.td", dformat(stat.getDueWeek().getTd()));
        p.put("du.tp", dformat(stat.getDueWeek().getTp()));
        p.put("du.tppm", dformat(stat.getDueWeek().getTppm()));
        return p;
    }

    public int importFile(VFile folder, ImportOptions importOptions) throws IOException {
        XlsxLoadImporter i = new XlsxLoadImporter();
        return i.importFile(folder, importOptions);
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

    public List<AppCivility> findCivilities() {
        return UPA.getPersistenceUnit().findAll(AppCivility.class);
    }

    public List<AcademicTeacher> findTeachers() {
        return UPA.getPersistenceUnit().findAll(AcademicTeacher.class);
    }

    public List<AcademicTeacher> findEnabledTeachers() {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicTeacher u where u.deleted=false and u.enabled=true").getEntityList();
    }

    public List<AcademicStudent> findStudents() {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicStudent u where u.deleted=false").getEntityList();
    }

    public List<AcademicFormerStudent> findGraduatedStudents() {
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicFormerStudent u where u.deleted=false and u.graduated=true").getEntityList();
    }

    public List<AcademicTeacher> findTeachersWithAssignementsOrIntents() {
        return UPA.getPersistenceUnit()
                .createQuery("Select u from AcademicTeacher u where u.id in ("
                        + " Select t.id from AcademicTeacher t "
                        + " left join AcademicCourseAssignment a on a.teacheId=t.id"
                        + " left join AcademicCourseIntent i on i.teacherId=t.id"
                        + " where (a is not null) or (i is not null)"
                        + ") order by u.contact.fullName")
                .getEntityList();
    }

    public List<AcademicTeacher> findTeachersWithAssignements() {
        return UPA.getPersistenceUnit()
                .createQuery("Select u from AcademicTeacher u where u.id in ("
                        + " Select t.id from AcademicTeacher t "
                        + " inner join AcademicCourseAssignment a on a.teacherId=t.id"
                        + ") order by u.contact.fullName")
                .getEntityList();
    }

    public List<AcademicProgram> findPrograms() {
        return UPA.getPersistenceUnit()
                .createQuery(
                        "Select a from AcademicProgram a order by a.name")
                .getEntityList();
    }

    public List<AppGender> findGenders() {
        return UPA.getPersistenceUnit().findAll(AppGender.class);
    }

    public List<AcademicTeacherDegree> findTeacherDegrees() {
        return UPA.getPersistenceUnit()
                .createQuery(
                        "Select a from AcademicTeacherDegree a")
                .getEntityList();
    }

    public List<AcademicTeacherSituation> findTeacherSituations() {
        return UPA.getPersistenceUnit().findAll(AcademicTeacherSituation.class);
    }

    public List<AcademicClass> findAcademicClasses() {
        return UPA.getPersistenceUnit().findAll(AcademicClass.class);
    }

    public List<AcademicCourseLevel> findCourseLevels() {
        return UPA.getPersistenceUnit().findAll(AcademicCourseLevel.class);
    }

    public List<AcademicSemester> findSemesters() {
        return UPA.getPersistenceUnit().createQueryBuilder(AcademicSemester.class).setOrder(new Order().addOrder(new Var("name"), true))
                .getEntityList();
    }

    public AcademicProgram findProgram(int departmentId, String t) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicProgram a where a.name=:t and a.departmentId=:departmentId")
                .setParameter("t", t)
                .setParameter("departmentId", departmentId)
                .getEntity();
    }

    public AcademicSemester findSemester(String code) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicSemester a where a.code=:code or a.name=:code or a.name2=:code")
                .setParameter("code", code)
                .getSingleEntityOrNull();
    }

    public AppDepartment findDepartment(String code) {
        return VrApp.getBean(CorePlugin.class).findDepartment(code);
    }

    public AppCivility findCivility(String t) {
        return VrApp.getBean(CorePlugin.class).findCivility(t);
    }

    public AcademicCoursePlan findCoursePlan(int studentClassId, int semesterId, String courseName) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicCoursePlan a where a.name=:courseName and a.semesterId=:semesterId and a.studentClassId=:studentClassId")
                .setParameter("courseName", courseName)
                .setParameter("semesterId", semesterId)
                .setParameter("studentClassId", studentClassId)
                .getEntity();
    }

    public AppGender findGender(String t) {
        return (AppGender) UPA.getPersistenceUnit().findByMainField(AppGender.class, t);
    }

    public AcademicTeacher findTeacher(String t) {
        return (AcademicTeacher) UPA.getPersistenceUnit().createQuery("Select u from AcademicTeacher u where u.contact.fullName=:name").setParameter("name", t).getEntity();
    }

    public AcademicStudent findStudent(String t) {
        return (AcademicStudent) UPA.getPersistenceUnit().createQuery("Select u from AcademicStudent u where u.contact.fullName=:name").setParameter("name", t).getEntity();
    }

    public AcademicTeacherDegree findTeacherDegree(String t) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicTeacherDegree a where a.code=:t")
                .setParameter("t", t)
                .getEntity();
    }

    public AcademicTeacherSituation findTeacherSituation(String t) {
        return (AcademicTeacherSituation) UPA.getPersistenceUnit().findByMainField(AcademicTeacherSituation.class, t);
    }

    public AcademicCourseLevel findCourseLevel(int programId, String name) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicCourseLevel a where a.name=:name and a.programId=:programId")
                .setParameter("name", name)
                .setParameter("programId", programId)
                .getEntity();
    }

    public AcademicCourseLevel findCourseLevel(String name) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicCourseLevel a where a.name=:name")
                .setParameter("name", name)
                .getEntity();
    }

    public AcademicCourseGroup findCourseGroup(int courseLevelId, String name) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicCourseGroup a where a.name=:name and a.courseLevelId=:courseLevelId")
                .setParameter("name", name)
                .setParameter("courseLevelId", courseLevelId)
                .getEntity();
    }

    public List<AcademicCourseGroup> findCourseGroups() {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicCourseGroup a")
                .getEntityList();
    }

    public AcademicDiscipline findDiscipline(String nameOrCode) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicDiscipline a where a.code=:code or a.name=:code or a.name2=:code")
                .setParameter("code", nameOrCode)
                .getEntity();
    }

    public AcademicCourseType findCourseType(String name) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseType a where "
                + "a.name=:name")
                .setParameter("name", name)
                .getEntity();
    }

    public List<AcademicCourseType> findCourseTypes() {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseType a ")
                .getEntityList();
    }

    /**
     *
     */
    public void resetModuleTeaching() {
        resetCurrentYear();
        resetHistAcademicYears();
        trace.trace("resetModuleTeaching", "reset Module Academic", null, getClass().getSimpleName(), Level.FINE);
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
        trace.trace("resetCurrentYear", "reset Module Academic", null, getClass().getSimpleName(), Level.FINE);
    }

    public AcademicCourseAssignment findCourseAssignment(int courseAssignmentId) {
        return (AcademicCourseAssignment) UPA.getPersistenceUnit().findById(AcademicCourseAssignment.class, courseAssignmentId);
    }

    public AcademicTeacher findTeacher(int t) {
        return (AcademicTeacher) UPA.getPersistenceUnit().findById(AcademicTeacher.class, t);
    }

    public AcademicStudent findStudent(int t) {
        return (AcademicStudent) UPA.getPersistenceUnit().findById(AcademicStudent.class, t);
    }

    public void updateViewsCounterforTeacherCV(int t) {
        AcademicTeacherCV cv = findOrCreateAcademicTeacherCV(t);
        if (cv != null) {
            cv.setViewsCounter(cv.getViewsCounter() + 1);
            UPA.getPersistenceUnit().merge(cv);
        }
    }

    public AcademicTeacherCV findOrCreateAcademicTeacherCV(final int t) {
        return UPA.getContext().invokePrivileged(new Action<AcademicTeacherCV>() {

            @Override
            public AcademicTeacherCV run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                AcademicTeacherCV a = (AcademicTeacherCV) pu.createQuery("Select u from AcademicTeacherCV u where u.teacherId=:id")
                        .setParameter("id", t).getEntity();
                if (a != null) {
                    return a;
                }
                //check teacher
                AcademicTeacher teacher = findTeacher(t);
                if (teacher != null) {
                    final AcademicTeacherCV cv = new AcademicTeacherCV();
                    cv.setTeacher(teacher);
                    UPA.getPersistenceUnit().persist(cv);
                    return cv;
                }
                return null;
            }
        }, null);
    }

    public List<AcademicCourseAssignment> findCourseAssignments() {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a order by a.coursePlan.semester.code,a.coursePlan.program.name,a.name,a.courseType.name")
                .setHint("navigationDepth", 5)
                .getEntityList();
    }

    public List<AcademicCoursePlan> findCoursePlans() {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCoursePlan a")
                .getEntityList();
    }

    public List<AcademicCourseAssignment> findCourseAssignmentsByPlan(int planId) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicCourseAssignment a where a.coursePlanId=:v")
                .setParameter("v", planId).getEntityList();
    }

    public List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoads(int teacherId) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicTeacherSemestrialLoad a where a.teacherId=:t")
                .setParameter("t", teacherId)
                .getEntityList();
    }

    public List<AcademicTeacherSemestrialLoad> findTeacherSemestrialLoads() {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicTeacherSemestrialLoad a")
                .getEntityList();
    }

    public AcademicClass findStudentClass(String t) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicClass a where a.name=:t")
                .setParameter("t", t)
                .getEntity();
    }

    public AcademicClass findStudentClass(int programId, String t) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicClass a where a.name=:t and a.programId=:programId")
                .setParameter("t", t)
                .setParameter("programId", programId)
                .getEntity();
    }

    public void updateAllCoursePlanValuesByLoadValues() {
        Chronometer ch = new Chronometer();
        for (AcademicCoursePlan coursePlan : findCoursePlans()) {
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
        return (AcademicCoursePlan) UPA.getPersistenceUnit().findById(AcademicCoursePlan.class, id);
    }

    protected VirtualFileSystem getFileSystem() {
        return this.fileSystemPlugin.getFileSystem();
    }

    ////////////////////////////////////////////////////////////////////////////
    public AppPeriod addAcademicYearSnapshot(String year, String snapshotName) {
        AppPeriod y = createAcademicYear(year, snapshotName);
        AppPeriod s = new AppPeriod();
        s.setCreationTime(new DateTime());
        s.setName(y.getName());
        s.setSnapshotName(snapshotName);
        UPA.getPersistenceUnit().persist(s);
        Map<String, AcademicHistTeacherDegree> histTeacherDegreeMap = new HashMap<>();
        for (AcademicTeacherDegree m : findTeacherDegrees()) {
            AcademicHistTeacherDegree h = new AcademicHistTeacherDegree();
            h.setCode(m.getCode());
            h.setName(m.getName());
            h.setName2(m.getName2());
            h.setPosition(m.getPosition());
            h.setValueC(m.getValueC());
            h.setValueDU(m.getValueDU());
            h.setValuePM(m.getValuePM());
            h.setValueTD(m.getValueTD());
            h.setValueTP(m.getValueTP());
            h.setAcademicYear(s);
            histTeacherDegreeMap.put(h.getCode(), h);
            add(h);
        }
        Map<Integer, AcademicHistTeacherAnnualLoad> teacherToLoad = new HashMap<>();
        for (AcademicTeacher m : findTeachers()) {
            AcademicHistTeacherAnnualLoad h = new AcademicHistTeacherAnnualLoad();
            h.setAcademicYear(s);
            h.setDegree(histTeacherDegreeMap.get(m.getDegree() == null ? null : m.getDegree().getName()));
            h.setSituation(m.getSituation());
            h.setTeacher(m);
            add(h);
            teacherToLoad.put(m.getId(), h);
        }
        for (AcademicTeacherSemestrialLoad m : findTeacherSemestrialLoads()) {
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
        for (AcademicCourseGroup m : findCourseGroups()) {
            AcademicHistCourseGroup h = new AcademicHistCourseGroup();
            h.setAcademicYear(s);
            h.setCourseLevel(m.getCourseLevel());
            add(h);
            academicHistCourseGroups.put(m.getId(), h);
        }

        for (AcademicCoursePlan m : findCoursePlans()) {
            AcademicHistCoursePlan h = new AcademicHistCoursePlan();

            h.setAcademicYear(s);
            h.setProgram(m.getProgram() == null ? null : academicHistCoursePrograms.get(m.getProgram().getId()));
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

            h.setStudentClass(m.getStudentClass());
            h.setSemester(m.getSemester());
            add(h);
        }
        return s;
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

    public List<AcademicHistCourseAssignment> findHistCourseAssignments(int yearId, Integer teacher, String semester) {
        List<AcademicHistCourseAssignment> m = new ArrayList<>();
        for (AcademicHistCourseAssignment value : findHistCourseAssignments(yearId)) {
            if (teacher == null || (value.getTeacher() != null && value.getTeacher().getId() == (teacher))) {
                if (semester == null || (value.getCoursePlan().getSemester() != null && value.getCoursePlan().getSemester().getName().equals(semester))) {
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
                .getEntityList();
    }

    public List<AcademicHistTeacherDegree> findHistTeacherDegrees(int yearId) {
        return UPA.getPersistenceUnit()
                .createQuery(
                        "Select a from AcademicHistTeacherDegree a where a.academicYearId=:x")
                .setParameter("x", yearId)
                .getEntityList();
    }

    public AcademicHistTeacherDegree findHistTeacherDegree(int yearId, String t) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AcademicHistTeacherDegree a where a.name=:t and a.academicYearId=:y")
                .setParameter("t", t)
                .setParameter("y", yearId)
                .getEntity();
    }

    public void resetHistAcademicYear(int year) {
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherSemestrialLoad where academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherAnnualLoad where academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherDegree where academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseAssignment a where a.coursePlan.academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCoursePlan a where a.academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseGroup a where a.courseLevel.program.academiYear=:y").setParameter("y", year).executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistProgram a where a.academiYear=:y").setParameter("y", year).executeNonQuery();
        trace.trace("resetAcademicYear", "reset Academic Year", String.valueOf(year), getClass().getSimpleName(), Level.FINE);
    }

    public void resetHistAcademicYears() {
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherSemestrialLoad").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherAnnualLoad").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistTeacherDegree").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseAssignment").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCoursePlan").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistCourseGroup").executeNonQuery();
        UPA.getPersistenceUnit().createQuery("delete from AcademicHistProgram").executeNonQuery();
        trace.trace("resetHistAcademicYears", "reset Academic Years", "", getClass().getSimpleName(), Level.FINE);
    }

    public List<AcademicHistTeacherAnnualLoad> findHistTeacherAnnualLoads(int year) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistTeacherAnnualLoad a where a.academicYearId=:v")
                .setParameter("v", year)
                .getEntityList();
    }

    public AcademicHistTeacherAnnualLoad findHistTeacherAnnualLoad(int year, int teacherId) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistTeacherAnnualLoad a where a.academicYearId=:v and a.teacherId=:t")
                .setParameter("v", year)
                .setParameter("t", teacherId)
                .getEntity();
    }

    public List<AcademicHistTeacherSemestrialLoad> findHistTeacherSemestrialLoads(int year, int teacherId) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicTeacherSemestrialLoad a where a.annualLoad.academicYearId=:v and a.annualLoad.teacherId=:t")
                .setParameter("v", year)
                .setParameter("t", teacherId)
                .getEntityList();
    }

    public List<AcademicHistTeacherSemestrialLoad> findHistTeacherSemestrialLoads(int year) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistTeacherSemestrialLoad a where a.annualLoad.academicYearId=:v")
                .setParameter("v", year)
                .getEntityList();
    }

    public List<AcademicHistCourseAssignment> findHistCourseAssignments(int yearId) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistCourseAssignment a where a.coursePlan.academicYearId=:v")
                .setParameter("v", yearId).getEntityList();
    }

    public List<AcademicHistCoursePlan> findHistCoursePlans(int year) {
        return UPA.getPersistenceUnit().createQuery("Select a from AcademicHistCoursePlan a where a.academicYearId=:v")
                .setParameter("v", year).getEntityList();
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

    public AcademicTeacher getCurrentTeacher() {
        UserSession sm = VrApp.getBean(UserSession.class);
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user != null) {
            return findTeacherByUser(user.getId());
        }
        return null;
    }

    public AcademicStudent getCurrentStudent() {
        UserSession sm = VrApp.getBean(UserSession.class);
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user != null) {
            return findStudentByUser(user.getId());
        }
        return null;
    }

    @Start
    public void startService() {

    }

    @Install
    public void installService() {
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

        AppUserType teacherType;
        teacherType = new AppUserType();
        teacherType.setName("Teacher");
        teacherType = core.findOrCreate(teacherType);

        AppUserType studentType;
        studentType = new AppUserType();
        studentType.setName("Student");
        studentType = core.findOrCreate(studentType);

        AppProfile teacherProfile;
        teacherProfile = new AppProfile();
        teacherProfile.setName("Teacher");
        teacherProfile = core.findOrCreate(teacherProfile);

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
        AppProfile studentProfile;
        studentProfile = new AppProfile();
        studentProfile.setName("Student");
        studentProfile = core.findOrCreate(studentProfile);

        core.addProfileRight(studentProfile.getId(), "Custom.FileSystem.MyFileSystem");

        AppProfile headOfDepartment;
        headOfDepartment = new AppProfile();
        headOfDepartment.setName(CorePlugin.PROFILE_HEAD_OF_DEPARTMENT);
        headOfDepartment = core.findOrCreate(headOfDepartment);

        core.addProfileRight(headOfDepartment.getId(), "Custom.Education.TeacherCourseLoad");
        core.addProfileRight(headOfDepartment.getId(), "Custom.Education.GlobalStat");
        core.addProfileRight(headOfDepartment.getId(), "Custom.Education.AllTeachersCourseLoad");
        core.addProfileRight(headOfDepartment.getId(), "Custom.Education.CourseLoadUpdateIntents");
        core.addProfileRight(headOfDepartment.getId(), "Custom.Education.CourseLoadUpdateAssignments");
        core.addProfileRight(headOfDepartment.getId(), "Custom.FileSystem.MyFileSystem");

        for (net.vpc.upa.Entity ee : UPA.getPersistenceUnit().getPackage("Education").getEntities(true)) {
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Persist");
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Remove");
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Update");
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Navigate");
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Load");
            core.addProfileRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".DefaultEditor");
        }

        AppProfile directorOfStudies;
        directorOfStudies = new AppProfile();
        directorOfStudies.setName("DirectorOfStudies");
        directorOfStudies = core.findOrCreate(directorOfStudies);

        core.addProfileRight(directorOfStudies.getId(), "Custom.Education.TeacherCourseLoad");
        core.addProfileRight(directorOfStudies.getId(), "Custom.Education.GlobalStat");
        core.addProfileRight(directorOfStudies.getId(), "Custom.Education.AllTeachersCourseLoad");
        core.addProfileRight(directorOfStudies.getId(), "Custom.FileSystem.MyFileSystem");

        for (net.vpc.upa.Entity ee : UPA.getPersistenceUnit().getPackage("Education").getEntities(true)) {
            core.addProfileRight(directorOfStudies.getId(), ee.getAbsoluteName() + ".Navigate");
            core.addProfileRight(directorOfStudies.getId(), ee.getAbsoluteName() + ".Load");
            core.addProfileRight(directorOfStudies.getId(), ee.getAbsoluteName() + ".DefaultEditor");
        }

        AppProfile director;
        director = new AppProfile();
        director.setName("Director");
        director = core.findOrCreate(director);

        core.addProfileRight(director.getId(), "Custom.Education.TeacherCourseLoad");
        core.addProfileRight(director.getId(), "Custom.Education.GlobalStat");
        core.addProfileRight(director.getId(), "Custom.Education.AllTeachersCourseLoad");
        core.addProfileRight(director.getId(), "Custom.FileSystem.MyFileSystem");

        for (net.vpc.upa.Entity ee : UPA.getPersistenceUnit().getPackage("Education").getEntities(true)) {
            core.addProfileRight(director.getId(), ee.getAbsoluteName() + ".Navigate");
            core.addProfileRight(director.getId(), ee.getAbsoluteName() + ".Load");
            core.addProfileRight(director.getId(), ee.getAbsoluteName() + ".DefaultEditor");
        }
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

    public void importTeachingLoad() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        try {
            String year = (String) core.getOrCreateAppPropertyValue("academicPlugin.year", null, "2015-2016");
            String version = (String) core.getOrCreateAppPropertyValue("academicPlugin.import.version", null, "v01");
            String dir = (String) core.getOrCreateAppPropertyValue("academicPlugin.import.configFolder", null, "/Config/Import/${year}");
            Map<String, String> vars = new HashMap<>();
            vars.put("home", System.getProperty("user.home"));
            vars.put("year", year);
            vars.put("version", version);

            dir = StringUtils.replaceDollarPlaceHolders(dir, new MapStringConverter(vars));

            String dataFolder = dir + "/data";

            AcademicPlugin s = VrApp.getBean(AcademicPlugin.class);

            net.vpc.common.vfs.VirtualFileSystem fs = this.fileSystemPlugin.getFileSystem();
            s.resetModuleTeaching();
            s.importFile(
                    fs.get(dataFolder),
                    new ImportOptions()
            );
        } catch (Exception ex) {
            Logger.getLogger(XlsxLoadImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateTeachingLoad() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        try {
            String year = (String) core.getOrCreateAppPropertyValue("academicPlugin.year", null, "2015-2016");
            String version = (String) core.getOrCreateAppPropertyValue("academicPlugin.import.version", null, "v01");
            String dir = (String) core.getOrCreateAppPropertyValue("academicPlugin.import.configFolder", null, "/Config/Import/import/${year}");
            String namePattern = (String) core.getOrCreateAppPropertyValue("academicPlugin.import.namePattern", null, "*-eniso-ii-${year}-${version}");
            Map<String, String> vars = new HashMap<>();
            vars.put("home", System.getProperty("user.home"));
            vars.put("year", year);
            vars.put("version", version);

            dir = StringUtils.replaceDollarPlaceHolders(dir, new MapStringConverter(vars));

            String outdir = (String) core.getOrCreateAppPropertyValue("academicPlugin.outputFolder", null, "/Output/${year}/${version}");

            outdir = StringUtils.replaceDollarPlaceHolders(outdir, new MapStringConverter(vars));

//            String dataFolder = dir + "/data";
            String templatesFolder = dir + "/templates";

            namePattern = StringUtils.replaceDollarPlaceHolders(namePattern, new MapStringConverter(vars));

            AcademicPlugin s = VrApp.getBean(AcademicPlugin.class);

            net.vpc.common.vfs.VirtualFileSystem fs = getFileSystem();
            fs.get(outdir).mkdirs();
            //TODO should export from DB all this information
//            VFS.copy(fs.get(dataFolder), fs.get(outdir), new VFileFilter() {
//
//                @Override
//                public boolean accept(VFile pathname) {
//                    return pathname.isDirectory() || (pathname.isFile()
//                            && (pathname.getName().toLowerCase().endsWith(".xls")
//                            || pathname.getName().toLowerCase().endsWith(".xlsx")));
//                }
//            });
            s.generate(
                    new TeacherGenerationOptions()
                    //                    .setTeacherIds(new Integer[]{s.findTeacher(StringComparators.ilike("*zarrouk*")).getId()})
                    //                    .setSemester(null)
                    .setContents(
                            //                            GeneratedContent.CourseListLoads,
                            GeneratedContent.GroupedTeacherAssignments,
                            GeneratedContent.TeacherAssignments,
                            GeneratedContent.CourseListLoads,
                            GeneratedContent.TeacherListAssignmentsSummary,
                            GeneratedContent.Bundle
                    )
                    .setTemplateFolder(templatesFolder)
                    .setOutputFolder(outdir)
                    .setOutputNamePattern(namePattern)
                    .setIncludeIntents(true)
            );
//            XMailService mails = new XMailService();
//            XMail m=mails.read(XMailFormat.TEXT, new File(dir+"/notification-charge.xmail"));
//            m.setSimulate(true);
//            mails.send(m);
//            for () {
//                break;
//            }
        } catch (Exception ex) {
            Logger.getLogger(XlsxLoadImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addUserForTeacher(AcademicTeacher academicTeacher) {
        AppUser u = core.findUserByContact(academicTeacher.getContact().getId());
        if (u == null) {
            String login = core.resolveLoginProposal(academicTeacher.getContact());
            u = core.findUser(login);
            AppUserType teacherType = VrApp.getBean(CorePlugin.class).findUserType("Teacher");
            if (u == null) {
                u = new AppUser();
                u.setLogin(login);
                u.setContact(academicTeacher.getContact());
                String pwd = academicTeacher.getContact().getFirstName().toLowerCase() + "1243";
                u.setPassword(pwd);
                u.setPasswordAuto(pwd);
                u.setType(teacherType);
                u.setDepartment(academicTeacher.getDepartment());
                u.setEnabled(true);
                UPA.getPersistenceUnit().persist(u);
            } else {
                u.setContact(academicTeacher.getContact());
                u.setType(teacherType);
                UPA.getPersistenceUnit().merge(u);
            }
            academicTeacher.setUser(u);
            UPA.getPersistenceUnit().merge(academicTeacher);
        }
        core.userAddProfile(u.getId(), "Teacher");
    }

    public boolean addUserForStudent(AcademicStudent academicStudent) {
        AppUser u = core.findUserByContact(academicStudent.getContact().getId());
        if (u == null) {
            String login = core.resolveLoginProposal(academicStudent.getContact());
            if (StringUtils.isEmpty(login)) {
                return false;
            }
            u = core.findUser(login);
            if (u != null) {
                //check if already bound to another contact!
                AppContact otherContact = u.getContact();
                AppContact myContact = academicStudent.getContact();
                if (otherContact != null && myContact != null && otherContact.getId() != myContact.getId()) {
                    //two students with the same name?
                    int index = 2;
                    while (true) {
                        AppUser u2 = core.findUser(login + index);
                        if (u2 != null) {
                            u = u2;
                            login = login + index;
                            break;
                        }
                        index++;
                    }
                }
            }
            AppUserType studentType = VrApp.getBean(CorePlugin.class).findUserType("Student");
            if (u == null) {
                u = new AppUser();

                u.setLogin(login);
                u.setContact(academicStudent.getContact());
                String pwd = academicStudent.getContact().getFirstName().toLowerCase() + "7788";
                u.setPassword(pwd);
                u.setPasswordAuto(pwd);
                u.setType(studentType);
                u.setDepartment(academicStudent.getDepartment());
                u.setEnabled(true);
                UPA.getPersistenceUnit().persist(u);
            } else {
                u.setContact(academicStudent.getContact());
                u.setType(studentType);
                UPA.getPersistenceUnit().merge(u);
            }
            academicStudent.setUser(u);
            UPA.getPersistenceUnit().merge(academicStudent);
        }

        core.userAddProfile(u.getId(), "Student");

        for (AcademicClass c : new AcademicClass[]{academicStudent.getLastClass1(), academicStudent.getLastClass2(), academicStudent.getLastClass3()}) {
            if (c != null) {
                String s = c.getName();
                s = s.replace("(", "_").replace(")", "_").replace("+", "_").replace("+", "_");
                AppProfile p = core.findOrCreateProfile(s);
                core.userAddProfile(u.getId(), p.getName());
            }

            AcademicProgram pr = academicStudent.getLastClass1() == null ? null : academicStudent.getLastClass1().getProgram();
            if (pr != null) {
                String s = pr.getName();
                s = s.replace("(", "_").replace(")", "_").replace("+", "_").replace("+", "_");
                AppProfile p = core.findOrCreateProfile(s);
                core.userAddProfile(u.getId(), p.getName());
            }
        }
        AppDepartment d = academicStudent.getDepartment();
        if (d != null) {
            String s = d.getName();
            s = s.replace("(", "_").replace(")", "_").replace("+", "_").replace("+", "_");
            AppProfile p = core.findOrCreateProfile(s);
            core.userAddProfile(u.getId(), p.getName());
        }

        return true;
    }

    public AppPeriod findAcademicYear(String name, String snapshot) {
        return (AppPeriod) UPA.getPersistenceUnit()
                .createQuery("Select a from AppPeriod a where a.name=:t and a.snapshotName=:s")
                .setParameter("t", name)
                .setParameter("s", snapshot)
                .getEntity();
    }

    public AppPeriod findAcademicYear(int id) {
        return (AppPeriod) UPA.getPersistenceUnit()
                .findById(AppPeriod.class, id);
    }

    public AppPeriod findAcademicYearSnapshot(String t, String snapshotName) {
        return (AppPeriod) UPA.getPersistenceUnit()
                .createQuery("Select a from AppPeriod a where a.name=:t and a.snapshotName=:s")
                .setParameter("t", t)
                .setParameter("s", snapshotName)
                .getEntity();
    }

    public List<AppPeriod> findAcademicYearSnapshots(String t) {
        return UPA.getPersistenceUnit()
                .createQuery("Select a from AppPeriod a where a.name=:t and a.snapshotName!=null")
                .setParameter("t", t)
                .getEntityList();
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

    public static void main(String[] args) {
//        AcademicPlugin a = new AcademicPlugin();
//        for (PlanningDay loadTeacherPlanning : a.loadTeacherPlanning("Taha Ben Salah")) {
//            System.out.println(loadTeacherPlanning);
//        }
    }

    public List<String> loadStudentPlanningListNames() {
        TreeSet<String> all = new TreeSet<>();

        VFile[] emploisFiles = fileSystemPlugin.getProfileFileSystem("Teacher").get("/EmploiDuTemps").listFiles(new VFileFilter() {

            @Override
            public boolean accept(VFile pathname) {
                return pathname.getName().toLowerCase().endsWith("_subgroups.xml");
            }
        });
        VFile p = emploisFiles.length > 0 ? emploisFiles[0] : null;
        if (p != null && p.exists()) {
            try {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(p.getInputStream());
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("Subgroup");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        String tn = eElement.getAttribute("name");
                        all.add(tn);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(all);
    }

    private PlanningData parsePlanningDataXML(Node planningNode) {
//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
        if (planningNode.getNodeType() == Node.ELEMENT_NODE) {

            Element eElement = (Element) planningNode;
            String tn = eElement.getAttribute("name");
            PlanningData p2 = new PlanningData();
            p2.setPlanningName(tn.trim());
            p2.setDays(new ArrayList<PlanningDay>());
            NodeList days = eElement.getElementsByTagName("Day");
            for (int di = 0; di < days.getLength(); di++) {
                Node dayNode = days.item(di);
                PlanningDay dd = parsePlanningDayXML(dayNode);
                if (dd != null) {
                    p2.getDays().add(dd);
                }
            }
            while (p2.getDays().size() < 6) {
                PlanningDay d = new PlanningDay();
                d.setDayName("Day " + p2.getDays().size());
                p2.getDays().add(d);
            }
            return p2;
        }
        return null;
    }

    private PlanningDay parsePlanningDayXML(Node dayNode) {
        if (dayNode.getNodeType() == Node.ELEMENT_NODE) {
            Element dayElement = (Element) dayNode;
            PlanningDay planningDay = new PlanningDay();
            planningDay.setDayName(dayElement.getAttribute("name"));
            List<PlanningHour> planningHours = new ArrayList<>();
            NodeList hours = dayElement.getElementsByTagName("Hour");
            for (int hi = 0; hi < hours.getLength(); hi++) {
                Node hourNode = hours.item(hi);
                PlanningHour ph = parsePlanningHourXML(hourNode);
                if (ph != null) {
                    planningHours.add(ph);
                }

            }
            planningDay.setHours(planningHours);

            if (planningDay.getHours() == null || planningDay.getHours().size() < 5) {
                List<PlanningHour> all = new ArrayList<>();
                if (planningDay.getHours() != null) {
                    all.addAll((planningDay.getHours()));
                }
                while (all.size() < 5) {
                    PlanningHour dd = new PlanningHour();
                    dd.setHour("H #" + all.size());
                    all.add(dd);
                }
                planningDay.setHours(all);
            }

            return (planningDay);
        }
        return null;
    }

    private PlanningHour parsePlanningHourXML(Node hourNode) {
        if (hourNode.getNodeType() == Node.ELEMENT_NODE) {
            Element hourElement = (Element) hourNode;
            PlanningHour ph = new PlanningHour();
            ph.setHour(hourElement.getAttribute("name"));
            NodeList childNodes = hourElement.getChildNodes();
            for (int ci = 0; ci < childNodes.getLength(); ci++) {
                Node cNode = childNodes.item(ci);
                if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cElement = (Element) cNode;
                    if ("Activity_Tag".equals(cElement.getNodeName())) {
                        ph.setActivity(cElement.getAttribute("name"));
                    } else if ("Students".equals(cElement.getNodeName())) {
                        ph.setStudents(cElement.getAttribute("name"));
                    } else if ("Teacher".equals(cElement.getNodeName())) {
                        ph.setTeacher(cElement.getAttribute("name"));
                    } else if ("Subject".equals(cElement.getNodeName())) {
                        ph.setSubject(cElement.getAttribute("name"));
                    } else if ("Room".equals(cElement.getNodeName())) {
                        ph.setRoom(cElement.getAttribute("name"));
                    }
                }
            }
            String actor = "";
            if (!StringUtils.isEmpty(ph.getStudents())) {
                if (actor.length() > 0) {
                    actor += " / ";
                }
                actor += ph.getStudents();
            }
            if (!StringUtils.isEmpty(ph.getTeacher())) {
                if (actor.length() > 0) {
                    actor += " / ";
                }
                actor += ph.getTeacher();
            }
            ph.setActor(actor);
            return (ph);
        }
        return null;
    }

    public List<PlanningData> loadStudentPlanningList(int studentId) {
        AcademicStudent student = findStudent(studentId);
        List<PlanningData> list = new ArrayList<>();
        HashMap<String, String> nameMapping = new HashMap<>();
        if (student.getLastClass1() != null) {
            String n2 = student.getLastClass1().getName().trim().toLowerCase();
            nameMapping.put(n2, n2);
            for (String s : splitOtherNames(student.getLastClass1().getOtherNames())) {
                nameMapping.put(s, n2);
            }
        }
        if (student.getLastClass2() != null) {
            String n2 = student.getLastClass2().getName().trim().toLowerCase();
            nameMapping.put(n2, n2);
            for (String s : splitOtherNames(student.getLastClass2().getOtherNames())) {
                nameMapping.put(s, n2);
            }
        }
        if (student.getLastClass3() != null) {
            String n2 = student.getLastClass3().getName().trim().toLowerCase();
            nameMapping.put(n2, n2);
            for (String s : splitOtherNames(student.getLastClass3().getOtherNames())) {
                nameMapping.put(s, n2);
            }
        }
        VFile[] emploisFiles = fileSystemPlugin.getProfileFileSystem("Teacher").get("/EmploiDuTemps").listFiles(new VFileFilter() {

            @Override
            public boolean accept(VFile pathname) {
                return pathname.getName().toLowerCase().endsWith("_subgroups.xml");
            }
        });
        VFile p = emploisFiles.length > 0 ? emploisFiles[0] : null;
        if (p != null && p.exists()) {
            try {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(p.getInputStream());
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("Subgroup");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        String tn = eElement.getAttribute("name");
                        if (nameMapping.containsKey(tn.trim().toLowerCase())) {
                            PlanningData p2 = parsePlanningDataXML(nNode);
                            if (p2 != null) {
                                p2.setPlanningUniformName(nameMapping.get(tn.trim().toLowerCase()));
                                list.add(p2);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public PlanningData loadClassPlanning(String className) {
        String uniformClassName = className == null ? "" : className.toLowerCase().trim();
        if (StringUtils.isEmpty(uniformClassName)) {
            return null;
        }
        VFile[] emploisFiles = fileSystemPlugin.getProfileFileSystem("Teacher").get("/EmploiDuTemps").listFiles(new VFileFilter() {

            @Override
            public boolean accept(VFile pathname) {
                return pathname.getName().toLowerCase().endsWith("_subgroups.xml");
            }
        });
        VFile p = emploisFiles.length > 0 ? emploisFiles[0] : null;
        if (p != null && p.exists()) {
            try {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(p.getInputStream());
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("Subgroup");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        String tn = eElement.getAttribute("name");
                        if (uniformClassName.equals(tn.trim().toLowerCase())) {
                            PlanningData p2 = parsePlanningDataXML(nNode);
                            return p2;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Set<String> splitOtherNames(String value) {
        Set<String> all = new HashSet<>();
        if (value != null) {
            for (String s : value.split(",|;")) {
                if (s.trim().length() > 0) {
                    all.add(s.trim().toLowerCase());
                }
            }
        }
        return all;
    }

    public List<PlanningData> loadUserPlannings(int userId) {
        AppUser uuu = core.findUser(userId);
        List<PlanningData> list = new ArrayList<>();
        if (uuu == null) {
            return list;
        }
//        String teacherName = uuu == null ? "" : uuu.getContact().getFullName();
        VFile p = fileSystemPlugin.getUserFolder(uuu.getLogin()).get("/myplanning.xml");
        if (p != null && p.exists()) {
            try {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(p.getInputStream());
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("User");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        PlanningData d = parsePlanningDataXML(nNode);
                        if (d != null) {
                            list.add(d);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public PlanningData loadTeacherPlanning(int teacherId) {
        AcademicTeacher teacher = findTeacher(teacherId);

        String teacherName = teacher == null ? "" : teacher.getContact().getFullName();
        HashSet<String> teacherNames = new HashSet<>();
        teacherNames.add(teacherName.toLowerCase());
        if (teacher != null) {
            for (String s : splitOtherNames(teacher.getOtherNames())) {
                teacherNames.add(s);
            }
        }
        VFile[] emploisFiles = fileSystemPlugin.getProfileFileSystem("Teacher").get("/EmploiDuTemps").listFiles(new VFileFilter() {

            @Override
            public boolean accept(VFile pathname) {
                return pathname.getName().toLowerCase().endsWith("_teachers.xml");
            }
        });
        VFile p = emploisFiles.length > 0 ? emploisFiles[0] : null;
        if (p != null && p.exists()) {
            try {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(p.getInputStream());
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("Teacher");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        String tn = eElement.getAttribute("name");
                        if (teacherNames.contains(tn.trim().toLowerCase().trim())) {
                            PlanningData dd = parsePlanningDataXML(nNode);
                            if (dd != null) {
                                dd.setPlanningUniformName(teacherName);
                                return dd;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getExtendedPropertyValues(Object o) {
        if (o instanceof AppUser) {
            AcademicTeacher t = findTeacherByUser(((AppUser) o).getId());
            if (t != null) {
                HashMap<String, Object> m = new HashMap<>();
                m.put("discipline", t.getDiscipline());
                m.put("degree", t.getDegree() == null ? null : t.getDegree().getName());
                m.put("degreeCode", t.getDegree() == null ? null : t.getDegree().getCode());
                m.put("situation", t.getSituation() == null ? null : t.getSituation().getName());
                m.put("enabled", t.isEnabled());
                return m;
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
}
