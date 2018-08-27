/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.callbacks;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.events.PersistEvent;
import net.vpc.upa.events.RemoveEvent;
import net.vpc.upa.events.UpdateEvent;
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
        Entity entity = event.getEntity();
        CacheService.DomainCache service = getDomainCache(pu);
        if (service != null) {
            service.invalidate(entity, event.getPersistedDocument());
        }
    }

    @OnUpdate
    public void onUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        CacheService.DomainCache service = getDomainCache(pu);
        Entity entity = event.getEntity();
        if (service != null) {
            service.invalidate(entity, event.getUpdatesDocument());
        }
    }

    @OnRemove
    public void onRemove(RemoveEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        CacheService.DomainCache service = getDomainCache(pu);
        Entity entity = event.getEntity();
        if (service != null) {
            service.invalidate(entity, event.getFilterExpression());
        }
    }


    private CacheService.DomainCache getDomainCache(PersistenceUnit pu) {
        CacheService trace = null;
        try {
            trace = VrApp.getBean(CacheService.class);
        } catch (Exception e) {
            //
        }
        return trace==null?null:trace.getDomainCache(pu);
    }

}
