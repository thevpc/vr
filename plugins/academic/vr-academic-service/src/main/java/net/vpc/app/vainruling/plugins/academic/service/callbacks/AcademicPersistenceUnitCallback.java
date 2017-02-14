/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartmentPeriod;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicLoadConversionTable;
import net.vpc.upa.*;
import net.vpc.upa.callbacks.EntityEvent;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.PersistenceUnitEvent;
import net.vpc.upa.callbacks.UpdateObjectEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.*;
import net.vpc.upa.types.BooleanType;
import net.vpc.upa.types.ManyToOneType;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicPersistenceUnitCallback {

    @OnPreInitialize
    public void onPreInitEntity(EntityEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        String entityName = entity.getName();
        if (entityName.equals(AppDepartmentPeriod.class.getSimpleName())) {
            if (entity.findField("enableLoadEditing") == null) {
                Section tracking = entity.getSection("Load", MissingStrategy.CREATE);
                tracking.addField("enableLoadEditing", FlagSets.of(UserFieldModifier.SUMMARY), true, BooleanType.BOOLEAN);
            }
        }
        if (entityName.equals(AppPeriod.class.getSimpleName())) {
            if (entity.findField("loadConversionTable") == null) {
                Section tracking = entity.getSection("Load", MissingStrategy.CREATE);
                tracking.addField("loadConversionTable", FlagSets.of(UserFieldModifier.SUMMARY), null,
                        new ManyToOneType(AcademicLoadConversionTable.class, true)
                );
            }
        }
    }

    @OnPreUpdateFormula
    public void onPreUpdateFormulas(PersistenceUnitEvent event) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);

        p.validateAcademicData(c.getCurrentPeriod().getId());
    }

    @OnUpdate
    public void onUpdate(UpdateObjectEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicTeacher.class)) {
            onChangeAcademicTeacher(entity, event.getUpdatesObject());
        }else if (entity.getEntityType().equals(AcademicStudent.class)) {
            onChangeAcademicStudent(entity, event.getUpdatesObject());
        }
    }

    @OnPrePersist
    public void onPrePersist(PersistEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicTeacher.class)) {
            onChangeAcademicTeacher(entity, event.getPersistedObject());
        } else if (entity.getEntityType().equals(AppPeriod.class)) {
            onAppPeriodCreated(entity, event.getPersistedObject());
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
    private void onChangeAcademicStudent(net.vpc.upa.Entity entity, Object updatesObject) {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        int id=-1;
        if (updatesObject instanceof AcademicStudent) {
            id = ((AcademicStudent) updatesObject).getId();
        } else if (updatesObject instanceof Document) {
            id=(Integer) entity.getBuilder().objectToId(updatesObject);
        }
        if(id>0){
            p.moveToFormerStudent(id,true);
        }
    }

    private void onAppPeriodCreated(net.vpc.upa.Entity entity, Object updatesObject) {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        for (AcademicTeacher teacher : p.findTeachers()) {
            if (updatesObject instanceof AppPeriod) {
                p.updateTeacherPeriod(((AppPeriod) updatesObject).getId(), teacher.getId(), -1);
            } else if (updatesObject instanceof Document) {
                p.updateTeacherPeriod((Integer) entity.getBuilder().objectToId(updatesObject), teacher.getId(), -1);
            }
        }
    }


}
