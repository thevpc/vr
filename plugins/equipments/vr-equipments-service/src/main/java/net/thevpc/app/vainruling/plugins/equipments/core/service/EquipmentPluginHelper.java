package net.thevpc.app.vainruling.plugins.equipments.core.service;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.upa.UPA;
import net.thevpc.upa.VoidAction;

public class EquipmentPluginHelper {

    public static String getEquipmentDepartmentUpdateRight(AppDepartment department) {
        return "Entity.Equipment.Action.UpdateDepartment." + department.getCode();
    }

    public static void ensureCreatedEquipmentDepartmentUpdateRight(AppDepartment department) {
        String _rightName = getEquipmentDepartmentUpdateRight(department);
        UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                CorePlugin.get().addProfileRightName(_rightName, _rightName);
            }
        });
    }
}
