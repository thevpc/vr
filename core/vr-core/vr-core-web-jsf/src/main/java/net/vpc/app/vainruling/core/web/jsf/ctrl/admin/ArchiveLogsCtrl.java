/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.admin;

import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrOnPageLoad;
import net.vpc.app.vainruling.core.service.TraceService;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        menu = "/Admin",
        securityKey = CorePluginSecurity.RIGHT_CUSTOM_ADMIN_ARCHIVE_LOGS
)
public class ArchiveLogsCtrl {

    @VrOnPageLoad
    public void onInvoke() {
        TraceService.get().archiveLogs(30);
    }

}
