package net.vpc.app.vainruling.core.service.util;

import net.vpc.common.util.Converter;
import net.vpc.common.util.DefaultMapList;
import net.vpc.common.util.MapList;
import net.vpc.upa.Entity;

import java.util.List;

/**
 * Created by vpc on 6/1/16.
 */
public class EntityMapList<K, V> extends DefaultMapList<K, V> {
    private Entity entity;

    public EntityMapList(Entity e) {
        super(new EntityMapper<K, V>(e));
        this.entity = e;
    }

    public EntityMapList(List<V> list, Entity e) {
        super(list, new EntityMapper<K, V>(e));
        this.entity = e;
    }

    private static class EntityMapper<K, V> implements Converter<K, V> {
        Entity entity;

        public EntityMapper(Entity entity) {
            this.entity = entity;
        }

        @Override
        public K convert(V value) {
            return (K) entity.getBuilder().objectToId(value);
        }
    }
}
