package net.thevpc.app.vainruling.core.service;

import net.thevpc.app.vainruling.core.service.security.UserToken;

import java.util.Date;

public interface PlatformSession {
    String getSessionId();
    Date getLastAccessedTime();
    Date getConnexionTime();
    String getIpAddress();
    UserToken getToken();
    boolean isValid();
    boolean invalidate();
}
