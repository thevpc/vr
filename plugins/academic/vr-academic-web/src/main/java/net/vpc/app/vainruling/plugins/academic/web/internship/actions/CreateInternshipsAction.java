/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.internship.actions;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.editor.EntityAction;
import net.vpc.app.vainruling.core.service.editor.EntityViewActionDialog;
import net.vpc.app.vainruling.plugins.academic.model.internship.current.AcademicInternship;
import net.vpc.upa.AccessMode;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AcademicInternship.class,
        actionStyle = "fa-envelope-o"
)
public class CreateInternshipsAction implements EntityViewActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        VrApp.getBean(CreateInternshipsActionCtrl.class).openDialog();
    }

    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return mode == AccessMode.READ;
    }
}
