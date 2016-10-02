/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.web;

import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppTrace;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.app.vainruling.plugins.equipments.service.EquipmentPlugin;
import net.vpc.app.vainruling.plugins.equipments.service.model.Equipment;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.util.Utils;
import net.vpc.upa.*;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = Equipment.class,
        actionLabel = "sep.", actionStyle = "fa-calculator",
        dialog = false
)
public class SplitEquipmentQtyAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        return value != null;//value != null;
    }

    @Override
    public void invoke(Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        EquipmentPlugin equipmentPlugin = VrApp.getBean(EquipmentPlugin.class);
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity entity = pu.getEntity(entityType);
        EntityBuilder builder = entity.getBuilder();
        Equipment eq = (Equipment) builder.getObject(obj);

        if (equipmentPlugin.splitEquipmentQuantities(eq.getId()) > 0) {
            FacesUtils.addInfoMessage("Separation réussie");
        }
    }
}
