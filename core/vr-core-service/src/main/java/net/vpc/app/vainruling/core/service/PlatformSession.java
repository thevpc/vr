package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.security.UserToken;

import java.util.Date;

public interface PlatformSession {
    String getSessionId();
    Date getLastAccessedTime();
    Date getConnexionTime();
    UserToken getToken();
    boolean isValid();
    boolean invalidate();
}
