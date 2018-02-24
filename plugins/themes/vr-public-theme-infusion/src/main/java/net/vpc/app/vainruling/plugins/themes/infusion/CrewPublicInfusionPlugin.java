package net.vpc.app.vainruling.plugins.themes.infusion;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.plugins.AppPlugin;

@AppPlugin
public class CrewPublicInfusionPlugin {

    private void onInstall() {
        CorePlugin core = CorePlugin.get();
        for (int i = 1; i <= 7; i++) {
            core.findOrCreateDisposition("Main.Row" + i, "Page principale, Ligne " + i, null);
        }
    }
}
