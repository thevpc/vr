/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.planning.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.calendars.service.VrCalendarProvider;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarWeek;
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
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin()
public class AcademicPlanningPlugin {

    @Autowired
    CorePlugin core;
    @Autowired
    AcademicPlugin academicPlugin;

    public static AcademicPlanningPlugin get(){
        return VrApp.getBean(AcademicPlanningPlugin.class);
    }

    @Start
    private void onStart(){
        if (core == null) {
            core = CorePlugin.get();
        }
        core.createRight(AcademicPlanningPluginSecurity.RIGHT_CUSTOM_EDUCATION_TEACHER_PLANNING, "TeacherPlanning");
        core.createRight(AcademicPlanningPluginSecurity.RIGHT_CUSTOM_EDUCATION_CLASS_PLANNING, "ClassPlanning");
    }




    public Set<Integer> retainTeachersWithPublicCalendars(Set<Integer> teacherIds) {
        Set<Integer> validTeachers = new HashSet<>();
        ListValueMap<String, Integer> teacherNames = new ListValueMap<>();
        for (Integer teacherId : teacherIds) {
            //
            AcademicTeacher teacher = academicPlugin.findTeacher(teacherId);

            String teacherName = teacher == null ? "" : teacher.resolveFullName();
            teacherNames.put(teacherName.toLowerCase(), teacherId);
            if (teacher != null) {
                for (String s : splitOtherNames(teacher.getOtherNames())) {
                    teacherNames.put(s, teacherId);
                }
            }
        }
        VFile[] emploisFiles = getEmploiFolder().listFiles(new VFileFilter() {

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
                        String uniformName = tn.trim().toLowerCase().trim();
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

    public CalendarWeek loadTeacherPlanning(int teacherId) {
        AcademicTeacher teacher = academicPlugin.findTeacher(teacherId);

        String teacherName = teacher == null ? "" : teacher.resolveFullName();
        HashSet<String> teacherNames = new HashSet<>();
        teacherNames.add(teacherName.toLowerCase());
        if (teacher != null) {
            for (String s : splitOtherNames(teacher.getOtherNames())) {
                teacherNames.add(s);
            }
        }
        VFile[] emploisFiles = getEmploiFolder().listFiles(new VFileFilter() {

            @Override
            public boolean accept(VFile pathname) {
                return pathname.getName().toLowerCase().endsWith("_teachers.xml");
            }
        });
        VFile p = emploisFiles.length > 0 ? emploisFiles[0] : null;
        if (p != null && p.exists()) {
            try {
                FETXmlParser parser = new FETXmlParser(p, "Teacher", "Formation Ingénieur");
                CalendarWeekParser week;
                try {
                    int counter = 0;
                    while ((week = parser.next()) != null) {
                        if (teacherNames.contains(week.getName().trim().toLowerCase().trim())) {
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
            List<AcademicClass> allCls = ap.findAcademicDownHierarchyList(new AcademicClass[]{student.getLastClass1(), student.getLastClass2(), student.getLastClass3()}, null);
            for (AcademicClass ac : allCls) {
                String n2 = ac.getName().trim().toLowerCase();
                nameMapping.put(n2, studentId);
                for (String s : splitOtherNames(ac.getOtherNames())) {
                    nameMapping.put(s, studentId);
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
                        String uniformName = tn.trim().toLowerCase();
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

    public List<CalendarWeek> loadStudentPlanningList(int studentId) {
        AcademicStudent student = academicPlugin.findStudent(studentId);
        List<CalendarWeek> list = new ArrayList<>();
        HashMap<String, String> nameMapping = new HashMap<>();
        AcademicPlugin ap = VrApp.getBean(AcademicPlugin.class);
        List<AcademicClass> allCls = ap.findAcademicDownHierarchyList(new AcademicClass[]{student.getLastClass1(), student.getLastClass2(), student.getLastClass3()}, null);
        for (AcademicClass ac : allCls) {
            String n2 = ac.getName().trim().toLowerCase();
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
                            if (nameMapping.containsKey(week.getName().trim().toLowerCase().trim())) {
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

    public CalendarWeek loadClassPlanning(String className) {
        String uniformClassName = className == null ? "" : className.toLowerCase().trim();
        if (StringUtils.isEmpty(uniformClassName)) {
            return null;
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
                FETXmlParser parser = new FETXmlParser(p, "Subgroup", "Formation Ingénieur");
                CalendarWeekParser week;
                try {
                    int counter = 0;
                    while ((week = parser.next()) != null) {
                        if (uniformClassName.equals(week.getName().trim().toLowerCase().trim())) {
                            counter++;
                            return week.parse("Class-" + counter);
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

        VFile[] emploisFiles = getEmploiFolder().listFiles(new VFileFilter() {

            @Override
            public boolean accept(VFile pathname) {
                return pathname.getName().toLowerCase().endsWith("_subgroups.xml");
            }
        });
        VFile p = emploisFiles.length > 0 ? emploisFiles[0] : null;
        if (p != null && p.exists()) {
            try {
                FETXmlParser parser = new FETXmlParser(p, "Subgroup", "Formation Ingénieur");
                CalendarWeekParser week;
                try {
                    int counter = 0;
                    while ((week = parser.next()) != null) {
                        String tn = week.getName().trim().toLowerCase();
                        all.add(new NamedId(tn, week.getName()));
                    }
                } finally {
                    parser.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(all);
    }



}
