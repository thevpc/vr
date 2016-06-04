/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
@DependsOn("bootstrapService")
@Scope(value = "session")
public class UserSession implements Serializable {

    private transient AppUser rootUser;
    private transient AppUser user;
    private boolean connected;
    private Date connexionTime;
    private boolean admin;
    private boolean destroyed;
    private String lang;
    private String sessionId;
    private String clientIpAddress;
    private String lastVisitedPage;
//    private String componentsTheme="glass-x";
    private String componentsTheme="eniso-green";
    private Locale locale;
    private Set<String> rights = new HashSet<>();
    private List<AppProfile> profiles = new ArrayList<>();
    private String profilesString;
    private Set<String> profileNames = new HashSet<>();

    public static UserSession getCurrentSession() {
        try {
            return VrApp.getBean(UserSession.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static AppUser getCurrentUser() {
        UserSession s = getCurrentSession();
        return s == null ? null : s.getUser();
    }

    public Date getConnexionTime() {
        return connexionTime;
    }

    public void setConnexionTime(Date connexionTime) {
        this.connexionTime = connexionTime;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isImpersonating() {
        return rootUser != null;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isAdmin() {
        return admin;
    }

    public AppUser getUser() {
        return user;
    }

    public boolean isFemale() {
        if (user == null || user.getContact()==null || user.getContact().getGender() == null || user.getContact().getGender().getName() == null) {
            return false;
        }
        return user.getContact().getGender().getName().equals("F");
    }

    public Set<String> getRights() {
        return rights;
    }

    public void setRights(Set<String> rights) {
        this.rights = rights;
    }

    public Set<String> getProfileNames() {
        return profileNames;
    }

    public void setProfileNames(Set<String> profileNames) {
        this.profileNames = profileNames;
    }

    public void setUser(AppUser user) {
        this.user = user;
        if (user != null) {
            connected = true;
            rights.clear();
        } else {
            connected = false;
            admin = false;
            rights.clear();
        }
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean allowed(String s) {
        return admin || rights.contains(right(s));
    }

    public void checkAllowed(String s) {
        if (!allowed(s)) {
            throw new RuntimeException("Not Allowed");
        }
    }

    public String right(String s) {
        if (s == null) {
            return "DEFAULT";
        }
        return s;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public List<AppProfile> getProfiles() {
        return profiles;
    }

    public String getProfilesString() {
        return profilesString;
    }

    public void setProfiles(List<AppProfile> profiles) {
        this.profiles = profiles;
    }

    public void setProfilesString(String profilesString) {
        this.profilesString = profilesString;
    }

    public void reset() {
        setLocale(null);
        setConnexionTime(null);
        setUser(null);
        setAdmin(false);
        setRights(new HashSet<String>());
        setProfileNames(new HashSet<String>());
        setProfilesString(null);
        setProfiles(new ArrayList<AppProfile>());
    }

    public AppUser getRootUser() {
        return rootUser;
    }

    public void setRootUser(AppUser rootUser) {
        this.rootUser = rootUser;
    }

    public String getLastVisitedPage() {
        return lastVisitedPage;
    }

    public void setLastVisitedPage(String lastVisitedPage) {
        this.lastVisitedPage = lastVisitedPage;
    }

    public String getComponentsTheme() {
        return componentsTheme;
    }

    public void setComponentsTheme(String componentsTheme) {
        this.componentsTheme = componentsTheme;
    }
    
}
