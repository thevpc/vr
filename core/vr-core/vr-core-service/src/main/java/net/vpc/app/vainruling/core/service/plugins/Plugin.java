/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.plugins;

import net.vpc.app.vainruling.core.service.util.PlatformReflector;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
public class Plugin implements Comparable<Plugin> {

    private static final Logger log = Logger.getLogger(Plugin.class.getName());

    PluginBundle info;
    List<Object> beanInstances;
    List<String> beanNames;

    public Plugin(List<Object> beanInstances, List<String> beanNames,PluginBundle info) {
        this.beanInstances = beanInstances;
        this.beanNames = beanNames;
        this.info = info;
    }

    public List<String> getBeanNames() {
        return beanNames;
    }

    public static int compare(Plugin s1, Plugin s2) {
        HashSet<String> hs1 = new HashSet<>(s1.getInfo().getBundleDependencies());
        HashSet<String> hs2 = new HashSet<>(s2.getInfo().getBundleDependencies());
        int r = 0;
        if (hs1.contains(s2.getId())) {
            r = 1;
        } else if (hs2.contains(s1.getId())) {
            r = -1;
        } else {
            r = s1.getId().compareTo(s2.getId());
        }
        return r;
    }

    public String getId() {
        return getInfo().getId();
    }

    public <T> T getBeanInstances() {
        return (T) beanInstances;
    }

    public PluginBundle getInfo() {
        return info;
    }

    public PlatformReflector.InstanceInvoker[] findMethodsByAnnotation(Object obj,Class anno) {
        return PlatformReflector.findInstanceByAnnotation(obj, anno);
    }

    public PlatformReflector.InstanceInvoker[] findMethodsByAnnotation(Object obj,Class anno, Class[] args) {
        return PlatformReflector.findInstanceByAnnotation(obj, anno, args);
    }

    public PlatformReflector.InstanceInvoker[] findMethodsByName(Object obj,String name, Class[] args) {
        return PlatformReflector.findInstanceByName(obj, name, args);
    }

    @Override
    public int compareTo(Plugin o) {
        return compare(this, o);
    }

    public void start() {
        for (Object obj : beanInstances) {
            log.log(Level.INFO, "Start Plugin {0}", obj);
            for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(obj,Start.class)) {
                p.invoke();
            }
        }
    }

    public void install() {
        for (Object obj : beanInstances) {
            log.log(Level.INFO, "Install Plugin {0}", obj);
            for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(obj,Install.class)) {
                p.invoke();
            }
        }
    }

    public void installDemo() {
        for (Object obj : beanInstances) {
            log.log(Level.INFO, "Install Plugin Demo {0}", obj);
            for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(obj,InstallDemo.class)) {
                p.invoke();
            }
        }
    }


    @Override
    public String toString() {
        return "Plugin{" + getId() + '}';
    }

}
