/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.admin.actions;

import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppTrace;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.common.jsf.FacesUtils;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AppTrace.class,
        actionLabel = "Arch", actionStyle = "fa-calculator",
        dialog = false
)
public class PurgeTraceAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        return true;//value != null;
    }

    @Override
    public void invoke(Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        TraceService.get().archiveLogs(30);
        FacesUtils.addInfoMessage("Archivage réussi");
    }
}
