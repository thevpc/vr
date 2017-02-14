/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.load;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.app.vainruling.core.web.obj.ActionDialogResult;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicStudent;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCourseAssignment;
import net.vpc.app.vainruling.plugins.academic.web.dialog.UpdateStudentClassActionCtrl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AcademicCourseAssignment.class,
        actionLabel = "g/2", actionStyle = "fa-envelope-o",
        dialog = false,
        confirm = true
)
public class SplitGroupsAcademicCourseAssignmentAction implements ActionDialog {

    @Autowired
    AcademicPlugin academic;
    @Override
    public void openDialog(String actionId, List<String> itemIds) {

    }

    @Override
    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        return mode!=EditCtrlMode.NEW;
    }

    @Override
    public ActionDialogResult invoke(Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        for (String id : selectedIdStrings) {
            academic.splitGroupCourseAssignment(Integer.parseInt(id));
        }
        //do nothing!
        return ActionDialogResult.RELOAD_ALL;
    }
}
