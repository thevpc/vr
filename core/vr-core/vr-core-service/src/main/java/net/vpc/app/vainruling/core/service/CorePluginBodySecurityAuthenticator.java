package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.security.*;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.*;
import net.vpc.upa.types.DateTime;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.util.Arg;
import net.vpc.app.vainruling.core.service.util.I18n;

class CorePluginBodySecurityAuthenticator extends CorePluginBody {

    private List<UserSessionConfigurator> userSessionConfigurators;
    private static Logger log = Logger.getLogger(CorePluginBodySecurityAuthenticator.class.getName());

    @Override
    public void onInstall() {
        CorePlugin core = getContext().getCorePlugin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppProfile oldAdmin = pu.findByMainField(AppProfile.class, "admin");
        if (oldAdmin != null) {
            oldAdmin.setName("Admin");
            pu.merge(oldAdmin);
        }
        InitData d = new InitData();
        d.now = System.currentTimeMillis();
        d.adminProfile = new AppProfile();
        d.adminProfile.setCode("Admin");
        d.adminProfile.setName("Admin");
        d.adminProfile = core.findOrCreate(d.adminProfile);
        d.adminProfile.setCustom(true);
        d.adminProfile.setCustomType("Profile");
        pu.merge(d.adminProfile);

        d.adminType = core.findOrCreate(new AppUserType("Admin", "Admin"));

        d.civilities = new ArrayList<>();
        for (String n : new String[]{"M.", "Mlle", "Mme"}) {
            AppCivility c = new AppCivility(0, n);
            c = core.findOrCreate(c);
            d.civilities.add(c);
        }
        d.genders = new ArrayList<>();
        for (String n : new String[]{"M", "F"}) {
            AppGender c = new AppGender(0, n);
            c = core.findOrCreate(c);
            d.genders.add(c);
        }

        AppUser uu = new AppUser();
        d.admin = uu;
        d.admin.setFirstName("admin");
        d.admin.setLastName("admin");
        d.admin.setFullName("admin");
        d.admin.setCivility(d.civilities.get(0));
        d.admin.setGender(d.genders.get(0));
        d.admin.setEmail("admin@vr.net");
        d.admin.setLogin("admin");
        d.admin.setPassword("admin");
        d.admin.setType(d.adminType);
        d.admin.setEnabled(true);
        d.admin = core.findOrCreate(d.admin);
        if (d.admin == uu) {
            pu.persist(new AppUserProfileBinding(d.admin, d.adminProfile));
        }
    }

    private List<UserSessionConfigurator> getUserSessionConfigurators() {
        if (userSessionConfigurators == null) {
            ArrayList all = new ArrayList<>();
            for (UserSessionConfigurator bn : VrApp.getBeansForType(UserSessionConfigurator.class)) {
                all.add(bn);
            }
            userSessionConfigurators = all;
        }
        return userSessionConfigurators;
    }

    public UserSessionInfo authenticate(String login, String password, String clientAppId, String clientApp) {
        try {
            AppUser appUser = null;
            UserToken token = null;
            try {
                token = getCurrentToken();
                for (UserSessionConfigurator userSessionConfigurator : getUserSessionConfigurators()) {
                    userSessionConfigurator.preConfigure(token);
                }
                AppUser u = authenticateByLoginPassword(login, password, token, clientAppId, clientApp);
                if (u == null) {
                    throw new SecurityException("Invalid Login or Password");
                }
                AppUser appUser0 = onUserLoggedIn(u, token, clientAppId, clientApp);
                if (appUser0 != null) {
                    for (UserSessionConfigurator userSessionConfigurator : getUserSessionConfigurators()) {
                        userSessionConfigurator.postConfigure(token);
                    }
                }
                appUser = appUser0;
            } finally {
                if (appUser == null) {
                    if (token != null) {
                        //should not invalidate in jsf!!
                        //invalidateToken(token.getSessionId());
                    }
                }
            }
            return populateSessionInfo(token, new UserSessionInfo(), true, new Date());
        } catch (Exception any) {
            log.severe("Login failed (" + login + "): " + any.getMessage());
            return null;
        }
    }

    private String toUniformLogin(String login) {
        if (login == null) {
            login = "";
        }
        login = VrUtils.normalizeName(login);
        StringBuilder sb = new StringBuilder();
        boolean firstSpace = true;
        for (char c : login.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                if (firstSpace) {
                    sb.append('.');
                    firstSpace = false;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private AppUser authenticateByLoginPassword(String login, String password, UserToken token, String clientAppId, String clientApp) {
        login = toUniformLogin(login);
        String flogin = login;
        AppUser user = UPA.getPersistenceUnit().invokePrivileged(new Action<AppUser>() {
            @Override
            public AppUser run() {
                return findEnabledUser(flogin, password);
            }
        });
        if (user == null) {
            AppUser user2 = getContext().getCorePlugin().findUser(login);
            if (user2 == null) {
                getContext().getTrace().trace("System.login.failed", "login not found. Failed as " + login + "/" + password, login + "/" + password, "/System/Access", null, null, (login == null || login.length() == 0) ? "anonymous" : login, -1, Level.SEVERE, token.getIpAddress());
            } else if ((user2.isDeleted() || !user2.isEnabled())) {
                getContext().getTrace().trace("System.login.failed", "invalid state. Failed as " + login + "/" + password, login + "/" + password
                        + ". deleted=" + user2.isDeleted()
                        + ". enabled=" + user2.isEnabled(), "/System/Access", null, null, (login.length() == 0) ? "anonymous" : login, user2.getId(), Level.SEVERE, token.getIpAddress()
                );
            } else {
                getContext().getTrace().trace(
                        "System.login.failed", "Invalid password. Failed as " + login + "/" + password, login + "/" + password,
                        "/System/Access", null, null, (login.length() == 0) ? "anonymous" : login, user2.getId(),
                        Level.SEVERE, token.getIpAddress()
                );
            }
        }
        return user;
    }

    private AppUser findEnabledUser(String login, String password) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppUser) pu
                .createQuery("Select u from AppUser u "
                        + "where "
                        + "u.login=:login "
                        + "and u.password=:password "
                        + "and u.enabled=true "
                        + "and u.deleted=false "
                        + "")
                .setParameter("login", login)
                .setParameter("password", password)
                .getFirstResultOrNull();
    }

    private AppUser onUserLoggedIn(AppUser user, UserToken token, String clientAppId, String clientApp) {
        if (token == null) {
            throw new SecurityException("No Session Found");
        }
        //login is always lower cased and trimmed!
        if (token.getUserId() != null) {
            //throw new SecurityException("Already connected");
        }
        if (user != null) {
            user.setConnexionCount(user.getConnexionCount() + 1);
            user.setLastConnexionDate(new DateTime());
            UPA.getContext().invokePrivileged(
                    TraceService.makeSilenced(
                            new Action<Object>() {

                        @Override
                        public Object run() {
                            UPA.getPersistenceUnit().merge(user);
                            return null;
                        }
                    }), null);
            token.setClientApp(StringUtils.isBlank(clientApp) ? "default" : clientApp);
            token.setClientAppId(StringUtils.isBlank(clientAppId) ? "unknown" : clientAppId);
            token.setDestroyed(false);
            token.setDomain(getContext().getCorePlugin().getCurrentDomain());
            token.setConnexionTime(user.getLastConnexionDate());
            token.setUserId(user.getId());
            buildToken(token);

//            activeSessionsTracker.onCreate(currentSession);
            //update stats
            getContext().getTrace().trace("System.login",
                    I18n.get().get("System.login.succeeded.trace.message", new Arg("user", user.getLogin())),
                    user.getLogin(), "/System/Access", null, null, user.getLogin(), user.getId(), Level.INFO, token.getIpAddress());
            UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    getContext().getCorePlugin().updateMaxAppDataStoreLong("usersCountPeak", getActiveSessionsCount(), true);
                }
            });
            getContext().getCorePlugin().onPoll();
        } else {
            invalidateToken(token.getSessionId());
        }
        return user;
    }

    //wont call core.findUser to avoid StackOverflowError
    protected AppUser findUser(int id) {
        return getContext().getCacheService().getList(AppUser.class).getByKey(id);
    }

    protected void buildToken(UserToken token) {
        AppUser user = findUser(token.getUserId());
        token.setUserLogin(user.getLogin());
        if (token.getRootUserId() != null) {
            AppUser user1 = UPA.getContext().invokePrivileged(new Action<AppUser>() {
                @Override
                public AppUser run() {
                    return findUser(token.getRootUserId());
                }
            });
            token.setRootLogin(user1 == null ? null : user1.getLogin());
        } else {
            token.setRootLogin(null);
        }
        token.setUserTypeName(user.getType() == null ? null : user.getType().getName());
        if (user.getGender() == null) {
            token.setFemale(false);
        } else {
            token.setFemale("F".equals(user.getGender().getName()));
        }
        final List<AppProfile> userProfiles = getContext().getCorePlugin().findProfilesByUser(user.getId());
        Set<String> userProfilesCodes = new HashSet<>();
        for (AppProfile u : userProfiles) {
            userProfilesCodes.add(u.getCode());
        }
        token.setProfileCodes(userProfilesCodes);
//        s.setProfiles(userProfiles);
//        StringBuilder ps = new StringBuilder();
//        for (AppProfile up : userProfiles) {
//            if (ps.length() > 0) {
//                ps.append(", ");
//            }
//            ps.append(up.getName());
//        }
//        s.setProfilesString(ps.toString());
        token.setAdmin(false);
        token.setManagedDepartments(new int[0]);
        token.setManager(false);
        token.setRights(UPA.getContext().invokePrivileged(() -> getContext().getCorePlugin().findUserRightsAll(user.getId())));
        if (user.getLogin().equalsIgnoreCase(CorePlugin.USER_ADMIN) || userProfilesCodes.contains(CorePlugin.PROFILE_ADMIN)) {
            token.setAdmin(true);
        }
        if (userProfilesCodes.contains(CorePlugin.PROFILE_HEAD_OF_DEPARTMENT)) {
            if (user.getDepartment() != null) {
                AppUser d = getContext().getCorePlugin().findHeadOfDepartment(user.getDepartment().getId());
                if (d != null && d.getId() == user.getId()) {
                    token.setManager(true);
                    if (token.getManagedDepartments() == null) {
                        token.setManagedDepartments(new int[]{d.getDepartment().getId()});
                    } else {
                        token.setManagedDepartments(VrUtils.append(token.getManagedDepartments(), d.getDepartment().getId()));
                    }
                }
            }
        }
        for (String mp : getContext().getCorePlugin().getManagerProfiles()) {
            if (userProfilesCodes.contains(mp)) {
                token.setManager(true);
            }
        }
    }

    public void logout() {
        final UserToken s = getCurrentToken();
//        AppUser user = s == null ? null : s.getUser();
        String login = s == null ? null : s.getUserLogin();
        int id = s == null ? -1 : s.getUserId() == null ? -1 : s.getUserId();
        if (login != null) {

            if (s.getRootUserId() != null) {
                getContext().getTrace().trace("logout",
                        I18n.get().get("System.logout-back.succeeded.trace.message", new Arg("user", login), new Arg("oldUser", s.getRootLogin())),
                        login + " => "
                        + s.getRootLogin(),
                        "/System/Access", null, null, login, id, Level.INFO, s.getIpAddress()
                );
                s.setUserId(s.getRootUserId());
                s.setRootUserId(null);
                buildToken(s);
            } else {
                getContext().getTrace().trace("System.logout", I18n.get().get("System.logout.succeeded.trace.message", new Arg("user", login)),
                        login,
                        "/System/Access", null, null, login, id, Level.INFO, s.getIpAddress()
                );
                invalidateToken(s.getSessionId());
            }
        }
    }

    public void logout(String sessionId) {
        PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
        Session cs = persistenceUnit.getCurrentSession();
        if (!"SessionDestroyed".equals(cs.getParam(persistenceUnit, String.class, "Event", null))) {
            CorePluginSecurity.requireAdmin();
        }

//        UserSession currentSession0 = null;
//        try {
//            currentSession0 = getCurrentSession();
//        } catch (Exception any) {
//            //
//        }
//        final UserSession currentSession = currentSession0;
        SessionStore sessionStore = VrApp.getBean(SessionStoreProvider.class).resolveSessionStore();
        if (sessionStore != null) {
            PlatformSession s = sessionStore.get(sessionId);
            UserToken t = s == null ? null : s.getToken();
            String login = t == null ? null : t.getUserLogin();
            int id = t == null || t.getUserId() == null ? -1 : t.getUserId();
            if (t != null) {
                if (login != null) {
                    getContext().getTrace().trace("logout", I18n.get().get("System.logout.succeeded.trace.message", new Arg("user", login)),
                            login,
                            "/System/Access", null, null, login, id, Level.INFO, t.getIpAddress()
                    );
                }
                invalidateToken(s.getToken().getSessionId());
                invalidateToken(sessionId);
            } else {
                if (s != null) {
                    invalidateToken(s.getSessionId());
                }
                invalidateToken(sessionId);
            }
        }
    }

    public AppUser impersonate(String login, String password) {
        login = toUniformLogin(login);
        UserToken s = getCurrentToken();
        if (s.isAdmin() && s.getRootUserId() == null) {
            AppUser user = findEnabledUser(login, password);
            if (user != null) {
                getContext().getTrace().trace("impersonate", "successfull impersonate of " + s.getUserLogin() + " as " + login, s.getUserLogin() + "," + login, "/System/Access", null, null, s.getUserLogin(),
                        s.getUserId(), Level.INFO, s.getIpAddress()
                );
            } else {
                user = getContext().getCorePlugin().findUser(login);
                if (user != null) {
                    if (!user.isEnabled()) {
                        getContext().getTrace().trace("impersonate", "successful impersonate of " + s.getUserLogin() + " as " + login + ". but user is not enabled!", s.getUserLogin() + "," + login, "/System/Access", null, null, s.getUserLogin(), s.getUserId(), Level.WARNING, s.getIpAddress());
                    } else {
                        getContext().getTrace().trace("impersonate", "successful impersonate of " + s.getUserLogin() + " as " + login + ". but password " + password + " seems not to be correct", s.getUserLogin() + "," + login, "/System/Access", null, null, s.getUserLogin(), s.getUserId(), Level.WARNING, s.getIpAddress());
                    }
                } else {
                    getContext().getTrace().trace(
                            "impersonate", "failed impersonate of " + s.getUserLogin() + " as " + login, s.getUserLogin() + "," + login, "/System/Access", null, null, s.getUserLogin(), s.getUserId(), Level.SEVERE, s.getIpAddress()
                    );
                }
            }
            if (user != null) {
                s.setRootUserId(s.getUserId());
                s.setRootLogin(s.getUserLogin());
                s.setUserId(user.getId());
                buildToken(s);
            }
            getContext().getCorePlugin().onPoll();
            return user;
        } else {
            getContext().getTrace().trace("impersonate", "failed impersonate of " + s.getUserLogin() + " as " + login + ". not admin or already impersonating", s.getUserLogin() + "," + login, "/System/Access", null, null, s.getUserLogin(), s.getUserId(), Level.WARNING, s.getIpAddress());
        }
        return null;
    }

    private void invalidateToken(String sessionId) {
        if (sessionId != null) {
            SessionStore sessionStore = VrApp.getBean(SessionStoreProvider.class).resolveSessionStore();
            if (sessionStore != null) {
                PlatformSession platformSession = sessionStore.get(sessionId);
                if (platformSession != null) {
                    platformSession.invalidate();
                }
            }
        }
    }

    public UserToken getCurrentToken() {
        if (!getContext().getCorePlugin().isStarted()) {
            //in start method
            return null;
        }
        try {
            UserTokenProvider p = VrApp.getBean(UserTokenProvider.class);
            UserToken token = p.getToken();
            if (token != null) {
                return token;
            }
        } catch (Exception ex) {
        }
        try {
            UserSession s = UserSession.get();
            return s == null ? null : s.getToken();
        } catch (Exception ex) {
            return null;
        }
    }

    private UserSessionInfo populateSessionInfo(UserToken t, UserSessionInfo i, boolean currentSessionAdmin, Date lastAccessedTime) {
        Integer userId = t.getUserId();
        String login = t.getUserLogin();
        if (!currentSessionAdmin && t.getRootUserId() != null) {
            userId = t.getRootUserId();
            login = t.getRootLogin();
        }
        i.setUserId(userId);
        if (i.getUserLogin() == null) {
            i.setUserLogin(login);
            i.setSessionId(t.getSessionId());
            AppUser u = userId == null ? null : findUser(userId);
            i.setUserFullTitle(u == null ? null : u.getFullTitle());
            i.setUserFullName(u == null ? null : u.getFullName());
            i.setFemale(t.isFemale());
            i.setUserTypeName(t.getUserTypeName());
            i.setDomain(t.getDomain());
            i.setLocale(t.getLocale());
            i.setIconURL(getContext().getCorePlugin().getUserIcon(t.getUserId()));
            if (currentSessionAdmin) {
                i.setIpAddress(t.getIpAddress());
                i.setClientApp(t.getClientApp());
                i.setClientAppId(t.getClientAppId());
                i.setRootUserId(t.getRootUserId());
                i.setRootLogin(t.getRootLogin());
                i.setManagedDepartments(t.getManagedDepartments());
                i.setAdmin(t.isAdmin());
                i.setConnexionTime(t.getConnexionTime());
                i.setPublicTheme(t.getPublicTheme());
                i.setPrivateTheme(t.getPrivateTheme());
                i.setDestroyed(t.isDestroyed());
                i.setProfileNames(new HashSet<>(t.getProfileCodes()));
            }
        }
        if (i.getLastAccessTime() == null || i.getLastAccessTime().compareTo(lastAccessedTime) < 0) {
            i.setLastAccessTime(lastAccessedTime);
        }
        i.incCount();
        return i;
    }

    public List<UserSessionInfo> getActiveSessions(boolean groupSessions, boolean groupAnonymous, boolean showAnonymous) {
        boolean currentSessionAdmin = getContext().getCorePlugin().isCurrentSessionAdmin();
        List<UserSessionInfo> found = new ArrayList<>();
        SessionStore sessionStore = VrApp.getBean(SessionStoreProvider.class).resolveSessionStore();
        UserSessionInfo anonymous = new UserSessionInfo();
        anonymous.setUserFullTitle("Anonymous");
        anonymous.setUserFullName("Anonymous");
        Map<Integer, UserSessionInfo> grouped = new HashMap<>();
        for (PlatformSession p : sessionStore.getAll()) {
            UserToken t = p.getToken();
            if (t != null && t.getUserId() != null) {
                Integer userId = t.getUserId();
                if (!currentSessionAdmin && t.getRootUserId() != null) {
                    userId = t.getRootUserId();
                }
                UserSessionInfo i = null;
                if (groupSessions) {
                    i = grouped.get(userId);
                    if (i == null) {
                        i = new UserSessionInfo();
                        i.setUserId(userId);
                        grouped.put(userId, i);
                        found.add(i);
                    }
                } else {
                    i = new UserSessionInfo();
                    i.setUserId(userId);
                    found.add(i);
                }
                populateSessionInfo(t, i, currentSessionAdmin, p.getLastAccessedTime());
            } else {
                if (showAnonymous && groupAnonymous) {
                    if (anonymous.getSessionId() == null) {
                        anonymous.setSessionId(p.getSessionId());
                    }
                    anonymous.setSessionId(p.getSessionId());
                    anonymous.incCount();
                    if (anonymous.getConnexionTime() == null || (p.getConnexionTime() != null && anonymous.getConnexionTime().compareTo(p.getConnexionTime()) > 0)) {
                        anonymous.setConnexionTime(p.getConnexionTime());
                    }
                    if (anonymous.getLastAccessTime() == null || (p.getLastAccessedTime() != null && anonymous.getLastAccessTime().compareTo(p.getLastAccessedTime()) < 0)) {
                        anonymous.setLastAccessTime(p.getLastAccessedTime());
                    }
                } else if (showAnonymous) {
                    UserSessionInfo i = new UserSessionInfo();
                    i.setSessionId(p.getSessionId());
                    i.setUserFullName("Anonymous");
                    i.setUserFullTitle("Anonymous");
                    i.setConnexionTime(p.getConnexionTime());
                    i.setUserTypeName("Anonymous");
                    i.setIpAddress(p.getIpAddress());
                    i.setLastAccessTime(p.getLastAccessedTime());
                    found.add(i);
                }
            }
        }
        if (showAnonymous && anonymous.getCount() > 0) {
            found.add(anonymous);
        }
        Collections.sort(found, new Comparator<UserSessionInfo>() {

            @Override
            public int compare(UserSessionInfo o1, UserSessionInfo o2) {
                Date t1 = o2.getConnexionTime();
                Date t2 = o2.getConnexionTime();
                if (t1 != t2) {
                    if (t2 == null) {
                        return -1;
                    }
                    if (t1 == null) {
                        return -1;
                    }
                    int x = t2.compareTo(t1);
                    if (x != 0) {
                        return x;
                    }
                }
                return 0;
            }
        });
        return found;

    }

    public int getActiveSessionsCount() {
        SessionStore sessionStore = VrApp.getBean(SessionStoreProvider.class).resolveSessionStore();
        return sessionStore.size();
    }

    public String getActualLogin() {
        UserPrincipal up = UPA.getPersistenceUnit().getUserPrincipal();
        if (up != null && up.getObject() instanceof AppUser) {
            AppUser u = (AppUser) up.getObject();
            return u.getLogin();
        }
        return getCurrentUserLogin();
    }

    public AppUser getCurrentUser() {
        UserToken t = getCurrentToken();
        if (t == null || t.getUserId() == null) {
            return null;
        }
        return findUser(t.getUserId());
//        return getContext().getCacheService().getList(AppUser.class).getByKey(t.getUserId())
//                getContext().getCorePlugin().findUser(t.getUserId());
    }

    public Integer getCurrentUserId() {
        UserToken s = getCurrentToken();
        return s == null ? null : s.getUserId();
    }

    public String getCurrentUserLogin() {
        UserToken s = getCurrentToken();
        return s == null ? null : s.getUserLogin();
    }

    public UserSession getCurrentSession() {
        UserSession s = UserSession.get();
        if (s != null) {
            s.setToken(getCurrentToken());
        }
        return s;
    }

    //    public boolean isSessionAdmin() {
//        UserSession us = getCurrentSession();
//        return us != null && us.isAdmin();
//    }
    public boolean isCurrentSessionAdminOrManager() {
        if (isCurrentSessionAdmin()) {
            return true;
        }
        UserToken us = getContext().getCorePlugin().getCurrentToken();
        return us.isManager();
    }

    public boolean isCurrentSessionAdminOrManagerOf(int depId) {
        if (isCurrentSessionAdmin()) {
            return true;
        }
        UserToken us = getContext().getCorePlugin().getCurrentToken();
        if (us.isManager()) {
            int[] managedDepartments = us.getManagedDepartments();
            if (managedDepartments != null) {
                for (int d : managedDepartments) {
                    if (d == depId) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCurrentSessionAdmin() {
        if (!getContext().getCorePlugin().isStarted()) {
            //in start method
            return true;
        }
        UserToken us = null;
        try {
            us = getContext().getCorePlugin().getCurrentToken();
        } catch (Exception e) {
            //session not yet created!
            return true;
        }
        UserPrincipal up = UPA.getPersistenceUnit().getUserPrincipal();
        if (up != null) {
            if (up.getName().equals(VRSecurityManager.INTERNAL_LOGIN)) {
                return true;
            }
            if (up.getObject() instanceof AppUser) {
                AppUser u = (AppUser) up.getObject();
                if (us.getUserLogin() != null && u.getLogin().equals(us.getUserLogin())) {
                    return us.isAdmin();
                }
                List<AppProfile> profiles = getContext().getCorePlugin().findProfilesByUser(u.getId());
                for (AppProfile p : profiles) {
                    if (CorePlugin.PROFILE_ADMIN.equals(p.getCode())) {
                        return true;
                    }
                }
            }
        }
        return us != null && us.isAdmin();
    }

    public boolean isCurrentAllowed(String key) {
        return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(key);
    }

    public boolean isCurrentSessionAdminOrUser(String login) {
        if (StringUtils.isBlank(login)) {
            return isCurrentSessionAdmin();
        }
        UserToken token = getContext().getCorePlugin().getCurrentToken();
//        UserSession us = getCurrentSession();
        UserPrincipal up = UPA.getPersistenceUnit().getUserPrincipal();
        if (up != null) {
            if (up.getName().equals(VRSecurityManager.INTERNAL_LOGIN)) {
                return true;
            }
            if (up.getObject() instanceof AppUser) {
                AppUser u = (AppUser) up.getObject();
                if (token.getUserLogin() != null) {
                    String login2 = token.getUserLogin();
                    if (login2.equals(login)) {
                        return true;
                    }
                    if (login2.equals(token.getUserLogin())) {
                        return token.isAdmin();
                    }
                }
                List<AppProfile> profiles = getContext().getCorePlugin().findProfilesByUser(u.getId());
                for (AppProfile p : profiles) {
                    if (CorePlugin.PROFILE_ADMIN.equals(p.getCode())) {
                        return true;
                    }
                }
                return false;
            }
        }
        if (token != null) {
            if (token.isAdmin()) {
                return true;
            }
            if (token.getUserLogin().equals(login)) {
                return true;
            }
        }
        return token != null && token.isAdmin();
    }

    public boolean isCurrentSessionAdminOrUser(int userId) {
        if (userId <= 0) {
            return isCurrentSessionAdmin();
        }
        UserToken us = getContext().getCorePlugin().getCurrentToken();
        UserPrincipal up = UPA.getPersistenceUnit().getUserPrincipal();
        if (up != null) {
            if (up.getName().equals(VRSecurityManager.INTERNAL_LOGIN)) {
                return true;
            }
            if (up.getObject() instanceof AppUser) {
                AppUser u = (AppUser) up.getObject();
                if (us.getUserLogin() != null) {
                    String login2 = u.getLogin();
                    if (u.getId() == userId) {
                        return true;
                    }
                    if (login2.equals(us.getUserLogin())) {
                        return us.isAdmin();
                    }
                }
                List<AppProfile> profiles = getContext().getCorePlugin().findProfilesByUser(u.getId());
                for (AppProfile p : profiles) {
                    if (CorePlugin.PROFILE_ADMIN.equals(p.getCode())) {
                        return true;
                    }
                }
                return false;
            }
        }
        if (us != null) {
            if (us.isAdmin()) {
                return true;
            }
            if (us.getUserId() == userId) {
                return true;
            }
        }
        return us != null && us.isAdmin();
    }

//    public boolean isCurrentSessionAdminOrContact(int contactId) {
//        if (contactId <= 0) {
//            return isCurrentSessionAdmin();
//        }
//        UserToken us = getContext().getCorePlugin().getCurrentToken();
//        UserPrincipal up = UPA.getPersistenceUnit().getUserPrincipal();
//        if (up != null) {
//            if (up.getName().equals(VRSecurityManager.INTERNAL_LOGIN)) {
//                return true;
//            }
//            if (up.getObject() instanceof AppUser) {
//                AppUser u = (AppUser) up.getObject();
//                if (us.getUserLogin() != null) {
//                    String login2 = u.getLogin();
//                    if (u.getContact() != null && u.getContact().getId() == contactId) {
//                        return true;
//                    }
//                    if (login2.equals(us.getUserLogin())) {
//                        return us.isAdmin();
//                    }
//                }
//                List<AppProfile> profiles = getContext().getCorePlugin().findProfilesByUser(u.getId());
//                for (AppProfile p : profiles) {
//                    if (CorePlugin.PROFILE_ADMIN.equals(p.getCode())) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//        }
//        if (us != null) {
//            if (us.isAdmin()) {
//                return true;
//            }
//            if (us.getUserId() != null) {
//                AppUser user = getContext().getCorePlugin().findUser(us.getUserId());
//                if (user != null && user.getContact() != null && user.getContact().getId() == contactId) {
//                    return true;
//                }
//            }
//        }
//        return us != null && us.isAdmin();
//    }
    public boolean isCurrentSessionAdminOrProfile(String profileName) {
        return isCurrentSessionAdminOrProfiles(true, profileName);
    }

    public boolean isCurrentSessionAdminOrAllOfProfiles(String... profileNames) {
        return isCurrentSessionAdminOrProfiles(true, profileNames);
    }

    public boolean isCurrentSessionAdminOrAnyOfProfiles(String... profileNames) {
        return isCurrentSessionAdminOrProfiles(false, profileNames);
    }

    public boolean isCurrentSessionAdminOrProfiles(boolean all, String... profileNames) {
        UserToken us = getContext().getCorePlugin().getCurrentToken();
        UserPrincipal up = UPA.getPersistenceUnit().getUserPrincipal();
        if ((us != null && us.isAdmin()) || isCurrentSessionAdmin()) {
            return true;
        }
        if (up == null) {
            return false;
        }
        List<AppProfile> profiles = null;
        if (profileNames == null || profileNames.length == 0) {
            return false;
        }
        for (String profileName : new HashSet<String>(Arrays.asList(profileNames))) {
            boolean cur = false;
            if (!StringUtils.isBlank(profileName)) {
                if (up.getName().equals(VRSecurityManager.INTERNAL_LOGIN)) {
                    return true;
                }
                if (up.getObject() instanceof AppUser) {
                    AppUser u = (AppUser) up.getObject();
                    if (us.getUserLogin() != null) {
                        String login2 = u.getLogin();
                        if (login2.equals(us.getUserLogin())) {
                            cur = us.isAdmin();
                        }
                    }
                    if (!cur) {
                        if (us.getProfileCodes().contains(profileName)) {
                            cur = true;
                        }
                        if (!cur) {
                            if (profiles == null) {
                                profiles = getContext().getCorePlugin().findProfilesByUser(u.getId());
                            }
                            for (AppProfile p : profiles) {
                                if (CorePlugin.PROFILE_ADMIN.equals(p.getCode())) {
                                    cur = true;
                                    break;
                                }
                                if (profileName.equals(p.getCode())) {
                                    cur = true;
                                    break;
                                }
                            }
                        }
                    }
                }

            }
            if (all) {
                if (!cur) {
                    return false;
                }
            } else {
                if (cur) {
                    return true;
                }
            }
        }
        return false;
    }

    private static class InitData {

        long now;
        AppProfile adminProfile;
        AppUser admin;
        AppUserType adminType;
        List<AppCivility> civilities;
        List<AppGender> genders;
    }

    public String getCurrentDomain() {
        return UPA.getPersistenceUnit().getName();
    }

}
