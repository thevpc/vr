/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.core.service.CorePluginSecurity;
import net.thevpc.app.vainruling.core.web.jsf.Vr;

import java.util.logging.Logger;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        menu = "/Admin",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_ADMIN
)
public class NotifyShutdownServerCtrl {

    private static final Logger log = Logger.getLogger(NotifyShutdownServerCtrl.class.getName());

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @VrOnPageLoad
    public void onInvoke() {
        Vr.get().notifyShutdown();
    }

    public static class Model {
    }
}
