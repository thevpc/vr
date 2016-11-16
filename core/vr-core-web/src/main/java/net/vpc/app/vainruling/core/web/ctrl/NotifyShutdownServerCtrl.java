/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.Vr;

import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        menu = "/Admin",
        securityKey = "Custom.Admin"
)
public class NotifyShutdownServerCtrl {

    private static final Logger log = Logger.getLogger(NotifyShutdownServerCtrl.class.getName());

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    public void onInvoke() {
        Vr.get().notifyShutdown();
    }

    public static class Model {
    }
}
