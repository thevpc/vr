package net.vpc.app.vainruling.core.service.util;

import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.callbacks.UpdateEvent;
import net.vpc.upa.expressions.Expression;
import net.vpc.upa.expressions.IdEnumerationExpression;
import net.vpc.upa.expressions.Var;

import java.util.List;

/**
 * Created by vpc on 9/5/16.
 */
public class VrUPAUtils {
    public static void storeUpdatedIds(UpdateEvent event){
        Expression expr = event.getFilterExpression();
        Entity entity = event.getEntity();
        if (expr instanceof IdEnumerationExpression) {
            IdEnumerationExpression k = (IdEnumerationExpression) expr;
            expr = new IdEnumerationExpression(k.getIds(), new Var(entity.getName()));
        }
        PersistenceUnit pu = event.getPersistenceUnit();
        List old = pu.createQueryBuilder(entity.getName()).byExpression(expr).getIdList();
        old.size();//force load!
        event.getContext().setObject("updated_ids_"+entity.getName(),old);
    }
    public static <T> List<T> loadUpdatedIds(UpdateEvent event){
        List object = (List) event.getContext().getObject("updated_ids_" + event.getEntity().getName());
        if(object==null){
            throw new IllegalArgumentException("Not Loaded");
        }
        return object;
    }
}
