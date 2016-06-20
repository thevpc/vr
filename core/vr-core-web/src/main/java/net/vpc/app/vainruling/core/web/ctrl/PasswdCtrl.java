/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.springframework.context.annotation.Scope;

import javax.faces.bean.ManagedBean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vpc
 */
@UCtrl(
        title = "Changer Mot de passe", css = "fa-dashboard", url = "modules/admin/passwd",
        securityKey = "Custom.Admin.Passwd"
)
@ManagedBean
@Scope(value = "session")
public class PasswdCtrl {

    private static final Logger log = Logger.getLogger(PasswdCtrl.class.getName());

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public void changePassword() {
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
                FacesUtils.addErrorMessage(null, "Les mots de passe ne coincident pas");
                return;
            }
            UPA.getContext().invokePrivileged(new VoidAction() {
                                                  @Override
                                                  public void run() {
                                                      try {
                                                          t.passwd(t.getActualLogin(), getModel().getOldPassword(), getModel().getPassword1());
                                                          FacesUtils.addInfoMessage(null, "Mot de passe modifié");
                                                      } catch (Exception ex) {
                                                          log.log(Level.SEVERE, "Error", ex);
                                                          FacesUtils.addErrorMessage(null, ex.getMessage());
                                                      }
                                                  }
                                              }
            );
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(null, ex.getMessage());
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
