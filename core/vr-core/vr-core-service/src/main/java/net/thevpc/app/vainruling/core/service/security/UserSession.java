/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.security;

import net.thevpc.app.vainruling.core.service.PlatformSession;
import net.thevpc.app.vainruling.core.service.VrApp;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author taha.bensalah@gmail.com
 */
@Service
@Scope(value = "session")
public class UserSession implements Serializable, Cloneable {

    private UserToken token = new DefaultUserToken();
    private transient PlatformSession platformSession;
    private String sessionId;
    private String preConnexionURL;
    private String invalidConnexionURL;
    private String selectedSiteFilter;

    public UserSession copy() {
        try {
            UserSession s = ((UserSession) clone());
            return s;
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("Unexpected");
        }
    }

    public static UserSession get() {
        try {
            return VrApp.getBean(UserSession.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getInvalidConnexionURL() {
        return invalidConnexionURL;
    }

    public void setInvalidConnexionURL(String invalidConnexionURL) {
        this.invalidConnexionURL = invalidConnexionURL;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void reset() {
        getToken().reset();
    }

    public PlatformSession resolvePlatformSession() {
        return platformSession;
    }

    public void setPlatformSession(PlatformSession platformSession) {
        this.platformSession = platformSession;
    }

    public boolean isValid() {
        return platformSession != null && platformSession.isValid();
    }

    public boolean invalidate() {
        if (platformSession != null) {
            return platformSession.invalidate();
        }
        return false;
    }

    public String getSelectedSiteFilter() {
        return selectedSiteFilter;
    }

    public void setSelectedSiteFilter(String selectedSiteFilter) {
        this.selectedSiteFilter = selectedSiteFilter;
    }

    public String getPreConnexionURL() {
        return preConnexionURL;
    }

    public void setPreConnexionURL(String preConnexionURL) {
        this.preConnexionURL = preConnexionURL;
    }

    public UserToken getToken() {
        return token;
    }

    public void setToken(UserToken token) {
        this.token = token;
    }
}
