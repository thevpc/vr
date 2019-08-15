/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.extensions.data;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.events.EntityEvent;
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
        invalidateCache(event, event.getPersistedDocument());
    }

    @OnUpdate
    public void onUpdate(UpdateEvent event) {
        invalidateCache(event, event.getUpdatesDocument());
    }

    @OnRemove
    public void onRemove(RemoveEvent event) {
        invalidateCache(event, event.getFilterExpression());
    }


    private CacheService.DomainCache getDomainCache(PersistenceUnit pu) {
        CacheService trace = null;
        try {
            trace = VrApp.getBean(CacheService.class);
        } catch (Exception e) {
            //
        }
        return trace == null ? null : trace.getDomainCache(pu);
    }

    private void invalidateCache(EntityEvent event, Object d) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        CacheService.DomainCache service = getDomainCache(pu);
        if (service != null) {
            service.invalidate(entity, d);
        }
        if (
                entity.getClass().equals(AppProfile.class)
                        || entity.getClass().equals(AppUser.class)
                ) {
            CorePlugin.get().invalidateUserProfileMap();
        }
    }

}
