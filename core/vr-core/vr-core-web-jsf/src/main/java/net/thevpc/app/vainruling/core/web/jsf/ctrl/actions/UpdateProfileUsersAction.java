/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.actions;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.core.service.VrApp;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.app.vainruling.VrEditorActionDialog;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AppProfile",
        actionIcon = "users"
)
public class UpdateProfileUsersAction implements VrEditorActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        UpdateProfileUsersActionCtrl.Config c = new UpdateProfileUsersActionCtrl.Config();
        c.setProfile(itemIds.size() > 0 ? itemIds.get(0) : null);
        VrApp.getBean(UpdateProfileUsersActionCtrl.class).openDialog(c);
    }

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return value != null;
    }

}
