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
        url = "modules/academic/planning/room-week-calendars",
        menu = "/Calendars",
        securityKey = AcademicPlanningPluginSecurity.RIGHT_CUSTOM_EDUCATION_ROOM_PLANNING
)
@Controller
public class RoomWeekCalendarsCtrl extends AbstractWeekCalendarCtrl {

    public RoomWeekCalendarsCtrl() {
        super();
        model = new ModelExt();
    }

    public void onRoomChanged() {
        onRefresh();
    }

    public void onRefresh() {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        getModel().setRooms(new ArrayList<SelectItem>());
        AcademicPlanningPlugin pl = VrApp.getBean(AcademicPlanningPlugin.class);
        for (NamedId t : pl.loadRoomPlanningListNames()) {
            getModel().getRooms().add(FacesUtils.createSelectItem(t.getStringId(), StringUtils.nonNull(t.getName())));
        }
        getModel().setCalendar(pl.loadRoomPlanning(getModel().getRoomName()));
    }

    @VrOnPageLoad
    public void onRefresh(String cmd) {
        onRefresh();
    }

    public boolean isValidPlanning() {
        return getModel().getPlanning() != null && (model.getPlanning().size()) > 0;
    }

    public boolean isMissingPlanning() {
        return getModel().getRoomName() != null
                && getModel().getRoomName().length() > 0
                && (getModel().getPlanning() == null
                || (getModel().getPlanning().size()) == 0);
    }

    public String getSelectedRoomLabel() {
        String r = getModel().getRoomName();
        if (StringUtils.isBlank(r)) {
            return r;
        }
        for (SelectItem room : getModel().getRooms()) {
            if (room.getValue() != null && room.getValue().equals(r)) {
                return room.getLabel();
            }
        }
        return r;
    }

    public ModelExt getModel() {
        return (ModelExt) super.getModel();
    }

    public class ModelExt extends Model {

        String roomName;
        List<SelectItem> groups = new ArrayList<SelectItem>();

        public List<SelectItem> getRooms() {
            return groups;
        }

        public void setRooms(List<SelectItem> groups) {
            this.groups = groups;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

    }
}
