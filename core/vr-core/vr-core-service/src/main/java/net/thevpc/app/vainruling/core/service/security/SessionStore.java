package net.thevpc.app.vainruling.core.service.security;

import net.thevpc.app.vainruling.core.service.PlatformSession;

import java.util.Collection;
import java.util.Map;

public interface SessionStore {
    void init(Map<String, String> configMap);

    void destroy();

    int size();

    void remove(String id);

    PlatformSession get(String id);

    PlatformSession getValid(String id);

    Collection<PlatformSession> getAll();

    void put(String id, PlatformSession session);

}
