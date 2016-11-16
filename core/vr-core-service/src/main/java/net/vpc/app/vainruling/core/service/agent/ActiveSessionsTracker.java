/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.agent;

import net.vpc.app.vainruling.core.service.security.UserSession;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
public class ActiveSessionsTracker {

    private Map<String, UserSession> activeSessions = new HashMap<>();

    public UserSession getUserSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public void onCreate(UserSession s) {
        if (s.getSessionId() == null) {
            throw new IllegalArgumentException("Why");
        }
        activeSessions.put(s.getSessionId(), s);
    }

    public void onDestroy(UserSession s) {
        if (s != null) {
            s.reset();
            onDestroy(s.getSessionId());
        }
    }

    public void onDestroy(String sessionId) {
        if (sessionId != null) {
            UserSession s = activeSessions.get(sessionId);
            if (s != null) {
                s.setUser(null);
                s.setDestroyed(true);
                s.reset();
                activeSessions.remove(sessionId);
            }
        }
    }

    public int getActiveSessionsCount() {
        return activeSessions.size();
    }

    public List<UserSession> getOrderedActiveSessions() {
        List<UserSession> all = new ArrayList<>(activeSessions.values());
        Collections.sort(all, new Comparator<UserSession>() {

            @Override
            public int compare(UserSession o1, UserSession o2) {
                Date t1 = o2.getConnexionTime();
                Date t2 = o2.getConnexionTime();
                if (t1 != t2) {
                    if (t2 == null) {
                        return -1;
                    }
                    if (t1 == null) {
                        return -1;
                    }
                    int x = t2.compareTo(t1);
                    if (x != 0) {
                        return x;
                    }
                }
                return 0;
            }
        });
        return all;
    }
}
