/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        url = "modules/config/passwd",
        menu = "/Desktop",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_ADMIN_PASSWD
)
public class PasswdCtrl {

    private static final Logger log = Logger.getLogger(PasswdCtrl.class.getName());

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public void onChangePassword() {
        try {
            final CorePlugin t = VrApp.getBean(CorePlugin.class);
            String s1 = getModel().getPassword1();
            String s2 = getModel().getPassword2();
            if (s1 == null) {
                s1 = "";
            }
            if (s2 == null) {
                s2 = "";
            }
            if (!s1.equals(s2)) {
                FacesUtils.addErrorMessage("Les mots de passe ne coincident pas");
                return;
            }
            String oldPassword = getModel().getOldPassword();
            String newPassword = getModel().getPassword1();
            getModel().setOldPassword("");
            getModel().setPassword1("");
            getModel().setPassword2("");
            final String actualLogin = t.getActualLogin();
            UPA.getContext().invokePrivileged(new VoidAction() {
                                                  @Override
                                                  public void run() {
                                                      try {
                                                          t.passwd(actualLogin, oldPassword, newPassword);
                                                          FacesUtils.addInfoMessage("Mot de passe modifié");
                                                      } catch (Exception ex) {
                                                          log.log(Level.SEVERE, "Error", ex);
                                                          FacesUtils.addErrorMessage(ex);
                                                      }
                                                  }
                                              }
            );
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public static class Model {

        private String oldPassword;
        private String password1;
        private String password2;

        public String getPassword1() {
            return password1;
        }

        public void setPassword1(String password1) {
            this.password1 = password1;
        }

        public String getPassword2() {
            return password2;
        }

        public void setPassword2(String password2) {
            this.password2 = password2;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

    }
}
