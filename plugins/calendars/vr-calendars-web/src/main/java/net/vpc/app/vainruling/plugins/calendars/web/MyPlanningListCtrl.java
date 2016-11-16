/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.calendars.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.calendars.service.CalendarsPlugin;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarWeek;
import net.vpc.app.vainruling.plugins.calendars.service.model.CalendarDay;
import net.vpc.common.strings.StringUtils;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Mon Emploi du temps",
        url = "modules/calendars/my-calendars-list",
        menu = "/Calendars",
        securityKey = "Custom.Education.MyPlanning"
)
public class MyPlanningListCtrl extends AbstractPlanningCtrl {

    public MyPlanningListCtrl() {
        super();
        model = new ModelExt();
    }

    public void onGroupChanged() {
        onRefresh();
    }

    public void onRefresh() {

        CalendarsPlugin calendarsPlugin = VrApp.getBean(CalendarsPlugin.class);
        AppUser user = UserSession.getCurrentUser();
        getModel().setPlannings(calendarsPlugin.findUserPublicCalendars(user==null?-1:user.getId(),true));
        getModel().setGroups(new ArrayList<SelectItem>());
        List<CalendarWeek> plannings = getModel().getPlannings();
        for (int i = 0; i < plannings.size(); i++) {
            CalendarWeek data = plannings.get(i);
            String planningName = data.getPlanningName();
            if (!StringUtils.isEmpty(data.getSourceName())) {
                planningName += " (" + data.getSourceName() + ")";
            }
            getModel().getGroups().add(new SelectItem(String.valueOf(i), planningName));
        }
        if (getModel().getSelectionIndex() == null) {
            if (plannings.size() >0) {
                getModel().setSelectionIndex(0);
            }
        }else if(getModel().getSelectionIndex() <0 || getModel().getSelectionIndex()>=getModel().getSelectionIndex()){
            if (plannings.size() >0) {
                getModel().setSelectionIndex(0);
            }else{
                getModel().setSelectionIndex(null);
            }
        }

        CalendarWeek planning = getModel().getSelectionIndex()==null?null: plannings.get(getModel().getSelectionIndex());
        if (planning == null) {
            updateModel(new ArrayList<CalendarDay>());
        } else {
            updateModel(planning.getDays());
        }
    }

    @OnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();
    }

    public ModelExt getModel() {
        return (ModelExt) super.getModel();
    }

    public class ModelExt extends Model {

        Integer selectionIndex;
        List<SelectItem> groups = new ArrayList<SelectItem>();
        List<CalendarWeek> plannings = new ArrayList<>();

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

        public List<CalendarWeek> getPlannings() {
            return plannings;
        }

        public void setPlannings(List<CalendarWeek> plannings) {
            this.plannings = plannings;
        }

    }
}
