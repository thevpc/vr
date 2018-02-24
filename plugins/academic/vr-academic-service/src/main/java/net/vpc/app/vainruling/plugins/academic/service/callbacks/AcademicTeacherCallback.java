/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import net.vpc.app.vainruling.core.service.util.VrUPAUtils;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.*;
import net.vpc.upa.Entity;
import net.vpc.upa.callbacks.EntityEvent;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.*;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.types.TypesFactory;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicTeacherCallback {


    @OnPrepare
    public void OnPrepareEntity(EntityEvent event) throws UPAException {
        Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicTeacher.class)) {
            if (!entity.containsField("contactEmail")) {
                entity.addField(
                        new DefaultFieldBuilder().setName("contactEmail")
                                .addModifier(UserFieldModifier.SUMMARY)
                                .setDataType(TypesFactory.STRING)
                                .setProtectionLevel(ProtectionLevel.PROTECTED)
                                .setIndex(3)
                                .setLiveSelectFormula("this.user.contact.email")
                );
            }
            if (!entity.containsField("phone1")) {
                entity.addField(
                        new DefaultFieldBuilder()
                                .setName("contactPhone1")
                                .addModifier(UserFieldModifier.SUMMARY)
                                .setProtectionLevel(ProtectionLevel.PROTECTED)
                                .setDataType(TypesFactory.STRING)
                                .setIndex(4)
                                .setLiveSelectFormula("this.user.contact.phone1")
                );
            }
        }
    }


    private void onChangeAcademicTeacher(net.vpc.upa.Entity entity, Object updatesObject) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        if (updatesObject instanceof AcademicTeacher) {
            p.updateTeacherPeriod(c.getCurrentPeriod().getId(), ((AcademicTeacher) updatesObject).getId(), -1);
        } else if (updatesObject instanceof Document) {
            p.updateTeacherPeriod(c.getCurrentPeriod().getId(), (Integer) entity.getBuilder().objectToId(updatesObject), -1);
        }
    }


    private boolean isEntity(Entity entity, Class entityType) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && entity.getEntityType().equals(entityType);
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();


        if (isEntity(entity, AppUser.class)) {
            AppUser user = (AppUser) event.getPersistedObject();
            AppUserType t = user.getType();
            if (t != null) {
                if ("Teacher".equals(AppUserType.getCodeOrName(t))) {
                    CorePlugin.get().userAddProfile(user.getId(), "Teacher");
                }
            }
        }

        if (isEntity(entity, AcademicTeacher.class)) {
            AcademicTeacher persistedObject = (AcademicTeacher) event.getPersistedObject();
            ap.validateAcademicData_Teacher(persistedObject.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
            onChangeAcademicTeacher(entity, event.getPersistedObject());
        }

        if (entity.getEntityType().equals(AppPeriod.class)) {
            Object updatesObject = event.getPersistedObject();
            for (AcademicTeacher teacher : ap.findTeachers()) {
                if (updatesObject instanceof AppPeriod) {
                    ap.updateTeacherPeriod(((AppPeriod) updatesObject).getId(), teacher.getId(), -1);
                } else if (updatesObject instanceof Document) {
                    ap.updateTeacherPeriod((Integer) entity.getBuilder().objectToId(updatesObject), teacher.getId(), -1);
                }
            }
        }

    }

    @OnPreUpdate
    public void onPreUpdate(UpdateEvent event) {
        Entity entity = event.getEntity();
        if (isEntity(entity, AcademicTeacher.class)) {
            event.storeUpdatedIds();
        }
    }

    @OnUpdate
    public void onUpdate(UpdateEvent event) {
        Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();
        if (isEntity(entity, AcademicTeacher.class)) {
            AppPeriod mainPeriod = CorePlugin.get().getCurrentPeriod();
            int s = ap.findSemesters().size();
            List<Integer> integers = event.loadUpdatedIds();
            for (Integer id : integers) {
                AcademicTeacher t = ap.findTeacher(id);
                ap.validateAcademicData_Teacher(t.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
                onChangeAcademicTeacher(entity, t);
            }
        }
    }

}
