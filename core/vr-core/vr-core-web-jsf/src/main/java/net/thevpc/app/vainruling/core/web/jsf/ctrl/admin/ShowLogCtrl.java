/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.admin;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.core.service.CorePluginSecurity;
import net.thevpc.app.vainruling.VrPage;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import org.springframework.context.annotation.Scope;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Admin", css = "fa-dashboard", ctrl = "")},
        url = "modules/admin/show-log",
        menu = "/Admin",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_ADMIN_SHOW_LOGS
)
@Scope("singleton")
public class ShowLogCtrl {


    @VrOnPageLoad
    public void refresh() {

    }

    
    public String getServerLog() {
        return CorePlugin.get().getServerLog();
    }


}
