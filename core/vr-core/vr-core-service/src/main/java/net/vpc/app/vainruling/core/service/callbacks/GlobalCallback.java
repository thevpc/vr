/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.callbacks;

import net.vpc.upa.callbacks.EntityEvent;
import net.vpc.upa.callbacks.PersistenceGroupEvent;
import net.vpc.upa.callbacks.PersistenceUnitEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnCreate;
import net.vpc.upa.exceptions.UPAException;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class GlobalCallback {

    @OnCreate
    public void onCreatePersistenceGroup(PersistenceGroupEvent event) throws UPAException {
        //never called
        event.getPersistenceGroup().setI18n(I18nHolder.INSTANCE);
    }

    @OnCreate
    public void onCreatePersistenceUnit(PersistenceUnitEvent event) throws UPAException {
        //never called
        event.getPersistenceGroup().setI18n(I18nHolder.INSTANCE);
    }

    @OnCreate
    public void onCreateEntity(EntityEvent event) throws UPAException {
        //never called
        event.getPersistenceUnit().getPersistenceGroup().setI18n(I18nHolder.INSTANCE);
    }

}
