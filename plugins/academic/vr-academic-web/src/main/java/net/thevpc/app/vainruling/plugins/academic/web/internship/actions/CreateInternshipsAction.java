/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.web.internship.actions;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.VrEditorActionDialog;
import net.thevpc.app.vainruling.core.service.VrApp;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.common.util.Convert;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AcademicInternshipBoard",
        actionIcon = "user-cog"
)
public class CreateInternshipsAction implements VrEditorActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        if (itemIds.size() == 1) {
            CreateInternshipsActionCtrl bean = VrApp.getBean(CreateInternshipsActionCtrl.class);
            bean.getModel().setBoardId(Convert.toInt(itemIds.get(0)));
            bean.openDialog();
        }
    }

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return mode == VrAccessMode.UPDATE;
    }
}
