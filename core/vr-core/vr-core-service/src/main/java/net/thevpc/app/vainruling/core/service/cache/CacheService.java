package net.thevpc.app.vainruling.core.service.cache;

import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.common.collections.KeyValueList;
import net.thevpc.upa.Action;
import net.thevpc.upa.Entity;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by vpc on 6/1/16.
 */
@Service
public class CacheService {
    public static CacheService get() {
        return VrApp.getBean(CacheService.class);
    }

    public static class DomainCache {
        private WeakHashMap<String, EntityCache> entityCache = new WeakHashMap<>();
        private Map<String, Object> properties = new HashMap<>();
        public void invalidate() {
            properties.clear();
            entityCache.clear();
        }

        public void invalidate(Entity entity) {
            properties.clear();
            EntityCache c = entityCache.get(entity.getName());
            if (c != null) {
                c.invalidate();
            }
        }
        
        public void invalidate(Entity entity, Object value) {
            properties.clear();
            EntityCache c = entityCache.get(entity.getName());
            if (c != null) {
                c.invalidate();
            }
        }

        public <K, V> KeyValueList<K, V> getList(Class<V> entity) {
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

    public DomainCache getDomainCache() {
        return getDomainCache(UPA.getPersistenceUnit());
    }
    public DomainCache getDomainCache(PersistenceUnit pu) {
        String name = CacheService.class.getName();
        DomainCache cache = pu.getProperties().getObject(name);
        if(cache==null){
            synchronized (pu){
                cache = pu.getProperties().getObject(name);
                if(cache==null) {
                    cache=new DomainCache();
                    pu.getProperties().setObject(name,cache);
                }
            }
        }
        return cache;
    }

    public void invalidate() {
        getDomainCache().invalidate();
    }

    public void invalidate(Entity entity, Object value) {
        getDomainCache().invalidate(entity,value);
    }
    
    public void invalidate(Entity entity) {
        getDomainCache().invalidate(entity);
    }

    public <K, V> KeyValueList<K, V> getList(Class<V> entity) {
        return getDomainCache().getList(entity);
    }

    public EntityCache get(Class entity) {
        return getDomainCache().get(entity);
    }

    public EntityCache get(Entity entity) {
        return getDomainCache().get(entity);
    }

    public <T> T getProperty(String value, Action<T> evalAction) {
        return getDomainCache().getProperty(value,evalAction);
    }

    public Map<String, Object> getProperties() {
        return getDomainCache().getProperties();
    }
}
