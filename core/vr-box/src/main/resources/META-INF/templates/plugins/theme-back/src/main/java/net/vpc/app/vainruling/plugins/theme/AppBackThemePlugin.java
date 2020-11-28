package ${{packageName(ProjectGroup)}}.theme;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.plugins.VrPlugin;

@VrPlugin
public class ${{className(ProjectName)}}BAckTheme {
    private void onInstall() {
        CorePlugin core = CorePlugin.get();
    }

    public String getSkin() {
        return "skin-blue";
    }
}
