package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.cache.EntityCache;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.common.strings.StringBuilder2;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.MapList;
import net.vpc.upa.*;

import java.util.*;
import java.util.logging.Level;
import net.vpc.common.util.Chronometer;
import net.vpc.upa.types.I18NString;

class CorePluginBodySecurityManager extends CorePluginBody {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CorePluginBodySecurityManager.class.getName());

    @Override
    public void onStart() {
        validateRightsDefinitions();
    }

    protected void validateRightsDefinitions() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        CorePlugin core = getContext().getCorePlugin();
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

                    AppRightName r = new AppRightName();
                    r.setName(rname);
                    r.setDescription(rname);
                    core.findOrCreate(r);
                }
            }
        }
        createRight(CorePluginSecurity.RIGHT_CUSTOM_ADMIN_PASSWD, CorePluginSecurity.RIGHT_CUSTOM_ADMIN_PASSWD);
    }

    public AppUser findUser(String login) {
        final EntityCache entityCache = getContext().getCacheService().get(AppUser.class);
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
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.findAll(AppUserType.class);
    }

    public AppUserType findUserType(int id) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppUserType) pu.findById(AppUserType.class, id);
    }

    public AppProfile findProfile(int profileId) {
        return getContext().getCacheService().getList(AppProfile.class).getByKey(profileId);
    }

    public boolean createRight(String rightName, String desc) {
        CorePluginSecurity.requireAdmin();
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
        if (StringUtils.isEmpty(profile.getAdmin())) {
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

    public boolean userRemoveProfile(int userId, int profileId) {
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

    public boolean userAddProfile(int userId, String profileCode) {
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

    public boolean userAddProfile(int userId, int profileId) {
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

    public boolean userHasProfile(int userId, String profileName) {
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
        List<String> rights = pu.createQuery("Select distinct n.rightName from AppProfileRight n where n.profileId in ( "
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
        if (p == null) {
            p = new AppProfile();
            p.setCode(profileCode);
            p.setCustomType(customType);
            p.setName(I18n.get().get(new I18NString("Profile", p.getCustomType(), p.getCode()),
                    new Arg("code", p.getCode()),
                    new Arg("name", p.getName())
            ));
            p.setCustom(true);
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
            p.setName(p.getCode());
            UPA.getPersistenceUnit().merge(p);
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
            if (!StringUtils.isEmpty(adminProfiles)) {
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
                MapList<Integer, AppProfile> values = entityCache.getValues();
                for (AppProfile profile : values) {
                    String key = profile.getCode();
                    if (!StringUtils.isEmpty(key)) {
                        m.put(key.toLowerCase(), profile);
                    }
                }
                return m;
            }
        });
        return m.get(profileCode == null ? null : profileCode.toLowerCase());
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
                profiles.add(u.getLogin().toLowerCase());
            }
            profiles.addAll(expandProfileCodes(profiles));
        } else {
            profiles = new HashSet(profiles);
            AppUser u = findUser0(userId);
            if (u == null) {
                return null;
            }
            if (includeLogin) {
                profiles.add(u.getLogin().toLowerCase());
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
                                list.add(o.getProfile().getCode().toLowerCase());
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
            if (!StringUtils.isEmpty(profile)) {
                stack.push(profile.toLowerCase());
            }
        }
        while (!stack.isEmpty()) {
            String profileCode = stack.pop();
            found.add(profileCode);
            AppProfile profile = findProfileByCode(profileCode);
            if (profile != null) {
                String inherited = profile.getInherited();
                if (!StringUtils.isEmpty(inherited)) {
                    String[] st = inherited.split("[ ,;]");
                    for (String s : st) {
                        s = s.toLowerCase();
                        if (!found.contains(s)) {
                            stack.push(s);
                        }
                    }
                }
            }
        }
        return found;
    }

    public List<AppProfile> findProfilesByUser(int userId) {
        Set<String> codes = findUniformProfileCodesMapByUserId(userId, true, true);
        List<AppProfile> pro = new ArrayList<>();
        if (codes != null) {
            for (String code : codes) {
                AppProfile pp = findProfileByCode(code);
                if (pp != null) {
                    pro.add(pp);
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
//                        MapList<Integer, AppUserProfileBinding> values = entityCache.getValues();
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
        Set<String> foundProfileNames = (userId == null) ? (new HashSet<String>()) : findUniformProfileCodesMapByUserId(userId, true, true);
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
                    userId == null ? new int[0] : new int[]{userId},
                    profileExpr.getFilterExpression()).size() > 0;
        }
        return b;
    }

    public boolean userMatchesProfileFilter(Integer userId, String login, String profile, String whereClause) {
        return userMatchesProfileFilter(userId, login, profile, whereClause, null);
    }

    private InSetEvaluator createProfilesEvaluator(final Set<String> profiles) {
        return new SimpleJavaEvaluator(profiles);
    }

    public <T> List<T> filterByProfilePattern(List<T> in, Integer userId, String login, ProfilePatternFilter<T> filter) {
        List<T> out = new ArrayList<>();
        final HashMap<String, Object> cache = new HashMap<String, Object>();
        if (in != null) {
            for (T i : in) {
                String profilePattern = filter.getProfilePattern(i);
                Chronometer ch = new Chronometer();
                boolean b = userMatchesProfileFilter(userId, login, new ProfileFilterExpression(profilePattern), cache);
                ch.stop();
                int sec = (int) (ch.getTime() / 1000000000);
                if (sec > 1) {
                    b = userMatchesProfileFilter(userId, login, new ProfileFilterExpression(profilePattern), cache);
                }
                if (b) {
                    out.add(i);
                }
            }
        }
        return out;
    }

    public boolean userMatchesProfileFilter(int userId, String profileExpr) {
        return userMatchesProfileFilter(userId, null, profileExpr, null);
    }

    public boolean userMatchesProfileFilter(String userLogin, String profileExpr) {
        return userMatchesProfileFilter(null, userLogin, profileExpr, null);
    }

    public List<String> autoCompleteUserOrProfile(String userOrProfile) {
        if (userOrProfile == null) {
            userOrProfile = "";
        }
        userOrProfile = userOrProfile.trim().toLowerCase();
        List<String> all = new ArrayList<>();
        for (AppProfile appProfile : findProfiles()) {
            if (appProfile.getCode() != null && appProfile.getCode().toLowerCase().contains(userOrProfile)) {
                all.add(appProfile.getCode().trim());
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

    public boolean isCurrentSessionMatchesProfileFilter(String profilePattern) {
        final AppUser currentUser = getContext().getCorePlugin().getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        return filterUsersByProfileFilter(Arrays.asList(currentUser), profilePattern, currentUser.getType() == null ? null : currentUser.getType().getId()).size() > 0;
    }

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
        return filterUsersByProfileFilter(Arrays.asList(currentUser), profilePattern, currentUser.getType() == null ? null : currentUser.getType().getId()).size() > 0;
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
        StringBuilder2 q = new StringBuilder2("-1");
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
                q.delete();
                q.append("-1");
            }
        }
        return ret;
    }

//    public List<AppContact> filterContactsByProfileFilter(List<AppContact> contacts, String profilePattern) {
//        List<AppContact> ret = new ArrayList<>();
//        Set<Integer> visited = new HashSet<>();
//        for (AppUser user : filterUsersByProfileFilter(filterUsersBysContacts(contacts), profilePattern, null)) {
//            AppContact c = user.getContact();
//            if (c != null && !visited.contains(c.getId())) {
//                visited.add(c.getId());
//                ret.add(c);
//            }
//        }
//        return ret;
//    }
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

    public AppUser createUser(AppUser newUser, int userTypeId, int departmentId, boolean attachToExistingUser, String[] defaultProfiles, VrPasswordStrategy passwordStrategy) {
        AppUser u = findUser(newUser.getId());
        if (u == null) {
            if (passwordStrategy == null) {
                passwordStrategy = VrPasswordStrategyRandom.INSTANCE;
            }
            String login = resolveLoginProposal(newUser);
            if (StringUtils.isEmpty(login)) {
                login = "user";
            }
            String password = passwordStrategy.generatePassword(newUser);
            u = findUser(login);
            if (u != null) {
                if (u.getId() == newUser.getId()) {
                    //this is the same user !! ok
                    return u;
                }
            }
            if (!attachToExistingUser || (u != null)) {
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
            AppDepartment userDepatment = getContext().getCorePlugin().findDepartment(departmentId);
            if (u == null) {
                u = new AppUser();
                u.setLogin(login);
                // TODO FIX ME
                //u.setContact(contact);
                String pwd = password;
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
        if (defaultProfiles != null) {
            for (String defaultProfile : defaultProfiles) {
                if (!StringUtils.isEmpty(defaultProfile)) {
                    userAddProfile(u.getId(), defaultProfile);
                }
            }
        }
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
        if (StringUtils.isEmpty(login)) {
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
        return StringUtils.normalizeString(fn.toLowerCase()).replace(" ", "") + "." + StringUtils.normalizeString(ln.toLowerCase()).replace(" ", "");
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
        return StringUtils.normalizeString(fn.toLowerCase()).replace(" ", "") + "." + StringUtils.normalizeString(ln.toLowerCase()).replace(" ", "");
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
        if (StringUtils.isEmpty(login)) {
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
                if (!StringUtils.isEmpty(defaultProfile)) {
                    userAddProfile(user.getId(), defaultProfile);
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
        if (!StringUtils.isEmpty(nin)) {
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
        if (!StringUtils.isEmpty(c.getLogin())) {
            return findUser(c.getLogin());
        }
        String nin = c.getNin();
        if (!StringUtils.isEmpty(nin)) {
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

//    public List<AppUser> findContacts(String name, String type) {
//        PersistenceUnit pu = UPA.getPersistenceUnit();
//        return pu.createQuery(
//                "Select u from AppUser u where 1=1 "
//                + (StringUtils.isEmpty(name) ? "" : " u.fullName like :name ")
//                + (StringUtils.isEmpty(type) ? "" : " and u.type.name =:type ")
//        )
//                .setParameter("name", "%" + name + "%", !StringUtils.isEmpty(name))
//                .setParameter("name", name, !StringUtils.isEmpty(name))
//                .getResultList();
//    }
}
