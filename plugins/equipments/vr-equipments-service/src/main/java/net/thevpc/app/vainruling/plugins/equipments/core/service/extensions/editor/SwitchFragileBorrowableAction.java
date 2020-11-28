/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.core.service.extensions.editor;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.VrEditorActionInvoke;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.thevpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.thevpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.thevpc.upa.*;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.common.util.Convert;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "Equipment",
        actionIcon = "cubes"
)
public class SwitchFragileBorrowableAction implements VrEditorActionInvoke {

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return value != null;//value != null;
    }

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        EquipmentPlugin equipmentPlugin = VrApp.getBean(EquipmentPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            Equipment e = equipmentPlugin.findEquipment(Convert.toInt(selectedIdStrings.get(i)));
            e.setFragile(!e.isFragile());
            pu.merge(e);
        }
        String message = "Opération réussie";
        return new ActionDialogResult(message, ActionDialogResultPostProcess.RELOAD_ALL);
    }
}
