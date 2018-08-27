/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.jsf.VrJsf;
import net.vpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import net.vpc.app.vainruling.plugins.calendars.service.CalendarsPluginSecurity;
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
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Tous les Emplois",
        url = "modules/calendars/user-calendars",
        menu = "/Calendars",
        securityKey = CalendarsPluginSecurity.RIGHT_CUSTOM_EDUCATION_USER_CALENDARS
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

    public void onChangeDepartment() {
        onChangeUserType();
    }

    public void onChangeUserType() {
        getModel().getUsers().clear();
        int userTypeId = Convert.toInt(getModel().getUserTypeId(), IntegerParserConfig.LENIENT_F);
        int userDeptId = Convert.toInt(getModel().getUserDepartmentId(), IntegerParserConfig.LENIENT_F);
        int oldSelectedUser = Convert.toInt(getModel().getUserId(), IntegerParserConfig.LENIENT_F);
        boolean oldSelectedUserFound = false;
        Set<Integer> userIds = new HashSet<>();
        if(userTypeId>=0 && userDeptId>=0) {
            for (AppUser user : userTypeId < 0 ? core.findUsers() : core.findUsersByTypeAndDepartment(userTypeId,userDeptId)) {
                if (user.isEnabled()) {
                    userIds.add(user.getId());
                }
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
                return StringUtils.nonNull((o1==null?"":o1.getFullTitle())).compareTo(StringUtils.nonNull((o2==null?"":o2.getFullTitle())));
            }
        });
        getModel().setUsers(VrJsf.toSelectItemList(users));
        for (AppUser user : users) {
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
        int oldSelectedUser = Convert.toInt(getModel().getUserId(), IntegerParserConfig.LENIENT_F);
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
            AppConfig appConfig = core.getCurrentConfig();
            if (appConfig != null) {
                AppPeriod mainPeriod = appConfig.getMainPeriod();
                if (mainPeriod != null) {
                    return mainPeriod.getId();
                }
            }
            return -1;
        }
        return Convert.toInt(p,IntegerParserConfig.LENIENT_F);
    }

    public void onRefresh() {
        onChangeUserType();
    }

    @OnPageLoad
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
                    Convert.toInt(ii,IntegerParserConfig.LENIENT_F)
            );
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
