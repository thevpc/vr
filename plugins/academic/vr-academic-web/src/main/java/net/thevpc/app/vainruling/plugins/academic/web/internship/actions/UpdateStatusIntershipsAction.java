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

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AcademicInternship",
        actionIcon = "calendar-check"
)
public class UpdateStatusIntershipsAction implements VrEditorActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        VrApp.getBean(UpdateStatusInternshipsActionCtrl.class).openDialog(itemIds);
    }

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return value == null;
    }

}
