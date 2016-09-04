/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherSemestrialLoad;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityModifier;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPersist;
import net.vpc.upa.config.OnUpdate;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicTeacherCallback {

    protected boolean accept(Entity entity) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && entity.getName().equals("AcademicTeacher");
    }

    protected boolean isEntity(Entity entity, Class entityType) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && entity.getEntityType().equals(entityType);
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();
        if (isEntity(entity, AcademicTeacher.class)) {
            int s = ap.findSemesters().size();
            AcademicTeacher persistedObject = (AcademicTeacher) event.getPersistedObject();
            for (int i = 1; i < s + 1; i++) {
                AcademicTeacherSemestrialLoad load = new AcademicTeacherSemestrialLoad();
                load.setSemester(i);
                load.setWeeksLoad(ap.getSemesterMaxWeeks());
                load.setTeacher(persistedObject);
                load.setPeriod(CorePlugin.get().findAppConfig().getMainPeriod());
                pu.persist(load);
            }
            ap.validateAcademicData_Teacher(persistedObject.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
        } else if (isEntity(entity, AcademicStudent.class)) {
            AcademicStudent persistedObject = (AcademicStudent) event.getPersistedObject();
            ap.validateAcademicData_Student(persistedObject.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
        }
    }

    @OnUpdate
    public void onUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();
        if (isEntity(entity, AcademicTeacher.class)) {
            int s = ap.findSemesters().size();
            AcademicTeacher persistedObject = (AcademicTeacher) event.getUpdatesObject();
            if(persistedObject!=null) {
                for (int i = 1; i < s + 1; i++) {
                    AcademicTeacherSemestrialLoad load = new AcademicTeacherSemestrialLoad();
                    load.setSemester(i);
                    load.setWeeksLoad(ap.getSemesterMaxWeeks());
                    load.setTeacher(persistedObject);
                    load.setPeriod(CorePlugin.get().findAppConfig().getMainPeriod());
                    pu.persist(load);
                }
                ap.validateAcademicData_Teacher(persistedObject.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
            }
        } else if (isEntity(entity, AcademicStudent.class)) {
            AcademicStudent persistedObject = (AcademicStudent) event.getUpdatesObject();
            if(persistedObject!=null) {
                ap.validateAcademicData_Student(persistedObject.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
            }
        }
    }

}
