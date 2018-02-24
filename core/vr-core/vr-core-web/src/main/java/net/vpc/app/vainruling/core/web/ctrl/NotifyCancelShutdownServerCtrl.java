/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.Vr;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        menu = "/Admin",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_ADMIN
)
public class NotifyCancelShutdownServerCtrl {

    @OnPageLoad
    public void onInvoke() {
        Vr.get().cancelShutdown();
    }
}
