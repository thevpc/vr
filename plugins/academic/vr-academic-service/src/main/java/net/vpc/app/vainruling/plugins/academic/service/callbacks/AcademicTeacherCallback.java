/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.VrUPAUtils;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.*;
import net.vpc.upa.Entity;
import net.vpc.upa.callbacks.EntityEvent;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.config.*;
import net.vpc.upa.config.Callback;
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
        if(entity.getEntityType().equals(AcademicTeacher.class)){
            if(!entity.containsField("contactEmail")) {
                entity.addField(
                        new DefaultFieldBuilder().setName("contactEmail")
                                .addModifier(UserFieldModifier.SUMMARY)
                                .setDataType(TypesFactory.STRING)
                                .setAccessLevel(AccessLevel.PROTECTED)
                                .setIndex(3)
                                .setLiveSelectFormula("this.contact.email")
                );
            }
            if(!entity.containsField("phone1")) {
                entity.addField(
                        new DefaultFieldBuilder()
                                .setName("contactPhone1")
                                .addModifier(UserFieldModifier.SUMMARY)
                                .setAccessLevel(AccessLevel.PROTECTED)
                                .setDataType(TypesFactory.STRING)
                                .setIndex(4)
                                .setLiveSelectFormula("this.contact.phone1")
                );
            }
        }
    }

    protected boolean isEntity(Entity entity, Class entityType) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && entity.getEntityType().equals(entityType);
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();
        if (isEntity(entity, AcademicTeacher.class)) {
            AcademicTeacher persistedObject = (AcademicTeacher) event.getPersistedObject();
            ap.validateAcademicData_Teacher(persistedObject.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
        } else if (isEntity(entity, AcademicStudent.class)) {
            AcademicStudent persistedObject = (AcademicStudent) event.getPersistedObject();
            ap.validateAcademicData_Student(persistedObject.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
        }
    }

    @OnPreUpdate
    public void onPreUpdate(UpdateEvent event) {
        Entity entity = event.getEntity();
        if (isEntity(entity, AcademicTeacher.class) || isEntity(entity, AcademicStudent.class)) {
            VrUPAUtils.storeUpdatedIds(event);
        }
    }
    @OnUpdate
    public void onUpdate(UpdateEvent event) {
        Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();
        if (isEntity(entity, AcademicTeacher.class)) {
            AppPeriod mainPeriod = CorePlugin.get().getCurrentPeriod();
            int s = ap.findSemesters().size();
            List<Integer> integers = VrUPAUtils.loadUpdatedIds(event);
            for (Integer id : integers) {
                AcademicTeacher t = ap.findTeacher(id);
                ap.validateAcademicData_Teacher(t.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
            }
        } else if (isEntity(entity, AcademicStudent.class)) {
            List<Integer> integers = VrUPAUtils.loadUpdatedIds(event);
            for (Integer id : integers) {
                ap.validateAcademicData_Student(id, CorePlugin.get().findPeriodOrMain(-1).getId());
            }
        }
    }

}
