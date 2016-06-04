/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.admin.actions;

import java.util.List;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;

/**
 *
 * @author vpc
 */
@EntityAction(entityType = AppProfile.class,
        actionLabel = "autorisations", actionStyle = "fa-envelope-o",
        dialog = true
)
public class UpdateProfileRightsAction implements ActionDialog{

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        UpdateProfileRightsActionCtrl.Config c = new UpdateProfileRightsActionCtrl.Config();
        c.setProfile(itemIds.size()>0?itemIds.get(0):null);
        VrApp.getBean(UpdateProfileRightsActionCtrl.class).openDialog(c);
    }
    
    
    @Override
    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        return value != null;
    }

    @Override
    public void invoke(Class entityType, Object obj, Object[] args) {
        //do nothing!
    }
}
