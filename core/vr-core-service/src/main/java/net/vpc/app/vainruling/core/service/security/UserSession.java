/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.security;

import net.vpc.app.vainruling.core.service.PlatformSession;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Service
@Scope(value = "session")
public class UserSession implements Serializable,Cloneable {

    private AppUser rootUser;
    private AppUser user;
    private transient PlatformSession platformSession;
    private boolean connected;
    private Date connexionTime;
    private boolean admin;
    private boolean destroyed;
    private String lang;
    private String clientApp;
    private String clientAppId;
    private String sessionId;
    private String clientIpAddress;
    private String lastVisitedPage;
    private String preConnexionURL;
    private String lastVisitedPageInfo;
    //    private String componentsTheme="glass-x";
//    private String componentsTheme = "eniso-green";
    private String theme = null;
    private Locale locale;
    private Set<String> rights = new HashSet<>();
    private Set<String> profileNames = new HashSet<>();
    private int departmentManager = -1;
    private boolean manager;
    private String domain;
    private String selectedSiteFilter;

    public UserSession copy(){
        try {
            UserSession s=((UserSession)clone());
            s.rights=new HashSet<>(s.rights);
            s.profileNames=new HashSet<>(s.profileNames);
            s.user=s.user==null?null:s.user.copy();
            s.rootUser=s.rootUser==null?null:s.rootUser.copy();
            return s;
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("Unexpected");
        }
    }
    public String getLastVisitedPageInfo() {
        return lastVisitedPageInfo;
    }

    public void setLastVisitedPageInfo(String lastVisitedPageInfo) {
        this.lastVisitedPageInfo = lastVisitedPageInfo;
    }

    public static UserSession get() {
        try {
            return VrApp.getBean(UserSession.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentLogin() {
        AppUser u = getCurrentUser();
        return u == null ? null : u.getLogin();
    }

    public static AppUser getCurrentUser() {
        UserSession s = get();
        return s == null ? null : s.getUser();
    }

    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
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

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isImpersonating() {
        return rootUser != null;
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

    public boolean isSuperAdmin() {
        return "admin".equals(user == null ? null : user.getLogin()) && admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getTypeName() {
        AppUser u = getUser();
        if(u==null){
            return null;
        }
        AppUserType t = u.getType();
        if(t==null){
            return null;
        }
        return t.getName();
    }

    public String getFirstName() {
        return user==null ?null: user.resolveFullName();
    }

    public String getLastName() {
        return user==null ?null: user.resolveLastName();
    }

    public String getFullName() {
        return user==null ?null: user.resolveFullName();
    }

    public String getUserLogin() {
        return user==null?null:user.getLogin();
    }

    public AppUser getUser() {
        return user;
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

    public boolean isFemale() {
        if (user == null || user.getContact() == null || user.getContact().getGender() == null || user.getContact().getGender().getName() == null) {
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

    public void reset() {
        setDomain(null);
        setLocale(null);
        setConnexionTime(null);
        setUser(null);
        setRootUser(null);
        setAdmin(false);
        setRights(new HashSet<String>());
        setProfileNames(new HashSet<String>());
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

//    public String getComponentsTheme() {
//        return componentsTheme;
//    }
//
//    public void setComponentsTheme(String componentsTheme) {
//        this.componentsTheme = componentsTheme;
//    }

    public PlatformSession resolvePlatformSession() {
        return platformSession;
    }

    public void setPlatformSession(PlatformSession platformSession) {
        this.platformSession = platformSession;
    }

    public boolean isValid(){
        return platformSession!=null && platformSession.isValid();
    }

    public boolean invalidate(){
        if(platformSession!=null){
            return platformSession.invalidate();
        }
        return false;
    }

    public int getDepartmentManager() {
        return departmentManager;
    }

    public boolean isDepartmentManager(int deptId) {
        return departmentManager == deptId;
    }

    public boolean isDepartmentManager() {
        AppUser u = getUser();
        return u != null && u.getDepartment() != null && isDepartmentManager(u.getDepartment().getId());
    }

    public void setDepartmentManager(int departmentManager) {
        this.departmentManager = departmentManager;
    }

    public boolean isManagerOrAdmin() {
        return manager || admin;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getSelectedSiteFilter() {
        return selectedSiteFilter;
    }

    public void setSelectedSiteFilter(String selectedSiteFilter) {
        this.selectedSiteFilter = selectedSiteFilter;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getClientApp() {
        return clientApp;
    }

    public void setClientApp(String clientApp) {
        this.clientApp = clientApp;
    }

    public String getPreConnexionURL() {
        return preConnexionURL;
    }

    public void setPreConnexionURL(String preConnexionURL) {
        this.preConnexionURL = preConnexionURL;
    }
}
