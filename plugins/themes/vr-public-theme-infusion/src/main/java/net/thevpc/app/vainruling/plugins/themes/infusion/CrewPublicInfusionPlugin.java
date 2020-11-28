package net.thevpc.app.vainruling.plugins.themes.infusion;

import net.thevpc.app.vainruling.VrPlugin;
import net.thevpc.app.vainruling.core.service.CorePlugin;

@VrPlugin
public class CrewPublicInfusionPlugin {

    private void onInstall() {
        CorePlugin core = CorePlugin.get();
        for (int i = 1; i <= 7; i++) {
            core.findOrCreateArticleDisposition("Main.Row" + i, "Page principale, Ligne " + i, null);
        }
    }
}
