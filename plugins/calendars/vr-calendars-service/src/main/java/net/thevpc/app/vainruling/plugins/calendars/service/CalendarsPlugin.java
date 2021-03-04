/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.calendars.service;

import net.thevpc.app.vainruling.VrPlugin;
import net.thevpc.app.vainruling.VrStart;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.plugins.calendars.model.AppCalendar;
import net.thevpc.app.vainruling.plugins.calendars.model.AppCalendarEvent;
import net.thevpc.app.vainruling.plugins.calendars.model.RuntimeAppCalendar;
import net.thevpc.app.vainruling.plugins.calendars.model.RuntimeAppCalendarEvent;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarDay;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarHour;
import net.thevpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import net.thevpc.common.time.MutableDate;
import net.thevpc.upa.Action;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;
import net.thevpc.upa.VoidAction;
import net.thevpc.app.vainruling.plugins.calendars.service.dto.CalendarActivity;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin()
public class CalendarsPlugin {

    public static CalendarsPlugin get() {
        return VrApp.getBean(CalendarsPlugin.class);
    }

    @Autowired
    CorePlugin core;
    Map<String, AppWeekCalendarProvider> weekCalendarProviders;
    Map<String, AppCalendarService> calendarEventServices;

    @VrStart
    private void onStart() {
        weekCalendarProviders = VrApp.getContext().getBeansOfType(AppWeekCalendarProvider.class);
        calendarEventServices = VrApp.getContext().getBeansOfType(AppCalendarService.class);
        core.addProfileRightName(CalendarsPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_PLANNING, "MyPlanning");
    }

    public WeekCalendar findMergedUserPublicWeekCalendar(int userId) {
        return CalendarsPlugin.this.findMergedUserPublicWeekCalendar(userId, null);
    }

    private String findUserName(int userId) {
        String newName = null;
        AppUser user = UPA.getPersistenceUnit().invokePrivileged(new Action<AppUser>() {
            @Override
            public AppUser run() {
                return core.findUser(userId);
            }
        });
        if (user != null) {
            newName = user.getFullName();
            if (StringUtils.isBlank(newName)) {
                newName = user.getLogin();
            }
        }
        if (StringUtils.isBlank(newName)) {
            newName = "noname";
        }
        return newName;
    }

    public WeekCalendar findMergedUserPublicWeekCalendar(int userId, String newName) {
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            String newName1 = newName;
            if (StringUtils.isBlank(newName1)) {
                newName1 = findUserName(userId);
            }
            return mergeWeekCalendars(findPlainUserPublicWeekCalendars(userId), newName1);
        });
    }

    public List<WeekCalendar> findUserPublicWeekCalendars(int userId, boolean merge) {
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            List<WeekCalendar> all = findPlainUserPublicWeekCalendars(userId);
            if (merge) {
                String name = findUserName(userId);
                if (all.size() > 1) {
                    all.add(0, mergeWeekCalendars(all, name + " (*)"));
                }
            }
            return all;
        });
    }

    public WeekCalendar mergeWeekCalendars(List<WeekCalendar> plannings, String newName) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(newName)) {
            int max = 0;
            List<String> names = new ArrayList<>();
            for (int i = 0; i < plannings.size(); i++) {
                String s = VrUtils.normalizeName(plannings.get(i).getPlanningName());
                names.add(s);
                int len = s.length();
                if (max < len) {
                    max = len;
                }
            }
            String s0 = StringUtils.trim(plannings.get(0).getPlanningName());
            String s0n = VrUtils.normalizeName(s0);

            for (int i = 0; i < max; i++) {
                boolean eq = true;
                for (int j = 1; j < plannings.size(); j++) {
                    String n = VrUtils.normalizeName(plannings.get(j).getPlanningName());
                    if (i < n.length()
                            && i < s0n.length()
                            && n.charAt(i) == s0n.charAt(i)) {
                        //ok
                    } else {
                        eq = false;
                    }
                }
                if (eq) {
                    sb.append(s0.charAt(i));
                } else {
                    break;
                }
            }
            if (sb.length() > 0 && (sb.charAt(sb.length() - 1) == '-' || sb.charAt(sb.length() - 1) == '.')) {
                sb.deleteCharAt(sb.length() - 1);
            }
            newName = sb.toString();
        }
        String newNameNew = newName;

        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            if (plannings.size() == 1) {
                return plannings.get(0);
            }
            Map<String, WeekCalendar> planningsMap = new HashMap<>();

            for (WeekCalendar calendarWeek : plannings) {
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
                WeekCalendar fusion = new WeekCalendar();
                fusion.setId(VrUtils.normalizeName(newNameNew));
                fusion.setSourceName("");
                fusion.setPlanningName(newNameNew);
                fusion.setPlanningUniformName(VrUtils.normalizeName(newNameNew));
                for (WeekCalendar pp : planningsMap.values()) {
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
                                for (CalendarActivity act2 : hour.getActivities()) {
                                    if (!CalendarsUtils.isBlank(act2)) {
                                        h0.getActivities().add(new CalendarActivity(act2));
                                    }
                                }
                                day0.getHours().add(h0);
                            } else {
                                for (CalendarActivity act2 : hour.getActivities()) {
                                    if (!h0.getActivities().contains(act2)) {
                                        h0.getActivities().add(new CalendarActivity(act2));
                                    }
                                }
                            }
                        }
                    }
                }
                return CalendarsUtils.buildWeekCalendar(fusion);
            } else if (planningsMap.size() == 1) {
                for (WeekCalendar calendarWeek : planningsMap.values()) {
                    return calendarWeek;
                }
            }
            return null;
        });
    }

    public Set<Integer> findUsersWithPublicWeekCalendars() {
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            Set<Integer> all = new HashSet<>();
            Set<Integer> good = new HashSet<>();
            List<AppUser> users = UPA.getPersistenceUnit().invokePrivileged(new Action<List<AppUser>>() {
                @Override
                public List<AppUser> run() {
                    return core.findUsers();
                }
            });
            for (AppUser appUser : users) {
                all.add(appUser.getId());
            }
            for (Map.Entry<String, AppWeekCalendarProvider> o : weekCalendarProviders.entrySet()) {
                Set<Integer> ok = o.getValue().retainUsersWithPublicWeekCalendars(all);
                good.addAll(ok);
                all.removeAll(ok);
            }
            return good;
        });
    }

    public Set<Integer> findUsersWithPublicWeekCalendars(Set<Integer> userIds) {
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            Set<Integer> all = new HashSet<>();
            if (userIds != null) {
                all.addAll(userIds);
            }
            Set<Integer> good = new HashSet<>();
            for (Map.Entry<String, AppWeekCalendarProvider> o : weekCalendarProviders.entrySet()) {
                Set<Integer> ok = o.getValue().retainUsersWithPublicWeekCalendars(all);
                good.addAll(ok);
                all.removeAll(ok);
            }
            return good;
        });
    }

    private List<WeekCalendar> findPlainUserPublicWeekCalendars(int userId) {
        return UPA.getPersistenceUnit().invokePrivileged(() -> {
            List<WeekCalendar> all = new ArrayList<>();
            for (Map.Entry<String, AppWeekCalendarProvider> o : weekCalendarProviders.entrySet()) {
                List<WeekCalendar> found = o.getValue().findUserPublicWeekCalendars(userId);
                if (found != null) {
                    for (WeekCalendar calendarWeek : found) {
                        if (calendarWeek != null) {
                            all.add(calendarWeek);
                        }
                    }
                }
            }
            return all;
        });
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
    public List<WeekCalendar> findMyWeekCalenders() {
        return loadWeekCalendars("my-calendars", "");
    }

    public List<WeekCalendar> loadWeekCalendars(String type, String key) {
        List<WeekCalendar> all = new ArrayList<>();
        for (Map.Entry<String, AppWeekCalendarProvider> o : weekCalendarProviders.entrySet()) {
            List<WeekCalendar> found = o.getValue().findWeekCalendars(type, key);
            if (found != null) {
                for (WeekCalendar calendarWeek : found) {
                    if (calendarWeek != null) {
                        all.add(calendarWeek);
                    }
                }
            }
        }
        return all;
    }

    public void saveEventCalendarEvent(AppCalendarEvent calendarEvent) {
        if (calendarEvent.getStartDate().equals(calendarEvent.getEndDate())) {
            throw new IllegalArgumentException("Invalid Event Date");
        }
        if (calendarEvent.getStartDate().after(calendarEvent.getEndDate())) {
            throw new IllegalArgumentException("Invalid Event Date");
        }
        if (calendarEvent.getCalendar() == null) {
            calendarEvent.setCalendar(findMyPrivateEventCalendar());
        }

        if (StringUtils.isBlank(calendarEvent.getTitle())) {
            calendarEvent.setTitle("TODO");
        }
        if (calendarEvent.getOwner() == null) {
            calendarEvent.setOwner(core.getCurrentUser());
        }
        if (calendarEvent.getStartDate() == null || calendarEvent.getEndDate() == null) {
            throw new IllegalArgumentException("Invalid Event Date");
        }
        core.save(calendarEvent);
    }

    public void removeEventCalendarEvent(int eventId) {
        AppCalendarEvent calendarEvent = UPA.getPersistenceUnit().findById(AppCalendarEvent.class, eventId);
        if (calendarEvent != null
                && (core.isCurrentSessionAdmin()
                || core.getCurrentUser().equals(calendarEvent.getOwner()))) {
            core.remove(AppCalendarEvent.class, calendarEvent.getId());
        }
    }

    public AppCalendar findEventCalendarByCode(String code) {
        return (AppCalendar) UPA.getPersistenceUnit().findByField(AppCalendar.class, "code", code);
    }

    public AppCalendar findMyDefaultEditEventCalendar() {
        AppUser me = core.getCurrentUser();
        String c = (String) core.getAppPropertyValue("vr-calendars.DefaultEditCalendar", me.getLogin());
        if (!StringUtils.isBlank(c)) {
            AppCalendar ca = findEventCalendarByCode(c);
            if (ca != null && isEventCalendarWriteAllowed(ca)) {
                return ca;
                //log error
            }
        }
        return findMyPrivateEventCalendar();
    }

    public AppCalendar findMyPrivateEventCalendar() {
        AppUser me = core.getCurrentUser();
        String c = (String) core.getAppPropertyValue("vr-calendars.DefaultCalendar", me.getLogin());
        if (!StringUtils.isBlank(c)) {
            AppCalendar ca = findEventCalendarByCode(c);
            if (ca != null) {
                return ca;
                //log error
            }
        }
        String cc = me.getLogin() + ".calendar";
        AppCalendar ca = findEventCalendarByCode(cc);
        if (ca == null) {
            ca = new AppCalendar();
            ca.setCode(cc);
            ca.setName("Calendrier de " + me.getFullName());
            ca.setOwner(me);
            ca.setReadUserFilter(me);
            AppCalendar ca2 = ca;
            UPA.getContext().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    UPA.getPersistenceUnit().persist(ca2);
                }

            });
        }
        return ca;
    }

    public boolean isEventCalendarWriteAllowed(AppCalendar i) {
        if (i == null) {
            return false;
        }
        AppUser cu = core.getCurrentUser();
        int userId = cu == null ? -1 : cu.getId();
        if (i.getOwner() != null && i.getOwner().getId() == userId) {
            return true;
        }
        String s = i.getWriteProfileFilter();
        if (!StringUtils.isBlank(s) && core.isCurrentSessionMatchesProfileFilter(s)) {
            return true;
        }
        return false;
    }

    public boolean isEventCalendarReadAllowed(AppCalendar i) {
        if (isEventCalendarWriteAllowed(i)) {
            return true;
        }
        if (i == null) {
            return false;
        }
        AppUser cu = core.getCurrentUser();
        int userId = cu == null ? -1 : cu.getId();
        int userTypeId = (cu == null || cu.getType() == null) ? -1 : cu.getType().getId();
        if (i.getOwner() != null && i.getOwner().getId() == userId) {
            return true;
        }
        if (i.getReadUserFilter() != null) {
            return i.getReadUserFilter().getId() == userId;
        }
        if (i.getReadUserTypeFilter() != null && i.getReadUserTypeFilter().getId() == userTypeId) {
            return true;
        }

        String s = i.getReadProfileFilter();
        if (StringUtils.isBlank(s) || core.isCurrentSessionMatchesProfileFilter(s)) {
            return true;
        }
        s = i.getWriteProfileFilter();
        if (!StringUtils.isBlank(s) && core.isCurrentSessionMatchesProfileFilter(s)) {
            return true;
        }
        return false;
    }

    public List<AppCalendarEvent> findMyPrivateEventCalendarEvents() {
        return UPA.getPersistenceUnit().createQuery("select i from AppCalendarEvent i where i.calendarId=:calendarId").setParameter("calendarId", findMyPrivateEventCalendar().getId())
                .getResultList();
    }

    public int findMyTodaysEventCalendarEventsCount(String calendar) {
        return findMyEventCalendarEventsCount(calendar, new MutableDate().setTime(0, 0, 0).getDateTime(), new MutableDate().setTime(0, 0, 0).addDaysOfYear(1).getDateTime());
    }

    public List<AppCalendarEvent> findMyTodaysEventCalendarEvents(String calendar) {
        return findMyEventCalendarEvents(calendar, new MutableDate().setTime(0, 0, 0).getDateTime(), new MutableDate().setTime(0, 0, 0).addDaysOfYear(1).getDateTime());
    }

    public int findMyEventCalendarEventsCount(String calendar) {
        return findMyEventCalendarEventsCount(calendar, new MutableDate().addMonths(-12).getDateTime(), new MutableDate().addMonths(+12).getDateTime());
    }

    public List<AppCalendarEvent> findMyEventCalendarEvents(String calendar) {
        return findMyEventCalendarEvents(calendar, new MutableDate().addMonths(-12).getDateTime(), new MutableDate().addMonths(+12).getDateTime());
    }

    /**
     * find all events in the window
     * [referenceDate-windowSizeInDays,referenceDate+windowSizeInDays]
     *
     * @param calendar
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<AppCalendarEvent> findMyEventCalendarEvents(String calendar, Date fromDate, Date toDate) {
        List<AppCalendar> a = findMyEventCalendars();
        List<AppCalendarEvent> all = new ArrayList<>();
        int v = -99;
        if (StringUtils.isBlank(calendar)) {
            calendar = null;
        }
        for (AppCalendar cal : a) {
            if (calendar == null || cal.getCode().equals(calendar)) {
                if (cal.getClass().equals(AppCalendar.class)) {
                    AppCalendar aa = (AppCalendar) cal;
                    List<AppCalendarEvent> evts = UPA.getPersistenceUnit().createQuery(
                            "select i from AppCalendarEvent i where "
                            + " i.calendarId=:calendarId "
                            + " and ("
                            + "    (i.startDate>=:startDate and i.startDate<:endDate) "
                            + " or (i.endDate>=:startDate and i.endDate<:endDate) "
                            + " or (i.startDate<=:startDate and i.endDate>=:endDate) "
                            + " )"
                    )
                            .setParameter("calendarId", aa.getId())
                            .setParameter("startDate", fromDate)
                            .setParameter("endDate", toDate)
                            .getResultList();
                    for (AppCalendarEvent evt : evts) {
                        if (evt.isEditable() && evt.getOwner() != null && evt.getOwner().getId() != core.getCurrentUserId()) {
                            evt.setEditable(false);
                        }
                        all.add(evt);
                    }

                } else if (cal.getClass().equals(RuntimeAppCalendar.class)) {
                    RuntimeAppCalendar r = (RuntimeAppCalendar) cal;
                    AppCalendarService y = calendarEventServices.get(r.getServiceName());
                    if (y != null) {
                        for (RuntimeAppCalendarEvent e : y.getMyEvents(calendar, fromDate, toDate)) {
                            v--;
                            e.setId(v);
                            e.setEditable(false);
                            all.add(e);
                        }
                    }
                }
            }
        }
        return all;
    }

    /**
     * find all events in the window
     * [referenceDate-windowSizeInDays,referenceDate+windowSizeInDays]
     *
     * @param calendar
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<AppCalendarEvent> findPublicEventCalendarEvents(Date fromDate, Date toDate) {
        List<AppCalendar> a = findPublicEventCalendars(true, true);
        List<AppCalendarEvent> all = new ArrayList<>();
        int v = -99;
        for (AppCalendar cal : a) {
            if (cal.getClass().equals(AppCalendar.class)) {
                AppCalendar aa = (AppCalendar) cal;
                List<AppCalendarEvent> evts = UPA.getPersistenceUnit().createQuery(
                        "select i from AppCalendarEvent i where "
                        + " i.calendarId=:calendarId "
                        + " and ("
                        + "    (i.startDate>=:startDate and i.startDate<:endDate) "
                        + " or (i.endDate>=:startDate and i.endDate<:endDate) "
                        + " or (i.startDate<=:startDate and i.endDate>=:endDate) "
                        + " )"
                )
                        .setParameter("calendarId", aa.getId())
                        .setParameter("startDate", fromDate)
                        .setParameter("endDate", toDate)
                        .getResultList();
                for (AppCalendarEvent evt : evts) {
                    if (evt.isEditable() && evt.getOwner() != null && evt.getOwner().getId() != core.getCurrentUserId()) {
                        evt.setEditable(false);
                    }
                    all.add(evt);
                }

            } else if (cal.getClass().equals(RuntimeAppCalendar.class)) {
                RuntimeAppCalendar r = (RuntimeAppCalendar) cal;
                AppCalendarService y = calendarEventServices.get(r.getServiceName());
                if (y != null) {
                    for (RuntimeAppCalendarEvent e : y.getPublicEvents(cal.getCode(), fromDate, toDate)) {
                        v--;
                        e.setId(v);
                        e.setEditable(false);
                        all.add(e);
                    }
                }
            }

        }
        return all;
    }

    public int findMyEventCalendarEventsCount(String calendar, Date fromDate, Date toDate) {
        List<AppCalendar> a = findMyEventCalendars();
        if (StringUtils.isBlank(calendar)) {
            calendar = null;
        }
        int count = 0;
        for (AppCalendar cal : a) {
            if (calendar == null || cal.getCode().equals(calendar)) {
                if (cal instanceof AppCalendar) {
                    AppCalendar aa = (AppCalendar) cal;
                    count += UPA.getPersistenceUnit().createQuery(
                            "select count(i.id) from AppCalendarEvent i where "
                            + " i.calendarId=:calendarId "
                            + " and ("
                            + "    (i.startDate>=:startDate and i.startDate<:endDate) "
                            + " or (i.endDate>=:startDate and i.endDate<:endDate) "
                            + " or (i.startDate<=:startDate and i.endDate>=:endDate) "
                            + " )"
                    )
                            .setParameter("calendarId", aa.getId())
                            .setParameter("startDate", fromDate)
                            .setParameter("endDate", toDate)
                            .getInteger();
                } else if (cal instanceof RuntimeAppCalendar) {
                    RuntimeAppCalendar r = (RuntimeAppCalendar) cal;
                    AppCalendarService y = calendarEventServices.get(r.getServiceName());
                    if (y != null) {
                        count += y.getMyEventsCount(calendar, fromDate, toDate);
                    }
                }
            }
        }
        return count;
    }

    public List<AppCalendar> findMyWritableEventCalendars() {
        List<AppCalendar> all = new ArrayList<>();
        for (AppCalendar ii : findMyEventCalendars(true, false)) {
            if (isEventCalendarWriteAllowed(ii)) {
                all.add(ii);
            }
        }
        return all;
    }

    public AppCalendar findMyEventCalendar(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        AppCalendar c = UPA.getPersistenceUnit().findByField("AppCalendar", "code", name);
        if (c != null) {
            if (!isEventCalendarReadAllowed(c)) {
                return null;
            }
        }
        for (AppCalendarService srv : calendarEventServices.values()) {
            RuntimeAppCalendar cls = srv.getMyCalendar(name);
            if (cls != null) {
                return cls;
            }
        }
        return c;
    }

    public List<AppCalendar> findMyEventCalendars() {
        return findMyEventCalendars(true, true);
    }

    public List<AppCalendar> findPublicEventCalendars(boolean includePersistent, boolean includeRuntime) {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<List<AppCalendar>>() {
            @Override
            public List<AppCalendar> run() {
                Map<String, AppCalendar> all = new HashMap<>();

                if (includePersistent) {
                    PersistenceUnit pu = UPA.getPersistenceUnit();
                    List<AppCalendar> allPersistentValendars = pu.createQuery("select i from AppCalendar i where i.publicCalendar=true").getResultList();
                    AppCalendar dc = findMyPrivateEventCalendar();
                    all.put(dc.getCode(), dc);
                    for (AppCalendar c : allPersistentValendars) {
                        if (c.getId() != dc.getId()) {
                            if (isEventCalendarReadAllowed(c)) {
                                if (!StringUtils.isBlank(c.getCode())) {
                                    if (!all.containsKey(c.getCode())) {
                                        all.put(c.getCode(), c);
                                    }
                                }
                            }
                        }
                    }
                }
                if (includeRuntime) {
                    for (AppCalendarService calendarEventSerivce : calendarEventServices.values()) {
                        List<RuntimeAppCalendar> cls = calendarEventSerivce.getPublicCalendars();
                        if (cls != null) {
                            for (RuntimeAppCalendar cl : cls) {
                                if (cl != null && !StringUtils.isBlank(cl.getCode()) && !all.containsKey(cl.getCode())) {
                                    all.put(cl.getCode(), cl);
                                }
                            }
                        }
                    }
                }
                return new ArrayList<>(all.values());
            }
        });
    }

    public List<AppCalendar> findMyEventCalendars(boolean includePersistent, boolean includeRuntime) {
        Map<String, AppCalendar> all = new HashMap<>();

        if (includePersistent) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            AppUser cu = core.getCurrentUser();
            int userId = cu == null ? -1 : cu.getId();
            int userTypeId = (cu == null || cu.getType() == null) ? -1 : cu.getType().getId();
            List<AppCalendar> allPersistentValendars = pu.createQuery("select i from AppCalendar i where "
                    + "  i.publicCalendar=true "
                    + " or ( "
                    + " (i.readUserFilterId=null or i.readUserFilterId=:userId)"
                    + " and (i.readUserTypeFilterId=null or i.readUserTypeFilterId=:userTypeId)"
                    + " ) or i.ownerId=:userId"
            )
                    .setParameter("userId", userId)
                    .setParameter("userTypeId", userTypeId)
                    .getResultList();
            AppCalendar dc = findMyPrivateEventCalendar();
            all.put(dc.getCode(), dc);
            for (AppCalendar c : allPersistentValendars) {
                if (c.getId() != dc.getId()) {
                    if (isEventCalendarReadAllowed(c)) {
                        if (!StringUtils.isBlank(c.getCode())) {
                            if (!all.containsKey(c.getCode())) {
                                all.put(c.getCode(), c);
                            }
                        }
                    }
                }
            }
        }
        if (includeRuntime) {

            for (AppCalendarService calendarEventSerivce : calendarEventServices.values()) {
                List<RuntimeAppCalendar> cls = calendarEventSerivce.getMyCalendars();
                if (cls != null) {
                    for (RuntimeAppCalendar cl : cls) {
                        if (cl != null && !StringUtils.isBlank(cl.getCode()) && !all.containsKey(cl.getCode())) {
                            all.put(cl.getCode(), cl);
                        }
                    }
                }
            }
        }
        return new ArrayList<>(all.values());
    }

//    public List<AppEvent> findAllEventsByCurrentMonth() {
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.HOUR_OF_DAY, 00);
//        c.set(Calendar.MINUTE, 00);
//        c.set(Calendar.SECOND, 00);
//
//        c.set(Calendar.DAY_OF_MONTH, 1);
//        Date d1 = c.getTime();
//
//        c.add(Calendar.MONTH, 1);
//        Date d2 = c.getTime();
//
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select e from AppEvent e where e.beginDate >=:d1 and e.beginDate < :d2 ")
//                .setParameter("d1", d1)
//                .setParameter("d2", d2)
//                .getResultList();
//    }
}
