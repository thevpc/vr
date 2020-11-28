/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.web.dialog;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.VrEditorActionDialog;
import net.thevpc.app.vainruling.core.service.VrApp;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AcademicStudent",
        actionIcon = "user-cog"
)
public class UpdateStudentClassAction implements VrEditorActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        VrApp.getBean(UpdateStudentClassActionCtrl.class).openDialog(itemIds);
    }

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return true;
    }
}
