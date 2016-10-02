package net.vpc.app.vainruling.plugins.calendars.service;


import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarWeek;

import java.util.List;
import java.util.Set;

/**
 * Created by vpc on 9/15/16.
 */
public interface VrCalendarProvider {
    /**
     * filter <code>users</code> to retain only users that do have effective calendars.
     * input set should not be updated
     * @param users to check against having calendars
     * @return subset of <code>users</code> that do have effective calendars
     */
    public Set<Integer> retainUsersWithPublicCalendars(final Set<Integer> users);

    public List<CalendarWeek> findUserPublicCalendars(int userId);

    public List<CalendarWeek> findUserPrivateCalendars(int userId);

    public List<CalendarWeek> findCalendars(String type, String key);
}
