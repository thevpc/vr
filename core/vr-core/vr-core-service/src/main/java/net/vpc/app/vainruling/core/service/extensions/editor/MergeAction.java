/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.extensions.editor;


import net.vpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.vpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.vpc.upa.AccessMode;

import java.util.List;
import net.vpc.app.vainruling.VrEditorActionInvoke;
import net.vpc.app.vainruling.VrEditorAction;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.upa.Field;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(
        actionStyle = "fa-calculator"
)
public class MergeAction implements VrEditorActionInvoke {

    @Override
    public boolean isEnabled(String actionId, String entityType, AccessMode mode, Object value) {
        if(mode!=AccessMode.READ){
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
