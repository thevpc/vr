/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.core.service.extensions.data;

import net.thevpc.app.vainruling.plugins.equipments.core.model.EquipmentIssueLog;
import net.thevpc.app.vainruling.plugins.equipments.core.model.EquipmentIssueLogList;
import net.thevpc.upa.Entity;
import net.thevpc.upa.events.PersistEvent;
import net.thevpc.upa.config.*;


/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class EquipmentIssueLogDaoExtension {

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
