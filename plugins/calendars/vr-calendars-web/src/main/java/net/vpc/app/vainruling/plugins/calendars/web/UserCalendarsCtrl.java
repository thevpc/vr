/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarWeek;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarDay;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.IntegerParserConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Tous les Emplois",
        url = "modules/calendars/user-calendars",
        menu = "/Calendars",
        securityKey = "Custom.Education.UserCalendars"
)
public class UserCalendarsCtrl extends AbstractPlanningCtrl {
    @Autowired
    private CalendarsPlugin calendars;
    @Autowired
    private CorePlugin core;

    public UserCalendarsCtrl() {
        super();
        model = new ModelExt();
    }

    public void onChangeUserType() {
        getModel().getUsers().clear();
        int userTypeId = Convert.toInteger(getModel().getUserTypeId(), IntegerParserConfig.LENIENT_F);
        int oldSelectedUser = Convert.toInteger(getModel().getUserId(), IntegerParserConfig.LENIENT_F);
        boolean oldSelectedUserFound = false;
        Set<Integer> userIds = new HashSet<>();
        for (AppUser user : userTypeId < 0 ? core.findUsers() : core.findUsersByType(userTypeId)) {
            if (user.isEnabled() && user.getContact() != null) {
                userIds.add(user.getId());
            }
        }
        userIds = calendars.findUsersWithPublicCalendars(userIds);
        List<AppUser> users = new ArrayList<>();
        for (Integer userId : userIds) {
            users.add(core.findUser(userId));
        }
        Collections.sort(users, new Comparator<AppUser>() {
            @Override
            public int compare(AppUser o1, AppUser o2) {
                return StringUtils.nonNull(core.getUserFullTitle(o1)).compareTo(StringUtils.nonNull(core.getUserFullTitle(o2)));
            }
        });
        for (AppUser user : users) {
            getModel().getUsers().add(new SelectItem(user.getId(), user.getContact().getFullTitle()));
            if (oldSelectedUser == user.getId()) {
                oldSelectedUserFound = true;
            }
        }

        //Set<Integer> findUsersWithPublicCalendars()
        if (!oldSelectedUserFound) {
            getModel().setUserId(null);
        }
        onChangeUser();
    }

    public void onChangeUser() {
        int oldSelectedUser = Convert.toInteger(getModel().getUserId(), IntegerParserConfig.LENIENT_F);
        CalendarWeek plannings = calendars.findMergedUserPublicCalendar(oldSelectedUser);
        if (plannings == null) {
            updateModel(new ArrayList<CalendarDay>());
        } else {
            updateModel(plannings.getDays());
        }
    }

    public int getPeriodId() {
        String p = "";//getModel().getSelectedPeriod();
        if (StringUtils.isEmpty(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            AppConfig appConfig = core.findAppConfig();
            if (appConfig != null) {
                AppPeriod mainPeriod = appConfig.getMainPeriod();
                if (mainPeriod != null) {
                    return mainPeriod.getId();
                }
            }
            return -1;
        }
        return Integer.parseInt(p);
    }

    public void onRefresh() {
        onChangeUserType();
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        getModel().getUserTypes().clear();
        getModel().setUserTypeId(null);
        for (AppUserType userType : core.findUserTypes()) {
            getModel().getUserTypes().add(new SelectItem(userType.getId(), userType.getName()));
        }
        onChangeUserType();
    }

    public AppUser getCurrentUser() {
        String ii = getModel().getUserId();
        if (ii != null && ii.length() > 0) {
            AppUser tt = core.findUser(Integer.parseInt(ii));
            if (tt != null) {
                return tt;
            }
        }
        return null;
//        AcademicPlugin a = VRApp.getBean(AcademicPlugin.class);
//        return a.getCurrentTeacher();
    }

    public ModelExt getModel() {
        return (ModelExt) super.getModel();
    }

    public class ModelExt extends Model {

        String userId;
        String userTypeId;
        List<SelectItem> users = new ArrayList<SelectItem>();
        List<SelectItem> userTypes = new ArrayList<SelectItem>();

        public List<SelectItem> getUsers() {
            return users;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserTypeId() {
            return userTypeId;
        }

        public void setUserTypeId(String userTypeId) {
            this.userTypeId = userTypeId;
        }

        public void setUsers(List<SelectItem> users) {
            this.users = users;
        }

        public List<SelectItem> getUserTypes() {
            return userTypes;
        }

        public void setUserTypes(List<SelectItem> userTypes) {
            this.userTypes = userTypes;
        }
    }
}
