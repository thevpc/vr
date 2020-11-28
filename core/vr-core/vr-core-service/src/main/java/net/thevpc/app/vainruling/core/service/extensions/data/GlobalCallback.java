/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.extensions.data;

import net.thevpc.upa.events.EntityEvent;
import net.thevpc.upa.events.PersistenceGroupEvent;
import net.thevpc.upa.events.PersistenceUnitEvent;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.OnCreate;
import net.thevpc.upa.exceptions.UPAException;

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
