/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.cache;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.RemoveEvent;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPersist;
import net.vpc.upa.config.OnRemove;
import net.vpc.upa.config.OnUpdate;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback(
        //config = @Config(persistenceUnit = "main")
)
public class CacheServiceCallback {

    @OnPersist
    public void onPersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        if (!UPA.getPersistenceUnit().getName().equals(pu.getName())) {
            return;
        }
        Entity entity = event.getEntity();
        CacheService service = getEntityCacheService();
        if (service != null) {
            service.invalidate(entity, event.getPersistedRecord());
        }
    }

    @OnUpdate
    public void onUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        if (!UPA.getPersistenceUnit().getName().equals(pu.getName())) {
            event.getContext().setObject("silenced", true);
            return;
        }
        CacheService service = getEntityCacheService();
        Entity entity = event.getEntity();
        if (service != null) {
            service.invalidate(entity, event.getUpdatesRecord());
        }
    }

    @OnRemove
    public void onRemove(RemoveEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        if (!UPA.getPersistenceUnit().getName().equals(pu.getName())) {
            event.getContext().setObject("silenced", true);
            return;
        }
        CacheService service = getEntityCacheService();
        Entity entity = event.getEntity();
        if (service != null) {
            service.invalidate(entity, event.getFilterExpression());
        }
    }


    private CacheService getEntityCacheService() {
        CacheService trace = null;
        try {
            trace = VrApp.getBean(CacheService.class);
        } catch (Exception e) {
            //
        }
        return trace;
    }

}
