package net.vpc.app.vainruling.core.service.stats;

/**
 * Created by vpc on 8/29/16.
 */
public interface KPIValue {
    KPIValueDef getDefinition();

    String getName();

    <T> T getValue();
}
