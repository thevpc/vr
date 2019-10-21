/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.VrPageHistoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.*;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrOnPageLoad;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
//        title = "Historique de Navigation",
        url = "modules/util/navigation-history",
        menu = "/Desktop",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_UTIL_NAVIGATION_HISTORY
)
@Scope(value = "session")
public class NavigationHistoryCtrl  {
    public static final Date MIN_DATE = new Date(Long.MIN_VALUE);
    private static final Comparator<UserSession> SESSION_COMPARATOR = new Comparator<UserSession>() {
        @Override
        public int compare(UserSession o1, UserSession o2) {
            Date d1=(o1!=null && o1.getToken().getConnexionTime()!=null)?o1.getToken().getConnexionTime(): MIN_DATE;
            Date d2=(o2!=null && o2.getToken().getConnexionTime()!=null)?o2.getToken().getConnexionTime(): MIN_DATE;
            return -d1.compareTo(d2);
        }
    };

    @Autowired
    private CorePlugin core;
    private final Model model = new Model();


    @VrOnPageLoad
    public void onRefresh() {
        ArrayList<VrPageHistoryItem> historyElements = new ArrayList<>(Vr.get().getPageHistory());
        Collections.reverse(historyElements);
        model.setHistoryElements(historyElements);
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


    public static class Model {

        private List<VrPageHistoryItem> historyElements = new ArrayList<>();

        public List<VrPageHistoryItem> getHistoryElements() {
            return historyElements;
        }

        public void setHistoryElements(List<VrPageHistoryItem> historyElements) {
            this.historyElements = historyElements;
        }
    }
}
