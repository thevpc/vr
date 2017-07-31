package net.vpc.app.vainruling.core.service.cache;

import net.vpc.common.util.MapList;
import net.vpc.upa.Action;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by vpc on 6/1/16.
 */
@Service
public class CacheService {
    protected static class CacheData {
        private WeakHashMap<String, EntityCache> entityCache = new WeakHashMap<>();
        private Map<String, Object> properties = new HashMap<>();
        public void invalidate() {
            properties.clear();
            entityCache.clear();
        }

        public void invalidate(Entity entity, Object value) {
            properties.clear();
            EntityCache c = entityCache.get(entity.getName());
            if (c != null) {
                c.invalidate();
            }
        }

        public <K, V> MapList<K, V> getList(Class<V> entity) {
            return get(entity).getValues();
        }

        public EntityCache get(Class entity) {
            return get(UPA.getPersistenceUnit().getEntity(entity));
        }

        public EntityCache get(Entity entity) {
            EntityCache c = entityCache.get(entity.getName());
            if (c == null) {
                c = new EntityCache(entity);
                entityCache.put(entity.getName(), c);
            }
            return c;
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
    }

    public CacheData getCacheData() {
        final PersistenceUnit pu = UPA.getPersistenceUnit();
        String name = CacheService.class.getName();
        CacheData cache = pu.getProperties().getObject(name);
        if(cache==null){
            synchronized (pu){
                if(cache==null) {
                    cache=new CacheData();
                    pu.getProperties().setObject(name,cache);
                }
            }
        }
        return cache;
    }

    public void invalidate() {
        getCacheData().invalidate();
    }

    public void invalidate(Entity entity, Object value) {
        getCacheData().invalidate(entity,value);
    }

    public <K, V> MapList<K, V> getList(Class<V> entity) {
        return getCacheData().getList(entity);
    }

    public EntityCache get(Class entity) {
        return getCacheData().get(entity);
    }

    public EntityCache get(Entity entity) {
        return getCacheData().get(entity);
    }

    public <T> T getProperty(String value, Action<T> evalAction) {
        return getCacheData().getProperty(value,evalAction);
    }

    public Map<String, Object> getProperties() {
        return getCacheData().getProperties();
    }
}
