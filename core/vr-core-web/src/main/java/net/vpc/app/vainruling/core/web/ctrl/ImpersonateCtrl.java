/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.web.UCtrl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        title = "Changer Utilisateur",
        url = "modules/admin/impersonate",
        menu = "/Admin",
        securityKey = "Custom.Admin.Impersonate"
)
@ManagedBean
@SessionScoped
public class ImpersonateCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public static class Model {

    }
}
