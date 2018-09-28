/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppTrace;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.notification.PollAware;
import net.vpc.app.vainruling.core.service.security.UserSessionInfo;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Chronometer;
import net.vpc.common.util.DatePart;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import org.primefaces.model.chart.DonutChartModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
//        title = "Utilisateurs Connectés",
        url = "modules/admin/active-sessions",
        menu = "/Social",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_ADMIN_ACTIVE_SESSIONS
)
//@Scope(value = "singleton")
public class ActiveSessionsCtrl implements PollAware {
    public static final Date MIN_DATE = new Date(Long.MIN_VALUE);
    private static final Comparator<UserSessionInfo> SESSION_COMPARATOR = new Comparator<UserSessionInfo>() {
        @Override
        public int compare(UserSessionInfo o1, UserSessionInfo o2) {
            Date d1=(o1!=null && o1.getConnexionTime()!=null)?o1.getConnexionTime(): MIN_DATE;
            Date d2=(o2!=null && o2.getConnexionTime()!=null)?o2.getConnexionTime(): MIN_DATE;
            return -d1.compareTo(d2);
        }
    };

    @Autowired
    private CorePlugin core;
    private final Model model = new Model();
    private static int COUNT=0;
    public ActiveSessionsCtrl() {
//        COUNT++;
//        System.out.println("ActiveSessionsCtrl "+(COUNT));
    }

    public boolean isInvalidatable(UserSessionInfo s){
        if(s==null || s.getSessionId()==null){
            return false;
        }
        UserToken userSession = core.getCurrentToken();
        if(userSession!=null) {
            if (StringUtils.nonNull(userSession.getSessionId()).equals(StringUtils.nonNull(s.getSessionId()))) {
                return false;
            }
        }
        return true;
    }

    public void onShowLog(UserSessionInfo s){
        if(s==null || s.getUserId()==null){
            getModel().setCurrentUserSession(null);
            getModel().setCurrentLog(new ArrayList<>());
        }else{
            getModel().setCurrentUserSession(s);
            getModel().setCurrentLog(core.findTraceByUser(s.getUserId(),100));
        }

    }

    public void onInvalidateAllAnonymous(){
        for (UserSessionInfo userSessionInfo : new ArrayList<>(getModel().getSessions())) {
            if (isInvalidatable(userSessionInfo) && userSessionInfo.getUserId()==null) {
                onInvalidateSession(userSessionInfo);
            }
        }
        onRefresh();
    }

    public void onInvalidateSession(UserSessionInfo s){
        if(isInvalidatable(s)){
            core.logout(s.getSessionId());
            onRefresh();
        }
    }

    public List<UserSessionInfo> getOrderedAndValidSessions(){
        List<UserSessionInfo> validOnly=new ArrayList<>();
        boolean admin = core.isCurrentSessionAdmin();
        boolean groupSessions = !admin || getModel().isShowGrouped();
        boolean groupAnonymous = groupSessions;
        boolean anonymous = admin && getModel().isShowAnonymous();
        for (UserSessionInfo userSession : core.getActiveSessions(groupSessions, groupAnonymous, anonymous)) {
            validOnly.add(userSession);
//            boolean validSession=true;
//            if(isInvalidatable(userSession)) {
//                validSession=userSession.isValid();
//            }
//            if(!validSession){
//                onInvalidateSession(userSession);
//            }else{
//                validOnly.add(userSession);
//            }
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
                List<UserSessionInfo> list = getOrderedAndValidSessions();
                Map<String, TypeStat> stats = new HashMap<String, TypeStat>();
                int typeCounter=0;
                for (UserSessionInfo i : list) {
                    if (i != null && i.getUserId() != null) {
                        String t = i.getUserTypeName();
                        if (t != null) {
                            TypeStat s = stats.get(t);
                            if (s == null) {
                                s = new TypeStat(++typeCounter, t, 0);
                                stats.put(t, s);
                            }
                            s.count++;
                        }
                    } else if (i != null && i.getUserId() == null) {
                        i.setUserLogin("<anonymous>");
                    }
                }
                getModel().setSessions(list);
                ArrayList<TypeStat> sli = new ArrayList<TypeStat>(stats.values());
                Collections.sort(sli);
                getModel().setTypeStats(sli);
                getModel().setAdmin(VrApp.getBean(CorePlugin.class).isCurrentSessionAdmin());

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
                    for (Map.Entry<String, TypeStat> entry : stats.entrySet()) {
                        circle1.put(entry.getValue().getType(), entry.getValue().getCount());
                    }
                    for (UserSessionInfo i : list) {
                        if (i != null && i.getUserId() != null) {
                            AppUser u = UPA.getContext().invokePrivileged(new Action<AppUser>() {
                                @Override
                                public AppUser run() {
                                    return core.findUser(i.getUserId());
                                }
                            });
                            if(u!=null) {
                                AppDepartment c = u.getDepartment();
                                if (c != null && !StringUtils.isEmpty(c.getName())) {
                                    VrUtils.incKey(circle2, c.getName());
                                }
                            }
                        }
                    }
                    VrUtils.reverseSortCount(circle1);
                    VrUtils.reverseSortCount(circle2);
                    VrUtils.mergeMapKeys(circle1, circle2);

                    //workaround
                    if(circle1.isEmpty()){
                        circle1.put("No Data", 0);
                    }
                    if(circle2.isEmpty()){
                        circle2.put("No Data", 0);
                    }
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
                    for (UserSessionInfo i : list) {
                        if (i.getLocale() != null) {
                            String language = new Locale(i.getLocale()).getLanguage();
                            if (!StringUtils.isEmpty(language)) {
                                VrUtils.incKey(circle1, language);
                            }
                        }
                    }
                    if(circle1.size()==0){
                        circle1.put("EN",0);
                    }
                    VrUtils.reverseSortCount(circle1);
                    VrUtils.reverseSortCount(circle2, 12, null);
                    VrUtils.mergeMapKeys(circle1, circle2);

                    //workaround
//                    if(circle1.isEmpty()){
//                        circle1.put("No Data", 0);
//                    }
//                    if(circle2.isEmpty()){
//                        circle2.put("No Data", 0);
//                    }

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

    public String connectionPeriod(UserSessionInfo s) {
        if (s == null) {
            return "";
        }
        final Date t = s.getConnexionTime();
        if(t==null){
            return null;
        }
        return Chronometer.formatPeriodMilli(System.currentTimeMillis() - t.getTime(), DatePart.SECOND);
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

        private List<UserSessionInfo> sessions = new ArrayList<>();
        private List<TypeStat> typeStats = new ArrayList<>();
        private boolean updating = false;
        private boolean admin = false;
        private boolean showAnonymous = false;
        private boolean showGrouped = false;
        private DonutChartModel donut1;
        private DonutChartModel donut2;
        private List<AppTrace> currentLog;
        private UserSessionInfo currentUserSession;

        public boolean isShowAnonymous() {
            return showAnonymous;
        }

        public void setShowAnonymous(boolean showAnonymous) {
            this.showAnonymous = showAnonymous;
        }

        public boolean isShowGrouped() {
            return showGrouped;
        }

        public void setShowGrouped(boolean showGrouped) {
            this.showGrouped = showGrouped;
        }

        public UserSessionInfo getCurrentUserSession() {
            return currentUserSession;
        }

        public void setCurrentUserSession(UserSessionInfo currentUserSession) {
            this.currentUserSession = currentUserSession;
        }

        public void setCurrentLog(List<AppTrace> currentLog) {
            this.currentLog = currentLog;
        }

        public List<AppTrace> getCurrentLog() {
            return currentLog;
        }

        public List<UserSessionInfo> getOrderedSessions() {
            List<UserSessionInfo> arr=new ArrayList<>(getSessions());
            UserToken curr = CorePlugin.get().getCurrentToken();
            if(curr!=null) {
                Collections.sort(arr, SESSION_COMPARATOR);
                if (!curr.isAdmin()) {
                    HashSet<Integer> visited = new HashSet<>();
                    for (Iterator<UserSessionInfo> iterator = arr.iterator(); iterator.hasNext(); ) {
                        UserSessionInfo userSession = iterator.next();
                        if (userSession.getUserId() == null) {
                            iterator.remove();
                        } else if (visited.contains(userSession.getUserId())) {
                            iterator.remove();
                        } else {
                            visited.add(userSession.getUserId());
                        }
                    }
                }
            }
            return arr;
        }

        public List<UserSessionInfo> getSessions() {
            return sessions;
        }

        public void setSessions(List<UserSessionInfo> sessions) {
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
