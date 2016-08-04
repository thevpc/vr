/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.model.AppVersion;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.service.util.PlatformReflector;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@Service
@DependsOn(value = {"i18n", "vrApp"})
public class BootstrapService {

    private static final Logger log = Logger.getLogger(BootstrapService.class.getName());

    @Autowired
    private I18n i18n;

    @PostConstruct
    public void prepare() {
        i18n.register("i18n.dictionary");
        i18n.register("i18n.presentation");
        i18n.register("i18n.service");
//        try {
//            InitialContext c = new InitialContext();
//            c.bind("java:comp/env/datasource", VrApp.getContext().getBean("datasource"));
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                tryInstall();
            }
        });
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

    public void tryInstall() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ArrayList<Plugin> toInstall = new ArrayList<>();
        ArrayList<Plugin> toStart = new ArrayList<>();
        for (Plugin pp : VrApp.getBean(CorePlugin.class).getPlugins()) {
            String sver = pp.getVersion();
            AppVersion v = pu.findById(AppVersion.class, pp.getBeanName());
            if (v == null) {
                v = pu.findById(AppVersion.class, pp.getBeanName());
            }
            boolean ignore = false;
            if (v == null || !sver.equals(v.getServiceVersion()) || !v.isCoherent()) {
                if (v != null && !v.isActive()) {
                    log.log(Level.INFO, "Plugin {0} is deactivated (version {1})", new Object[]{pp.getBeanName(), pp.getVersion()});
                    //ignore
                    ignore = true;
                } else {
                    if (v == null) {
                        v = new AppVersion();
                        v.setActive(true);
                        final net.vpc.upa.types.Timestamp dte = new net.vpc.upa.types.Timestamp();
                        v.setInstallDate(dte);
                        v.setServiceName(pp.getBeanName());
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
                log.log(Level.INFO, "Plugin {0} is uptodate ({1})", new Object[]{pp.getBeanName(), pp.getVersion()});
            }
            if (!ignore) {
                toStart.add(pp);
            }
        }
        HashSet<String> nonCoherent = new HashSet<>();
        Collections.sort(toInstall);
        for (Plugin plugin : toInstall) {
            if (!nonCoherent.contains(plugin.getBeanName())) {
                try {
                    plugin.install();
                } catch (Exception e) {
                    nonCoherent.add(plugin.getBeanName());
                    log.log(Level.SEVERE, "Error Starting " + plugin.getBeanName(), e);
                    AppVersion v = pu.findById(AppVersion.class, plugin.getBeanName());
                    v.setCoherent(false);
                    pu.merge(v);

                }
            }
        }
        for (Plugin plugin : toInstall) {
            if (!nonCoherent.contains(plugin.getBeanName())) {
                try {
                    plugin.installDemo();
                } catch (Exception e) {
                    nonCoherent.add(plugin.getBeanName());
                    log.log(Level.SEVERE, "Error Starting " + plugin.getBeanName(), e);
                    AppVersion v = pu.findById(AppVersion.class, plugin.getBeanName());
                    v.setCoherent(false);
                    pu.merge(v);
                }
            }
        }

        Collections.sort(toStart);
        for (Plugin plugin : toStart) {
            i18n.register("i18n." + plugin.getBeanName() + ".dictionary");
            i18n.register("i18n." + plugin.getBeanName() + ".presentation");
            i18n.register("i18n." + plugin.getBeanName() + ".service");
            try {
                plugin.start();
            } catch (Exception e) {
                nonCoherent.add(plugin.getBeanName());
                log.log(Level.SEVERE, "Error Starting " + plugin.getBeanName(), e);
                AppVersion v = pu.findById(AppVersion.class, plugin.getBeanName());
                v.setCoherent(false);
                pu.merge(v);

            }
        }
    }

}
