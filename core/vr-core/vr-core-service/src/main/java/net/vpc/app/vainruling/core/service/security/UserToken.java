package net.vpc.app.vainruling.core.service.security;

import java.util.Date;
import java.util.Set;

public interface UserToken {
    String getDomain();

    void setDomain(String domain);

    String getSessionId();

    void setSessionId(String sessionId);

    Integer getUserId();

    void setUserId(Integer userId);

    Integer getRootUserId();

    void setRootUserId(Integer rootUserId);

    String getRootLogin();

    void setRootLogin(String rootLogin);

    String getUserLogin();

    void setUserLogin(String login);

    String getIpAddress();

    void setIpAddress(String ipAddress);

    String getClientApp();

    void setClientApp(String clientApp);

    String getClientAppId();

    void setClientAppId(String clientAppId);

    String getPublicTheme();

    void setPublicTheme(String theme);

    String getPrivateTheme();

    void setPrivateTheme(String theme);

    String getLocale();

    void setLocale(String locale);

    Date getConnexionTime();

    void setConnexionTime(Date connexionTime);

    boolean isAdmin();

    void setAdmin(boolean admin);

    boolean isDestroyed();

    void setDestroyed(boolean destroyed);

    boolean isFemale();

    void setFemale(boolean female);

    int[] getManagedDepartments();

    void setManagedDepartments(int[] managedDepartments);

    boolean isManager();

    void setManager(boolean manager);

    String getUserTypeName();

    void setUserTypeName(String userTypeName);

    void reset();

    Set<String> getRights();

    void setRights(Set<String> rights);

    Set<String> getProfileNames();

    void setProfileNames(Set<String> profileNames);
}
