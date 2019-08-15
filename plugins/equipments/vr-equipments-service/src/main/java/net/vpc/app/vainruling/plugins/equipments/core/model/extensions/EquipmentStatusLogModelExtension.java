/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.model.extensions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.upa.CustomDefaultObject;
import net.vpc.upa.config.*;
import net.vpc.upa.events.FieldEvent;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class EquipmentStatusLogModelExtension {

    @OnPreCreate
    public void onPreCreate(FieldEvent event) {
        if (event.getField().getAbsoluteName().equals("EquipmentStatusLog.actor")) {
            event.getField().setDefaultObject((CustomDefaultObject) () -> CorePlugin.get().getCurrentUser());
        }
    }
}
