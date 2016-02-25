/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.core.PluginManagerService;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.ctrl.BasePageCtrl;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author vpc
 */
@UCtrl(
        title = "Informations Systeme", css = "fa-dashboard", url = "modules/appinfo"
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
        getModel().setPluginsCount(VrApp.getBean(PluginManagerService.class).getPlugins().length);
    }

    public static class Model {

        private String appVersion = "1.0.32";
        private String appDate = "2016-02-25";
        private String appBuild = "36";
        private String author = "Taha BEN SALAH (c)";
        private int pluginsCount = 0;

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public String getAppDate() {
            return appDate;
        }

        public void setAppDate(String appDate) {
            this.appDate = appDate;
        }

        public String getAppBuild() {
            return appBuild;
        }

        public void setAppBuild(String appBuild) {
            this.appBuild = appBuild;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getPluginsCount() {
            return pluginsCount;
        }

        public void setPluginsCount(int pluginsCount) {
            this.pluginsCount = pluginsCount;
        }

    }
}
