/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.fs.FileInfo;
import net.vpc.app.vainruling.core.service.fs.VrFSEntry;
import net.vpc.app.vainruling.core.service.fs.VrFSTable;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.notification.PollAware;
import net.vpc.app.vainruling.core.service.obj.AutoFilterData;
import net.vpc.app.vainruling.core.service.obj.ObjSearch;
import net.vpc.app.vainruling.core.service.plugins.*;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.security.UserSessionInfo;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.core.service.util.AppVersion;
import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.upa.*;
import net.vpc.upa.expressions.Expression;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

import net.vpc.app.vainruling.core.service.model.content.AppArticleDisposition;
import net.vpc.app.vainruling.core.service.model.content.AppArticleDispositionGroup;
import net.vpc.app.vainruling.core.service.model.content.AppArticleDispositionBundle;
import net.vpc.app.vainruling.core.service.model.content.AppArticleFile;
import net.vpc.app.vainruling.core.service.model.content.AppArticle;
import net.vpc.app.vainruling.core.service.model.content.FullArticle;
import net.vpc.app.vainruling.core.service.obj.MainPhotoProvider;
import net.vpc.app.vainruling.core.service.obj.PropertyMainPhotoProvider;
import net.vpc.common.util.MutableDate;
import net.vpc.upa.types.DataType;
import org.springframework.context.annotation.DependsOn;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin()
@DependsOn("vrApp")
public class CorePlugin {

    public static final String PATH_TEMP = "/Var/Temp";
    public static final String USER_ADMIN = "admin";
    public static final String PROFILE_ADMIN = "Admin";
    public static final String PROFILE_HEAD_OF_DEPARTMENT = "HeadOfDepartment";
    public static final Set<String> ADMIN_ENTITIES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            AppTrace.class.getSimpleName(),
            AppUser.class.getSimpleName(),
            AppProfile.class.getSimpleName(),
            AppUserProfileBinding.class.getSimpleName(),
            AppProfileRight.class.getSimpleName())
    ));
    public static final SimpleDateFormat NAME_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH");
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CorePlugin.class.getName());
    public static String FOLDER_MY_DOCUMENTS = "Mes Documents";
    public static String FOLDER_ALL_DOCUMENTS = "Tous";
    public static String FOLDER_BACK = "<Dossier Parent>";
    CorePluginBodyFileSystem bodyFileSystem = new CorePluginBodyFileSystem();
    //    private VirtualFileSystem fileSystem;
//    private String nativeFileSystemPath;
    @Autowired
    private TraceService trace;
    @Autowired
    private I18n i18n;
    @Autowired
    private CacheService cacheService;
    private boolean updatingPoll = false;
    private final Set<String> managerProfiles = new HashSet<>(Arrays.asList("Director"));
    private final CorePluginBodyPluginManager bodyPluginManager = new CorePluginBodyPluginManager();
    private final CorePluginBodySecurityManager bodySecurityManager = new CorePluginBodySecurityManager();
    private final CorePluginBodySecurityAuthenticator bodySecurityAuth = new CorePluginBodySecurityAuthenticator();
    private final CorePluginBodyDAOManager bodyDaoManager = new CorePluginBodyDAOManager();
    private final CorePluginBodyContentManager bodyContentManager = new CorePluginBodyContentManager();
    private final CorePluginBodyConfig bodyConfig = new CorePluginBodyConfig();
    private final CorePluginBody[] bodies = new CorePluginBody[]{bodyFileSystem, bodyConfig, bodySecurityAuth, bodySecurityManager, bodyDaoManager, bodyContentManager};
    private CorePluginBodyContext bodyContext;
    private boolean started = false;

    public static CorePlugin get() {
        return VrApp.getBean(CorePlugin.class);
    }

    //    private void init(){
//    }
//    public UserSession getUserSession() {
//        return VrApp.getContext().getBean(UserSession.class);
//    }
    void start() {
        try {
            //LogUtils.configure(Level.FINE,"net.vpc");
            //disable log4j imported by jxl-api
            org.apache.log4j.LogManager.getRootLogger().setLevel(org.apache.log4j.Level.OFF);

            i18n.register("i18n.dictionary");
            i18n.register("i18n.presentation");
            i18n.register("i18n.service");
//        try {
//            InitialContext c = new InitialContext();
//            c.bind("java:comp/env/datasource", VrApp.getContext().getBean("datasource"));
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
            bodyContext = new CorePluginBodyContext(this, cacheService, trace);
            List<PersistenceUnit> persistenceUnits = UPA.getPersistenceGroup().getPersistenceUnits();
            bodyPluginManager.setContext(bodyContext);
            for (CorePluginBody body : bodies) {
                body.setContext(bodyContext);
            }
            for (PersistenceUnit persistenceUnit : persistenceUnits) {
                persistenceUnit.invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        bodyPluginManager.install();

//                    for (CorePluginBody body : bodies) {
//                        body.install();
//                    }
                        bodyPluginManager.onStart();
                    }
                });
            }
        } finally {
            started = true;
        }
    }

    public boolean isStarted() {
        return started;
    }

    @Install
    private void onInstall() {
        for (CorePluginBody body : bodies) {
            body.install();
        }
    }

    @Start
    private void onStart() {
        for (CorePluginBody body : bodies) {
            body.start();
        }
    }

    public void onPoll() {
        if (!updatingPoll) {
            updatingPoll = true;
            try {
                synchronized (this) {
                    for (PollAware b : VrApp.getBeansForType(PollAware.class)) {
                        b.onPoll();
                    }
                }
            } finally {
                updatingPoll = false;
            }
        }
    }

    public AppUser findUser(String login) {
        return bodySecurityManager.findUser(login);
    }

    public AppUser findUser(AppUser user) {
        return bodySecurityManager.findUser(user);
    }

    public AppUser findUser(int id) {
        return bodySecurityManager.findUser(id);
    }

    public AppUserType findUserType(String name) {
        return bodySecurityManager.findUserType(name);
    }

    public List<AppUserType> findUserTypes() {
        return bodySecurityManager.findUserTypes();
    }

    public AppUserType findUserType(int id) {
        return bodySecurityManager.findUserType(id);
    }

    public AppProfile findProfile(int profileId) {
        return bodySecurityManager.findProfile(profileId);
    }

    public boolean createRight(String rightName, String desc) {
        return bodySecurityManager.createRight(rightName, desc);
    }

    public List<AppRightName>[] findProfileRightNamesDualList(int profileId) {
        return bodySecurityManager.findProfileRightNamesDualList(profileId);
    }

    public List<AppUser>[] findProfileUsersDualList(int profileId) {
        return bodySecurityManager.findProfileUsersDualList(profileId);
    }

    public int setProfileRights(int profileId, List<String> rightNames) {
        return bodySecurityManager.setProfileRights(profileId, rightNames);
    }

    public int setProfileUsers(int profileId, List<String> logins) {
        return bodySecurityManager.setProfileUsers(profileId, logins);
    }

    public boolean addProfileRight(int profileId, String rightName) {
        return bodySecurityManager.addProfileRight(profileId, rightName);
    }

    public boolean addProfileRight(String profileCode, String rightName) {
        return bodySecurityManager.addProfileRight(profileCode, rightName);
    }

    public boolean removeUserProfile(int userId, int profileId) {
        return bodySecurityManager.removeUserProfile(userId, profileId);
    }

    public boolean addUserProfile(int userId, String profileCode) {
        return bodySecurityManager.addUserProfile(userId, profileCode);
    }

    public boolean addUserProfile(int userId, int profileId) {
        return bodySecurityManager.addUserProfile(userId, profileId);
    }

    public boolean isUserWithProfile(int userId, String profileName) {
        return bodySecurityManager.isUserWithProfile(userId, profileName);
    }

    public Set<String> findUserRightsImmediate(int userId) {
        return bodySecurityManager.findUserRightsImmediate(userId);
    }

    public Set<String> findUserRightsAll(int userId) {
        return bodySecurityManager.findUserRightsAll(userId);
    }

    public AppProfile findOrCreateCustomProfile(String profileCode, String customType) {
        return bodySecurityManager.findOrCreateCustomProfile(profileCode, customType);
    }

    public AppProfile findOrCreateProfile(String profileName) {
        return bodySecurityManager.findOrCreateProfile(profileName);
    }

    public AppProfile findProfileByName(String profileName) {
        return bodySecurityManager.findProfileByName(profileName);
    }

    public AppProfile findProfileByCode(String profileCode) {
        return bodySecurityManager.findProfileByCode(profileCode);
    }

    public List<AppProfile> findAdministrableProfiles() {
        return bodySecurityManager.findAdministrableProfiles();
    }

    public List<AppProfile> findProfilesByUser(int userId) {
        return bodySecurityManager.findProfilesByUser(userId);
    }

    public List<AppUser> findUsersByTypeAndDepartment(int userType, int userDepartment) {
        return bodySecurityManager.findUsersByTypeAndDepartment(userType, userDepartment);
    }

    public List<AppUser> findUsersByType(int userType) {
        return bodySecurityManager.findUsersByType(userType);
    }

    public List<AppUser> findUsersByProfile(int profileId) {
        return bodySecurityManager.findUsersByProfile(profileId);
    }

    public List<AppContact> findContactsByFullTitle(String fullTitle) {
        return bodySecurityManager.findContactsByFullTitle(fullTitle);
    }

    public List<AppUser> findUsersByFullTitle(String fullTitle) {
        return bodySecurityManager.findUsersByFullTitle(fullTitle);
    }

    public List<AppContact> findContactsByFullName(String fullName) {
        return bodySecurityManager.findContactsByFullName(fullName);
    }

    public List<AppUser> findUsersByFullName(String fullName) {
        return bodySecurityManager.findUsersByFullName(fullName);
    }

    public Integer findUserIdByLogin(String login) {
        return bodySecurityManager.findUserIdByLogin(login);
    }

    public Map<String, Integer> findUserLoginToIdMap() {
        return bodySecurityManager.findUserLoginToIdMap();
    }

    public List<AppUser> findUsersByProfile(String profileName) {
        return bodySecurityManager.findUsersByProfile(profileName);
    }

    public List<AppProfile> findProfiles() {
        return bodySecurityManager.findProfiles();
    }

    public List<AppUser> findUsers() {
        return bodySecurityManager.findUsers();
    }

    public List<AppUser> findEnabledUsers(Integer userType) {
        return bodySecurityManager.findEnabledUsers(userType);
    }

    public List<AppUser> findEnabledUsers() {
        return bodySecurityManager.findEnabledUsers();
    }

    public AppUser findUser(String login, String password) {
        return bodySecurityManager.findUser(login, password);
    }

    public AppDepartment findDepartment(int id) {
        return bodyConfig.findDepartment(id);
    }

    public List<AppDepartment> findDepartments() {
        return bodyConfig.findDepartments();
    }

    public AppDepartment findDepartment(String code) {
        return bodyConfig.findDepartment(code);
    }

    public AppCivility findCivility(String t) {
        return bodyConfig.findCivility(t);
    }

    public List<AppCivility> findCivilities() {
        return bodyConfig.findCivilities();
    }

    public AppContact findContact(int id) {
        return bodyConfig.findContact(id);
    }

    public AppCompany findCompany(int id) {
        return bodyConfig.findCompany(id);
    }

    public AppCompany findCompany(String name) {
        return bodyConfig.findCompany(name);
    }

    public List<AppCompany> findCompanies() {
        return bodyConfig.findCompanies();
    }

    public AppGender findGender(String t) {
        return bodyConfig.findGender(t);
    }

    public List<AppGender> findGenders() {
        return bodyConfig.findGenders();
    }

    public <T> T findOrCreate(T o) {
        if (o instanceof AppUserType) {
            return bodyDaoManager.findOrCreate(o, "code");
        } else {
            return bodyDaoManager.findOrCreate(o);
        }
    }

    public <T> T findOrCreate(T o, String field) {
        return bodyDaoManager.findOrCreate(o, field);
    }

    public <T> List<T> filterByProfilePattern(List<T> in, Integer userId, String login, ProfilePatternFilter<T> filter) {
        return bodySecurityManager.filterByProfilePattern(in, userId, login, filter);
    }

    public boolean isUserMatchesProfileFilter(int userId, String profileExpr) {
        return bodySecurityManager.isUserMatchesProfileFilter(userId, profileExpr);
    }

    public boolean isUserMatchesProfileFilter(String userLogin, String profileExpr) {
        return bodySecurityManager.isUserMatchesProfileFilter(userLogin, profileExpr);
    }

    public boolean isUserMatchesProfileFilter(Integer userId, String login, String profile, String whereClause) {
        return bodySecurityManager.isUserMatchesProfileFilter(userId, login, profile, whereClause);
    }

    public List<String> autoCompleteUserOrProfile(String userOrProfile) {
        return bodySecurityManager.autoCompleteUserOrProfile(userOrProfile);
    }

    public List<String> autoCompleteProfileExpression(String queryExpr) {
        return bodySecurityManager.autoCompleteProfileExpression(queryExpr);
    }

    public boolean isCurrentSessionMatchesProfileFilter(String profilePattern) {
        return bodySecurityManager.isCurrentSessionMatchesProfileFilter(profilePattern);
    }

    public List<AppUser> filterUsersByProfileFilter(List<AppUser> users, String profilePattern, Integer userType) {
        return bodySecurityManager.filterUsersByProfileFilter(users, profilePattern, userType);
    }

    public List<AppUser> findUsersByProfileFilter(String profilePattern, Integer userType) {
        return bodySecurityManager.findUsersByProfileFilter(profilePattern, userType);
    }

    //    public AppUser findUserByContact(int contactId) {
//        return bodyConfig.findUserByContact(contactId);
//    }
    public AppContact findOrCreateContact(AppContact c) {
        return bodySecurityManager.findOrCreateContact(c);
    }

    public AppUser findOrCreateUser(AppUser c, String[] defaultProfiles, VrPasswordStrategy passwordStrategy) {
        return bodySecurityManager.findOrCreateUser(c, defaultProfiles, passwordStrategy);
    }

    public AppContact findContact(AppContact c) {
        return bodySecurityManager.findContact(c);
    }

    public List<AppContact> findAllContacts() {
        return UPA.getPersistenceUnit().findAll(AppContact.class);
    }

    //    public List<AppContact> findContacts(String name, String type) {
//        return bodySecurityManager.findContacts(name, type);
//    }
    public String getActualLogin() {
        return bodySecurityAuth.getActualLogin();
    }

    public AppConfig getCurrentConfig() {
        return bodyConfig.getCurrentConfig();
    }

    public AppUser addUser(CreateUserInfo userInfo) {
        return bodySecurityManager.addUser(userInfo);
    }

    public void passwd(String login, String oldPassword, String newPassword) {
        bodySecurityManager.passwd(login, oldPassword, newPassword);
    }

    public String validateProfileName(String s) {
        return bodySecurityManager.validateProfileName(s);
    }

    public AppPeriod getCurrentPeriod() {
        return bodyConfig.getCurrentPeriod();
    }

    public AppPeriod findPeriodOrMain(int id) {
        return bodyConfig.findPeriodOrMain(id);
    }

    public AppPeriod findPeriod(int id) {
        return bodyConfig.findPeriod(id);
    }

    public List<AppPeriod> findValidPeriods() {
        return bodyConfig.findValidPeriods();
    }

    public List<AppPeriod> findNavigatablePeriods() {
        return bodyConfig.findNavigatablePeriods();
    }

    public List<AppPeriod> findPeriods() {
        return bodyConfig.findPeriods();
    }

    public AppPeriod findPeriod(String name) {
        return bodyConfig.findPeriod(name);
    }

    public AppUser getCurrentUser() {
        return bodySecurityAuth.getCurrentUser();
    }

    public Integer getCurrentUserId() {
        return bodySecurityAuth.getCurrentUserId();
    }

    public String getCurrentUserLogin() {
        return bodySecurityAuth.getCurrentUserLogin();
    }

    public UserSession getCurrentSession() {
        return bodySecurityAuth.getCurrentSession();
    }

    public UserToken getCurrentToken() {
        return bodySecurityAuth.getCurrentToken();
    }

    public void logout() {
        bodySecurityAuth.logout();
    }

    public void logout(String sessionId) {
        bodySecurityAuth.logout(sessionId);
    }

    public AppUser impersonate(String login, String password) {
        return bodySecurityAuth.impersonate(login, password);
    }

    public String getCurrentDomain() {
        return bodySecurityAuth.getCurrentDomain();
    }

    public UserSessionInfo authenticate(String login, String password, String clientAppId, String clientApp) {
        return bodySecurityAuth.authenticate(login, password, clientAppId, clientApp);
    }

    public PluginInfo getPluginInfo(String bundleId) {
        return bodyPluginManager.getPluginInfo(bundleId);
    }

    public Map<String, List<String>> getPluginsAPI() {
        return bodyPluginManager.getPluginsAPI();
    }

    public List<Plugin> getPlugins() {
        return bodyPluginManager.getPlugins();
    }

    public AppVersion getAppVersion() {
        return bodyPluginManager.getAppVersion();
    }

    public MirroredPath createTempUploadFolder() {
        return bodyFileSystem.createTempUploadFolder();
    }

    public VirtualFileSystem getMyFileSystem() {
        return bodyFileSystem.getMyFileSystem();
    }

    public VirtualFileSystem getRootFileSystem() {
        return bodyFileSystem.getRootFileSystem();
    }

    public VFile getUserDocumentsFolder(final String login) {
        return bodyFileSystem.getUserDocumentsFolder(login);
    }

    public VFile getUserFolder(final String login) {
        return bodyFileSystem.getUserFolder(login);
    }

    public VFile getUserSharedFolder() {
        return bodyFileSystem.getUserSharedFolder();
    }

    public VFile getProfileFolder(final String profile) {
        return bodyFileSystem.getProfileFolder(profile);
    }

    public VFile getUserTypeFolder(int userTypeId) {
        return bodyFileSystem.getUserTypeFolder(userTypeId);
    }

    /**
     * Current User Home Folder path
     *
     * @param path
     * @return
     */
    public FileInfo getMyHomeFile(String path) {
        return bodyFileSystem.getMyHomeFile(path);
    }

    /**
     * Extended Current User Folder path (includes all Profile Folders)
     *
     * @param path
     * @return
     */
    public FileInfo getMyFile(String path) {
        return bodyFileSystem.getMyFile(path);
    }

    /**
     * Extended User Folder path (includes all Profile Folders)
     *
     * @param path
     * @return
     */
    public FileInfo getUserFile(String login, String path) {
        return bodyFileSystem.getUserFile(login, path);
    }

    /**
     * User Home Folder path
     *
     * @param path
     * @return
     */
    public FileInfo getUserHomeFile(String login, String path) {
        return bodyFileSystem.getUserHomeFile(login, path);
    }

    /**
     * Profile Home Folder path
     *
     * @param path
     * @return
     */
    public FileInfo getProfileFile(String profile, String path) {
        return bodyFileSystem.getProfileFile(profile, path);
    }

    /**
     * Root Folder path
     *
     * @param path
     * @return
     */
    public FileInfo getRootFile(String path) {
        return bodyFileSystem.getRootFile(path);
    }

    public VirtualFileSystem getMyHomeFileSystem() {
        return bodyFileSystem.getMyHomeFileSystem();
    }

    public VirtualFileSystem getUserHomeFileSystem(final String login) {
        return bodyFileSystem.getUserHomeFileSystem(login);
    }

    public VirtualFileSystem getUserFileSystem(final String login) {
        return bodyFileSystem.getUserFileSystem(login);
    }

    public VirtualFileSystem getProfileFileSystem(String profileName) {
        return bodyFileSystem.getProfileFileSystem(profileName);
    }

    public void removeUserLinkPathEntry(int userId, String linkPath) throws IOException {
        bodyFileSystem.removeUserLinkPathEntry(userId, linkPath);
    }

    public void setUserLinkPathEntry(int userId, VrFSEntry entry) throws IOException {
        bodyFileSystem.setUserLinkPathEntry(userId, entry);
    }

    public void saveUserVrFSTable(int userId, VrFSTable table) throws IOException {
        bodyFileSystem.saveUserVrFSTable(userId, table);
    }

    public VrFSTable getUserVrFSTable(int userId) {
        return bodyFileSystem.getUserVrFSTable(userId);
    }

    public long getDownloadsCount(final VFile file) {
        return bodyFileSystem.getDownloadsCount(file);
    }

    public void markDownloaded(final VFile file) {
        bodyFileSystem.markDownloaded(file);
    }

    public long getDownloadsCount(final String file) {
        return bodyFileSystem.getDownloadsCount(getRootFileSystem().get(file));
    }

    public void markDownloaded(final String file) {
        bodyFileSystem.markDownloaded(getRootFileSystem().get(file));
    }

    public VFile uploadFile(VFile baseFile, UploadedFileHandler event) throws IOException {
        return bodyFileSystem.uploadFile(baseFile, event);
    }

    public AppUser findHeadOfDepartment(int depId) {
        List<AppUser> usersByProfile = UPA.getPersistenceUnit().invokePrivileged(new Action<List<AppUser>>() {
            @Override
            public List<AppUser> run() {
                return findUsersByProfile(CorePlugin.PROFILE_HEAD_OF_DEPARTMENT);
            }
        });
        for (AppUser u : usersByProfile) {
            if (u.getDepartment() != null && u.getDepartment().getId() == depId) {
                return u;
            }
        }
        return null;
    }

    public Set<String> getManagerProfiles() {
        return managerProfiles;
    }

    public void invalidateCache() {
        CorePluginSecurity.requireAdmin();
        cacheService.invalidate();
        invalidateUserProfileMap();
        UPA.getPersistenceUnit().invalidateCache();
    }

    public AppArticle findArticle(int articleId) {
        return bodyContentManager.findArticle(articleId);
    }

    public AppArticleDisposition findArticleDisposition(int articleDispositionId) {
        return bodyContentManager.findArticleDisposition(articleDispositionId);
    }

    public List<AppArticleDispositionBundle> findArticleDispositionGroupTypes() {
        return bodyContentManager.findArticleDispositionGroupTypes();
    }

    public List<AppArticleDispositionGroup> findArticleDispositionGroups(int siteType) {
        return bodyContentManager.findArticleDispositionGroups(siteType);
    }

    public AppArticleDispositionGroup findArticleDispositionGroup(String name) {
        return bodyContentManager.findArticleDispositionGroup(name);
    }

    public AppArticleDisposition findArticleDisposition(String name) {
        return bodyContentManager.findArticleDisposition(name);
    }

    public List<AppArticleFile> findArticlesFiles(int articleId) {
        return bodyContentManager.findArticleFiles(articleId);
    }

    public List<AppArticleFile> findArticlesFiles(int[] articleIds) {
        return bodyContentManager.findArticleFiles(articleIds);
    }

    public FullArticle findFullArticle(int id) {
        return bodyContentManager.findFullArticle(id);
    }

    public void markArticleVisited(int articleId) {
        bodyContentManager.markArticleVisited(articleId);
    }

    public AppArticleDisposition findOrCreateArticleDisposition(String name, String description, String actionName) {
        return bodyContentManager.findOrCreateArticleDisposition(name, description, actionName);
    }

    //    @Deprecated
//    public List<FullArticle> findFullArticlesByCategory(String disposition) {
//        return bodyContentManager.findFullArticlesByCategory(disposition);
//    }
    public List<FullArticle> findFullArticlesByDisposition(String group, String disposition) {
        return bodyContentManager.findFullArticlesByDisposition(group, disposition);
    }

    public List<FullArticle> findFullArticlesByDisposition(int dispositionGroupId, boolean includeNoDept, final String disposition) {
        return bodyContentManager.findFullArticlesByDisposition(dispositionGroupId, includeNoDept, disposition);
    }

    public List<FullArticle> findFullArticlesByAnonymousDisposition(int dispositionGroupId, boolean includeNoDept, final String disposition) {
        return bodyContentManager.findFullArticlesByAnonymousDisposition(dispositionGroupId, includeNoDept, disposition);
    }

    public String getRSS(String rss) {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        getRSS(rss, o);
        return new String(o.toByteArray());
    }

    public void getRSS(String rss, OutputStream out) {
        bodyContentManager.getRSS(rss, out);
    }

    public String getArticleProperty(int articleId, String name) {
        return bodyContentManager.getArticleProperty(articleId, name);
    }

    public String findOrCreateArticleProperty(int articleId, String name, String defaultValue) {
        return bodyContentManager.findOrCreateArticleProperty(articleId, name, defaultValue);
    }

    public Map<String, String> getArticlesProperties(int articleId) {
        return bodyContentManager.getArticleProperties(articleId);
    }

    public int getActiveSessionsCount() {
        return bodySecurityAuth.getActiveSessionsCount();
    }

    public List<UserSessionInfo> getActiveSessions(boolean groupSessions, boolean groupAnonymous, boolean showAnonymous) {
        return bodySecurityAuth.getActiveSessions(groupSessions, groupAnonymous, showAnonymous);
    }

    public Object resolveId(String entityName, Object t) {
        return bodyDaoManager.resolveId(entityName, t);
    }

    public Object save(Object t) {
        return bodyDaoManager.save(t);
    }

    public Object save(String entityName, Object t) {
        return bodyDaoManager.save(entityName, t);
    }

    public RemoveTrace erase(String entityName, Object id) {
        return bodyDaoManager.erase(entityName, id);
    }

    public boolean isSoftRemovable(String entityName) {
        return bodyDaoManager.isSoftRemovable(entityName);
    }

    public RemoveTrace remove(Class entityType, Object id) {
        return bodyDaoManager.remove(entityType, id);
    }

    public RemoveTrace remove(String entityName, Object id) {
        return bodyDaoManager.remove(entityName, id);
    }

    public String getObjectName(String entityName, Object obj) {
        return bodyDaoManager.getObjectName(entityName, obj);
    }

    public boolean archive(String entityName, Object object) {
        return bodyDaoManager.archive(entityName, object);
    }

    public Object find(String entityName, Object id) {
        return bodyDaoManager.find(entityName, id);
    }

    public Document findDocument(String entityName, Object id) {
        return bodyDaoManager.findDocument(entityName, id);
    }

    public boolean isArchivable(String entityName) {
        return bodyDaoManager.isArchivable(entityName);
    }

    public List<Object> findAll(String entityName, Map<String, Object> criteria) {
        return bodyDaoManager.findAll(entityName, criteria);
    }

    public List<NamedId> findAllNamedIdsByRelationship(String relationshipName, Map<String, Object> criteria, Object currentInstance) {
        return bodyDaoManager.findAllNamedIdsByRelationship(relationshipName, criteria, currentInstance);
    }

    public List<NamedId> findAllNamedIds(String entityName, Map<String, Object> criteria, Expression condition) {
        return bodyDaoManager.findAllNamedIds(entityName, criteria, condition);
    }

    public List<NamedId> findAllNamedIds(String entityName, Map<String, Object> criteria, Object currentInstance) {
        return bodyDaoManager.findAllNamedIds(entityName, criteria, currentInstance);
    }

    public long findCountByFilter(String entityName, String criteria, ObjSearch objSearch, Map<String, Object> parameters) {
        return bodyDaoManager.findCountByFilter(entityName, criteria, objSearch, parameters);
    }

    public List<Object> findByFilter(String entityName, String criteria, ObjSearch objSearch, Map<String, Object> parameters) {
        return bodyDaoManager.findByFilter(entityName, criteria, objSearch, parameters);
    }

    public List<Document> findDocumentsByFilter(String entityName, String criteria, ObjSearch objSearch, String textSearch, Map<String, Object> parameters) {
        return bodyDaoManager.findDocumentsByFilter(entityName, criteria, objSearch, textSearch, parameters);
    }

    public List<Document> findDocumentsByAutoFilter(String entityName, Map<String, String> autoFilterValues, String textSearch, String exprFilter) {
        return bodyDaoManager.findDocumentsByAutoFilter(entityName, autoFilterValues, textSearch, exprFilter);
    }

    public String createSearchHelperString(String name, String entityName) {
        return bodyDaoManager.createSearchHelperString(name, entityName);
    }

    public List<Object> findAll(String entityName) {
        return bodyDaoManager.findAll(entityName);
    }

    public List<Object> findByField(Class entityName, String field, Object value) {
        return bodyDaoManager.findByField(entityName, field, value);
    }

    /**
     * load an then re save the object!
     * @param entityName
     * @param id 
     */
    public void updateObjectValue(String entityName,Object id) {
        bodyDaoManager.updateObjectValue(entityName,id);
    }
    
    public void updateObjectFormulas(String entityName,Object id) {
        bodyDaoManager.updateObjectFormulas(entityName,id);
    }

    public void updateEntityFormulas(String entityName) {
        bodyDaoManager.updateEntityFormulas(entityName);
    }
    
    public void updateAllEntitiesFormulas() {
        bodyDaoManager.updateAllEntitiesFormulas();
    }

    public List<String> getAllCompletionLists(int monitorUserId) {
        TreeSet<String> cats = new TreeSet<>();
        for (CompletionProvider bean : VrApp.getBeansForType(CompletionProvider.class)) {
            cats.addAll(bean.getCompletionLists(monitorUserId));
        }
        return new ArrayList<>(cats);
    }

    public List<CompletionInfo> findAllCompletions(int monitorUserId, String category, String objectType, Object objectId, Level minLevel) {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<List<CompletionInfo>>() {
            @Override
            public List<CompletionInfo> run() {
                List<CompletionInfo> cats = new ArrayList<>();
                for (CompletionProvider bean : VrApp.getBeansForType(CompletionProvider.class)) {
                    cats.addAll(bean.findCompletions(monitorUserId, category, objectType, objectId, minLevel));
                }
                return new ArrayList<>(cats);
            }
        });
    }

    public PersistenceUnitInfo getPersistenceUnitInfo() {
        return UPA.getPersistenceUnit().getInfo();
    }

    public Map getMessages() {
        return i18n.getResourceBundleSuite().getMap();
    }

    public List<NamedId> getFieldValues(String entityName, String fieldName, Map<String, Object> constraints, Object currentInstance) {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<List<NamedId>>() {
            @Override
            public List<NamedId> run() {
                return bodyDaoManager.getFieldValues(entityName, fieldName, constraints, currentInstance);
            }
        });
    }

    private TraceService getTrace() {
        return trace;
    }

    private I18n getI18n() {
        return i18n;
    }

    private CacheService getCacheService() {
        return cacheService;
    }

    public boolean isCurrentAllowed(String key) {
        return bodySecurityAuth.isCurrentAllowed(key);
    }

    public boolean isCurrentSessionAdmin() {
        return bodySecurityAuth.isCurrentSessionAdmin();
    }

    public boolean isCurrentSessionAdminOrProfile(String profileName) {
        return bodySecurityAuth.isCurrentSessionAdminOrProfile(profileName);
    }

    public boolean isCurrentSessionAdminOrUser(int userId) {
        return bodySecurityAuth.isCurrentSessionAdminOrUser(userId);
    }

    //    public boolean isCurrentSessionAdminOrContact(int userId) {
//        return bodySecurityAuth.isCurrentSessionAdminOrContact(userId);
//    }
    public boolean isCurrentSessionAdminOrUser(String login) {
        return bodySecurityAuth.isCurrentSessionAdminOrUser(login);
    }

    public boolean isCurrentSessionAdminOrManager() {
        return bodySecurityAuth.isCurrentSessionAdminOrManager();
    }

    public boolean isCurrentSessionAdminOrManagerOf(int depId) {
        return bodySecurityAuth.isCurrentSessionAdminOrManagerOf(depId);
    }

    public List<AppTrace> findTraceByUser(int userId, int maxRows) {
        return bodySecurityManager.findTraceByUser(userId, maxRows);
    }

    /////////////// CONFIG
    /////////////////////////////////////////
    public void setAppProperty(String propertyName, String userLogin, Object propertyValue) {
        bodyConfig.setAppProperty(propertyName, userLogin, propertyValue);
    }

    public void setEnabledAppProperty(String propertyName, String userLogin, boolean enabled) {
        bodyConfig.setEnabledAppProperty(propertyName, userLogin, enabled);
    }

    public Object getAppPropertyValue(AppProperty p) {
        return bodyConfig.getAppPropertyValue(p);
    }

    public Object getAppDataStoreValue(String propertyName, Class type, Object defaultValue) {
        return bodyConfig.getAppDataStoreValue(propertyName, type, defaultValue);
    }

    public int updateIncrementAppDataStoreInt(String propertyName) {
        return bodyConfig.updateIncrementAppDataStoreInt(propertyName);
    }

    public long updateIncrementAppDataStoreLong(String propertyName) {
        return bodyConfig.updateIncrementAppDataStoreLong(propertyName);
    }

    public long updateMaxAppDataStoreLong(String propertyName, long value, boolean doLog) {
        return bodyConfig.updateMaxAppDataStoreLong(propertyName, value, doLog);
    }

    public void setAppDataStoreValue(String propertyName, Object defaultValue) {
        bodyConfig.setAppDataStoreValue(propertyName, defaultValue);
    }

    public Object getOrCreateAppPropertyValue(String propertyName, String userLogin, Object value) {
        return bodyConfig.getOrCreateAppPropertyValue(propertyName, userLogin, value);
    }

    public Object getAppPropertyValue(String propertyName, String userLogin) {
        return bodyConfig.getAppPropertyValue(propertyName, userLogin);
    }

    public Map<String, AppProperty> getAppPropertiesMap() {
        return bodyConfig.getAppPropertiesMap();
    }

    public AppProperty getAppProperty(String propertyName, String userLogin) {
        return bodyConfig.getAppProperty(propertyName, userLogin);
    }

    public void setAppProperty(AppProperty ap) {
        bodyConfig.setAppProperty(ap);
    }

    public String getUnknownUserPhoto() {
        return bodyConfig.getUnknownUserPhoto();
    }

    public String getUnknownUserIcon() {
        return bodyConfig.getUnknownUserIcon();
    }

    public String getUserPhoto(int id) {
        return bodyConfig.getUserPhoto(id);
    }

    public String getUserIcon(int id) {
        return bodyConfig.getUserIcon(id);
    }

    public String getUserPhoto(int id, boolean icon) {
        return bodyConfig.getUserPhoto(id, icon);
    }

    public boolean existsUserPhoto(int id) {
        return bodyConfig.existsUserPhoto(id);
    }

    public String getCurrentUserPhoto() {
        return bodyConfig.getCurrentUserPhoto();
    }

    public String getCurrentUserIcon() {
        return bodyConfig.getCurrentUserIcon();
    }

    public String getDefaultUserPublicTheme() {
        return bodyConfig.getDefaultUserPublicTheme();
    }

    public String getCurrentUserPublicTheme() {
        return bodyConfig.getCurrentUserPublicTheme();
    }

    public void setCurrentUserPublicTheme(String theme) {
        bodyConfig.setCurrentUserPublicTheme(theme);
    }

    public String getDefaultUserPrivateTheme() {
        return bodyConfig.getDefaultUserPrivateTheme();
    }

    public String getCurrentUserPrivateTheme() {
        return bodyConfig.getCurrentUserPrivateTheme();
    }

    public void setCurrentUserPrivateTheme(String theme) {
        bodyConfig.setCurrentUserPrivateTheme(theme);
    }

    public void setUserTheme(int userId, String theme) {
        bodyConfig.setUserTheme(userId, theme);
    }

    public AppDepartment findOrCreateAppDepartment(String code, String name, String description) {
        return bodyConfig.findOrCreateAppDepartment(code, name, description);
    }

    public String getPreferredFileName(String name) {
        String shortName = getAppVersion().getShortName();
        String n = shortName.replace(" ", "-");
        return n + "-" + name + "-" + NAME_DATE_FORMAT.format(new Date());
    }

    public boolean isAllowed(String key) {
        return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(key);
    }

    public boolean hasProfile(String key) {
        return getCurrentToken().isAdmin() || getCurrentToken().getProfileCodes().contains(key);
    }

    public String[] resolveLogin(String login0) {
        String login = StringUtils.trim(login0);
        String domain = null;
        if (login.contains("@")) {
            int i = login.indexOf('@');
            domain = i == login.length() - 1 ? "" : login.substring(i + 1);
            login = login.substring(0, i);
        }
        if (StringUtils.isEmpty(domain)) {
            domain = "main";
        }
        return new String[]{domain, login};
    }

    public AutoFilterData getEntityAutoFilter(String entityName, String autoFilterName) {
        return bodyDaoManager.getEntityAutoFilter(entityName, autoFilterName);
    }

    public AutoFilterData[] getEntityAutoFilters(String entityName) {
        return bodyDaoManager.getEntityAutoFilters(entityName);
    }

    //    public NamedId getEntityAutoFilterDefaultSelectedValue(String entityName, String autoFilterName) {
//        return bodyDaoManager.getEntityAutoFilterDefaultSelectedValue(entityName, autoFilterName);
//    }
    public List<NamedId> getEntityAutoFilterValues(String entityName, String autoFilterName) {
        return bodyDaoManager.getEntityAutoFilterValues(entityName, autoFilterName);
    }

    public List<NamedId> getFieldValues(String entityName, String fieldName) {
        return bodyDaoManager.getFieldValues(entityName, fieldName);
    }

    public List<NamedId> getDataTypeValues(DataType type) {
        return bodyDaoManager.getDataTypeValues(type);
    }

    public String createEntityAutoFilterExpression(String entityName, String autoFilterName, Map<String, Object> parameters, String paramPrefix, String selectedString) {
        return bodyDaoManager.createEntityAutoFilterExpression(entityName, autoFilterName, parameters, paramPrefix, selectedString);
    }

    public DataType getEntityAutoFilterDataType(String entityName, String autoFilterName) {
        return bodyDaoManager.getEntityAutoFilterDataType(entityName, autoFilterName);
    }

    public MainPhotoProvider getEntityMainPhotoProvider(String entityName) {
        Entity entity = UPA.getPersistenceUnit().getEntity(entityName);
        MainPhotoProvider oldProvider = entity.getProperties().getObject("cache.ui.main-photo-provider");
        if (oldProvider != null) {
            return oldProvider;
        }
        boolean noProvider = entity.getProperties().getBoolean("cache.ui.main-photo-provider.null", false);
        if (noProvider) {
            return null;
        }
        MainPhotoProvider mainPhotoProvider = null;
        mainPhotoProvider = null;
        String p = entity.getProperties().getString("ui.main-photo-property");
        String d = entity.getProperties().getString("ui.main-photo-property.default");
        if (!StringUtils.isEmpty(p)) {
            mainPhotoProvider = new PropertyMainPhotoProvider(p, d);
        } else {
            p = StringUtils.trimToNull(entity.getProperties().getString("ui.main-photo-provider"));
            if (!StringUtils.isEmpty(p)) {
                try {
                    mainPhotoProvider = (MainPhotoProvider) Class.forName(p).newInstance();
                } catch (Exception e) {
                    log.log(Level.SEVERE, " Unable to create Main Photo provider for entity " + entityName + " as type " + p, e);
                }
            }
        }
        if (mainPhotoProvider == null) {
            entity.getProperties().setBoolean("cache.ui.main-photo-provider.null", true);
        } else {
            entity.getProperties().setObject("cache.ui.main-photo-provider", mainPhotoProvider);
        }
        return mainPhotoProvider;
    }

    public boolean isEnabledMainPhoto(String entityName) {
        return getEntityMainPhotoProvider(entityName) != null;
    }

    public String[] getMainPhotoPathList(String entityName, Object[] ids) {
        MainPhotoProvider p = getEntityMainPhotoProvider(entityName);
        if (p == null) {
            return null;
        }
        String[] r = new String[ids.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = p.getMainPhotoPath(ids[i], null);
        }
        return r;
    }

    public String[] getMainIconPathList(String entityName, Object[] ids) {
        MainPhotoProvider p = getEntityMainPhotoProvider(entityName);
        if (p == null) {
            return null;
        }
        String[] r = new String[ids.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = p.getMainIconPath(ids[i], null);
        }
        return r;
    }

    public String getMainPhotoPath(String entityName, Object id, Object valueOrNull) {
        MainPhotoProvider p = getEntityMainPhotoProvider(entityName);
        if (p == null) {
            return null;
        }
        return p.getMainPhotoPath(id, valueOrNull);
    }

    public String getMainIconPath(String entityName, Object id, Object valueOrNull) {
        MainPhotoProvider p = getEntityMainPhotoProvider(entityName);
        if (p == null) {
            return null;
        }
        return p.getMainIconPath(id, valueOrNull);
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    private Set<String> getInaccessibleEntitiesSet() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Set<String> set = pu.getProperties().getObject("InaccessibleEntities");
        if (set == null) {
            set = new HashSet<>();
            pu.getProperties().setObject("InaccessibleEntities", set);
        }
        return set;
    }

    public boolean isInaccessibleEntity(String entity) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return getInaccessibleEntitiesSet().contains(pu.getEntity(entity).getName());
    }

    public void addInaccessibleEntity(String entity) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        getInaccessibleEntitiesSet().add(pu.getEntity(entity).getName());
    }

    public void removeInaccessibleEntity(String entity) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        getInaccessibleEntitiesSet().remove(pu.getEntity(entity).getName());
    }

    public boolean isInaccessibleEntity(Class entity) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return getInaccessibleEntitiesSet().contains(pu.getEntity(entity).getName());
    }

    public void removeInaccessibleEntities(Class... entities) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        final Set<String> s = getInaccessibleEntitiesSet();
        for (Class entity : entities) {
            s.remove(pu.getEntity(entity).getName());
        }
    }

    public void addInaccessibleEntities(Class... entities) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        final Set<String> s = getInaccessibleEntitiesSet();
        for (Class entity : entities) {
            s.add(pu.getEntity(entity).getName());
        }
    }

    public void removeInaccessibleEntities(String... entities) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        final Set<String> s = getInaccessibleEntitiesSet();
        for (String entity : entities) {
            s.remove(pu.getEntity(entity).getName());
        }
    }

    public void addInaccessibleEntities(String... entities) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        final Set<String> s = getInaccessibleEntitiesSet();
        for (String entity : entities) {
            s.add(pu.getEntity(entity).getName());
        }
    }

    public void addInaccessibleEntity(Class entity) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        getInaccessibleEntitiesSet().add(pu.getEntity(entity).getName());
    }

    public void removeInaccessibleEntity(Class entity) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        getInaccessibleEntitiesSet().remove(pu.getEntity(entity).getName());
    }

    private Set<String> getInaccessibleComponentsSet() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Set<String> set = pu.getProperties().getObject("InaccessibleComponents");
        if (set == null) {
            set = new HashSet<>();
            pu.getProperties().setObject("InaccessibleComponents", set);
        }
        return set;
    }

    public boolean isInaccessibleComponent(String component) {
        return getInaccessibleComponentsSet().contains(component);
    }

    public void addInaccessibleComponent(String component) {
        CorePluginSecurity.requireAdmin();
        getInaccessibleComponentsSet().add(component);
    }

    public void removeInaccessibleComponent(String component) {
        CorePluginSecurity.requireAdmin();
        getInaccessibleComponentsSet().remove(component);
    }

    public void addInaccessibleComponents(String... components) {
        CorePluginSecurity.requireAdmin();
        final Set<String> s = getInaccessibleComponentsSet();
        for (String component : components) {
            s.add(component);
        }

    }

    public void removeInaccessibleComponents(String... components) {
        CorePluginSecurity.requireAdmin();
        final Set<String> s = getInaccessibleComponentsSet();
        for (String component : components) {
            s.remove(component);
        }
    }

    public String resolveLoginProposal(String fn, String ln) {
        return bodySecurityManager.resolveLoginProposal(fn, ln);
    }

    public String resolvePasswordProposal(AppUser user) {
        return VrPasswordStrategyRandom.INSTANCE.generatePassword(user);
    }

    public List<Date> findLatestLogins(String user, boolean successfulOnly, boolean daysOnly, boolean includeToday, int maxDays) {
        CorePluginSecurity.requireUser(user);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (maxDays <= 0) {
            maxDays = 10;
        }
        //AppTrace t;t.
        List<AppTrace> t = pu.createQuery("Select o from AppTrace o where o.module=:module and (o.action=:action or o.action=:action2) and o.user=:user and o.time>=:firstDay order by o.time desc")
                .setParameter("module", "/System/Access")
                .setParameter("action", "System.login")
                .setParameter("action2", successfulOnly ? "System.login" : "System.login.failed")
                .setParameter("firstDay", new Timestamp(new MutableDate().addDaysOfYear(-maxDays).getTimeInMillis()))
                .setParameter("user", user)
                .getResultList();
        List<Date> dates = new ArrayList<>();
        net.vpc.upa.types.Date today = new net.vpc.upa.types.Date();
        for (AppTrace appTrace : t) {
            Date c = appTrace.getTime();
            if (c != null) {
                if (daysOnly) {
                    c = new net.vpc.upa.types.Date(c);
                }
                if (includeToday || !new net.vpc.upa.types.Date(c).equals(today)) {
                    dates.add(c);
                }
            }
        }
        Collections.sort(dates);
        Collections.reverse(dates);
        return dates;
    }

    public Map<Date, Integer> findLatestDayLoginsCount(String user, boolean successfulOnly, boolean includeToday, int maxDays) {
        CorePluginSecurity.requireUser(user);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (maxDays <= 0) {
            maxDays = 10;
        }
        //AppTrace t;t.
        List<AppTrace> t = pu.createQuery("Select o from AppTrace o where o.module=:module and (o.action=:action or o.action=:action2) and o.user=:user and o.time>=:firstDay order by o.time desc")
                .setParameter("module", "/System/Access")
                .setParameter("action", "System.login")
                .setParameter("action2", successfulOnly ? "System.login" : "System.login.failed")
                .setParameter("firstDay", new Timestamp(new MutableDate().addDaysOfYear(-maxDays).getTimeInMillis()))
                .setParameter("user", user)
                .getResultList();
        Map<Date, Integer> dates = new TreeMap<>(Collections.reverseOrder());
        net.vpc.upa.types.Date today = new net.vpc.upa.types.Date();
        for (AppTrace appTrace : t) {
            Date c = appTrace.getTime();
            if (c != null) {
                c = new net.vpc.upa.types.Date(c);
                if (includeToday || !new net.vpc.upa.types.Date(c).equals(today)) {
                    Integer i = dates.get(c);
                    if (i == null) {
                        i = 0;
                    }
                    i++;
                    dates.put(c, i);
                }
            }
        }
        return dates;
    }

    public UserProfileMap getUserProfileMap() {
        return bodySecurityManager.getUserProfileMap();
    }

    public void invalidateUserProfileMap() {
        bodySecurityManager.invalidateUserProfileMap();
    }
}
