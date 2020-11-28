/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.extensions.editor;


import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.app.vainruling.VrEditorActionInvoke;
import net.thevpc.upa.Field;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.UPA;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(
        actionIcon = "object-group"
)
public class MergeAction implements VrEditorActionInvoke {

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        if(mode!=VrAccessMode.READ){
            return false;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        List<Field> f = pu.getEntity(entityType).getIdFields();
        if(f.size()!=1){
            return false;
        }
        return true;
    }

    @Override
    public ActionDialogResult invoke(String actionId, String entityName, Object obj, List<String> selectedIdStrings, Object[] args) {
        if(selectedIdStrings.size()<=1){
            return new ActionDialogResult("Selectionner les élements", ActionDialogResultPostProcess.VOID);
        }
        CorePlugin.get().mergeDocuments(entityName, selectedIdStrings.get(0), selectedIdStrings.subList(1, selectedIdStrings.size()));
        return new ActionDialogResult("Fusion réussi", ActionDialogResultPostProcess.RELOAD_ALL);
    }
}
