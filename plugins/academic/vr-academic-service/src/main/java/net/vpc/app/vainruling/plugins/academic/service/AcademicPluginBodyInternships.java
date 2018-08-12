package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import net.vpc.app.vainruling.core.service.util.NamedValueCount;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicProgram;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.config.*;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.*;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExt;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExtList;
import net.vpc.app.vainruling.core.service.util.LocationInfo;
import net.vpc.common.util.ListValueMap;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.QueryBuilder;
import net.vpc.upa.UPA;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import net.vpc.app.vainruling.core.service.util.JsonUtils;
import net.vpc.common.util.MapUtils;

public class AcademicPluginBodyInternships extends AcademicPluginBody {

    public void onStart() {
        VrApp.getBean(CorePlugin.class).createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_INTERNSHIPS, AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_INTERNSHIPS);
        VrApp.getBean(CorePlugin.class).createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_INTERNSHIP_BOARDS, AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_INTERNSHIP_BOARDS);
        VrApp.getBean(CorePlugin.class).createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ALL_INTERNSHIPS, AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ALL_INTERNSHIPS);
        VrApp.getBean(CorePlugin.class).createRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_INTERNSHIP_BOARDS_STAT, AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_INTERNSHIP_BOARDS_STAT);
    }

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
        LocationInfo currentLocation = VrUtils.resolveLocation(currentCompany);
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
            LocationInfo lc = VrUtils.resolveLocation(c);
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
        return VrUtils.buildNamedValueCountList(circle1, (ListValueMap) circle1_internships);
    }

    public List<NamedValueCount> statEvalInternshipGovernorate(List<AcademicInternship> internships) {
        Map<String, Number> circle2 = new LinkedHashMap<String, Number>();
        CorePlugin cp = VrApp.getBean(CorePlugin.class);
        AppCompany currentCompany = cp.getCurrentConfig().getMainCompany();
        LocationInfo currentLocation = VrUtils.resolveLocation(currentCompany);
        ListValueMap<String, AcademicInternship> circle2_internships = new ListValueMap<>();

        for (AcademicInternship ii : internships) {
            AppCompany c = ii.getCompany();
            LocationInfo lc = VrUtils.resolveLocation(c);
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
        return VrUtils.buildNamedValueCountList(circle2, (ListValueMap) circle2_internships);
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
        return VrUtils.buildNamedValueCountList(circle1, (ListValueMap) circle1_internships);
    }

    public List<NamedValueCount> statEvalInternshipTechnologies(List<AcademicInternship> internships) {
        Map<String, Number> circle2 = new LinkedHashMap<String, Number>();

        ListValueMap<String, AcademicInternship> circle2_internships = new ListValueMap<>();

        AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
        for (AcademicInternship ii : internships) {

            HashSet<String> set = new HashSet<String>(VrUtils.parseWords(ii.getTechnologies()));
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
        return VrUtils.buildNamedValueCountList(circle2, (ListValueMap) circle2_internships);
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
        return VrUtils.buildNamedValueCountList(circle1, (ListValueMap) circle1_internships);
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
        return VrUtils.buildNamedValueCountList(circle2, (ListValueMap) circle2_internships);
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

    public int generateInternships(AcademicInternship internship, String studentProfiles) {
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
        HashSet<Integer> validStudents = new HashSet<>();

        HashSet<String> validCodes = new HashSet<>();
        for (AcademicInternship vc : allFound) {
            validCodes.add(vc.getCode());
            if (vc.getStudent() != null) {
                validStudents.add(vc.getStudent().getId());
            }
            if (vc.getSecondStudent() != null) {
                validStudents.add(vc.getSecondStudent().getId());
            }
        }
        AppDepartment department = internship.getMainGroup().getDepartment();
        AppDepartment department2 = internship.getBoard().getDepartment();
        AcademicClass cls = internship.getBoard().getAcademicClass();
        AcademicProgram prog = internship.getBoard().getProgram();

        AppUserType studentType = core.findUserType("Student");
        if (cls != null) {
            studentProfiles = VrUtils.combineAndProfile(studentProfiles, cls.getName());
        }
        if (department != null) {
            studentProfiles = VrUtils.combineAndProfile(studentProfiles, department.getCode());
        }
        if (department2 != null) {
            studentProfiles = VrUtils.combineAndProfile(studentProfiles, department2.getCode());
        }
        if (prog != null) {
            studentProfiles = VrUtils.combineAndProfile(studentProfiles, prog.getName());
        }
        int generatedCount = 0;
        int alreadyFoundCount = 0;
        for (AppUser appUser : core.findUsersByProfileFilter(studentProfiles, studentType.getId())) {
            AcademicStudent student = acad.findStudentByUser(appUser.getId());
            if (student != null) {
                if (validStudents.contains(student.getId())) {
                    alreadyFoundCount++;
                    continue;
                }
                if (department != null) {
                    if ((student.getDepartment() == null) || (department.getId() != student.getDepartment().getId())) {
                        continue;
                    }
                }
                if (department2 != null) {
                    if (student.getDepartment() == null || department2.getId() != student.getDepartment().getId()) {
                        continue;
                    }
                }
                if (cls != null) {
                    if ((student.getLastClass1() == null || cls.getId() != student.getLastClass1().getId())
                            && (student.getLastClass2() == null || cls.getId() != student.getLastClass2().getId())
                            && (student.getLastClass3() == null || cls.getId() != student.getLastClass2().getId())) {
                        continue;
                    }
                }
                if (prog != null) {
                    if ((student.getLastClass1() == null || student.getLastClass1().resolveProgramType() == null || prog.getId() != student.getLastClass1().resolveProgramType().getId())
                            && (student.getLastClass2() == null || student.getLastClass2().resolveProgramType() == null || prog.getId() != student.getLastClass2().resolveProgramType().getId())
                            && (student.getLastClass3() == null || student.getLastClass3().resolveProgramType() == null || prog.getId() != student.getLastClass3().resolveProgramType().getId())) {
                        continue;
                    }
                }
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
                    generatedCount++;
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
        if (generatedCount > 0) {
            TraceService.get().trace("Academic.generated-internships", "success", MapUtils.map("name", internship.getBoard().getName(), "count", generatedCount, "oldCount", alreadyFoundCount),
                    JsonUtils.jsonMap("name", internship.getBoard().getName(), "count", generatedCount, "oldCount", alreadyFoundCount),
                    "/Education/Internships", Level.INFO
            );
        }
        return generatedCount;
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

    public void generateInternships(int internshipId, String studentProfiles) {
        AcademicInternship internship = findInternship(internshipId);
        if (internship == null) {
            throw new RuntimeException("Internship with id " + internshipId + " not found");
        }
        generateInternships(internship, studentProfiles);
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
                List<AcademicInternship> internships = pu.createQuery("Select u from AcademicInternship u where "
                        + "(u.boardId==null or (u.board.departmentId=:departmentId "
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
                List<AcademicInternshipSupervisorIntent> supervisorIntents = pu.createQuery("Select u from AcademicInternshipSupervisorIntent u where "
                        + "(u.internship.boardId==null or (u.internship.board.departmentId=:departmentId"
                        + (periodId >= 0 ? " and u.internship.board.periodId=:periodId " : "")
                        + (internshipTypeId >= 0 ? " and u.internship.board.internshipTypeId=:internshipTypeId " : "")
                        + " ))"
                        + (openOnly ? " and u.internship.internshipStatus.closed=false" : "")
                )
                        .setParameter("departmentId", d.getId())
                        .setParameter("periodId", periodId, periodId >= 0)
                        .setParameter("internshipTypeId", internshipTypeId, internshipTypeId >= 0)
                        .getResultList();
                List<AcademicInternshipBoardMessage> messages = pu.createQuery("Select u from AcademicInternshipBoardMessage u "
                        + "where (u.internship.boardId==null or (u.internship.board.departmentId=:departmentId "
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
            List<AcademicInternshipBoard> goodBoards = null;
            if (teacherId > 0) {
                goodBoards = pu.createQuery("Select u.board from AcademicInternshipBoardTeacher u where "
                        + " 1=1"
                        + ((openOnly) ? " and u.board.enabled=true" : "")
                        + ((deptId > 0) ? (" and u.board.departmentId=" + deptId) : "")
                        + ((periodId > 0) ? (" and u.board.periodId=" + periodId) : "")
                        + ((teacherId > 0) ? (" and u.teacherId=" + teacherId) : "")
                        + ((internshipTypeId > 0) ? (" and u.board.internshipTypeId=" + internshipTypeId) : "")
                )
                        .getResultList();
            } else {
                goodBoards = pu.createQuery("Select board from AcademicInternshipBoard board where "
                        + " 1=1"
                        + ((openOnly) ? " and board.enabled=true" : "")
                        + ((deptId > 0) ? (" and board.departmentId=" + deptId) : "")
                        + ((periodId > 0) ? (" and board.periodId=" + periodId) : "")
                        + ((internshipTypeId > 0) ? (" and board.internshipTypeId=" + internshipTypeId) : "")
                )
                        .getResultList();
            }
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
                .getResultList();

    }

    public AcademicInternshipSupervisorIntent findInternshipTeacherIntent(int internship, int teacherId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AcademicInternshipSupervisorIntent u where u.internshipId=:id and u.teacherId=:teacherId")
                .setParameter("id", internship)
                .setParameter("teacherId", teacherId)
                .getFirstResultOrNull();
    }

}
