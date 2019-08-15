package net.vpc.app.vainruling.plugins.calendars.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;

/**
 * Created by vpc on 9/15/16.
 */
@Service
public class VrDefaultWeekCalendarProvider implements AppWeekCalendarProvider {
    @Autowired
    CorePlugin core;
    @Autowired
    CalendarsPlugin calendarsPlugin;


    @Override
    public Set<Integer> retainUsersWithPublicWeekCalendars(Set<Integer> users) {
        Set<Integer> ret=new HashSet<>();
        for (Integer userId : users) {
            if(findUserCalendarsCount(userId,"/Config/public-plannings.xml")>0){
                ret.add(userId);
            }
        }
        return ret;
    }

    public List<WeekCalendar> findUserPrivateWeekCalendars(int userId) {
        return findUserCalendars(userId, "/Config/private-plannings.xml");
    }

    public List<WeekCalendar> findUserPublicWeekCalendars(int userId) {
        return findUserCalendars(userId, "/Config/public-plannings.xml");
    }

    public int findUserCalendarsCount(int userId, String path) {
        AppUser uuu = core.findUser(userId);
        int count=0;
        if (uuu == null) {
            return 0;
        }
        VFile p = core.getUserFolder(uuu.getLogin()).get(path);
        return new WeekCalendarFETParser().parseWeekCalendarFETXMLCount(p);
    }

    public List<WeekCalendar> findUserCalendars(int userId, String path) {
        AppUser uuu = core.findUser(userId);
        List<WeekCalendar> list = new ArrayList<>();
        if (uuu == null) {
            return list;
        }
//        String teacherName = uuu == null ? "" : uuu.resolveFullName();
        VFile p = core.getUserFolder(uuu.getLogin()).get(path);
        WeekCalendarFETParser pp=new WeekCalendarFETParser();
        return pp.parseWeekCalendarFETXML(p);
    }

    @Override
    public List<WeekCalendar> findWeekCalendars(String type, String key) {
        if ("my-calendars".equals(type)) {
            AppUser user = core.getCurrentUser();
            if (user != null) {
                return calendarsPlugin.findUserPublicWeekCalendars(user.getId(), true);
            }
        } else if ("user-calendars".equals(type)) {
            if (!StringUtils.isBlank(key)) {
                key = key.trim();
                if (Character.isDigit(key.charAt(0))) {
                    return calendarsPlugin.findUserPublicWeekCalendars(Convert.toInt(key,IntegerParserConfig.LENIENT_F), true);
                }
                AppUser user = core.findUser(key);
                if (user != null) {
                    return calendarsPlugin.findUserPublicWeekCalendars(user.getId(), true);
                }
            }
        }
        return Collections.EMPTY_LIST;
    }
}
