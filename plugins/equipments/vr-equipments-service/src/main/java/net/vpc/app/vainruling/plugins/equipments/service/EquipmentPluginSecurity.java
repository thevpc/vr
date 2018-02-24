package net.vpc.app.vainruling.plugins.equipments.service;

import net.vpc.app.vainruling.core.service.util.VrPlatformUtils;

public class EquipmentPluginSecurity {

    public static final String RIGHT_CUSTOM_EQUIPMENT_EQUIPMENT_MAINTENANCE = "Custom.Equipment.EquipmentMaintenance";
    public static final String[] RIGHTS_CORE = VrPlatformUtils.getStringArrayConstantsValues(EquipmentPluginSecurity.class,"RIGHT_*");
}
