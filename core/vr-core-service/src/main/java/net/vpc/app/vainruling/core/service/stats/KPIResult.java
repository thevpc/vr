package net.vpc.app.vainruling.core.service.stats;

import java.util.List;

/**
 * Created by vpc on 8/29/16.
 */
public interface KPIResult {
    List<KPIValueDef> getDefinitions();

    List<KPIResultRow> getRows();

    public KPIResultRow getRow(KPIGroup group);
}
