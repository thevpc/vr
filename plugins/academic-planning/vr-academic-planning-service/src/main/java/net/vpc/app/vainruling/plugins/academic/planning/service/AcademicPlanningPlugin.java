/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.planning.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.ListValueMap;
import net.vpc.common.util.SetValueMap;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileFilter;
import net.vpc.upa.Action;
import net.vpc.upa.NamedId;
import net.vpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
import java.util.function.Predicate;
import net.vpc.app.vainruling.core.service.ProfileRightBuilder;
import net.vpc.app.vainruling.VrPlugin;
import net.vpc.app.vainruling.VrStart;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import net.vpc.app.vainruling.plugins.calendars.service.CalendarsUtils;
import net.vpc.app.vainruling.plugins.calendars.service.dto.CalendarActivity;
import net.vpc.app.vainruling.plugins.calendars.service.dto.CalendarDay;
import net.vpc.app.vainruling.plugins.calendars.service.dto.CalendarHour;

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
        ListValueMap<String, Integer> teacherNames = new ListValueMap<>();
        for (Integer teacherId : teacherIds) {
            //
            AcademicTeacher teacher = academicPlugin.findTeacher(teacherId);

            String teacherName = teacher == null ? "" : teacher.resolveFullName();
            teacherNames.put(VrUtils.normalizeName(teacherName), teacherId);
            if (teacher != null) {
                for (String s : splitOtherNames(teacher.getOtherNames())) {
                    teacherNames.put(VrUtils.normalizeName(s), teacherId);
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
                            validTeachers.addAll(teacherNames.get(uniformName));
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
        SetValueMap<String, Integer> nameMapping = new SetValueMap<>();
        for (Integer studentId : studentIds) {
            AcademicStudent student = academicPlugin.findStudent(studentId);
            AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
            List<AcademicClass> allCls = ap.findClassDownHierarchyList(new AcademicClass[]{student.getLastClass1(), student.getLastClass2(), student.getLastClass3()}, null);
            for (AcademicClass ac : allCls) {
                String n2 = VrUtils.normalizeName(ac.getName());
                nameMapping.put(n2, studentId);
                for (String s : splitOtherNames(ac.getOtherNames())) {
                    nameMapping.put(s, studentId);
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
                            all.addAll(nameMapping.get(uniformName));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return all;
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
