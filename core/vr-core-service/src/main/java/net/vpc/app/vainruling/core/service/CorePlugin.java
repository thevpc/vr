/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import net.vpc.app.vainruling.core.service.agent.ActiveSessionsTracker;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.cache.EntityCache;
import net.vpc.app.vainruling.core.service.fs.VrFS;
import net.vpc.app.vainruling.core.service.fs.VrFSEntry;
import net.vpc.app.vainruling.core.service.fs.VrFSTable;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.model.content.*;
import net.vpc.app.vainruling.core.service.notification.PollAware;
import net.vpc.app.vainruling.core.service.notification.VrNotificationManager;
import net.vpc.app.vainruling.core.service.obj.EntityObjSearchFactory;
import net.vpc.app.vainruling.core.service.obj.ObjSearch;
import net.vpc.app.vainruling.core.service.obj.ObjSimpleSearch;
import net.vpc.app.vainruling.core.service.plugins.*;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.AppVersion;
import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.common.io.FileUtils;
import net.vpc.common.io.PathInfo;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.*;
import net.vpc.common.vfs.*;
import net.vpc.upa.*;
import net.vpc.upa.expressions.*;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.DateTime;
import net.vpc.upa.types.ManyToOneType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * @author taha.bensalah@gmail.com
 */
@AppPlugin()
public class CorePlugin {

    public static final java.util.logging.Logger LOG_APPLICATION_STATS = java.util.logging.Logger.getLogger(CorePlugin.class.getName() + ".Stats");
    public static final String SEND_EXTERNAL_MAIL_QUEUE = "sendExternalMailQueue";
    public static final String PATH_LOG = "/Var/Log";
    public static final String PATH_TEMP = "/Var/Temp";
    public static final String USER_ADMIN = "admin";
    public static final String PROFILE_ADMIN = "Admin";
    public static final String PROFILE_HEAD_OF_DEPARTMENT = "HeadOfDepartment";
    public static final String RIGHT_FILESYSTEM_WRITE = "Custom.FileSystem.Write";
    public static final String RIGHT_FILESYSTEM_ASSIGN_RIGHTS = "Custom.FileSystem.AssignRights";
    public static final String RIGHT_FILESYSTEM_SHARE_FOLDERS = "Custom.FileSystem.ShareFolders";
    public static final Set<String> ADMIN_ENTITIES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Trace", "User", "UserProfile", "UserProfileBinding", "UserProfileRight")));
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CorePlugin.class.getName());
    public static String FOLDER_MY_DOCUMENTS = "Mes Documents";
    public static String FOLDER_ALL_DOCUMENTS = "Tous";
    public static String FOLDER_BACK = "<Dossier Parent>";
    //    private VirtualFileSystem fileSystem;
//    private String nativeFileSystemPath;
    @Autowired
    private TraceService trace;
    @Autowired
    private I18n i18n;
    @Autowired
    private VrApp app; //actually to force dependency, may have used @DependsOn
    @Autowired
    private CacheService cacheService;
    private boolean updatingPoll = false;
    private List<Plugin> plugins;
    private AppVersion appVersion;
    private Set<String> managerProfiles = new HashSet<>(Arrays.asList("Director"));
    private ActiveSessionsTracker sessions = new ActiveSessionsTracker();
    private Map<String, PluginComponent> components;
    private Map<String, PluginBundle> bundles;

    public static CorePlugin get() {
        return VrApp.getBean(CorePlugin.class);
    }

//    private void init(){
//    }
//    public UserSession getUserSession() {
//        return VrApp.getContext().getBean(UserSession.class);
//    }

    @PostConstruct
    public void prepare() {
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
        for (PersistenceUnit pu : getPersistenceUnits()) {
            pu.invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    tryInstall();
                }
            });
        }
    }

    public void onPoll() {
        if (!updatingPoll) {
            updatingPoll = true;
            try {
                synchronized (this) {
                    ApplicationContext context = VrApp.getContext();
                    for (String pn : context.getBeanNamesForType(PollAware.class)) {
                        PollAware b = (PollAware) context.getBean(pn);
                        b.onPoll();
                    }
                }
            } finally {
                updatingPoll = false;
            }
        }
    }

    public AppUser findUser(String login) {
        final EntityCache entityCache = cacheService.get(AppUser.class);
        Map<String, AppUser> m = entityCache.getProperty("findUserByLogin", new Action<Map<String, AppUser>>() {
            @Override
            public Map<String, AppUser> run() {
                Map<String, AppUser> m = new HashMap<String, AppUser>();
                MapList<Integer, AppUser> values = entityCache.getValues();
                for (AppUser u : values) {
                    String key = u.getLogin();
                    if (!StringUtils.isEmpty(key)) {
                        m.put(key, u);
                    }
                }
                return m;
            }
        });
        return m.get(login);

//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return (AppUser) pu.findByField(AppUser.class, "login", login);
    }

    public AppUser findUser(int id) {
        return cacheService.getList(AppUser.class).getByKey(id);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return (AppUser) pu.findById(AppUser.class, id);
    }

    public AppUserType findUserType(String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppUserType) pu.findByField(AppUserType.class, "code", name);
    }

    public List<AppUserType> findUserTypes() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findAll(AppUserType.class);
    }

    public AppUserType findUserType(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppUserType) pu.findById(AppUserType.class, id);
    }

    public AppProfile findProfile(int profileId) {
        return cacheService.getList(AppProfile.class).getByKey(profileId);
    }

    public boolean createRight(String rightName, String desc) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppRightName r = pu.findById(AppRightName.class, rightName);
        if (r != null) {
            return false;
        }
        r = new AppRightName();
        r.setName(rightName);
        r.setDescription(desc);
        pu.persist(r);
        return true;
    }

    public List<AppRightName>[] findProfileRightNamesDualList(int profileId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Map<String, AppRightName> allMap = new HashMap<>();
        Map<String, AppRightName> existing = new HashMap<>();
        List<AppRightName> oldRigths = pu.createQuery("Select u.`right` from AppProfileRight u where u.profileId=:profileId")
                .setParameter("profileId", profileId)
                .getResultList();
        List<AppRightName> allRigths = pu.createQuery("Select u from AppRightName u")
                .getResultList();
        for (AppRightName r : allRigths) {
            if(r!=null) {
                allMap.put(r.getName(), r);
            }
        }
        for (AppRightName r : oldRigths) {
            if(r!=null) {
                existing.put(r.getName(), r);
                allMap.remove(r.getName());
            }
        }
        List<AppRightName> in = new ArrayList<>(existing.values());
        List<AppRightName> out = new ArrayList<>(allMap.values());
        return new List[]{in, out};
    }

    public List<AppUser>[] findProfileUsersDualList(int profileId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Map<String, AppUser> allMap = new HashMap<>();
        Map<String, AppUser> existing = new HashMap<>();
        List<AppUser> oldRigths = pu.createQuery("Select u.user from AppUserProfileBinding u where u.profileId=:profileId")
                .setParameter("profileId", profileId)
                .getResultList();
        List<AppUser> allRigths = pu.createQuery("Select u from AppUser u")
                .getResultList();
        for (AppUser r : allRigths) {
            allMap.put(r.getLogin(), r);
        }
        for (AppUser r : oldRigths) {
            existing.put(r.getLogin(), r);
            allMap.remove(r.getLogin());
        }
        List<AppUser> in = new ArrayList<>(existing.values());
        List<AppUser> out = new ArrayList<>(allMap.values());
        return new List[]{in, out};
    }

    public int setProfileRights(int profileId, List<String> rightNames) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppProfile p = pu.findById(AppProfile.class, profileId);
        if (p == null) {
            throw new IllegalArgumentException("Profile not found " + profileId);
        }
        List<AppProfileRight> oldRigths = pu.createQuery("Select u from AppProfileRight u where u.profileId=:profileId")
                .setParameter("profileId", profileId)
                .getResultList();
        Set<String> baseSet = new HashSet<String>();
        Set<String> visitedSet = new HashSet<String>();
        if (rightNames != null) {
            baseSet.addAll(rightNames);
        }
        int modifications = 0;
        for (AppProfileRight r : oldRigths) {
            if (baseSet.contains(r.getRight().getName())) {
                //ok
            } else {
                pu.remove(r);
                modifications++;
            }
            visitedSet.add(r.getRight().getName());
        }
        for (String s : baseSet) {
            if (!visitedSet.contains(s)) {
                //this is new
                AppRightName r = pu.findById(AppRightName.class, s);
                if (r == null) {
                    log.log(Level.SEVERE, "Right " + s + " not found");
                } else {
                    AppProfileRight pr = new AppProfileRight();
                    pr.setProfile(p);
                    pr.setRight(r);
                    pu.persist(pr);
                    modifications++;
                }
            }
        }
        return modifications;
    }

    public int setProfileUsers(int profileId, List<String> logins) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppProfile p = pu.findById(AppProfile.class, profileId);
        if (p == null) {
            throw new IllegalArgumentException("Profile not found " + profileId);
        }
        List<AppUserProfileBinding> oldUserBindings = pu.createQuery("Select u from AppUserProfileBinding u where u.profileId=:profileId")
                .setParameter("profileId", profileId)
                .getResultList();
        List<AppUser> oldUsers = findUsers();
        Map<String, AppUser> usersByName = new HashMap<String, AppUser>();
        for (AppUser u : oldUsers) {
            usersByName.put(u.getLogin(), u);
        }
        Set<String> baseSet = new HashSet<String>();
        Set<String> visitedSet = new HashSet<String>();
        if (logins != null) {
            baseSet.addAll(logins);
        }
        int modifications = 0;
        for (AppUserProfileBinding r : oldUserBindings) {
            if (baseSet.contains(r.getUser().getLogin())) {
                //ok
            } else {
                pu.remove(r);
                modifications++;
            }
            visitedSet.add(r.getUser().getLogin());
        }
        for (String s : baseSet) {
            if (!visitedSet.contains(s)) {
                //this is new
                AppUser r = usersByName.get(s);
                if (r == null) {
                    log.log(Level.SEVERE, "User " + s + " not found");
                } else {
                    AppUserProfileBinding pr = new AppUserProfileBinding();
                    pr.setProfile(p);
                    pr.setUser(r);
                    pu.persist(pr);
                    modifications++;
                }
            }
        }
        return modifications;
    }

    public boolean addProfileRight(int profileId, String rightName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppProfile p = pu.findById(AppProfile.class, profileId);
        if (p == null) {
            throw new IllegalArgumentException("Profile not found " + profileId);
        }
        AppRightName r = pu.findById(AppRightName.class, rightName);
        if (r == null) {
            log.log(Level.SEVERE, "Right " + rightName + " not found");
            return false;
        }
        AppProfileRight pr = pu.createQuery("Select u from AppProfileRight u where u.profileId=:profileId and u.rightName=:rightName")
                .setParameter("rightName", rightName)
                .setParameter("profileId", profileId)
                .getFirstResultOrNull();
        if (pr == null) {
            pr = new AppProfileRight();
            pr.setProfile(p);
            pr.setRight(r);
            pu.persist(pr);
            return true;
        }
        return false;
    }

    public boolean userRemoveProfile(int userId, int profileId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppUserProfileBinding old = pu.createQuery("Select u from AppUserProfileBinding  u where u.userId=:userId and u.profileId=:profileId")
                .setParameter("userId", userId)
                .setParameter("profileId", profileId)
                .getFirstResultOrNull();
        if (old != null) {
            pu.remove(old);
            return true;
        }
        return false;
    }

    public boolean userAddProfile(int userId, String profileCode) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (pu.createQuery("Select u.profile from AppUserProfileBinding  u where u.userId=:userId and u.profile.name=:name")
                .setParameter("userId", userId)
                .setParameter("name", profileCode)
                .isEmpty()) {
            //should not call findUser because cache is not yet invalidated!
            AppUser u = pu.findById(AppUser.class, userId);
            if (u == null) {
                throw new IllegalArgumentException("Unknown User " + userId);
            }
            AppProfile p = findProfileByCode(profileCode);
            if (p == null) {
                p = findProfileByName(profileCode);
            }
            if (p == null) {
                p = new AppProfile();
                p.setName(profileCode);
                p.setCode(profileCode);
                pu.persist(p);
            }

            pu.persist(new AppUserProfileBinding(u, p));
            return true;
        }
        return false;
    }

    public boolean userAddProfile(int userId, int profileId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppUserProfileBinding b = pu.createQuery("Select u from AppUserProfileBinding  u where u.userId=:userId and u.profileId=:profileId")
                .setParameter("userId", userId)
                .setParameter("profileId", profileId)
                .getFirstResultOrNull();
        if (b == null) {
            AppUser u = findUser(userId);
            if (u == null) {
                throw new IllegalArgumentException("Unknown User " + userId);
            }
            AppProfile p = findProfile(profileId);
            if (p == null) {
                throw new IllegalArgumentException("Unknown Profile " + profileId);
            }
            pu.persist(new AppUserProfileBinding(u, p));
            return true;
        }
        return false;
    }

    public boolean userHasProfile(int userId, String profileName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return !pu.createQuery("Select u.profile from AppUserProfileBinding  u where u.userId=:userId and u.profile.name=:name")
                .setParameter("userId", userId)
                .setParameter("name", profileName)
                .isEmpty();
    }

    public Set<String> findUserRights(int userId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<String> rights = pu.createQuery("Select n.rightName from AppUserProfileBinding  u "
                + " inner join AppProfileRight n on n.profileId=u.profileId "
                + " where u.userId=:userId")
                .setParameter("userId", userId)
                .getValueList(0);
        return new HashSet<>(rights);
    }

    public AppProfile findOrCreateCustomProfile(String profileCode, String customType) {
        AppProfile p = findProfileByCode(profileCode);
        if (p == null) {
            p = findProfileByName(profileCode);
        }
        if (p == null) {
            p = new AppProfile();
            p.setName(profileCode);
            p.setCode(profileCode);
            p.setCustom(true);
            p.setCustomType(customType);
            UPA.getPersistenceUnit().persist(p);
        } else if (!p.isCustom()) {
            //force to custom
            p.setCustom(true);
            p.setCustomType(customType);
            UPA.getPersistenceUnit().merge(p);
        }
        if (StringUtils.isEmpty(p.getCode())) {
            p.setCode(p.getName());
            UPA.getPersistenceUnit().merge(p);
        }
        if (StringUtils.isEmpty(p.getName())) {
            p.setCode(p.getCode());
            UPA.getPersistenceUnit().merge(p);
        }
        return p;
    }

    public AppProfile findOrCreateProfile(String profileName) {
        AppProfile p = findProfileByName(profileName);
        if (p == null) {
            p = new AppProfile();
            p.setName(profileName);
            UPA.getPersistenceUnit().persist(p);
        }
        return p;
    }


//    public Object getGlobalCache(String name){
//        synchronized (globalCache){
//            return globalCache.get(name);
//        }
//    }
//
//    public void setGlobalCache(String name,Object value){
//        synchronized (globalCache){
//            globalCache.put(name, value);
//        }
//    }
//
//    public void removeGlobalCache(String name){
//        synchronized (globalCache){
//            globalCache.remove(name);
//        }
//    }

    public AppProfile findProfileByName(String profileName) {
        final EntityCache entityCache = cacheService.get(AppProfile.class);
        Map<String, AppProfile> m = entityCache.getProperty("findProfileByName", new Action<Map<String, AppProfile>>() {
            @Override
            public Map<String, AppProfile> run() {
                Map<String, AppProfile> m = new HashMap<String, AppProfile>();
                MapList<Integer, AppProfile> values = entityCache.getValues();
                for (AppProfile profile : values) {
                    String key = profile.getName();
                    if (!StringUtils.isEmpty(key)) {
                        m.put(key, profile);
                    }
                }
                return m;
            }
        });
        return m.get(profileName);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQueryBuilder(AppProfile.class).byField("name", profileName)
//                .getEntity();

    }

    public AppProfile findProfileByCode(String profileCode) {
        final EntityCache entityCache = cacheService.get(AppProfile.class);
        Map<String, AppProfile> m = entityCache.getProperty("findProfileByCode", new Action<Map<String, AppProfile>>() {
            @Override
            public Map<String, AppProfile> run() {
                Map<String, AppProfile> m = new HashMap<String, AppProfile>();
                MapList<Integer, AppProfile> values = entityCache.getValues();
                for (AppProfile profile : values) {
                    String key = profile.getCode();
                    if (!StringUtils.isEmpty(key)) {
                        m.put(key, profile);
                    }
                }
                return m;
            }
        });
        return m.get(profileCode);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQueryBuilder(AppProfile.class).byField("code", profileCode)
//                .getEntity();

    }

//    public Map<Integer,List<AppProfile>> findProfilesMapByUserId() {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        List<AppUserProfileBinding> appUserProfileBindings = pu.createQuery("Select u from AppUserProfileBinding  u").getResultList();
//        HashMap<Integer, List<AppProfile>> all = new HashMap<>();
//        for (AppUserProfileBinding o : appUserProfileBindings) {
//            if(o.getUser()!=null && o.getProfile()!=null){
//                List<AppProfile> list = all.get(o.getUser().getId());
//                if(list==null){
//                    list=new ArrayList<>();
//                    all.put(o.getUser().getId(), list);
//                }
//                list.add(o.getProfile());
//            }
//        }
//        return all;
//    }

    public Set<String> findUniformProfileNamesMapByUserId(int userId, boolean includeLogin) {
        Map<Integer, Set<String>> uniformProfileNamesMapByUserId = findUniformProfileNamesMapByUserId(includeLogin);
        Set<String> profiles = uniformProfileNamesMapByUserId.get(userId);
        if (profiles == null) {
            AppUser u = findUser(userId);
            if (u == null) {
                return null;
            }
            profiles = new HashSet<>();
            uniformProfileNamesMapByUserId.put(userId, profiles);
            if (includeLogin) {
                profiles.add(u.getLogin().toLowerCase());
            }
        }
        return profiles;
    }

    public Map<Integer, Set<String>> findUniformProfileNamesMapByUserId(final boolean includeLogin) {
        String cacheKey = "findUniformProfileNamesMapByUserId:" + includeLogin;
        final EntityCache entityCache = cacheService.get(AppUserProfileBinding.class);
        return entityCache
                .getProperty(cacheKey, new Action<Map<Integer, Set<String>>>() {
                    @Override
                    public Map<Integer, Set<String>> run() {
                        HashMap<Integer, Set<String>> all = new HashMap<>();
                        MapList<Integer, AppUserProfileBinding> values = entityCache.getValues();
                        for (AppUserProfileBinding o : values) {
                            if (o.getUser() != null && o.getProfile() != null) {
                                Set<String> list = all.get(o.getUser().getId());
                                if (list == null) {
                                    list = new HashSet<>();
                                    if (includeLogin) {
                                        list.add(o.getUser().getLogin().toLowerCase());
                                    }
                                    all.put(o.getUser().getId(), list);
                                }
                                list.add(o.getProfile().getName().toLowerCase());
                            }
                        }
                        return all;
                    }
                });


//        Map<Integer,Set<String>> value=(Map<Integer, Set<String>>) getGlobalCache(cacheKey);
//        if(value==null){
//            PersistenceUnit pu = UPA.getPersistenceUnit();
//            List<AppUserProfileBinding> appUserProfileBindings = pu.createQuery("Select u from AppUserProfileBinding  u").getResultList();
//            HashMap<Integer, Set<String>> all = new HashMap<>();
//            for (AppUserProfileBinding o : appUserProfileBindings) {
//                if(o.getUser()!=null && o.getProfile()!=null){
//                    Set<String> list = all.get(o.getUser().getId());
//                    if(list==null){
//                        list=new HashSet<>();
//                        if(includeLogin){
//                            list.add(o.getUser().getLogin().toLowerCase());
//                        }
//                        all.put(o.getUser().getId(), list);
//                    }
//                    list.add(o.getProfile().getName().toLowerCase());
//                }
//            }
//            value=all;
//            setGlobalCache(cacheKey,value);
//        }
//        return value;

    }

    public List<AppProfile> findProfilesByUser(int userId) {
        final EntityCache entityCache = cacheService.get(AppUserProfileBinding.class);
        Map<Integer, List<AppProfile>> m = entityCache
                .getProperty("findProfilesByUser", new Action<Map<Integer, List<AppProfile>>>() {
                    @Override
                    public Map<Integer, List<AppProfile>> run() {
                        PersistenceUnit pu = UPA.getPersistenceUnit();
                        MapList<Integer, AppUserProfileBinding> values = entityCache.getValues();
                        Map<Integer, List<AppProfile>> m = new HashMap<Integer, List<AppProfile>>();
                        for (AppUserProfileBinding binding : values) {
                            if (binding.getUser() != null && binding.getProfile() != null) {
                                List<AppProfile> found = m.get(binding.getUser().getId());
                                if (found == null) {
                                    found = new ArrayList<AppProfile>();
                                    m.put(binding.getUser().getId(), found);
                                }
                                found.add(binding.getProfile());
                            }
                        }
                        return m;
                    }
                });
        List<AppProfile> all = m.get(userId);
        if (all == null) {
            all = Collections.EMPTY_LIST;
        }
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select u.profile from AppUserProfileBinding  u where u.userId=:userId")
//                .setParameter("userId", userId)
//                .getResultList();
        return all;
    }

    public List<AppUser> findUsersByTypeAndDepartment(int userType, int userDepartment) {
        if (userDepartment < 0) {
            return findUsersByType(userType);
        }
        final EntityCache entityCache = cacheService.get(AppUser.class);
        Map<String, List<AppUser>> m = entityCache
                .getProperty("findUsersByTypeAndDepartment", new Action<Map<String, List<AppUser>>>() {
                    @Override
                    public Map<String, List<AppUser>> run() {
                        List<AppUser> bindings = entityCache.getValues();
                        Map<String, List<AppUser>> m = new HashMap<String, List<AppUser>>();
                        for (AppUser user : bindings) {
                            String key = (user.getType() == null ? "" : user.getType().getId()) + ";" + (user.getDepartment() == null ? "" : user.getDepartment().getId());
                            List<AppUser> found = m.get(key);
                            if (found == null) {
                                found = new ArrayList<AppUser>();
                                m.put(key, found);
                            }
                            found.add(user);
                        }
                        return m;
                    }
                });
        List<AppUser> all = m.get((userType < 0 ? "" : String.valueOf(userType)) + ";" + (userDepartment < 0 ? "" : String.valueOf(userDepartment)));
        if (all == null) {
            all = Collections.EMPTY_LIST;
        }
        return all;
    }

    public List<AppUser> findUsersByType(int userType) {
        final EntityCache entityCache = cacheService.get(AppUser.class);
        Map<Integer, List<AppUser>> m = entityCache
                .getProperty("findUsersByType", new Action<Map<Integer, List<AppUser>>>() {
                    @Override
                    public Map<Integer, List<AppUser>> run() {
                        List<AppUser> bindings = entityCache.getValues();
                        Map<Integer, List<AppUser>> m = new HashMap<Integer, List<AppUser>>();
                        for (AppUser user : bindings) {
                            if (user.getType() != null) {
                                List<AppUser> found = m.get(user.getType().getId());
                                if (found == null) {
                                    found = new ArrayList<AppUser>();
                                    m.put(user.getType().getId(), found);
                                }
                                found.add(user);
                            }
                        }
                        return m;
                    }
                });
        List<AppUser> all = m.get(userType);
        if (all == null) {
            all = Collections.EMPTY_LIST;
        }
        return all;

    }

    public List<AppUser> findUsersByProfile(int profileId) {
        final EntityCache entityCache = cacheService.get(AppUserProfileBinding.class);
        Map<Integer, List<AppUser>> m = entityCache
                .getProperty("findProfilesByUser", new Action<Map<Integer, List<AppUser>>>() {
                    @Override
                    public Map<Integer, List<AppUser>> run() {
                        PersistenceUnit pu = UPA.getPersistenceUnit();
                        List<AppUserProfileBinding> bindings = entityCache.getValues();
                        Map<Integer, List<AppUser>> m = new HashMap<Integer, List<AppUser>>();
                        for (AppUserProfileBinding binding : bindings) {
                            if (binding.getUser() != null && binding.getProfile() != null) {
                                List<AppUser> found = m.get(binding.getProfile().getId());
                                if (found == null) {
                                    found = new ArrayList<AppUser>();
                                    m.put(binding.getProfile().getId(), found);
                                }
                                found.add(binding.getUser());
                            }
                        }
                        return m;
                    }
                });
        List<AppUser> all = m.get(profileId);
        if (all == null) {
            all = Collections.EMPTY_LIST;
        }
        return all;

//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select u.user from AppUserProfileBinding  u where u.profileId=:profileId")
//                .setParameter("profileId", profileId)
//                .getResultList();
    }

    public Integer findUserIdByLogin(String login) {
        return findUserLoginToIdMap().get(login);
    }

    public Map<String, Integer> findUserLoginToIdMap() {
        final EntityCache entityCache = cacheService.get(AppUser.class);
        return entityCache.getProperty("findUserLoginToIdMap", new Action<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> run() {
                Map<String, Integer> m = new HashMap<String, Integer>();
                MapList<Integer, AppUser> values = entityCache.getValues();
                for (AppUser s : values) {
                    m.put(s.getLogin(), s.getId());
                }
                return m;
            }
        });
//        String cacheKey = "findUserLoginToIdMap";
//        Map<String,Integer> value=(Map<String, Integer>) getGlobalCache(cacheKey);
//        if(value==null){
//            PersistenceUnit pu = UPA.getPersistenceUnit();
//            Map<String,Integer> ret=new HashMap<>();
//            for (Document document : pu.createQuery("Select u.id id, u.login login from AppUser u")
//                    .getDocumentList()) {
//                ret.put(document.getString("login"),document.getInt("id"));
//            }
//            value=ret;
//            setGlobalCache(cacheKey,value);
//        }
//        return value;
    }

    public List<AppUser> findUsersByProfile(String profileName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u.user from AppUserProfileBinding  u where u.profile.name=:profileName")
                .setParameter("profileName", profileName)
                .getResultList();
    }

    public List<AppProfile> findProfiles() {
        return cacheService.getList(AppProfile.class);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select u from AppProfile u")
//                .getResultList();
    }

    public List<AppUser> findUsers() {
        return cacheService.getList(AppUser.class);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select u from AppUser  u")
//                .getResultList();
    }

    public List<AppUser> findEnabledUsers(Integer userType) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (userType == null) {
            return pu.createQuery("Select u from AppUser  u where u.enabled=true and u.deleted=false")
                    .getResultList();
        }
        return pu.createQuery("Select u from AppUser  u where u.enabled=true and u.deleted=false and u.typeId=:userType")
                .setParameter("userType", userType)
                .getResultList();
    }

    public List<AppUser> findEnabledUsers() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AppUser  u where u.enabled=true and u.deleted=false")
                .getResultList();
    }

    public AppUser findUser(String login, String password) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppUser) pu
                .createQuery("Select u from AppUser u "
                        + "where "
                        + "u.login=:login "
                        + "and u.password=:password")
                .setParameter("login", login)
                .setParameter("password", password)
                .getFirstResultOrNull();
    }

    public AppDepartment findDepartment(int id) {
        return UPA.getPersistenceUnit().findById(AppDepartment.class, id);
    }

    public List<AppDepartment> findDepartments() {
        return UPA.getPersistenceUnit().findAll(AppDepartment.class);
    }

    public AppDepartment findDepartment(String code) {
        return UPA.getPersistenceUnit().
                createQuery("Select a from AppDepartment a where a.code=:code or a.name=:code or a.name2=:code")
                .setParameter("code", code)
                .getFirstResultOrNull();
    }

    public AppCivility findCivility(String t) {
        return (AppCivility) UPA.getPersistenceUnit().findByMainField(AppCivility.class, t);
    }

    public List<AppCivility> findCivilities() {
        return UPA.getPersistenceUnit().findAll(AppCivility.class);
    }

    public AppContact findContact(int id) {
        return (AppContact) UPA.getPersistenceUnit().findById(AppContact.class, id);
    }

    public AppCompany findCompany(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppCompany) pu.createQuery("Select u from AppCompany u where u.id=:id").setParameter("id", id)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                .getFirstResultOrNull();
//        return (AppCompany) pu.findById(AppCompany.class, id);
    }

    public AppCompany findCompany(String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppCompany) pu.createQuery("Select u from AppCompany u where u.name=:name").setParameter("name", name)
//                .setHint(QueryHints.MAX_NAVIGATION_DEPTH, 3)
                .getFirstResultOrNull();
//
//        return (AppCompany) UPA.getPersistenceUnit().findByMainField(AppCompany.class, name);
    }

    public List<AppCompany> findCompanies() {
        return UPA.getPersistenceUnit().findAll(AppCompany.class);
    }

    public AppGender findGender(String t) {
        return (AppGender) UPA.getPersistenceUnit().findByMainField(AppGender.class, t);
    }

    public List<AppGender> findGenders() {
        List<AppGender> all = UPA.getPersistenceUnit().findAll(AppGender.class);
        return all;
    }

    @Install
    private void installService() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppCountry tunisia = pu.findByMainField(AppCountry.class, "Tunisie");
        if (tunisia == null) {
            tunisia = new AppCountry();
            tunisia.setName("Tunisie");
            pu.persist(tunisia);
        }
        AppIndustry eduIndustry = pu.findByMainField(AppIndustry.class, "Education");
        if (eduIndustry == null) {
            eduIndustry = new AppIndustry();
            eduIndustry.setName("Education");
            pu.persist(eduIndustry);
        }

        AppCompany eniso = pu.findByMainField(AppIndustry.class, "ENISo");
        if (eniso == null) {
            eniso = new AppCompany();
            eniso.setName("ENISo");
            eniso.setIndustry(eduIndustry);
            eniso.setCountry(tunisia);
            pu.persist(eniso);
        }

        AppProfile oldAdmin = pu.findByMainField(AppProfile.class, "admin");
        if (oldAdmin != null) {
            oldAdmin.setName("Admin");
            pu.merge(oldAdmin);
        }
        InitData d = new InitData();
        d.now = System.currentTimeMillis();
        d.adminProfile = new AppProfile();
        d.adminProfile.setName("Admin");
        d.adminProfile = findOrCreate(d.adminProfile);
        d.adminProfile.setCustom(true);
        d.adminProfile.setCustomType("Profile");
        pu.merge(d.adminProfile);

        d.adminType = new AppUserType();
        d.adminType.setName("Admin");
        d.adminType = findOrCreate(d.adminType);

        d.civilities = new ArrayList<>();
        for (String n : new String[]{"M.", "Mlle", "Mme"}) {
            AppCivility c = new AppCivility(0, n);
            c = findOrCreate(c);
            d.civilities.add(c);
        }
        d.genders = new ArrayList<>();
        for (String n : new String[]{"H", "F"}) {
            AppGender c = new AppGender(0, n);
            c = findOrCreate(c);
            d.genders.add(c);
        }
        AppContact adminContact = new AppContact();
        adminContact.setFirstName("admin");
        adminContact.setLastName("admin");
        adminContact.setFullName("admin");
        adminContact.setCivility(d.civilities.get(0));
        adminContact.setGender(d.genders.get(0));
        adminContact.setEmail("admin@vr.net");
        adminContact = findOrCreate(adminContact, "firstName");

        AppUser uu = new AppUser();
        d.admin = uu;
        d.admin.setLogin("admin");
        d.admin.setPassword("admin");
        d.admin.setType(d.adminType);
        d.admin.setContact(adminContact);
        d.admin.setEnabled(true);
        d.admin = findOrCreate(d.admin);
        if (d.admin == uu) {
            pu.persist(new AppUserProfileBinding(d.admin, d.adminProfile));
        }
        d.departments = new ArrayList<>();
        for (String[] n : new String[][]{{"II", "Informatique Industrielle"}, {"EI", "Electronique Indstrielle"}, {"MA", "Mecanique Avancee"}, {"ADM", "Administration"}}) {
            AppDepartment c = new AppDepartment();
            c.setCode(n[0]);
            c.setName(n[1]);
            AppDepartment old = pu.findByField(AppDepartment.class, "code", c.getCode());
            if (old == null) {
                c = findOrCreate(c);
            } else {
                c = old;
            }
            d.departments.add(c);
        }
        AppConfig mainConfig = new AppConfig();
        mainConfig.setId(1);
        mainConfig.setMainCompany(eniso);
        mainConfig.setMainPeriod(new AppPeriod("2015-2016"));
        findOrCreate(mainConfig, "id");
//        validateRightsDefinitions();
        ArticlesDisposition ad;

        for (int i = 1; i <= 7; i++) {
            ad = new ArticlesDisposition();
            ad.setName("Main.Row" + i);
            ad.setDescription("Page principale, Ligne " + i);
            findOrCreate(ad);
        }

        ad = new ArticlesDisposition();
        ad.setName("Welcome");
        ad.setDescription("Page de bienvenue");
        findOrCreate(ad);

        ad = new ArticlesDisposition();
        ad.setName("News");
        ad.setDescription("Page actualités");
        findOrCreate(ad);

        ad = new ArticlesDisposition();
        ad.setName("Activities");
        ad.setDescription("Page activités");
        findOrCreate(ad);


        {
            AppProfile p = new AppProfile();
            p.setCode("Publisher");
            p.setName("Publisher");
            p = findOrCreate(p);
            addProfileRight(p.getId(), "ArticlesItem.DefaultEditor");
            addProfileRight(p.getId(), "ArticlesItem.Load");
            addProfileRight(p.getId(), "ArticlesItem.Navigate");
            addProfileRight(p.getId(), "ArticlesItem.Persist");
            addProfileRight(p.getId(), "ArticlesItem.Update");
            addProfileRight(p.getId(), "ArticlesItem.Remove");
            addProfileRight(p.getId(), "ArticlesFile.DefaultEditor");
            addProfileRight(p.getId(), "ArticlesFile.Load");
            addProfileRight(p.getId(), "ArticlesFile.Navigate");
            addProfileRight(p.getId(), "ArticlesFile.Persist");
            addProfileRight(p.getId(), "ArticlesFile.Update");
            addProfileRight(p.getId(), "ArticlesFile.Remove");
        }
    }

    @Start
    private void onStart() {
        createRight("Custom.FileSystem.RootFileSystem", "Root FileSystem Access");
        createRight("Custom.FileSystem.MyFileSystem", "My FileSystem Access");
        createRight(RIGHT_FILESYSTEM_ASSIGN_RIGHTS, "Assign Access Rights for File System");
        createRight(RIGHT_FILESYSTEM_SHARE_FOLDERS, "Share Folders in File System");
        createRight(RIGHT_FILESYSTEM_WRITE, "Enable Write Access for File System");


        createRight("Custom.Article.SendExternalEmail", "Send External Email");
        createRight("Custom.Article.SendInternalEmail", "Send Internal Email");


        //check if already bound
//            for (Handler handler : LOG_APPLICATION_STATS.getHandlers()) {
//                if(handler instanceof FileHandler){
//                    FileHandler f=(FileHandler) handler;
//                }
//            }

        if (LOG_APPLICATION_STATS.getHandlers().length == 0) {
            UPA.getPersistenceUnit("main").invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    String path = getNativeFileSystemPath();
                    try {
                        FileHandler handler = new FileHandler(path + PATH_LOG + "/application-stats.log", 5 * 1024 * 1024, 5, true);
                        handler.setFormatter(new CustomTextFormatter());
                        LOG_APPLICATION_STATS.addHandler(handler);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        validateRightsDefinitions();
        VrApp.getBean(VrNotificationManager.class).register(SEND_EXTERNAL_MAIL_QUEUE, SEND_EXTERNAL_MAIL_QUEUE, 200);
    }

    protected void validateRightsDefinitions() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (Entity entity : pu.getEntities()) {
            if (!entity.isSystem()) {
                EntityShield s = entity.getShield();
                if (true) {
                    AppRightName r = new AppRightName();
                    r.setName(entity.getAbsoluteName() + ".DefaultEditor");
                    r.setDescription("List " + entity.getName());
                    findOrCreate(r);
                }
                if (true) {
                    AppRightName r = new AppRightName();
                    r.setName(entity.getAbsoluteName() + ".Navigate");
                    r.setDescription("List " + entity.getName());
                    findOrCreate(r);
                }
                if (true) {
                    AppRightName r = new AppRightName();
                    r.setName(entity.getAbsoluteName() + ".Load");
                    r.setDescription("Detail " + entity.getName());
                    findOrCreate(r);
                }
                if (!ADMIN_ENTITIES.contains(entity.getName())) {
                    if (s.isPersistSupported()) {
                        AppRightName r = new AppRightName();
                        r.setName(entity.getAbsoluteName() + ".Persist");
                        r.setDescription("Persist " + entity.getName());
                        findOrCreate(r);
                    }
                    if (s.isUpdateSupported()) {
                        AppRightName r = new AppRightName();
                        r.setName(entity.getAbsoluteName() + ".Update");
                        r.setDescription("Update " + entity.getName());
                        findOrCreate(r);
                    }
                    if (s.isDeleteSupported()) {
                        AppRightName r = new AppRightName();
                        r.setName(entity.getAbsoluteName() + ".Remove");
                        r.setDescription("Remove " + entity.getName());
                        findOrCreate(r);
                    }
                    String extraActions = entity.getProperties().getString("actions");
                    if (extraActions != null) {
                        for (String a : extraActions.split(" ,|;")) {
                            if (a.length() > 0) {
                                AppRightName r = new AppRightName();
                                r.setName(entity.getAbsoluteName() + "." + a);
                                r.setDescription(a + " " + entity.getName());
                                findOrCreate(r);
                            }
                        }
                    }
                    for (Field field : entity.getFields()) {
                        if (field.getReadAccessLevel() == AccessLevel.PROTECTED) {
                            String rightName = entity.getAbsoluteName() + "." + field.getName() + ".Read";
                            createRight(rightName, rightName);
                        }
                        if (field.getUpdateAccessLevel() == AccessLevel.PROTECTED) {
                            String rightName = entity.getAbsoluteName() + "." + field.getName() + ".Write";
                            createRight(rightName, rightName);
                        }
                    }
                }
            }
        }
        createRight("Custom.Admin.Passwd", "Custom.Admin.Passwd");
    }

    public <T> T findOrCreate(T o) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        return findOrCreate(o, e.getMainField().getName());
    }

    public <T> T findOrCreate(T o, String field) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(o.getClass());
        Object value = e.getBuilder().objectToDocument(o, true).getObject(field);
        T t = pu.createQueryBuilder(o.getClass()).setEntityAlias("o").byExpression(new Equals(new Var(new Var("o"), field), new Literal(value, e.getField(field).getDataType())))
                .getFirstResultOrNull();
        if (t == null) {
            pu.persist(o);
            return o;
        }
        return t;
    }

    public <T> List<T> filterByProfilePattern(List<T> in, Integer userId, String login, ProfilePatternFilter<T> filter) {
        List<T> out = new ArrayList<>();
        final HashMap<String, Object> cache = new HashMap<String, Object>();
        if (in != null) {
            for (T i : in) {
                if (userMatchesProfileFilter(userId, login, new ProfileFilterExpression(filter.getProfilePattern(i)), cache)) {
                    out.add(i);
                }
            }
        }
        return out;
    }

    private InSetEvaluator createProfilesEvaluator(final Set<String> profiles) {
        return new SimpleJavaEvaluator(profiles);
    }

    public boolean userMatchesProfileFilter(int userId, String profileExpr) {
        return userMatchesProfileFilter(userId, null, profileExpr, null);
    }

    public boolean userMatchesProfileFilter(String userLogin, String profileExpr) {
        return userMatchesProfileFilter(null, userLogin, profileExpr, null);
    }

    public boolean userMatchesProfileFilter(Integer userId, String login, String profile, String whereClause) {
        return userMatchesProfileFilter(userId, login, profile, whereClause, null);
    }

    //    public List<AppProfile> resolveProfilesByProfileFilter(String profile) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        if (profile != null && profile.trim().length() > 0) {
//            StringBuilder x = new StringBuilder();
//            for (String p : profile.split(" , |;")) {
//                if (p != null) {
//                    x.append("/").append(p);m
//                }
//            }
//            x.append("/");
//            return pu.createQuery("Select u from AppProfile u where :expr like concat('%/',u.name,'/%')")
//                    .setParameter("expr", x.toString())
//                    .getResultList();
//        }
//        return Collections.EMPTY_LIST;
//    }

//    private static void removeDuplicates(List<String> list){
//        HashSet<String> noDuplicates=new HashSet<>();
//        for(Iterator<String> i =list.iterator()){
//            String s=i.next();
//            if(noDuplicates.contains(s)){
//                i.remove();
//            }else{
//                noDuplicates.add(s);
//            }
//        }
//    }

    private boolean userMatchesProfileFilter(Integer userId, String login, String profile, String whereClause, Map<String, Object> cache) {
        return userMatchesProfileFilter(userId, login, new ProfileFilterExpression(profile, whereClause), cache);
    }

    private boolean userMatchesProfileFilter(Integer userId, String login, ProfileFilterExpression profileExpr, Map<String, Object> cache) {
        if (StringUtils.isEmpty(profileExpr.getFilterExpression()) && StringUtils.isEmpty(profileExpr.getProfileListExpression())) {
            return true;
        }
        if (cache == null) {
            cache = new HashMap<>();
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (userId == null) {
            userId = findUserIdByLogin(login);
        }
        Set<String> foundProfileNames = (userId == null) ? (new HashSet<String>()) : findUniformProfileNamesMapByUserId(userId, true);
        if (foundProfileNames == null) {
            foundProfileNames = new HashSet<>();
        }
        InSetEvaluator evaluator = createProfilesEvaluator(foundProfileNames);
        boolean b = false;
        try {
            b = evaluator.evaluateExpression(profileExpr.getProfileListExpression());
        } catch (Exception e) {
            //error
        }
        if (b && !StringUtils.isEmpty(profileExpr.getFilterExpression())) {
            return filterUsersByExpression(
                    userId == null ? new int[0] : new int[]{userId}
                    , profileExpr.getFilterExpression()).size() > 0;
        }
        return b;
    }

    public List<String> autoCompleteUserOrProfile(String userOrProfile) {
        if (userOrProfile == null) {
            userOrProfile = "";
        }
        userOrProfile = userOrProfile.trim().toLowerCase();
        List<String> all = new ArrayList<>();
        for (AppProfile appProfile : findProfiles()) {
            if (appProfile.getName() != null && appProfile.getName().toLowerCase().contains(userOrProfile)) {
                all.add(appProfile.getName().trim());
            }
        }
        for (AppUser appUser : findUsers()) {
            if (appUser.getLogin() != null && appUser.getLogin().toLowerCase().contains(userOrProfile)) {
                all.add(appUser.getLogin().trim());
            }
        }
        return all;
    }

    public List<String> autoCompleteProfileExpression(String queryExpr) {
        if (queryExpr == null) {
            queryExpr = "";
        }
        queryExpr = queryExpr.trim();
        int x = queryExpr.length() - 1;
        while (x >= 0) {
            char c = queryExpr.charAt(x);
            if (!Character.isLetterOrDigit(c) && c != '.') {
                break;
            }
            x--;
        }
        if (x > 0) {
            if (x < queryExpr.length() - 1) {
                String prefix = queryExpr.substring(0, x + 1);
                String suffix = queryExpr.substring(x + 1);
                List<String> all = new ArrayList<>();
                List<String> strings = autoCompleteUserOrProfile(suffix);
                if (strings.isEmpty()) {
                    strings = Arrays.asList("");
                }
                for (String s : strings) {
                    all.add(prefix + s);
                }
                return all;
            } else {
                return Arrays.asList(queryExpr);
            }
        } else {
            return new ArrayList<String>(new TreeSet<String>(autoCompleteUserOrProfile(queryExpr)));
        }
    }

    public List<AppUser> filterUsersByProfileFilter(List<AppUser> users, String profilePattern, Integer userType) {
        //check if pattern contains where clause!
        ProfileFilterExpression ee = new ProfileFilterExpression(profilePattern);
        ProfileFilterExpression profilesOnlyExpr = new ProfileFilterExpression(ee.getProfileListExpression(), null);

        List<AppUser> all = new ArrayList<>();
        final HashMap<String, Object> cache = new HashMap<String, Object>();
        HashMap<Integer, AppUser> usersById = new HashMap<Integer, AppUser>();
        HashMap<String, AppUser> usersByLogin = new HashMap<String, AppUser>();
        for (AppUser user : users) {
            usersById.put(user.getId(), user);
            usersByLogin.put(user.getLogin(), user);
        }
        cache.put("usersById", usersById);
        cache.put("usersByLogin", usersByLogin);
        int userTypeInt = userType == null ? -1 : userType.intValue();
        for (AppUser u : users) {
            if (userType == null || (u.getType() != null && u.getType().getId() == userTypeInt)) {
                if (userMatchesProfileFilter(u.getId(), u.getLogin(), profilesOnlyExpr, cache)) {
                    all.add(u);
                }
            }
        }
        return filterUsersByExpression(all, ee.getFilterExpression());
    }

    public List<AppUser> filterUsersBysContacts(List<AppContact> users) {
        Set<Integer> visited = new HashSet<>();
        List<AppUser> ret = new ArrayList<>();
        StringBuilder q = new StringBuilder("-1");
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (int i = 0; i < users.size(); i++) {
            AppContact contact = users.get(i);
            q.append(",").append(contact.getId());
            if (i % 50 == (50 - 1) || i == users.size() - 1) {
                for (AppUser o : pu.createQuery("Select u from AppUser u where u.contactId in (" + q + ")")
                        .<AppUser>getResultList()) {
                    if (visited.contains(o.getId())) {
                        visited.add(o.getId());
                        ret.add(o);
                    }
                }
                q.delete(0, q.length());
                q.append("-1");
            }
        }
        return ret;
    }

    public List<AppContact> filterContactsByProfileFilter(List<AppContact> contacts, String profilePattern) {
        List<AppContact> ret = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        for (AppUser user : filterUsersByProfileFilter(filterUsersBysContacts(contacts), profilePattern, null)) {
            AppContact c = user.getContact();
            if (c != null && !visited.contains(c.getId())) {
                visited.add(c.getId());
                ret.add(c);
            }
        }
        return ret;
    }

    public List<AppUser> findUsersByProfileFilter(String profilePattern, Integer userType) {
        return filterUsersByProfileFilter(findEnabledUsers(), profilePattern, userType);
    }

    private List<AppUser> filterUsersByExpression(List<AppUser> all, String expression) {
        if (all.isEmpty()) {
            return all;
        }
        if (StringUtils.isEmpty(expression)) {
            return new ArrayList<AppUser>(all);
        }
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < all.size(); i++) {
            if (i > 0) {
                ids.append(",");
            }
            ids.append(all.get(i).getId());
        }
        return UPA.getPersistenceUnit()
                .createQuery("Select x from AppUser x where x.id in (" + ids + ") " + expression)
                .getResultList();
    }
//    public List<AppUser> resolveUsersByProfileFilter(String profile) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        if (profile != null && profile.trim().length() > 0) {
//            StringBuilder x = new StringBuilder();
//            for (String p : profile.split(" , |;")) {
//                if (p != null) {
//                    x.append("/").append(p);
//                }
//            }
//            return pu.createQuery("Select u.user from AppUserProfileBinding u where :expr like concat('/',u.profile.name,'/')")
//                    .setParameter("expr", x.toString())
//                    .getResultList();
//        }
//        return Collections.EMPTY_LIST;
//    }

    private List<AppUser> filterUsersByExpression(int[] all, String expression) {
        if (all.length == 0) {
            return Collections.emptyList();
        }
        if (StringUtils.isEmpty(expression)) {
            return findUsers();
        }
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < all.length; i++) {
            if (i > 0) {
                ids.append(",");
            }
            ids.append(all[i]);
        }
        return UPA.getPersistenceUnit()
                .createQuery("Select x from AppUser x where x.id in (" + ids + ") " + expression)
                .getResultList();
    }

    public void setAppProperty(String propertyName, String userLogin, Object propertyValue) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppUser u = null;
        if (userLogin != null) {
            u = findUser(userLogin);
        }
        AppProperty ap = new AppProperty();
        ap.setEnabled(true);
        ap.setUser(u);
        ap.setPropertyName(propertyName);
        String propertyType = "null";
        String propertyValueString = "";
        if (propertyValue != null) {
            if (propertyValue instanceof String) {
                propertyValueString = String.valueOf(propertyValue);
                propertyType = "string";
            } else if (propertyValue instanceof Integer) {
                propertyValueString = String.valueOf(propertyValue);
                propertyType = "int";
            } else if (propertyValue instanceof Long) {
                propertyValueString = String.valueOf(propertyValue);
                propertyType = "long";
            } else if (propertyValue instanceof Double) {
                propertyValueString = String.valueOf(propertyValue);
                propertyType = "double";
            } else if (propertyValue instanceof Boolean) {
                propertyValueString = String.valueOf(propertyValue);
                propertyType = "boolean";
            } else {
                throw new IllegalArgumentException("Not supported " + propertyValue);
            }
        }
        ap.setPropertyType(propertyType);
        ap.setPropertyValue(propertyValueString);
        setAppProperty(ap);
    }

    public void setEnabledAppProperty(String propertyName, String userLogin, boolean enabled) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppProperty e = getAppProperty(propertyName, userLogin);
        if (e != null) {
            e.setEnabled(enabled);
            pu.merge(e);
        }
    }

    public Object getAppPropertyValue(AppProperty p) {
        if (p == null) {
            return null;
        }
        String t = p.getPropertyType();
        String v = p.getPropertyValue();
        if ("null".equalsIgnoreCase(t)) {
            return null;
        }
        if ("string".equalsIgnoreCase(t)) {
            return v;
        }
        if ("int".equalsIgnoreCase(t)) {
            if (v.isEmpty()) {
                return null;
            }
            return Integer.valueOf(v);
        }
        if ("long".equalsIgnoreCase(t)) {
            if (v.isEmpty()) {
                return null;
            }
            return Long.valueOf(v);
        }
        if ("double".equalsIgnoreCase(t)) {
            if (v.isEmpty()) {
                return null;
            }
            return Double.valueOf(v);
        }
        if ("boolean".equalsIgnoreCase(t)) {
            if (v.isEmpty()) {
                return null;
            }
            return Boolean.valueOf(v);
        }
        throw new IllegalArgumentException("Unsupported");
    }

    public Object getAppDataStoreValue(String propertyName, Class type, Object defaultValue) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppDataStore p = pu.findById(AppDataStore.class, propertyName);
        if (p == null) {
            return defaultValue;
        }
        String propertyValue = p.getPropertyValue();
        if (StringUtils.isEmpty(propertyValue)) {
            return defaultValue;
        }
        try {
            if (type.equals(String.class)) {
                return propertyValue;
            }
            if (type.equals(Integer.class)) {
                return Integer.parseInt(propertyValue);
            }
            if (type.equals(Long.class)) {
                return Long.parseLong(propertyValue);
            }
            if (type.equals(Double.class)) {
                return Double.parseDouble(propertyValue);
            }
            if (type.equals(Float.class)) {
                return Float.parseFloat(propertyValue);
            }
        } catch (Exception e) {
            return defaultValue;
        }
        return null;
    }

    public int updateIncrementAppDataStoreInt(String propertyName) {
        int d = (Integer) getAppDataStoreValue(propertyName, Integer.class, 0);
        d++;
        setAppDataStoreValue(propertyName, d);
        return d;
    }

    public long updateIncrementAppDataStoreLong(String propertyName) {
        long d = (Integer) getAppDataStoreValue(propertyName, Long.class, 0L);
        d++;
        setAppDataStoreValue(propertyName, d);
        return d;
    }

    public long updateMaxAppDataStoreLong(String propertyName, long value, boolean doLog) {
        long oldValue = (Long) getAppDataStoreValue(propertyName, Long.class, 0L);
        if (value > oldValue) {
            setAppDataStoreValue(propertyName, value);
            if (doLog) {
                LOG_APPLICATION_STATS.info("update " + propertyName + " : " + value);
            }
        }
        return value;
    }

    public void setAppDataStoreValue(String propertyName, Object defaultValue) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppDataStore p = pu.findById(AppDataStore.class, propertyName);
        boolean notFound = p == null;
        if (notFound) {
            p = new AppDataStore();
            p.setIdName(propertyName);
            p.setCreationDate(new Timestamp(System.currentTimeMillis()));
            p.setUpdateDate(p.getCreationDate());
        } else {
            p.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        }
        String propertyString = null;
        if (defaultValue != null) {
            Class<?> type = defaultValue.getClass();
            try {
                if (type.equals(String.class)) {
                    propertyString = defaultValue.toString();
                } else if (type.equals(Integer.class)) {
                    propertyString = defaultValue.toString();
                } else if (type.equals(Long.class)) {
                    propertyString = defaultValue.toString();
                } else if (type.equals(Double.class)) {
                    propertyString = defaultValue.toString();
                } else if (type.equals(Float.class)) {
                    propertyString = defaultValue.toString();
                }
            } catch (Exception e) {
                //;
            }
        }
        p.setPropertyValue(propertyString);
        if (notFound) {
            pu.persist(p);
        } else {
            pu.merge(p);
        }
    }

    public Object getOrCreateAppPropertyValue(String propertyName, String userLogin, Object value) {
        AppProperty p = getAppProperty(propertyName, userLogin);
        if (p != null) {
            if (p.isEnabled()) {
                Object v = getAppPropertyValue(p);
                if (v != null) {
                    return v;
                }
            }
        }
        if (value != null) {
            setAppProperty(propertyName, userLogin, value);
        }
        return value;
    }

    public Object getAppPropertyValue(String propertyName, String userLogin) {
        AppProperty p = getAppProperty(propertyName, userLogin);
        if (p != null && p.isEnabled()) {
            return getAppPropertyValue(p);
        }
        return null;
    }

    public Map<String, AppProperty> getAppPropertiesMap() {
        final EntityCache entityCache = cacheService.get(AppProperty.class);
        return entityCache.getProperty("getAppPropertyByPropertyAndLogin", new Action<Map<String, AppProperty>>() {
            @Override
            public Map<String, AppProperty> run() {
                MapList<Integer, AppProperty> values = entityCache.getValues();
                Map<String, AppProperty> map = new HashMap<String, AppProperty>();
                for (AppProperty o : values) {
                    String n = o.getPropertyName();
                    String u = o.getUser() == null ? null : o.getUser().getLogin();
                    map.put(StringUtils.nonNull(n) + "\n" + StringUtils.nonNull(u), o);
                }
                return map;
            }
        });
    }

    public AppProperty getAppProperty(String propertyName, String userLogin) {

        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppUser u = null;
        if (userLogin != null) {
            u = findUser(userLogin);
            if (u == null) {
                return null;
            }
        }

        Map<String, AppProperty> map = getAppPropertiesMap();
        AppProperty v = map.get(StringUtils.nonNull(propertyName) + "\n" + StringUtils.nonNull(userLogin));
        if (v == null) {
            map.get(StringUtils.nonNull(propertyName) + "\n" + StringUtils.nonNull(null));
        }
        return v;

//        QueryBuilder q = pu.createQueryBuilder(AppProperty.class);
//        q.byField("propertyName", propertyName);
//        if (u != null) {
//            q.byExpression("(userId=" + u.getId() + " or userId = null)");
//        } else {
//            q.byExpression("(userId = null)");
//        }
//        List<AppProperty> props = q.getResultList();
//        List<AppProperty> all = new ArrayList<AppProperty>(props);
//        Collections.sort(all, new Comparator<AppProperty>() {
//
//            @Override
//            public int compare(AppProperty o1, AppProperty o2) {
//                AppUser u1 = o1.getUser();
//                AppUser u2 = o2.getUser();
//                if (u1 == null && u2 != null) {
//                    return 1;
//
//                } else if (u1 != null && u2 == null) {
//                    return -1;
//                }
//                String s1 = o1.getPropertyName();
//                String s2 = o2.getPropertyName();
//                if (s1 == null) {
//                    s1 = "";
//                }
//                if (s2 == null) {
//                    s2 = "";
//                }
//                int x = s1.compareTo(s2);
//                if (x != 0) {
//                    return x;
//                }
//                x = s1.compareTo(s2);
//                if (x != 0) {
//                    return x;
//                }
//                return 0;
//            }
//        });
//        if (all.size() > 0) {
//            return all.get(0);
//        }
//        return null;
    }

    public void setAppProperty(AppProperty ap) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppProperty old = null;
        if (ap.getUser() == null) {
            old = pu.createQuery("Select a from AppProperty a where a.propertyName=:propertyName and a.userId=null")
                    .setParameter("propertyName", ap.getPropertyName())
                    .getFirstResultOrNull();
        } else {
            old = pu.createQuery("Select a from AppProperty a where a.propertyName=:propertyName and a.userId=:userId")
                    .setParameter("propertyName", ap.getPropertyName())
                    .setParameter("userId", ap.getUser().getId())
                    .getFirstResultOrNull();
        }
        if (old == null) {
            pu.persist(ap);
        } else {
            old.setEnabled(ap.isEnabled());
            old.setPropertyType(ap.getPropertyType());
            old.setPropertyValue(ap.getPropertyValue());
            pu.merge(old);
        }
    }

    public AppUser findUserByContact(int contactId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQueryBuilder(AppUser.class)
                .byField("contactId", contactId)
                .getFirstResultOrNull();
    }

    public AppContact findOrCreateContact(AppContact c) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppContact old = findContact(c);
        if (old != null) {
            return old;
        }
        pu.persist(c);
        return c;
    }

    public AppContact findContact(AppContact c) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String nin = c.getNin();
        if (!StringUtils.isEmpty(nin)) {
            nin = nin.trim();
            AppContact oldAcademicTeacher = pu.createQueryBuilder(AppContact.class)
                    .byField("nin", nin)
                    .getFirstResultOrNull();
            if (oldAcademicTeacher != null) {
                return oldAcademicTeacher;
            }
            StringBuilder s = new StringBuilder(nin);
            while (s.length() > 0 && s.charAt(0) == '0') {
                s.delete(0, 1);
            }
            if (s.length() > 0) {
                List<AppContact> possibleContacts = pu.createQuery("Select u from AppContact u where u.nin like :nin")
                        .setParameter("nin", "%" + s + "%")
                        .getResultList();
                for (AppContact o : possibleContacts) {
                    String nin1 = o.getNin();
                    if (!StringUtils.isEmpty(nin1)) {
                        nin1 = nin1.trim();
                        StringBuilder s1 = new StringBuilder(nin1);
                        while (s1.length() > 0 && s1.charAt(0) == '0') {
                            s1.delete(0, 1);
                        }
                        if (s1.toString().equals(s.toString())) {
                            //okkay found!
                            return o;
                        }
                    }
                }
            }
        } else {
            AppContact oldAcademicTeacher = pu.createQueryBuilder(AppContact.class)
                    .byField("firstName", c.getFirstName())
                    .byField("lastName", c.getLastName())
                    .getFirstResultOrNull();
            if (oldAcademicTeacher != null) {
                return oldAcademicTeacher;
            }
        }
        return null;
    }

    public String getActualLogin() {
        UserPrincipal up = UPA.getPersistenceUnit().getUserPrincipal();
        if (up != null && up.getObject() instanceof AppUser) {
            AppUser u = (AppUser) up.getObject();
            return u.getLogin();
        }
        UserSession us = getUserSession();
        if (us != null) {
            if (us.getUser() != null) {
                return us.getUser().getLogin();
            }
        }
        return null;
    }

    public boolean isUserSessionAdminOrUser(String login) {
        UserSession us = getUserSession();
        UserPrincipal up = UPA.getPersistenceUnit().getUserPrincipal();
        if (up != null && up.getObject() instanceof AppUser) {
            AppUser u = (AppUser) up;
            if (us.getUser() != null) {
                String login2 = u.getLogin();
                if (login2.equals(login)) {
                    return true;
                }
                if (login2.equals(us.getUser().getLogin())) {
                    return us.isAdmin();
                }
            }
            List<AppProfile> profiles = findProfilesByUser(u.getId());
            for (AppProfile p : profiles) {
                if (PROFILE_ADMIN.equals(p.getCode())) {
                    return true;
                }
            }
            return false;
        }
        if (us != null) {
            if (us.isAdmin()) {
                return true;
            }
            if (us.getUser().getLogin().equals(login)) {
                return true;
            }
        }
        return us != null && us.isAdmin();
    }

//    public String validateName(String text) {
//        //make it kamel based
//        boolean wasWhite = true;
//        char[] chars = (text == null ? "" : text).toCharArray();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < chars.length; i++) {
//            char aChar = chars[i];
//            if (Character.isWhitespace(aChar)) {
//                if (!(aChar == ' ' || aChar == '\t')) {
//                    aChar = ' ';
//                }
//                if (!wasWhite) {
//                    sb.append(aChar);
//                }
//                wasWhite = true;
//            } else if (wasWhite) {
//                sb.append(Character.toUpperCase(aChar));
//                wasWhite = false;
//            } else {
//                sb.append(Character.toLowerCase(aChar));
//                wasWhite = false;
//            }
//        }
//        return sb.toString().trim();
//    }

    public boolean isUserSessionAdmin() {
        UserSession us = null;
        try {
            us = getUserSession();
        } catch (Exception e) {
            //session not yet created!
            return true;
        }
        UserPrincipal up = UPA.getPersistenceUnit().getUserPrincipal();
        if (up != null) {
            if (up.getName().equals("<internal>")) {
                return true;
            }
            if (up.getObject() instanceof AppUser) {
                AppUser u = (AppUser) up.getObject();
                if (us.getUser() != null && u.getLogin().equals(us.getUser().getLogin())) {
                    return us.isAdmin();
                }
                List<AppProfile> profiles = findProfilesByUser(u.getId());
                for (AppProfile p : profiles) {
                    if (PROFILE_ADMIN.equals(p.getCode())) {
                        return true;
                    }
                }
            }
        }
        return us != null && us.isAdmin();
    }

    public boolean isSessionAdmin() {
        UserSession us = getUserSession();
        return us != null && us.isAdmin();
    }



    public String resolveLoginProposal(AppContact contact) {
        String fn = contact.getFirstName();
        String ln = contact.getLastName();
        if (fn == null) {
            fn = "";
        }
        if (ln == null) {
            ln = "";
        }
        return StringUtils.normalize(fn.toLowerCase()).replace(" ", "") + "." + StringUtils.normalize(ln.toLowerCase()).replace(" ", "");
    }

    public void runThread(Runnable r) {
        new SpringThread(r).start();
    }

    public AppConfig getCurrentConfig() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppConfig c = pu.findById(AppConfig.class, 1);
        //should exist;
        return c;
    }

    public AppUser createUser(AppContact contact, int userTypeId, int departmentId, boolean attachToExistingUser, String[] defaultProfiles,VrPasswordStrategy passwordStrategy) {
        AppUser u = findUserByContact(contact.getId());
        if (u == null) {
            if(passwordStrategy==null){
                passwordStrategy=VrPasswordStrategyRandom.INSTANCE;
            }
            String login = resolveLoginProposal(contact);
            if (StringUtils.isEmpty(login)) {
                login = "user";
            }
            String password = passwordStrategy.generatePassword(contact);
            u = findUser(login);
            if (u != null && u.getContact() != null) {
                if (u.getContact().getId() == contact.getId()) {
                    //this is the same user !! ok
                    return u;
                }
            }
            if (!attachToExistingUser || (u != null && u.getContact() != null)) {
                String y = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 2000);
                if (u != null) {
                    u = findUser(login + y);
                    if (u == null) {
                        //ok
                        login = login + y;
                    } else {
                        String chars = "abcdefghijklmnopqrstuvwxyz";
                        for (int i = 0; i < chars.length(); i++) {
                            u = findUser(login + y + chars.charAt(i));
                            if (u == null) {
                                login = login + y + chars.charAt(i);
                                break;
                            }
                        }
                    }
                }
                if (u != null) {
                    int index = 1;
                    while (true) {
                        u = findUser(login + y + "_" + index);
                        if (u == null) {
                            login = login + y + "_" + index;
                            break;
                        }
                        index++;
                    }
                }
                if (u != null) {
                    throw new IllegalArgumentException("Unable to add new user");
                }
            }
            AppUserType userType = findUserType(userTypeId);
            AppDepartment userDepatment = findDepartment(departmentId);
            if (u == null) {
                u = new AppUser();
                u.setLogin(login);
                u.setContact(contact);
                String pwd = password;
                u.setPassword(pwd);
                u.setPasswordAuto(pwd);
                u.setType(userType);
                u.setDepartment(userDepatment);
                u.setEnabled(true);
                UPA.getPersistenceUnit().persist(u);
            } else {
                u.setContact(contact);
                u.setType(userType);
                UPA.getPersistenceUnit().merge(u);
            }
        }
        if (defaultProfiles != null) {
            for (String defaultProfile : defaultProfiles) {
                if (!StringUtils.isEmpty(defaultProfile)) {
                    userAddProfile(u.getId(), defaultProfile);
                }
            }
        }
        return u;
    }

    public boolean isComplexProfileExpr(String s) {
        for (char c : s.toCharArray()) {
            switch (c) {
                case '(':
                case ')':
                case '[':
                case ']':
                case '+':
                case '-':
                case '_':
                case ':':
                case ',':
                case ';':
                case ' ':
                case '&':
                case '|': {
                    return true;
                }
                default: {
                    break;
                }
            }
        }
        return false;
    }

    public String validateProfileName(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '(':
                case ')':
                case '[':
                case ']':
                case '+':
                case '-':
                case '_':
                case ':':
                case ',':
                case ';':
                case ' ':
                case '&':
                case '|': {
                    sb.append("_");
                    break;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    public void passwd(String login, String oldPassword, String newPassword) {
        if (newPassword == null) {
            newPassword = "";
        }
        if (newPassword.trim().length() == 0) {
            throw new RuntimeException("Mot de passe trop court");
        }
        if (newPassword.length() == 5) {
            throw new RuntimeException("Mot de passe trop court");
        }
        if (StringUtils.isEmpty(login)) {
            login = getActualLogin();
        }
        if (isUserSessionAdmin()) {
            AppUser u = findUser(login);
            if (u == null) {
                throw new RuntimeException("User not found " + login);
            }

            u.setPassword(newPassword);
            UPA.getPersistenceUnit().merge(u);
        } else {
            AppUser u = findUser(login, oldPassword);
            if (u == null) {
                throw new RuntimeException("Invalid User or Password for " + login);
            }
            u.setPassword(newPassword);
            UPA.getPersistenceUnit().merge(u);
        }
    }

    public AppPeriod getCurrentPeriod() {
        AppConfig currentConfig = getCurrentConfig();
        return currentConfig == null ? null : currentConfig.getMainPeriod();
    }

    public AppPeriod findPeriodOrMain(int id) {
        AppPeriod p = findPeriod(id);
        if (p == null) {
            p = getCurrentPeriod();
        }
        return p;
    }

    public AppPeriod findPeriod(int id) {
        return (AppPeriod) cacheService.get(AppPeriod.class).getValues().getByKey(id);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return (AppPeriod) pu.createQuery("Select u from AppPeriod u where u.id=:id")
//                .setParameter("id", id).getEntity();
    }

    public List<AppPeriod> findValidPeriods() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AppPeriod u where (u.snapshotName=null or u.snapshotName='') order by u.name desc")
                .getResultList();
    }

    public List<AppPeriod> findNavigatablePeriods() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AppPeriod u where u.navigatable=true and (u.snapshotName=null or u.snapshotName='') order by u.name  desc")
                .getResultList();
    }

    public AppPeriod findPeriod(String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppPeriod) pu.createQuery("Select u from AppPeriod u where (u.snapshotName=null or u.snapshotName='') and u.name=:name")
                .setParameter("name", name).getFirstResultOrNull();
    }

    public AppUser getCurrentUser() {
        UserSession s = getUserSession();
        return s == null ? null : s.getUser();
    }

    public Integer getCurrentUserId() {
        AppUser s = getCurrentUser();
        return s == null ? null : s.getId();
    }

    public String getCurrentUserLogin() {
        AppUser s = getCurrentUser();
        return s == null ? null : s.getLogin();
    }

    public UserSession getUserSession() {
        return VrApp.getContext().getBean(UserSession.class);
    }

    public void logout() {
        final UserSession s = getUserSession();
        AppUser user = s == null ? null : s.getUser();
        String login = user == null ? null : user.getLogin();
        int id = user == null ? -1 : user.getId();
        if (s != null && user != null) {
            if (s.isImpersonating()) {
                trace.trace("logout", "successful logout " + login + " to " + s.getRootUser().getLogin(),
                        login + " => "
                                + s.getRootUser().getLogin(),
                        "/System/Access", null, null, login, id, Level.INFO, s.getClientIpAddress()
                );
                s.setUser(s.getRootUser());
                s.setRootUser(null);
                buildSession(s, s.getUser());
            } else {
                trace.trace("logout", "successful logout " + login,
                        login,
                        "/System/Access", null, null, login, id, Level.INFO, s.getClientIpAddress()
                );
                getSessions().onDestroy(s);
            }
        }
    }

    public void logout(String sessionId) {
        UserSession currentSession0 = null;
        try {
            currentSession0 = getUserSession();
        } catch (Exception any) {
            //
        }
        final UserSession currentSession = currentSession0;
        UserSession s = getSessions().getUserSession(sessionId);
        AppUser user = s == null ? null : s.getUser();
        String login = user == null ? null : user.getLogin();
        int id = user == null ? -1 : user.getId();
        if (s != null && user != null && currentSession != null) {
            trace.trace("logout", "force logout " + login,
                    login,
                    "/System/Access", null, null, currentSession.getUser().getLogin(), id, Level.INFO, s.getClientIpAddress()
            );
            getSessions().onDestroy(s);
        }
    }

    public AppUser impersonate(String login, String password) {
        if (login == null) {
            login = "";
        }
        //login is always lower cased and trimmed!
        login = login.trim().toLowerCase();
        UserSession s = getUserSession();
        if (s.isAdmin() && !s.isImpersonating()) {
            AppUser user = findEnabledUser(login, password);
            if (user != null) {
                trace.trace("impersonate", "successfull impersonate of " + login, login, "/System/Access", null, null, s.getUser().getLogin(),
                        s.getUser().getId(), Level.INFO, s.getClientIpAddress()
                );
            } else {
                user = findUser(login);
                if (user != null) {
                    if (!user.isEnabled()) {
                        trace.trace("impersonate", "successful impersonate of " + login + ". but user is not enabled!", login, "/System/Access", null, null, s.getUser().getLogin(), s.getUser().getId(), Level.WARNING, s.getClientIpAddress()
                        );
                    } else {
                        trace.trace("impersonate", "successful impersonate of " + login + ". but password " + password + " seems not to be correct", login, "/System/Access", null, null, s.getUser().getLogin(), s.getUser().getId(), Level.WARNING, s.getClientIpAddress());
                    }
                } else {
                    trace.trace(
                            "impersonate", "failed impersonate of " + login, login, "/System/Access", null, null, s.getUser().getLogin(), s.getUser().getId(), Level.SEVERE, s.getClientIpAddress()
                    );
                }
            }
            if (user != null) {
                s.setRootUser(s.getUser());
                s.setUser(user);
                buildSession(s, user);
            }
            onPoll();
            return user;
        } else {
            trace.trace(
                    "impersonate", "failed impersonate of " + login + ". not admin or already impersonating", login, "/System/Access", null, null, s.getUser().getLogin(), s.getUser().getId(), Level.WARNING, s.getClientIpAddress()
            );
        }
        return null;
    }

    public String getDomain() {
        return UPA.getPersistenceUnit().getName();
    }

    public AppUser login(String login, String password) {
        if (login == null) {
            login = "";
        }
        //login is always lower cased and trimmed!
        login = login.trim().toLowerCase();

        final AppUser user = findEnabledUser(login, password);
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
            UserSession s = getUserSession();
            s.setDestroyed(false);
            s.setDomain(getDomain());
            final ActiveSessionsTracker activeSessionsTracker = getSessions();
            activeSessionsTracker.onCreate(s);
            //update stats
            UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    updateMaxAppDataStoreLong("usersCountPeak", activeSessionsTracker.getActiveSessionsCount(), true);
                }
            });
            trace.trace("login", "successful", login, "/System/Access", null, null, login, user.getId(), Level.INFO, s.getClientIpAddress());
            getUserSession().setConnexionTime(user.getLastConnexionDate());
            getUserSession().setUser(user);
            buildSession(s, user);
            onPoll();
        } else {
            UserSession s = getUserSession();
            s.reset();
            AppUser user2 = findUser(login);
            if (user2 == null) {
                trace.trace("login", "login not found. Failed as " + login + "/" + password, login + "/" + password, "/System/Access", null, null, (login == null || login.length() == 0) ? "anonymous" : login, -1, Level.SEVERE, s.getClientIpAddress());
            } else if (user2.isDeleted() || !user2.isEnabled()) {
                trace.trace("login", "invalid state. Failed as " + login + "/" + password, login + "/" + password
                        + ". deleted=" + user2.isDeleted()
                        + ". enabled=" + user2.isEnabled(), "/System/Access", null, null, (login == null || login.length() == 0) ? "anonymous" : login, user2.getId(), Level.SEVERE, s.getClientIpAddress()
                );
            } else {
                trace.trace(
                        "login", "invalid password. Failed as " + login + "/" + password, login + "/" + password,
                        "/System/Access", null, null, (login == null || login.length() == 0) ? "anonymous" : login, user2.getId(),
                        Level.SEVERE, s.getClientIpAddress()
                );
            }
        }
        return user;
    }


//    private AppUser findUser(String login, String password) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return (AppUser) pu
//                .createQuery("Select u from AppUser u "
//                        + "where "
//                        + "u.login=:login "
//                        + "and u.password=:password")
//                .setParameter("login", login)
//                .setParameter("password", password)
//                .getEntity();
//    }

    protected void buildSession(UserSession s, AppUser user) {
        final List<AppProfile> userProfiles = findProfilesByUser(user.getId());
        Set<String> userProfilesNames = new HashSet<>();
        for (AppProfile u : userProfiles) {
            userProfilesNames.add(u.getName());
        }
        s.setProfiles(userProfiles);
        StringBuilder ps = new StringBuilder();
        for (AppProfile up : userProfiles) {
            if (ps.length() > 0) {
                ps.append(", ");
            }
            ps.append(up.getName());
        }
        s.setProfileNames(userProfilesNames);
        s.setProfilesString(ps.toString());
        s.setAdmin(false);
        s.setDepartmentManager(-1);
        s.setManager(false);
        s.setRights(findUserRights(user.getId()));
        if (user.getLogin().equalsIgnoreCase("admin") || userProfilesNames.contains(CorePlugin.PROFILE_ADMIN)) {
            s.setAdmin(true);
        }
        if (userProfilesNames.contains(CorePlugin.PROFILE_HEAD_OF_DEPARTMENT)) {
            if (user.getDepartment() != null) {
                AppUser d = findHeadOfDepartment(user.getDepartment().getId());
                if (d != null && d.getId() == user.getId()) {
                    s.setManager(true);
                    s.setDepartmentManager(d.getDepartment().getId());
                }
            }
        }
        for (String mp : getManagerProfiles()) {
            if (userProfilesNames.contains(mp)) {
                s.setManager(true);
            }
        }
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

    public PluginInfo getPluginInfo(String bundleId) {
        getPlugins();
        return bundles.get(bundleId);
    }

    private void buildPluginInfos() {
        if (this.components == null) {
//            Map<String, List<PluginInfo>> bundleToComponents = new HashMap<>();
            Map<String, PluginBundle> bundles = new HashMap<>();
            Map<String, PluginComponent> components = new HashMap<>();
//            Map<String, PluginInfo> componentToBundle = new HashMap<>();

//            bundleToComponents = new HashMap<>();
            try {
                //first load all
                for (URL url : Collections.list(Thread.currentThread().getContextClassLoader().getResources("/META-INF/vr-plugin.properties"))) {
                    PluginComponent e = PluginComponent.parsePluginComponent(url);
                    if (e != null) {
                        if (e.getName() == null) {
                            String id = e.getId();
                            if (id != null) {
                                if (id.contains(":")) {
                                    e.setName(id.substring(id.indexOf(':') + 1));
                                } else {
                                    e.setName(id);
                                }
                            }
                        }
                        components.put(e.getId(), e);
                    }
                }
                for (PluginComponent e : components.values()) {
                    String bundleId = e.getBundleId();
                    PluginBundle bundle = bundles.get(bundleId);
                    if (bundle == null) {
                        bundle = new PluginBundle();
                        bundle.setId(bundleId);
                        bundles.put(bundleId, bundle);
                    }
                    bundle.addComponent(e);
                }

                //reevaluate dependencies
                //reevaluate versions

                for (PluginBundle bundle : bundles.values()) {
                    for (String depIdAndVer : bundle.getDependencies()) {
                        String depId = depIdAndVer;
                        if (depIdAndVer.contains(":")) {
                            depId = depIdAndVer.substring(0, depIdAndVer.lastIndexOf(":"));
                        }
                        PluginComponent comp = components.get(depId);
                        if (comp != null) {
                            if (!bundle.getId().equals(comp.getBundleId())) {
                                bundle.getBundleDependencies().add(comp.getBundleId());
                            }
                        } else {
                            bundle.getExtraDependencies().add(depId);
                        }
                    }
                }
                this.bundles = bundles;
                this.components = components;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public PluginComponent getPluginComponent(Class type) {
        buildPluginInfos();
        String id = null;
        try {
            URL url = getPluginComponentURL(type, "/META-INF/vr-plugin.properties");
            if (url != null) {
                id = PluginComponent.parsePluginInfoId(url);
            }
//            id = PluginInfo.parsePluginInfoId(type.getResource("/META-INF/vr-plugin.properties"));
        } catch (IOException e) {
            //
        }
        return components.get(id);
    }

    public URL getPluginComponentURL(Class type, String path) {
        try {
            String location = type.getProtectionDomain().getCodeSource().getLocation().toString();
            if (location.endsWith("/")) {
                return new URL(location + path);
            }
            return new URL("jar:" + location + "!" + path);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public PluginComponent getPluginComponent(Object obj) {
        return getPluginComponent(PlatformReflector.getTargetClass(obj));
    }

    public PluginBundle getPluginBundle(Class type) {
        PluginComponent comp = getPluginComponent(type);
        if (comp == null) {
            return null;
        }
        return comp.getBundle();
    }

    public PluginBundle getPluginBundle(Object type) {
        PluginComponent comp = getPluginComponent(type);
        if (comp == null) {
            return null;
        }
        return comp.getBundle();
    }

    public List<PersistenceUnit> getPersistenceUnits() {
        return new ArrayList<>(UPA.getPersistenceGroup("").getPersistenceUnits());
    }

    public List<String> getPluginIds() {
        List<String> plugins=new ArrayList<>();
        for (Plugin plugin : getPlugins()) {
            plugins.add(plugin.getId());
        }
        return plugins;
    }

    public List<String> getPluginBeans() {
        List<String> plugins=new ArrayList<>();
        for (Plugin plugin : getPlugins()) {
            plugins.addAll(plugin.getBeanNames());
        }
        return plugins;
    }

    public List<Plugin> getPlugins() {
        if (plugins == null) {
            buildPluginInfos();
            String[] appPluginBeans = VrApp.getContext().getBeanNamesForAnnotation(AppPlugin.class);
            ListValueMap<String, Object> instances = new ListValueMap<>();
            ListValueMap<String, String> beanNames = new ListValueMap<>();
            List<String> errors = new ArrayList<>();
            Arrays.sort(appPluginBeans); //just to have a reproducible error if any
            for (String beanName : appPluginBeans) {
                Object bean = VrApp.getContext().getBean(beanName);
                PluginBundle bundle = getPluginBundle(bean);
                if (bundle != null) {
                    instances.put(bundle.getId(), bean);
                    beanNames.put(bundle.getId(), beanName);
                } else {
                    errors.add(beanName);
                    log.log(Level.SEVERE, "Unable to find bundle Instance for " + beanName + "... some thing is wrong...");
                }
            }

            if (errors.size() > 0) {
                Enumeration<URL> resources = null;
                try {
                    resources = Thread.currentThread().getContextClassLoader().getResources("/META-INF/vr-plugin.properties");
                    for (URL url : Collections.list(resources)) {
                        log.log(Level.SEVERE, "\t resolved plugin url : " + url);
                    }
//                    String beanName=errors.get(0);
//                    Object bean=VrApp.getContext().getBean(beanName);
//                    PluginBundle bundle = getPluginBundle(bean);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<Plugin> plugins = new ArrayList<>(bundles.size());
            for (PluginBundle pluginInfo : bundles.values()) {
                List<Object> objects = instances.get(pluginInfo.getId());
                if (objects == null || objects.size() == 0) {
                    if (objects == null) {
                        objects = new ArrayList<>();
                    }
                    log.log(Level.INFO, "Plugin " + pluginInfo.getId() + " defines no configurator class");
                }
                List<String> bnames = beanNames.get(pluginInfo.getId());
                if (bnames == null || bnames.size() == 0) {
                    if (bnames == null) {
                        bnames = new ArrayList<>();
                    }
                    //log.log(Level.INFO, "Plugin " + pluginInfo.getId() + " defines no configurator class");
                }
                Plugin p = new Plugin(objects, bnames,pluginInfo);
                plugins.add(p);
            }
            Collections.sort(plugins);
            this.plugins = plugins;
        }
        return this.plugins;
    }

    public AppVersion getAppVersion() {
        if (appVersion == null) {
            AppVersion _appVersion = new AppVersion();
            InputStream appVersionStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/vr-app.version");
            if (appVersionStream == null) {
                //consider all defaults
            } else {
                java.util.Properties p = new Properties();
                try {
                    p.load(appVersionStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                _appVersion.setShortName(p.getProperty("short-name"));
                _appVersion.setLongName(p.getProperty("long-name"));
                _appVersion.setVersion(p.getProperty("version"));
                _appVersion.setBuildNumber(p.getProperty("build-number"));
                _appVersion.setBuildDate(p.getProperty("build-date"));
                _appVersion.setAuthor(p.getProperty("author"));
                _appVersion.setDefaultTheme(p.getProperty("default-theme"));

            }
            appVersion = _appVersion;
        }
        return appVersion;
    }

    public String getNativeFileSystemPath() {
        return cacheService.get(AppProperty.class).getProperty("System.FileSystem.Path", new Action<String>() {
            @Override
            public String run() {
                String appName="vr";//change me
                String home = System.getProperty("user.home");
                home = home.replace("\\", "/");
                String domain = getDomain();
                if (StringUtils.isEmpty(domain)) {
                    domain = "";
                }
                String path = (String) getOrCreateAppPropertyValue("System.FileSystem", null,
                        home+"/workspace/"+appName+"/filesystem/" + domain
                );
                return path;
            }
        });
    }

    public VirtualFileSystem getFileSystem() {
        return cacheService.get(AppProperty.class).getProperty("System.FileSystem", new Action<VirtualFileSystem>() {
            @Override
            public VirtualFileSystem run() {
                String path = getNativeFileSystemPath();
                VirtualFileSystem fileSystem = new VrFS().subfs(path, "vrfs");
                fileSystem.get("/").mkdirs();
                new File(path + PATH_LOG).mkdirs();
                return fileSystem;
            }
        });
    }

    public VFile getUserDocumentsFolder(final String login) {
        return getUserFileSystem(login).get("/" + FOLDER_MY_DOCUMENTS);
    }

    public VFile getUserFolder(final String login) {
        AppUser u = findUser(login);
        if (u != null) {
            AppUserType t = u.getType();
            String typeName = t == null ? "NoType" : AppUserType.getCodeOrName(t);
            final String path = "/Documents/ByUser/" + normalizeFilePath(typeName) + "/" + normalizeFilePath(login);
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
                    VirtualFileACL v = getFileSystem().getACL(path);
                    if (!v.isReadOnly()) {
                        v.setOwner(login);
                    }
                    return null;
                }

            }, null);
            return getFileSystem().get(path);
        }
        return null;
    }

    public VFile getUserSharedFolder() {
        final String path = "/Documents/Shared/";
        UPA.getContext().invokePrivileged(new VoidAction() {

            @Override
            public void run() {
                getFileSystem().mkdirs(path);
                VirtualFileACL v = getFileSystem().getACL(path);
            }

        });
        return getFileSystem().get(path);
    }

    public VFile getProfileFolder(final String profile) {
        AppProfile u = findProfileByName(profile);
        if (u != null) {
            final String path = "/Documents/ByProfile/" + normalizeFilePath(profile);

            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
                    VirtualFileACL v = getFileSystem().getACL(path);
                    if (!v.isReadOnly()) {
                        v.setPermissionListDirectory(profile);
                    }
                    return null;
                }

            }, null);

            return getFileSystem().get(path);
        }
        return null;
    }

    public VFile getUserTypeFolder(int userTypeId) {
        AppUserType u = findUserType(userTypeId);
        if (u != null) {
            final String path = "/Documents/ByUserType/" + normalizeFilePath(AppUserType.getCodeOrName(u));
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
//                    VirtualFileACL v = getFileSystem().getACL(path);
//                    v.setOwner(login);
                    return null;
                }

            }, null);
            return getFileSystem().get(path);
        }
        return null;
    }

    public VirtualFileSystem getUserHomeFileSystem(final String login) {
        VFile home = getUserFolder(login);
        return getFileSystem().subfs(home.getPath());
    }

    public VirtualFileSystem getUserFileSystem(final String login) {
        AppUser u = findUser(login);
        if (u != null) {
            VFile home = getUserFolder(login);
            final VirtualFileSystem me = getFileSystem().subfs(home.getPath());
            MountableFS mfs = VFS.createMountableFS("user:" + login);
            try {
                mfs.mount("/" + FOLDER_MY_DOCUMENTS, me);
                List<AppProfile> profiles = findProfilesByUser(u.getId());
                for (AppProfile p : profiles) {
                    if (CorePlugin.PROFILE_ADMIN.equals(p.getName())) {
                        //this is admin
                        mfs.mount("/" + FOLDER_ALL_DOCUMENTS, getFileSystem());
                    }
                }
                VrFSTable t0 = getVrFSTable();
                Map<Integer, VrFSTable> usersVrFSTable = getUsersVrFSTable();
                List<VrFSTable> all=new ArrayList<>();
                all.add(t0);
                all.addAll(usersVrFSTable.values());

                for (AppProfile p : profiles) {
                    String profileMountPoint = "/" + normalizeFilePath(p.getName()) + " Documents";
                    VirtualFileSystem profileFileSystem = getProfileFileSystem(p.getName(), t0);
                    if(profileFileSystem.get("/").listFiles().length>0){
                        mfs.mount(profileMountPoint, profileFileSystem);
                    }
                }

                for (VrFSTable t : all) {
                    for (VrFSEntry e : t.getEntries(login, "User")) {
                        mfs.mount("/" + e.getMountPoint(), getFileSystem().subfs(e.getLinkPath()));
                    }
                    for (VrFSEntry e : t.getEntriesByType("Profile")) {
                        //if (isComplexProfileExpr(e.getFilterName())) {
                        if (userMatchesProfileFilter(u.getId(), e.getFilterName())) {
                            mountSubFS(mfs, e);
                        }
                        //}
                    }
                }

            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
            return mfs;
        } else {
            return VFS.createEmptyFS();
        }
    }

    private void mountSubFS(MountableFS mfs, VrFSEntry e) throws IOException {
        String linkPath = e.getLinkPath();
        if (linkPath.contains("*")) {
            VFile[] files = getFileSystem().get("/").find(linkPath, new VFileFilter() {
                @Override
                public boolean accept(VFile pathname) {
                    return pathname.isDirectory();
                }
            });
            ListFS lfs = VFS.createListFS(e.getMountPoint());
            for (VFile file : files) {
                lfs.addOrRename(file.getName(), file, null);
            }
            mfs.mount("/" + e.getMountPoint(), lfs);
        } else {
            mfs.mount("/" + e.getMountPoint(), getFileSystem().subfs(e.getLinkPath()));
        }
    }

    public VirtualFileSystem getProfileFileSystem(String profileName) {
        return getProfileFileSystem(profileName, null);
    }

    public VirtualFileSystem getProfileFileSystem(String profileName, VrFSTable t) {
        AppProfile u = findProfileByName(profileName);
        if (u != null) {
            final String path = "/Documents/ByProfile/" + normalizeFilePath(profileName);
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
//                    VrACL v = (VrACL) getFileSystem().getACL(path);
//                    v.setOwner(login);
                    return null;
                }

            }, null);
            VirtualFileSystem pfs = getFileSystem().subfs(path);
            MountableFS mfs = null;
            try {
                if (t == null) {
                    t = getVrFSTable();
                }
                mfs = VFS.createMountableFS("profile:" + profileName);
                mfs.mount("/", pfs);
                for (VrFSEntry e : t.getEntries(profileName, "Profile")) {
                    mountSubFS(mfs, e);
                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
            if (mfs == null) {
                return pfs;
            }
            //VFile[] all = mfs.listFiles("/");
            return mfs;
        } else {
            return VFS.createEmptyFS();
        }
    }

    private void commitVrFSTable(VrFSTable tab) {
        getFileSystem().mkdirs("/Config");
        OutputStream out = null;
        try {
            try {
                out = getFileSystem().getOutputStream("/Config/fstab");
                tab.store(out);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (Exception exx) {
            //ignore it
        }
    }

    public void removeUserLinkPathEntry(int userId,String linkPath) throws IOException {
        VrFSTable table = getUserVrFSTable(userId);
        VrFSEntry[] entries = table.getEntries();
        for (int i = 0; i < entries.length; i++) {
            VrFSEntry e = entries[i];
            if (e.getLinkPath().equals(linkPath)) {
                table.removeEntry(i);
                saveUserVrFSTable(userId,table);
                return;
            }
        }
    }

    public void setUserLinkPathEntry(int userId,VrFSEntry entry) throws IOException {
        if(StringUtils.isEmpty(entry.getMountPoint())){
            removeUserLinkPathEntry(userId,entry.getLinkPath());
        }else {
            VrFSTable table = getUserVrFSTable(userId);
            String lp = entry.getLinkPath();
            for (VrFSEntry e : table.getEntries()) {
                if (e.getLinkPath().equals(lp)) {
                    e.setFilterName(entry.getFilterName());
                    e.setFilterType(entry.getFilterType());
                    e.setLinkPath(entry.getLinkPath());
                    e.setMountPoint(entry.getMountPoint());
                    saveUserVrFSTable(userId, table);
                    return;
                }
            }
            VrFSEntry e = new VrFSEntry();
            e.setFilterName(entry.getFilterName());
            e.setFilterType(entry.getFilterType());
            e.setLinkPath(entry.getLinkPath());
            e.setMountPoint(entry.getMountPoint());
            table.addEntry(e);
            saveUserVrFSTable(userId, table);
        }
    }

    public void saveUserVrFSTable(int userId, VrFSTable table) throws IOException {
        AppUser u = findUser(userId);
        if(u==null){
            throw new IllegalArgumentException("Invalid user");
        }
        VirtualFileSystem fs = getFileSystem();
        fs.get("/Config").mkdirs();
        VFile file = fs.get("/Config/" + u.getLogin() + ".fstab");
        table.store(file);
    }

    public VrFSTable getUserVrFSTable(int userId) {
        VrFSTable t=new VrFSTable();
        AppUser u = findUser(userId);
        if(u==null){
            return null;
        }
        VirtualFileSystem fs = getFileSystem();
        VFile file = fs.get("/Config/" + u.getLogin() + ".fstab");
        if(file.isFile()){
            t.loadSilently(file);
        }
        return t;
    }

    private Map<Integer,VrFSTable> getUsersVrFSTable() {
        HashMap<Integer, VrFSTable> map = new HashMap<>();
        VirtualFileSystem fs = getFileSystem();
        if (fs.exists("/Config")) {
            for (VFile userfstab:fs.get("/Config").listFiles(new VFileFilter() {
                     @Override
                     public boolean accept(VFile pathname) {
                         return pathname.getName().endsWith(".fstab");
                     }
                 })) {
                String login = userfstab.getName().substring(0,userfstab.getName().length()-".fstab".length());//PathInfo.create(userfstab.getName()).getNamePart();
                AppUser u = findUser(login);
                if(u!=null){
                    VrFSTable t = new VrFSTable();
                    t.loadSilently(userfstab);

                    for (VrFSEntry vrFSEntry : t.getEntries()) {
                        String m = vrFSEntry.getMountPoint();
                        if(m==null){
                            m="unknown";
                        }
                        m=m.trim();
                        if(m.endsWith("/")){
                            m=m.substring(0,m.length()-1);
                        }
                        if(m.isEmpty()){
                            m="unknown";
                        }
                        vrFSEntry.setMountPoint("/"+ m +"#"+u.getId());
                    }
                    map.put(u.getId(),t);
                }
            }
        }
        return map;
    }

    private VrFSTable getVrFSTable() {
        VrFSTable t = new VrFSTable();
        InputStream in = null;
        try {
            try {
                if (getFileSystem().exists("/Config/fstab")) {
                    in = getFileSystem().getInputStream("/Config/fstab");
                    t.load(in);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (Exception exx) {
            //ignore it
        }
        return t;
    }

    public long getDownloadsCount(final VFile file) {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<Long>() {

            @Override
            public Long run() {
                VirtualFileACL a = file.getACL();
                String d = a.getProperty("downloads");
                if (d == null) {
                    d = "0";
                }
                long dd = 0;
                try {
                    dd = Long.parseLong(d);
                } catch (Exception ee) {
                    //
                }
                return dd;
            }

        });
    }

    public void markDownloaded(final VFile file) {
        UPA.getPersistenceUnit().invokePrivileged(new Action<Object>() {

            @Override
            public Object run() {
                VirtualFileACL a = file.getACL();
                if (!a.isReadOnly()) {
                    String d = a.getProperty("downloads");
                    if (d == null) {
                        d = "0";
                    }
                    long dd = 0;
                    try {
                        dd = Long.parseLong(d);
                    } catch (Exception ee) {
                        //
                    }
                    a.setProperty("downloads", String.valueOf(dd + 1));
                }
//                    VrACL v = (VrACL) getFileSystem().getACL(path);
//                    v.setOwner(login);
                return null;
            }

        });
    }

//    public boolean isUserSessionHeadOfDepartment() {
//        UserSession sm = UserSession.get();
//        AppUser user = (sm == null) ? null : sm.getUser();
//        if (user == null || user.getDepartment() == null) {
//            return false;
//        }
//        AppUser d = findHeadOfDepartment(user.getDepartment().getId());
//        return d != null && d.getId() == user.getId();
//    }

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
        cacheService.invalidate();
    }

    public String getDefaultUserTheme() {
        return (String) getOrCreateAppPropertyValue("System.DefaultTheme", null, "");
    }

    public String getCurrentUserTheme() {
        UserSession session = getUserSession();
        String val = null;
        if (session != null && session.getUser() != null) {
            val = UPA.getPersistenceUnit().invokePrivileged(new Action<String>() {
                @Override
                public String run() {
                    return (String) getOrCreateAppPropertyValue("System.DefaultTheme", session.getUser().getLogin(), "");
                }
            });
        }
        return val == null ? "" : val;
    }

    public void setCurrentUserTheme(String theme) {
        UserSession session = getUserSession();
        if (session != null && session.getUser() != null) {
            UPA.getContext().invokePrivileged(new VoidAction() {
                                                  @Override
                                                  public void run() {
                                                      setAppProperty("System.DefaultTheme", session.getUser().getLogin(), theme);
                                                  }
                                              }
            );

        }
    }

    public void setUserTheme(int userId, String theme) {
        AppUser user = findUser(userId);
        if (user != null) {
            setAppProperty("System.DefaultTheme", user.getLogin(), theme);
        }
    }

    private List<String> getOrderedPlugins() {
        final Map<String, Object> s = VrApp.getContext().getBeansWithAnnotation(AppPlugin.class);
        ArrayList<String> ordered = new ArrayList<>();
        for (String k : s.keySet()) {
            Object o1 = VrApp.getContext().getBean(k);
            AppPlugin a1 = (AppPlugin) PlatformReflector.getTargetClass(o1).getAnnotation(AppPlugin.class);
            for (String d : a1.dependsOn()) {
                VrApp.getContext().getBean(d);
            }
            ordered.add(k);
        }
        Collections.sort(ordered, new Comparator<String>() {

            @Override
            public int compare(String s1, String s2) {
                Object o1 = VrApp.getContext().getBean(s1);
                AppPlugin a1 = (AppPlugin) PlatformReflector.getTargetClass(o1).getAnnotation(AppPlugin.class);
                Object o2 = VrApp.getContext().getBean(s1);
                AppPlugin a2 = (AppPlugin) PlatformReflector.getTargetClass(o2).getAnnotation(AppPlugin.class);
                HashSet<String> hs1 = new HashSet<>(Arrays.asList(a1.dependsOn()));
                HashSet<String> hs2 = new HashSet<>(Arrays.asList(a2.dependsOn()));
                if (!s1.equals("coreService")) {
                    hs1.add("coreService");
                }
                if (!s2.equals("coreService")) {
                    hs2.add("coreService");
                }
                if (Arrays.asList(a1.dependsOn()).contains(s2)) {
                    return -1;
                }
                if (Arrays.asList(a2.dependsOn()).contains(s2)) {
                    return 1;
                }
                return 0;
            }
        });
        return ordered;
    }

    private void tryInstall() {
        boolean alwaysInstall = false;
        boolean alwaysNonCoherent = false;
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ArrayList<Plugin> toInstall = new ArrayList<>();
        ArrayList<Plugin> toStart = new ArrayList<>();
        for (Plugin pp : getPlugins()) {
            String sver = pp.getInfo().getVersion();
            String pluginId = pp.getId();
            net.vpc.app.vainruling.core.service.model.AppVersion v = pu.findById(net.vpc.app.vainruling.core.service.model.AppVersion.class, pluginId);
            if (v == null) {
                v = pu.findById(net.vpc.app.vainruling.core.service.model.AppVersion.class, pluginId);
            }
            boolean ignore = false;
            if (v == null || !sver.equals(v.getServiceVersion()) || !v.isCoherent()) {
                if (v != null && !v.isActive()) {
                    log.log(Level.INFO, "Plugin {0} is deactivated (version {1})", new Object[]{pluginId, pp.getInfo().getVersion()});
                    //ignore
                    ignore = true;
                    if (alwaysInstall) {
                        toInstall.add(pp);
                    }
                } else {
                    if (v == null) {
                        v = new net.vpc.app.vainruling.core.service.model.AppVersion();
                        v.setActive(true);
                        final net.vpc.upa.types.Timestamp dte = new net.vpc.upa.types.Timestamp();
                        v.setInstallDate(dte);
                        v.setServiceName(pluginId);
                        v.setServiceVersion(sver);
                        v.setUpdateDate(dte);
                        v.setCoherent(true);
                        pu.persist(v);

                    } else {
                        v.setActive(true);
                        final net.vpc.upa.types.Timestamp dte = new net.vpc.upa.types.Timestamp();
                        v.setServiceVersion(sver);
                        v.setUpdateDate(dte);
                        v.setCoherent(true);
                        pu.merge(v);
                    }
                    toInstall.add(pp);
                }
            } else {
                if (alwaysInstall) {
                    toInstall.add(pp);
                }
                log.log(Level.INFO, "Plugin {0} is uptodate ({1})", new Object[]{pluginId, pp.getInfo().getVersion()});
            }
            if (!ignore) {
                toStart.add(pp);
            }
        }
        HashSet<String> nonCoherent = new HashSet<>();
        Collections.sort(toInstall);
        for (Plugin plugin : toInstall) {
            if (alwaysInstall || !nonCoherent.contains(plugin.getId())) {
                try {
                    plugin.install();
                } catch (Exception e) {
                    nonCoherent.add(plugin.getId());
                    log.log(Level.SEVERE, "Error Starting " + plugin.getId(), e);
                    net.vpc.app.vainruling.core.service.model.AppVersion v = pu.findById(net.vpc.app.vainruling.core.service.model.AppVersion.class, plugin.getId());
                    v.setCoherent(false);
                    pu.merge(v);

                }
            }
        }
        for (Plugin plugin : toInstall) {
            if (alwaysNonCoherent || !nonCoherent.contains(plugin.getId())) {
                try {
                    plugin.installDemo();
                } catch (Exception e) {
                    nonCoherent.add(plugin.getId());
                    log.log(Level.SEVERE, "Error Starting " + plugin.getId(), e);
                    net.vpc.app.vainruling.core.service.model.AppVersion v = pu.findById(net.vpc.app.vainruling.core.service.model.AppVersion.class, plugin.getId());
                    v.setCoherent(false);
                    pu.merge(v);
                }
            }
        }

        Collections.sort(toStart);
        for (Plugin plugin : toStart) {
//            i18n.register("i18n." + plugin.getId() + ".dictionary");
//            i18n.register("i18n." + plugin.getId() + ".presentation");
//            i18n.register("i18n." + plugin.getId() + ".service");
            try {
                plugin.start();
            } catch (Exception e) {
                nonCoherent.add(plugin.getId());
                log.log(Level.SEVERE, "Error Starting " + plugin.getId(), e);
                net.vpc.app.vainruling.core.service.model.AppVersion v = pu.findById(net.vpc.app.vainruling.core.service.model.AppVersion.class, plugin.getId());
                v.setCoherent(false);
                pu.merge(v);

            }
        }
    }

    public ArticlesItem findArticle(int articleId) {
        return UPA.getPersistenceUnit().findById(ArticlesItem.class, articleId);
    }

    public ArticlesDisposition findArticleDisposition(int articleDispositionId) {
        return UPA.getPersistenceUnit().findById(ArticlesDisposition.class, articleDispositionId);
    }

    public List<ArticlesDispositionGroupType> findArticleDispositionGroupTypes() {
        EntityCache entityCache = cacheService.get(ArticlesDispositionGroupType.class);
        return entityCache.getValues();
    }

    public List<ArticlesDispositionGroup> findArticleDispositionGroups(int siteType) {
        final EntityCache entityCache = cacheService.get(ArticlesDispositionGroup.class);
        return entityCache.getProperty("findArticleDispositionGroups:" + siteType, new Action<List<ArticlesDispositionGroup>>() {
            @Override
            public List<ArticlesDispositionGroup> run() {
                return UPA.getPersistenceUnit()
                        .createQuery("Select u from ArticlesDispositionGroup u where u.typeId=:typeId order by u.index,u.title")
                        .setParameter("typeId", siteType)
                        .getResultList();
            }
        });
    }

    public ArticlesDispositionGroup findArticleDispositionGroup(String name) {
        final EntityCache entityCache = cacheService.get(ArticlesDispositionGroup.class);
        Map<String, ArticlesDispositionGroup> m = entityCache.getProperty("findArticleDispositionGroup", new Action<Map<String, ArticlesDispositionGroup>>() {
            @Override
            public Map<String, ArticlesDispositionGroup> run() {
                Map<String, ArticlesDispositionGroup> m = new HashMap<String, ArticlesDispositionGroup>();
                MapList<Integer, ArticlesDispositionGroup> values = entityCache.getValues();
                for (ArticlesDispositionGroup u : values) {
                    String key = u.getName();
                    if (!StringUtils.isEmpty(key)) {
                        m.put(key, u);
                    }
                }
                return m;
            }
        });
        return m.get(name);
    }

    public ArticlesDisposition findArticleDisposition(String name) {
        final EntityCache entityCache = cacheService.get(ArticlesDisposition.class);
        Map<String, ArticlesDisposition> m = entityCache.getProperty("findArticleDispositionByName", new Action<Map<String, ArticlesDisposition>>() {
            @Override
            public Map<String, ArticlesDisposition> run() {
                Map<String, ArticlesDisposition> m = new HashMap<String, ArticlesDisposition>();
                MapList<Integer, ArticlesDisposition> values = entityCache.getValues();
                for (ArticlesDisposition u : values) {
                    String key = u.getName();
                    if (!StringUtils.isEmpty(key)) {
                        m.put(key, u);
                    }
                }
                return m;
            }
        });
        return m.get(name);
    }

    public List<ArticlesFile> findArticlesFiles(int articleId) {
        return UPA.getPersistenceUnit().createQuery("Select u from ArticlesFile u where "
                + " u.articleId=:articleId"
                + " order by "
                + "  u.position asc"
        )
                .setParameter("articleId", articleId)
                .getResultList();
    }

    public List<ArticlesFile> findArticlesFiles(int[] articleIds) {
        if (articleIds.length == 0) {
            return Collections.emptyList();
        }
        StringBuilder ids_string = new StringBuilder();
        ids_string.append(articleIds[0]);
        for (int i = 1; i < articleIds.length; i++) {
            ids_string.append(",");
            ids_string.append(articleIds[i]);
        }
        return UPA.getPersistenceUnit().createQuery("Select u from ArticlesFile u where "
                + " u.articleId in(" + ids_string + ")"
                + " order by "
                + "  u.position asc"
        )
                .getResultList();
    }

    public FullArticle findFullArticle(int id) {
        //invoke privileged to find out article not created by self.
        //when using non privileged mode, users can see SOLELY articles they have created
        ArticlesItem a = UPA.getPersistenceUnit().invokePrivileged(new Action<ArticlesItem>() {
            @Override
            public ArticlesItem run() {
                return findArticle(id);
            }
        });
        if (a == null) {
            return null;
        }
        String aname = a.getLinkText();
        String aurl = a.getLinkURL();
        String acss = a.getLinkClassStyle();
        List<ArticlesFile> att = new ArrayList<>();
        if (!StringUtils.isEmpty(aname) || !StringUtils.isEmpty(aurl)) {
            if (StringUtils.isEmpty(aname)) {
                aname = VrUtils.getURLName(aurl);
            }
            if (StringUtils.isEmpty(aname)) {
                aname = "NoName";
            }
            ArticlesFile baseArt = new ArticlesFile();
            baseArt.setId(-1);
            baseArt.setName(aname);
            baseArt.setPath(aurl);
            baseArt.setStyle(acss);
            att.add(baseArt);
        }
        List<ArticlesFile> c = findArticlesFiles(a.getId());
        if (c != null) {
            att.addAll(c);
        }
        FullArticle f = new FullArticle(a, att);
        return f;
    }

    public List<FullArticle> findFullArticlesByUserAndCategory(final String login, int dispositionGroupId, boolean includeNoDept, final String disposition) {
        return UPA.getContext().invokePrivileged(new Action<List<FullArticle>>() {

            @Override
            public List<FullArticle> run() {
                List<FullArticle> all = new ArrayList<>();
                List<ArticlesItem> articles = findArticlesByUserAndCategory(login, dispositionGroupId, includeNoDept, disposition);
                int[] articleIds = new int[articles.size()];
                for (int i = 0; i < articleIds.length; i++) {
                    articleIds[i] = articles.get(i).getId();
                }
                ListValueMap<Integer, ArticlesFile> articlesFilesMap = new ListValueMap<Integer, ArticlesFile>();
                for (ArticlesFile articlesFile : findArticlesFiles(articleIds)) {
                    articlesFilesMap.put(articlesFile.getArticle().getId(), articlesFile);
                }
                for (ArticlesItem a : articles) {
                    ArticlesDisposition d = a.getDisposition();
                    if(d!=null){
                        int periodCalendarType=-1;
                        if(d.getPeriodType()!=null && d.getMaxPeriod()>0){
                            switch (d.getPeriodType()){
                                case DAY:
                                case WEEK:
                                case MONTH:
                                case YEAR:
                                    {
                                    periodCalendarType=d.getPeriodType().getCalendarId();
                                    break;
                                }
                            }
                        }
                        if(periodCalendarType>0) {
                            DateTime dd = a.getSendTime();
                            Date d2 = net.vpc.common.util.DateUtils.add(new Date(), periodCalendarType, -d.getMaxPeriod());
                            if (dd.compareTo(d2) < 0) {
                                continue;
                            }
                        }

                    }
                    String aname = a.getLinkText();
                    String aurl = a.getLinkURL();
                    String acss = a.getLinkClassStyle();
                    List<ArticlesFile> att = new ArrayList<>();
                    if (!StringUtils.isEmpty(aname) || !StringUtils.isEmpty(aurl)) {
                        if (StringUtils.isEmpty(aname)) {
                            aname = VrUtils.getURLName(aurl);
                        }
                        if (StringUtils.isEmpty(aname)) {
                            aname = "NoName";
                        }
                        ArticlesFile baseArt = new ArticlesFile();
                        baseArt.setId(-1);
                        baseArt.setName(aname);
                        baseArt.setPath(aurl);
                        baseArt.setStyle(acss);
                        att.add(baseArt);
                    }

                    List<ArticlesFile> c = articlesFilesMap.get(a.getId());
                    if (c != null) {
                        att.addAll(c);
                    }
                    FullArticle f = new FullArticle(a, att);
                    all.add(f);
                }
                return all;
            }

        }, null);
    }

    public List<ArticlesItem> findArticlesByUserAndCategory(String login, int dispositionGroupId, boolean includeNoDept, String... dispositions) {
        if (dispositions.length == 0) {
            return Collections.EMPTY_LIST;
        }
        StringBuilder queryStr = new StringBuilder("Select u from ArticlesItem u where ");
        queryStr.append(" u.deleted=false ");
        queryStr.append(" and u.archived=false");
        queryStr.append(" and (");
        Map<String, Object> disps = new HashMap<>();
        for (int i = 0; i < dispositions.length; i++) {
            String disposition = dispositions[i];
            if (i > 0) {
                queryStr.append(" or ");
            }
            queryStr.append(" u.disposition.name=:disposition" + i);
            disps.put("disposition" + i, disposition);
        }
        queryStr.append(" )");
        if (includeNoDept) {
            queryStr.append(" and (u.dispositionGroupId=:dispositionGroupId or u.dispositionGroupId=null)");
            disps.put("dispositionGroupId", dispositionGroupId);
        } else {
            queryStr.append(" and (u.dispositionGroupId=:dispositionGroupId)");
            disps.put("dispositionGroupId", dispositionGroupId);
        }

        queryStr.append(" order by "
                + "  u.position"
                + ", u.important desc"
                + ", u.sendTime desc");

        Query query = UPA.getPersistenceUnit().createQuery(queryStr.toString()).setParameters(disps);
        List<ArticlesItem> all = query.getResultList();

        return filterByProfilePattern(all, null, login, new ProfilePatternFilter<ArticlesItem>() {
            @Override
            public String getProfilePattern(ArticlesItem t) {
                AppUser s = t.getSender();
                String author = s == null ? null : s.getLogin();
                String p = t.getRecipientProfiles();
                if (StringUtils.isEmpty(p)) {
                    return p;
                }
                if (StringUtils.isEmpty(author)) {
                    return p;
                }
                return "( " + p + " ) , " + author;
            }
        });
    }

    public void generateRSS(String login, String rss, OutputStream out) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ArticlesDisposition t = pu.findByMainField(ArticlesDisposition.class, "rss." + rss);
        List<FullArticle> articles = findFullArticlesByUserAndCategory(login, -1, true, "rss." + rss);
        try {
            String feedType = "rss_2.0";
//            String fileName = "feed.xml";

            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType(feedType);

            feed.setTitle("ENISo Computer Engeneering Department");
            feed.setLink("http://www.eniso.info");
            feed.setDescription(t == null ? null : t.getDescription());

            List entries = new ArrayList();
            SyndEntry entry;
            SyndContent description;
            for (FullArticle art : articles) {
                entry = new SyndEntryImpl();
                entry.setTitle(art.getSubject());
                entry.setLink(art.getLinkURL() == null ? feed.getLink() : art.getLinkURL());
                entry.setPublishedDate(art.getPublishTime());
                description = new SyndContentImpl();
                description.setType("text/html;charset=UTF-8");
                description.setValue(art.getContent());
                entry.setDescription(description);
                entry.setAuthor(art.getUser() == null ? null : art.getUser().resolveFullName());
                entries.add(entry);
            }

            feed.setEntries(entries);

            Writer writer = new OutputStreamWriter(out);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    @EntityActionList(entityType = ArticlesItem.class)
//    public String[] findArticleActions(){
//        return new String[]{"SendEmail"};
//    }

    public String getArticlesProperty(String value) {
        return getArticlesProperties().get(value);
    }

    public String getArticlesPropertyOrCreate(String value, String defaultValue) {
        String s = getArticlesProperties().get(value);
        if (StringUtils.isEmpty(s) && !StringUtils.isEmpty(defaultValue)) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            pu.invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    ArticlesProperty p = new ArticlesProperty();
                    p.setName(value);
                    p.setValue(defaultValue);
                    pu.persist(p);
                }
            });
            s = defaultValue;
        }
        return s;
    }

    public Map<String, String> getArticlesProperties() {
        final EntityCache entityCache = cacheService.get(ArticlesProperty.class);
        Map<String, String> m = entityCache.getProperty("getArticlesProperties", new Action<Map<String, String>>() {
            @Override
            public Map<String, String> run() {
                Map<String, String> m = new HashMap<String, String>();
                MapList<Integer, ArticlesProperty> values = entityCache.getValues();
                for (ArticlesProperty u : values) {
                    String key = u.getName();
                    if (!StringUtils.isEmpty(key)) {
                        m.put(key, u.getValue());
                    }
                }
                return m;
            }
        });
        return m;
    }

    public ActiveSessionsTracker getSessions() {
        return sessions;
    }

    public Object resolveId(String entityName, Object t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        if (t instanceof Document) {
            return entity.getBuilder().documentToId((Document) t);
        }
        return entity.getBuilder().objectToId(t);
    }

    public void save(String entityName, Object t) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Object id = resolveId(entityName, t);
        List<Field> pf = entity.getIdFields();
        boolean persist = false;
        if (id == null) {
            persist = true;
        } else if (pf.size() <= 1) {
            DataType dt = pf.get(0).getDataType();
            if (dt instanceof ManyToOneType) {
                persist = entity.findById(id) == null;
            } else if (pf.size() == 1 && pf.get(0).getModifiers().contains(FieldModifier.PERSIST_SEQUENCE)) {
                persist = Objects.equals(dt.getDefaultUnspecifiedValue(), id);
            } else {
                persist = entity.findById(id) == null;
            }
        } else {
            persist = entity.findById(id) == null;
        }
        if (persist) {
            pu.persist(entityName, t);
//            trace.inserted(t, getClass(), Level.FINE);
        } else {
            pu.merge(entityName, t);
//            trace.updated(t, old, getClass(), Level.FINE);
        }
    }

    public void erase(String entityName, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        if (trace.accept(entity)) {
            trace.removed(entityName, pu.findById(entityName, id), entity.getParent().getPath(), Level.FINE);
        }
        pu.remove(entityName, RemoveOptions.forId(id));
    }

    public boolean isSoftRemovable(String entityName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        return entity.containsField("deleted");
    }

    public void remove(String entityName, Object id) {
        if (!isSoftRemovable(entityName)) {
            erase(entityName, id);
            return;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Document t = pu.findDocumentById(entityName, id);
        if (t != null) {
            //check if already soft deleted
            Boolean b = (Boolean) entity.getField("deleted").getValue(t);
            if (b != null && b.booleanValue()) {
                erase(entityName, id);
            } else {
                UserSession session = VrApp.getContext().getBean(UserSession.class);
                if (entity.containsField("deleted")) {
                    t.setBoolean("deleted", true);
                }
                if (entity.containsField("deletedBy")) {
                    t.setString("deletedBy", session.getUser().getLogin());
                }
                if (entity.containsField("deletedOn")) {
                    t.setDate("deletedOn", new Timestamp(System.currentTimeMillis()));
                }
                pu.merge(entityName, t);
                if (trace.accept(entity)) {
                    trace.softremoved(entityName, pu.findById(entityName, id), entity.getParent().getPath(), Level.FINE);
                }
            }
        }
    }

    public String getObjectName(String entityName, Object obj) {
        if (obj == null) {
            return "NO_NAME";
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Field mf = entity.getMainField();
        if (mf == null) {
            return obj.toString();
        }
        return String.valueOf(entity.getBuilder().objectToDocument(obj, true).getObject(mf.getName()));
    }

    //    public boolean isEntityAction(String type, String action, Object object) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        Entity entity = pu.getEntity(type);
//        return VrApp.getBean(PluginManagerService.class).isEnabledEntityAction(entity.getEntityType(), action, object);
//    }
//
//    public ActionInfo[] getEntityActionList(String type, Object object) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        Entity entity = pu.getEntity(type);
//        return VrApp.getBean(PluginManagerService.class).getEntityActionList(entity.getEntityType(), object);
//    }
//    public Object invokeEntityAction(String type, String action, Object object, Object[] args) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        Entity entity = pu.getEntity(type);
//        if ("Archive".equals(action)) {
//            Object id = resolveId(object);
//            Object t = pu.findById(type, id);
//            if (t != null) {
//                Document r = entity.getBuilder().objectToDocument(t, true);
//                if (entity.containsField("archived")) {
//                    r.setBoolean("archived", true);
//                }
////            Object old = pu.findById(type, id);
//                pu.merge(t);
//                trace.archived(pu.findById(type, id), entity.getParent().getPath(), Level.FINE);
////            trace.updated(t, old, getClass(), Level.FINE);
//            }
//            return null;
//        }
//
//        return VrApp.getBean(PluginManagerService.class).invokeEntityAction(entity.getEntityType(), action, object, args);
//    }
    public void archive(String entityName, Object object) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Object id = resolveId(entityName, object);
        Object t = pu.findById(entityName, id);
        if (t != null) {
            Document r = entity.getBuilder().objectToDocument(t, true);
            if (entity.containsField("archived")) {
                r.setBoolean("archived", true);
            }
//            Object old = pu.findById(type, id);
            pu.merge(entityName, t);
            if (trace.accept(entity)) {
                trace.archived(entityName, pu.findById(entityName, id), entity.getParent().getPath(), Level.FINE);
            }
//            trace.updated(t, old, getClass(), Level.FINE);
        }

//        invokeEntityAction(type, "Archive", object, null);
    }

    public Object find(Class type, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(type, id);
    }

    public Object find(String entityName, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findById(entityName, id);
    }

    public Document findDocument(String entityName, Object id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findDocumentById(entityName, id);
    }

    public boolean isArchivable(String entityName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        return entity.containsField("archived");
    }

    public List<Object> findAll(String entityName, Map<String, Object> criteria) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        QueryBuilder cc = pu.createQueryBuilder(entityName)
                .orderBy(entity.getListOrder())
                .setEntityAlias("o");
        if (criteria != null) {
            for (Map.Entry<String, Object> entrySet : criteria.entrySet()) {
                cc.byExpression(new Equals(new UserExpression("o." + entrySet.getKey()), new Literal(entrySet.getValue(), null)));
            }
        }
        Chronometer c = new Chronometer();
        List<Object> entityList = cc
                .getResultList();
        entityList.size();
        c.stop();
        return entityList;
    }

    public List<NamedId> findAllNamedIds(Relationship r, Map<String, Object> criteria, Object currentInstance) {
        final String aliasName = "o";
        Expression relationExpression = r.createTargetListExpression(currentInstance, aliasName);
        return findAllNamedIds(r.getTargetEntity(), criteria, relationExpression);
    }

    public List<NamedId> findAllNamedIds(Entity entity, Map<String, Object> criteria, Expression condition) {
        final String aliasName = "o";


        PersistenceUnit pu = UPA.getPersistenceUnit();
        Select q = new Select();

        Field primaryField = entity.getIdFields().get(0);
        q.field(" o." + primaryField.getName(), "id");
        Field mainField = entity.getMainField();
        if (mainField == null) {
            mainField = primaryField;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(aliasName + "." + mainField.getName());
        while (mainField.getDataType() instanceof ManyToOneType) {
            Entity t = ((ManyToOneType) mainField.getDataType()).getRelationship().getTargetEntity();
            mainField = t.getMainField();
            sb.append("." + mainField.getName());
        }
        q.field(sb.toString(), "name");

        q.from(entity.getName(), aliasName);
        q.orderBy(entity.getListOrder());
        Expression where = null;
        if (criteria != null) {
            for (Map.Entry<String, Object> entrySet : criteria.entrySet()) {
                where = And.create(where, new Equals(new UserExpression(aliasName + "." + entrySet.getKey()), new Literal(entrySet.getValue(), null)));
            }
        }
        q.where(condition);
        q.where(where);
        Chronometer c = new Chronometer();
        List<NamedId> entityList = pu.createQuery(q)
                .getResultList(NamedId.class);
        entityList.size();
        c.stop();
        return entityList;
    }

    public List<NamedId> findAllNamedIds(Entity r, Map<String, Object> criteria, Object currentInstance) {
        String entityName = r.getName();
        final String aliasName = "o";
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        Select q = new Select();

        Field primaryField = entity.getIdFields().get(0);
        q.field(" o." + primaryField.getName(), "id");
        Field mainField = entity.getMainField();
        if (mainField == null) {
            mainField = primaryField;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(aliasName + "." + mainField.getName());
        while (mainField.getDataType() instanceof ManyToOneType) {
            Entity t = ((ManyToOneType) mainField.getDataType()).getRelationship().getTargetEntity();
            mainField = t.getMainField();
            sb.append("." + mainField.getName());
        }
        q.field(sb.toString(), "name");

        q.from(entityName, aliasName);
        q.orderBy(entity.getListOrder());
        Expression where = null;
        if (criteria != null) {
            for (Map.Entry<String, Object> entrySet : criteria.entrySet()) {
                where = And.create(where, new Equals(new UserExpression(aliasName + "." + entrySet.getKey()), new Literal(entrySet.getValue(), null)));
            }
        }
        q.where(where);
        Chronometer c = new Chronometer();
        List<NamedId> entityList = pu.createQuery(q)
                .getResultList(NamedId.class);
        entityList.size();
        c.stop();
        return entityList;
    }

    public long findCountByFilter(String entityName, String criteria, ObjSearch objSearch, Map<String, Object> parameters) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String qq = "Select count(1) from " + entityName + " o ";
        Expression filterExpression = null;
        if (criteria != null) {
            filterExpression = new UserExpression(criteria);
        }
        if (objSearch != null) {
            String c = objSearch.createPreProcessingExpression(entityName, parameters, "os");
            if (c != null) {
                if (filterExpression == null) {
                    filterExpression = new UserExpression(c);
                } else {
                    filterExpression = new And(filterExpression, new UserExpression(c));
                }
            }
        }
        if (filterExpression != null) {
            qq += " where " + filterExpression;
        }
        Query query = pu.createQuery(qq);
        if (parameters != null) {
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                query.setParameter(pp.getKey(), pp.getValue());
            }
        }
        Number nn = (Number) query.getSingleValue();
        return nn.longValue();
    }

    public List<Object> findByFilter(String entityName, String criteria, ObjSearch objSearch, Map<String, Object> parameters) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        QueryBuilder q = pu
                .createQueryBuilder(entityName)
                .setEntityAlias("o")
                .orderBy(entity.getListOrder());
        Expression filterExpression = null;
        if (criteria != null) {
            filterExpression = new UserExpression(criteria);
        }
        if (objSearch != null) {
            String c = objSearch.createPreProcessingExpression(entityName, parameters, "os");
            if (c != null) {
                if (filterExpression == null) {
                    filterExpression = new UserExpression(c);
                } else {
                    filterExpression = new And(filterExpression, new UserExpression(c));
                }
            }
        }
        if (filterExpression != null) {
            q.byExpression(filterExpression);
        }
        if (parameters != null) {
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                q.setParameter(pp.getKey(), pp.getValue());
            }
        }
        List<Object> list = q.getResultList();
        if (objSearch != null) {
            list = objSearch.filterList(list, entityName);
        }
        return list;
    }

    public List<Document> findDocumentsByFilter(String entityName, String criteria, ObjSearch objSearch, String textSearch, Map<String, Object> parameters) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityName);
        QueryBuilder q = pu
                .createQueryBuilder(entityName)
                .setEntityAlias("o")
                .orderBy(entity.getListOrder());
        Expression filterExpression = null;
        if (!org.apache.commons.lang.StringUtils.isEmpty(criteria)) {
            filterExpression = new UserExpression(criteria);
        }
        if (objSearch != null) {
            String c = objSearch.createPreProcessingExpression(entityName, parameters, "os");
            if (c != null) {
                if (filterExpression == null) {
                    filterExpression = new UserExpression(c);
                } else {
                    filterExpression = new And(filterExpression, new UserExpression(c));
                }
            }
        }
        if (filterExpression != null) {
            q.byExpression(filterExpression);

        }
        if (parameters != null) {
            for (Map.Entry<String, Object> pp : parameters.entrySet()) {
                q.setParameter(pp.getKey(), pp.getValue());
            }
        }
        Object appPropertyValue = getAppPropertyValue("System.MaxLoadedObjects", null);
        if (appPropertyValue == null) {
            appPropertyValue = 7000;
        }
        q.setTop(Convert.toInt(appPropertyValue, IntegerParserConfig.LENIENT_F));
        List<Document> list = q.getDocumentList();
        if (objSearch != null) {
            list = objSearch.filterList(list, entityName);
        }
        if (!StringUtils.isEmpty(textSearch)) {

            list = createSearch(null, entity, textSearch).filterList(list, entityName);
        }
        return list;
    }

    public ObjSearch createSearch(String name, Entity entity, String expression) {
        if (StringUtils.isEmpty(expression)) {
            return new ObjSimpleSearch(null);
        }
        String f = entity.getProperties().getString(UIConstants.ENTITY_TEXT_SEARCH_FACTORY);
        if (StringUtils.isEmpty(f)) {
            return new ObjSimpleSearch(expression);
        }
        EntityObjSearchFactory g = null;
        try {
            g = (EntityObjSearchFactory) Class.forName(f).newInstance();
            ObjSearch objSearch = g.create(name, entity, expression);
            if (objSearch == null) {
                return new ObjSimpleSearch(expression);
            }
            return objSearch;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<Object> findAll(String type) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        return pu.createQueryBuilder(type)
                .orderBy(entity.getListOrder())
                .getResultList();
    }

    public List<Object> findByField(Class type, String field, Object value) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(type);
        DataType dt = entity.getField(field).getDataType();
        return pu.createQueryBuilder(type).setEntityAlias("o")
                .byExpression(new And(new Var(new Var("o"), field), new Literal(value, dt)))
                .orderBy(entity.getListOrder())
                .getResultList();
    }

    public VFile uploadFile(VFile baseFile, UploadedFileHandler event) throws IOException {
        String fileName = normalizeFilePath(event.getFileName());
        VFile newFile = baseFile;//.get(fileName);
        VFile baseFolder = null;
        if (newFile.exists() && newFile.isDirectory()) {
            baseFolder = newFile;
            int pos = 0;
            while (true) {
                String n = fileName;
                if (pos > 0) {
                    n = FileUtils.changeFileSuffix(new File(fileName), "-" + pos).getPath();
                }
                VFile vFile = newFile.get(n);
                if (!vFile.exists()) {
                    newFile = vFile;
                    break;
                }
                pos++;
            }
        } else if (newFile.exists()) {
            boolean doOverride = false;
            //check if alreay selected
            if (event.acceptOverride(newFile)) {
                doOverride = true;
            }
            if (!doOverride) {
                throw new IOException(fileName + " already exists please select to force override.");
            }
        }
        String tempPath = CorePlugin.PATH_TEMP + "/Files/" + VrUtils.date(new Date(), "yyyy-MM-dd-HH-mm")
                + "-" + UserSession.getCurrentLogin();
        CorePlugin fsp = VrApp.getBean(CorePlugin.class);
        String p = fsp.getNativeFileSystemPath() + tempPath;
        new File(p).mkdirs();
        File f = new File(p, fileName);
        try {
            event.write(f.getPath());
            //do work here
            int count = 1;//
            if (count > 0) {
                return newFile; //addInfoMessage(event.getFileName() + " successfully uploaded.");
            } else {
                return newFile;
                //addWarnMessage(null, event.getFileName() + " is uploaded but nothing is updated.");
            }
        } finally {
            //should not delete the file!
            VirtualFileSystem nfs = VFS.createNativeFS();
            nfs.get(f.getPath()).copyTo(newFile);
        }
    }

    public String normalizeFilePath(String path) {
        char[] chars = StringUtils.normalize(path).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '°': {
                    chars[i] = 'o';
                    break;
                }
                case '"':
                case '\'':
                case '?':
                case '*':
                case ':':
                case '%':
                case '|':
                case '<':
                case '>': {
                    chars[i] = ' ';
                    break;
                }
            }
        }
        return new String(chars);
    }

    public List<String> getAllCompletionLists(int monitorUserId) {
        String[] appPluginBeans = VrApp.getContext().getBeanNamesForType(CompletionProvider.class);
        TreeSet<String> cats=new TreeSet<>();
        for (String beanName : appPluginBeans) {
            CompletionProvider bean = (CompletionProvider) VrApp.getContext().getBean(beanName);
            cats.addAll(bean.getCompletionLists(monitorUserId));
        }
        return new ArrayList<>(cats);
    }

    public List<CompletionInfo> findAllCompletions(int monitorUserId, String category, String objectType, Object objectId, Level minLevel){
        String[] appPluginBeans = VrApp.getContext().getBeanNamesForType(CompletionProvider.class);
        List<CompletionInfo> cats=new ArrayList<>();
        for (String beanName : appPluginBeans) {
            CompletionProvider bean = (CompletionProvider) VrApp.getContext().getBean(beanName);
            cats.addAll(bean.findCompletions(monitorUserId, category, objectType, objectId, minLevel));
        }
        return new ArrayList<>(cats);
    }

    private static class InitData {

        long now;
        AppProfile adminProfile;
        AppUser admin;
        AppUserType adminType;
        List<AppCivility> civilities;
        List<AppGender> genders;
        List<AppDepartment> departments;
    }
}
