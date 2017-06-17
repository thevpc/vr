/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        menu = "/Admin",
        securityKey = "Custom.Admin.InvalidateCache"
)
public class InvalidateCacheCtrl {

    @OnPageLoad
    public void onInvoke() {
        CorePlugin.get().invalidateCache();
    }

}
