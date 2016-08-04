/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.AppVersion;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        title = "Informations Systeme", css = "fa-dashboard", url = "modules/app-info"
)
@ManagedBean
@Scope(value = "session")
public class AppInfoCtrl extends BasePageCtrl {

    private Model model = new Model();

    public AppInfoCtrl() {
    }

    public Model getModel() {
        return model;
    }

    @PostConstruct
    public void reloadPage() {
        reloadPage(null);
    }

    @OnPageLoad
    public void reloadPage(String cmd) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        getModel().setPluginsCount(core.getPlugins().length);
        getModel().setVersion(core.getAppVersion());
    }

    public static class Model {

        private AppVersion version;
        private int pluginsCount = 0;

        public AppVersion getVersion() {
            return version;
        }

        public void setVersion(AppVersion version) {
            this.version = version;
        }

        public int getPluginsCount() {
            return pluginsCount;
        }

        public void setPluginsCount(int pluginsCount) {
            this.pluginsCount = pluginsCount;
        }

    }
}
