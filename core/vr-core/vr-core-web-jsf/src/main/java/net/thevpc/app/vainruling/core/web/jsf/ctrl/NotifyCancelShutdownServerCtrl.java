/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.core.service.CorePluginSecurity;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        menu = "/Admin",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_ADMIN
)
public class NotifyCancelShutdownServerCtrl {

    @VrOnPageLoad
    public void onInvoke() {
        Vr.get().cancelShutdown();
    }
}
