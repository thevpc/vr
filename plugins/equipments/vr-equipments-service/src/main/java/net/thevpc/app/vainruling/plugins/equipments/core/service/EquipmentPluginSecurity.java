package net.thevpc.app.vainruling.plugins.equipments.core.service;

import net.thevpc.app.vainruling.core.service.util.VrPlatformUtils;

public class EquipmentPluginSecurity {

    public static final String RIGHT_CUSTOM_EQUIPMENT_BORROWED = "Custom.Equipment.EquipmentBorrowed";
    public static final String RIGHT_CUSTOM_EQUIPMENT_BORROWABLE = "Custom.Equipment.EquipmentBorrowable";
    public static final String RIGHT_CUSTOM_EQUIPMENT_BORROW_VISA = "Custom.Equipment.EquipmentBorrowVisa";
    public static final String RIGHT_CUSTOM_EQUIPMENT_RETURN_BORROWED = "Custom.Equipment.EquipmentReturnBorrowed";
    public static final String RIGHT_CUSTOM_EQUIPMENT_EQUIPMENT_MAINTENANCE = "Custom.Equipment.EquipmentMaintenance";
    public static final String[] RIGHTS_CORE = VrPlatformUtils.getStringArrayConstantsValues(EquipmentPluginSecurity.class,"RIGHT_*");
}
