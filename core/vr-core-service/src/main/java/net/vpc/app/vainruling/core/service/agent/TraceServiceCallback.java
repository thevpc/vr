/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.agent;

import java.util.List;
import java.util.logging.Level;

import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.callbacks.PersistEvent;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.Config;
import net.vpc.upa.config.OnPersist;
import net.vpc.upa.config.OnPrePersist;
import net.vpc.upa.config.OnPreUpdate;
import net.vpc.upa.config.OnUpdate;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.Var;
import net.vpc.upa.expressions.IdEnumerationExpression;

/**
 *
 * @author vpc
 */
@Callback(
        //config = @Config(persistenceUnit = "main")
)
public class TraceServiceCallback {

    @OnPrePersist
    public void onPrePersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        Entity entity = event.getEntity();
        TraceService trace = getTraceService();
        if (trace == null || !trace.accept(entity)) {
            return;
        }
    }

    @OnPersist
    public void onPersist(PersistEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        if(!UPA.getPersistenceUnit().getName().equals(pu.getName())){
            return;
        }
        Entity entity = event.getEntity();
        TraceService trace = getTraceService();
        if (trace == null || !trace.accept(entity)) {
            return;
        }
        trace.inserted(entity.getName(),event.getPersistedObject(), entity.getParent().getPath(), Level.FINE);

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

    @OnPreUpdate
    public void onPreUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        if(!UPA.getPersistenceUnit().getName().equals(pu.getName())){
            event.getContext().setObject("silenced", true);
            return;
        }
        TraceService trace = getTraceService();
        Entity entity = event.getEntity();
        if (trace == null || trace.isSilenced() || !trace.accept(entity)) {
            event.getContext().setObject("silenced", true);
            return;
        }
        Expression expr = event.getFilterExpression();
        if (expr instanceof IdEnumerationExpression) {
            IdEnumerationExpression k = (IdEnumerationExpression) expr;
            expr = new IdEnumerationExpression(k.getIds(), new Var(entity.getName()));
        }
        List old = pu.createQueryBuilder(entity.getName()).setExpression(expr).getRecordList();
        old.size();//force load!
        event.getContext().setObject("updated_objects", old);
    }

    @OnUpdate
    public void onUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        if(!UPA.getPersistenceUnit().getName().equals(pu.getName())){
            return;
        }
        Entity entity = event.getEntity();
        TraceService trace = getTraceService();
        if (trace == null  || trace.isSilenced() || !trace.accept(entity) || Boolean.TRUE.equals(event.getContext().getObject("silenced"))) {
            return;
        }
        List old = event.getContext().getObject("updated_objects");
        if (old.size() == 1) {
            if (trace != null) {
                trace.updated(entity.getName(),event.getUpdatesRecord(), old.get(0), entity.getParent().getPath(), Level.FINE);
            }
        } else {
            //??
        }
    }

}
