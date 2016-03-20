/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherSemestrialLoad;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityModifier;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.callbacks.EntityListenerAdapter;
import net.vpc.upa.callbacks.EntityListener;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnPersist;

/**
 *
 * @author vpc
 */
@Callback
public class AcademicTeacherCallback
       {

    protected boolean accept(Entity entity) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && entity.getName().equals("AcademicTeacher");
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (!accept(entity)) {
            return;
        }
        int s = VrApp.getBean(AcademicPlugin.class).findSemesters().size();
        for (int i = 1; i < s + 1; i++) {
            AcademicTeacherSemestrialLoad load = new AcademicTeacherSemestrialLoad();
            load.setSemester(i);
            load.setWeeksLoad(14);
            load.setTeacher((AcademicTeacher) event.getPersistedObject());
            pu.persist(load);
        }
    }

    
}
