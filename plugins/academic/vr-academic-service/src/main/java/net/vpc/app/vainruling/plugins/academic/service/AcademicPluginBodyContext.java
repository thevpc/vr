package net.vpc.app.vainruling.plugins.academic.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.PluginBodyContext;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.cache.CacheService;

class AcademicPluginBodyContext extends PluginBodyContext<AcademicPlugin> {

    public AcademicPluginBodyContext(AcademicPlugin plugin) {
        super(plugin);
    }
}
