/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.extensions.data;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.model.AppUserType;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.thevpc.upa.*;
import net.thevpc.upa.Entity;
import net.thevpc.upa.events.PersistEvent;
import net.thevpc.upa.events.UpdateEvent;
import net.thevpc.upa.events.UpdateObjectEvent;
import net.thevpc.upa.config.*;
import net.thevpc.upa.config.Callback;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicStudentDataExtension {

    @OnPersist
    public void onPersist(PersistEvent event) {
        Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();

        if (isEntity(entity, AppUser.class)) {
            AppUser user = (AppUser) event.getPersistedObject();
            AppUserType t = user.getType();
            if (t != null) {
                if ("Student".equals(AppUserType.getCodeOrName(t))) {
                    CorePlugin.get().addUserProfile(user.getId(), "Student");
                }
            }
        }

        if (isEntity(entity, AcademicStudent.class)) {
            AcademicStudent persistedObject = (AcademicStudent) event.getPersistedObject();
            ap.validateAcademicData_Student(persistedObject.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
        }
    }

    @OnPreUpdate
    public void onPreUpdate(UpdateEvent event) {
        Entity entity = event.getEntity();
        if (isEntity(entity, AcademicStudent.class)) {
            event.storeUpdatedIds();
        }
    }

    @OnUpdate
    public void onUpdate(UpdateObjectEvent event) {
        net.thevpc.upa.Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();
        if (entity.getEntityType().equals(AcademicStudent.class)) {
            int mainPeriod = CorePlugin.get().findPeriodOrMain(-1).getId();
            for (Integer id : event.<Integer>loadUpdatedIds()) {
                AcademicStudent student = ap.findStudent(id);
                if (student != null) {
                    onChangeAcademicStudent(entity, student);
                    ap.validateAcademicData_Student(id, mainPeriod);
                }
            }
        }
    }

    private boolean isEntity(Entity entity, Class entityType) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && entity.getEntityType().equals(entityType);
    }

    private void onChangeAcademicStudent(net.thevpc.upa.Entity entity, Object updatesObject) {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        int id = -1;
        if (updatesObject instanceof AcademicStudent) {
            id = ((AcademicStudent) updatesObject).getId();
        } else if (updatesObject instanceof Document) {
            id = (Integer) entity.getBuilder().objectToId(updatesObject);
        }
        if (id > 0) {
            p.moveToFormerStudent(id, true);
        }
    }

}
