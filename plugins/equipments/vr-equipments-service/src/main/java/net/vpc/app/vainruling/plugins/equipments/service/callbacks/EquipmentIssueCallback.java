/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.callbacks;

import net.vpc.app.vainruling.plugins.equipments.service.model.*;
import net.vpc.upa.Entity;
import net.vpc.upa.events.PersistEvent;
import net.vpc.upa.config.*;


/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class EquipmentIssueCallback {

    @OnPrePersist
    public void onPrePersist(PersistEvent event) {
        Entity entity = event.getEntity();
        if (entity.getName().equals("EquipmentIssueLog")) {
            EquipmentIssueLog eq = (EquipmentIssueLog) event.getPersistedObject();
            EquipmentIssueLogList type = eq.getList();
            if (type != null && type.getOperator() != null && eq.getResolutionUser() != null
                    && type.getOperator().getId() != eq.getResolutionUser().getId()) {
                //why !!!
            }
            if (type != null && eq.getResolutionUser()==null) {
                eq.setResolutionUser(type.getOperator());
            }
        }
    }

}
