package net.vpc.app.vainruling.core.service.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class DefaultUserToken implements UserToken {

    private String domain;
    private String sessionId;
    private Integer userId;
    private Integer rootUserId;
    private String rootLogin;
    private String userLogin;
    private String ipAddress;
    private String clientApp;
    private String clientAppId;
    private String publicTheme;
    private String privateTheme;
    private String userTypeName;
    private String locale;
    private Date connexionTime;
    private boolean admin;
    private boolean destroyed;
    private boolean female;
    private int[] managedDepartments = {};
    private boolean manager;
    private Set<String> rights = new HashSet<>();
    private Set<String> profileNames = new TreeSet<>();

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public Integer getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public Integer getRootUserId() {
        return rootUserId;
    }

    @Override
    public void setRootUserId(Integer rootUserId) {
        this.rootUserId = rootUserId;
    }

    @Override
    public String getRootLogin() {
        return rootLogin;
    }

    @Override
    public void setRootLogin(String rootLogin) {
        this.rootLogin = rootLogin;
    }

    @Override
    public String getUserLogin() {
        return userLogin;
    }

    @Override
    public void setUserLogin(String login) {
        this.userLogin = login;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String getClientApp() {
        return clientApp;
    }

    @Override
    public void setClientApp(String clientApp) {
        this.clientApp = clientApp;
    }

    @Override
    public String getClientAppId() {
        return clientAppId;
    }

    @Override
    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }

    @Override
    public String getPublicTheme() {
        return publicTheme;
    }

    @Override
    public void setPublicTheme(String publicTheme) {
        this.publicTheme = publicTheme;
    }

    @Override
    public String getPrivateTheme() {
        return privateTheme;
    }

    @Override
    public void setPrivateTheme(String privateTheme) {
        this.privateTheme = privateTheme;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public Date getConnexionTime() {
        return connexionTime;
    }

    @Override
    public void setConnexionTime(Date connexionTime) {
        this.connexionTime = connexionTime;
    }

    @Override
    public boolean isAdmin() {
        return admin;
    }

    @Override
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    @Override
    public boolean isFemale() {
        return female;
    }

    @Override
    public void setFemale(boolean female) {
        this.female = female;
    }

    @Override
    public int[] getManagedDepartments() {
        return managedDepartments;
    }

    @Override
    public void setManagedDepartments(int[] managedDepartments) {
        this.managedDepartments = managedDepartments;
    }

    @Override
    public boolean isManager() {
        return manager;
    }

    @Override
    public void setManager(boolean manager) {
        this.manager = manager;
    }

    @Override
    public String getUserTypeName() {
        return userTypeName;
    }

    @Override
    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    @Override
    public void reset() {
        setDestroyed(true);
        setDomain(null);
        setLocale(null);
        setConnexionTime(null);
        setUserId(null);
        setRootUserId(null);
        setUserLogin(null);
        setRootLogin(null);
        setAdmin(false);
        setManager(false);
        setRights(new HashSet<String>());
        setProfileCodes(new HashSet<String>());
        setClientApp(null);
        setClientApp(null);
        setClientAppId(null);
        setDestroyed(false);
        setFemale(false);
        setIpAddress(null);
        setManagedDepartments(new int[0]);
        setSessionId(null);
        setPublicTheme(null);
        setPrivateTheme(null);
    }

    @Override
    public Set<String> getRights() {
        return rights;
    }

    @Override
    public void setRights(Set<String> rights) {
        this.rights = rights;
    }

    @Override
    public Set<String> getProfileCodes() {
        return profileNames;
    }

    @Override
    public void setProfileCodes(Set<String> profileNames) {
        this.profileNames = profileNames == null ? new TreeSet<>() : new TreeSet<>(profileNames);
    }
}
