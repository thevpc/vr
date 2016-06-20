/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

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
 * @author vpc
 */
@Callback
public class AppUserTeacherCallback {

    protected boolean accept(Entity entity) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && entity.getEntityType().equals(AppUser.class);
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (!accept(entity)) {
            return;
        }
        AppUser user = (AppUser) event.getPersistedObject();
        AppUserType t = user.getType();
        if (t != null) {
            if (t.getName().equals("Teacher")) {
                VrApp.getBean(CorePlugin.class).userAddProfile(user.getId(), "Teacher");
            } else if (t.getName().equals("Student")) {
                VrApp.getBean(CorePlugin.class).userAddProfile(user.getId(), "Student");
            }
        }

    }

}
