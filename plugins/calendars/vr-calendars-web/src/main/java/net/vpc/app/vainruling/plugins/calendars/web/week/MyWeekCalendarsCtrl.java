/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.web.week;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import net.vpc.app.vainruling.plugins.calendars.service.CalendarsPluginSecurity;
import net.vpc.app.vainruling.plugins.calendars.service.dto.WeekCalendar;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrPathItem;
import net.vpc.app.vainruling.VrOnPageLoad;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        //        title = "Mon Emploi du temps",
        url = "modules/calendars/my-week-calendars",
        menu = "/Calendars",
        securityKey = CalendarsPluginSecurity.RIGHT_CUSTOM_EDUCATION_MY_PLANNING
)
public class MyWeekCalendarsCtrl extends AbstractWeekCalendarCtrl {

    @Autowired
    CorePlugin core;
    @Autowired
    CalendarsPlugin calendarsPlugin;

    public MyWeekCalendarsCtrl() {
        super();
        model = new ModelExt();
    }

    public void onGroupChanged() {
        onRefresh();
    }

    public void onRefresh() {

        AppUser user = core.getCurrentUser();
        getModel().setPlannings(calendarsPlugin.findUserPublicWeekCalendars(user == null ? -1 : user.getId(), true));
        getModel().setGroups(new ArrayList<SelectItem>());
        List<WeekCalendar> plannings = getModel().getPlannings();
        for (int i = 0; i < plannings.size(); i++) {
            WeekCalendar data = plannings.get(i);
            String planningName = data.getPlanningName();
            if (!StringUtils.isBlank(data.getSourceName())) {
                planningName += " (" + data.getSourceName() + ")";
            }
            getModel().getGroups().add(FacesUtils.createSelectItem(String.valueOf(i), planningName));
        }
        if (getModel().getSelectionIndex() == null) {
            if (plannings.size() > 0) {
                getModel().setSelectionIndex(0);
            }
        } else if (getModel().getSelectionIndex() < 0 || getModel().getSelectionIndex() >= getModel().getSelectionIndex()) {
            if (plannings.size() > 0) {
                getModel().setSelectionIndex(0);
            } else {
                getModel().setSelectionIndex(null);
            }
        }
        getModel().setCalendar(getModel().getSelectionIndex() == null ? null : plannings.get(getModel().getSelectionIndex()));
    }

    @VrOnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();
    }

    public boolean isValidPlanning() {
        return getModel().getPlanning() != null && (model.getPlanning().size()) > 0;
    }

    public boolean isMissingPlanning() {
        return getModel().getSelectionIndex()!= null
                && (getModel().getPlanning() == null
                || (getModel().getPlanning().size()) == 0);
    }

    public String getSelectedLabel() {
        WeekCalendar w = getModel().getCalendar();
        return w==null?null:w.getPlanningName();
    }

    public ModelExt getModel() {
        return (ModelExt) super.getModel();
    }

    public class ModelExt extends Model {

        Integer selectionIndex;
        List<SelectItem> groups = new ArrayList<SelectItem>();
        List<WeekCalendar> plannings = new ArrayList<>();

        public List<SelectItem> getGroups() {
            return groups;
        }

        public void setGroups(List<SelectItem> groups) {
            this.groups = groups;
        }

        public Integer getSelectionIndex() {
            return selectionIndex;
        }

        public void setSelectionIndex(Integer selectionIndex) {
            this.selectionIndex = selectionIndex;
        }

        public List<WeekCalendar> getPlannings() {
            return plannings;
        }

        public void setPlannings(List<WeekCalendar> plannings) {
            this.plannings = plannings;
        }

    }
}
