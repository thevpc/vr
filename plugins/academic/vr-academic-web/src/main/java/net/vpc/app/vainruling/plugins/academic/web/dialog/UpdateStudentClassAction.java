/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicStudent;
import net.vpc.upa.AccessMode;

import java.util.List;
import net.vpc.app.vainruling.VrEditorActionDialog;
import net.vpc.app.vainruling.VrEditorAction;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityType = AcademicStudent.class,
        actionStyle = "fa-envelope-o"
)
public class UpdateStudentClassAction implements VrEditorActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        VrApp.getBean(UpdateStudentClassActionCtrl.class).openDialog(itemIds);
    }

    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return true;
    }
}
