package net.thevpc.app.vainruling.core.service;

import net.thevpc.app.vainruling.core.service.cache.CacheService;

class CorePluginBodyContext extends PluginBodyContext<CorePlugin> {
    private CacheService cacheService;
    private TraceService trace;

    public CorePluginBodyContext(CorePlugin corePlugin, CacheService cacheService, TraceService trace) {
        super(corePlugin);
        this.cacheService = cacheService;
        this.trace = trace;
    }

    public TraceService getTrace() {
        return trace;
    }

    public CacheService getCacheService() {
        return cacheService;
    }

    public CorePlugin getCorePlugin() {
        return getPlugin();
    }

}
