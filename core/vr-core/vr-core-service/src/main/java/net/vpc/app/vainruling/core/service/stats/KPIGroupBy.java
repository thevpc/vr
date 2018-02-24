package net.vpc.app.vainruling.core.service.stats;

import java.util.List;

/**
 * Created by vpc on 8/29/16.
 */
public interface KPIGroupBy<V> {
    List<KPIGroup> createGroups(V value);
}
