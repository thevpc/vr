package net.vpc.app.vainruling.core.service.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UserSessionInfo {
    private String domain;
    private String sessionId;
    private Integer userId;
    private Integer rootUserId;
    private String rootLogin;
    private String userLogin;
    private String userFullName;
    private String userFullTitle;
    private String ipAddress;
    private String clientApp;
    private String clientAppId;
    private String theme;
    private String userTypeName;
    private String locale;
    private String iconURL;
    private Date connexionTime;
    private Date lastAccessTime;
    private boolean admin;
    private boolean destroyed;
    private boolean female;
    private int count;
    private int[] managedDepartments = {};
    private boolean manager;
    private Set<String> profileNames = new HashSet<>();

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserFullTitle() {
        return userFullTitle;
    }

    public void setUserFullTitle(String userFullTitle) {
        this.userFullTitle = userFullTitle;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRootUserId() {
        return rootUserId;
    }

    public void setRootUserId(Integer rootUserId) {
        this.rootUserId = rootUserId;
    }

    public String getRootLogin() {
        return rootLogin;
    }

    public void setRootLogin(String rootLogin) {
        this.rootLogin = rootLogin;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getClientApp() {
        return clientApp;
    }

    public void setClientApp(String clientApp) {
        this.clientApp = clientApp;
    }

    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Date getConnexionTime() {
        return connexionTime;
    }

    public void setConnexionTime(Date connexionTime) {
        this.connexionTime = connexionTime;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public int[] getManagedDepartments() {
        return managedDepartments;
    }

    public void setManagedDepartments(int[] managedDepartments) {
        this.managedDepartments = managedDepartments;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public Set<String> getProfileNames() {
        return profileNames;
    }

    public void setProfileNames(Set<String> profileNames) {
        this.profileNames = profileNames;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incCount() {
        count++;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
}
