/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.actions;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.upa.AccessMode;

import java.util.List;
import net.vpc.app.vainruling.VrEditorActionDialog;
import net.vpc.app.vainruling.VrEditorAction;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityType = AppProfile.class,
        actionStyle = "fa-envelope-o"
)
public class UpdateProfileRightsAction implements VrEditorActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        UpdateProfileRightsActionCtrl.Config c = new UpdateProfileRightsActionCtrl.Config();
        c.setProfile(itemIds.size() > 0 ? itemIds.get(0) : null);
        VrApp.getBean(UpdateProfileRightsActionCtrl.class).openDialog(c);
    }


    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return value != null;
    }

}
