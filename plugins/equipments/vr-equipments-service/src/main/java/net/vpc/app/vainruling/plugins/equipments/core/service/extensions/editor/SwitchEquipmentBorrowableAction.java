/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.service.extensions.editor;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.editor.EntityAction;
import net.vpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.vpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.vpc.app.vainruling.core.service.editor.EntityViewActionInvoke;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.vpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.vpc.upa.*;

import java.util.List;
import net.vpc.common.util.Convert;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = Equipment.class,
        actionStyle = "fa-calculator"
)
public class SwitchEquipmentBorrowableAction implements EntityViewActionInvoke {

    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return value != null;//value != null;
    }

    @Override
    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        EquipmentPlugin equipmentPlugin = VrApp.getBean(EquipmentPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (int i = 0; i < selectedIdStrings.size(); i++) {
            Equipment e = equipmentPlugin.findEquipment(Convert.toInt(selectedIdStrings.get(i)));
            e.setBorrowable(!e.isBorrowable());
            pu.merge(e);
        }
        String message = "Opération réussie";
        return new ActionDialogResult(message, ActionDialogResultPostProcess.RELOAD_ALL);
    }
}
