/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.extensions.editor;


import net.vpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.vpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.vpc.upa.AccessMode;
import net.vpc.upa.UPA;

import java.util.List;
import net.vpc.app.vainruling.VrEditorActionInvoke;
import net.vpc.app.vainruling.VrEditorAction;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AppConfig",
        actionStyle = "fa-calculator"
)
public class UpdateFormulasAction implements VrEditorActionInvoke {

    @Override
    public boolean isEnabled(String actionId, String entityType, AccessMode mode, Object value) {
        return true;//value != null;
    }

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        UPA.getPersistenceUnit().updateAllFormulas();
        return new ActionDialogResult("Mise à jour réussie", ActionDialogResultPostProcess.RELOAD_ALL);
    }
}
