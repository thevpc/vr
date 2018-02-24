package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.plugins.*;
import net.vpc.app.vainruling.core.service.util.AppVersion;
import net.vpc.app.vainruling.core.service.util.PlatformReflector;
import net.vpc.common.util.ListValueMap;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

class CorePluginBodyPluginManager extends CorePluginBody{
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CorePluginBodyPluginManager.class.getName());
    private List<Plugin> plugins;
    private AppVersion appVersion;
    private Map<String, PluginComponent> components;
    private Map<String, PluginBundle> bundles;

    @Override
    public void onPrepare() {
        for (PersistenceUnit pu : getPersistenceUnits()) {
            pu.invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    tryInstall();
                }
            });
        }
    }

    protected List<PersistenceUnit> getPersistenceUnits() {
        return new ArrayList<>(UPA.getPersistenceGroup("").getPersistenceUnits());
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
                    String urlString = url.toString();
                    int pos = urlString.lastIndexOf("/META-INF/vr-plugin.properties");
                    String p = urlString.substring(0,pos)+urlString.substring(pos+"/META-INF/vr-plugin.properties".length());
                    if(p.endsWith("!")){
                        int ji = p.lastIndexOf("jar:");
                        p=p.substring(ji+"jar:".length(),p.length()-1);

                    }
                    PluginComponent e = PluginComponent.parsePluginComponent(new URL(p),url);
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


    public List<String> getPluginIds() {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : getPlugins()) {
            plugins.add(plugin.getId());
        }
        return plugins;
    }

    public List<String> getPluginBeans() {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : getPlugins()) {
            plugins.addAll(plugin.getBeanNames());
        }
        return plugins;
    }

    public Map<String, List<String>> getPluginsAPI() {
        String[] beanNames = VrApp.getContext().getBeanNamesForAnnotation(AppPlugin.class);
        Map<String, List<String>> api = new HashMap<>();
        for (String beanName : beanNames) {

            Object bean = VrApp.getContext().getBean(beanName);
            ArrayList<String> a = new ArrayList<>();
            //declared Method
            for (Method method : bean.getClass().getMethods()) {
                String mname = method.getName();
                if (Modifier.isStatic(method.getModifiers()) || Modifier.isAbstract(method.getModifiers())) {
                    continue;
                }
                if (method.getParameterCount() == 0) {
                    if (
                            mname.equals("toString")
                                    || mname.equals("notify")
                                    || mname.equals("notifyAll")
                                    || mname.equals("getClass")
                                    || mname.equals("hashCode")
                                    || mname.equals("wait")
                            ) {
                        continue;
                    }
                }
                if (method.getParameterCount() == 1) {
                    if (
                            (mname.equals("wait") && method.getParameterTypes()[0].equals(Long.TYPE))
                                    || (mname.equals("equals") && method.getParameterTypes()[0].equals(Object.class))
                            ) {
                        continue;
                    }
                }
                if (method.getParameterCount() == 2) {
                    if (
                            (mname.equals("wait") && method.getParameterTypes()[0].equals(Long.TYPE) && method.getParameterTypes()[1].equals(Integer.TYPE))
                            ) {
                        continue;
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append(typeToString(method.getGenericReturnType()));
                sb.append(" ");
                sb.append(beanName);
                sb.append(".");
                sb.append(mname);
                sb.append("(");
                boolean first = true;
                for (Type t : method.getGenericParameterTypes()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(typeToString(t));
                }
                sb.append(")");
                a.add(sb.toString());
            }
            api.put(beanName, a);
        }
        return api;
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
                Plugin p = new Plugin(objects, bnames, pluginInfo);
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
                _appVersion.setId(p.getProperty("id"));
                _appVersion.setShortName(p.getProperty("short-name"));
                _appVersion.setLongName(p.getProperty("long-name"));
                _appVersion.setVersion(p.getProperty("version"));
                _appVersion.setBuildNumber(p.getProperty("build-number"));
                _appVersion.setBuildDate(p.getProperty("build-date"));
                _appVersion.setAuthor(p.getProperty("author"));
                _appVersion.setDefaultPublicTheme(p.getProperty("default-public-theme"));
                _appVersion.setDefaultPrivateTheme(p.getProperty("default-private-theme"));
                for (Object k : p.keySet()) {
                    String sk=String.valueOf(k);
                    if(sk.startsWith("config.")){
                        String kk = sk.substring("config.".length());
                        _appVersion.getConfig().put(kk,p.getProperty(kk));
                    }
                }

            }
            appVersion = _appVersion;
        }
        return appVersion;
    }

    private String typeToString(Type type) {
        if (type instanceof Class) {
            Class cls = (Class) type;
            if (cls.isArray()) {
                return typeToString(cls.getComponentType()) + "[]";
            }
            return cls.getName();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            return ptype.toString();
        }
        return type.toString();
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


}
