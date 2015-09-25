/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.core.Plugin;
import net.vpc.app.vainruling.api.core.PluginManagerService;
import net.vpc.app.vainruling.api.i18n.I18n;
import net.vpc.app.vainruling.api.model.AppVersion;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service
@DependsOn(value = {"i18n", "vrApp", "pluginManagerService"})
public class BootstrapService {

    private static final Logger log = Logger.getLogger(BootstrapService.class.getName());

    @Autowired
    private I18n i18n;

    @PostConstruct
    public void prepare() {
        i18n.register("i18n.dictionary");
        i18n.register("i18n.presentation");
        i18n.register("i18n.service");
        UPA.getContext().invokePrivileged(new Action<Object>() {
            @Override
            public Object run() {
                tryInstall();
                return null;
            }
        }, null);
    }

    private List<String> getOrderedPlugins() {
        final Map<String, Object> s = VrApp.getContext().getBeansWithAnnotation(AppPlugin.class);
        ArrayList<String> ordered = new ArrayList<>();
        for (String k : s.keySet()) {
            Object o1 = VrApp.getContext().getBean(k);
            AppPlugin a1 = o1.getClass().getAnnotation(AppPlugin.class);
            for (String d : a1.dependsOn()) {
                VrApp.getContext().getBean(d);
            }
            ordered.add(k);
        }
        Collections.sort(ordered, new Comparator<String>() {

            @Override
            public int compare(String s1, String s2) {
                Object o1 = VrApp.getContext().getBean(s1);
                AppPlugin a1 = o1.getClass().getAnnotation(AppPlugin.class);
                Object o2 = VrApp.getContext().getBean(s1);
                AppPlugin a2 = o2.getClass().getAnnotation(AppPlugin.class);
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
        for (Plugin pp : VrApp.getBean(PluginManagerService.class).getPlugins()) {
            String sver = pp.getVersion();
            AppVersion v = (AppVersion) pu.findById(AppVersion.class, pp.getBeanName());
            boolean ignore = false;
            if (v == null || !sver.equals(v.getServiceVersion()) || !v.isCoherent()) {
                if (v != null && !v.isActive()) {
                    log.log(Level.INFO, "Plugin {0} is diactivated (version {1})", new Object[]{pp.getBeanName(), pp.getVersion()});
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
                    AppVersion v = (AppVersion) pu.findById(AppVersion.class, plugin.getBeanName());
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
                    AppVersion v = (AppVersion) pu.findById(AppVersion.class, plugin.getBeanName());
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
                AppVersion v = (AppVersion) pu.findById(AppVersion.class, plugin.getBeanName());
                v.setCoherent(false);
                pu.merge(v);

            }
        }
    }

}
