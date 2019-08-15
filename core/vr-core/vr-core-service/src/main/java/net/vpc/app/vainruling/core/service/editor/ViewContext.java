package net.vpc.app.vainruling.core.service.editor;

import net.vpc.upa.Action;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vpc on 6/3/16.
 */
public class ViewContext {
    private final Map<String, Object> cache = new HashMap<>();
    private final Map<String, Object> properties = new HashMap<>();

    public Map<String, Object> getProperties() {
        return properties;
    }

    public <T> T getCacheItem(String v, Action<T> action) {
        if (cache.containsKey(v)) {
            return (T) cache.get(v);
        }
        T value = action.run();
        cache.put(v, value);

        return value;
    }
}
