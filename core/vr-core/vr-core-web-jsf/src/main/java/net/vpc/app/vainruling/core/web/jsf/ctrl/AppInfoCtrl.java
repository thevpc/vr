/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import org.springframework.context.annotation.Scope;
import net.vpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        menu = "/Desktop",
//        title = "Informations Systeme", css = "fa-dashboard",
        url = "modules/app-info"
)
@Scope(value = "singleton")
public class AppInfoCtrl extends BasePageCtrl {
//
//    private Model model = new Model();
//
//    public AppInfoCtrl() {
//    }
//
//    public Model getModel() {
//        return model;
//    }
//
//    @PostConstruct
//    public void reloadPage() {
//        reloadPage(null);
//    }
//
//    @OnPageLoad
//    public void reloadPage(String cmd) {
//        CorePlugin core = VrApp.getBean(CorePlugin.class);
//        Plugin[] plugins = core.getPlugins();
//        getModel().setPlugins(plugins);
//        getModel().setPluginsCount(plugins.length);
//        getModel().setVersion(core.getAppVersion());
//    }
//
//    public static class Model {
//
//        private AppVersion version;
//        private Plugin[] plugins;
//        private int pluginsCount = 0;
//
//        public AppVersion getVersion() {
//            return version;
//        }
//
//        public void setVersion(AppVersion version) {
//            this.version = version;
//        }
//
//        public int getPluginsCount() {
//            return pluginsCount;
//        }
//
//        public void setPluginsCount(int pluginsCount) {
//            this.pluginsCount = pluginsCount;
//        }
//
//        public Plugin[] getPlugins() {
//            return plugins;
//        }
//
//        public void setPlugins(Plugin[] plugins) {
//            this.plugins = plugins;
//        }
//    }
}
