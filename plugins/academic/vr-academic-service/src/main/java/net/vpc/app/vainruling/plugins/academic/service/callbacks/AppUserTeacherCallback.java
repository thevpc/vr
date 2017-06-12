/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityModifier;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPersist;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AppUserTeacherCallback {

    protected boolean accept(Entity entity) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && entity.getEntityType().equals(AppUser.class);
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        Entity entity = event.getEntity();
        if (!accept(entity)) {
            return;
        }
        AppUser user = (AppUser) event.getPersistedObject();
        AppUserType t = user.getType();
        if (t != null) {
            if ("Teacher".equals(AppUserType.getCodeOrName(t))) {
                VrApp.getBean(CorePlugin.class).userAddProfile(user.getId(), "Teacher");
            } else if ("Student".equals(AppUserType.getCodeOrName(t))) {
                VrApp.getBean(CorePlugin.class).userAddProfile(user.getId(), "Student");
            }
        }

    }

}
