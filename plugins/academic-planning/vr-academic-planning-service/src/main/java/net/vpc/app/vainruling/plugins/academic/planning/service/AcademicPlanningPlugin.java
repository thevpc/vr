/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.planning.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningData;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningDay;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningHour;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.filesystem.service.FileSystemPlugin;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author vpc
 */
@AppPlugin(dependsOn = "academicPlugin", version = "1.4")
public class AcademicPlanningPlugin {

    @Autowired
    CorePlugin core;
    @Autowired
    FileSystemPlugin fileSystemPlugin;
    @Autowired
    AcademicPlugin academicPlugin;

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
        AcademicTeacher teacher = academicPlugin.findTeacher(teacherId);

        String teacherName = teacher == null ? "" : teacher.getContact().getFullName();
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

    private VFile getEmploiFolder() {
        return fileSystemPlugin.getProfileFileSystem("DirectorOfStudies").get("/Emplois");
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
        AcademicStudent student = academicPlugin.findStudent(studentId);
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

    public List<String> loadStudentPlanningListNames() {
        TreeSet<String> all = new TreeSet<>();

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
                        all.add(tn);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(all);
    }

}
