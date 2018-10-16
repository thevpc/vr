/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.service;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarDay;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarHour;
import net.vpc.app.vainruling.plugins.calendars.service.model.WeekCalendar;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author vpc
 */
public class WeekCalendarFETParser {

    public int parseWeekCalendarFETXMLCount(VFile p) {
        int count = 0;
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
                        count++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public List<WeekCalendar> parseWeekCalendarFETXML(VFile p) {
        List<WeekCalendar> list = new ArrayList<>();
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
                        WeekCalendar d = WeekCalendarFETParser.this.parseWeekCalendarFETXML(nNode, "Mon Planning");
                        if (d != null) {
                            d.setId("Custom-" + (temp + 1));
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

    public WeekCalendar parseWeekCalendarFETXML(Node planningNode, String sourceName) {
//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
        if (planningNode.getNodeType() == Node.ELEMENT_NODE) {

            Element eElement = (Element) planningNode;
            String tn = eElement.getAttribute("name");
            WeekCalendar p2 = new WeekCalendar();
            p2.setSourceName(sourceName);
            p2.setPlanningName(tn.trim());
            p2.setDays(new ArrayList<CalendarDay>());
            NodeList days = eElement.getElementsByTagName("Day");
            for (int di = 0; di < days.getLength(); di++) {
                Node dayNode = days.item(di);
                CalendarDay dd = parseWeekCalendarDayFETXML(dayNode);
                if (dd != null) {
                    p2.getDays().add(dd);
                }
            }
            while (p2.getDays().size() < 6) {
                CalendarDay d = new CalendarDay();
                d.setDayName("Day " + p2.getDays().size());
                p2.getDays().add(d);
            }
            return p2;
        }
        return null;
    }

    private CalendarDay parseWeekCalendarDayFETXML(Node dayNode) {
        if (dayNode.getNodeType() == Node.ELEMENT_NODE) {
            Element dayElement = (Element) dayNode;
            CalendarDay calendarDay = new CalendarDay();
            calendarDay.setDayName(dayElement.getAttribute("name"));
            List<CalendarHour> calendarHours = new ArrayList<>();
            NodeList hours = dayElement.getElementsByTagName("Hour");
            for (int hi = 0; hi < hours.getLength(); hi++) {
                Node hourNode = hours.item(hi);
                CalendarHour ph = parseWeekCalendarHourFETXML(hourNode);
                if (ph != null) {
                    calendarHours.add(ph);
                }

            }
            calendarDay.setHours(calendarHours);

            if (calendarDay.getHours() == null || calendarDay.getHours().size() < 5) {
                List<CalendarHour> all = new ArrayList<>();
                if (calendarDay.getHours() != null) {
                    all.addAll((calendarDay.getHours()));
                }
                while (all.size() < 5) {
                    CalendarHour dd = new CalendarHour();
                    dd.setHour("H #" + all.size());
                    all.add(dd);
                }
                calendarDay.setHours(all);
            }

            return (calendarDay);
        }
        return null;
    }

    private CalendarHour parseWeekCalendarHourFETXML(Node hourNode) {
        if (hourNode.getNodeType() == Node.ELEMENT_NODE) {
            Element hourElement = (Element) hourNode;
            CalendarHour ph = new CalendarHour();
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

}
