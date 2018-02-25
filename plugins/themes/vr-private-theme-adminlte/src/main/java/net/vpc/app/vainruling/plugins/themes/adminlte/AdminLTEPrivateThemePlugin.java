package net.vpc.app.vainruling.plugins.themes.adminlte;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;

@VrPlugin
public class AdminLTEPrivateThemePlugin {
    private void onInstall() {
        CorePlugin core = CorePlugin.get();
    }

    public String getSkin() {
        return "skin-blue";
    }
}
