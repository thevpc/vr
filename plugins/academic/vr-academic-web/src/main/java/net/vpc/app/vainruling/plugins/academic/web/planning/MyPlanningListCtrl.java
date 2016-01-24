/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningData;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningDay;
import net.vpc.app.vainruling.plugins.academic.service.model.PlanningHour;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.common.strings.StringUtils;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Mon Emploi du temps",
        url = "modules/academic/myplanninglist",
        menu = "/Education/Planning",
        securityKey = "Custom.Education.MyPlanning"
)
@ManagedBean
public class MyPlanningListCtrl extends AbstractPlanningCtrl {

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

    public MyPlanningListCtrl() {
        super();
        model = new ModelExt();
    }

    public void onGroupChanged() {
        onRefresh();
    }

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);

        getModel().setPlannings(new HashMap<String, PlanningData>());

        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        PlanningData plannings0 = null;
        UserSession sm = VrApp.getBean(UserSession.class);
        AppUser user = (sm == null) ? null : sm.getUser();
        if (user != null) {
            List<PlanningData> all = a.loadUserPlannings(user.getId());
            if (all != null) {
                for (PlanningData pp : all) {
                    String nn = pp.getPlanningName();
                    String nnn = nn;
                    if (getModel().getPlannings().containsKey(nnn)) {
                        int index = 2;
                        while (true) {
                            nnn = nn + " " + index;
                            if (!getModel().getPlannings().containsKey(nnn)) {
                                break;
                            }
                            index++;
                        }
                    }

                    getModel().getPlannings().put(nnn, pp);
                }
            }
        }
        AcademicTeacher t = a.getCurrentTeacher();
        if (t != null) {
            plannings0 = a.loadTeacherPlanning(t.getId());
            if (plannings0 != null) {
                String nn = t.getContact().getFullName();
                String nnn = nn;
                if (getModel().getPlannings().containsKey(nnn)) {
                    int index = 2;
                    while (true) {
                        nnn = nn + " " + index;
                        if (!getModel().getPlannings().containsKey(nnn)) {
                            break;
                        }
                        index++;
                    }
                }
                getModel().getPlannings().put(nnn, plannings0);
            }
        }

        AcademicStudent st = a.getCurrentStudent();
        if (st != null) {
            List<PlanningData> all = a.loadStudentPlanningList(st.getId());
            if (all != null) {
                for (PlanningData planningData : all) {
                    if (planningData != null) {
                        String nn = planningData.getPlanningName();
                        String nnn = nn;
                        if (getModel().getPlannings().containsKey(nnn)) {
                            int index = 2;
                            while (true) {
                                nnn = nn + " " + index;
                                if (!getModel().getPlannings().containsKey(nnn)) {
                                    break;
                                }
                                index++;
                            }
                        }
                        getModel().getPlannings().put(nnn, planningData);
                    }
                }
            }
        }

//        for (String t : p.loadStudentPlanningListNames()) {
        if (getModel().getPlannings().size() > 1) {
            PlanningData fusion = new PlanningData();
            fusion.setPlanningName("* Mon Emploi *");
            HashSet<String> visited = new HashSet<>();
            for (PlanningData pp : getModel().getPlannings().values()) {
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
                        String ha = "A:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonnull(hour.getActivity());
                        String hr = "R:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonnull(hour.getRoom());
                        String hs = "S:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonnull(hour.getStudents());
                        String hj = "J:" + day.getDayName() + ":" + hour.getHour() + ":" + StringUtils.nonnull(hour.getSubject());

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
            getModel().getPlannings().put(fusion.getPlanningName(), fusion);
        }

        getModel().setGroups(new ArrayList<SelectItem>());
        for (String k : new TreeSet<String>(getModel().getPlannings().keySet())) {
            getModel().getGroups().add(new SelectItem(String.valueOf(k), k));
        }
        if (getModel().getGroupName() == null) {
            if (getModel().getPlannings().size() == 1) {
                for (String k : getModel().getPlannings().keySet()) {
                    getModel().setGroupName(k);
                    break;
                }
            } else {
                for (String k : getModel().getPlannings().keySet()) {
                    if ("* Mon Emploi *".equals(k)) {
                        getModel().setGroupName(k);
                        break;
                    }
                }
            }
        }

        PlanningData plannings = getModel().getPlannings().get(getModel().getGroupName());
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
}
