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
import net.vpc.app.vainruling.plugins.calendars.service.model.PlanningData;
import net.vpc.app.vainruling.plugins.calendars.service.model.PlanningDay;
import net.vpc.app.vainruling.plugins.calendars.service.model.PlanningHour;
import net.vpc.common.strings.StringUtils;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Mon Emploi du temps",
        url = "modules/academic/planning/my-planning-list",
        menu = "/Education/Planning",
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

        getModel().setPlannings(new HashMap<String, PlanningData>());

        CalendarsPlugin calendarsPlugin = VrApp.getBean(CalendarsPlugin.class);
        AppUser user = UserSession.getCurrentUser();
        Map<String, PlanningData> planningsMap = getModel().getPlannings();
        planningsMap.clear();
        if (user != null) {
            for (PlanningData planningData : calendarsPlugin.loadUserPlannings(user.getId())) {
                String nn = planningData.getId();
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
                planningsMap.put(nnn, planningData);
            }
        }

        if (planningsMap.size() > 1) {
            PlanningData fusion = new PlanningData();
            fusion.setPlanningName("* Mon Emploi *");
            HashSet<String> visited = new HashSet<>();
            for (PlanningData pp : planningsMap.values()) {
                if (fusion.getDays() == null) {
                    fusion.setDays(new ArrayList<PlanningDay>());
                }
                for (PlanningDay day : pp.getDays()) {
                    PlanningDay day0 = null;
                    for (PlanningDay dd : fusion.getDays()) {
                        if (dd.getDayName().equals(day.getDayName())) {
                            day0 = dd;
                            break;
                        }
                    }
                    if (day0 == null) {
                        day0 = new PlanningDay();
                        day0.setDayName(day.getDayName());
                        day0.setHours(new ArrayList<PlanningHour>());
                        fusion.getDays().add(day0);
                    }
                    for (PlanningHour hour : day.getHours()) {
                        String ha = "A:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonNull(hour.getActivity());
                        String hr = "R:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonNull(hour.getRoom());
                        String hs = "S:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonNull(hour.getStudents());
                        String hj = "J:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonNull(hour.getSubject());

                        PlanningHour h0 = null;
                        for (PlanningHour dd : day0.getHours()) {
                            if (dd.getHour().equals(hour.getHour())) {
                                h0 = dd;
                                break;
                            }
                        }

                        if (h0 == null) {
                            h0 = new PlanningHour();
                            h0.setHour(hour.getHour());
                            h0.setActivity(hour.getActivity());
                            h0.setRoom(hour.getRoom());
                            h0.setStudents(hour.getStudents());
                            h0.setSubject(hour.getSubject());

                            day0.getHours().add(h0);
                            visited.add(ha);
                            visited.add(hr);
                            visited.add(hs);
                            visited.add(hj);
                        } else {
                            if (!visited.contains(ha) && !StringUtils.isEmpty(hour.getActivity())) {
                                h0.setActivity((StringUtils.isEmpty(h0.getActivity())) ? hour.getActivity() : (h0.getActivity() + " / " + hour.getActivity()));
                                visited.add(ha);
                            }
                            if (!visited.contains(hr) && !StringUtils.isEmpty(hour.getRoom())) {
                                h0.setRoom((StringUtils.isEmpty(h0.getRoom())) ? hour.getRoom() : (h0.getRoom() + " / " + hour.getRoom()));
                                visited.add(hr);
                            }
                            if (!visited.contains(hs) && !StringUtils.isEmpty(hour.getStudents())) {
                                h0.setStudents((StringUtils.isEmpty(h0.getStudents())) ? hour.getStudents() : (h0.getStudents() + " / " + hour.getStudents()));
                                visited.add(hs);
                            }
                            if (!visited.contains(hj) && !StringUtils.isEmpty(hour.getSubject())) {
                                h0.setSubject((StringUtils.isEmpty(h0.getSubject())) ? hour.getSubject() : (h0.getSubject() + " / " + hour.getSubject()));
                                visited.add(hj);
                            }
                        }
                    }
                }
            }
            planningsMap.put(fusion.getPlanningName(), fusion);
        }

        getModel().setGroups(new ArrayList<SelectItem>());
        for (String k : new TreeSet<String>(planningsMap.keySet())) {
            getModel().getGroups().add(new SelectItem(String.valueOf(k), k));
        }
        if (getModel().getGroupName() == null) {
            if (planningsMap.size() == 1) {
                for (String k : planningsMap.keySet()) {
                    getModel().setGroupName(k);
                    break;
                }
            } else {
                for (String k : planningsMap.keySet()) {
                    if ("* Mon Emploi *".equals(k)) {
                        getModel().setGroupName(k);
                        break;
                    }
                }
            }
        }

        PlanningData plannings = planningsMap.get(getModel().getGroupName());
        if (plannings == null) {
            updateModel(new ArrayList<PlanningDay>());
        } else {
            updateModel(plannings.getDays());
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

        String groupName;
        List<SelectItem> groups = new ArrayList<SelectItem>();
        Map<String, PlanningData> plannings = new HashMap<>();

        public List<SelectItem> getGroups() {
            return groups;
        }

        public void setGroups(List<SelectItem> groups) {
            this.groups = groups;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public Map<String, PlanningData> getPlannings() {
            return plannings;
        }

        public void setPlannings(Map<String, PlanningData> plannings) {
            this.plannings = plannings;
        }

    }
}
