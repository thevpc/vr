package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.util.VrPasswordStrategyNin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicFormerStudent;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudentStage;
import net.vpc.app.vainruling.plugins.academic.model.config.EmploymentDelay;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicProgram;
import net.vpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternship;
import net.vpc.app.vainruling.plugins.academic.service.util.AcademicStudentProfileFilter;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

import java.util.*;
import java.util.stream.Collectors;
import net.vpc.app.vainruling.core.service.CreateUserInfo;

public class AcademicPluginBodyStudents extends AcademicPluginBody {

    private CorePlugin core;
    private CacheService cacheService;
    private TraceService trace;
    private AcademicPlugin academic;

    @Override
    public void onStart() {
        cacheService = CacheService.get();
        core = CorePlugin.get();
        academic = getContext().getPlugin();
        trace = TraceService.get();
        core.findOrCreate(new AppUserType("Student","Student"));

        AppProfile studentProfile = core.findOrCreateCustomProfile("Student", "UserType");
        core.addProfileRight(studentProfile.getId(), CorePluginSecurity.RIGHT_CUSTOM_FILE_SYSTEM_MY_FILE_SYSTEM);
        core.addProfileRight("Teacher", AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_STUDENTS);

    }

    public AcademicStudent findStudentByUser(Integer userId) {
        CorePluginSecurity.requireUser(userId);
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

//    public AcademicStudent findStudentByContact(int contactId) {
//        CorePluginSecurity.requireContact(contactId);
//        return UPA.getPersistenceUnit().createQuery("Select u from AcademicStudent u where u.user.contactId=:contactId")
//                .setParameter("contactId", contactId)
//                .getFirstResultOrNull();
//    }

    public List<AcademicStudent> findStudents(Integer department, AcademicStudentStage stage) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_STUDENTS);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select a from AcademicStudent a where a.stage=:stage "
                    +(department==null?"":"and a.user.departmentId=:department")
        )
                .setParameter("department", department,department!=null)
                .setParameter("stage", stage)
                .getResultList();
    }

    public List<AcademicStudent> findStudents() {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_STUDENTS);
        return UPA.getPersistenceUnit().createQuery("Select s from AcademicStudent s where s.deleted=false order by s.user.fullName").getResultList();
    }

    /**
     * @param studentUpqlFilter ql expression x based. example "x.fullName like
     * '%R%'"
     * @return
     */
    public List<AcademicStudent> findStudents(String studentProfileFilter, AcademicStudentStage stage, String studentUpqlFilter) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_STUDENTS);
        List<AcademicStudent> base = UPA.getPersistenceUnit().createQuery("Select x from AcademicStudent x "
                + " where "
                + " x.deleted=false "
                + (stage == null ? "" : " and x.stage=:stage ")
                + ((StringUtils.isBlank(studentUpqlFilter)) ? "" : (" and " + studentUpqlFilter))
                + " order by x.user.fullName")
                .setParameter("stage", AcademicStudentStage.ATTENDING, stage != null)
                .getResultList();
        return filterStudents(base, studentProfileFilter);
    }

    public List<AcademicStudent> filterStudents(List<AcademicStudent> objects, String studentProfileFilter) {
        AcademicStudentProfileFilter filter = new AcademicStudentProfileFilter(studentProfileFilter);
        return objects.stream().filter(x -> filter.accept(x)).collect(Collectors.toList());
    }

    public List<AcademicFormerStudent> findGraduatedStudents() {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_STUDENTS);
        return UPA.getPersistenceUnit().createQuery("Select u from AcademicFormerStudent u where u.deleted=false and u.graduated=true").getResultList();
    }

    public AcademicStudent findStudentByFullName(String name) {
        AcademicPluginSecurity.requireTeacherOrManager(-1);
        return (AcademicStudent) UPA.getPersistenceUnit().createQuery("Select u from AcademicStudent s where s.user.fullName=:name").setParameter("name", name).getFirstResultOrNull();
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

        AcademicInternship pfe = academic.findStudentPFE(studentId);
        if (pfe != null) {
            formerStudent.setGraduationProjectTitle(pfe.getName());
            formerStudent.setGraduationProjectSummary(pfe.getDescription());
            formerStudent.setGraduationProjectSupervisor(pfe.getSupervisor() == null ? null : pfe.getSupervisor().resolveFullTitle());
            {
                StringBuilder sb = new StringBuilder();
                if (pfe.getSupervisor() != null) {
                    sb.append(pfe.getSupervisor().resolveFullName());
                }
                if (pfe.getSecondSupervisor() != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(pfe.getSecondSupervisor().resolveFullName());
                }
                formerStudent.setGraduationProjectSupervisor(sb.toString());
            }

            {
                StringBuilder sb = new StringBuilder();
                if (pfe.getChairExaminer() != null) {
                    sb.append("Pr:").append(pfe.getChairExaminer().resolveFullName());
                }
                if (pfe.getFirstExaminer() != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append("R:").append(pfe.getFirstExaminer().resolveFullName());
                }
                if (pfe.getSecondExaminer() != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append("R:").append(pfe.getSecondExaminer().resolveFullName());
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
        sb.append("Select a from AcademicStudent a where 1=0 ");
        if (ns.contains(1)) {
            ns.remove(1);
            sb.append(" or a.lastClass1Id=:clsId ");
        }
        if (ns.contains(2)) {
            ns.remove(2);
            sb.append(" or a.lastClass2Id=:clsId ");
        }
        if (ns.contains(3)) {
            ns.remove(3);
            sb.append(" or a.lastClass3Id=:clsId ");
        }
        if (!ns.isEmpty()) {
            throw new IllegalArgumentException("Invalid class Number " + ns);
        }
        return pu.createQuery(sb.toString()).setParameter("clsId", classId).getResultList();
    }

    public void updateStudentClassByClass(int classNumber, int fromClassId, int toClassId) {
        for (AcademicStudent academicStudent : findStudentsByClass(fromClassId, classNumber)) {
            updateStudentClass(academicStudent.getId(), classNumber, toClassId);
        }
    }

    public AcademicStudent updateStudentClass(int studentId, int classNumber, int classId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicClass cls = academic.findAcademicClass(classId);
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

    public boolean addUserForStudent(AcademicStudent academicStudent) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_WRITE);
        AppUserType teacherType = VrApp.getBean(CorePlugin.class).findUserType("Student");
        CreateUserInfo ui=new CreateUserInfo()
                .setUserId(academicStudent.getUser().getId())
                .setFirstName(academicStudent.getUser().getFirstName())
                .setLastName(academicStudent.getUser().getLastName())
                .setUserTypeId(teacherType.getId())
                .setDepartmentId(academicStudent.getUser().getDepartment().getId())
                .setAttachToExistingUser(false)
                .setDefaultProfiles( new String[]{"Student"})
                .setPasswordStrategy(VrPasswordStrategyNin.INSTANCE)
                ;
        AppUser u = core.addUser(ui);
        academicStudent.setUser(u);
        UPA.getPersistenceUnit().merge(academicStudent);
        for (AcademicClass c : new AcademicClass[]{academicStudent.getLastClass1(), academicStudent.getLastClass2(), academicStudent.getLastClass3()}) {
            if (c != null) {
                AppProfile p = core.findOrCreateCustomProfile(c.getName(), "AcademicClass");
                core.addUserProfile(u.getId(), p.getCode());
            }

            AcademicProgram pr = academicStudent.getLastClass1() == null ? null : academicStudent.getLastClass1().getProgram();
            if (pr != null) {
                AppProfile p = core.findOrCreateCustomProfile(pr.getName(), "AcademicClass");
                core.addUserProfile(u.getId(), p.getCode());
            }
        }
        AppDepartment d = academicStudent.getUser().getDepartment();
        if (d != null) {
            AppProfile p = core.findOrCreateCustomProfile(d.getCode(), "Department");
            core.addUserProfile(u.getId(), p.getCode());
        }

        return true;
    }

    public String getValidName(AcademicStudent t) {
        if (t == null) {
            return "";
        }
        String name = t.resolveFullName();
        if (t.getUser() != null) {
            name = t.resolveFullName();
        }
        if (StringUtils.isBlank(name) && t.getUser() != null) {
            name = t.getUser().getLogin();
        }
        if (StringUtils.isBlank(name)) {
            name = "Teacher #" + t.getId();
        }
        return (name);
    }

    public void validateAcademicData_Student(int studentId, int periodId) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_WRITE);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AcademicStudent s = findStudent(studentId);
        Map<Integer, AcademicClass> academicClasses = academic.findAcademicClassesMap();
        AppUser u = s.getUser();
        AppDepartment d = null;
        Set<String> managedProfileTypes = new HashSet<>(Arrays.asList("Department", "StatusType", "AcademicClass", "AcademicProgram"));
        if (d == null && u != null) {
            d = u.getDepartment();
        }
//        if (s.getDepartment() == null && d != null) {
//            s.setDepartment(d);
//            UPA.getPersistenceUnit().merge(s);
//        }
        if (u != null) {
//            if (u.getDepartment() == null && d != null) {
//                u.setDepartment(d);
//                UPA.getPersistenceUnit().merge(u);
//            }
            HashSet<Integer> goodProfiles = new HashSet<>();

            {
                if (d != null) {
                    AppProfile p = core.findOrCreateCustomProfile(d.getCode(), "Department");
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

            for (AcademicClass ac : academic.findAcademicUpHierarchyList(new AcademicClass[]{s.getLastClass1(), s.getLastClass2(), s.getLastClass3()}, academicClasses)) {
                if (ac != null) {
                    //ignore inherited profiles in suffix
                    AppProfile p = core.findOrCreateCustomProfile(ac.getName(), "AcademicClass");
                    goodProfiles.add(p.getId());

                    AcademicProgram pr = ac.getProgram();
                    if (pr != null) {
                        p = core.findOrCreateCustomProfile(pr.getName(), "AcademicProgram");
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

            u.setPositionSuffix(goodSuffix.toString());

            u.setPositionTitle1("Student " + goodSuffix);
            pu.merge(u);

            if (u != null) {
                List<AppProfile> oldProfiles = core.findProfilesByUser(u.getId());
                for (AppProfile p : oldProfiles) {
                    if (goodProfiles.contains(p.getId())) {
                        goodProfiles.remove(p.getId());
                        //ok
                    } else if (p.isCustom() && (p.getCustomType() != null && managedProfileTypes.contains(p.getCustomType()))) {
                        core.removeUserProfile(u.getId(), p.getId());
                    }
                }
                for (Integer toAdd : goodProfiles) {
                    core.addUserProfile(u.getId(), toAdd);
                }
            }
        }
    }

    public List<AcademicClass> findStudentClasses(int studentId, boolean down, boolean up) {
        AcademicStudent student = findStudent(studentId);
        if (student == null) {
            return Collections.EMPTY_LIST;
        }
        AcademicClass[] refs = new AcademicClass[]{student.getLastClass1(), student.getLastClass2(), student.getLastClass3()};
        List<AcademicClass> upList = null;
        List<AcademicClass> downList = null;
        Map<Integer, AcademicClass> mm = academic.findAcademicClassesMap();
        if (down) {
            downList = academic.findClassDownHierarchyList(refs, mm);
        }
        if (up) {
            upList = academic.findAcademicUpHierarchyList(refs, mm);
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

}
