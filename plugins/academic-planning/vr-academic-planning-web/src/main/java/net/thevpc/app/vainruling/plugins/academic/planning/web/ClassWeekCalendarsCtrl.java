/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.planning.web;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPlugin;
import net.thevpc.app.vainruling.plugins.academic.planning.service.AcademicPlanningPluginSecurity;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.plugins.calendars.web.week.AbstractWeekCalendarCtrl;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.NamedId;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
                @VrPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Emploi par Groupe",
        url = "modules/academic/planning/class-week-calendars",
        menu = "/Calendars",
        securityKey = AcademicPlanningPluginSecurity.RIGHT_CUSTOM_EDUCATION_CLASS_PLANNING
)
@Controller
public class ClassWeekCalendarsCtrl extends AbstractWeekCalendarCtrl {

    public ClassWeekCalendarsCtrl() {
        super();
        model = new ModelExt();
    }

    public void onGroupChanged() {
        onRefresh();
    }

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        getModel().setGroups(new ArrayList<SelectItem>());
        AcademicPlanningPlugin pl = VrApp.getBean(AcademicPlanningPlugin.class);
        for (NamedId t : pl.loadStudentPlanningListNames()) {
            getModel().getGroups().add(FacesUtils.createSelectItem(t.getStringId(), StringUtils.nonNull(t.getName())));
        }
        getModel().setCalendar(pl.loadClassPlanning(getModel().getGroupName()));
    }

    @VrOnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();
    }
    
    public boolean isValidPlanning() {
        return getModel().getPlanning() != null && (model.getPlanning().size()) > 0;
    }

    public boolean isMissingPlanning() {
        return getModel().getGroupName() != null
                && getModel().getGroupName().length() > 0
                && (getModel().getPlanning() == null
                || (getModel().getPlanning().size()) == 0);
    }

    public String getSelectedGroupLabel() {
        String r = getModel().getGroupName();
        if (StringUtils.isBlank(r)) {
            return r;
        }
        for (SelectItem group : getModel().getGroups()) {
            if (group.getValue() != null && group.getValue().equals(r)) {
                return group.getLabel();
            }
        }
        return r;
    }

    @Override
    public ModelExt getModel() {
        return (ModelExt) super.getModel();
    }

    public class ModelExt extends Model {

        String groupName;
        List<SelectItem> groups = new ArrayList<SelectItem>();

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

    }
}
