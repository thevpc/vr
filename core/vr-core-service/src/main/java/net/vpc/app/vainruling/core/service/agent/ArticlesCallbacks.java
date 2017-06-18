/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.agent;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.service.model.content.ArticlesItem;
import net.vpc.upa.CustomDefaultObject;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.callbacks.FieldEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnCreate;
import net.vpc.upa.config.OnPreCreate;
import net.vpc.upa.exceptions.UPAException;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class ArticlesCallbacks {
    @OnPreCreate
    public void onPreCreate(FieldEvent event) {
        if(event.getField().getAbsoluteName().equals("EquipmentStatusLog.actor")){
            event.getField().setDefaultObject((CustomDefaultObject) () -> CorePlugin.get().getCurrentUser());
        }
    }

    @OnCreate
    public void onCreateField(FieldEvent event) throws UPAException {
        Entity e = event.getEntity();
        Field f = event.getField();
        if (e.getEntityType().equals(ArticlesItem.class)) {
            if (f.getName().equals("sender")) {
                f.setDefaultObject(VrUtils.DEFAULT_OBJECT_CURRENT_USER);
            }
            if (f.getName().equals("sendTime")) {
                f.setDefaultObject(VrUtils.DEFAULT_OBJECT_CURRENT_DATETIME);
            }
        }
    }

}
