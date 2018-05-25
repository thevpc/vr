package net.vpc.app.vainruling.core.service.security;

import net.vpc.app.vainruling.core.service.PlatformSession;
import net.vpc.app.vainruling.core.service.security.SessionStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemSessionStore implements SessionStore {
    private final  Map<String, PlatformSession> sessions = new HashMap<>();

    public void remove(String id) {
        synchronized (sessions) {
            sessions.remove(id);
        }
    }

    public PlatformSession get(String id) {
        synchronized (sessions) {
            return sessions.get(id);
        }
    }

    @Override
    public PlatformSession getValid(String id) {
        synchronized (sessions) {
            PlatformSession s = sessions.get(id);
            if(s!=null && !s.isValid()){
                try {
                    s.invalidate();
                }catch (Exception ex){
                    //
                }
                sessions.remove(id);
                s=null;
            }
            return s;
        }
    }

    public void put(String id, PlatformSession session) {
        synchronized (sessions) {
            sessions.put(id, session);
        }
    }

    @Override
    public void init(Map<String, String> configMap) {

    }

    @Override
    public void destroy() {
        synchronized (sessions) {
            sessions.clear();
        }
    }

    @Override
    public int size() {
        synchronized (sessions) {
            return sessions.size();
        }
    }

    @Override
    public Collection<PlatformSession> getAll() {
        synchronized (sessions) {
            return new ArrayList<>(sessions.values());
        }
    }
}
