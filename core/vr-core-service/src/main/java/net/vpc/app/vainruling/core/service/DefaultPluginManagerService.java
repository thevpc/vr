/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.api.core.Plugin;
import java.util.Arrays;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.core.PluginManagerService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 *
 * @author vpc
 */
@Service(value = "pluginManagerService")
@DependsOn(value = {"i18n", "vrApp"})
public class DefaultPluginManagerService implements PluginManagerService {

    private Plugin[] plugins;

    public Plugin[] getPlugins() {
        if (plugins == null) {
            String[] pp = VrApp.getContext().getBeanNamesForAnnotation(AppPlugin.class);
            Plugin[] h = new Plugin[pp.length];
            for (int i = 0; i < h.length; i++) {
                Plugin h1 = new Plugin(pp[i], VrApp.getContext().getBean(pp[i]));
                h[i] = h1;
            }
            Arrays.sort(h);
            plugins = h;
        }
        return plugins;
    }

//    public ActionInfo[] getEntityActionList(Class entityType, Object obj) {
//        List<ActionInfo> all = new ArrayList<ActionInfo>();
//        for (Plugin g : getPlugins()) {
//            try {
//                ActionInfo[] list = g.getEntityActionList(entityType, obj);
//                if (list != null) {
//                    all.addAll(Arrays.asList(list));
//                }
//            } catch (UnsupportedOperationException ee) {
//                //
//            }
//        }
//        return all.toArray(new ActionInfo[all.size()]);
//    }

//    public <T> T invokeEntityAction(Class entityType, String actionName, Object obj, Object[] args) {
//        for (Plugin g : getPlugins()) {
//            try {
//                return g.invokeEntityAction(entityType, actionName, obj, args);
//            } catch (UnsupportedOperationException ee) {
//                //
//            }
//        }
//        return null;
//    }
//
//    public boolean isEnabledEntityAction(Class entityType, String actionName, Object obj) {
//        for (Plugin g : getPlugins()) {
//            try {
//                boolean f = g.isEnabledEntityAction(entityType, actionName, obj);
//                if (!f) {
//                    return f;
//                }
//            } catch (UnsupportedOperationException ee) {
//                //
//            }
//        }
//        boolean ok = false;
//        for (ActionInfo a : getEntityActionList(entityType, obj)) {
//            if (a.getId().equals(actionName)) {
//                if (!a.isEnabled(obj)) {
//                    return false;
//                } else {
//                    ok = true;
//                }
//            }
//        }
//        return ok;
//    }
}
