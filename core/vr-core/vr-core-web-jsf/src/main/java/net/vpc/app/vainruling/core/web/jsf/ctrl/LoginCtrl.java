/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.PlatformSession;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.*;
import net.vpc.app.vainruling.core.web.HttpPlatformSession;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import net.vpc.app.vainruling.core.service.pages.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        //        title = "Login",
        url = "login",
        acceptAnonymous = true
)
@Controller
public class LoginCtrl {

    private Model model = new Model();

    @Autowired
    private CorePlugin core;

//    @PostConstruct
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
        String[] domainAndLogin = core.resolveLogin(getModel().getLogin());
        String domain = domainAndLogin[0];
        String login = domainAndLogin[1];
        Action<String> welcome = new Action<String>() {
            @Override
            public String run() {
                if (VrApp.getBean(AppGlobalCtrl.class).isShutdown() && !"admin".equals(login)) {
                    FacesUtils.addErrorMessage("Impossible de logger. Serveur indisponible momentannément. Redémarrage en cours.");
                    return null;
                }
                UserSessionInfo u = core.authenticate(login, getModel().getPassword(), "WebSite", null);
                if (u != null) {
                    FacesContext currentInstance = FacesContext.getCurrentInstance();
                    if (currentInstance != null) {
                        UserSession currentSession = CorePlugin.get().getCurrentSession();
                        String sessionId = currentSession.getSessionId();
                        SessionStore sessionStore = VrApp.getBean(SessionStoreProvider.class).resolveSessionStore();
                        PlatformSession platformSession = sessionStore.get(sessionId);
                        if (platformSession == null) {
                            ExternalContext externalContext = currentInstance.getExternalContext();
                            HttpServletRequest httpRequest = (HttpServletRequest) externalContext.getRequest();
                            String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
                            if (ipAddress == null) {
                                ipAddress = httpRequest.getRemoteAddr();
                            }
                            platformSession = new HttpPlatformSession((HttpSession) externalContext.getSession(false), ipAddress);
                        }
                        currentSession.setPlatformSession(platformSession);
//                        getSession().setPlatformSessionMap(externalContext.getSessionMap());
                        VrApp.getBean(ActiveSessionsCtrl.class).onRefresh();
                        if (s != null) {

                            String lvp = currentSession.getPreConnexionURL();
                            if (lvp != null) {
                                currentSession.setPreConnexionURL(null);
                                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                                try {
                                    if (lvp.startsWith("http://") || lvp.startsWith("https://")) {
                                        java.util.logging.Logger.getLogger(LoginCtrl.class.getName()).info("### [Success Link] Redirect to " + lvp);
                                        context.redirect(lvp);
                                    } else {
                                        java.util.logging.Logger.getLogger(LoginCtrl.class.getName()).info("### [Success Link] Redirect to " + context.getRequestContextPath() + lvp);
                                        context.redirect(context.getRequestContextPath() + lvp);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }
                        return Vr.get().gotoPage("welcome", "");
                    }
//            return VRApp.getBean(VrMenu.class).gotoPage("todo", "sys-labo-action");
                } else {
                    UserSession currentSession = CorePlugin.get().getCurrentSession();
                    if (currentSession == null) {
                        //ignore...
                    } else {
                        String lvp = currentSession.getInvalidConnexionURL();
                        if (lvp != null && (lvp.startsWith("http://") || lvp.startsWith("https://"))) {
                            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                            try {
                                java.util.logging.Logger.getLogger(LoginCtrl.class.getName()).info("### [Failed Link] Redirect to " + lvp);
                                context.redirect(lvp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }

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
        boolean impersonating = CorePlugin.get().getCurrentToken().getRootLogin() != null;
        core.logout();
        if (impersonating) {
            return Vr.get().gotoPage("welcome", "");
        }
        try {
            FacesUtils.invalidateSession();
        }catch (Exception ex){
            //ignore...
        }
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
