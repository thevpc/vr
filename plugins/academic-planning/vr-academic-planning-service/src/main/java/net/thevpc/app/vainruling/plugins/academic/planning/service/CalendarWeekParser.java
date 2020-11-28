package net.thevpc.app.vainruling.plugins.academic.planning.service;

import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.plugins.calendars.service.CalendarsUtils;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarDay;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarHour;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.thevpc.common.strings.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarActivity;

/**
 * Created by vpc on 10/12/16.
 */
public class CalendarWeekParser {

    private String name;
    private String sourceName;
    private Node nNode;

    public CalendarWeekParser(String name, String sourceName, Node nNode) {
        this.name = name;
        this.sourceName = sourceName;
        this.nNode = nNode;
    }

    public String getName() {
        return name;
    }

    public WeekCalendar parse(String id) {
        WeekCalendar dd = parsePlanningDataXML(nNode, sourceName);
        if (dd != null) {
            dd.setId(id);
            dd.setPlanningUniformName(VrUtils.normalizeName(name));
            return dd;
        }
        return null;
    }

    public static WeekCalendar parsePlanningDataXML(Node planningNode, String sourceName) {
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
                CalendarDay dd = parsePlanningDayXML(dayNode);
                if (dd != null) {
                    p2.getDays().add(dd);
                }
            }
            while (p2.getDays().size() < 6) {
                CalendarDay d = new CalendarDay();
                d.setDayName("Day " + p2.getDays().size());
                p2.getDays().add(d);
            }
            return CalendarsUtils.buildWeekCalendar(p2);
        }
        return null;
    }

    public static CalendarDay parsePlanningDayXML(Node dayNode) {
        if (dayNode.getNodeType() == Node.ELEMENT_NODE) {
            Element dayElement = (Element) dayNode;
            CalendarDay calendarDay = new CalendarDay();
            calendarDay.setDayName(dayElement.getAttribute("name"));
            List<CalendarHour> calendarHours = new ArrayList<>();
            NodeList hours = dayElement.getElementsByTagName("Hour");
            for (int hi = 0; hi < hours.getLength(); hi++) {
                Node hourNode = hours.item(hi);
                CalendarHour ph = parsePlanningHourXML(hourNode);
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

    public static CalendarHour parsePlanningHourXML(Node hourNode) {
        if (hourNode.getNodeType() == Node.ELEMENT_NODE) {
            Element hourElement = (Element) hourNode;
            CalendarHour ph = new CalendarHour();
            ph.setHour(hourElement.getAttribute("name"));
            CalendarActivity act = new CalendarActivity();
            ph.setActivities(new ArrayList<>(Arrays.asList(act)));
            NodeList childNodes = hourElement.getChildNodes();
            for (int ci = 0; ci < childNodes.getLength(); ci++) {
                Node cNode = childNodes.item(ci);
                if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cElement = (Element) cNode;
                    if ("Activity_Tag".equals(cElement.getNodeName())) {
                        act.setActivity(cElement.getAttribute("name"));
                    } else if ("Students".equals(cElement.getNodeName())) {
                        act.setStudents(cElement.getAttribute("name"));
                    } else if ("Teacher".equals(cElement.getNodeName())) {
                        act.setTeacher(cElement.getAttribute("name"));
                    } else if ("Subject".equals(cElement.getNodeName())) {
                        act.setSubject(cElement.getAttribute("name"));
                    } else if ("Room".equals(cElement.getNodeName())) {
                        act.setRoom(cElement.getAttribute("name"));
                    }
                }
            }
            String actor = "";
            if (!StringUtils.isBlank(act.getStudents())) {
                if (actor.length() > 0) {
                    actor += " / ";
                }
                actor += act.getStudents();
            }
            if (!StringUtils.isBlank(act.getTeacher())) {
                if (actor.length() > 0) {
                    actor += " / ";
                }
                actor += act.getTeacher();
            }
            act.setActor(actor);
            return (ph);
        }
        return null;
    }

}
