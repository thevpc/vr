/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service;

import net.thevpc.app.vainruling.VrEditorSearch;
import net.thevpc.app.vainruling.VrEntityName;
import net.thevpc.app.vainruling.VrStart;
import net.thevpc.app.vainruling.core.service.model.*;
import net.thevpc.app.vainruling.core.service.plugins.Plugin;
import net.thevpc.app.vainruling.core.service.plugins.PluginInfo;
import net.thevpc.app.vainruling.core.service.util.*;
import net.thevpc.app.vainruling.VrPlugin;
import net.thevpc.app.vainruling.core.service.fs.MirroredPath;
import net.thevpc.app.vainruling.core.service.cache.CacheService;
import net.thevpc.app.vainruling.core.service.fs.FileInfo;
import net.thevpc.app.vainruling.core.service.util.AppVersion;
import net.thevpc.app.vainruling.core.service.model.*;
import net.thevpc.app.vainruling.core.service.editor.AutoFilterData;
import net.thevpc.app.vainruling.core.service.plugins.*;
import net.thevpc.app.vainruling.core.service.security.UserSession;
import net.thevpc.app.vainruling.core.service.security.UserSessionInfo;
import net.thevpc.app.vainruling.core.service.security.UserToken;
import net.thevpc.app.vainruling.core.service.util.*;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.vfs.VFile;
import net.thevpc.common.vfs.VirtualFileSystem;
import net.thevpc.upa.*;
import net.thevpc.upa.expressions.Expression;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import net.thevpc.app.vainruling.core.service.model.content.AppArticleDisposition;
import net.thevpc.app.vainruling.core.service.model.content.AppArticleDispositionGroup;
import net.thevpc.app.vainruling.core.service.model.content.AppArticleDispositionBundle;
import net.thevpc.app.vainruling.core.service.model.content.AppArticleFile;
import net.thevpc.app.vainruling.core.service.model.content.AppArticle;
import net.thevpc.common.time.MutableDate;
import net.thevpc.upa.types.DataType;
import org.springframework.context.annotation.DependsOn;
import net.thevpc.app.vainruling.core.service.model.content.DefaultVrContentText;
import net.thevpc.app.vainruling.core.service.model.content.ArticlesDispositionStrict;
import net.thevpc.app.vainruling.core.service.model.strict.AppUserStrict;
import net.thevpc.app.vainruling.VrPollService;
import net.thevpc.app.vainruling.VrEditorMainPhotoProvider;
import net.thevpc.app.vainruling.VrCompletionService;
import net.thevpc.app.vainruling.VrCompletionInfo;
import net.thevpc.app.vainruling.VrInstall;
import net.thevpc.app.vainruling.core.service.model.content.VrContentTextConfig;
import net.thevpc.app.vainruling.core.service.content.VrContentText;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin()
@DependsOn("vrApp")
public class CorePlugin {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(CorePlugin.class.getName());

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
    private final CorePluginBodyLogManager logConfig = new CorePluginBodyLogManager();
    private final CorePluginBody[] bodies = new CorePluginBody[]{
        bodyFileSystem, bodyConfig, bodySecurityAuth, bodySecurityManager,
        bodyDaoManager, bodyContentManager,
        logConfig
    };
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
                for (VrEditorMainPhotoProvider bean : VrApp.getBeansForType(VrEditorMainPhotoProvider.class)) {
                    Class bc = PlatformReflector.getTargetClass(bean);
                    VrEntityName fe = (VrEntityName) bc.getAnnotation(VrEntityName.class);
                    if (fe == null) {
                        LOG.log(Level.SEVERE, "MainPhotoProvider implementation " + bc.getName() + " must declare @ForEntity annotation. Ignored");
                    } else {
                        Entity entity = persistenceUnit.findEntity(fe.value());
                        if (entity == null) {
                            LOG.log(Level.SEVERE, "MainPhotoProvider implementation " + bc.getName() + " ignored for unknown entity " + persistenceUnit.getName() + "/" + fe.value());
                        } else {
                            entity.getProperties().setObject("cache.ui.main-photo-provider", (VrEditorMainPhotoProvider) bean);
                            entity.getProperties().setString("ui.main-photo-provider", bc.getName());
                        }
                    }
                }
                for (VrEditorSearch bean : VrApp.getBeansForType(VrEditorSearch.class)) {
                    Class bc = PlatformReflector.getTargetClass(bean);
                    VrEntityName fe = (VrEntityName) bc.getAnnotation(VrEntityName.class);
                    String beanName = VrUtils.getBeanName(bean);
                    if (fe == null || StringUtils.isBlank(fe.value()) || fe.value().equals("*")) {
                        bodyDaoManager.addEntityEditorSearch("", beanName);
                    } else {
                        bodyDaoManager.addEntityEditorSearch(fe.value(), beanName);
                        Entity entity = persistenceUnit.getEntity(fe.value());
                        entity.getProperties().setString(UIConstants.ENTITY_TEXT_SEARCH_FACTORY,
                                VrUtils.stringSetAdd(entity.getProperties().getString(UIConstants.ENTITY_TEXT_SEARCH_FACTORY), beanName, ",")
                        );
                    }

                }
            }

        } finally {
            started = true;
        }
    }

    public boolean isStarted() {
        return started;
    }

    @VrInstall
    private void onInstall() {
        for (CorePluginBody body : bodies) {
            body.install();
        }
    }

    @VrStart
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
                    for (VrPollService b : VrApp.getBeansForType(VrPollService.class)) {
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

    public AppUser findUser(Integer id) {
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

    public AppRightName addProfileRightName(String rightName, String desc) {
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

    public boolean addProfileRights(int profileId, String... rightNames) {
        return bodySecurityManager.addProfileRights(profileId, rightNames);
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

    public List<AppUser> findEnabledUsers(Integer userType, Integer department) {
        return bodySecurityManager.findEnabledUsers(userType, department);
    }

    public List<AppUser> findEnabledUsers() {
        return bodySecurityManager.findEnabledUsers(null, null);
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

    public List<String> autoCompleteUserLogin(String queryExpr) {
        return bodySecurityManager.autoCompleteUserLogin(queryExpr);
    }

    public boolean isCurrentSessionMatchesProfileFilter(String profilePattern) {
        return bodySecurityManager.isCurrentSessionMatchesProfileFilter(profilePattern);
    }

    public List<AppUser> filterUsersByProfileFilter(List<AppUser> users, String profilePattern, Integer userType, Integer department) {
        return bodySecurityManager.filterUsersByProfileFilter(users, profilePattern, userType, department);
    }

    public List<AppUser> findUsersByProfileFilter(String profilePattern, Integer userType, Integer department) {
        return bodySecurityManager.findUsersByProfileFilter(profilePattern, userType, department);
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

    public int getCurrentUserIdFF() {
        Integer a = bodySecurityAuth.getCurrentUserId();
        return a == null ? -1 : a.intValue();
    }

    public Integer getCurrentUserId() {
        return bodySecurityAuth.getCurrentUserId();
    }

    public String getCurrentUserLoginOrAnonymous() {
        String s = getCurrentUserLogin();
        if (s == null || s.isEmpty()) {
            return "<anonymous>";
        }
        return s;
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

    public void saveFsSharing(AppFsSharing entry) {
        bodyFileSystem.saveFsSharing(entry);
    }

    public void removeFsSharing(int id) {
        bodyFileSystem.removeFsSharing(id);
    }

    public List<AppFsSharing> findFsSharings(Integer userId, String mountPath, String sharedPath) {
        return bodyFileSystem.findFsSharings(userId, mountPath, sharedPath);
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

    public VrContentText findFullArticle(int id, VrContentTextConfig config) {
        return bodyContentManager.findFullArticle(id, config);
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
    public List<VrContentText> findFullArticlesByDisposition(String group, String disposition, VrContentTextConfig config) {
        return bodyContentManager.findFullArticlesByDisposition(null, group, disposition, config);
    }

    public List<VrContentText> findFullArticlesByDisposition(Integer userId, String group, String disposition, VrContentTextConfig config) {
        return bodyContentManager.findFullArticlesByDisposition(userId, group, disposition, config);
    }

    public List<VrContentText> findFullArticlesByDisposition(int dispositionGroupId, boolean includeNoDept, final String disposition, VrContentTextConfig config) {
        return bodyContentManager.findFullArticlesByDisposition(null, dispositionGroupId, includeNoDept, disposition, config);
    }

    public List<VrContentText> findFullArticlesByDisposition(Integer userId, Integer dispositionGroupId, boolean includeNoDept, final String disposition, VrContentTextConfig config) {
        return bodyContentManager.findFullArticlesByDisposition(userId, dispositionGroupId, includeNoDept, disposition, config);
    }

    public List<VrContentText> findFullArticlesByAnonymousDisposition(Integer dispositionGroupId, boolean includeNoDept, final String disposition, VrContentTextConfig config) {
        return bodyContentManager.findFullArticlesByAnonymousDisposition(dispositionGroupId, includeNoDept, disposition, config);
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

    public List<NamedId> findAllNamedIds(String entityName, String criteria, String textSearchType, String textSearchExpression, Map<String, Object> parameters) {
        return bodyDaoManager.findAllNamedIds(entityName, criteria, textSearchType, textSearchExpression, parameters);
    }

    public List<Document> findAllDocuments(String entityName, String[] fields, String criteria, String textSearchType, String textSearchExpression, Map<String, Object> parameters) {
        return bodyDaoManager.findAllDocuments(entityName, fields, criteria, textSearchType, textSearchExpression, parameters);
    }

    public long findCountByFilter(String entityName, String criteria, String textSearchType, String textSearchExpression, Map<String, Object> parameters) {
        return bodyDaoManager.findCountByFilter(entityName, criteria, textSearchType, textSearchExpression, parameters);
    }

//    public List<Object> findByFilter(String entityName, String criteria, ObjSearch objSearch, Map<String, Object> parameters) {
//        return bodyDaoManager.findByFilter(entityName, criteria, objSearch, parameters);
//    }
    public List<Document> findDocumentsByFilter(String entityName, String criteria, String searchType, String textSearch, Map<String, Object> parameters) {
        return bodyDaoManager.findDocumentsByFilter(entityName, criteria, searchType, textSearch, parameters);
    }

    public List<Document> findDocumentsByAutoFilter(String entityName, Map<String, String> autoFilterValues, String textSearch, String exprFilter) {
        return bodyDaoManager.findDocumentsByAutoFilter(entityName, autoFilterValues, textSearch, exprFilter);
    }

    public VrEditorSearch getEditorSearch(String beanName) {
        return bodyDaoManager.getEditorSearch(beanName);
    }

    public List<VrEditorSearch> findEntityEditorSearchs(String entityName) {
        return bodyDaoManager.findEntityEditorSearchs(entityName);
    }

    public List<Object> findAll(String entityName) {
        return bodyDaoManager.findAll(entityName);
    }

    public List<Object> findByField(Class entityName, String field, Object value) {
        return bodyDaoManager.findByField(entityName, field, value);
    }

    /**
     * load an then re save the object!
     *
     * @param entityName
     * @param id
     */
    public void updateObjectValue(String entityName, Object id) {
        bodyDaoManager.updateObjectValue(entityName, id);
    }

    public void updateObjectFormulas(String entityName, Object id) {
        bodyDaoManager.updateObjectFormulas(entityName, id);
    }

    public void updateEntityFormulas(String entityName) {
        bodyDaoManager.updateEntityFormulas(entityName);
    }

    public void updateAllEntitiesFormulas() {
        bodyDaoManager.updateAllEntitiesFormulas();
    }

    public List<String> getAllCompletionLists(int monitorUserId) {
        TreeSet<String> cats = new TreeSet<>();
        for (VrCompletionService bean : VrApp.getBeansForType(VrCompletionService.class)) {
            cats.addAll(bean.getCompletionLists(monitorUserId));
        }
        return new ArrayList<>(cats);
    }

    public List<VrContentText> findAllCompletionFullArticles(int monitorUserId, String disposition, String category, String objectType, Object objectId, Level minLevel) {
        List<VrContentText> list = new ArrayList<>();
        int id = 1;
        ArticlesDispositionStrict dispo = new ArticlesDispositionStrict();
        dispo.setName(disposition);
        dispo.setEnabled(true);
        List<VrCompletionInfo> allCompletions = findAllCompletions(monitorUserId, category, objectType, objectId, minLevel);
        for (VrCompletionInfo c : allCompletions) {
            DefaultVrContentText aa = (DefaultVrContentText) convert(c, dispo);
            aa.setId(id);
            list.add(aa);
            id++;
        }
        return list;
    }

    public VrContentText convert(VrCompletionInfo x, ArticlesDispositionStrict dispo) {
        DefaultVrContentText a = new DefaultVrContentText();
        a.setContent(x.getContent());
        a.setTitle(x.getMessage());
        a.setCategories(dispo == null ? new String[0] : new String[]{dispo.getName()});
        AppUserStrict sender = new AppUserStrict();
        sender.setFullName("Système");
        a.setUser(sender);
        return a;
    }

    public List<VrCompletionInfo> findAllCompletions(int monitorUserId, String category, String objectType, Object objectId, Level minLevel) {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<List<VrCompletionInfo>>() {
            @Override
            public List<VrCompletionInfo> run() {
                List<VrCompletionInfo> cats = new ArrayList<>();
                for (VrCompletionService bean : VrApp.getBeansForType(VrCompletionService.class)) {
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
        return i18n.getResourceBundleSuite(null).getMap();
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

    public boolean isUserAdmin(int uid) {
        return bodySecurityAuth.isUserAdmin(uid);
    }

    public boolean isCurrentSessionAdmin() {
        return bodySecurityAuth.isCurrentSessionAdmin();
    }

    public boolean isCurrentSessionAdminOrProfile(String profileName) {
        return bodySecurityAuth.isCurrentSessionAdminOrProfile(profileName);
    }

    public boolean isCurrentSessionAdminOrAllOfProfiles(String... profileNames) {
        return bodySecurityAuth.isCurrentSessionAdminOrAllOfProfiles(profileNames);
    }

    public boolean isCurrentSessionAdminOrAnyOfProfiles(String... profileNames) {
        return bodySecurityAuth.isCurrentSessionAdminOrAnyOfProfiles(profileNames);
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
        if (StringUtils.isBlank(domain)) {
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

    public VrEditorMainPhotoProvider getEntityMainPhotoProvider(String entityName) {
        return VrUPAUtils.resolveCachedEntityPropertyInstance(UPA.getPersistenceUnit().getEntity(entityName), "ui.main-photo-provider", VrEditorMainPhotoProvider.class);
    }

    public boolean isEnabledMainPhoto(String entityName) {
        return getEntityMainPhotoProvider(entityName) != null;
    }

    public String[] getMainPhotoPathList(String entityName, Object[] ids) {
        VrEditorMainPhotoProvider p = getEntityMainPhotoProvider(entityName);
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
        VrEditorMainPhotoProvider p = getEntityMainPhotoProvider(entityName);
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
        VrEditorMainPhotoProvider p = getEntityMainPhotoProvider(entityName);
        if (p == null) {
            return null;
        }
        return p.getMainPhotoPath(id, valueOrNull);
    }

    public String getMainIconPath(String entityName, Object id, Object valueOrNull) {
        VrEditorMainPhotoProvider p = getEntityMainPhotoProvider(entityName);
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
        net.thevpc.upa.types.Date today = new net.thevpc.upa.types.Date();
        for (AppTrace appTrace : t) {
            Date c = appTrace.getTime();
            if (c != null) {
                if (daysOnly) {
                    c = new net.thevpc.upa.types.Date(c);
                }
                if (includeToday || !new net.thevpc.upa.types.Date(c).equals(today)) {
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
        net.thevpc.upa.types.Date today = new net.thevpc.upa.types.Date();
        for (AppTrace appTrace : t) {
            Date c = appTrace.getTime();
            if (c != null) {
                c = new net.thevpc.upa.types.Date(c);
                if (includeToday || !new net.thevpc.upa.types.Date(c).equals(today)) {
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

    public List<AppRightName> findProfileRightNames() {
        return bodySecurityManager.findProfileRightNames();
    }

    public Map<String, AppRightName> findProfileRightNamesMap() {
        return new HashMap<>(CorePlugin.get().findProfileRightNames().stream().collect(Collectors.toMap(AppRightName::getName, java.util.function.Function.identity())));
    }

    public Set<String> findProfileRightNameStrings() {
        return bodySecurityManager.findProfileRightNames().stream().map(x -> StringUtils.trim(x.getName())).filter(x -> x.length() > 0)
                .collect(Collectors.toSet());
    }

    public void addProfileParents(String childern, String... parents) {
        bodySecurityManager.addProfileParent(childern, parents);
    }

    public void removeProfileParents(String childern, String... parents) {
        bodySecurityManager.removeProfileParent(childern, parents);
    }

    public String getServerLog() {
        CorePluginSecurity.requireAdmin();
        return logConfig.getServerLog();
    }

    public Object parseEntityId(String entityName, String id) {
        Entity entity = UPA.getPersistenceUnit().getEntity(entityName);
        List<Field> idFields = entity.getIdFields();
        if (idFields.size() == 1) {
            Field f = idFields.get(0);
            if (f.getDataType() instanceof ManyToOneRelationship) {
                ManyToOneRelationship m = (ManyToOneRelationship) f.getDataType();
                return parseEntityId(m.getTargetEntity().getName(), id);
            }
            return convertId(id, f.getDataType().getPlatformType());
        } else {
            throw new IllegalArgumentException("unsupported parseEntityId");
        }
    }

    private Object convertId(Object a, Class to) {
        if (a == null) {
            return null;
        }
        if (to.isInstance(a)) {
            return a;
        }
        switch (to.getSimpleName()) {
            case "Integer":
            case "int": {
                if (a instanceof Number) {
                    return ((Number) a).intValue();
                }
                if (a instanceof String) {
                    String s = (String) a;
                    if (StringUtils.isBlank(s)) {
                        return null;
                    }
                    return Integer.parseInt(s);
                }
                break;
            }
            case "Long":
            case "long": {
                if (a instanceof Number) {
                    return ((Number) a).longValue();
                }
                if (a instanceof String) {
                    String s = (String) a;
                    if (StringUtils.isBlank(s)) {
                        return null;
                    }
                    return Long.parseLong(s);
                }
                break;
            }
            case "String": {
                if (a instanceof String) {
                    return a.toString();
                }
                break;
            }
        }
        throw new IllegalArgumentException("Unsupported id type " + a.getClass() + ". Expected : " + to.getSimpleName());
    }

    public Object mergeDocuments(String entityName, Object baseId, List selectedIdStrings) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(entityName);
        Set<Object> removeMe = new HashSet<Object>();
        List<Field> idFields = e.getIdFields();
        if (idFields.size() != 1) {
            throw new IllegalArgumentException("Cannot merge Entity " + entityName + ". Unsupported Id " + e.getIdFields());
        }
        Class to = idFields.get(0).getDataType().getPlatformType();
        Object bid = convertId(baseId, to);
        if (bid == null) {
            throw new IllegalArgumentException("Cannot merge Entity " + entityName + ". base id is null");
        }
        Document v0 = pu.findDocumentById(entityName, bid);
        if (bid == null) {
            throw new IllegalArgumentException("Cannot merge Entity " + entityName + ". base id not found" + bid);
        }
        Set<Object> goodMergeable = new LinkedHashSet<>();
        for (Object selectedId : selectedIdStrings) {
            Object oid = convertId(selectedId, to);
            if (oid == null) {
                throw new IllegalArgumentException("Cannot merge Entity " + entityName + ". id to merge null");
            }
            if (oid.equals(bid)) {
                //ignore the case where baseId is specified twice
                continue;
            }
            goodMergeable.add(oid);
        }

        switch (entityName) {
            case "AcademicStudent":
            case "AcademicTeacher": {
                EntityBuilder b = pu.getEntity("AppUser").getBuilder();
                List<Integer> usersToMerge = new ArrayList<>();
                Set<Object> goodMergeable2 = new LinkedHashSet<>();
                goodMergeable2.add(bid);
                goodMergeable2.addAll(goodMergeable);
                for (Object oid : goodMergeable2) {
                    Document v = pu.findDocumentById(entityName, oid);
                    if (bid == null) {
                        throw new IllegalArgumentException("Cannot merge Entity " + entityName + ". to merge not found " + oid);
                    }
                    Integer u = (Integer) b.objectToId(v.get("user"));
                    if (u != null) {
                        usersToMerge.add(u);
                    }
                }
                if (usersToMerge.size() > 1) {
                    mergeDocuments("AppUser", usersToMerge.get(0), usersToMerge);
                }
                break;
            }
        }

        for (Object oid : goodMergeable) {
            Document v = pu.findDocumentById(entityName, oid);
            if (bid == null) {
                throw new IllegalArgumentException("Cannot merge Entity " + entityName + ". to merge not found " + oid);
            }
            for (Map.Entry<String, Object> entry : v.entrySet()) {
                String k = entry.getKey();
                Field ff = e.getField(k);
                if (ff.isGeneratedId() || ff.isId()) {
                    //ignore...
                } else {
                    Object uv = ff.getDataType().getDefaultUnspecifiedValue();
                    if ((v0.get(k) == null || Objects.equals(v0.get(k), uv))
                            && (entry.getValue() != null && !Objects.equals(entry.getValue(), uv))) {
                        v0.set(k, entry.getValue());
                    }
                }
            }
            removeMe.add(e.getBuilder().documentToId(v));
        }
        //Object o0 = e.getBuilder().documentToObject(v0);
        for (Relationship relationship : e.getRelationshipsByTarget()) {
            Entity se = relationship.getSourceEntity();
            String f = relationship.getSourceRole().getEntityField().getName();
            Query q = pu.createQuery("Select a from " + se.getName()
                    + " a where a." + f + ".id=:id"
            );
            for (Object _id : removeMe) {
                q.setParameter("id", _id);
                for (Document document : q.getDocumentList()) {
                    document.set(f, v0);
                    pu.merge(se.getName(), document);
                }
            }

        }
        for (Object _id : removeMe) {
            pu.remove(entityName, RemoveOptions.forId(_id));
        }
        return bid;
    }
}
