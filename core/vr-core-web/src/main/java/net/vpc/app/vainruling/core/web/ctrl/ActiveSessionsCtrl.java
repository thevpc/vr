/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.agent.ActiveSessionsTracker;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import net.vpc.app.vainruling.core.service.notification.PollAware;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.util.ChartUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
import org.primefaces.model.chart.DonutChartModel;
import org.springframework.context.annotation.Scope;

import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        title = "Sessions Actives",
        url = "modules/admin/active-sessions",
        menu = "/Social",
        securityKey = "Custom.Admin.ActiveSessions"
)
@Scope(value = "singleton")
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
                    if (i != null && i.getUser() != null) {
                        AppUserType t = i.getUser().getType();
                        if (t != null) {
                            TypeStat s = stats.get(t.getId());
                            if (s == null) {
                                s = new TypeStat(t.getId(), t.getName(), 0);
                                stats.put(t.getId(), s);
                            }
                            s.count++;
                        }
                    } else if (i != null && i.getUser() == null) {
                        AppUser uu = new AppUser();
                        uu.setId(-1);
                        uu.setLogin("<anonymous>");
                        i.setUser(uu);
                    }
                }
                getModel().setSessions(list);
                ArrayList<TypeStat> sli = new ArrayList<TypeStat>(stats.values());
                Collections.sort(sli);
                getModel().setTypeStats(sli);
                getModel().setAdmin(VrApp.getBean(CorePlugin.class).isUserSessionAdmin());

                {
                    getModel().setDonut1(null);
                    DonutChartModel d = new DonutChartModel();
                    d.setTitle("Utilisateur");
                    d.setLegendPosition("e");
                    d.setSliceMargin(2);
                    d.setShowDataLabels(true);
                    d.setDataFormat("value");
                    d.setShadow(true);
                    getModel().setDonut1(d);
                    Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
                    Map<String, Number> circle2 = new LinkedHashMap<String, Number>();
                    for (Map.Entry<Integer, TypeStat> entry : stats.entrySet()) {
                        circle1.put(entry.getValue().getType(), entry.getValue().getCount());
                    }
                    for (UserSession i : list) {
                        if (i != null && i.getUser() != null && i.getUser().getContact() != null) {
                            AppContact c = i.getUser().getContact();
                            if (!StringUtils.isEmpty(c.getPositionSuffix())) {
                                ChartUtils.incKey(circle2, c.getPositionSuffix());
                            }
                        }
                    }
                    ChartUtils.reverseSortCount(circle1);
                    ChartUtils.reverseSortCount(circle2);
                    ChartUtils.mergeMapKeys(circle1, circle2);
                    d.addCircle(circle1);
                    d.addCircle(circle2);
                }
                {
                    getModel().setDonut2(null);
                    DonutChartModel d = new DonutChartModel();
                    d.setTitle("Langue/Page");
                    d.setLegendPosition("e");
                    d.setSliceMargin(2);
                    d.setShowDataLabels(true);
                    d.setDataFormat("value");
                    d.setShadow(true);
                    getModel().setDonut2(d);
                    Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
                    Map<String, Number> circle2 = new LinkedHashMap<String, Number>();
                    for (UserSession i : list) {
                        if (i.getLocale() != null) {
                            if (!StringUtils.isEmpty(i.getLocale().getLanguage())) {
                                ChartUtils.incKey(circle1, i.getLocale().getLanguage());
                            }
                        }
                        if (!StringUtils.isEmpty(i.getLastVisitedPage())) {
                            ChartUtils.incKey(circle2, i.getLastVisitedPage());
                        }
                    }
                    ChartUtils.reverseSortCount(circle1);
                    ChartUtils.reverseSortCount(circle2, 12, null);
                    ChartUtils.mergeMapKeys(circle1, circle2);
                    d.addCircle(circle1);
                    d.addCircle(circle2);
                }

            } finally {
                model.updating = false;
            }
        }
    }

    public Model getModel() {
        return model;
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
        private DonutChartModel donut1;
        private DonutChartModel donut2;

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

        public DonutChartModel getDonut1() {
            return donut1;
        }

        public void setDonut1(DonutChartModel donut1) {
            this.donut1 = donut1;
        }

        public DonutChartModel getDonut2() {
            return donut2;
        }

        public void setDonut2(DonutChartModel donut2) {
            this.donut2 = donut2;
        }

    }
}
