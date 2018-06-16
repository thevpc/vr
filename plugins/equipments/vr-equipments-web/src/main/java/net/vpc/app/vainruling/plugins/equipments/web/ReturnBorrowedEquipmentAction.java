/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.obj.*;
import net.vpc.app.vainruling.plugins.equipments.service.EquipmentPlugin;
import net.vpc.app.vainruling.plugins.equipments.service.model.Equipment;
import net.vpc.upa.*;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = Equipment.class,
        actionStyle = "fa-calculator",
        confirm = true
)
public class ReturnBorrowedEquipmentAction implements EntityViewActionInvoke {

    @Override
    public ActionParam[] getParams() {
        return new ActionParam[]{
                new ActionParam("Quantite", ParamType.DOUBLE,1)
        };
    }

    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        if (value == null) {
            return false;
        }
        EquipmentPlugin equipmentPlugin = VrApp.getBean(EquipmentPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityType);
        EntityBuilder builder = entity.getBuilder();
        Equipment eq = (Equipment) builder.getObject(value);

        return eq != null && equipmentPlugin.isBorrowed(eq.getId());
    }

    @Override
    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        double qty=((Number)args[0]).doubleValue();
        EquipmentPlugin equipmentPlugin = VrApp.getBean(EquipmentPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityType);
        EntityBuilder builder = entity.getBuilder();
        Equipment eq = (Equipment) builder.getObject(obj);
        String message = null;
        if (equipmentPlugin.borrowBackEquipment(eq.getId(), null,qty)) {
            message = "Retour réussi";
        }
        return new ActionDialogResult(message, ActionDialogResultPostProcess.RELOAD_CURRENT);
    }
}
