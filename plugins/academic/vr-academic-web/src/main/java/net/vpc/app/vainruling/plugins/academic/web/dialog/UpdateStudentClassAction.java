/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AcademicStudent.class,
        actionLabel = "cls", actionStyle = "fa-envelope-o",
        dialog = true
)
public class UpdateStudentClassAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        VrApp.getBean(UpdateStudentClassActionCtrl.class).openDialog(itemIds);
    }

    @Override
    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        return value == null;
    }

    @Override
    public void invoke(Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        //do nothing!
    }
}
