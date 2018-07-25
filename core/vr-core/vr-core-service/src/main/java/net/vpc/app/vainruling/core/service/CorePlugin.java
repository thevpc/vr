/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.model.AppEvent;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

import net.vpc.app.vainruling.core.service.model.content.ArticlesDisposition;
import net.vpc.app.vainruling.core.service.model.content.ArticlesDispositionGroup;
import net.vpc.app.vainruling.core.service.model.content.ArticlesDispositionGroupType;
import net.vpc.app.vainruling.core.service.model.content.ArticlesFile;
import net.vpc.app.vainruling.core.service.model.content.ArticlesItem;
import net.vpc.app.vainruling.core.service.model.content.FullArticle;
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
    public static final Set<String> ADMIN_ENTITIES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Trace", "User", "UserProfile", "UserProfileBinding", "UserProfileRight")));
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

    public static CorePlugin get() {
        return VrApp.getBean(CorePlugin.class);
    }

    //    private void init(){
//    }
//    public UserSession getUserSession() {
//        return VrApp.getContext().getBean(UserSession.class);
//    }
    void start() {
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

    public boolean userRemoveProfile(int userId, int profileId) {
        return bodySecurityManager.userRemoveProfile(profileId, profileId);
    }

    public boolean userAddProfile(int userId, String profileCode) {
        return bodySecurityManager.userAddProfile(userId, profileCode);
    }

    public boolean userAddProfile(int userId, int profileId) {
        return bodySecurityManager.userAddProfile(userId, profileId);
    }

    public boolean userHasProfile(int userId, String profileName) {
        return bodySecurityManager.userHasProfile(userId, profileName);
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
        return bodyDaoManager.findOrCreate(o);
    }

    public <T> T findOrCreate(T o, String field) {
        return bodyDaoManager.findOrCreate(o, field);
    }

    public <T> List<T> filterByProfilePattern(List<T> in, Integer userId, String login, ProfilePatternFilter<T> filter) {
        return bodySecurityManager.filterByProfilePattern(in, userId, login, filter);
    }

    public boolean userMatchesProfileFilter(int userId, String profileExpr) {
        return bodySecurityManager.userMatchesProfileFilter(userId, profileExpr);
    }

    public boolean userMatchesProfileFilter(String userLogin, String profileExpr) {
        return bodySecurityManager.userMatchesProfileFilter(userLogin, profileExpr);
    }

    public boolean userMatchesProfileFilter(Integer userId, String login, String profile, String whereClause) {
        return bodySecurityManager.userMatchesProfileFilter(userId, login, profile, whereClause);
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

    public List<AppUser> filterUsersBysContacts(List<AppContact> users) {
        return bodySecurityManager.filterUsersBysContacts(users);
    }

    public List<AppContact> filterContactsByProfileFilter(List<AppContact> contacts, String profilePattern) {
        return bodySecurityManager.filterContactsByProfileFilter(contacts, profilePattern);
    }

    public List<AppUser> findUsersByProfileFilter(String profilePattern, Integer userType) {
        return bodySecurityManager.findUsersByProfileFilter(profilePattern, userType);
    }

    public AppUser findUserByContact(int contactId) {
        return bodyConfig.findUserByContact(contactId);
    }

    public AppContact findOrCreateContact(AppContact c) {
        return bodySecurityManager.findOrCreateContact(c);
    }

    public AppContact findContact(AppContact c) {
        return bodySecurityManager.findContact(c);
    }

    public List<AppContact> findAllContacts() {
        return UPA.getPersistenceUnit().findAll(AppContact.class);
    }

    public List<AppContact> findContacts(String name, String type) {
        return bodySecurityManager.findContacts(name, type);
    }

    public String getActualLogin() {
        return bodySecurityAuth.getActualLogin();
    }

    public AppConfig getCurrentConfig() {
        return bodyConfig.getCurrentConfig();
    }

    public AppUser createUser(AppContact contact, int userTypeId, int departmentId, boolean attachToExistingUser, String[] defaultProfiles, VrPasswordStrategy passwordStrategy) {
        return bodySecurityManager.createUser(contact, userTypeId, departmentId, attachToExistingUser, defaultProfiles, passwordStrategy);
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

    public FileInfo getMyHomeFile(String path) {
        return bodyFileSystem.getMyHomeFile(path);
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

    public VFile uploadFile(VFile baseFile, UploadedFileHandler event) throws IOException {
        return bodyFileSystem.uploadFile(baseFile, event);
    }

    public AppUser findHeadOfDepartment(int depId) {
        for (AppUser u : findUsersByProfile(CorePlugin.PROFILE_HEAD_OF_DEPARTMENT)) {
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
    }

    public ArticlesItem findArticle(int articleId) {
        return bodyContentManager.findArticle(articleId);
    }

    public ArticlesDisposition findArticleDisposition(int articleDispositionId) {
        return bodyContentManager.findArticleDisposition(articleDispositionId);
    }

    public List<ArticlesDispositionGroupType> findArticleDispositionGroupTypes() {
        return bodyContentManager.findArticleDispositionGroupTypes();
    }

    public List<ArticlesDispositionGroup> findArticleDispositionGroups(int siteType) {
        return bodyContentManager.findArticleDispositionGroups(siteType);
    }

    public ArticlesDispositionGroup findArticleDispositionGroup(String name) {
        return bodyContentManager.findArticleDispositionGroup(name);
    }

    public ArticlesDisposition findArticleDisposition(String name) {
        return bodyContentManager.findArticleDisposition(name);
    }

    public List<ArticlesFile> findArticlesFiles(int articleId) {
        return bodyContentManager.findArticlesFiles(articleId);
    }

    public List<ArticlesFile> findArticlesFiles(int[] articleIds) {
        return bodyContentManager.findArticlesFiles(articleIds);
    }

    public FullArticle findFullArticle(int id) {
        return bodyContentManager.findFullArticle(id);
    }

    public void markArticleVisited(int articleId) {
        bodyContentManager.markArticleVisited(articleId);
    }

    public ArticlesDisposition findOrCreateDisposition(String name, String description, String actionName) {
        return bodyContentManager.findOrCreateDisposition(name, description, actionName);
    }

    @Deprecated
    public List<FullArticle> findFullArticlesByCategory(String disposition) {
        return bodyContentManager.findFullArticlesByCategory(disposition);
    }

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

    public String getArticlesProperty(String value) {
        return bodyContentManager.getArticlesProperty(value);
    }

    public String getArticlesPropertyOrCreate(String value, String defaultValue) {
        return bodyContentManager.getArticlesPropertyOrCreate(value, defaultValue);
    }

    public Map<String, String> getArticlesProperties() {
        return bodyContentManager.getArticlesProperties();
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

    public Object save(String entityName, Object t) {
        return bodyDaoManager.save(entityName, t);
    }

    public RemoveTrace erase(String entityName, Object id) {
        return bodyDaoManager.erase(entityName, id);
    }

    public boolean isSoftRemovable(String entityName) {
        return bodyDaoManager.isSoftRemovable(entityName);
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

    public String createSearchHelperString(String name, String entityName) {
        return bodyDaoManager.createSearchHelperString(name, entityName);
    }

    public List<Object> findAll(String entityName) {
        return bodyDaoManager.findAll(entityName);
    }

    public List<Object> findByField(Class entityName, String field, Object value) {
        return bodyDaoManager.findByField(entityName, field, value);
    }

    public void updateEntityFormulas(String entityName) {
        bodyDaoManager.updateEntityFormulas(entityName);
    }

    public List<String> getAllCompletionLists(int monitorUserId) {
        TreeSet<String> cats = new TreeSet<>();
        for (CompletionProvider bean : VrApp.getBeansForType(CompletionProvider.class)) {
            cats.addAll(bean.getCompletionLists(monitorUserId));
        }
        return new ArrayList<>(cats);
    }

    public List<CompletionInfo> findAllCompletions(int monitorUserId, String category, String objectType, Object objectId, Level minLevel) {
        List<CompletionInfo> cats = new ArrayList<>();
        for (CompletionProvider bean : VrApp.getBeansForType(CompletionProvider.class)) {
            cats.addAll(bean.findCompletions(monitorUserId, category, objectType, objectId, minLevel));
        }
        return new ArrayList<>(cats);
    }

    public PersistenceUnitInfo getPersistenceUnitInfo() {
        return UPA.getPersistenceUnit().getInfo();
    }

    public Map getMessages() {
        return i18n.getResourceBundleSuite().getMap();
    }

    public List<AutoFilterData> getEntityFilters(String entityName) {
        return bodyDaoManager.getEntityFilters(entityName);
    }

    public List<NamedId> getFieldValues(String entityName, String fieldName, Map<String, Object> constraints, Object currentInstance) {
        return bodyDaoManager.getFieldValues(entityName, fieldName, constraints, currentInstance);
    }

    public List<AppEvent> findAllEventsByCurrentMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 00);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 00);

        c.set(Calendar.DAY_OF_MONTH, 1);
        Date d1 = c.getTime();

        c.add(Calendar.MONTH, 1);
        Date d2 = c.getTime();

        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select e from AppEvent e where e.beginDate >=:d1 and e.beginDate < :d2 ")
                .setParameter("d1", d1)
                .setParameter("d2", d2)
                .getResultList();
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

    public boolean isCurrentSessionAdminOrContact(int userId) {
        return bodySecurityAuth.isCurrentSessionAdminOrContact(userId);
    }

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
        return getCurrentToken().getProfileNames().contains(key);
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
}
