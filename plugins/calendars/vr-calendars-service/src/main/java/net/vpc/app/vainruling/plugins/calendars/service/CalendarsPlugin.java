/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.service;

import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarWeek;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarDay;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarHour;
import net.vpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin()
public class CalendarsPlugin {

    @Autowired
    CorePlugin core;
    Map<String, VrCalendarProvider> providers;

    @Start
    public void onStart() {
        providers = VrApp.getContext().getBeansOfType(VrCalendarProvider.class);
        if (core == null) {
            core = CorePlugin.get();
        }
        core.createRight(CalendarsPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_PLANNING, "MyPlanning");
    }

    public CalendarWeek findMergedUserPublicCalendar(int userId) {
        return findMergedUserPublicCalendar(userId, null);
    }

    public CalendarWeek findMergedUserPublicCalendar(int userId, String newName) {
        if (StringUtils.isEmpty(newName)) {
            AppUser user = core.findUser(userId);
            newName = "noname";
            if (user != null) {
                newName = user.getFullName();
                if (StringUtils.isEmpty(newName)) {
                    newName = user.getLogin();
                    if (StringUtils.isEmpty(newName)) {
                        newName = "noname";
                    }
                }
            }
        }
        return mergeCalendars(findPlainUserPublicCalendars(userId), newName);
    }

    public List<CalendarWeek> findUserPublicCalendars(int userId, boolean merge) {
        List<CalendarWeek> all = findPlainUserPublicCalendars(userId);
        if (merge) {
            AppUser user = core.findUser(userId);
            String name = "noname";
            if (user != null) {
                 name = user.getFullName();
                if (StringUtils.isEmpty(name)) {
                    name = user.getLogin();
                    if (StringUtils.isEmpty(name)) {
                        name = "noname";
                    }
                }
            }
            if (all.size() > 1) {
                all.add(0, mergeCalendars(all, name + " (*)"));
            }
        }
        return all;
    }

    public CalendarWeek mergeCalendars(List<CalendarWeek> plannings, String newName) {
        Map<String, CalendarWeek> planningsMap = new HashMap<>();

        for (CalendarWeek calendarWeek : plannings) {
            String nn = calendarWeek.getId();
            String nnn = nn;
            if (planningsMap.containsKey(nnn)) {
                int index = 2;
                while (true) {
                    nnn = nn + " " + index;
                    if (!planningsMap.containsKey(nnn)) {
                        break;
                    }
                    index++;
                }
            }
            planningsMap.put(nnn, calendarWeek);
        }

        if (planningsMap.size() > 1) {
            CalendarWeek fusion = new CalendarWeek();
            fusion.setSourceName("");
            fusion.setPlanningName(newName);
            HashSet<String> visited = new HashSet<>();
            for (CalendarWeek pp : planningsMap.values()) {
                if (fusion.getDays() == null) {
                    fusion.setDays(new ArrayList<CalendarDay>());
                }
                for (CalendarDay day : pp.getDays()) {
                    CalendarDay day0 = null;
                    for (CalendarDay dd : fusion.getDays()) {
                        if (dd.getDayName().equals(day.getDayName())) {
                            day0 = dd;
                            break;
                        }
                    }
                    if (day0 == null) {
                        day0 = new CalendarDay();
                        day0.setDayName(day.getDayName());
                        day0.setHours(new ArrayList<CalendarHour>());
                        fusion.getDays().add(day0);
                    }
                    for (CalendarHour hour : day.getHours()) {
                        String ha = "A:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonNull(hour.getActivity());
                        String hr = "R:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonNull(hour.getRoom());
                        String hs = "S:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonNull(hour.getStudents());
                        String hj = "J:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonNull(hour.getSubject());
                        String hc = "C:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonNull(hour.getActor());

                        CalendarHour h0 = null;
                        for (CalendarHour dd : day0.getHours()) {
                            if (dd.getHour().equals(hour.getHour())) {
                                h0 = dd;
                                break;
                            }
                        }

                        if (h0 == null) {
                            h0 = new CalendarHour();
                            h0.setHour(hour.getHour());
                            h0.setActivity(hour.getActivity());
                            h0.setRoom(hour.getRoom());
                            h0.setStudents(hour.getStudents());
                            h0.setSubject(hour.getSubject());
                            h0.setActor(hour.getActor());

                            day0.getHours().add(h0);
                            visited.add(ha);
                            visited.add(hr);
                            visited.add(hs);
                            visited.add(hj);
                            visited.add(hc);
                        } else {
                            if (!visited.contains(ha) && !StringUtils.isEmpty(hour.getActivity())) {
                                h0.setActivity((StringUtils.isEmpty(h0.getActivity())) ? hour.getActivity() : (h0.getActivity() + " / " + hour.getActivity()));
                                visited.add(ha);
                            }
                            if (!visited.contains(hr) && !StringUtils.isEmpty(hour.getRoom())) {
                                h0.setRoom((StringUtils.isEmpty(h0.getRoom())) ? hour.getRoom() : (h0.getRoom() + " / " + hour.getRoom()));
                                visited.add(hr);
                            }
                            if (!visited.contains(hs) && !StringUtils.isEmpty(hour.getStudents())) {
                                h0.setStudents((StringUtils.isEmpty(h0.getStudents())) ? hour.getStudents() : (h0.getStudents() + " / " + hour.getStudents()));
                                visited.add(hs);
                            }
                            if (!visited.contains(hj) && !StringUtils.isEmpty(hour.getSubject())) {
                                h0.setSubject((StringUtils.isEmpty(h0.getSubject())) ? hour.getSubject() : (h0.getSubject() + " / " + hour.getSubject()));
                                visited.add(hj);
                            }
                            if (!visited.contains(hc) && !StringUtils.isEmpty(hour.getActor())) {
                                h0.setActor((StringUtils.isEmpty(h0.getActor())) ? hour.getActor() : (h0.getActor() + " / " + hour.getActor()));
                                visited.add(hj);
                            }
                        }
                    }
                }
            }
            return fusion;
        } else if (planningsMap.size() == 1) {
            for (CalendarWeek calendarWeek : planningsMap.values()) {
                return calendarWeek;
            }
        }
        return null;
    }

    public Set<Integer> findUsersWithPublicCalendars() {
        Set<Integer> all = new HashSet<>();
        Set<Integer> good = new HashSet<>();
        for (AppUser appUser : core.findUsers()) {
            all.add(appUser.getId());
        }
        for (Map.Entry<String, VrCalendarProvider> o : providers.entrySet()) {
            Set<Integer> ok = o.getValue().retainUsersWithPublicCalendars(all);
            good.addAll(ok);
            all.removeAll(ok);
        }
        return good;
    }

    public Set<Integer> findUsersWithPublicCalendars(Set<Integer> userIds) {
        Set<Integer> all = new HashSet<>();
        if (userIds != null) {
            all.addAll(userIds);
        }
        Set<Integer> good = new HashSet<>();
        for (Map.Entry<String, VrCalendarProvider> o : providers.entrySet()) {
            Set<Integer> ok = o.getValue().retainUsersWithPublicCalendars(all);
            good.addAll(ok);
            all.removeAll(ok);
        }
        return good;
    }

    private List<CalendarWeek> findPlainUserPublicCalendars(int userId) {
        List<CalendarWeek> all = new ArrayList<>();
        for (Map.Entry<String, VrCalendarProvider> o : providers.entrySet()) {
            List<CalendarWeek> found = o.getValue().findUserPublicCalendars(userId);
            if (found != null) {
                for (CalendarWeek calendarWeek : found) {
                    if (calendarWeek != null) {
                        all.add(calendarWeek);
                    }
                }
            }
        }
        return all;
    }

    public CalendarWeek parsePlanningDataXML(Node planningNode, String sourceName) {
//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
        if (planningNode.getNodeType() == Node.ELEMENT_NODE) {

            Element eElement = (Element) planningNode;
            String tn = eElement.getAttribute("name");
            CalendarWeek p2 = new CalendarWeek();
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
            return p2;
        }
        return null;
    }

    private CalendarDay parsePlanningDayXML(Node dayNode) {
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

    private CalendarHour parsePlanningHourXML(Node hourNode) {
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

//    private Set<String> splitOtherNames(String value) {
//        Set<String> all = new HashSet<>();
//        if (value != null) {
//            for (String s : value.split(",|;")) {
//                if (s.trim().length() > 0) {
//                    all.add(s.trim().toLowerCase());
//                }
//            }
//        }
//        return all;
//    }
    public List<CalendarWeek> findMyCalenders() {
        return loadCalendars("my-calendars", "");
    }

    public List<CalendarWeek> loadCalendars(String type, String key) {
        List<CalendarWeek> all = new ArrayList<>();
        for (Map.Entry<String, VrCalendarProvider> o : providers.entrySet()) {
            List<CalendarWeek> found = o.getValue().findCalendars(type, key);
            if (found != null) {
                for (CalendarWeek calendarWeek : found) {
                    if (calendarWeek != null) {
                        all.add(calendarWeek);
                    }
                }
            }
        }
        return all;
    }

}
