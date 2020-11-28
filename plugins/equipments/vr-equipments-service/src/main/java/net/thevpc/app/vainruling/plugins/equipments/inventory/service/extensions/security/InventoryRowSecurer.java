/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.inventory.service.extensions.security;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.model.AppDepartment;
import net.thevpc.app.vainruling.plugins.equipments.inventory.model.InventoryRow;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.security.UserToken;
import net.thevpc.upa.DefaultEntitySecurityManager;
import net.thevpc.upa.Document;
import net.thevpc.upa.Entity;
import net.thevpc.upa.config.SecurityContext;

/**
 * @author taha.bensalah@gmail.com
 */
@SecurityContext(entity = "InventoryRow")
public class InventoryRowSecurer extends DefaultEntitySecurityManager {

    public InventoryRowSecurer() {
    }

    @Override
    public boolean isAllowedUpdate(Entity entity, Object id, Object value) {
        boolean allowed = super.isAllowedUpdate(entity, id, value);
        if (!allowed) {
            return false;
        }
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        UserToken us = core.getCurrentToken();
        if (us != null) {
            if(us.isAdmin()){
                return true;
            }
            AppDepartment d = core.getCurrentUser().getDepartment();
            if (d != null) {
                if (value instanceof Document) {
                    value = entity.getBuilder().documentToObject((Document) value);
                }
                InventoryRow e = (InventoryRow) value;
                AppDepartment d2 = e.getInventory()==null?null:e.getInventory().getDepartment();
                if (d2 != null) {
                    if (d.getId() == d2.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
