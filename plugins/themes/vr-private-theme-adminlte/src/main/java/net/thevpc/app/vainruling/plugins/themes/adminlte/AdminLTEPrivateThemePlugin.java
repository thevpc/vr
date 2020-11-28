package net.thevpc.app.vainruling.plugins.themes.adminlte;

import net.thevpc.app.vainruling.VrPlugin;
import net.thevpc.app.vainruling.core.service.CorePlugin;

@VrPlugin
public class AdminLTEPrivateThemePlugin {
    private void onInstall() {
        CorePlugin core = CorePlugin.get();
    }

    public String getSkin() {
        return "skin-blue";
    }
}
