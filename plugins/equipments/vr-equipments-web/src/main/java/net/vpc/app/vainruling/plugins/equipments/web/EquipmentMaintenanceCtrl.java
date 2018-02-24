/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.web;

import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.equipments.service.EquipmentPluginSecurity;

import javax.annotation.PostConstruct;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Site", css = "fa-dashboard", ctrl = "")},
        url = "modules/equipments/maintenance",
        securityKey = EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_EQUIPMENT_MAINTENANCE
)
public class EquipmentMaintenanceCtrl {


    @OnPageLoad
    @PostConstruct
    public void onRefresh() {

    }


}
