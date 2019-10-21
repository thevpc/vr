package ${{packageName(ProjectGroup)}}.${{packageName(ModuleName)}}.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.VrPlugin;
import net.vpc.app.vainruling.VrInstall;
import net.vpc.app.vainruling.VrStart;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.logging.Logger;

/**
 * Plugin (Module) ${{className(ModuleName)}} for application
 *
 * @author ${{ConfigAuthor}}
 */
@VrPlugin
public class ${{className(ProjectName)}}${{className(ModuleName)}}Plugin {
    private static final Logger log = Logger.getLogger(${{className(ProjectName)}}${{className(ModuleName)}}Plugin.class.getName());

    @Autowired
    private CorePlugin core;

    /**
     * module installation process.
     * Called on application startup when the plugin is first bundled (as dependency) 
     * or when a new version is bundled
     */
    @VrInstall
    private void onInstall() {
        //configure CMS properties
        core.setAppProperty("System.App.Description", null, "${{ProjectName}}");
        core.setAppProperty("System.App.Keywords", null, "${{ProjectName}}");
        core.setAppProperty("System.App.Title.Major.Main", null, "${{ProjectName}}");
        core.setAppProperty("System.App.Title.Major.Secondary", null, "app");
        core.setAppProperty("System.App.Title.Minor.Main", null, "${{ProjectName}}");
        core.setAppProperty("System.App.Title.Minor.Secondary", null, "app");
        core.findOrCreateAppDepartment("D", "D", "Department");
        core.findOrCreateArticleDisposition("Services", "Services", "Services");
    }

    /**
     * module initialization process.
     * Called on each application startup (after @Install phase methods if any)
     */
    @VrStart
    private void onStart() {
    }

}
