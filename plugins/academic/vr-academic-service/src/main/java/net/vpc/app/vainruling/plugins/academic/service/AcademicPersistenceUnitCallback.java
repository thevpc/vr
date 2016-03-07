/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.api.VrApp;
import net.vpc.upa.callbacks.PersistenceUnitEvent;
import net.vpc.upa.callbacks.PersistenceUnitListenerAdapter;
import net.vpc.upa.config.Callback;

/**
 *
 * @author vpc
 */
@Callback
public class AcademicPersistenceUnitCallback extends PersistenceUnitListenerAdapter
//        implements PersistenceUnitListener 
{

    @Override
    public void onPreUpdateFormulas(PersistenceUnitEvent event) {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        p.validateAcademicData();
    }

    @Override
    public void onUpdateFormulas(PersistenceUnitEvent event) {
//        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
//        p.validateAcademicData();
    }

}
