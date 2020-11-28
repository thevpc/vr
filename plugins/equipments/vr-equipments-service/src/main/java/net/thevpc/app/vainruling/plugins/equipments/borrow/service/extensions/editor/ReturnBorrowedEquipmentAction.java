/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.borrow.service.extensions.editor;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.VrEditorActionInvoke;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.thevpc.app.vainruling.core.service.editor.ActionParam;
import net.thevpc.app.vainruling.core.service.editor.ParamType;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.thevpc.upa.*;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.app.vainruling.plugins.equipments.borrow.service.EquipmentBorrowService;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "Equipment",
        actionIcon = "cubes",
        confirm = true
)
public class ReturnBorrowedEquipmentAction implements VrEditorActionInvoke {

    @Override
    public ActionParam[] getParams() {
        return new ActionParam[]{
            new ActionParam("Quantite", ParamType.DOUBLE, 1)
        };
    }

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        if (value == null) {
            return false;
        }
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityType);
        EntityBuilder builder = entity.getBuilder();
        Equipment eq = (Equipment) builder.getObject(value);

        return eq != null && ebs.isBorrowed(eq.getId());
    }

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        double qty = ((Number) args[0]).doubleValue();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityType);
        EntityBuilder builder = entity.getBuilder();
        Equipment eq = (Equipment) builder.getObject(obj);
        String message = null;
        ebs.returnBorrowed(eq.getId(), null, null, qty, null, null, null);
        message = "Retour réussi";
        return new ActionDialogResult(message, ActionDialogResultPostProcess.RELOAD_CURRENT);
    }
}
