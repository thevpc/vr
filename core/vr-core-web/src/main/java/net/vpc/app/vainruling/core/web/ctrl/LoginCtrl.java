/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.agent.ActiveSessionsTracker;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import net.vpc.app.vainruling.core.web.util.VRWebHelper;
import net.vpc.common.jsf.FacesUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * @author vpc
 */
@UCtrl(
        title = "Login",
        url = "login"
)
@ManagedBean
@SessionScoped
public class LoginCtrl {

    private Model model = new Model();

    @Autowired
    private CorePlugin core;

    @PostConstruct
    private void prepare() {
    }

    public void onPoll() {
        VrApp.getBean(CorePlugin.class).onPoll();
    }

    public String gotoLogin() {
        return "/login";
    }

    public String gotoAppInfo() {
        return VrApp.getBean(VrMenuManager.class).gotoPage("appInfo", "");
    }

    public String doimpersonate() {
        AppUser u = core.impersonate(getModel().getLogin(), getModel().getPassword());
        if (u != null) {
            return VrApp.getBean(VrMenuManager.class).gotoPage("welcome", "");
//            return VRApp.getBean(VrMenu.class).gotoPage("todo", "sys-labo-action");
        }
        FacesUtils.addErrorMessage("Login ou mot de passe incorrect");
        getModel().setPassword(null);
        return null;
    }


    public String dologin() {
        VRWebHelper.prepareUserSession();
        if (VrApp.getBean(AppGlobalCtrl.class).isShutdown() && !"admin".equals(getModel().getLogin())) {
            FacesUtils.addErrorMessage("Impossible de logger. Serveur indisponible momentannément. Redémarrage en cours.");
            return null;
        }
        AppUser u = core.login(getModel().getLogin(), getModel().getPassword());
        if (u != null) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            getSession().setPlatformSession(externalContext.getSession(false));
            getSession().setPlatformSessionMap(externalContext.getSessionMap());
            VrApp.getBean(ActiveSessionsCtrl.class).onRefresh();
            return VrApp.getBean(VrMenuManager.class).gotoPage("welcome", "");
//            return VRApp.getBean(VrMenu.class).gotoPage("todo", "sys-labo-action");
        }
        FacesUtils.addErrorMessage("Login ou mot de passe incorrect");
        getModel().setPassword(null);
        return null;
    }

    public String dologout() {
        boolean impersonating = getSession().isImpersonating();
        core.logout();
        if (impersonating) {
            return VrApp.getBean(VrMenuManager.class).gotoPage("welcome", "");
        }
        FacesUtils.invalidateSession();
        return "/index.xhtml?faces-redirect=true";

    }

    public Model getModel() {
        return model;
    }

    public UserSession getSession() {
        return VrApp.getBean(UserSession.class);
    }

    public static class Model {

        private String login;
        private String password;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }
}
