/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import net.vpc.app.vainruling.core.service.notification.PollAware;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
import org.primefaces.model.chart.DonutChartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
//        title = "Utilisateurs Connectés",
        url = "modules/admin/active-sessions",
        menu = "/Social",
        securityKey = "Custom.Admin.ActiveSessions"
)
@Scope(value = "singleton")
public class ActiveSessionsCtrl implements PollAware {
    public static final Date MIN_DATE = new Date(Long.MIN_VALUE);
    private static final Comparator<UserSession> SESSION_COMPARATOR = new Comparator<UserSession>() {
        @Override
        public int compare(UserSession o1, UserSession o2) {
            Date d1=(o1!=null && o1.getConnexionTime()!=null)?o1.getConnexionTime(): MIN_DATE;
            Date d2=(o2!=null && o2.getConnexionTime()!=null)?o2.getConnexionTime(): MIN_DATE;
            return -d1.compareTo(d2);
        }
    };

    @Autowired
    private CorePlugin core;
    private final Model model = new Model();

    public boolean isInvalidatable(UserSession s){
        UserSession userSession = core.getUserSession();
        if(userSession==null || s==null){
            return false;
        }
        if(s.getPlatformSession()==null){
            return false;
        }
        if(StringUtils.nonNull(userSession.getSessionId()).equals(StringUtils.nonNull(s.getSessionId()))){
            return false;
        }
        return true;
    }

    public void onInvalidateSession(UserSession s){
        if(isInvalidatable(s)){
            Object session = s.getPlatformSession();
            core.logout(s.getSessionId());
            if(session instanceof HttpSession){
                try {
                    ((HttpSession) session).invalidate();
                }catch(IllegalStateException ex){
                    //already invalid
                    core.getSessions().onDestroy(s.getSessionId());
                }catch(Exception ex){
                    //any other exception
                    core.getSessions().onDestroy(s.getSessionId());
                }
            }
        }
    }

    public List<UserSession> getOrderedAndValidSessions(){
        List<UserSession> validOnly=new ArrayList<>();
        for (UserSession userSession : core.getSessions().getOrderedActiveSessions()) {
            boolean validSession=true;
            if(isInvalidatable(userSession)) {
                Object session = userSession.getPlatformSession();
                if (session instanceof HttpSession) {
                    HttpSession hsession = (HttpSession) session;
                    try {
                        hsession.getLastAccessedTime();
                    } catch (IllegalStateException ex) {
                        validSession = false;
                    }
                }
            }
            if(!validSession){
                onInvalidateSession(userSession);
            }else{
                validOnly.add(userSession);
            }
        }
        return validOnly;
    }

    @OnPageLoad
    public void onRefresh() {
        if (model.updating) {
            return;
        }
        synchronized (model) {
            model.updating = true;
            try {
                List<UserSession> list = getOrderedAndValidSessions();
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
                        if (i != null && i.getUser() != null && i.getUser().getDepartment() != null) {
                            AppDepartment c = i.getUser().getDepartment();
                            if (c!=null && !StringUtils.isEmpty(c.getName())) {
                                VrUtils.incKey(circle2, c.getName());
                            }
                        }
                    }
                    VrUtils.reverseSortCount(circle1);
                    VrUtils.reverseSortCount(circle2);
                    VrUtils.mergeMapKeys(circle1, circle2);
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
                                VrUtils.incKey(circle1, i.getLocale().getLanguage());
                            }
                        }
                        if (!StringUtils.isEmpty(i.getLastVisitedPage())) {
                            VrUtils.incKey(circle2, i.getLastVisitedPage());
                        }
                    }
                    VrUtils.reverseSortCount(circle1);
                    VrUtils.reverseSortCount(circle2, 12, null);
                    VrUtils.mergeMapKeys(circle1, circle2);
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
        if(t==null){
            return null;
        }
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

        public List<UserSession> getOrderedSessions() {
            List<UserSession> arr=new ArrayList<>(getSessions());
            UserSession curr = UserSession.get();
            if(curr!=null) {
                Collections.sort(arr, SESSION_COMPARATOR);
                if (!curr.isAdmin()) {
                    HashSet<Integer> visited = new HashSet<>();
                    for (Iterator<UserSession> iterator = arr.iterator(); iterator.hasNext(); ) {
                        UserSession userSession = iterator.next();
                        if (userSession.getUser() == null) {
                            iterator.remove();
                        } else if (visited.contains(userSession.getUser().getId())) {
                            iterator.remove();
                        } else {
                            visited.add(userSession.getUser().getId());
                        }
                    }
                }
            }
            return arr;
        }

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
