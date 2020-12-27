/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.planning.service;

import net.thevpc.app.vainruling.VrPlugin;
import net.thevpc.app.vainruling.VrStart;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.util.ListValueMap;
import net.thevpc.common.util.SetValueMap;
import net.thevpc.common.vfs.VFile;
import net.thevpc.common.vfs.VFileFilter;
import net.thevpc.upa.Action;
import net.thevpc.upa.NamedId;
import net.thevpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.thevpc.app.vainruling.core.service.ProfileRightBuilder;
import net.thevpc.app.vainruling.core.service.VrLabel;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseAssignment;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.model.current.AcademicCourseType;
import net.thevpc.app.vainruling.plugins.academic.planning.service.model.CalendarAssignment;
import net.thevpc.app.vainruling.plugins.academic.planning.service.model.CalendarAssignmentsMapping;
import net.thevpc.app.vainruling.plugins.academic.planning.service.model.CalendarClass;
import net.thevpc.app.vainruling.plugins.academic.planning.service.model.CalendarCoursePlan;
import net.thevpc.app.vainruling.plugins.academic.planning.service.model.CalendarTeacher;
import net.thevpc.app.vainruling.plugins.academic.service.integration.parsers.AcademicClassParser;
import net.thevpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import net.thevpc.app.vainruling.plugins.calendars.service.CalendarsUtils;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarActivity;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarDay;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarHour;
import net.thevpc.common.util.Collections2;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin()
public class AcademicPlanningPlugin {

    @Autowired
    CorePlugin core;
    @Autowired
    AcademicPlugin academicPlugin;

    public static AcademicPlanningPlugin get() {
        return VrApp.getBean(AcademicPlanningPlugin.class);
    }

    @VrStart
    private void onStart() {
        if (core == null) {
            core = CorePlugin.get();
        }
        ProfileRightBuilder b = new ProfileRightBuilder();
        b.addName(AcademicPlanningPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_PLANNING);
        b.addName(AcademicPlanningPluginSecurity.RIGHT_CUSTOM_EDUCATION_CLASS_PLANNING);
        b.execute();
    }

    public Set<Integer> retainTeachersWithPublicCalendars(Set<Integer> teacherIds) {
        Set<Integer> validTeachers = new HashSet<>();
        ListValueMap<String, Integer> teacherNames = Collections2.arrayListValueHashMap();
        for (Integer teacherId : teacherIds) {
            //
            AcademicTeacher teacher = academicPlugin.findTeacher(teacherId);

            String teacherName = teacher == null ? "" : teacher.resolveFullName();
            teacherNames.add(VrUtils.normalizeName(teacherName), teacherId);
            if (teacher != null) {
                for (String s : splitOtherNames(teacher.getOtherNames())) {
                    teacherNames.add(VrUtils.normalizeName(s), teacherId);
                }
            }
        }
        VFile[] emploisFiles = getEmploiFolder().listFiles((VFile pathname) -> pathname.getName().toLowerCase().endsWith("_teachers.xml"));
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
                        String uniformName = VrUtils.normalizeName(tn);
                        if (teacherNames.containsKey(uniformName)) {
                            validTeachers.addAll(teacherNames.getValues(uniformName));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return validTeachers;
    }

    public WeekCalendar loadTeacherPlanning(int teacherId) {
        AcademicTeacher teacher = academicPlugin.findTeacher(teacherId);

        String teacherName = teacher == null ? "" : teacher.resolveFullName();
        HashSet<String> teacherNames = new HashSet<>();
        teacherNames.add(VrUtils.normalizeName(teacherName));
        if (teacher != null) {
            for (String s : splitOtherNames(teacher.getOtherNames())) {
                teacherNames.add(s);
            }
        }
        VFile[] emploisFiles = getEmploiFolder().listFiles((VFile pathname) -> pathname.getName().toLowerCase().endsWith("_teachers.xml"));
        VFile p = emploisFiles.length > 0 ? emploisFiles[0] : null;
        if (p != null && p.exists()) {
            try {
                FETXmlParser parser = new FETXmlParser(p, "Teacher", "Formation Ingénieur");
                CalendarWeekParser week;
                try {
                    int counter = 0;
                    while ((week = parser.next()) != null) {
                        if (teacherNames.contains(VrUtils.normalizeName(week.getName()))) {
                            counter++;
                            return week.parse("Teacher-" + counter);
                        }
                    }
                } finally {
                    parser.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private VFile getEmploiFolder() {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<VFile>() {
            @Override
            public VFile run() {
                final String academicPlanningPluginPlanningRoot = "AcademicPlanningPlugin.PlanningRoot";
                Object t = core.getAppPropertyValue(academicPlanningPluginPlanningRoot, null);
                String ts = StringUtils.nonNull(t);
                if (ts.trim().length() > 0) {
                    return core.getRootFileSystem().get(ts);
                } else {
                    final String defaultPath = core.getProfileFileSystem("DirectorOfStudies").get("/Emplois").getBaseFile("vrfs").getPath();
                    t = core.getOrCreateAppPropertyValue(academicPlanningPluginPlanningRoot, null, defaultPath);
                    return core.getRootFileSystem().get(StringUtils.nonNull(t));
                }
            }
        });
    }

    public Set<Integer> retainStudentsWithPublicCalendars(Set<Integer> studentIds) {
        Set<Integer> all = new HashSet<>();
        SetValueMap<String, Integer> nameMapping = Collections2.setValueMap();
        for (Integer studentId : studentIds) {
            AcademicStudent student = academicPlugin.findStudent(studentId);
            AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
            List<AcademicClass> allCls = ap.findClassDownHierarchyList(new AcademicClass[]{student.getLastClass1(), student.getLastClass2(), student.getLastClass3()}, null);
            for (AcademicClass ac : allCls) {
                String n2 = VrUtils.normalizeName(ac.getName());
                nameMapping.add(n2, studentId);
                for (String s : splitOtherNames(ac.getOtherNames())) {
                    nameMapping.add(s, studentId);
                }
            }
        }

        VFile[] emploisFiles = getEmploiFolder().listFiles((VFile pathname) -> pathname.getName().toLowerCase().endsWith("_subgroups.xml"));
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
                        String uniformName = VrUtils.normalizeName(tn);
                        if (nameMapping.containsKey(uniformName)) {
                            all.addAll(nameMapping.getValues(uniformName));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return all;
    }

    public CalendarAssignmentsMapping loadAllAssignments(int periodId, int semesterId, Integer clazzId) {
        CalendarAssignmentsMappingBuilder b = new CalendarAssignmentsMappingBuilder();
        List<CalendarAssignmentsMapping> a = new ArrayList<>();
        AcademicClassParser cp = new AcademicClassParser();
        List<WeekCalendar> all = loadAllGroupedClassPlannings()
                .stream().filter((x)
                        -> {
                    if (clazzId == null) {
                        return VrUtils.normalize(x.getPlanningUniformName()).matches("[a-z]{2,3}\\d[.]\\d");
                    }
                    AcademicClass ac = cp.get(x.getPlanningUniformName());
                    return (ac != null && clazzId.equals(ac.getId()));
                }
                ).collect(Collectors.toList());
        for (WeekCalendar lcp : all) {
            a.add(classCalendarToCalendarAssignmentsMapping(lcp, periodId, semesterId, clazzId, b));
        }
        return mergeCalendarAssignmentsMappings(a.toArray(new CalendarAssignmentsMapping[0]));
    }

    public CalendarAssignmentsMapping classCalendarToCalendarAssignmentsMapping(WeekCalendar classCalendar, int periodId, int semesterId, Integer clazzId, CalendarAssignmentsMappingBuilder b) {
        CalendarAssignmentsMapping mm = new CalendarAssignmentsMapping();
        b.setPeriod(core.findPeriod(periodId));
        b.setSemester(academicPlugin.findSemester(semesterId));
        CalendarClass c = b.findCalendarClass(classCalendar.getPlanningName());
        if (c.getAcademicClass() == null) {
            return null;
        }
        AcademicCourseType tpType = b.getParsers().getCourseTypes().get("TP");
        AcademicCourseType cType = b.getParsers().getCourseTypes().get("C");
        Map<String, CalendarAssignment> mmm = new LinkedHashMap<>();
        HashSet<Integer> visitedCoursePrograms = new HashSet<Integer>();
        HashSet<Integer> visitedAssignments = new HashSet<Integer>();
        for (CalendarDay day : classCalendar.getDays()) {
            for (CalendarHour hour : day.getHours()) {
                for (CalendarActivity activity : hour.getActivities()) {
                    CalendarAssignment cass = new CalendarAssignment();
                    cass.setAcademicClass(c);
                    String subject = StringUtils.trim(activity.getSubject());
                    if (!StringUtils.isBlank(subject)) {
                        boolean tp = false;
                        boolean qz = false;
                        if (subject.endsWith("(1/15)")) {
                            subject = subject.substring(0, subject.length() - 6).trim();
                            qz = true;
                        }
                        if (subject.toLowerCase().endsWith("(tp)")) {
                            subject = subject.substring(0, subject.length() - 4).trim();
                            tp = true;
                        }
                        CalendarCoursePlan ccc = b.findCalendarCoursePlanByClass(c.getAcademicClass().getId(), subject);
//                        if (ccc.getCoursePlan() == null && StringUtils.isBlank(ccc.getCoursePlanName())) {
//                            ccc = b.findCalendarCoursePlanByClass(c.getAcademicClass().getId(), subject);
//                        }
                        cass.setCoursePlan(ccc);
                        AcademicCoursePlan seenCP = cass.getCoursePlan().getCoursePlan();
                        if (seenCP != null) {
                            visitedCoursePrograms.add(seenCP.getId());
                        }
                        cass.setTeacher(b.findCalendarTeacher(activity.getTeacher()));
                        int weeks = 14;//
                        double timeSlot = 1.5;//
                        if (tp) {
                            cass.setTp(timeSlot * weeks);
                        } else {
                            if (qz) {
                                cass.setC(timeSlot * weeks / 2.0);
                                cass.setTd(0);
                            } else {
                                cass.setC(timeSlot * weeks);
                                cass.setTd(0);
                            }
                        }
                        cass.setGrp(1);
                        if (qz) {
                            cass.setW(0.5);
                        } else {
                            cass.setW(1);
                        }
                        cass.setCourseType(tp ? tpType : cType);
                        String k = buildCalendarAssignmentGroupKey(cass);
                        CalendarAssignment oo = mmm.get(k);
                        if (oo == null) {
                            if (cass.getAcademicClass().getAcademicClass() != null && cass.getCoursePlan().getCoursePlan() != null) {
                                List<AcademicCourseAssignment> assignments = academicPlugin.findAssignments(null, cass.getCoursePlan().getCoursePlan().getId(), cass.getAcademicClass().getAcademicClass().getId(),
                                        null,
                                        null, semesterId, cass.getCourseType().getId());
                                for (AcademicCourseAssignment assignment : assignments) {
                                    visitedAssignments.add(assignment.getId());
                                }
                                if (cass.getTeacher().getTeacher() == null) {
                                    for (AcademicCourseAssignment assignment : assignments) {
                                        if (assignment.getTeacher() != null) {
                                            cass.getLabels().add(VrLabel.forDanger("AssignedToOther", "Assigned to " + assignment.getTeacher().getUser().getFullName())
                                                    .setAction("AcademicCourseAssignment", String.valueOf(assignment.getId()))
                                            );
                                            cass.getLabels().add(VrLabel.forInfo("Assignable", "Assignable"));
                                        } else {
                                            cass.getLabels().add(VrLabel.forInfo("Assignable", "Assignable"));
                                            cass.getLabels().add(VrLabel.forDanger("NotAssigned", "Not Assigned").setAction("AcademicCourseAssignment", String.valueOf(assignment.getId())));
                                        }
                                    }
                                    if (assignments.isEmpty()) {
                                        cass.getLabels().add(VrLabel.forDanger("MissingAssignement", "Missing Assignement").setAction("AcademicCoursePlan", String.valueOf(cass.getCoursePlan().getCoursePlan().getId())));
                                    }
                                } else {
                                    for (AcademicCourseAssignment assignment : assignments) {
                                        if (assignment.getTeacher() == null) {
                                            cass.setAssignment(assignment);
                                            cass.getLabels().add(VrLabel.forWarning("NotAssigned", "Not Assigned").setAction("AcademicCourseAssignment", String.valueOf(assignment.getId())));
                                            cass.getLabels().add(VrLabel.forInfo("Assignable", "Assignable"));
                                        } else if (assignment.getTeacher().getId() != cass.getTeacher().getTeacher().getId()) {
                                            cass.getLabels().add(VrLabel.forDanger("AssignedToOther", "Assigned to " + assignment.getTeacher().getUser().getFullName())
                                                    .setAction("AcademicCourseAssignment", String.valueOf(assignment.getId()))
                                            );
                                            cass.getLabels().add(VrLabel.forInfo("Assignable", "Assignable"));
                                        } else {
                                            if (cass.getAssignment() == null) {
                                                cass.setAssignment(assignment);
                                            } else {
                                                cass.getLabels().add(VrLabel.forDanger("AssignedExtra", "Too Many Assignements").setAction("AcademicCourseAssignment", String.valueOf(assignment.getId())));
                                            }
                                        }
                                    }
                                    if (assignments.isEmpty()) {
                                        cass.getLabels().add(VrLabel.forDanger("MissingAssignement", "Missing Assignement").setAction("AcademicCoursePlan", String.valueOf(cass.getCoursePlan().getCoursePlan().getId())));
                                    }
                                }
                            }
                            mmm.put(k, cass);
                        } else {
                            oo.setC(oo.getC() + cass.getC());
                            oo.setTd(oo.getTd() + cass.getTd());
                            oo.setTp(oo.getTp() + cass.getTp());
                            oo.setW(oo.getW() + cass.getW());
                        }
                    }
                }
            }
        }
        mm.getElements().addAll(mmm.values());
        for (CalendarAssignment element : mm.getElements()) {
            if (element.getC() != 0 && element.getTd() == 0) {
                if (Math.abs(element.getC() - 21) < 1E-2) {
                    element.setC(15);
                    element.setTd(6);
                } else if (Math.abs(element.getC() - 31.5) < 1E-2) {
                    element.setC(21);
                    element.setTd(10.5);
                } else if (Math.abs(element.getC() - 42) < 1E-2) {
                    element.setC(30);
                    element.setTd(12);
                }
            }
        }
        if (true) {
            for (AcademicCourseAssignment assignment : academicPlugin.findAssignments(periodId, null, clazzId, null, null, semesterId, null)) {
                if (!visitedAssignments.contains(assignment.getId())) {
                    visitedAssignments.add(assignment.getId());
                    visitedCoursePrograms.add(assignment.getCoursePlan().getId());
                    if (assignment.getCourseType().getId() == cType.getId() || assignment.getCourseType().getId() == tpType.getId()) {
                        CalendarAssignment cs = new CalendarAssignment();
                        cs.setAssignment(assignment);
                        double grp = assignment.getGroupCount();
                        cs.setC(assignment.getValueC() * grp);
                        cs.setTd(assignment.getValueTD() * grp);
                        cs.setTp(assignment.getValueTP() * grp);
                        AcademicClass cls2 = assignment.getSubClass() != null ? assignment.getSubClass() : assignment.getCoursePlan().getCourseLevel().getAcademicClass();
                        cs.setAcademicClass(new CalendarClass(cls2.getName(), cls2));
                        cs.setCoursePlan(new CalendarCoursePlan(null, assignment.getCoursePlan()));
                        cs.setTeacher(new CalendarTeacher(assignment.getTeacher() == null ? "Unconnu" : assignment.getTeacher().getUser().getFullName(), assignment.getTeacher()));
                        cs.getLabels().add(VrLabel.forWarning("MissingPlanning", "Not Planned Assignement").setAction("AcademicCourseAssignment", String.valueOf(assignment.getId())));
                        cs.setCourseType(assignment.getCourseType());
                        mm.getElements().add(cs);
                    }
                }
            }
            for (AcademicCoursePlan cp : academicPlugin.findCoursePlans(periodId, null, clazzId, semesterId)) {
                if (!visitedCoursePrograms.contains(cp.getId())) {
                    visitedCoursePrograms.add(cp.getId());
                    CalendarAssignment cs = new CalendarAssignment();
                    cs.setC(cp.getValueC() * cp.getGroupCountC());
                    cs.setTd(cp.getValueTD() * cp.getGroupCountTD());
                    cs.setTp(cp.getValueTP() * cp.getGroupCountTP());
                    if (cs.getC() != 0 || cs.getTp() != 0) {
                        if (cs.getC() != 0) {
                            cs.setCourseType(cType);
                        } else {
                            cs.setCourseType(tpType);
                        }
                        AcademicClass cls = cp.getCourseLevel().getAcademicClass();
                        cs.setAcademicClass(new CalendarClass(cls.getName(), cls));
                        cs.setCoursePlan(new CalendarCoursePlan(null, cp));
                        cs.setTeacher(new CalendarTeacher("Unconnu", null));
                        cs.getLabels().add(VrLabel.forWarning("MissingCourse", "Not Planned Course").setAction("AcademicCoursePlan", String.valueOf(cp.getId())));
                        mm.getElements().add(cs);
                    }
                }
            }
        }
        return mm;
    }

    private CalendarAssignmentsMapping mergeCalendarAssignmentsMappings(CalendarAssignmentsMapping... all) {
        Map<String, CalendarAssignment> mmm = new HashMap<>();
        for (CalendarAssignmentsMapping m : all) {
            for (CalendarAssignment cass : m.getElements()) {
                String k = buildCalendarAssignmentGroupKey(cass);
                CalendarAssignment oo = mmm.get(k);
                if (oo == null) {
                    mmm.put(k, cass);
                } else {
                    oo = oo.copy();
                    mmm.put(k, oo);
                    oo.setC(oo.getC() + cass.getC());
                    oo.setTd(oo.getTd() + cass.getTd());
                    oo.setTp(oo.getTp() + cass.getTp());
                    oo.setW(oo.getW() + cass.getW());
                }
            }
        }
        CalendarAssignmentsMapping r = new CalendarAssignmentsMapping();
        r.getElements().addAll(mmm.values());
        return r;
    }

    private String buildCalendarAssignmentGroupKey(CalendarAssignment cass) {
        boolean tp = cass.getTp() > 0;
        return cass.getAcademicClass().getAcademicClassName() + "\n"
                + cass.getCoursePlan().getCoursePlanName() + "\n"
                + cass.getTeacher().getTeacherName() + "\n"
                + (tp ? "TP" : "C");
    }

    public List<WeekCalendar> loadStudentPlanningList(int studentId) {
        AcademicStudent student = academicPlugin.findStudent(studentId);
        List<WeekCalendar> list = new ArrayList<>();
        HashMap<String, String> nameMapping = new HashMap<>();
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        List<AcademicClass> allCls = ap.findClassDownHierarchyList(new AcademicClass[]{student.getLastClass1(), student.getLastClass2(), student.getLastClass3()}, null);
        for (AcademicClass ac : allCls) {
            String n2 = VrUtils.normalizeName(ac.getName().trim());
            nameMapping.put(n2, n2);
            for (String s : splitOtherNames(ac.getOtherNames())) {
                if (!nameMapping.containsKey(s)) {
                    nameMapping.put(s, n2);
                }
            }
        }
        VFile[] emploisFiles = getEmploiFolder().listFiles(new VFileFilter() {

            @Override
            public boolean accept(VFile pathname) {
                return pathname.getName().toLowerCase().endsWith("_subgroups.xml");
            }
        });
        VFile p = emploisFiles.length > 0 ? emploisFiles[0] : null;
        if (p != null && p.exists()) {
            try {

                try {
                    FETXmlParser parser = new FETXmlParser(p, "Subgroup", "Formation Ingénieur");
                    CalendarWeekParser week;
                    try {
                        int counter = 0;
                        while ((week = parser.next()) != null) {
                            if (nameMapping.containsKey(VrUtils.normalizeName(week.getName()))) {
                                counter++;
                                list.add(week.parse("Class-" + counter));
                            }
                        }
                    } finally {
                        parser.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public WeekCalendar loadClassPlanning(String className) {
        if (className == null) {
            className = "";
        }
        if (StringUtils.isBlank(className)) {
            return null;
        }
        if (className.startsWith("[L2]:")) {
            String prefix = className.substring("[L2]:".length());
            String uniformClassName = VrUtils.normalizeName(prefix);
            if (StringUtils.isBlank(uniformClassName)) {
                return null;
            }
            List<WeekCalendar> found = loadClassPlannings(week -> {
                String yy = VrUtils.normalizeName(week.getName());
                return yy.startsWith(uniformClassName + ".") || yy.startsWith(uniformClassName + "-");
            }, false);
            String bestName = prefix;
            if (found.size() > 0) {
                bestName = found.get(0).getPlanningName().substring(0, prefix.length());
            }
            return CalendarsPlugin.get().mergeWeekCalendars(found, bestName);
        } else {
            String uniformClassName = VrUtils.normalizeName(className);
            if (StringUtils.isBlank(uniformClassName)) {
                return null;
            }
            List<WeekCalendar> found = loadClassPlannings(week -> uniformClassName.equals(VrUtils.normalizeName(week.getName())), true);
            return found.isEmpty() ? null : found.get(0);
        }
    }

    public List<WeekCalendar> loadAllGroupedClassPlannings() {
        return loadAllClassPlannings(true, true);
    }

    public List<WeekCalendar> loadAllClassPlannings(boolean group, boolean removeGrouped) {
        List<WeekCalendar> found = loadClassPlannings(null, false);
        if (!group) {
            return found;
        }
        Map<String, List<WeekCalendar>> mapped = new LinkedHashMap<>();
        for (Iterator<WeekCalendar> it = found.iterator(); it.hasNext();) {
            WeekCalendar weekCalendar = it.next();
            String n0 = VrUtils.normalize(weekCalendar.getPlanningUniformName());
            String n = CalendarsUtils.parentStudentsDescriminator(n0);
            if (!n.equals(n0)) {
                List<WeekCalendar> a = mapped.get(n);
                if (a == null) {
                    a = new ArrayList<>();
                    mapped.put(n, a);
                }
                a.add(weekCalendar);
            } else {
                List<WeekCalendar> a = null;
                if (!removeGrouped) {
                    a = mapped.get(n);
                    if (a == null) {
                        a = new ArrayList<>();
                        mapped.put(n, a);
                    }
                    a.add(weekCalendar);
                }
                a = mapped.get(n0);
                if (a == null) {
                    a = new ArrayList<>();
                    mapped.put(n0, a);
                }
                a.add(weekCalendar);
            }
        }
        List<WeekCalendar> result = new ArrayList<>();
        for (List<WeekCalendar> value : mapped.values()) {
            if (value.size() == 1) {
                result.add(value.get(0));
            } else if (value.size() > 0) {
                result.add(CalendarsPlugin.get().mergeWeekCalendars(value, null));
            }
        }
        return result;
    }

    public List<WeekCalendar> loadClassPlannings(Predicate<CalendarWeekParser> filter, boolean first) {
        List<WeekCalendar> all = new ArrayList<>();
        VFile[] emploisFiles = getEmploiFolder().listFiles((VFile pathname) -> pathname.getName().toLowerCase().endsWith("_subgroups.xml"));
        VFile p = emploisFiles.length > 0 ? emploisFiles[0] : null;
        if (p != null && p.exists()) {
            try {
                FETXmlParser parser = new FETXmlParser(p, "Subgroup", "Formation Ingénieur");
                CalendarWeekParser week;
                try {
                    int counter = 0;
                    while ((week = parser.next()) != null) {
                        if (filter == null || filter.test(week)) {
                            counter++;
                            WeekCalendar r = week.parse("Class-" + counter);
                            if (r != null) {
                                all.add(r);
                                if (first) {
                                    break;
                                }
                            }
                        }
                    }
                } finally {
                    parser.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return all;
    }

    public WeekCalendar loadRoomPlanning(String className) {
        if (className == null) {
            className = "";
        }
        if (StringUtils.isBlank(className)) {
            return null;
        }
        String uniformClassName = VrUtils.normalizeName(className);
        if (StringUtils.isBlank(uniformClassName)) {
            return null;
        }
        return loadRoomPlanning(room -> uniformClassName.equals(VrUtils.normalizeName(room)), true);
    }

    public WeekCalendar loadRoomPlanning(Predicate<String> filterByRoomName, boolean clearRoomName) {
        List<WeekCalendar> all = loadClassPlannings(null, false);
        List<WeekCalendar> toMerge = new ArrayList<>();
        Set<String> names = new TreeSet<>();
        for (WeekCalendar w : all) {
            boolean empty = true;
            for (CalendarDay day : w.getDays()) {
                for (CalendarHour hour : day.getHours()) {
                    for (Iterator<CalendarActivity> it = hour.getActivities().iterator(); it.hasNext();) {
                        CalendarActivity activity = it.next();
                        if (filterByRoomName.test(activity.getRoom())) {
                            if (clearRoomName) {
                                activity.setRoom("");
                            }
                            empty = false;
                            names.add(activity.getRoom());
                        } else {
                            it.remove();
                        }
                    }
                }
            }
            if (!empty) {
                toMerge.add(w);
            }
        }
        if (toMerge.isEmpty()) {
            return null;
        }
        return CalendarsPlugin.get().mergeWeekCalendars(toMerge, String.join(";", names));
    }

    private Set<String> splitOtherNames(String value) {
        Set<String> all = new HashSet<>();
        if (value != null) {
            for (String s : value.split(",|;")) {
                if (s.trim().length() > 0) {
                    all.add(VrUtils.normalizeName(s));
                }
            }
        }
        return all;
    }

    public List<NamedId> loadRoomPlanningListNames() {
        ArrayList<NamedId> ret = new ArrayList<>();
        List<WeekCalendar> all = loadClassPlannings(null, false);
        Set<String> names = new TreeSet<>();
        for (WeekCalendar w : all) {
            boolean empty = true;
            for (CalendarDay day : w.getDays()) {
                for (CalendarHour hour : day.getHours()) {
                    for (Iterator<CalendarActivity> it = hour.getActivities().iterator(); it.hasNext();) {
                        CalendarActivity activity = it.next();
                        if (!StringUtils.isBlank(activity.getRoom())) {
                            names.add(activity.getRoom());
                        }
                    }
                }
            }
        }
        for (String name : names) {
            ret.add(new NamedId(VrUtils.normalize(name), name));
        }
        return ret;
    }

    public List<NamedId> loadStudentPlanningListNames() {
        TreeSet<NamedId> all = new TreeSet<>(new Comparator<NamedId>() {
            @Override
            public int compare(NamedId o1, NamedId o2) {
                String n1 = StringUtils.nonNull(o1.getName());
                String n2 = StringUtils.nonNull(o2.getName());
                int i = n1.compareTo(n2);
                if (i != 0) {
                    return i;
                }
                return StringUtils.nonNull(o1.getId()).compareTo(StringUtils.nonNull(o2.getId()));
            }
        });

        VFile[] emploisFiles = getEmploiFolder().listFiles((VFile pathname) -> pathname.getName().toLowerCase().endsWith("_subgroups.xml"));
        VFile p = emploisFiles.length > 0 ? emploisFiles[0] : null;
        if (p != null && p.exists()) {
            try {
                FETXmlParser parser = new FETXmlParser(p, "Subgroup", "Formation Ingénieur");
                CalendarWeekParser week;
                try {
                    int counter = 0;
                    while ((week = parser.next()) != null) {
                        String tn = VrUtils.normalizeName(week.getName());
                        all.add(new NamedId(tn, week.getName()));
                    }
                } finally {
                    parser.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (NamedId aa : all.toArray(new NamedId[0])) {
            String n = CalendarsUtils.parentStudentsDescriminator((String) aa.getName());
            if (!n.equals(aa.getName())) {
                NamedId y = new NamedId("[L2]:" + VrUtils.normalizeName(n), n);
                all.add(y);
            }
        }
        return new ArrayList<>(all);
    }

}
