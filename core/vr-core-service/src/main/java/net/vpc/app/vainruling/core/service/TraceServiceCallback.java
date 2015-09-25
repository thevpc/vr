/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.api.TraceService;
import net.vpc.app.vainruling.api.VrApp;
import java.util.List;
import java.util.logging.Level;
import net.vpc.app.vainruling.api.model.AppTrace;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityModifier;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.Record;
import net.vpc.upa.callbacks.EntityListenerAdapter;
import net.vpc.upa.callbacks.EntityListener;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.Var;
import net.vpc.upa.impl.uql.expression.KeyEnumerationExpression;

/**
 *
 * @author vpc
 */
@Callback
public class TraceServiceCallback
        extends EntityListenerAdapter
        implements EntityListener {

    protected boolean accept(Entity entity) {
        return !entity.getModifiers().contains(EntityModifier.SYSTEM)
                && !entity.getEntityType().equals(AppTrace.class);
    }

    @Override
    public void onPrePersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (!accept(entity)) {
            return;
        }
    }

    @Override
    public void onPersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (!accept(entity)) {
            return;
        }
        TraceService trace = getTraceService();
        if (trace != null) {
            trace.inserted(event.getPersistedObject(), entity.getParent().getPath(), Level.FINE);
        }

    }

    private TraceService getTraceService() {
        TraceService trace = null;
        try {
            trace = VrApp.getBean(TraceService.class);
        } catch (Exception e) {
            //
        }
        return trace;
    }

    @Override
    public void onPreUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (!accept(entity)) {
            return;
        }
        Expression expr = event.getFilterExpression();
        if (expr instanceof KeyEnumerationExpression) {
            KeyEnumerationExpression k = (KeyEnumerationExpression) expr;
            expr = new KeyEnumerationExpression(k.getKeys(), new Var(entity.getName()));
        }
        List old = pu.createQueryBuilder(entity.getName()).setExpression(expr).getEntityList();
        old.size();//force load!
        event.getContext().setObject("updated_objects", old);
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        if (!accept(entity)) {
            return;
        }
        List old = event.getContext().getObject("updated_objects");
        if (old.size() == 1) {
            TraceService trace = getTraceService();
            if (trace != null) {
                trace.updated(event.getUpdatesObject(), old.get(0), entity.getParent().getPath(), Level.FINE);
            }
        } else {
            //??
        }
    }

}
