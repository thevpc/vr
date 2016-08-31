package net.vpc.app.vainruling.core.service.stats;

import net.vpc.app.vainruling.core.service.stats.KPIValueDef;

/**
 * Created by vpc on 8/29/16.
 */
public class DefaultKPIValueDef implements KPIValueDef {
    private String name;

    public DefaultKPIValueDef(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
