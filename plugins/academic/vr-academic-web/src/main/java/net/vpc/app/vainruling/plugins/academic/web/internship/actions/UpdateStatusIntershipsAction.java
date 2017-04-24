/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.internship.actions;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.app.vainruling.core.web.obj.ActionDialogResult;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternship;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AcademicInternship.class,
        actionLabel = "phases", actionStyle = "fa-envelope-o",
        dialog = true
)
public class UpdateStatusIntershipsAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        VrApp.getBean(UpdateStatusInternshipsActionCtrl.class).openDialog(itemIds);
    }

    @Override
    public boolean isEnabled(String actionId, Class entityType, EditCtrlMode mode, Object value) {
        return value == null;
    }

    @Override
    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        //do nothing!
        return ActionDialogResult.VOID;
    }
}
