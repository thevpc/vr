/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.web.HttpPlatformSession;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
//        title = "Login",
        url = "login"
)
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
        VrWebHelper.prepareUserSession();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        String login = StringUtils.trim(getModel().getLogin());
        String domain = null;
        if (login.contains("@")) {
            int i = login.indexOf('@');
            domain = login.substring(i + 1);
            login = login.substring(0, i);
        }
        if (StringUtils.isEmpty(domain)) {
            domain = "main";
        }
        String finalLogin = login;
        Action<String> welcome = new Action<String>() {
            @Override
            public String run() {
                if (VrApp.getBean(AppGlobalCtrl.class).isShutdown() && !"admin".equals(finalLogin)) {
                    FacesUtils.addErrorMessage("Impossible de logger. Serveur indisponible momentannément. Redémarrage en cours.");
                    return null;
                }
                    AppUser u = core.login(finalLogin, getModel().getPassword(),"WebSite",null);
                if (u != null) {
                    FacesContext currentInstance = FacesContext.getCurrentInstance();
                    if(currentInstance!=null) {
                        ExternalContext externalContext = currentInstance.getExternalContext();
                        getSession().setPlatformSession(new HttpPlatformSession((HttpSession) externalContext.getSession(false)));
//                        getSession().setPlatformSessionMap(externalContext.getSessionMap());
                        VrApp.getBean(ActiveSessionsCtrl.class).onRefresh();
                        return VrApp.getBean(VrMenuManager.class).gotoPage("welcome", "");
                    }
//            return VRApp.getBean(VrMenu.class).gotoPage("todo", "sys-labo-action");
                }
                FacesUtils.addErrorMessage("Login ou mot de passe incorrect");
                getModel().setPassword(null);
                return null;
            }
        };
        try {
            if (pu.getName().equals(domain)) {
                return welcome.run();
            } else {
                return pu.getPersistenceGroup().getPersistenceUnit(domain).invokePrivileged(welcome);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtils.addErrorMessage("Login ou mot de passe incorrect");
            getModel().setPassword(null);
            return null;
        }
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
        return UserSession.get();
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
