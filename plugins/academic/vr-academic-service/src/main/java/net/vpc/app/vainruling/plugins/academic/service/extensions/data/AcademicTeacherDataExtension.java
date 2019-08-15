/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.extensions.data;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.AppUserType;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.upa.*;
import net.vpc.upa.Entity;
import net.vpc.upa.events.PersistEvent;
import net.vpc.upa.events.UpdateEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.*;

import java.util.List;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacherPeriod;
import net.vpc.upa.PersistenceUnit;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AcademicTeacherDataExtension {

    @OnPersist
    public void onPersist(PersistEvent event) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        Entity entity = event.getEntity();
        AcademicPlugin ap = AcademicPlugin.get();

        if (isEntity(entity, AppUser.class)) {
            AppUser user = (AppUser) event.getPersistedObject();
            AppUserType t = user.getType();
            if (t != null) {
                if ("Teacher".equals(AppUserType.getCodeOrName(t))) {
                    CorePlugin.get().addUserProfile(user.getId(), "Teacher");
                }
            }
        }

        if (isEntity(entity, AcademicTeacher.class)) {
            AcademicTeacher persistedObject = (AcademicTeacher) event.getPersistedObject();
            ap.validateAcademicData_Teacher(persistedObject.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
            ap.updateTeacherPeriod(c.getCurrentPeriod().getId(), (Integer) entity.getBuilder().objectToId(event.getPersistedObject()), -1);
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

        if (isEntity(entity, AcademicTeacherPeriod.class)) {
            AcademicTeacherPeriod persistedObject = (AcademicTeacherPeriod) event.getPersistedObject();
            // what to do?
        }
    }

    @OnPreUpdate
    public void onPreUpdate(UpdateEvent event) {
        Entity entity = event.getEntity();
        if (isEntity(entity, AcademicTeacher.class)) {
            event.storeUpdatedIds();
        } else if (isEntity(entity, AcademicTeacherPeriod.class)) {
            event.storeUpdatedIds();
        }
    }

    @OnUpdate
    public void onUpdate(UpdateEvent event) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = event.getEntity();
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin ap = AcademicPlugin.get();
        if (isEntity(entity, AcademicTeacher.class)) {
            int s = ap.findSemesters().size();
            List<Integer> integers = event.loadUpdatedIds();
            for (Integer id : integers) {
                AcademicTeacher t = ap.findTeacher(id);
                ap.validateAcademicData_Teacher(t.getId(), CorePlugin.get().findPeriodOrMain(-1).getId());
                ap.updateTeacherPeriod(c.getCurrentPeriod().getId(), (Integer) entity.getBuilder().objectToId(t), -1);
            }
        } else if (isEntity(entity, AcademicTeacherPeriod.class)) {
            AppPeriod mainPeriod = CorePlugin.get().getCurrentPeriod();
            int s = ap.findSemesters().size();
            List<Integer> integers = event.loadUpdatedIds();
            for (Integer id : integers) {
                AcademicTeacherPeriod t = (AcademicTeacherPeriod) c.find("AcademicTeacherPeriod", id);
                if (t != null && t.getTeacher() != null && t.getPeriod() != null && mainPeriod != null && t.getPeriod().getId() == mainPeriod.getId()) {
                    AcademicTeacher te = ap.findTeacher(t.getTeacher().getId());//reload teacher!!
                    if (t.isEnabled()) {
                        te.setDegree(t.getDegree());
//                        te.setDepartment(t.getDepartment());
                        te.setSituation(t.getSituation());
                        pu.merge(te);
                        te.getUser().setDepartment(t.getDepartment());
                        pu.merge(te.getUser());
                    } else {
                        te.setDegree(t.getDegree());
//                        te.setDepartment(t.getDepartment());
                        te.setSituation(null);
                        pu.merge(te);
                        te.getUser().setDepartment(t.getDepartment());
                        pu.merge(te.getUser());
                    }
                }
            }
        }
    }

//    private void onChangeAcademicTeacher(net.vpc.upa.Entity entity, Object updatesObject) {
//        CorePlugin c = VrApp.getBean(CorePlugin.class);
//        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
//        p.updateTeacherPeriod(c.getCurrentPeriod().getId(), (Integer) entity.getBuilder().objectToId(updatesObject), -1);
//    }
    private boolean isEntity(Entity entity, Class entityType) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && entity.getEntityType().equals(entityType);
    }

}
