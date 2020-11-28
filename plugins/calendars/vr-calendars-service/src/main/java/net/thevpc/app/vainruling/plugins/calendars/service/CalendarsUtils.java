/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.calendars.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarActivity;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarDay;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarHour;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.thevpc.common.strings.StringUtils;

/**
 *
 * @author vpc
 */
public class CalendarsUtils {

    public static WeekCalendar buildWeekCalendar(WeekCalendar calendar) {
        List<CalendarDay> days = calendar.getDays();
        if(days==null){
            days=new ArrayList<>();
            calendar.setDays(days);
        }
        Set<String> subjectsSet = new HashSet<>();
        Set<String> studentsSet = new HashSet<>();
        for (CalendarDay p : days) {
            List<CalendarHour> hours = p.getHours();
            if(hours==null){
                hours=new ArrayList<>();
                p.setHours(hours);
            }
            for (CalendarHour h : hours) {
                List<CalendarActivity> activities = h.getActivities();
                if (activities == null) {
                    h.setActivities(activities=new ArrayList<>());
                }
                for (CalendarActivity activity : activities) {
                    if (!isBlank(activity)) {
                        if (!StringUtils.isBlank(activity.getSubject())) {
                            subjectsSet.add(uniformSubjectDescriminator(activity.getSubject().trim()));
                        }
                        if (!StringUtils.isBlank(activity.getStudents())) {
                            studentsSet.add(uniformStudentsDescriminator(activity.getStudents()));
                        }
                    }
                }
                if (activities.isEmpty()) {
                    activities.add(new CalendarActivity());
                }
            }
        }
        Map<String, Integer> subjectsMap = new HashMap<>();
        int index = 0;
        for (String c : subjectsSet) {
            subjectsMap.put(c, index);
            index++;
        }
        Map<String, Integer> studentsMap = new HashMap<>();
        index = 0;
        for (String c : studentsSet) {
            studentsMap.put(c, index);
            index++;
        }
        for (CalendarDay p : days) {
            for (CalendarHour h : p.getHours()) {
                for (CalendarActivity activity : h.getActivities()) {
                    if (!StringUtils.isBlank(activity.getSubject())) {
                        String s = uniformSubjectDescriminator(activity.getSubject().trim());
                        Integer i = subjectsMap.get(s);
                        if (i == null) {
                            i = -1;
                        }
                        activity.setSubjectIndex(i);
                    }else{
                        activity.setSubjectIndex(-1);
                    }
                    if (!StringUtils.isBlank(activity.getStudents())) {
                        String s = uniformStudentsDescriminator(activity.getStudents().trim());
                        Integer i = studentsMap.get(s);
                        if (i == null) {
                            i = -1;
                        }
                        activity.setStudentsIndex(i);
                    }else{
                        activity.setStudentsIndex(-1);
                    }
                }
            }
        }
        return calendar;
    }

    public static String parentStudentsDescriminator(String s) {
        s = s.trim();
        final Pattern p = Pattern.compile("(?<A>[^.0-9]+[0-9]+[.-][0-9]+)[.-][0-9]+");
        Matcher m = p.matcher(s);
        if (m.find()) {
            return m.group("A");
        }
        return s;
    }

    public static String uniformStudentsDescriminator(String s) {
        s = VrUtils.normalizeName(s).trim();
        final Pattern p = Pattern.compile("(?<A>[^.0-9]+[0-9]+[.-][0-9]+)[.-][0-9]+");
        Matcher m = p.matcher(s);
        if (m.find()) {
            return m.group("A");
        }
        return s;
    }

    public static String uniformSubjectDescriminator(String s) {
        s = VrUtils.normalizeName(s).trim();
        if (s.endsWith("(1/15)")) {
            s = s.substring(0, s.length() - 6).trim();
        }
        if (s.endsWith("(tp)")) {
            s = s.substring(0, s.length() - 4).trim();
        }
//        final Pattern p = Pattern.compile("(?<A>[^.0-9]+[0-9]+[.-][0-9]+)[.-][0-9]+");
//        Matcher m=p.matcher(s);
//        if(m.find()){
//            return m.group("A");
//        }
        return s;
    }

    public static boolean isBlank(CalendarActivity a) {
        if (a == null) {
            return true;
        }
        if (!StringUtils.isBlank(a.getActivity())) {
            return false;
        }
        if (!StringUtils.isBlank(a.getActor())) {
            return false;
        }
        if (!StringUtils.isBlank(a.getRoom())) {
            return false;
        }
        if (!StringUtils.isBlank(a.getStudents())) {
            return false;
        }
        if (!StringUtils.isBlank(a.getSubject())) {
            return false;
        }
        if (!StringUtils.isBlank(a.getTeacher())) {
            return false;
        }
        return true;
    }
}
