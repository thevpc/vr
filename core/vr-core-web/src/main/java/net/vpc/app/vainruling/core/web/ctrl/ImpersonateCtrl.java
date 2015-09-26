/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import net.vpc.app.vainruling.api.web.UCtrl;

/**
 *
 * @author vpc
 */
@UCtrl(
        title = "Switch User",
        url = "modules/admin/impersonate",
        menu = "/",
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
