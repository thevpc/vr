/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.extensions.editor;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.core.service.TraceService;

import net.thevpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.app.vainruling.VrEditorActionInvoke;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AppConfig",
        actionIcon = "newspaper"
)
public class PurgeTraceAction implements VrEditorActionInvoke {

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return true;//value != null;
    }

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        TraceService.get().archiveLogs(30);
        return new ActionDialogResult("Archivage réussi", ActionDialogResultPostProcess.VOID);
    }
}
