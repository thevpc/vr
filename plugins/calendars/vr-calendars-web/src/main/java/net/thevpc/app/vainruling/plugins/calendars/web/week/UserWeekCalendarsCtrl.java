/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.calendars.web.week;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppConfig;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import net.thevpc.app.vainruling.plugins.calendars.service.CalendarsPluginSecurity;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.*;
import net.thevpc.app.vainruling.core.web.jsf.VrJsf;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.util.Convert;
import net.thevpc.common.util.IntegerParserConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.*;
import net.thevpc.app.vainruling.VrPage;
import net.thevpc.upa.UPA;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        //        title = "Tous les Emplois",
        url = "modules/calendars/user-week-calendars",
        menu = "/Calendars",
        securityKey = CalendarsPluginSecurity.RIGHT_CUSTOM_EDUCATION_USER_CALENDARS
)
public class UserWeekCalendarsCtrl extends AbstractWeekCalendarCtrl {

    @Autowired
    private CalendarsPlugin calendars;
    @Autowired
    private CorePlugin core;

    public UserWeekCalendarsCtrl() {
        super();
        model = new ModelExt();
    }

    public void onChangeDepartment() {
        onChangeUserType();
    }

    public void onChangeUserType() {
        getModel().getUsers().clear();
        int userTypeId = Convert.toInt(getModel().getUserTypeId(), IntegerParserConfig.LENIENT_F);
        int userDeptId = Convert.toInt(getModel().getUserDepartmentId(), IntegerParserConfig.LENIENT_F);
        int oldSelectedUser = Convert.toInt(getModel().getUserId(), IntegerParserConfig.LENIENT_F);
        boolean oldSelectedUserFound = false;
        final Set<Integer> userIds = new HashSet<>();
        if (userTypeId >= 0 && userDeptId >= 0) {
            for (AppUser user : userTypeId < 0 ? core.findUsers() : core.findUsersByTypeAndDepartment(userTypeId, userDeptId)) {
                if (user.isEnabled()) {
                    userIds.add(user.getId());
                }
            }
        }
//        userIds.clear();
        userIds.addAll(calendars.findUsersWithPublicWeekCalendars(userIds));

        List<AppUser> users = new ArrayList<>();
        UPA.getPersistenceUnit().invokePrivileged(() -> {
            for (Integer userId : userIds) {
                users.add(core.findUser(userId));
            }
        });
        Collections.sort(users, new Comparator<AppUser>() {
            @Override
            public int compare(AppUser o1, AppUser o2) {
                return StringUtils.nonNull((o1 == null ? "" : o1.getFullTitle())).compareTo(StringUtils.nonNull((o2 == null ? "" : o2.getFullTitle())));
            }
        });
        getModel().setUsers(VrJsf.toSelectItemList(users));
        for (AppUser user : users) {
            if (oldSelectedUser == user.getId()) {
                oldSelectedUserFound = true;
            }
        }

        //Set<Integer> findUsersWithPublicWeekCalendars()
        if (!oldSelectedUserFound) {
            getModel().setUserId(null);
        }
        onChangeUser();
    }

    public void onChangeUser() {
        int oldSelectedUser = Convert.toInt(getModel().getUserId(), IntegerParserConfig.LENIENT_F);
        getModel().setCalendar(calendars.findMergedUserPublicWeekCalendar(oldSelectedUser));
    }

    public int getPeriodId() {
        String p = "";//getModel().getSelectedPeriod();
        if (StringUtils.isBlank(p)) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            AppConfig appConfig = core.getCurrentConfig();
            if (appConfig != null) {
                AppPeriod mainPeriod = appConfig.getMainPeriod();
                if (mainPeriod != null) {
                    return mainPeriod.getId();
                }
            }
            return -1;
        }
        return Convert.toInt(p, IntegerParserConfig.LENIENT_F);
    }

    public void onRefresh() {
        onChangeUserType();
    }

    @VrOnPageLoad
    public void onRefresh(String cmd) {
        getModel().setUserTypeId(null);
        getModel().setUserTypes(VrJsf.toSelectItemList(core.findUserTypes()));
        getModel().setDepartments(VrJsf.toSelectItemList(core.findDepartments()));

        onChangeUserType();
    }

    public AppUser getCurrentUser() {
        String ii = getModel().getUserId();
        if (ii != null && ii.length() > 0) {
            AppUser tt = core.findUser(
                    Convert.toInt(ii, IntegerParserConfig.LENIENT_F)
            );
            if (tt != null) {
                return tt;
            }
        }
        return null;
//        AcademicPlugin a = VRApp.getBean(AcademicPlugin.class);
//        return a.getCurrentTeacher();
    }

    @Override
    public ModelExt getModel() {
        return (ModelExt) super.getModel();
    }

    public class ModelExt extends Model {

        String userId;
        String userTypeId;
        String userDepartmentId;
        List<SelectItem> users = new ArrayList<SelectItem>();
        List<SelectItem> userTypes = new ArrayList<SelectItem>();
        List<SelectItem> departments = new ArrayList<SelectItem>();

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

        public String getUserDepartmentId() {
            return userDepartmentId;
        }

        public void setUserDepartmentId(String userDepartmentId) {
            this.userDepartmentId = userDepartmentId;
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

        public List<SelectItem> getDepartments() {
            return departments;
        }

        public void setDepartments(List<SelectItem> departments) {
            this.departments = departments;
        }
    }
}
