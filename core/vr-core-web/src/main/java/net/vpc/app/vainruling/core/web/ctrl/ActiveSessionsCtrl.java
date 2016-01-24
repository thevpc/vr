/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.ActiveSessionsTracker;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.PollAware;
import net.vpc.app.vainruling.api.model.AppUserType;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.common.utils.Chronometer;

/**
 *
 * @author vpc
 */
@UCtrl(
        title = "Sessions Actives",
        url = "modules/admin/activesessions",
        menu = "/Admin/Security",
        securityKey = "Custom.Admin.ActiveSessions"
)
@ManagedBean
@ApplicationScoped
public class ActiveSessionsCtrl implements PollAware {

    private final Model model = new Model();

    @OnPageLoad
    public void onRefresh() {
        if (model.updating) {
            return;
        }
        synchronized (model) {
            model.updating = true;
            try {
                List<UserSession> list = VrApp.getBean(ActiveSessionsTracker.class).getOrderedActiveSessions();
                Map<Integer, TypeStat> stats = new HashMap<Integer, TypeStat>();
                for (UserSession i : list) {
                    AppUserType t = i.getUser().getType();
                    if (t != null) {
                        TypeStat s = stats.get(t.getId());
                        if (s == null) {
                            s = new TypeStat(t.getId(), t.getName(), 0);
                            stats.put(t.getId(), s);
                        }
                        s.count++;
                    }
                }
                getModel().setSessions(list);
                ArrayList<TypeStat> sli = new ArrayList<TypeStat>(stats.values());
                Collections.sort(sli);
                getModel().setTypeStats(sli);
                getModel().setAdmin(VrApp.getBean(CorePlugin.class).isActualAdmin());
            } finally {
                model.updating = false;
            }
        }
    }

    public Model getModel() {
        return model;
    }

    public static class TypeStat implements Comparable<TypeStat> {

        private int id;
        private String type;
        private int count;

        public TypeStat(int id, String type, int count) {
            this.id = id;
            this.type = type;
            this.count = count;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public int compareTo(TypeStat o) {
            int x = type.compareTo(o.type);
            if (x != 0) {
                return x;
            }
            return id - o.id;
        }

    }

    public static class Model {

        private List<UserSession> sessions = new ArrayList<>();
        private List<TypeStat> typeStats = new ArrayList<>();
        private boolean updating = false;
        private boolean admin = false;

        public List<UserSession> getSessions() {
            return sessions;
        }

        public void setSessions(List<UserSession> sessions) {
            this.sessions = sessions;
        }

        public List<TypeStat> getTypeStats() {
            return typeStats;
        }

        public void setTypeStats(List<TypeStat> typeStats) {
            this.typeStats = typeStats;
        }

        public boolean isUpdating() {
            return updating;
        }

        public void setUpdating(boolean updating) {
            this.updating = updating;
        }

        public boolean isAdmin() {
            return admin;
        }

        public void setAdmin(boolean admin) {
            this.admin = admin;
        }

    }

    public void onPoll() {
        onUpdate();
    }

    public void onUpdate() {
        onRefresh();
    }

    public String connectionPeriod(UserSession s) {
        if (s == null) {
            return "";
        }
        final Date t = s.getConnexionTime();
        return Chronometer.formatPeriod(System.currentTimeMillis() - t.getTime(), Chronometer.DatePart.s);
    }
}
