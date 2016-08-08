package net.vpc.app.vainruling.core.service.agent;

import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.RemoveEvent;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 5/17/16.
 */
@Callback(
        //config = @Config(persistenceUnit = "main")
)
public class CorePluginCacheInvalidatorCallback {

    @OnPrePersist
    public void onPrePersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
    }

    @OnPreRemove
    public void onPreRemove(RemoveEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
    }

    @OnPreUpdate
    public void onPreUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
    }



}
