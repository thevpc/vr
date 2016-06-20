/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.web.actions;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.app.vainruling.core.web.obj.ObjCtrl;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;

import java.util.List;

/**
 * @author vpc
 */
@EntityAction(entityType = AcademicInternship.class,
        actionLabel = "generer", actionStyle = "fa-envelope-o",
        dialog = true
)
public class CreateIntershipsAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        Object co = VrApp.getBean(ObjCtrl.class).getCurrentEntityObject();
        if (co == null) {
            return;
        }
        AcademicInternship a = (AcademicInternship) co;
        VrApp.getBean(CreateIntershipsActionCtrl.class).openDialog(a);
    }

    @Override
    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        return value != null && (mode == EditCtrlMode.NEW || mode == EditCtrlMode.UPDATE);
    }

    @Override
    public void invoke(Class entityType, Object obj, Object[] args) {
        //do nothing!
    }
}
