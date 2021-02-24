package net.thevpc.app.vainruling.core.service;

import net.thevpc.app.vainruling.core.service.cache.EntityCache;
import net.thevpc.app.vainruling.core.service.model.*;
import net.thevpc.app.vainruling.core.service.util.*;
import net.thevpc.app.vainruling.core.service.model.*;
import net.thevpc.app.vainruling.core.service.util.*;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.collections.KeyValueList;
import net.thevpc.upa.*;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import net.thevpc.upa.types.I18NString;

class CorePluginBodySecurityManager extends CorePluginBody {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CorePluginBodySecurityManager.class.getName());

    @Override
    public void onInstall() {
        validateRightsDefinitions();
    }

    @Override
    public void onStart() {
        validateRightsDefinitions();
    }

    protected void validateRightsDefinitions() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        CorePlugin core = getContext().getCorePlugin();
        ProfileRightBuilder prb = new ProfileRightBuilder();
        for (Entity entity : pu.getEntities()) {
            if (!entity.isSystem()) {
                for (String rname : CorePluginSecurity.getEntityRights(
                        entity,
                        true,
                        !CorePlugin.ADMIN_ENTITIES.contains(entity.getName()),
                        !CorePlugin.ADMIN_ENTITIES.contains(entity.getName()),
                        true,
                        true
                )) {
                    prb.addName(rname);
                }
            }
        }
        prb.addName(CorePluginSecurity.RIGHT_CUSTOM_ADMIN_PASSWD);
        prb.execute();
    }

    public AppUser findUser(String login) {
        if (login == null) {
            return null;
        }
        final EntityCache entityCache = getContext().getCacheService().get(AppUser.class);
        Map<String, AppUser> m = entityCache.getProperty("findUserByLogin", new Action<Map<String, AppUser>>() {
            @Override
            public Map<String, AppUser> run() {
                Map<String, AppUser> m = new HashMap<String, AppUser>();
                KeyValueList<Integer, AppUser> values = entityCache.getValues();
                for (AppUser u : values) {
                    String key = u.getLogin();
                    if (!StringUtils.isBlank(key)) {
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

    public AppUser findUser(Integer id) {
        if (id == null) {
            return null;
        }
        CorePluginSecurity.requireUser(id);
        return getContext().getCacheService().getList(AppUser.class).getByKey(id);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return (AppUser) pu.findById(AppUser.class, id);
    }

    public AppUser findUser0(int id) {
        return getContext().getCacheService().getList(AppUser.class).getByKey(id);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return (AppUser) pu.findById(AppUser.class, id);
    }

    public AppUserType findUserType(String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppUserType) pu.findByField(AppUserType.class, "code", name);
    }

    public List<AppUserType> findUserTypes() {
        return getContext().getCacheService().getList(AppUserType.class);
    }

    public AppUserType findUserType(int id) {
        return getContext().getCacheService().getList(AppUserType.class).getByKey(id);
    }

    public AppProfile findProfile(int profileId) {
        return getContext().getCacheService().getList(AppProfile.class).getByKey(profileId);
    }

    public AppRightName createRight(String rightName, String desc) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppRightName r = pu.findById(AppRightName.class, rightName);
        if (r != null) {
            return r;
        }
        r = new AppRightName();
        r.setName(rightName);
        r.setDescription(desc);
        pu.persist(r);
        return r;
    }

    public void addProfileParent(String child, String... parents) {
        AppProfile p = findProfileByCode(child);
        if (p != null) {
            AppProfile[] a = parseProfileParents(p.getInherited());
            if (a != null) {
                Set<String> s = Arrays.stream(a).map(x -> x.getCode()).collect(Collectors.toSet());
                for (String parent : parents) {
                    if (!StringUtils.isBlank(parent)) {
                        AppProfile p2 = findProfileByCode(parent);
                        if (p2 != null) {
                            s.add(p2.getCode());
                        }
                    }
                }
                p.setInherited(String.join(",", new TreeSet<String>(s)));
                UPA.getPersistenceUnit().merge(p);
            }
        }
    }

    public void removeProfileParent(String child, String... parents) {
        AppProfile p = findProfileByCode(child);
        if (p != null) {
            AppProfile[] a = getProfileParents(child);
            if (a != null) {
                Set<String> s = Arrays.stream(a).map(x -> x.getCode()).collect(Collectors.toSet());
                for (String parent : parents) {
                    s.remove(parent);
                }
                p.setInherited(String.join(",", new TreeSet<String>(s)));
                UPA.getPersistenceUnit().merge(p);
            }
        }
    }

    public List<AppRightName> findProfileRightNames() {
        return UPA.getPersistenceUnit().findAll(AppRightName.class);
    }

    public List<AppRightName> findProfileRights(int profileId) {
        Map<String, AppRightName> existing = new HashMap<>();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<AppRightName> oldRigths = pu.createQuery("Select u.`right` from AppProfileRight u where u.profileId=:profileId")
                .setParameter("profileId", profileId)
                .getResultList();
        for (AppRightName r : oldRigths) {
            if (r != null) {
                existing.put(r.getName(), r);
            }
        }
        return oldRigths;
    }

    public List<AppRightName>[] findProfileRightNamesDualList(int profileId) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Map<String, AppRightName> allMap = new HashMap<>();
        Map<String, AppRightName> existing = new HashMap<>();
        List<AppRightName> oldRigths = pu.createQuery("Select u.`right` from AppProfileRight u where u.profileId=:profileId")
                .setParameter("profileId", profileId)
                .getResultList();
        List<AppRightName> allRigths = pu.createQuery("Select u from AppRightName u")
                .getResultList();
        for (AppRightName r : allRigths) {
            if (r != null) {
                allMap.put(r.getName(), r);
            }
        }
        for (AppRightName r : oldRigths) {
            if (r != null) {
                existing.put(r.getName(), r);
                allMap.remove(r.getName());
            }
        }
        List<AppRightName> in = new ArrayList<>(existing.values());
        List<AppRightName> out = new ArrayList<>(allMap.values());
        return new List[]{in, out};
    }

    public List<AppUser>[] findProfileUsersDualList(int profileId) {
        checkProfileAdmin(profileId);
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
        CorePluginSecurity.requireAdmin();
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
            if (r.getRight() != null) {
                if (baseSet.contains(r.getRight().getName())) {
                    //ok
                } else {
                    pu.remove(r);
                    modifications++;
                }
                visitedSet.add(r.getRight().getName());
            } else {
                pu.remove(r);
            }
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

    public void checkProfileAdmin(int profileId) {
        final AppProfile profile = findProfile(profileId);
        if (profile == null) {
            return;
        }
        if (StringUtils.isBlank(profile.getAdmin())) {
            CorePluginSecurity.requireAdmin();
        }
        if (!isCurrentSessionMatchesProfileFilter(profile.getAdmin())) {
            CorePluginSecurity.requireAdmin();
        }
    }

    public int setProfileUsers(int profileId, List<String> logins) {
        checkProfileAdmin(profileId);
        //no need for further security checks!
        return UPA.getPersistenceUnit().invokePrivileged(new Action<Integer>() {
            @Override
            public Integer run() {
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
        });
    }

    public boolean addProfileRight(String profileCode, String rightName) {
        AppProfile profileByCode = findProfileByCode(profileCode);
        if (profileByCode == null) {
            throw new NoSuchElementException("Profile " + profileCode);
        }
        return addProfileRight(profileByCode.getId(), rightName);
    }

    public boolean addProfileRight(int profileId, String rightName) {
        CorePluginSecurity.requireAdmin();
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

    public boolean addProfileRights(int profileId, String... rightName) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppProfile p = pu.findById(AppProfile.class, profileId);
        if (p == null) {
            throw new IllegalArgumentException("Profile not found " + profileId);
        }
        Map<String, AppRightName> rights = new HashMap<>(findProfileRightNames().stream().collect(Collectors.toMap(AppRightName::getName, java.util.function.Function.identity())));
        for (String s : rightName) {
            if (!rights.containsKey(s)) {
                log.log(Level.SEVERE, "Right " + rightName + " not found");
            }
        }
        boolean added = false;
        List<AppRightName> profileRightsList = findProfileRights(profileId);
        Map<String, AppRightName> profileRights = new HashMap<>(profileRightsList.stream().collect(Collectors.toMap(AppRightName::getName, java.util.function.Function.identity(),
                (v1, v2)
                -> {
            return v2;
        }
        )));
        for (String rn : rightName) {
            if (!profileRights.containsKey(rn)) {
                AppRightName rn2 = rights.get(rn);
                if (rn2 == null) {
                    log.log(Level.SEVERE, "Right " + rn + " not found");
//                    rn2 = createRight(rn, rn);
//                    rights.put(rn, rn2);
                }
                if (rn2 != null) {
                    AppProfileRight pr = new AppProfileRight();
                    pr.setProfile(p);
                    pr.setRight(rn2);
                    pu.persist(pr);
                    added = true;
                    profileRights.put(rn, rn2);
                }
            }
        }
        return added;
    }

    public boolean removeUserProfile(int userId, int profileId) {
        CorePluginSecurity.requireAdmin();
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

    public boolean addUserProfile(int userId, String profileCode) {
        CorePluginSecurity.requireAdmin();
        PersistenceUnit pu = UPA.getPersistenceUnit();

        if (pu.createQuery("Select u.profile from AppUserProfileBinding  u where u.userId=:userId and u.profile.code=:code")
                .setParameter("userId", userId)
                .setParameter("code", profileCode)
                .isEmpty()) {
            //should not call findUser because cache is not yet invalidated!
            AppUser u = pu.findById(AppUser.class, userId);
            if (u == null) {
                throw new IllegalArgumentException("Unknown User " + userId);
            }
            AppProfile p = findProfileByCode(profileCode);
//            if (p == null) {
//                p = findProfileByName(profileCode);
//            }
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

    public boolean addUserProfile(int userId, int profileId) {
        CorePluginSecurity.requireAdmin();
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

    public boolean isUserWithProfile(int userId, String profileName) {
        CorePluginSecurity.requireUser(userId);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return !pu.createQuery("Select u.profile from AppUserProfileBinding  u where u.userId=:userId and u.profile.name=:name")
                .setParameter("userId", userId)
                .setParameter("name", profileName)
                .isEmpty();
    }

    public Set<String> findUserRightsAll(int userId) {
        CorePluginSecurity.requireUser(userId);
        List<AppProfile> a = findProfilesByUser(userId);
        if (a.isEmpty()) {
            return Collections.emptySet();
        }

        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<String> rights = pu.createQuery("Select distinct(n.rightName) from AppProfileRight n where n.profileId in ( "
                + VrUtils.strcatsep(",", a.stream().map(AppProfile::getId).toArray())
                + " )")
                .getValueList(0);
        return new HashSet<>(rights);
    }

    public Set<String> findUserRightsImmediate(int userId) {
        CorePluginSecurity.requireUser(userId);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<String> rights = pu.createQuery("Select n.rightName from AppUserProfileBinding  u "
                + " inner join AppProfileRight n on n.profileId=u.profileId "
                + " where u.userId=:userId")
                .setParameter("userId", userId)
                .getValueList(0);
        return new HashSet<>(rights);
    }

    public AppProfile findOrCreateCustomProfile(String profileCode, String customType) {
        profileCode = validateProfileName(profileCode);//must validate name first!
        AppProfile p = findProfileByCode(profileCode);
//        if (p == null) {
//            p = findProfileByCode(profileCode);
//        }
        boolean merge = false;
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (p == null) {
            p = new AppProfile();
            p.setCode(profileCode);
            p.setCustomType(customType);
            p.setName(I18n.get().get(new I18NString("Profile", p.getCustomType(), p.getCode()),
                    new Arg("code", p.getCode()),
                    new Arg("name", p.getName())
            ));
            p.setCustom(true);
            pu.persist(p);
        } else if (!p.isCustom()) {
            //force to custom
            p.setCustom(true);
            p.setCustomType(customType);
            merge = true;
        }
        if (StringUtils.isBlank(p.getCode()) || !profileCode.equals(p.getCode())) {
            p.setCode(profileCode);
            merge = true;
        }
        if (StringUtils.isBlank(p.getName())) {
            p.setName(p.getCode());
            merge = true;
        }
        if (StringUtils.isBlank(p.getCustomType()) || (customType != null && !customType.equals(p.getCustomType()))) {
            p.setCustomType(customType);
            merge = true;
        }
        if (merge) {
            pu.merge(p);
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
    public AppProfile findOrCreateProfile(String profileCode) {
        AppProfile p = findProfileByCode(profileCode);
        if (p == null) {
            p = new AppProfile();
            p.setCode(profileCode);
            p.setName(profileCode);
            UPA.getPersistenceUnit().persist(p);
        }
        return p;
    }

    public AppProfile findProfileByName(String profileName) {
        final EntityCache entityCache = getContext().getCacheService().get(AppProfile.class);
        Map<String, AppProfile> m = entityCache.getProperty("findProfileByName", new Action<Map<String, AppProfile>>() {
            @Override
            public Map<String, AppProfile> run() {
                Map<String, AppProfile> m = new HashMap<String, AppProfile>();
                KeyValueList<Integer, AppProfile> values = entityCache.getValues();
                for (AppProfile profile : values) {
                    String key = profile.getName();
                    if (!StringUtils.isBlank(key)) {
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
    public List<AppProfile> findAdministrableProfiles() {
        List<AppProfile> profiles = new ArrayList<>();
        boolean admin = this.getContext().getCorePlugin().isCurrentSessionAdmin();
        if (admin) {
            return findProfiles();
        }
        for (String profileName : this.getContext().getCorePlugin().getCurrentToken().getProfileCodes()) {
            AppProfile p = findProfileByCode(profileName);
            String adminProfiles = p.getAdmin();
            if (!StringUtils.isBlank(adminProfiles)) {
                if (isCurrentSessionMatchesProfileFilter(adminProfiles)) {
                    profiles.add(p);
                }
            }
        }
        return profiles;
    }

    public AppProfile findProfileByCode(String profileCode) {
        final EntityCache entityCache = getContext().getCacheService().get(AppProfile.class);
        Map<String, AppProfile> m = entityCache.getProperty("findProfileByCode", new Action<Map<String, AppProfile>>() {
            @Override
            public Map<String, AppProfile> run() {
                Map<String, AppProfile> m = new HashMap<String, AppProfile>();
                KeyValueList<Integer, AppProfile> values = entityCache.getValues();
                for (AppProfile profile : values) {
                    String key = profile.getCode();
                    if (!StringUtils.isBlank(key)) {
                        m.put(VrUtils.normalizeName(key), profile);
                    }
                }
                return m;
            }
        });
        return m.get(VrUtils.normalizeName(profileCode));
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQueryBuilder(AppProfile.class).byField("code", profileCode)
//                .getEntity();

    }

    protected Set<String> findUniformProfileCodesMapByUserId(int userId, boolean includeLogin, boolean expandProfiles) {
        Map<Integer, Set<String>> uniformProfileCodesMapByUserId = findUniformProfileCodesMapByUserId(includeLogin, expandProfiles);
        Set<String> profiles = uniformProfileCodesMapByUserId.get(userId);
        if (profiles == null) {
            AppUser u = findUser0(userId);
            if (u == null) {
                return null;
            }
            profiles = new HashSet<>();
            uniformProfileCodesMapByUserId.put(userId, profiles);
            if (includeLogin) {
                profiles.add(VrUtils.normalize(u.getLogin()));
            }
            profiles.addAll(expandProfileCodes(profiles));
        } else {
            profiles = new HashSet(profiles);
            AppUser u = findUser0(userId);
            if (u == null) {
                return null;
            }
            if (includeLogin) {
                profiles.add(VrUtils.normalize(u.getLogin()));
            }
        }
        return profiles;
    }

    protected Map<Integer, Set<String>> findUniformProfileCodesMapByUserId(final boolean includeLogin, boolean expandProfiles) {
        String cacheKey = "findUniformProfileCodesMapByUserId:" + includeLogin + ":" + expandProfiles;
        final EntityCache entityCache = getContext().getCacheService().get(AppUserProfileBinding.class);
        return entityCache
                .getProperty(cacheKey, new Action<Map<Integer, Set<String>>>() {
                    @Override
                    public Map<Integer, Set<String>> run() {
                        HashMap<Integer, Set<String>> all = new HashMap<>();
                        KeyValueList<Integer, AppUserProfileBinding> values = entityCache.getValues();
                        for (AppUserProfileBinding o : values) {
                            if (o.getUser() != null && o.getProfile() != null) {
                                Set<String> list = all.get(o.getUser().getId());
                                if (list == null) {
                                    list = new HashSet<>();
                                    if (includeLogin) {
                                        list.add(VrUtils.normalize(o.getUser().getLogin()));
                                    }
                                    all.put(o.getUser().getId(), list);
                                }
                                if (StringUtils.isBlank(o.getProfile().getCode())) {
                                    log.log(Level.SEVERE, "Profile witch invalid code {0} {1}", new Object[]{o.getProfile().getId(), o.getProfile().getName()});
                                } else {
                                    list.add(VrUtils.normalize(o.getProfile().getCode()));
                                }
                            }
                        }
                        if (expandProfiles) {
                            for (Map.Entry<Integer, Set<String>> entry : all.entrySet()) {
                                entry.getValue().addAll(expandProfileCodes(entry.getValue()));
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
//                            list.add(o.getUser().getUserLogin().toLowerCase());
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

    public Set<String> expandProfileCodes(Collection<String> profiles) {
        Set<String> found = new HashSet<>();
        Stack<String> stack = new Stack<>();
        for (String profile : profiles) {
            if (!StringUtils.isBlank(profile)) {
                stack.push(VrUtils.normalize(profile));
            }
        }
        while (!stack.isEmpty()) {
            String profileCode = stack.pop();
            found.add(profileCode);
            AppProfile profile = findProfileByCode(profileCode);
            if (profile != null) {
                String inherited = profile.getInherited();
                if (!StringUtils.isBlank(inherited)) {
                    String[] st = inherited.split("[ ,;]");
                    for (String s : st) {
                        s = VrUtils.normalize(s);
                        if (s.length() > 0 && !found.contains(s)) {
                            stack.push(s);
                        }
                    }
                }
            }
        }
        return found;
    }

    public List<AppProfile> findProfilesByUser(int userId) {
        Set<String> codes = findUniformProfileCodesMapByUserId(userId, false, true);
        List<AppProfile> pro = new ArrayList<>();
        if (codes != null) {
            for (String code : codes) {
                AppProfile pp = findProfileByCode(code);
                if (pp != null) {
                    pro.add(pp);
                } else {
                    log.log(Level.SEVERE, "Profile not found {0}", code);
                    pp = findProfileByCode(code);
                }
            }
        }
        return pro;
//        final EntityCache entityCache = getContext().getCacheService().get(AppUserProfileBinding.class);
//        Map<Integer, List<AppProfile>> m = entityCache
//                .getProperty("findProfilesByUser", new Action<Map<Integer, List<AppProfile>>>() {
//                    @Override
//                    public Map<Integer, List<AppProfile>> run() {
//                        PersistenceUnit pu = UPA.getPersistenceUnit();
//                        KeyValueList<Integer, AppUserProfileBinding> values = entityCache.getValues();
//                        Map<Integer, List<AppProfile>> m = new HashMap<Integer, List<AppProfile>>();
//                        for (AppUserProfileBinding binding : values) {
//                            if (binding.getUser() != null && binding.getProfile() != null) {
//                                List<AppProfile> found = m.get(binding.getUser().getId());
//                                if (found == null) {
//                                    found = new ArrayList<AppProfile>();
//                                    m.put(binding.getUser().getId(), found);
//                                }
//                                found.add(binding.getProfile());
//                            }
//                        }
//                        return m;
//                    }
//                });
//        List<AppProfile> all = m.get(userId);
//        if (all == null) {
//            all = Collections.EMPTY_LIST;
//        }
//        return expandProfiles(all);
    }

    public List<AppUser> findUsersByTypeAndDepartment(int userType, int userDepartment) {
        if (userDepartment < 0) {
            return findUsersByType(userType);
        }
        final EntityCache entityCache = getContext().getCacheService().get(AppUser.class);
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
        final EntityCache entityCache = getContext().getCacheService().get(AppUser.class);
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

    public List<AppContact> findContactsByFullTitle(String fullTitle) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQueryBuilder(AppContact.class).byField("fullTitle", fullTitle).getResultList();
    }

    public List<AppUser> findUsersByFullTitle(String fullTitle) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQueryBuilder(AppContact.class).byField("fullTitle", fullTitle).getResultList();
    }

    public List<AppContact> findContactsByFullName(String fullName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQueryBuilder(AppContact.class).byField("fullName", fullName).getResultList();
    }

    public List<AppUser> findUsersByFullName(String fullName) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQueryBuilder(AppUser.class).byField("fullName", fullName).getResultList();
    }

    public List<AppUser> findUsersByProfile(int profileId) {
        final EntityCache entityCache = getContext().getCacheService().get(AppUserProfileBinding.class);
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
        final EntityCache entityCache = getContext().getCacheService().get(AppUser.class);
        return entityCache.getProperty("findUserLoginToIdMap", new Action<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> run() {
                Map<String, Integer> m = new HashMap<String, Integer>();
                KeyValueList<Integer, AppUser> values = entityCache.getValues();
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
        AppProfile p = findProfileByCode(profileName);
        if (p == null) {
            return new ArrayList<>();
        } else {
            List<AppUser> u = new ArrayList<>();
            for (Integer uid : getUserProfileMap().getUsers(p.getId())) {
                AppUser uu = findUser(uid);
                if (uu != null) {
                    u.add(uu);
                }
            }
            return u;
        }
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select u.user from AppUserProfileBinding  u where u.profile.name=:profileName")
//                .setParameter("profileName", profileName)
//                .getResultList();
    }

    public List<AppProfile> findProfiles() {
        return getContext().getCacheService().getList(AppProfile.class);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select u from AppProfile u")
//                .getResultList();
    }

    public List<AppUser> findUsers() {
        return getContext().getCacheService().getList(AppUser.class);
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select u from AppUser  u")
//                .getResultList();
    }

    public List<AppUser> findEnabledUsers(Integer typeId, Integer departmentId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        StringBuilder sb = new StringBuilder();
        sb.append("Select u from AppUser  u where u.enabled=true and u.deleted=false");
        if (typeId != null) {
            sb.append(" and u.typeId=:typeId");
        }
        if (departmentId != null) {
            sb.append(" and u.departmentId=:departmentId");
        }
        return pu.createQuery(sb.toString())
                .setParameter("typeId", typeId, typeId != null)
                .setParameter("departmentId", departmentId, departmentId != null)
                .getResultList();
    }

//    public List<AppUser> findEnabledUsers() {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery("Select u from AppUser  u where u.enabled=true and u.deleted=false")
//                .getResultList();
//    }
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

    private boolean isUserMatchesProfileFilter(Integer userId, String login, String profile, String whereClause, Map<String, Object> cache) {
        return isUserMatchesProfileFilter(userId, login, new ProfileFilterExpression(profile, whereClause), cache);
    }

    private boolean isUserMatchesProfileFilter(Integer userId, String login, ProfileFilterExpression profileExpr, Map<String, Object> cache) {
        if (StringUtils.isBlank(profileExpr.getFilterExpression()) && StringUtils.isBlank(profileExpr.getProfileListExpression())) {
            return true;
        }
        if (cache == null) {
            cache = new HashMap<>();
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (userId == null) {
            userId = findUserIdByLogin(login);
        }
        Set<String> foundProfileNames = (userId == null) ? (new HashSet<String>()) : findUniformProfileCodesMapByUserId(userId, true, true);
        if (foundProfileNames == null) {
            foundProfileNames = new HashSet<>();
        }
        boolean b = false;
        try {
            b = SimpleJavaEvaluator.INSTANCE.evaluateExpression(profileExpr.getProfileListExpression(), foundProfileNames);
        } catch (Exception e) {
            //error
        }
        if (b && !StringUtils.isBlank(profileExpr.getFilterExpression())) {
            return filterUsersByExpression(
                    userId == null ? new int[0] : new int[]{userId},
                    profileExpr.getFilterExpression()).size() > 0;
        }
        return b;
    }

    public boolean isUserMatchesProfileFilter(Integer userId, String login, String profile, String whereClause) {
        return isUserMatchesProfileFilter(userId, login, profile, whereClause, null);
    }

    public <T> List<T> filterByProfilePattern(List<T> in, Integer userId, String login, ProfilePatternFilter<T> filter) {
        List<T> out = new ArrayList<>();
        final HashMap<String, Object> cache = new HashMap<String, Object>();
        if (in != null) {
            for (T i : in) {
                String profilePattern = filter.getProfilePattern(i);
                boolean b = isUserMatchesProfileFilter(userId, login, new ProfileFilterExpression(profilePattern), cache);
                if (b) {
                    out.add(i);
                }
            }
        }
        return out;
    }

    public boolean isUserMatchesProfileFilter(String userLogin, String profileExpr) {
        return isUserMatchesProfileFilter(null, userLogin, profileExpr, null);
    }

    public List<String> autoCompleteUserOrProfile(String userOrProfile) {
        if (userOrProfile == null) {
            userOrProfile = "";
        }
        userOrProfile = VrUtils.normalize(userOrProfile);
        List<String> all = new ArrayList<>();
        for (AppProfile appProfile : findProfiles()) {
            if (appProfile.getCode() != null && VrUtils.normalize(appProfile.getCode()).contains(userOrProfile)) {
                all.add(appProfile.getCode().trim());
            }
        }
        for (AppUser appUser : findUsers()) {
            if (appUser.getLogin() != null && VrUtils.normalize(appUser.getLogin()).contains(userOrProfile)) {
                all.add(appUser.getLogin().trim());
            }
        }
        return all;
    }

    public List<String> autoCompleteUserLoginByPortion(String userPortion) {
        if (userPortion == null) {
            userPortion = "";
        }
        userPortion = VrUtils.normalize(userPortion);
        List<String> all = new ArrayList<>();
        for (AppUser appUser : findUsers()) {
            if (appUser.getLogin() != null && VrUtils.normalize(appUser.getLogin()).contains(userPortion)) {
                all.add(appUser.getLogin().trim());
            }
        }
        return all;
    }

    public List<String> autoCompleteUserLogin(String login) {
        if (login == null) {
            login = "";
        }
        login = login.trim();
        int x = login.length() - 1;
        while (x >= 0) {
            char c = login.charAt(x);
            if (!Character.isLetterOrDigit(c) && c != '.') {
                break;
            }
            x--;
        }
        if (x > 0) {
            if (x < login.length() - 1) {
                String prefix = login.substring(0, x + 1);
                String suffix = login.substring(x + 1);
                List<String> all = new ArrayList<>();
                List<String> strings = autoCompleteUserLoginByPortion(suffix);
                if (strings.isEmpty()) {
                    strings = Arrays.asList("");
                }
                for (String s : strings) {
                    all.add(prefix + s);
                }
                return all;
            } else {
                return Arrays.asList(login);
            }
        } else {
            return new ArrayList<String>(new TreeSet<String>(autoCompleteUserLoginByPortion(login)));
        }
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

    public boolean isCurrentSessionMatchesProfileFilter(String profilePattern) {
        final AppUser currentUser = getContext().getCorePlugin().getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        return filterUsersByProfileFilter(Arrays.asList(currentUser), profilePattern, null, null).size() > 0;
    }

    //    public boolean isUserMatchesProfileFilter(int userId, String profileExpr) {
//        return isUserMatchesProfileFilter(userId, null, profileExpr, null);
//    }
    public boolean isUserMatchesProfileFilter(int userId, String profilePattern) {
        final AppUser currentUser = UPA.getContext().invokePrivileged(new Action<AppUser>() {
            @Override
            public AppUser run() {
                return findUser(userId);
            }
        });
        if (currentUser == null) {
            return false;
        }
        return filterUsersByProfileFilter(Arrays.asList(currentUser), profilePattern, null, null).size() > 0;
    }

    public List<AppUser> filterUsersByProfileFilter(List<AppUser> users, String profilePattern, Integer userType, Integer department) {
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
        int departmentInt = department == null ? -1 : department.intValue();
        for (AppUser u : users) {
            if (userType == null || (u.getType() != null && u.getType().getId() == userTypeInt)) {
                if (userType == null || (u.getDepartment() != null && u.getDepartment().getId() == departmentInt)) {
                    if (isUserMatchesProfileFilter(u.getId(), u.getLogin(), profilesOnlyExpr, cache)) {
                        all.add(u);
                    }
                }
            }
        }
        return filterUsersByExpression(all, ee.getFilterExpression());
    }

    public List<AppUser> findUsersByProfileFilter(String profilePattern, Integer userTypeId, Integer departmentId) {
        return filterUsersByProfileFilter(findEnabledUsers(userTypeId, departmentId), profilePattern, null, null);
    }

    private List<AppUser> filterUsersByExpression(List<AppUser> all, String expression) {
        if (all.isEmpty()) {
            return all;
        }
        if (StringUtils.isBlank(expression)) {
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

    private List<AppUser> filterUsersByExpression(int[] all, String expression) {
        if (all.length == 0) {
            return Collections.emptyList();
        }
        if (StringUtils.isBlank(expression)) {
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

    public AppUser addUser(CreateUserInfo userInfo) {
        //AppUser newUser, int userTypeId, int departmentId, boolean attachToExistingUser, String[] defaultProfiles, VrPasswordStrategy passwordStrategy
        AppUser u = findUser(userInfo.getUserId());
        if (u == null) {
            AppUser u2 = new AppUser();
            if (userInfo.getUserPrototype() != null) {
                u2.copyFrom(userInfo.getUserPrototype());
            }
            if (!StringUtils.isBlank(userInfo.getFirstName())) {
                u2.setFirstName(userInfo.getFirstName());
            }
            if (!StringUtils.isBlank(userInfo.getLastName())) {
                u2.setLastName(userInfo.getLastName());
            }
            String login = resolveLoginProposal(u2);
            if (StringUtils.isBlank(login)) {
                login = "user";
            }
            u = findUser(login);
            if (u != null) {
                if (u.getId() == userInfo.getUserId()) {
                    //this is the same user !! ok
                    return u;
                }
            }
            if (!userInfo.isAttachToExistingUser() || (u != null)) {
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
            AppUserType userType = userInfo.getUserTypeId() <= 0 ? ((userInfo.getUserPrototype() == null || userInfo.getUserPrototype().getType() == null) ? null : userInfo.getUserPrototype().getType())
                    : findUserType(userInfo.getUserTypeId());
            AppDepartment userDepatment = getContext().getCorePlugin().findDepartment(userInfo.getDepartmentId());
            if (u == null) {
                u = new AppUser();
                if (userInfo.getUserPrototype() != null) {
                    u.copyFrom(userInfo.getUserPrototype());
                }
                if (!StringUtils.isBlank(userInfo.getFirstName())) {
                    u.setFirstName(userInfo.getFirstName());
                }
                if (!StringUtils.isBlank(userInfo.getLastName())) {
                    u.setLastName(userInfo.getLastName());
                }
                u.setLogin(login);
                // TODO FIX ME
                //u.setContact(contact);
                VrPasswordStrategy passwordStrategy = userInfo.getPasswordStrategy();
                if (passwordStrategy == null) {
                    passwordStrategy = VrPasswordStrategyRandom.INSTANCE;
                }
                String password = passwordStrategy.generatePassword(u2);
                String pwd = password;
                if (StringUtils.isBlank(u.getFullName())
                        && (!StringUtils.isBlank(u.getFirstName())
                        || !StringUtils.isBlank(u.getLastName()))) {
                    u.setFullName(StringUtils.trim(u.getFirstName() + " " + u.getLastName()));
                }
                u.setPassword(pwd);
                u.setPasswordAuto(pwd);
                u.setType(userType);
                u.setDepartment(userDepatment);
                u.setEnabled(true);
                UPA.getPersistenceUnit().persist(u);
            } else {
                // TODO FIX ME
//                u.setContact(contact);
                u.setType(userType);
                UPA.getPersistenceUnit().merge(u);
            }
        }
        if (userInfo.getDefaultProfiles() != null) {
            for (String defaultProfile : userInfo.getDefaultProfiles()) {
                if (!StringUtils.isBlank(defaultProfile)) {
                    addUserProfile(u.getId(), defaultProfile);
                }
            }
            AppUserType userType = u.getType();
            AppProfile typeProfile = null;
            if (userType != null && !StringUtils.isBlank(userType.getCode())) {
                typeProfile = findProfileByCode(userType.getCode());
                if (typeProfile != null) {
                    addUserProfile(u.getId(), typeProfile.getId());
                }
            }
        }
        getContext().getCacheService().get(AppUser.class).invalidate();
        return u;
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
        if (StringUtils.isBlank(login)) {
            login = getContext().getCorePlugin().getActualLogin();
        }
        if (getContext().getCorePlugin().isCurrentSessionAdmin()) {
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

    public String resolveLoginProposal(AppUser contact) {
        String fn = contact.getFirstName();
        String ln = contact.getLastName();
        if (fn == null) {
            fn = "";
        }
        if (ln == null) {
            ln = "";
        }
        return VrUtils.normalizeName(fn).replace(" ", "") + "." + VrUtils.normalizeName(ln).replace(" ", "");
    }

    public String resolveLoginProposal(String fn, String ln) {
        if (fn == null) {
            fn = "";
        }
        if (ln == null) {
            ln = "";
        }
        if (fn.isEmpty()) {
            fn = Integer.toHexString((int) (Math.random() * 1000)).toLowerCase();
        }
        if (ln.isEmpty()) {
            ln = Integer.toHexString((int) (Math.random() * 1000)).toLowerCase();
        }
        return VrUtils.normalizeName(fn).replace(" ", "") + "." + VrUtils.normalizeName(ln).replace(" ", "");
    }

    //    public AppUser findUserByContact(int contactId) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQueryBuilder(AppUser.class)
//                .byField("contactId", contactId)
//                .getFirstResultOrNull();
//    }
    public List<AppTrace> findTraceByUser(int userId, int maxRows) {
        CorePluginSecurity.requireAdmin();
        List<AppTrace> found = new ArrayList<>();
        for (AppTrace t : UPA.getPersistenceUnit()
                .createQuery("Select Top " + maxRows + " u from AppTrace u where u.userId=:userId order by u.time desc")
                .setParameter("userId", userId)
                .<AppTrace>getResultList()) {
            found.add(t);
            if (found.size() >= maxRows) {
                break;
            }
        }
        return found;
    }

    public AppUser findOrCreateUser(AppUser user, String[] defaultProfiles, VrPasswordStrategy passwordStrategy) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppUser old = findUser(user);
        if (old != null) {
            return old;
        }
        if (passwordStrategy == null) {
            passwordStrategy = VrPasswordStrategyRandom.INSTANCE;
        }
        String login = resolveLoginProposal(user);
        if (StringUtils.isBlank(login)) {
            login = "user";
        }
        String password = passwordStrategy.generatePassword(user);

        old = findUser(login);
        if (old != null) {
            //the is a user with same name!
            String y = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 2000);
            old = findUser(login + y);
            if (old == null) {
                //ok
                login = login + y;
            } else {
                String chars = "abcdefghijklmnopqrstuvwxyz";
                for (int i = 0; i < chars.length(); i++) {
                    old = findUser(login + y + chars.charAt(i));
                    if (old == null) {
                        login = login + y + chars.charAt(i);
                        break;
                    }
                }
            }
            if (old != null) {
                int index = 1;
                while (true) {
                    old = findUser(login + y + "_" + index);
                    if (old == null) {
                        login = login + y + "_" + index;
                        break;
                    }
                    index++;
                }
            }
            if (old != null) {
                throw new IllegalArgumentException("Unable to add new user");
            }
        }
        user.setLogin(login);
        user.setPassword(password);
        user.setPasswordAuto(password);
        user.setEnabled(true);
        pu.persist(user);

        if (defaultProfiles != null) {
            for (String defaultProfile : defaultProfiles) {
                if (!StringUtils.isBlank(defaultProfile)) {
                    addUserProfile(user.getId(), defaultProfile);
                }
            }
        }
        return user;
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
        if (!StringUtils.isBlank(nin)) {
            nin = nin.trim();
            AppContact oldContact = pu.createQueryBuilder(AppContact.class)
                    .byField("nin", nin)
                    .getFirstResultOrNull();
            if (oldContact != null) {
                return oldContact;
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
                    if (!StringUtils.isBlank(nin1)) {
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
            AppContact oldContact = pu.createQueryBuilder(AppContact.class)
                    .byField("firstName", c.getFirstName())
                    .byField("lastName", c.getLastName())
                    .getFirstResultOrNull();
            if (oldContact != null) {
                return oldContact;
            }
        }
        return null;
    }

    public AppUser findUser(AppUser c) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        if (!StringUtils.isBlank(c.getLogin())) {
            return findUser(c.getLogin());
        }
        String nin = c.getNin();
        if (!StringUtils.isBlank(nin)) {
            nin = nin.trim();
            AppUser oldUser = pu.createQueryBuilder(AppUser.class)
                    .byField("nin", nin)
                    .getFirstResultOrNull();
            if (oldUser != null) {
                return oldUser;
            }
            StringBuilder s = new StringBuilder(nin);
            while (s.length() > 0 && s.charAt(0) == '0') {
                s.delete(0, 1);
            }
            if (s.length() > 0) {
                List<AppUser> possibleContacts = pu.createQuery("Select u from AppUser u where u.nin like :nin")
                        .setParameter("nin", "%" + s + "%")
                        .getResultList();
                for (AppUser o : possibleContacts) {
                    String nin1 = o.getNin();
                    if (!StringUtils.isBlank(nin1)) {
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
            AppUser oldUser = pu.createQueryBuilder(AppContact.class)
                    .byField("firstName", c.getFirstName())
                    .byField("lastName", c.getLastName())
                    .getFirstResultOrNull();
            if (oldUser != null) {
                return oldUser;
            }
        }
        return null;
    }

    public void invalidateUserProfileMap() {
        CorePluginSecurity.requireAdmin();
        EntityCache profilesCache = getContext().getCacheService().get(AppProfile.class);
        profilesCache.invalidateProperty("UserProfileMap");
    }

    public UserProfileMap getUserProfileMap() {
        CorePluginSecurity.requireAdmin();
        EntityCache profilesCache = getContext().getCacheService().get(AppProfile.class);
        return profilesCache.getProperty("UserProfileMap", new Action<UserProfileMap>() {
            @Override
            public UserProfileMap run() {
                return createUserProfileMap();
            }
        });
    }

    public AppProfile[] getProfileParents(String profileCode) {
        AppProfile p = findProfileByCode(profileCode);
        if (p != null) {
            return parseProfileParents(p.getInherited());
        }
        return null;
    }

    public AppProfile[] parseProfileParents(String inheritedString) {
        LinkedHashMap<Integer, AppProfile> a = new LinkedHashMap<>();
        if (!StringUtils.isBlank(inheritedString)) {
            String[] st = inheritedString.split("[ ,;]");
            for (String s : st) {
                AppProfile p = findProfileByCode(s);
                if (p != null && !a.containsKey(p.getId())) {
                    a.put(p.getId(), p);
                }
            }
        }
        return a.values().toArray(new AppProfile[0]);
    }

    private UserProfileMap createUserProfileMap() {
        UserProfileMap m = new UserProfileMap();
        EntityCache profilesCache = getContext().getCacheService().get(AppProfile.class);
        for (AppProfile o : profilesCache.<Integer, AppProfile>getValues()) {
            String inherited = o.getInherited();
            if (!StringUtils.isBlank(inherited)) {
                String[] st = inherited.split("[ ,;]");
                for (String s : st) {
                    AppProfile p = findProfileByCode(s);
                    if (p != null) {
                        m.addProfileParent(o.getId(), p.getId());
                    }
                }
            }
        }
        for (AppUserProfileBinding b : UPA.getPersistenceUnit().<AppUserProfileBinding>findAll(AppUserProfileBinding.class)) {
            if (b.getUser() != null && b.getProfile() != null) {
                m.add(b.getUser().getId(), b.getProfile().getId());
            }
        }
        return m;
    }

//    public List<AppUser> findContacts(String name, String type) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery(
//                "Select u from AppUser u where 1=1 "
//                + (StringUtils.isBlank(name) ? "" : " u.fullName like :name ")
//                + (StringUtils.isBlank(type) ? "" : " and u.type.name =:type ")
//        )
//                .setParameter("name", "%" + name + "%", !StringUtils.isBlank(name))
//                .setParameter("name", name, !StringUtils.isBlank(name))
//                .getResultList();
//    }
}
