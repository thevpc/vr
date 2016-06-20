/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.admin.actions;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;

import java.util.List;

/**
 * @author vpc
 */
@EntityAction(entityType = AppProfile.class,
        actionLabel = "usr", actionStyle = "fa-envelope-o",
        dialog = true
)
public class UpdateProfileUsersAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        UpdateProfileUsersActionCtrl.Config c = new UpdateProfileUsersActionCtrl.Config();
        c.setProfile(itemIds.size() > 0 ? itemIds.get(0) : null);
        VrApp.getBean(UpdateProfileUsersActionCtrl.class).openDialog(c);
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
