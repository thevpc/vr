/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        title = "Invalider cache", css = "fa-dashboard",
        menu = "/Admin",
        securityKey = "Custom.Admin.InvalidateCache"
)
public class InvalidateCacheCtrl {

    private static final Logger log = Logger.getLogger(InvalidateCacheCtrl.class.getName());

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    public void onInvoke() {
        CorePlugin.get().invalidateCache();
    }

    public static class Model {
    }
}
