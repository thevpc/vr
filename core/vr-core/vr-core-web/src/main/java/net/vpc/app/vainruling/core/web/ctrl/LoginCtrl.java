/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.PlatformSession;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.*;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import net.vpc.app.vainruling.core.web.Vr;

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
        return Vr.get().gotoPage("appInfo", "");
    }

    public String doimpersonate() {
        AppUser u = core.impersonate(getModel().getLogin(), getModel().getPassword());
        if (u != null) {
            return Vr.get().gotoPage("welcome", "");
//            return VRApp.getBean(VrMenu.class).gotoPage("todo", "sys-labo-action");
        }
        FacesUtils.addErrorMessage("Login ou mot de passe incorrect");
        getModel().setPassword(null);
        return null;
    }


    public String dologin() {
        VrWebHelper.prepareUserSession();
        UserToken s = CorePlugin.get().getCurrentToken();
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
                    UserSessionInfo u = core.authenticate(finalLogin, getModel().getPassword(),"WebSite",null);
                if (u != null) {
                    FacesContext currentInstance = FacesContext.getCurrentInstance();
                    if(currentInstance!=null) {
                        UserSession currentSession = CorePlugin.get().getCurrentSession();
                        String sessionId = currentSession.getSessionId();
                        SessionStore sessionStore = VrApp.getBean(SessionStoreProvider.class).resolveSessionStore();
                        PlatformSession platformSession = sessionStore.get(sessionId);
                        if(platformSession==null){
                            ExternalContext externalContext = currentInstance.getExternalContext();
                            HttpServletRequest httpRequest = (HttpServletRequest) externalContext.getRequest();
                            String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
                            if (ipAddress == null) {
                                ipAddress = httpRequest.getRemoteAddr();
                            }
                            platformSession=new HttpPlatformSession((HttpSession) externalContext.getSession(false),ipAddress);
                        }
                        currentSession.setPlatformSession(platformSession);
//                        getSession().setPlatformSessionMap(externalContext.getSessionMap());
                        VrApp.getBean(ActiveSessionsCtrl.class).onRefresh();
                        if(s!=null){

                            String lvp = currentSession.getPreConnexionURL();
                            if(lvp!=null){
                                currentSession.setPreConnexionURL(null);
                                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                                try {
                                    context.redirect(context.getRequestContextPath() + lvp);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }
                        return Vr.get().gotoPage("welcome", "");
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
            FacesUtils.addErrorMessage(ex);
            getModel().setPassword(null);
            return null;
        }
    }

    public String dologout() {
        boolean impersonating = CorePlugin.get().getCurrentToken().getRootLogin()!=null;
        core.logout();
        if (impersonating) {
            return Vr.get().gotoPage("welcome", "");
        }
        FacesUtils.invalidateSession();
        return "/index.xhtml?faces-redirect=true";

    }

    public Model getModel() {
        return model;
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
