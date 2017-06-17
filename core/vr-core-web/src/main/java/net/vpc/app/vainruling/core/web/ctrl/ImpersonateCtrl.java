/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.web.VrController;
import org.springframework.context.annotation.Scope;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        url = "modules/admin/impersonate",
        menu = "/Admin/Security",
        securityKey = "Custom.Admin.Impersonate"
)
@Scope(value = "singleton")
public class ImpersonateCtrl {

}
