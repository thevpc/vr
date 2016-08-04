/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.util.PlatformReflector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
public class Plugin implements Comparable<Plugin> {

    private static final Logger log = Logger.getLogger(Plugin.class.getName());

    String beanName;
    Object beanInstance;

    public Plugin(String beanName, Object beanInstance) {
        this.beanName = beanName;
        this.beanInstance = beanInstance;
    }

    public static int compare(Plugin s1, Plugin s2) {
        Object o1 = s1.getBeanInstance();
        AppPlugin a1 = (AppPlugin) PlatformReflector.getTargetClass(o1).getAnnotation(AppPlugin.class);
        Object o2 = s2.getBeanInstance();
        AppPlugin a2 = (AppPlugin) PlatformReflector.getTargetClass(o2).getAnnotation(AppPlugin.class);
        HashSet<String> hs1 = new HashSet<>(Arrays.asList(a1.dependsOn()));
        HashSet<String> hs2 = new HashSet<>(Arrays.asList(a2.dependsOn()));
        final String corePlugin = "corePlugin";
        if (!s1.getBeanName().equals(corePlugin)) {
            hs1.add(corePlugin);
        }
        if (!s2.getBeanName().equals(corePlugin)) {
            hs2.add(corePlugin);
        }
        int r = 0;
        boolean d = false;
        if (hs1.contains(s2.getBeanName())) {
            r = 1;
        } else if (hs2.contains(s1.getBeanName())) {
            r = -1;
        } else {
            d = true;
            r = s1.getBeanName().compareTo(s2.getBeanName());
        }
//        String op = "=";
//        if (r < 0) {
//            op = "<";
//        } else if (r > 0) {
//            op = ">";
//        }
//        System.out.println(s1.getBeanName() + " " + op + " " + s2.getBeanName() + (d ? " (byname)" : ""));
        return r;
    }

    public String getBeanName() {
        return beanName;
    }

    public <T> T getBeanInstance() {
        return (T) beanInstance;
    }

    public String getVersion() {
        AppPlugin p = (AppPlugin) PlatformReflector.getTargetClass(beanInstance).getAnnotation(AppPlugin.class);
        return p.version();
    }

    public PlatformReflector.InstanceInvoker[] findMethodsByAnnotation(Class anno) {
        return PlatformReflector.findInstanceByAnnotation(beanInstance, anno);
    }

    public PlatformReflector.InstanceInvoker[] findMethodsByAnnotation(Class anno, Class[] args) {
        return PlatformReflector.findInstanceByAnnotation(beanInstance, anno, args);
    }

    public PlatformReflector.InstanceInvoker[] findMethodsByName(String name, Class[] args) {
        return PlatformReflector.findInstanceByName(beanInstance, name, args);
    }

    @Override
    public int compareTo(Plugin o) {
        return compare(this, o);
    }

    public void start() {
        log.log(Level.INFO, "Start Plugin {0}", beanName);
        for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(Start.class)) {
            p.invoke();
            return;
        }
    }

    public void install() {
        log.log(Level.INFO, "Install Plugin {0}", beanName);
        for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(Install.class)) {
            p.invoke();
            return;
        }
    }

    public void installDemo() {
        log.log(Level.INFO, "Install Plugin Demo {0}", beanName);
        for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(InstallDemo.class)) {
            p.invoke();
            return;
        }
    }


//    public boolean isEnabledEntityAction(Class entityType, String actionName, Object obj) {
//        Entity entity = UPA.getPersistenceUnit().getEntity(entityType);
//        for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(EntityActionFilter.class)) {
//            EntityActionFilter r = p.getAnnotation();
//            Class[] argTypes = p.getParameterTypes();
//            Class rt = p.getReturnType();
//            boolean unspecifiedEntityType = r.entityType().equals(Void.class);
//            boolean unspecifiedActionName = r.actionName().isEmpty();
//            if (!rt.equals(Boolean.class) && !rt.equals(Boolean.TYPE)) {
//                throw new IllegalArgumentException("Unvalid isEnabledEntityAction method " + p);
//            }
//            if (unspecifiedActionName || unspecifiedEntityType) {
//                if (!rt.equals(Boolean.TYPE)) {
//                    throw new IllegalArgumentException("Unvalid bulk isEnabledEntityAction method " + p + " : expected boolean return type");
//                }
//            }
//            Object[] vals = new Object[argTypes.length];
//            for (int i = 0; i < vals.length; i++) {
//                if (argTypes[i].equals(Class.class)) {
//                    vals[i] = entityType;
//                } else if (argTypes[i].equals(Entity.class)) {
//                    vals[i] = entity;
//                } else if (argTypes[i].equals(Object.class)) {
//                    vals[i] = obj;
//                } else if (argTypes[i].equals(entity.getEntityType())) {
//                    vals[i] = obj;
//                } else if (argTypes[i].equals(String.class)) {
//                    vals[i] = actionName;
//                } else {
//                    throw new IllegalArgumentException("Unsupported isEnabledEntityAction method " + p);
//                }
//            }
//            if ((unspecifiedEntityType
//                    || r.entityType().equals(entityType))
//                    && (unspecifiedActionName
//                    || r.actionName().equals(actionName))) {
//                Boolean i = null;
//                boolean ok = false;
//                try {
//                    i = p.invoke(vals);
//                    ok = true;
//                } catch (UnsupportedOperationException e) {
//
//                }
//                if (ok && i != null) {
//                    return i;
//                }
//            }
//        }
//        throw new UnsupportedOperationException();
//    }

//    public ActionInfo[] getEntityActionList(Class entityType, Object obj) {
//        ArrayList<ActionInfo> actions = new ArrayList<>();
//        Entity entity = UPA.getPersistenceUnit().getEntity(entityType);
//        for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(EntityActionList.class)) {
//            EntityActionList r = p.getAnnotation();
//            Class[] argTypes = p.getParameterTypes();
//            Class rt = p.getReturnType();
//            boolean unspecifiedEntityType = r.entityType().equals(Void.class);
//            if (!rt.equals(String[].class)) {
//                throw new IllegalArgumentException("Unvalid getEntityActionList method " + p);
//            }
//            Object[] vals = new Object[argTypes.length];
//            for (int i = 0; i < vals.length; i++) {
//                if (argTypes[i].equals(Class.class)) {
//                    vals[i] = entityType;
//                } else if (argTypes[i].equals(Entity.class)) {
//                    vals[i] = entity;
//                } else if (argTypes[i].equals(Object.class)) {
//                    vals[i] = obj;
//                } else if (argTypes[i].equals(entity.getEntityType())) {
//                    vals[i] = obj;
//                } else {
//                    throw new IllegalArgumentException("Unsupported isEnabledEntityAction method " + p);
//                }
//            }
//            if ((unspecifiedEntityType
//                    || r.entityType().equals(entityType))) {
//                String[] i = null;
//                boolean ok = false;
//                try {
//                    i = p.invoke(vals);
//                    ok = true;
//                } catch (UnsupportedOperationException e) {
//
//                }
//                if (ok && i != null) {
//                    for (String y : i) {
//                        actions.add(new ActionInfo(y, y, "") {
//
//                            @Override
//                            public <T> T invoke(Class entityType, String actionName, Object obj, Object[] args) {
//                                return invokeEntityAction(entityType, actionName, obj, args);
//                            }
//
//                        });
//                    }
//                }
//            }
//
//        }
//        for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(EntityAction.class)) {
//            EntityAction r = p.getAnnotation();
//            String actionId = r.actionName();
//            String actionLabel = r.actionLabel();
//            if (StringUtils.isEmpty(actionId)) {
//                actionId = p.getName();
//            }
//            if (StringUtils.isEmpty(actionLabel)) {
//                actionLabel = actionId;
//            }
//            boolean unspecifiedEntityType = r.entityType().equals(Void.class);
//            if ((unspecifiedEntityType
//                    || r.entityType().equals(entityType))) {
//                actions.add(new ActionInfo(actionId, actionLabel, r.actionStyle()) {
//
//                    @Override
//                    public <T> T invoke(Class entityType, String actionName, Object obj, Object[] args) {
//                        return invokeEntityAction(entityType, actionName, obj, args);
//                    }
//                });
//            }
//        }
//        return actions.toArray(new ActionInfo[actions.size()]);
//    }

    @Override
    public String toString() {
        return "Plugin{" + beanName + '}';
    }

}
