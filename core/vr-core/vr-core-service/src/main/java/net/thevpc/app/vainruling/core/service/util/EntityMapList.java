package net.thevpc.app.vainruling.core.service.util;

import net.thevpc.common.collections.DefaultKeyValueList;
import net.thevpc.upa.Entity;

import java.util.List;
import java.util.function.Function;

/**
 * Created by vpc on 6/1/16.
 */
public class EntityMapList<K, V> extends DefaultKeyValueList<K, V> {
    private Entity entity;

    public EntityMapList(Entity e) {
        super(new EntityMapper<V, K>(e));
        this.entity = e;
    }

    public EntityMapList(List<V> list, Entity e) {
        super(list, new EntityMapper<V, K>(e));
        this.entity = e;
    }

    private static class EntityMapper<V, K> implements Function<V, K> {
        Entity entity;

        public EntityMapper(Entity entity) {
            this.entity = entity;
        }

        @Override
        public K apply(V value) {
            return (K) entity.getBuilder().objectToId(value);
        }
    }
}
