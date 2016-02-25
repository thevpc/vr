/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.VRWebHelper;
import net.vpc.app.vainruling.api.web.VrMenuManager;
import net.vpc.app.vainruling.api.LoginService;
import net.vpc.common.jsf.FacesUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
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
    private CorePlugin coreService;
    @Autowired
    private LoginService loginService;

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
        AppUser u = loginService.impersonate(getModel().getLogin(), getModel().getPassword());
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
        AppUser u = loginService.login(getModel().getLogin(), getModel().getPassword());
        if (u != null) {
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
        loginService.logout();
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
