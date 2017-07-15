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

    public Plugin(List<Object> beanInstances, PluginBundle info) {
        this.beanInstances = beanInstances;
        this.info = info;
    }

    public static int compare(Plugin s1, Plugin s2) {
        HashSet<String> hs1 = new HashSet<>(s1.getInfo().getBundleDependencies());
        HashSet<String> hs2 = new HashSet<>(s2.getInfo().getBundleDependencies());
//        final String corePlugin = "corePlugin";
//        if (!s1.getId().equals(corePlugin)) {
//            hs1.add(corePlugin);
//        }
//        if (!s2.getId().equals(corePlugin)) {
//            hs2.add(corePlugin);
//        }
        int r = 0;
//        boolean d = false;
        if (hs1.contains(s2.getId())) {
            r = 1;
        } else if (hs2.contains(s1.getId())) {
            r = -1;
        } else {
//            d = true;
            r = s1.getId().compareTo(s2.getId());
        }
//        String op = "=";
//        if (r < 0) {
//            op = "<";
//        } else if (r > 0) {
//            op = ">";
//        }
//        System.out.println(s1.getId() + " " + op + " " + s2.getId() + (d ? " (byname)" : ""));
        return r;
    }

//    public Manifest getManifest() {
//        Class targetClass = PlatformReflector.getTargetClass(beanInstances);
//        URL resource = targetClass.getResource("/META-INF/MANIFEST.MF");
//        Manifest manifest = null;
//        try {
//            manifest = new Manifest(resource.openStream());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return manifest;
//    }

//    public String getName() {
//        String beanName = getId();
//        if(beanName.endsWith("Plugin")){
//            beanName=beanName.substring(0,beanName.length()-"Plugin".length());
//        }
//        return beanName;
//    }

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
//            return;
            }
        }
    }

    public void install() {
        for (Object obj : beanInstances) {
            log.log(Level.INFO, "Install Plugin {0}", obj);
            for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(obj,Install.class)) {
                p.invoke();
//            return;
            }
        }
    }

    public void installDemo() {
        for (Object obj : beanInstances) {
            log.log(Level.INFO, "Install Plugin Demo {0}", obj);
            for (PlatformReflector.InstanceInvoker p : findMethodsByAnnotation(obj,InstallDemo.class)) {
                p.invoke();
//            return;
            }
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
//                throw new UPAIllegalArgumentException("Unvalid isEnabledEntityAction method " + p);
//            }
//            if (unspecifiedActionName || unspecifiedEntityType) {
//                if (!rt.equals(Boolean.TYPE)) {
//                    throw new UPAIllegalArgumentException("Unvalid bulk isEnabledEntityAction method " + p + " : expected boolean return type");
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
//                    throw new UPAIllegalArgumentException("Unsupported isEnabledEntityAction method " + p);
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
//                throw new UPAIllegalArgumentException("Unvalid getEntityActionList method " + p);
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
//                    throw new UPAIllegalArgumentException("Unsupported isEnabledEntityAction method " + p);
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
        return "Plugin{" + getId() + '}';
    }

}
