/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.agent.ActiveSessionsTracker;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.cache.EntityCache;
import net.vpc.app.vainruling.core.service.fs.VrFS;
import net.vpc.app.vainruling.core.service.fs.VrFSEntry;
import net.vpc.app.vainruling.core.service.fs.VrFSTable;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.notification.PollAware;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.AppVersion;
import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.CustomTextFormatter;
import net.vpc.common.util.MapList;
import net.vpc.common.vfs.*;
import net.vpc.upa.*;
import net.vpc.upa.expressions.Equals;
import net.vpc.upa.expressions.Literal;
import net.vpc.upa.expressions.Var;
import net.vpc.upa.types.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vpc
 */
@AppPlugin(version = "1.10")
@UpaAware
public class CorePlugin {

    public static final java.util.logging.Logger LOG_APPLICATION_STATS = java.util.logging.Logger.getLogger(CorePlugin.class.getName() + ".Stats");
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CorePlugin.class.getName());

    public static final String PATH_LOG = "/Var/Log";
    public static final String PATH_TEMP = "/Var/Temp";
    public static final String USER_ADMIN = "admin";
    public static final String PROFILE_ADMIN = "Admin";
    public static final String PROFILE_HEAD_OF_DEPARTMENT = "HeadOfDepartment";
    public static String FOLDER_MY_DOCUMENTS = "Mes Documents";
    public static String FOLDER_ALL_DOCUMENTS = "Tous";
    public static String FOLDER_BACK = "<Dossier Parent>";
    public static final String RIGHT_FILESYSTEM_WRITE = "Custom.FileSystem.Write";

    public static final Set<String> ADMIN_ENTITIES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Trace", "User", "UserProfile", "UserProfileBinding", "UserProfileRight")));
    private VirtualFileSystem fileSystem;
    private String nativeFileSystemPath;
    @Autowired
    private TraceService trace;
    @Autowired
    private CacheService cacheService;
    private boolean updatingPoll = false;
    private Plugin[] plugins;
    private AppVersion appVersion;
//    private final Map<String,Object> globalCache=new HashMap<>();

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

//    private void init(){
//    }
//    public UserSession getUserSession() {
//        return VrApp.getContext().getBean(UserSession.class);
//    }

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
        return (AppUserType) pu.findByMainField(AppUserType.class, name);
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
                .getEntityList();
        List<AppRightName> allRigths = pu.createQuery("Select u from AppRightName u")
                .getEntityList();
        for (AppRightName r : allRigths) {
            allMap.put(r.getName(), r);
        }
        for (AppRightName r : oldRigths) {
            existing.put(r.getName(), r);
            allMap.remove(r.getName());
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
                .getEntityList();
        List<AppUser> allRigths = pu.createQuery("Select u from AppUser u")
                .getEntityList();
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
                .getEntityList();
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
                .getEntityList();
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
                .getEntity();
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
                .getEntity();
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
            AppUser u = findUser(userId);
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
                .getEntity();
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
//            List<AppUserProfileBinding> appUserProfileBindings = pu.createQuery("Select u from AppUserProfileBinding  u").getEntityList();
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

//    public Map<Integer,List<AppProfile>> findProfilesMapByUserId() {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        List<AppUserProfileBinding> appUserProfileBindings = pu.createQuery("Select u from AppUserProfileBinding  u").getEntityList();
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
//                .getEntityList();
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
//                .getEntityList();
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
//            for (Record record : pu.createQuery("Select u.id id, u.login login from AppUser u")
//                    .getRecordList()) {
//                ret.put(record.getString("login"),record.getInt("id"));
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
                .getEntityList();
    }

    public List<AppProfile> findProfiles() {
        return cacheService.getList(AppProfile.class);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select u from AppProfile u")
//                .getEntityList();
    }

    public List<AppUser> findUsers() {
        return cacheService.getList(AppUser.class);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select u from AppUser  u")
//                .getEntityList();
    }

    public List<AppUser> findEnabledUsers(Integer userType) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (userType == null) {
            return pu.createQuery("Select u from AppUser  u where u.enabled=true and u.deleted=false")
                    .getEntityList();
        }
        return pu.createQuery("Select u from AppUser  u where u.enabled=true and u.deleted=false and u.typeId=:userType")
                .setParameter("userType", userType)
                .getEntityList();
    }

    public List<AppUser> findEnabledUsers() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AppUser  u where u.enabled=true and u.deleted=false")
                .getEntityList();
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
                .getEntity();
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
                .getEntity();
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
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getEntity();
//        return (AppCompany) pu.findById(AppCompany.class, id);
    }

    public AppCompany findCompany(String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppCompany) pu.createQuery("Select u from AppCompany u where u.name=:name").setParameter("name", name)
                .setHint(QueryHints.NAVIGATION_DEPTH, 3)
                .getEntity();
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
    public void installService() {
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
        VrApp.getBean(CorePlugin.class).createRight("Custom.FileSystem.RootFileSystem", "Root FileSystem Access");
        VrApp.getBean(CorePlugin.class).createRight("Custom.FileSystem.MyFileSystem", "My FileSystem Access");
        VrApp.getBean(CorePlugin.class).createRight(RIGHT_FILESYSTEM_WRITE, "Enable Write Access for File System");
    }

    @Start
    protected void onStart() {
        String home = System.getProperty("user.home");
        home = home.replace("\\", "/");
        if (!home.startsWith("/")) {
            home = "/" + home;
        }
        String path = (String) getOrCreateAppPropertyValue("System.FileSystem", null,
                home + "/filesystem/"

        );
        nativeFileSystemPath = path;
        fileSystem = new VrFS().subfs(path, "vrfs");
        fileSystem.get("/").mkdirs();
        new File(path + PATH_LOG).mkdirs();
        try {
            //check if already bound
//            for (Handler handler : LOG_APPLICATION_STATS.getHandlers()) {
//                if(handler instanceof FileHandler){
//                    FileHandler f=(FileHandler) handler;
//                }
//            }
            FileHandler handler = new FileHandler(path + PATH_LOG+"/application-stats.log", 5 * 1024 * 1024, 5, true);
            handler.setFormatter(new CustomTextFormatter());
            LOG_APPLICATION_STATS.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        validateRightsDefinitions();
    }

    //    @Start
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
        Object value = e.getBuilder().objectToRecord(o, true).getObject(field);
        T t = pu.createQueryBuilder(o.getClass()).byExpression(new Equals(new Var(field), new Literal(value, e.getField(field).getDataType())))
                .getEntity();
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
        return new InSetEvaluator(profiles);
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
//        AppUser u = null;
//        if (userId != null) {
//            Map<Integer, AppUser> usersById = (Map<Integer, AppUser>) cache.get("usersById");
//            if (usersById == null) {
//                usersById = new HashMap<>();
//                cache.put("usersById", usersById);
//            }
//            u = usersById.get(userId);
//            if (u == null) {
//                u = findUser(userId);
//                if (u != null) {
//                    usersById.put(userId, u);
//                }
//            }
//        }
//        if (!StringUtils.isEmpty(login)) {
//            Map<String, AppUser> usersByLogin = (Map<String, AppUser>) cache.get("usersByLogin");
//            if (usersByLogin == null) {
//                usersByLogin = new HashMap<>();
//                cache.put("usersByLogin", usersByLogin);
//            }
//
//            AppUser u1 = null;
//            u1 = usersByLogin.get(login);
//            if (u1 == null) {
//                u1 = findUser(login);
//                if (u1 != null) {
//                    usersByLogin.put(login, u1);
//                }
//            }
//            if (u != null && u1 != null && u.getId() != u1.getId()) {
//                u = null;
//            } else if (u == null) {
//                u = u1;
//            }
//        }

//        Map<Integer, Set<String>> usersProfilesByUserId = (Map<Integer, Set<String>>) cache.get("usersProfilesByUserId");
//        if (usersProfilesByUserId == null) {
//            usersProfilesByUserId = ;
//            cache.put("usersProfilesByUserId", usersProfilesByUserId);
//        }
        Set<String> foundProfileNames = (userId == null) ? (new HashSet<String>()) : (Set<String>) findUniformProfileNamesMapByUserId(true).get(userId);
        if (foundProfileNames == null) {
            foundProfileNames = new HashSet<>();
        }
        InSetEvaluator evaluator = createProfilesEvaluator(foundProfileNames);
        boolean b = evaluator.evaluate(profileExpr.getProfileListExpression());
        if (b && !StringUtils.isEmpty(profileExpr.getFilterExpression())) {
            return filterUsersByExpression(
                    userId == null ? new int[0] : new int[]{userId}
                    , profileExpr.getFilterExpression()).size() > 0;
        }
        return b;
    }

    //    public List<AppProfile> resolveProfilesByProfileFilter(String profile) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        if (profile != null && profile.trim().length() > 0) {
//            StringBuilder x = new StringBuilder();
//            for (String p : profile.split(" , |;")) {
//                if (p != null) {
//                    x.append("/").append(p);
//                }
//            }
//            x.append("/");
//            return pu.createQuery("Select u from AppProfile u where :expr like concat('%/',u.name,'/%')")
//                    .setParameter("expr", x.toString())
//                    .getEntityList();
//        }
//        return Collections.EMPTY_LIST;
//    }
    public List<AppUser> findUsersByProfileFilter(String profilePattern, Integer userType) {
        //check if pattern contains where clause!
        ProfileFilterExpression ee = new ProfileFilterExpression(profilePattern);
        ProfileFilterExpression profilesOnlyExpr = new ProfileFilterExpression(ee.getProfileListExpression(), null);

        List<AppUser> all = new ArrayList<>();
        final HashMap<String, Object> cache = new HashMap<String, Object>();
        List<AppUser> users = findEnabledUsers();
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
                .getEntityList();
    }

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
                .getEntityList();
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
//                    .getEntityList();
//        }
//        return Collections.EMPTY_LIST;
//    }

    public void setEnabledAppProperty(String propertyName, String userLogin, boolean enabled) {
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
        ap.setPropertyType(propertyType);
        ap.setPropertyValue(propertyValueString);
        ap.setEnabled(enabled);
        setAppProperty(ap);
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

    public long updateMaxAppDataStoreLong(String propertyName,long value,boolean doLog) {
        long oldValue = (Long) getAppDataStoreValue(propertyName, Long.class, 0L);
        if (value > oldValue) {
            setAppDataStoreValue(propertyName, value);
            if(doLog) {
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
//        List<AppProperty> props = q.getEntityList();
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
        AppProperty old = pu.createQueryBuilder(AppProperty.class)
                .byField("propertyName", ap.getPropertyName())
                .getEntity();
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
                .getEntity();
    }

    public AppContact findOrCreateContact(AppContact c) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (!StringUtils.isEmpty(c.getNin())) {
            AppContact oldAcademicTeacher = pu.createQueryBuilder(AppContact.class)
                    .byField("nin", c.getNin())
                    .getEntity();
            if (oldAcademicTeacher != null) {
                return oldAcademicTeacher;
            }
        } else {
            AppContact oldAcademicTeacher = pu.createQueryBuilder(AppContact.class)
                    .byField("firstName", c.getFirstName())
                    .byField("lastName", c.getLastName())
                    .getEntity();
            if (oldAcademicTeacher != null) {
                return oldAcademicTeacher;
            }
        }
        pu.persist(c);
        return c;
    }

    public String getActualLogin() {
        UserPrincipal up = UPA.getPersistenceUnit().getUserPrincipal();
        if (up != null && up.getObject() instanceof AppUser) {
            AppUser u = (AppUser) up.getObject();
            return u.getLogin();
        }
        UserSession us = VrApp.getBean(UserSession.class);
        if (us != null) {
            if (us.getUser() != null) {
                return us.getUser().getLogin();
            }
        }
        return null;
    }

    public boolean isActualAdminOrUser(String login) {
        UserSession us = VrApp.getBean(UserSession.class);
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
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            List<AppProfile> profiles = core.findProfilesByUser(u.getId());
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

    public boolean isActualAdmin() {
        UserSession us = null;
        try {
            us = VrApp.getBean(UserSession.class);
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
                CorePlugin core = VrApp.getBean(CorePlugin.class);
                List<AppProfile> profiles = core.findProfilesByUser(u.getId());
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
        UserSession us = VrApp.getBean(UserSession.class);
        return us != null && us.isAdmin();
    }

    public String validateName(String text) {
        //make it kamel based
        boolean wasWhite = true;
        char[] chars = (text == null ? "" : text).toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            if (Character.isWhitespace(aChar)) {
                if (!(aChar == ' ' || aChar == '\t')) {
                    aChar = ' ';
                }
                if (!wasWhite) {
                    sb.append(aChar);
                }
                wasWhite = true;
            } else if (wasWhite) {
                sb.append(Character.toUpperCase(aChar));
                wasWhite = false;
            } else {
                sb.append(Character.toLowerCase(aChar));
                wasWhite = false;
            }
        }
        return sb.toString().trim();
    }

    public String resolvePasswordProposal(AppContact contact) {
        String fn = contact.getFirstName();
        String ln = contact.getLastName();
        if (fn == null) {
            fn = "";
        }
        if (ln == null) {
            ln = "";
        }
        String fnlower = fn.toLowerCase();
        String[] fns = fnlower.split(" ");
        if (fns.length > 0 && fns[0].length() >= 3) {
            String p = fns[0];
            for (int i = 0; i < 4; i++) {
                int x = (int) (Math.random() * 10);
                p += x;
            }
            return p;
        }
//        if()
        String p = fnlower.replace(" ", "");
        for (int i = 0; i < 4; i++) {
            int x = (int) (Math.random() * 10);
            p += x;
        }
        return p;
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
        return fn.toLowerCase().replace(" ", "") + "." + ln.toLowerCase().replace(" ", "");
    }

    public void runThread(Runnable r) {
        new SpringThread(r).start();
    }

    public AppConfig findAppConfig() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppConfig c = pu.findById(AppConfig.class, 1);
        //should exist;
        return c;
    }

    public AppUser createUser(AppContact contact, int userTypeId, int departmentId, boolean attachToExistingUser, String[] defaultProfiles) {
        AppUser u = findUserByContact(contact.getId());
        if (u == null) {
            String login = resolveLoginProposal(contact);
            if (StringUtils.isEmpty(login)) {
                login = "user";
            }
            String password = resolvePasswordProposal(contact);
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
        if (isActualAdmin()) {
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

    public AppPeriod findPeriod(int id) {
        return (AppPeriod) cacheService.get(AppPeriod.class).getValues().get(id);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return (AppPeriod) pu.createQuery("Select u from AppPeriod u where u.id=:id")
//                .setParameter("id", id).getEntity();
    }

    public List<AppPeriod> findValidPeriods() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AppPeriod u where (u.snapshotName=null or u.snapshotName='') order by u.name")
                .getEntityList();
    }

    public AppPeriod findPeriod(String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppPeriod) pu.createQuery("Select u from AppPeriod u where (u.snapshotName=null or u.snapshotName='') and u.name=:name")
                .setParameter("name", name).getEntity();
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
                        "corePlugin", null, null, login, id, Level.INFO, s.getClientIpAddress()
                );
                s.setUser(s.getRootUser());
                s.setRootUser(null);
                buildSession(s, user);
            } else {
                trace.trace("logout", "successful logout " + login,
                        login,
                        "corePlugin", null, null, login, id, Level.INFO, s.getClientIpAddress()
                );
                VrApp.getBean(ActiveSessionsTracker.class).onDestroy(s);
            }
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
                trace.trace("impersonate", "successfull impersonate of " + login, login, "corePlugin", null, null, s.getUser().getLogin(),
                        s.getUser().getId(), Level.INFO, s.getClientIpAddress()
                );
            } else {
                user = findUser(login);
                if (user != null) {
                    if (!user.isEnabled()) {
                        trace.trace("impersonate", "successfull impersonate of " + login + ". but user is not enabled!", login, "corePlugin", null, null, s.getUser().getLogin(), s.getUser().getId(), Level.WARNING, s.getClientIpAddress()
                        );
                    } else {
                        trace.trace("impersonate", "successfull impersonate of " + login + ". but password " + password + " seems not to be correct", login, "corePlugin", null, null, s.getUser().getLogin(), s.getUser().getId(), Level.WARNING, s.getClientIpAddress());
                    }
                } else {
                    trace.trace(
                            "impersonate", "failed impersonate of " + login, login, "corePlugin", null, null, s.getUser().getLogin(), s.getUser().getId(), Level.SEVERE, s.getClientIpAddress()
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
                    "impersonate", "failed impersonate of " + login + ". not admin or already impersonating", login, "corePlugin", null, null, s.getUser().getLogin(), s.getUser().getId(), Level.WARNING, s.getClientIpAddress()
            );
        }
        return null;
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
            final ActiveSessionsTracker activeSessionsTracker = VrApp.getBean(ActiveSessionsTracker.class);
            activeSessionsTracker.onCreate(s);
            //update stats
            UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    updateMaxAppDataStoreLong("usersCountPeak", activeSessionsTracker.getActiveSessionsCount(), true);
                }
            });
            trace.trace("login", "successful", login, "corePlugin", null, null, login, user.getId(), Level.INFO, s.getClientIpAddress());
            getUserSession().setConnexionTime(user.getLastConnexionDate());
            getUserSession().setUser(user);
            buildSession(s, user);
            onPoll();
        } else {
            UserSession s = getUserSession();
            s.reset();
            AppUser user2 = findUser(login);
            if (user2 == null) {
                trace.trace("login", "login not found. Failed as " + login + "/" + password, login + "/" + password, "corePlugin", null, null, (login == null || login.length() == 0) ? "anonymous" : login, -1, Level.SEVERE, s.getClientIpAddress());
            } else if (user2.isDeleted() || !user2.isEnabled()) {
                trace.trace("login", "invalid state. Failed as " + login + "/" + password, login + "/" + password
                                + ". deleted=" + user2.isDeleted()
                                + ". enabled=" + user2.isEnabled(), "corePlugin", null, null, (login == null || login.length() == 0) ? "anonymous" : login, user2.getId(), Level.SEVERE, s.getClientIpAddress()
                );
            } else {
                trace.trace(
                        "login", "invalid password. Failed as " + login + "/" + password, login + "/" + password,
                        "corePlugin", null, null, (login == null || login.length() == 0) ? "anonymous" : login, user2.getId(),
                        Level.SEVERE, s.getClientIpAddress()
                );
            }
        }
        return user;
    }

    protected void buildSession(UserSession s, AppUser user) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
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
        s.setRights(core.findUserRights(user.getId()));
        if (user.getLogin().equalsIgnoreCase("admin") || userProfilesNames.contains(CorePlugin.PROFILE_ADMIN)) {
            s.setAdmin(true);
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
                .getEntity();
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

    public Plugin[] getPlugins() {
        if (plugins == null) {
            String[] pp = VrApp.getContext().getBeanNamesForAnnotation(AppPlugin.class);
            Plugin[] h = new Plugin[pp.length];
            for (int i = 0; i < h.length; i++) {
                Plugin h1 = new Plugin(pp[i], VrApp.getContext().getBean(pp[i]));
                h[i] = h1;
            }
            Arrays.sort(h);
            plugins = h;
        }
        return plugins;
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
                _appVersion.setShortName(p.getProperty("shortName"));
                _appVersion.setLongName(p.getProperty("longName"));
                _appVersion.setVersion(p.getProperty("version"));
                _appVersion.setBuildNumber(p.getProperty("buildNumber"));
                _appVersion.setBuildDate(p.getProperty("buildDate"));
                _appVersion.setAuthor(p.getProperty("author"));

            }
            appVersion = _appVersion;
        }
        return appVersion;
    }

    public String getNativeFileSystemPath() {
        return nativeFileSystemPath;
    }

    public VirtualFileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(VirtualFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public VFile getUserDocumentsFolder(final String login) {
        return getUserFileSystem(login).get("/" + FOLDER_MY_DOCUMENTS);
    }

    public VFile getUserFolder(final String login) {
        AppUser u = findUser(login);
        if (u != null) {
            AppUserType t = u.getType();
            String typeName = t == null ? "NoType" : t.getName();
            final String path = "/Documents/ByUser/" + typeName + "/" + login;
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
                    VirtualFileACL v = getFileSystem().getACL(path);
                    if (!v.isReadOnly()) {
                        v.chown(login);
                    }
                    return null;
                }

            }, null);
            return fileSystem.get(path);
        }
        return null;
    }

    public VFile getProfileFolder(final String profile) {
        AppProfile u = findProfileByName(profile);
        if (u != null) {
            final String path = "/Documents/ByProfile/" + profile;

            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
                    VirtualFileACL v = getFileSystem().getACL(path);
                    if (!v.isReadOnly()) {
                        v.grantListDirectory(profile);
                    }
                    return null;
                }

            }, null);

            return fileSystem.get(path);
        }
        return null;
    }

    public VFile getUserTypeFolder(String userType) {
        AppProfile u = findProfileByName(userType);
        if (u != null) {
            final String path = "/Documents/ByUserType/" + userType;
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
//                    VirtualFileACL v = getFileSystem().getACL(path);
//                    v.chown(login);
                    return null;
                }

            }, null);
            return fileSystem.get(path);
        }
        return null;
    }

    public VirtualFileSystem getUserFileSystem(final String login) {
        AppUser u = findUser(login);
        if (u != null) {
            VFile home = getUserFolder(login);
            final VirtualFileSystem me = fileSystem.subfs(home.getPath());
            MountableFS mfs = VFS.createMountableFS("user:" + login);
            try {
                mfs.mount("/" + FOLDER_MY_DOCUMENTS, me);
                List<AppProfile> profiles = findProfilesByUser(u.getId());
                for (AppProfile p : profiles) {
                    if (CorePlugin.PROFILE_ADMIN.equals(p.getName())) {
                        //this is admin
                        mfs.mount("/" + FOLDER_ALL_DOCUMENTS, fileSystem);
                    }
                }
                VrFSTable t = new VrFSTable();
                try {
                    if (fileSystem.exists("/Config/fstab")) {
                        t.load(fileSystem.getInputStream("/Config/fstab"));
                    }
                } catch (Exception exx) {
                    //ignore it
                }
                for (AppProfile p : profiles) {
                    String profileMountPoint = "/" + p.getName() + " Documents";
                    mfs.mount(profileMountPoint, getProfileFileSystem(p.getName(), t));
                }
                for (VrFSEntry e : t.getEntries(login, "User")) {
                    mfs.mount("/" + e.getMountPoint(), fileSystem.subfs(e.getLinkPath()));
                }
                for (VrFSEntry e : t.getEntriesByType("Profile")) {
                    if (isComplexProfileExpr(e.getFilterName())) {
                        if (userMatchesProfileFilter(u.getId(), e.getFilterName())) {
                            mfs.mount("/" + e.getMountPoint(), fileSystem.subfs(e.getLinkPath()));
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(CorePlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
            return mfs;
        } else {
            return VFS.createEmptyFS();
        }
    }

    public VirtualFileSystem getProfileFileSystem(String profileName) {
        return getProfileFileSystem(profileName, null);
    }

    public VirtualFileSystem getProfileFileSystem(String profileName, VrFSTable t) {
        AppProfile u = findProfileByName(profileName);
        if (u != null) {
            final String path = "/Documents/ByProfile/" + profileName;
            UPA.getContext().invokePrivileged(new Action<Object>() {

                @Override
                public Object run() {
                    getFileSystem().mkdirs(path);
//                    VrACL v = (VrACL) getFileSystem().getACL(path);
//                    v.chown(login);
                    return null;
                }

            }, null);
            VirtualFileSystem pfs = fileSystem.subfs(path);
            MountableFS mfs = null;
            try {
                if (t == null) {
                    t = new VrFSTable();
                    try {
                        if (fileSystem.exists("/Config/fstab")) {
                            t.load(fileSystem.getInputStream("/Config/fstab"));
                        }
                    } catch (Exception exx) {
                        //ignore it
                    }
                }
                mfs = VFS.createMountableFS("profile:" + profileName);
                mfs.mount("/", pfs);
                for (VrFSEntry e : t.getEntries(profileName, "Profile")) {
                    mfs.mount("/" + e.getMountPoint(), fileSystem.subfs(e.getLinkPath()));
                }
            } catch (IOException ex) {
                Logger.getLogger(CorePlugin.class.getName()).log(Level.SEVERE, null, ex);
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

    public long getDownloadsCount(final VFile file) {
        return UPA.getContext().invokePrivileged(new Action<Long>() {

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
        UPA.getContext().invokePrivileged(new Action<Object>() {

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
//                    v.chown(login);
                return null;
            }

        });
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
