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

    /**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "Equipment",
        actionIcon = "cubes"
)
public class MigrateEquipmentAction implements VrEditorActionInvoke {

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return value != null;//value != null;
    }

    @Override
    public ActionDialogResult invoke(String actionId, String entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        EquipmentPlugin equipmentPlugin = VrApp.getBean(EquipmentPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityType);
        EntityBuilder builder = entity.getBuilder();
        Equipment eq = (Equipment) builder.getObject(obj);

        String message=null;
        if (equipmentPlugin.migrateEquipmentStatuses(eq.getId(),true)) {
            message = "Opération réussie";
        }
        return new ActionDialogResult(message, ActionDialogResultPostProcess.RELOAD_ALL);
    }
}
