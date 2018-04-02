package net.vpc.app.vainruling.core.web;

import net.vpc.app.vainruling.core.service.PlatformSession;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.security.UserToken;

import javax.servlet.http.HttpSession;
import java.util.Date;

public class HttpPlatformSession implements PlatformSession {
    private final HttpSession session;
    private final String sessionId;
    private final String ipAddress;
    private final long connexionTime;

    public HttpPlatformSession(HttpSession session,String ipAddress) {
        this.session = session;
        this.ipAddress = ipAddress;
        this.sessionId=session.getId();
        this.connexionTime=session.getCreationTime();
    }



    @Override
    public Date getConnexionTime() {
        return new Date(connexionTime);
    }

    @Override
    public Date getLastAccessedTime() {
        try {
            return new Date(session.getLastAccessedTime());
        } catch (IllegalStateException ex) {
            return null;
        }
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    public HttpSession unwrap() {
        return session;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public boolean isValid() {
        try {
            session.getLastAccessedTime();
        } catch (IllegalStateException ex) {
            return false;
        }
        return true;
    }

    @Override
    public UserToken getToken() {
        try {
            UserSession us = (UserSession) session.getAttribute("userSession");
            if (us == null) {
                return null;
            }
            return us.getToken();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean invalidate() {
        boolean err=false;
        try {
            UserSession us = (UserSession) session.getAttribute("userSession");
            if (us != null) {
                us.reset();
            }
        } catch (Exception e) {
            err=true;
        }
        try {
            session.invalidate();
        } catch (Exception e) {
            err=true;
        }
        return !err;
    }
}
