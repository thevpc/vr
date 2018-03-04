package net.vpc.app.vainruling.core.service.cache;

import net.vpc.app.vainruling.core.service.util.EntityMapList;
import net.vpc.common.util.MapList;
import net.vpc.common.util.Utils;
import net.vpc.upa.Action;
import net.vpc.upa.Entity;
import net.vpc.upa.UPA;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vpc on 6/1/16.
 */
public class EntityCache {
    private Entity entity;
    private SoftReference<MapList> list;
    private int navigationDepth = 3;
    private Map<String, Object> properties = new HashMap<>();

    public EntityCache(Entity entity) {
        this.entity = entity;
        this.navigationDepth = entity.getProperties().getInt("cache.navigationDepth", 3);
    }

    public <T> T getProperty(String value, Action<T> evalAction) {
        if (properties.containsKey(value)) {
            return (T) properties.get(value);
        } else {
            T val = evalAction.run();
            properties.put(value, val);
            return val;
        }
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public <K, V> MapList<K, V> getValues() {
        MapList ret = null;
        if (list == null || list.get() == null) {
            ret = Utils.unmodifiableMapList(new EntityMapList(
                    UPA.getPersistenceUnit()
                            .createQueryBuilder(entity.getName())
                            .orderBy(entity.getListOrder())
//                            .setHint(QueryHints.MAX_NAVIGATION_DEPTH, this.navigationDepth)
                            .getResultList(),
                    entity
            ));
            list = new SoftReference<MapList>(
                    ret
            );
        } else {
            ret = list.get();
        }
        return ret;
    }

    public void invalidate() {
        list = null;
        properties.clear();
    }
}
