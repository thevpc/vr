package net.vpc.app.vainruling.core.service.stats;


/**
 * Created by vpc on 8/29/16.
 */
public interface KPIEvaluator<V> {
    void start();

    void visit(V assignment);

    KPIValue[] evaluate();
}
