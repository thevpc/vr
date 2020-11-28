/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.extensions.data;

import net.thevpc.app.vainruling.core.service.TraceService;
import net.thevpc.upa.Entity;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;
import net.thevpc.upa.events.PersistEvent;
import net.thevpc.upa.events.UpdateEvent;
import net.thevpc.upa.config.*;
import net.thevpc.upa.expressions.Expression;
import net.thevpc.upa.expressions.IdEnumerationExpression;
import net.thevpc.upa.expressions.Var;

import java.util.List;
import java.util.logging.Level;

/**
 * @author taha.bensalah@gmail.com
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
        if (!UPA.getPersistenceUnit().getName().equals(pu.getName())) {
            return;
        }
        Entity entity = event.getEntity();
        TraceService trace = getTraceService();
        if (trace == null || !trace.accept(entity)) {
            return;
        }
        trace.inserted(entity.getName(), event.getPersistedObject(), entity.getParent().getPath(), Level.FINE);

    }

    private TraceService getTraceService() {
        TraceService trace = null;
        try {
            trace = TraceService.get();
        } catch (Exception e) {
            //
        }
        return trace;
    }

    @OnPreUpdate
    public void onPreUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        if (!UPA.getPersistenceUnit().getName().equals(pu.getName())) {
            event.getContext().setObject("silenced", true);
            return;
        }
        TraceService trace = getTraceService();
        Entity entity = event.getEntity();
        if (trace == null || TraceService.isSilenced() || !trace.accept(entity)) {
            event.getContext().setObject("silenced", true);
            return;
        }
        Expression expr = event.getFilterExpression();
        if (expr instanceof IdEnumerationExpression) {
            IdEnumerationExpression k = (IdEnumerationExpression) expr;
            expr = new IdEnumerationExpression(k.getIds(), new Var("this"));
        }
        List old = pu.createQueryBuilder(entity.getName()).byExpression(expr).getDocumentList();
        old.size();//force load!
        event.getContext().setObject("updated_objects", old);
    }

    @OnUpdate
    public void onUpdate(UpdateEvent event) {
        PersistenceUnit pu = event.getPersistenceUnit();
        if (!UPA.getPersistenceUnit().getName().equals(pu.getName())) {
            return;
        }
        Entity entity = event.getEntity();
        TraceService trace = getTraceService();
        if (trace == null || TraceService.isSilenced() || !trace.accept(entity) || Boolean.TRUE.equals(event.getContext().getObject("silenced"))) {
            return;
        }
        List old = event.getContext().getObject("updated_objects");
        if (old.size() == 1) {
            if (trace != null) {
                trace.updated(entity.getName(), event.getUpdatesDocument(), old.get(0), entity.getParent().getPath(), Level.FINE);
            }
        } else {
            //??
        }
    }

}
