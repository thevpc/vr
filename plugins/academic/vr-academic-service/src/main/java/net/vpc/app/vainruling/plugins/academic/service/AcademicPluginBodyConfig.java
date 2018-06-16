package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.cache.EntityCache;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.util.DateFormatUtils;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.academic.service.model.config.*;
import net.vpc.app.vainruling.plugins.academic.service.model.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternshipGroup;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Action;
import net.vpc.upa.Document;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.*;
import net.vpc.common.util.MutableDate;

public class AcademicPluginBodyConfig extends AcademicPluginBody {
    public static final int DEFAULT_SEMESTER_MAX_WEEKS = 14;
    CorePlugin core;
    CacheService cacheService;
    AcademicPlugin academic;

    public int getSemesterMaxWeeks() {
        return CacheService.get().get(AcademicTeacher.class).getProperty("getSemesterMaxWeeks", new Action<Integer>() {
            @Override
            public Integer run() {
                try {
                    return (Integer) CorePlugin.get().getOrCreateAppPropertyValue("AcademicPlugin.SemesterMaxWeeks", null, DEFAULT_SEMESTER_MAX_WEEKS);
                } catch (Exception e) {
                    return DEFAULT_SEMESTER_MAX_WEEKS;
                }
            }
        });
    }

    @Override
    public void onStart() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        core = CorePlugin.get();
        cacheService = CacheService.get();
        academic = getContext().getPlugin();
        AppProfile teacherProfile = core.findOrCreateCustomProfile("Teacher", "UserType");
        AppProfile studentProfile = core.findOrCreateCustomProfile("Student", "UserType");
        AppProfile director = core.findOrCreateCustomProfile("Director", "UserType");
        AppProfile headOfDepartment = core.findOrCreateCustomProfile(CorePlugin.PROFILE_HEAD_OF_DEPARTMENT, "UserType");
        AppProfile directorOfStudies = core.findOrCreateCustomProfile("DirectorOfStudies","UserType");


        core.addProfileRight(director.getId(), CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);

        for (net.vpc.upa.Entity ee : pu.getPackage("Education").getEntities(true)) {
            for (String right : CorePluginSecurity.getEntityRights(ee, true, true, true, false, false)) {
                core.addProfileRight(headOfDepartment.getId(), right);
            }
            for (String right : CorePluginSecurity.getEntityRights(ee, true, false, false, false, false)) {
                core.addProfileRight(director.getId(), right);
                core.addProfileRight(directorOfStudies.getId(), right);
            }
        }


        core.addProfileRight(headOfDepartment.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_COURSE_LOAD);
        core.addProfileRight(headOfDepartment.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_GLOBAL_STAT);
        core.addProfileRight(headOfDepartment.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ALL_TEACHERS_COURSE_LOAD);
        core.addProfileRight(headOfDepartment.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_INTENTS);
        core.addProfileRight(headOfDepartment.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_COURSE_LOAD_UPDATE_ASSIGNMENTS);
        core.addProfileRight(headOfDepartment.getId(), CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);


        core.addProfileRight(directorOfStudies.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_COURSE_LOAD);
        core.addProfileRight(directorOfStudies.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_GLOBAL_STAT);
        core.addProfileRight(directorOfStudies.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ALL_TEACHERS_COURSE_LOAD);
        core.addProfileRight(directorOfStudies.getId(), CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);


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
            }
        }
        core.getManagerProfiles().add("Director");
        core.getManagerProfiles().add("DirectorOfStudies");
        core.addProfileRight("Teacher", AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
    }

    public List<AcademicProgramType> findProgramTypes() {
        return UPA.getPersistenceUnit().findAll(AcademicProgramType.class);
    }

    public Set<String> findCoursePlanLabels(int periodId) {
        HashSet<String> labels = new HashSet<>();
        for (AcademicCoursePlan plan : getContext().getPlugin().findCoursePlans(periodId)) {
            labels.addAll(VrUtils.splitLabels(plan.getLabels()));
        }
        return labels;
    }


    public AcademicTeacher findCurrentHeadOfDepartment() {
        AppUser user = core.getCurrentUser();
        if (user == null || user.getDepartment() == null) {
            return null;
        }
        return findHeadOfDepartment(user.getDepartment().getId());
    }

    public AcademicTeacher findHeadOfDepartment(int depId) {
        AppUser u = core.findHeadOfDepartment(depId);
        if (u != null) {
            return getContext().getPlugin().findTeacherByUser(u.getId());
        }
        return null;
    }

    public List<AcademicProgram> findPrograms() {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
        return UPA.getPersistenceUnit()
                .createQuery(
                        "Select a from AcademicProgram a order by a.name")
                .getResultList();
    }

    public List<AcademicTeacherDegree> findTeacherDegrees() {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
        return UPA.getPersistenceUnit()
                .createQuery(
                        "Select a from AcademicTeacherDegree a")
                .getResultList();
    }

    public List<AcademicTeacherSituation> findTeacherSituations() {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
        return UPA.getPersistenceUnit().findAll(AcademicTeacherSituation.class);
    }

    public List<AcademicPreClass> findAcademicPreClasses() {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
        return UPA.getPersistenceUnit().findAll(AcademicPreClass.class);
    }

    public List<AcademicBac> findAcademicBacs() {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
        return UPA.getPersistenceUnit().findAll(AcademicBac.class);
    }

    public Map<Integer, AcademicClass> findAcademicClassesMap() {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
        HashMap<Integer, AcademicClass> _allClasses = new HashMap<>();
        for (AcademicClass a : findAcademicClasses()) {
            _allClasses.put(a.getId(), a);
        }
        return _allClasses;
    }

    public Map<Integer, AcademicPreClass> findAcademicPreClassesMap() {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
        HashMap<Integer, AcademicPreClass> _allClasses = new HashMap<>();
        for (AcademicPreClass a : findAcademicPreClasses()) {
            _allClasses.put(a.getId(), a);
        }
        return _allClasses;
    }

    public Map<Integer, AcademicBac> findAcademicBacsMap() {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
        HashMap<Integer, AcademicBac> _allClasses = new HashMap<>();
        for (AcademicBac a : findAcademicBacs()) {
            _allClasses.put(a.getId(), a);
        }
        return _allClasses;
    }

    public List<AcademicClass> findAcademicClasses() {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
        return UPA.getPersistenceUnit().findAll(AcademicClass.class);
    }

    public List<AcademicCourseLevel> findCourseLevels() {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
        return UPA.getPersistenceUnit().findAll(AcademicCourseLevel.class);
    }

    public AcademicCourseLevel findCourseLevel(int academicClassId, int semesterId) {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
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
            String[] cn = VrUtils.codeAndName(n);
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
            String[] cn = VrUtils.codeAndName(n);
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
            String[] cn = VrUtils.codeAndName(n);
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


    public AppPeriod findAcademicYear(String name, String snapshot) {
//        VrUtils.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_READ);
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
            CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_WRITE);
            z = new AppPeriod();
            z.setName(academicYearName);
            z.setSnapshotName(snapshot);
            CorePlugin.get().save(null, z);
        }
        return z;
    }

    public AppPeriod createAcademicYear(String academicYearName, String snapshot) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_WRITE);
        AppPeriod z = findAcademicYear(academicYearName, snapshot);
        if (z != null) {
            throw new IllegalArgumentException("Already exists");
        }
        z = new AppPeriod();
        z.setName(academicYearName);
        z.setSnapshotName(snapshot);
        CorePlugin.get().save(null, z);

        return z;
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


    public void validateAcademicData(int periodId) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_WRITE);
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
        for (AcademicStudent s : academic.findStudents()) {
            academic.validateAcademicData_Student(s.getId(), periodId);
        }
        for (AcademicTeacher s : academic.findTeachers()) {
            academic.validateAcademicData_Teacher(s.getId(), periodId);
        }
        academic.generateTeacherAssignmentDocumentsFolder(periodId);
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

    public AcademicCoursePlan findCoursePlan(int id) {
        return (AcademicCoursePlan) UPA.getPersistenceUnit()
                .createQueryBuilder(AcademicCoursePlan.class)
                .byField("id", id)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
                .getFirstResultOrNull();
    }

    public AcademicSemester getCurrentSemester() {
        List<AcademicSemester> semesters = findSemesters();
        MutableDate date = new MutableDate().clearTime().setYear(2000);
        for (AcademicSemester semester : semesters) {
            MutableDate fromDate = DateFormatUtils.parseMutableDate(semester.getFromDate(), "dd/MM", null);
            MutableDate toDate = DateFormatUtils.parseMutableDate(semester.getToDate(), "dd/MM", null);
            if (fromDate != null && toDate != null) {
                fromDate.setYear(2000);
                toDate.setYear(2000);
                if (fromDate.before(toDate)) {
                    if(date.compareTo(fromDate)>=0 && date.compareTo(toDate)<=0){
                        return semester;
                    }
                } else {
                    if(date.compareTo(fromDate)<=0 && date.compareTo(toDate)>=0){
                        return semester;
                    }
                }
            }
        }
        return null;
    }
}
