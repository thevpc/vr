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

    private UserToken token=new DefaultUserToken();
    private transient PlatformSession platformSession;
    private String fullName;
    private String firstName;
    private String lastName;
    private String sessionId;
    private String lastVisitedPage;
    private String preConnexionURL;
    private String lastVisitedPageInfo;
    private String selectedSiteFilter;

    public UserSession copy(){
        try {
            UserSession s=((UserSession)clone());
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

//    public boolean isImpersonating() {
//        return rootUser != null;
//    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

//    public String getTypeName() {
//        AppUser u = getUser();
//        if(u==null){
//            return null;
//        }
//        AppUserType t = u.getType();
//        if(t==null){
//            return null;
//        }
//        return t.getName();
//    }

//    public AppUser getUser() {
//        return user;
//    }


//
//    public void setUserLogin(String login) {
//        this.login = login;
//    }

//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public Set<String> getRights() {
//        return rights;
//    }
//
//    public void setRights(Set<String> rights) {
//        this.rights = rights;
//    }
//
//    public Set<String> getProfileNames() {
//        return profileNames;
//    }
//
//    public void setProfileNames(Set<String> profileNames) {
//        this.profileNames = profileNames;
//    }
//
//    public boolean isConnected() {
//        return connected;
//    }
//
//    public boolean allowed(String s) {
//        return admin || rights.contains(right(s));
//    }

//    public void checkAllowed(String s) {
//        if (!allowed(s)) {
//            throw new RuntimeException("Not Allowed");
//        }
//    }

//    public String right(String s) {
//        if (s == null) {
//            return "DEFAULT";
//        }
//        return s;
//    }

    public void reset() {
//        setDomain(null);
//        setLocale(null);
        getToken().reset();
//        setUser(null);
//        setRootUser(null);
//        setAdmin(false);
//        setRights(new HashSet<String>());
//        setProfileNames(new HashSet<String>());
    }

//    public AppUser getRootUser() {
//        return rootUser;
//    }
//
//    public void setRootUser(AppUser rootUser) {
//        this.rootUser = rootUser;
//    }
//
//    public String getLastVisitedPage() {
//        return lastVisitedPage;
//    }

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

//    public int getDepartmentManager() {
//        return departmentManager;
//    }
//
//    public boolean isDepartmentManager(int deptId) {
//        return departmentManager == deptId;
//    }
//
//    public boolean isDepartmentManager() {
//        AppUser u = getUser();
//        return u != null && u.getDepartment() != null && isDepartmentManager(u.getDepartment().getId());
//    }
//
//    public void setDepartmentManager(int departmentManager) {
//        this.departmentManager = departmentManager;
//    }

//    public boolean isManagerOrAdmin() {
//        return getToken().isManager() || getToken().isAdmin();
//    }

//    public String getTheme() {
//        return theme;
//    }

//    public void setTheme(String theme) {
//        this.theme = theme;
//    }

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
