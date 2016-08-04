/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartmentPeriod;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicFormerStudent;
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

import java.util.Calendar;

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

        p.validateAcademicData(c.findAppConfig().getMainPeriod().getId());
    }

    @OnUpdate
    public void onUpdate(UpdateObjectEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicTeacher.class)) {
            onChangeAcademicTeacher(entity, event.getUpdatesObject());
        }
    }

    @OnPrePersist
    public void onPrePersist(PersistEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicFormerStudent.class)) {
            onPreAcademicFormerStudentCreated(entity, event.getPersistedObject());
        }
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        net.vpc.upa.Entity entity = event.getEntity();
        if (entity.getEntityType().equals(AcademicTeacher.class)) {
            onChangeAcademicTeacher(entity, event.getPersistedObject());
        } else if (entity.getEntityType().equals(AppPeriod.class)) {
            onAppPeriodCreated(entity, event.getPersistedObject());
        } else if (entity.getEntityType().equals(AppPeriod.class)) {
            onAcademicFormerStudentCreated(entity, event.getPersistedObject());
        }
    }

    private void onChangeAcademicTeacher(net.vpc.upa.Entity entity, Object updatesObject) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        if (updatesObject instanceof AcademicTeacher) {
            p.updateTeacherPeriod(c.findAppConfig().getMainPeriod().getId(), ((AcademicTeacher) updatesObject).getId(), -1);
        } else if (updatesObject instanceof Record) {
            p.updateTeacherPeriod(c.findAppConfig().getMainPeriod().getId(), (Integer) entity.getBuilder().objectToId(updatesObject), -1);
        }
    }

    private void onAppPeriodCreated(net.vpc.upa.Entity entity, Object updatesObject) {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        for (AcademicTeacher teacher : p.findTeachers()) {
            if (updatesObject instanceof AppPeriod) {
                p.updateTeacherPeriod(((AppPeriod) updatesObject).getId(), teacher.getId(), -1);
            } else if (updatesObject instanceof Record) {
                p.updateTeacherPeriod((Integer) entity.getBuilder().objectToId(updatesObject), teacher.getId(), -1);
            }
        }
    }

    private void onAcademicFormerStudentCreated(net.vpc.upa.Entity entity, Object updatesObject) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);

        if (updatesObject instanceof AcademicFormerStudent) {

            updatesObject = UPA.getPersistenceUnit().getEntity(AcademicFormerStudent.class).getBuilder().objectToRecord(updatesObject);
        }

        if (updatesObject instanceof Record) {
            Record u = (Record) updatesObject;
            AcademicStudent s = u.getObject("student");
            if (s != null) {
                s.setLastClass1(null);
                s.setLastClass2(null);
                s.setLastClass3(null);
                UPA.getPersistenceUnit().merge(s);
            }
        }
    }

    private void onPreAcademicFormerStudentCreated(net.vpc.upa.Entity entity, Object updatesObject) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);

        if (updatesObject instanceof AcademicFormerStudent) {

            updatesObject = entity.getBuilder().objectToRecord(updatesObject);
        }

        if (updatesObject instanceof Record) {
            Record u = (Record) updatesObject;
            AcademicStudent s = u.getObject("student");
            if (s != null) {
                if (u.getObject("lastClass1") == null) {
                    u.setObject("lastClass1", s.getLastClass1());
                }
                if (u.getObject("lastClass2") == null) {
                    u.setObject("lastClass2", s.getLastClass2());
                }
                if (u.getObject("lastClass3") == null) {
                    u.setObject("lastClass3", s.getLastClass3());
                }

                AppPeriod pp = c.findAppConfig().getMainPeriod();
                if (u.getObject("graduationPeriod") == null) {
                    u.setObject("graduationPeriod", pp);
                }
                if (u.getObject("graduationDate") == null) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.MILLISECOND, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.HOUR, 0);
                    cal.set(Calendar.DAY_OF_MONTH, 30);
                    cal.set(Calendar.MONTH, Calendar.JUNE);
                    u.setObject("graduationDate", cal.getTime());
                }

            }
        }

    }
}
