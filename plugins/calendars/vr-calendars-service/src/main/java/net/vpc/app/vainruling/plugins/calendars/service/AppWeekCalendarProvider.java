package net.vpc.app.vainruling.plugins.calendars.service;


import net.vpc.app.vainruling.plugins.calendars.service.model.WeekCalendar;

import java.util.List;
import java.util.Set;

/**
 * Created by vpc on 9/15/16.
 */
public interface AppWeekCalendarProvider {
    /**
     * filter <code>users</code> to retain only users that do have effective calendars.
     * input set should not be updated
     * @param users to check against having calendars
     * @return subset of <code>users</code> that do have effective calendars
     */
    public Set<Integer> retainUsersWithPublicWeekCalendars(final Set<Integer> users);

    public List<WeekCalendar> findUserPublicWeekCalendars(int userId);

    public List<WeekCalendar> findUserPrivateWeekCalendars(int userId);

    public List<WeekCalendar> findWeekCalendars(String type, String key);
}
