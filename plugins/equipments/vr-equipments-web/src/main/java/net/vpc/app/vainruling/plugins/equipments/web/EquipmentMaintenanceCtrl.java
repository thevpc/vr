/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.web;

import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;

import javax.annotation.PostConstruct;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Site", css = "fa-dashboard", ctrl = "")},
        url = "modules/equipments/maintenance",
        securityKey = "Custom.Equipment.EquipmentMaintenance"
)
public class EquipmentMaintenanceCtrl {


    @OnPageLoad
    @PostConstruct
    public void onRefresh() {

    }


}
