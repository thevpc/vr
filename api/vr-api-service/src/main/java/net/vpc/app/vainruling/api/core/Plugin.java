/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.EntityAction;
import net.vpc.app.vainruling.api.EntityActionFilter;
import net.vpc.app.vainruling.api.EntityActionList;
import net.vpc.app.vainruling.api.InstallDemo;
import net.vpc.app.vainruling.api.Install;
import net.vpc.app.vainruling.api.Start;
import net.vpc.app.vainruling.api.util.Reflector;
import net.vpc.upa.Entity;
import net.vpc.upa.UPA;
import net.vpc.common.strings.*;

/**
 *
 * @author vpc
 */
public class Plugin implements Comparable<Plugin> {

    private static final Logger log = Logger.getLogger(Plugin.class.getName());

    String beanName;
    Object beanInstance;

    public Plugin(String beanName, Object beanInstance) {
        this.beanName = beanName;
        this.beanInstance = beanInstance;
    }

    public String getBeanName() {
        return beanName;
    }

    public <T> T getBeanInstance() {
        return (T) beanInstance;
    }

    public String getVersion() {
        AppPlugin p = beanInstance.getClass().getAnnotation(AppPlugin.class);
        return p.version();
    }

    public Reflector.InstanceInvoker[] findMethodsByAnnotation(Class anno) {
        return Reflector.findInstanceByAnnotation(beanInstance, anno);
    }

    public Reflector.InstanceInvoker[] findMethodsByAnnotation(Class anno, Class[] args) {
        return Reflector.findInstanceByAnnotation(beanInstance, anno, args);
    }

    public Reflector.InstanceInvoker[] findMethodsByName(String name, Class[] args) {
        return Reflector.findInstanceByName(beanInstance, name, args);
    }

    @Override
    public int compareTo(Plugin o) {
        return compare(this, o);
    }

    public static int compare(Plugin s1, Plugin s2) {
        Object o1 = s1.getBeanInstance();
        AppPlugin a1 = o1.getClass().getAnnotation(AppPlugin.class);
        Object o2 = s2.getBeanInstance();
        AppPlugin a2 = o2.getClass().getAnnotation(AppPlugin.class);
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

    public void start() {
        log.log(Level.INFO, "Start Plugin {0}", beanName);
        for (Reflector.InstanceInvoker p : findMethodsByAnnotation(Start.class)) {
            p.invoke();
            return;
        }
    }

    public void install() {
        log.log(Level.INFO, "Install Plugin {0}", beanName);
        for (Reflector.InstanceInvoker p : findMethodsByAnnotation(Install.class)) {
            p.invoke();
            return;
        }
    }

    public void installDemo() {
        log.log(Level.INFO, "Install Plugin Demo {0}", beanName);
        for (Reflector.InstanceInvoker p : findMethodsByAnnotation(InstallDemo.class)) {
            p.invoke();
            return;
        }
    }

    public <T> T invokeEntityAction(Class entityType, String actionName, Object obj, Object[] args) {
        log.log(Level.INFO, "invoke Entity Action {0} {1}", new Object[]{entityType, actionName});
        Entity entity = UPA.getPersistenceUnit().getEntity(entityType);
        for (Reflector.InstanceInvoker p : findMethodsByAnnotation(EntityAction.class)) {
            EntityAction r = p.getAnnotation();
            Class[] argTypes = p.getParameterTypes();
            Class rt = p.getReturnType();
            boolean unspecifiedEntityType = r.entityType().equals(Void.class);
            String rname = r.actionName();
            if (rname.isEmpty()) {
                rname = p.getName();
            }
            boolean unspecifiedActionName = rname.equals("*");
            PluginActionEvent evt = new PluginActionEvent(entityType, actionName, obj, args);
            Object[] vals = new Object[argTypes.length];
            if (argTypes.length == 0) {

            } else if (argTypes.length == 1 && argTypes[0].equals(PluginActionEvent.class)) {
                vals[0] = evt;
            } else {
                //ignore all!!
                for (int i = 0; i < vals.length; i++) {
                    if (argTypes[i].equals(Class.class)) {
                        vals[i] = entityType;
                    } else if (argTypes[i].equals(Entity.class)) {
                        vals[i] = entity;
                    } else if (argTypes[i].equals(Object.class)) {
                        vals[i] = obj;
                    } else if (argTypes[i].equals(entity.getEntityType())) {
                        vals[i] = obj;
                    } else if (argTypes[i].equals(String.class)) {
                        vals[i] = actionName;
                    } else if (argTypes[i].equals(Object[].class)) {
                        vals[i] = args;
                    } else {
                        throw new IllegalArgumentException("Unsupported Action method " + p);
                    }
                }
            }

            if ((unspecifiedEntityType
                    || r.entityType().equals(entityType))
                    && (unspecifiedActionName
                    || rname.equals(actionName))) {

                T i = null;
                boolean ok = false;
                try {
                    i = p.invoke(vals);
                    ok = true;
                } catch (UnsupportedOperationException e) {

                }
                if (ok) {
                    return i;
                }
            }

        }
        throw new UnsupportedOperationException();
    }

    public boolean isEnabledEntityAction(Class entityType, String actionName, Object obj) {
        Entity entity = UPA.getPersistenceUnit().getEntity(entityType);
        for (Reflector.InstanceInvoker p : findMethodsByAnnotation(EntityActionFilter.class)) {
            EntityActionFilter r = p.getAnnotation();
            Class[] argTypes = p.getParameterTypes();
            Class rt = p.getReturnType();
            boolean unspecifiedEntityType = r.entityType().equals(Void.class);
            boolean unspecifiedActionName = r.actionName().isEmpty();
            if (!rt.equals(Boolean.class) && !rt.equals(Boolean.TYPE)) {
                throw new IllegalArgumentException("Unvalid isEnabledEntityAction method " + p);
            }
            if (unspecifiedActionName || unspecifiedEntityType) {
                if (!rt.equals(Boolean.TYPE)) {
                    throw new IllegalArgumentException("Unvalid bulk isEnabledEntityAction method " + p + " : expected boolean return type");
                }
            }
            Object[] vals = new Object[argTypes.length];
            for (int i = 0; i < vals.length; i++) {
                if (argTypes[i].equals(Class.class)) {
                    vals[i] = entityType;
                } else if (argTypes[i].equals(Entity.class)) {
                    vals[i] = entity;
                } else if (argTypes[i].equals(Object.class)) {
                    vals[i] = obj;
                } else if (argTypes[i].equals(entity.getEntityType())) {
                    vals[i] = obj;
                } else if (argTypes[i].equals(String.class)) {
                    vals[i] = actionName;
                } else {
                    throw new IllegalArgumentException("Unsupported isEnabledEntityAction method " + p);
                }
            }
            if ((unspecifiedEntityType
                    || r.entityType().equals(entityType))
                    && (unspecifiedActionName
                    || r.actionName().equals(actionName))) {
                Boolean i = null;
                boolean ok = false;
                try {
                    i = p.invoke(vals);
                    ok = true;
                } catch (UnsupportedOperationException e) {

                }
                if (ok && i != null) {
                    return i;
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    public ActionInfo[] getEntityActionList(Class entityType, Object obj) {
        ArrayList<ActionInfo> actions = new ArrayList<>();
        Entity entity = UPA.getPersistenceUnit().getEntity(entityType);
        for (Reflector.InstanceInvoker p : findMethodsByAnnotation(EntityActionList.class)) {
            EntityActionList r = p.getAnnotation();
            Class[] argTypes = p.getParameterTypes();
            Class rt = p.getReturnType();
            boolean unspecifiedEntityType = r.entityType().equals(Void.class);
            if (!rt.equals(String[].class)) {
                throw new IllegalArgumentException("Unvalid getEntityActionList method " + p);
            }
            Object[] vals = new Object[argTypes.length];
            for (int i = 0; i < vals.length; i++) {
                if (argTypes[i].equals(Class.class)) {
                    vals[i] = entityType;
                } else if (argTypes[i].equals(Entity.class)) {
                    vals[i] = entity;
                } else if (argTypes[i].equals(Object.class)) {
                    vals[i] = obj;
                } else if (argTypes[i].equals(entity.getEntityType())) {
                    vals[i] = obj;
                } else {
                    throw new IllegalArgumentException("Unsupported isEnabledEntityAction method " + p);
                }
            }
            if ((unspecifiedEntityType
                    || r.entityType().equals(entityType))) {
                String[] i = null;
                boolean ok = false;
                try {
                    i = p.invoke(vals);
                    ok = true;
                } catch (UnsupportedOperationException e) {

                }
                if (ok && i != null) {
                    for (String y : i) {
                        actions.add(new ActionInfo(y, y, "") {

                            @Override
                            public <T> T invoke(Class entityType, String actionName, Object obj, Object[] args) {
                                return invokeEntityAction(entityType, actionName, obj, args);
                            }

                        });
                    }
                }
            }

        }
        for (Reflector.InstanceInvoker p : findMethodsByAnnotation(EntityAction.class)) {
            EntityAction r = p.getAnnotation();
            String actionId = r.actionName();
            String actionLabel = r.actionLabel();
            if (StringUtils.isEmpty(actionId)) {
                actionId = p.getName();
            }
            if (StringUtils.isEmpty(actionLabel)) {
                actionLabel = actionId;
            }
            boolean unspecifiedEntityType = r.entityType().equals(Void.class);
            if ((unspecifiedEntityType
                    || r.entityType().equals(entityType))) {
                actions.add(new ActionInfo(actionId, actionLabel, r.actionStyle()) {

                    @Override
                    public <T> T invoke(Class entityType, String actionName, Object obj, Object[] args) {
                        return invokeEntityAction(entityType, actionName, obj, args);
                    }
                });
            }
        }
        return actions.toArray(new ActionInfo[actions.size()]);
    }

    @Override
    public String toString() {
        return "Plugin{" + beanName + '}';
    }

}
