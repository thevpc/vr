package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.util.VrPasswordStrategyRandom;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacherPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgram;
import net.vpc.app.vainruling.plugins.academic.service.util.AcademicTeacherProfileFilter;
import net.vpc.app.vainruling.plugins.academic.service.util.DefaultCourseAssignmentFilter;
import net.vpc.app.vainruling.plugins.academic.service.util.TeacherPeriodFilter;
import net.vpc.common.strings.StringCollection;
import net.vpc.common.strings.StringComparator;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Action;
import net.vpc.upa.NamedId;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AcademicPluginBodyTeachers extends AcademicPluginBody {

    private static final Logger log = Logger.getLogger(AcademicPluginBodyTeachers.class.getName());

    private CorePlugin core;
    private AcademicPlugin academic;
    private CacheService cacheService;

    @Override
    public void onStart() {
        cacheService = CacheService.get();
        core = CorePlugin.get();
        academic = getContext().getPlugin();

        AppUserType teacherType;
        teacherType = new AppUserType();
        teacherType.setCode("Teacher");
        teacherType.setName("Teacher");
        core.findOrCreate(teacherType);

        core.addProfileRight("Teacher", AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER);

        AppProfile teacherProfile = core.findOrCreateCustomProfile("Teacher", "UserType");
        StringCollection stringCollection = VrUtils.createStringMap(teacherProfile.getInherited());
        if (!stringCollection.contains("LocalMailUser")) {
            stringCollection.add("LocalMailUser");
            teacherProfile.setInherited(stringCollection.toString());
        }

        core.addProfileRight(teacherProfile.getId(), AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_COURSE_LOAD);

        core.addProfileRight(teacherProfile.getId(), CorePluginSecurity.getEntityRightPersist("AcademicCourseIntent"));
        core.addProfileRight(teacherProfile.getId(), CorePluginSecurity.getEntityRightRemove("AcademicCourseIntent"));
        core.addProfileRight(teacherProfile.getId(), CorePluginSecurity.getEntityRightLoad("AppContact"));
        core.addProfileRight(teacherProfile.getId(), CorePluginSecurity.getEntityRightNavigate("AcademicCoursePlan"));

        core.addProfileRight(teacherProfile.getId(), CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);
        for (String navigateOnlyEntity : new String[]{"AppContact"}) {
            core.addProfileRight(teacherProfile.getId(), CorePluginSecurity.getEntityRightNavigate(navigateOnlyEntity));
        }
        for (String readOnlyEntity : new String[]{"AcademicTeacher", "AcademicClass", "AcademicCoursePlan", "AcademicCourseLevel", "AcademicCourseGroup", "AcademicCourseType", "AcademicProgram", "AcademicDiscipline", "AcademicStudent"
    //,"AcademicCourseAssignment"
        }) {
            for (String right : new String[]{
                CorePluginSecurity.getEntityRightEditor(readOnlyEntity),
                CorePluginSecurity.getEntityRightLoad(readOnlyEntity),
                CorePluginSecurity.getEntityRightNavigate(readOnlyEntity),}) {
                core.addProfileRight(teacherProfile.getId(), right);
            }
        }

        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppConfig appConfig = core.getCurrentConfig();
        if (appConfig != null) {
            AppPeriod mainPeriod = appConfig.getMainPeriod();
            if (mainPeriod != null) {
                for (AcademicTeacher academicTeacher : getContext().getPlugin().findTeachers()) {
                    getContext().getPlugin().updateTeacherPeriod(mainPeriod.getId(), academicTeacher.getId(), -1);
                }
            }
        }
    }

    public AcademicTeacher findTeacherByUser(int userId) {
        CorePluginSecurity.requireUser(userId);
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

//    public AcademicTeacher findTeacherByContact(int contactId) {
//        CorePluginSecurity.requireContact(contactId);
//        return UPA.getPersistenceUnit().createQuery("Select u from AcademicTeacher u where u.user.contactId=:contacId")
//                .setParameter("contacId", contactId)
//                .getFirstResultOrNull();
//    }

    public AcademicTeacher findTeacher(StringComparator t) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER);
        for (AcademicTeacher teacher : findTeachers()) {
            if (t.matches(teacher.getUser()== null ? null : teacher.resolveFullName())) {
                return teacher;
            }
        }
        return null;
    }

//    public void update(Object t) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        pu.merge(t);
//    }
    public List<AcademicTeacher> findTeachers(int period, TeacherPeriodFilter teacherFilter) {
        List<AcademicTeacher> teachers = findTeachers();
        if (teacherFilter == null) {
            return teachers;
        }
        List<AcademicTeacher> teachers2 = new ArrayList<>(teachers.size());
        for (AcademicTeacher teacher : teachers) {
            if (teacherFilter.acceptTeacher(academic.findAcademicTeacherPeriod(period, teacher))) {
                teachers2.add(teacher);
            }
        }
        return teachers2;
    }

    public List<AcademicTeacher> findTeachers() {
        //method called before starting the 'body'
        if (cacheService == null) {
            cacheService = CacheService.get();
        }
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER);
        return cacheService.getList(AcademicTeacher.class);

        //return UPA.getPersistenceUnit().findAll(AcademicTeacher.class);
    }

    public List<NamedId> findEnabledTeacherNames(int periodId) {
        return UPA.getPersistenceUnit().invokePrivileged(
                new Action<List<NamedId>>() {
            @Override
            public List<NamedId> run() {
                return findEnabledTeachers(periodId).stream()
                        .map(x -> new NamedId(x.getId(), x.resolveFullName()))
                        .collect(Collectors.toList());
            }
        }
        );
    }

    public List<AcademicTeacher> findEnabledTeachers(int periodId) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER);
        List<AcademicTeacher> periodId1 = UPA.getPersistenceUnit().createQuery("Select distinct u.teacher from AcademicTeacherPeriod u where u.teacher.deleted=false and u.enabled=true order by u.teacher.user.fullName and u.periodId=:periodId")
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

    public AcademicTeacher findTeacher(int t) {
        return cacheService.getList(AcademicTeacher.class).getByKey(t);
//        return (AcademicTeacher) UPA.getPersistenceUnit()
//                .createQuery("Select u from AcademicTeacher u where u.id=:id")
//                .setParameter("id", t)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 5)
//                .getEntity();
//                .findById(AcademicTeacher.class, t);
    }

    public List<AcademicTeacher> findTeachers(String teacherProfileFilter) {
        return filterTeachers(findTeachers(), teacherProfileFilter);
    }

    public List<AcademicTeacher> filterTeachers(List<AcademicTeacher> objects, String studentProfileFilter) {
        AcademicTeacherProfileFilter filter = new AcademicTeacherProfileFilter(studentProfileFilter);
        return objects.stream().filter(x -> filter.accept(x)).collect(Collectors.toList());
    }

    public List<AcademicTeacher> findTeachersWithAssignmentsOrIntents(int periodId, int semesterId, boolean includeAssignments, boolean includeIntents, int teacherDepId, int assignmentDepId) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ASSIGNMENTS);
        HashMap<Integer, AcademicTeacher> visited = new HashMap<>();
        if (includeAssignments && includeIntents) {
            List<AcademicTeacher> byAssignment = UPA.getPersistenceUnit()
                    .createQuery("Select u from AcademicTeacher u where u.id in ("
                            + " Select t.id from AcademicTeacher t "
                            + " left join AcademicCourseAssignment a on a.teacherId=t.id "
                            + " left join AcademicCourseIntent     b on b.teacherId=t.id "
                            + " where ((a.id != null) or (b.id != null)) "
                            + VrUtils.conditionalString(" and t.departmentId=:teacherDepId ", teacherDepId >= 0)
                            + VrUtils.conditionalString(" and a.coursePlan.period.id=:periodId ", periodId >= 0)
                            + VrUtils.conditionalString(" and a.coursePlan.courseLevel.semesterId=:semesterId ", semesterId >= 0)
                            + VrUtils.conditionalString(" and a.coursePlan.courseLevel.academicClass.program.departmentId=:assignmentDepId ", assignmentDepId >= 0)
                            + VrUtils.conditionalString(" and b.assignment.coursePlan.period.id=:periodId ", periodId >= 0)
                            + VrUtils.conditionalString(" and b.assignment.coursePlan.courseLevel.semesterId=:semesterId ", semesterId >= 0)
                            + VrUtils.conditionalString(" and b.assignment.coursePlan.courseLevel.academicClass.program.departmentId=:assignmentDepId ", assignmentDepId >= 0)
                            + ") order by u.user.fullName")
                    .setParameter("periodId", periodId, periodId >= 0)
                    .setParameter("semesterId", semesterId, semesterId >= 0)
                    .setParameter("teacherDepId", teacherDepId, teacherDepId >= 0)
                    .setParameter("assignmentDepId", assignmentDepId, assignmentDepId >= 0)
                    .getResultList();
            for (AcademicTeacher academicTeacher : byAssignment) {
                if (!visited.containsKey(academicTeacher.getId())) {
                    visited.put(academicTeacher.getId(), academicTeacher);
                }
            }
        } else {
            if (includeAssignments) {
                List<AcademicTeacher> byAssignment = UPA.getPersistenceUnit()
                        .createQuery("Select u from AcademicTeacher u where u.id in ("
                                + " Select t.id from AcademicTeacher t "
                                + " inner join AcademicCourseAssignment a on a.teacherId=t.id "
                                + " where 1=1 "
                                + VrUtils.conditionalString(" and t.departmentId=:teacherDepId ", teacherDepId >= 0)
                                + VrUtils.conditionalString(" and a.coursePlan.period.id=:periodId ", periodId >= 0)
                                + VrUtils.conditionalString(" and a.coursePlan.courseLevel.semesterId=:semesterId ", semesterId >= 0)
                                + VrUtils.conditionalString(" and a.coursePlan.courseLevel.academicClass.program.departmentId=:assignmentDepId ", assignmentDepId >= 0)
                                + ") order by u.user.fullName")
                        .setParameter("periodId", periodId, periodId >= 0)
                        .setParameter("semesterId", semesterId, semesterId >= 0)
                        .setParameter("teacherDepId", teacherDepId, teacherDepId >= 0)
                        .setParameter("assignmentDepId", assignmentDepId, assignmentDepId >= 0)
                        .getResultList();
                for (AcademicTeacher academicTeacher : byAssignment) {
                    if (!visited.containsKey(academicTeacher.getId())) {
                        visited.put(academicTeacher.getId(), academicTeacher);
                    }
                }
            }
            if (includeIntents) {
                List<AcademicTeacher> byIntent = UPA.getPersistenceUnit()
                        .createQuery("Select u from AcademicTeacher u where u.id in ("
                                + " Select t.id from AcademicTeacher t "
                                + " inner join AcademicCourseIntent b on b.teacherId=t.id "
                                + " where 1=1 "
                                + VrUtils.conditionalString(" and t.departmentId=:teacherDepId ", teacherDepId >= 0)
                                + VrUtils.conditionalString(" and b.assignment.coursePlan.period.id=:periodId ", periodId >= 0)
                                + VrUtils.conditionalString(" and b.assignment.coursePlan.courseLevel.semesterId=:semesterId ", semesterId >= 0)
                                + VrUtils.conditionalString(" and b.assignment.coursePlan.courseLevel.academicClass.program.departmentId=:assignmentDepId ", assignmentDepId >= 0)
                                + ") order by u.user.fullName")
                        .setParameter("periodId", periodId, periodId >= 0)
                        .setParameter("semesterId", semesterId, semesterId >= 0)
                        .setParameter("teacherDepId", teacherDepId, teacherDepId >= 0)
                        .setParameter("assignmentDepId", assignmentDepId, assignmentDepId >= 0)
                        .getResultList();
                for (AcademicTeacher academicTeacher : byIntent) {
                    if (!visited.containsKey(academicTeacher.getId())) {
                        visited.put(academicTeacher.getId(), academicTeacher);
                    }
                }
            }
        }
        List<AcademicTeacher> ret = new ArrayList<>(visited.values());
        ret.sort(Comparator.comparing(AcademicTeacher::resolveFullName));
        return ret;
    }

//    public List<AcademicTeacher> findTeachersWithAssignmentsOrIntent(int periodId) {
//        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ASSIGNMENTS);
//        List<AcademicTeacher> byAssignment = findTeachersWithAssignments(periodId);
//        List<AcademicTeacher> byIntent = findTeachersWithIntents(periodId);
//        HashMap<Integer, AcademicTeacher> visited = new HashMap<>();
//        for (AcademicTeacher academicTeacher : byAssignment) {
//            if (!visited.containsKey(academicTeacher.getId())) {
//                visited.put(academicTeacher.getId(), academicTeacher);
//            }
//        }
//        for (AcademicTeacher academicTeacher : byIntent) {
//            if (!visited.containsKey(academicTeacher.getId())) {
//                visited.put(academicTeacher.getId(), academicTeacher);
//            }
//        }
//        List<AcademicTeacher> ret = new ArrayList<>(visited.values());
//        ret.sort(Comparator.comparing(AcademicTeacher::resolveFullName));
//        return ret;
//    }
//
//    public List<AcademicTeacher> findTeachersWithAssignments(int periodId) {
//        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ASSIGNMENTS);
//        return UPA.getPersistenceUnit()
//                .createQuery("Select u from AcademicTeacher u where u.id in ("
//                        + " Select t.id from AcademicTeacher t "
//                        + " inner join AcademicCourseAssignment a on a.teacherId=t.id "
//                        + " where a.coursePlan.period.id=:periodId "
//                        + ") order by u.fullName")
//                .setParameter("periodId", periodId)
//                .getResultList();
//    }
//
//    public List<AcademicTeacher> findTeachersWithIntents(int periodId) {
//        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER);
//        return UPA.getPersistenceUnit()
//                .createQuery("Select u from AcademicTeacher u where u.id in ("
//                        + " Select t.id from AcademicTeacher t "
//                        + " inner join AcademicCourseIntent a on a.teacherId=t.id "
//                        + " where a.assignment.coursePlan.period.id=:periodId "
//                        + ") order by u.fullName")
//                .setParameter("periodId", periodId)
//                .getResultList();
//    }
//
    public AcademicTeacher findTeacher(String t) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        return (AcademicTeacher) UPA.getPersistenceUnit().createQuery("Select u from AcademicTeacher u where u.user.fullName=:name").setParameter("name", t).getFirstResultOrNull();
    }

//    public List<AcademicTeacher> findTeachersWithAssignmentsOrIntents() {
//        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER);
//        return UPA.getPersistenceUnit()
//                .createQuery("Select u from AcademicTeacher u where u.id in ("
//                        + " Select t.id from AcademicTeacher t "
//                        + " left join AcademicCourseAssignment a on a.teacheId=t.id"
//                        + " left join AcademicCourseIntent i on i.teacherId=t.id"
//                        + " where (a is not null) or (i is not null)"
//                        + ") order by u.fullName")
//                .getResultList();
//    }
//
    public AcademicTeacherPeriod findAcademicTeacherPeriod(final int periodId, AcademicTeacher t) {
        Map<Integer, AcademicTeacherPeriod> m = cacheService.get(AcademicTeacherPeriod.class).getProperty("findAcademicTeacherPeriodByTeacher:" + periodId, new Action<Map<Integer, AcademicTeacherPeriod>>() {
            @Override
            public Map<Integer, AcademicTeacherPeriod> run() {

                List<AcademicTeacherPeriod> ret
                        = UPA.getPersistenceUnit()
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
        a.setDepartment(t.getUser().getDepartment());
//        a.setEnabled(t.isEnabled());
        a.setPeriod(VrApp.getBean(CorePlugin.class).findPeriod(periodId));
        return a;
    }

    public String getValidName(AcademicTeacher t) {
        if (t == null) {
            return "";
        }
        String name = null;
        if (t.getUser()!= null) {
            name = t.resolveFullName();
        }
        if (StringUtils.isEmpty(name) && t.getUser() != null) {
            name = t.getUser().getLogin();
        }
        if (StringUtils.isEmpty(name)) {
            name = "Teacher #" + t.getId();
        }
        return (name);
    }

    public void validateAcademicData_Teacher(int teacherId, int periodId) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_WRITE);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicTeacher s = findTeacher(teacherId);
        Map<Integer, AcademicClass> academicClasses = academic.findAcademicClassesMap();
        AppUser u = s.getUser();
        AppDepartment d = s.getUser().getDepartment();

        if (d == null && u != null) {
            d = u.getDepartment();
        }
//        if (s.getDepartment() == null && d != null) {
//            s.setDepartment(d);
//            UPA.getPersistenceUnit().merge(s);
//        }
        if (u != null) {

            if (u.getDepartment() == null && d != null) {
                u.setDepartment(d);
                UPA.getPersistenceUnit().merge(u);
            }
//            if (u.getContact() == null && c != null) {
//                u.setContact(c);
//                UPA.getPersistenceUnit().merge(u);
//            }
        }

        int ss = academic.findSemesters().size();
        for (int i = 1; i < ss + 1; i++) {
            academic.addAcademicTeacherSemestrialLoad(
                    i, academic.getSemesterMaxWeeks(),
                    s.getId(),
                    periodId
            );
        }

        if (u != null) {
            HashSet<Integer> goodProfiles = new HashSet<>();
            String depCode = null;
            {
                if (d != null) {
                    depCode = d.getCode();
                    AppProfile p = core.findOrCreateCustomProfile(d.getCode(), "Department");
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
            for (AcademicCourseAssignment a : academic.findCourseAssignments(periodId, s.getId(), new DefaultCourseAssignmentFilter().setAcceptIntents(false))) {
                AcademicProgram academicProgram = a.resolveProgram();
                if (academicProgram != null) {
                    myPrograms.add(academicProgram.getName());
                }
                myClasses.add(a.resolveAcademicClass());//not to force loading of sub class!
            }

            for (AcademicClass ac : academic.findAcademicUpHierarchyList(myClasses.toArray(new AcademicClass[myClasses.size()]), academicClasses)) {
                if (ac != null) {
                    //ignore inherited profiles in suffix
//                        classNames.add(n);
                    AppProfile p = core.findOrCreateCustomProfile(ac.getName(), "AcademicClass");
                    goodProfiles.add(p.getId());

                    AcademicProgram pr = ac.getProgram();
                    if (pr != null) {
                        myPrograms.add(pr.getName());
                    }
                }
            }
            for (String myProgram : myPrograms) {
                AppProfile p = core.findOrCreateCustomProfile(myProgram, "AcademicProgram");
                goodProfiles.add(p.getId());
            }

//                                    n = a.getCoursePlan().getStudentClass().getName();
//                    p = core.findOrCreateCustomProfile(n, "AcademicClass");
//                    goodProfiles.add(p.getId());
            boolean perm = false;
            List<AppProfile> oldProfiles = u == null ? new ArrayList<AppProfile>() : core.findProfilesByUser(u.getId());
            for (AppProfile op : oldProfiles) {
                if ("Permanent".equals(op.getCode())) {
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
            u.setPositionSuffix(goodSuffix.toString());
            pu.merge(u);

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

    public void updateTeacherPeriod(int periodId, int teacherId, int copyFromPeriod) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_WRITE);
//        AppPeriod p = core.getCurrentPeriod();
        AcademicTeacher teacher = findTeacher(teacherId);
        AppPeriod period = core.findPeriod(periodId);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AcademicTeacherPeriod> items = pu.createQuery("Select u from AcademicTeacherPeriod u where u.teacherId=:teacherId and u.periodId=:periodId")
                .setParameter("periodId", periodId)
                .setParameter("teacherId", teacherId)
                .getResultList();
        boolean toPersist = items.isEmpty();
        while (items.size() > 1) {
            AcademicTeacherPeriod i = items.get(0);
            pu.remove(i);
            log.log(Level.SEVERE, "Duplicated AcademicTeacherPeriod {0}", items.size());
            items.remove(0);
        }
        if (toPersist) {
            AcademicTeacherPeriod item = new AcademicTeacherPeriod();
            item.setPeriod(period);
            item.setTeacher(teacher);
            if (copyFromPeriod <= 0 || copyFromPeriod == periodId) {
                item.setDegree(teacher.getDegree());
                item.setSituation(teacher.getSituation());
                item.setEnabled(teacher.getSituation() != null);
                item.setDepartment(teacher.getUser().getDepartment());
            } else {
                AcademicTeacherPeriod other = findAcademicTeacherPeriod(copyFromPeriod, teacher);
                item.setDegree(other.getDegree());
                item.setSituation(other.getSituation());
                item.setEnabled(other.isEnabled());
                item.setDepartment(other.getDepartment());
            }
            item.setLoadConfirmed(false);
            pu.persist(item);
        }
    }

}
