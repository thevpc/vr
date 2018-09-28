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
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.upa.*;
import net.vpc.upa.Entity;
import net.vpc.upa.events.EntityEvent;
import net.vpc.upa.events.PersistEvent;
import net.vpc.upa.events.UpdateEvent;
import net.vpc.upa.events.UpdateObjectEvent;
import net.vpc.upa.config.*;
import net.vpc.upa.config.Callback;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.types.DataTypeFactory;


/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicStudentCallback {


    @OnPrepare
    public void onPrepareEntity(EntityEvent event) throws UPAException {
        Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicStudent.class)) {
            if (!entity.containsField("contactEmail")) {
                entity.addField(
                        new DefaultFieldBuilder()
                                .setName("contactEmail")
                                .addModifier(UserFieldModifier.SUMMARY)
                                .setReadProtectionLevel(ProtectionLevel.PROTECTED)
                                .setDataType(DataTypeFactory.STRING)
                                .setPosition(3)
                                .setLiveSelectFormula("this.user.email")
                );
            }
            if (!entity.containsField("phone1")) {
                entity.addField(
                        new DefaultFieldBuilder()
                                .setName("contactPhone1")
                                .addModifier(UserFieldModifier.SUMMARY)
                                .setReadProtectionLevel(ProtectionLevel.PROTECTED)
                                .setDataType(DataTypeFactory.STRING)
                                .setPosition(4)
                                .setLiveSelectFormula("this.user.phone1")
                );
            }
        }
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();

        if (isEntity(entity, AppUser.class)) {
            AppUser user = (AppUser) event.getPersistedObject();
            AppUserType t = user.getType();
            if (t != null) {
                if ("Student".equals(AppUserType.getCodeOrName(t))) {
                    CorePlugin.get().userAddProfile(user.getId(), "Student");
                }
            }
        }

        if (isEntity(entity, AcademicStudent.class)) {
            AcademicStudent persistedObject = (AcademicStudent) event.getPersistedObject();
            ap.validateAcademicData_Student(persistedObject.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
        }
    }

    private void onChangeAcademicStudent(net.vpc.upa.Entity entity, Object updatesObject) {
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

    @OnPreUpdate
    public void onPreUpdate(UpdateEvent event) {
        Entity entity = event.getEntity();
        if (isEntity(entity, AcademicStudent.class)) {
            event.storeUpdatedIds();
        }
    }

    @OnUpdate
    public void onUpdate(UpdateObjectEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();
        if (entity.getEntityType().equals(AcademicStudent.class)) {
            int mainPeriod = CorePlugin.get().findPeriodOrMain(-1).getId();
            for (Integer id : event.<Integer>loadUpdatedIds()) {
                AcademicStudent student = ap.findStudent(id);
                if(student!=null){
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

}
