package ${{vrProjectGroup}}.${{packageName(vrModuleName)}}.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.logging.Logger;

/**
 * Plugin (Module) ${{className(vrModuleName)}} for application
 * 
 * @author ${{vrConfigAuthor}}
 */
@VrPlugin
public class ${{className(vrProjectName)}}${{className(vrModuleName)}}Plugin {
    private static final Logger log = Logger.getLogger(${{className(vrProjectName)}}${{className(vrModuleName)}}Plugin.class.getName());

    @Autowired
    private CorePlugin core;

    /**
     * module installation process.
     * Called on application startup when the plugin is first bundled (as dependency) 
     * or when a new version is bundled
     */
    @Install
    private void onInstall() {
        //configure CMS properties
        core.setAppProperty("System.App.Description", null, "${{vrProjectName}}");
        core.setAppProperty("System.App.Keywords", null, "${{vrProjectName}}");
        core.setAppProperty("System.App.Title.Major.Main", null, "${{vrProjectName}}");
        core.setAppProperty("System.App.Title.Major.Secondary", null, "app");
        core.setAppProperty("System.App.Title.Minor.Main", null, "${{vrProjectName}}");
        core.setAppProperty("System.App.Title.Minor.Secondary", null, "app");
        core.findOrCreateAppDepartment("D", "D", "Department");
        core.findOrCreateArticleDisposition("Services", "Services", "Services");
    }

    /**
     * module initialization process.
     * Called on each application startup (after @Install phase methods if any)
     */
    @Start
    private void onStart() {
    }

}
