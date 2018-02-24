package net.vpc.app.vainruling.core.service.stats;

import net.vpc.app.vainruling.core.service.stats.KPIValue;
import net.vpc.app.vainruling.core.service.stats.KPIValueDef;

/**
 * Created by vpc on 8/29/16.
 */
public class DefaultKPIValue implements KPIValue {
    private KPIValueDef def;
    private Object value;

    public DefaultKPIValue(KPIValueDef def, Object value) {
        this.def = def;
        this.value = value;
    }

    @Override
    public KPIValueDef getDefinition() {
        return def;
    }

    @Override
    public String getName() {
        return def.getName();
    }

    @Override
    public <T> T getValue() {
        return (T) value;
    }
}
