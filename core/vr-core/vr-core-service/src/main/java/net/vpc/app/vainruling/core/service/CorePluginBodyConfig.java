package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.cache.EntityCache;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.CustomTextFormatter;
import net.vpc.common.util.MapList;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.FileHandler;

class CorePluginBodyConfig extends CorePluginBody{
    public static final java.util.logging.Logger LOG_APPLICATION_STATS = java.util.logging.Logger.getLogger(CorePlugin.class.getName() + ".Stats");
    public static final String PATH_LOG = "/Var/Log";

    @Override
    public void onInstall() {
        AppConfig mainConfig = new AppConfig();
        mainConfig.setId(1);
        mainConfig.setMainCompany(installDefaultCompany());

        String periodName=getContext().getCorePlugin().getAppVersion().getConfig().get("period");
        if(StringUtils.isEmpty(periodName)){
            periodName="${year}";
        }
        periodName=periodName.replace("${year}",String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        periodName=periodName.replace("${next-year}",String.valueOf(Calendar.getInstance().get(Calendar.YEAR)+1));
        periodName=periodName.replace("${previous-year}",String.valueOf(Calendar.getInstance().get(Calendar.YEAR)-1));

        mainConfig.setMainPeriod(new AppPeriod(periodName));
        getContext().getCorePlugin().findOrCreate(mainConfig, "id");
    }

    @Override
    public void onStart() {
        getContext().getCorePlugin().getRootFileSystem().get(PATH_LOG).mkdirs();
        if (LOG_APPLICATION_STATS.getHandlers().length == 0) {
            UPA.getPersistenceUnit("main").invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    String path = getContext().getCorePlugin().bodyFileSystem.getNativeFileSystemPath();
                    try {
                        String pp = path + PATH_LOG;
                        new File(pp).mkdirs();
                        FileHandler handler = new FileHandler(pp + "/application-stats.log", 5 * 1024 * 1024, 5, true);
                        handler.setFormatter(new CustomTextFormatter());
                        LOG_APPLICATION_STATS.addHandler(handler);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void setAppProperty(String propertyName, String userLogin, Object propertyValue) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppUser u = null;
        if (userLogin != null) {
            u = getContext().getCorePlugin().findUser(userLogin);
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
        final EntityCache entityCache = getContext().getCacheService().get(AppProperty.class);
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
            u = getContext().getCorePlugin().findUser(userLogin);
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

    public String getUnknownUserPhoto() {
        return getUserPhoto(-1);
    }

    public String getUnknownUserIcon() {
        return getUserPhoto(-1, true);
    }

    public String getUserPhoto(int id) {
        return getUserPhoto(id, false);
    }

    public String getUserIcon(int id) {
        return getUserPhoto(id, true);
    }

    public String getUserPhoto(int id, boolean icon) {
        return UPA.getContext().invokePrivileged(new Action<String>() {
            @Override
            public String run() {
                AppUser t = getContext().getCorePlugin().findUser(id);
                AppContact c = t == null ? null : t.getContact();
                boolean female = false;
                if (c != null) {
                    AppGender g = c.getGender();
                    if (g != null) {
                        if ("F".equals(g.getCode())) {
                            female = true;
                        }
                    }
                }
                List<String> paths = new ArrayList<String>();
                for (String p : new String[]{"Config/photo.png", "Config/photo.jpg", "Config/photo.gif"}) {
                    paths.add(p);
                }
                if (female) {
                    for (String p : new String[]{"Config/photo-women.png", "Config/photo-women.jpg", "Config/photo-women.gif"}) {
                        paths.add(p);
                    }
                } else {
                    for (String p : new String[]{"Config/photo-men.png", "Config/photo-men.jpg", "Config/photo-men.gif"}) {
                        paths.add(p);
                    }
                }
                VFile file = VrUtils.getUserAbsoluteFile(t == null ? -1 : t.getId(), paths.toArray(new String[paths.size()]));
                if (icon) {
                    if (file != null && file.isFile() && file.exists()) {
                        try {
                            return VrUtils.getOrResizeIcon(file).getPath();
                        } catch (IOException e) {
                            //
                        }
                    }
                }
                String photo = (file == null) ? null : (file.getPath());
                if (photo == null) {
                    return "private-theme-context://images/person.png";
                }
                return photo;
            }
        });

    }

    public String getCurrentUserPhoto() {
        UserToken token = getContext().getCorePlugin().getCurrentToken();
        Integer s = token == null ? null : token.getUserId();
        if (s != null) {
            return getUserPhoto(s);
        }
        return null;
    }

    public String getCurrentUserIcon() {
        UserToken token = getContext().getCorePlugin().getCurrentToken();
        Integer s = token == null ? null : token.getUserId();
        if (s != null) {
            return getUserIcon(s);
        }
        return null;
    }

    public String getDefaultUserPublicTheme() {
        return (String) getOrCreateAppPropertyValue("System.DefaultPublicTheme", null, "");
    }

    public String getCurrentUserPublicTheme() {
        String login = getContext().getCorePlugin().getCurrentUserLogin();
        String val = null;
        if (login != null) {
            val = UPA.getPersistenceUnit().invokePrivileged(new Action<String>() {
                @Override
                public String run() {
                    return (String) getOrCreateAppPropertyValue("System.DefaultPublicTheme", login, "");
                }
            });
        }
        return val == null ? "" : val;
    }

    public String getDefaultUserPrivateTheme() {
        return (String) getOrCreateAppPropertyValue("System.DefaultPrivateTheme", null, "");
    }

    public String getCurrentUserPrivateTheme() {
        String login = getContext().getCorePlugin().getCurrentUserLogin();
        String val = null;
        if (login != null) {
            val = UPA.getPersistenceUnit().invokePrivileged(new Action<String>() {
                @Override
                public String run() {
                    return (String) getOrCreateAppPropertyValue("System.DefaultPrivateTheme", login, "");
                }
            });
        }
        return val == null ? "" : val;
    }

    public void setCurrentUserPublicTheme(String theme) {
        String login = getContext().getCorePlugin().getCurrentUserLogin();
        if (login != null) {
            UPA.getContext().invokePrivileged(new VoidAction() {
                                                  @Override
                                                  public void run() {
                                                      setAppProperty("System.DefaultPublicTheme", login, theme);
                                                  }
                                              }
            );

        }
    }

    public void setCurrentUserPrivateTheme(String theme) {
        String login = getContext().getCorePlugin().getCurrentUserLogin();
        if (login != null) {
            UPA.getContext().invokePrivileged(new VoidAction() {
                                                  @Override
                                                  public void run() {
                                                      setAppProperty("System.DefaultPrivateTheme", login, theme);
                                                  }
                                              }
            );

        }
    }

    public void setUserTheme(int userId, String theme) {
        AppUser user = getContext().getCorePlugin().findUser(userId);
        if (user != null) {
            setAppProperty("System.DefaultTheme", user.getLogin(), theme);
        }
    }

    private AppCountry installDefaultCountry() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String countryName=getContext().getCorePlugin().getAppVersion().getConfig().get("country");
        if(StringUtils.isEmpty(countryName)){
            countryName="Tunisia";
        }
        AppCountry country = pu.findByMainField(AppCountry.class, countryName);
        if (country == null) {
            country = new AppCountry();
            country.setName(countryName);
            pu.persist(country);
        }
        return country;
    }

    private AppIndustry installDefaultIndustry() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String industryName=getContext().getCorePlugin().getAppVersion().getConfig().get("industry");
        if(StringUtils.isEmpty(industryName)){
            industryName="My Industry";
        }

        AppIndustry eduIndustry = pu.findByMainField(AppIndustry.class, industryName);
        if (eduIndustry == null) {
            eduIndustry = new AppIndustry();
            eduIndustry.setName(industryName);
            pu.persist(eduIndustry);
        }
        return eduIndustry;
    }

    private AppCompany installDefaultCompany() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String companyName=getContext().getCorePlugin().getAppVersion().getConfig().get("company");
        if(StringUtils.isEmpty(companyName)){
            companyName="My Company";
        }
        AppCompany defaultCompany = pu.findByMainField(AppIndustry.class, companyName);
        if (defaultCompany == null) {
            defaultCompany = new AppCompany();
            defaultCompany.setName(companyName);
            defaultCompany.setIndustry(installDefaultIndustry());
            defaultCompany.setCountry(installDefaultCountry());
            pu.persist(defaultCompany);
        }
        return defaultCompany;
    }

    public AppConfig getCurrentConfig() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppConfig c = pu.findById(AppConfig.class, 1);
        //should exist;
        return c;
    }

    public AppPeriod findPeriodOrMain(int id) {
        AppPeriod p = findPeriod(id);
        if (p == null) {
            p = getCurrentPeriod();
        }
        return p;
    }

    public AppPeriod findPeriod(int id) {
        return (AppPeriod) getContext().getCacheService().get(AppPeriod.class).getValues().getByKey(id);
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

    public List<AppPeriod> findPeriods() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQuery("Select u from AppPeriod u order by u.name  desc")
                .getResultList();
    }

    public AppPeriod findPeriod(String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppPeriod) pu.createQuery("Select u from AppPeriod u where (u.snapshotName=null or u.snapshotName='') and u.name=:name")
                .setParameter("name", name).getFirstResultOrNull();
    }

    public AppPeriod getCurrentPeriod() {
        AppConfig currentConfig = getCurrentConfig();
        return currentConfig == null ? null : currentConfig.getMainPeriod();
    }


    public AppUser findUserByContact(int contactId) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return pu.createQueryBuilder(AppUser.class)
                .byField("contactId", contactId)
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

    public AppDepartment findOrCreateAppDepartment(String code,String name,String description) {
        AppDepartment department = new AppDepartment();
        department.setCode(code);
        department.setName(name);
        department.setDescription(description);
        return getContext().getCorePlugin().findOrCreate(department,"code");
    }

}
