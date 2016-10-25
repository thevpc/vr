/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.VrUPAUtils;
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
import net.vpc.upa.config.OnPreUpdate;
import net.vpc.upa.config.OnUpdate;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.IdEnumerationExpression;
import net.vpc.upa.expressions.Var;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicTeacherCallback {


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
            AppPeriod mainPeriod = CorePlugin.get().findAppConfig().getMainPeriod();
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
