/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.core.service.model.AppDepartmentPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicLoadConversionTable;
import net.vpc.upa.*;
import net.vpc.upa.callbacks.EntityEvent;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.PersistenceUnitEvent;
import net.vpc.upa.callbacks.UpdateObjectEvent;
import net.vpc.upa.config.*;
import net.vpc.upa.config.Callback;
import net.vpc.upa.types.BooleanType;
import net.vpc.upa.types.ManyToOneType;

/**
 * @author vpc
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
                tracking.addField("enableLoadEditing", null, true, BooleanType.BOOLEAN);
            }
        }
        if (entityName.equals(AppPeriod.class.getSimpleName())) {
            if (entity.findField("loadConversionTable") == null) {
                Section tracking = entity.getSection("Load", MissingStrategy.CREATE);
                tracking.addField("loadConversionTable", null, null,
                        new ManyToOneType(AcademicLoadConversionTable.class,true)
                );
            }
        }
    }

    @OnPreUpdateFormula
    public void onPreUpdateFormulas(PersistenceUnitEvent event) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);

        p.validateAcademicData(c.findAppConfig().getMainPeriod().getId());
    }

    @OnUpdate
    public void onUpdate(UpdateObjectEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicTeacher.class)) {
            onChangeAcademicTeacher(entity, event.getUpdatesObject());
        }
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicTeacher.class)) {
            onChangeAcademicTeacher(entity, event.getPersistedObject());
        }else if (entity.getEntityType().equals(AppPeriod.class)) {
            onAppPeriodCreated(entity, event.getPersistedObject());
        }
    }

    private void onChangeAcademicTeacher(net.vpc.upa.Entity entity, Object updatesObject) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        if (updatesObject instanceof AcademicTeacher) {
            p.updateTeacherPeriod(c.findAppConfig().getMainPeriod().getId(), ((AcademicTeacher) updatesObject).getId(),-1);
        } else if (updatesObject instanceof Record) {
            p.updateTeacherPeriod(c.findAppConfig().getMainPeriod().getId(), (Integer) entity.getBuilder().objectToId(updatesObject),-1);
        }
    }

    private void onAppPeriodCreated(net.vpc.upa.Entity entity, Object updatesObject) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        for (AcademicTeacher teacher : p.findTeachers()) {
            if (updatesObject instanceof AppPeriod) {
                p.updateTeacherPeriod(((AppPeriod) updatesObject).getId(), teacher.getId(),-1);
            } else if (updatesObject instanceof Record) {
                p.updateTeacherPeriod((Integer) entity.getBuilder().objectToId(updatesObject),teacher.getId(),-1);
            }
        }
    }
}
