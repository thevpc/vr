/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.core.model.extensions;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.upa.CustomDefaultObject;
import net.thevpc.upa.config.*;
import net.thevpc.upa.events.FieldEvent;

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
