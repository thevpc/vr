/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.Record;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.PersistenceUnitEvent;
import net.vpc.upa.callbacks.UpdateObjectEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPersist;
import net.vpc.upa.config.OnPreUpdateFormula;
import net.vpc.upa.config.OnUpdate;

/**
 * @author vpc
 */
@Callback
public class AcademicPersistenceUnitCallback {

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
        }
    }

    private void onChangeAcademicTeacher(net.vpc.upa.Entity entity, Object updatesObject) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        if (updatesObject instanceof AcademicTeacher) {
            p.updateTeacherPeriod(c.findAppConfig().getMainPeriod().getId(), ((AcademicTeacher) updatesObject).getId());
        } else if (updatesObject instanceof Record) {
            p.updateTeacherPeriod(c.findAppConfig().getMainPeriod().getId(), (Integer) entity.getBuilder().objectToId(updatesObject));
        }
    }
}
