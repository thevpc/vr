/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        url = "modules/admin/impersonate",
        menu = "/Admin/Security",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_ADMIN_IMPERSONATE
)
public class ImpersonateCtrl {

    @OnPageLoad
    private void onInit(){

    }
}
