/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.web.actions;

import java.util.List;
import net.vpc.app.vainruling.api.EntityAction;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.api.web.obj.ActionDialog;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternship;

/**
 *
 * @author vpc
 */
@EntityAction(entityType = AcademicInternship.class,
        actionLabel = "generer", actionStyle = "fa-envelope-o",
        dialog = true
)
public class CreateIntershipsAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        Object co = VrApp.getBean(ObjCtrl.class).getModel().getCurrentObj();
        if(co==null){
            return;
        }
        AcademicInternship a = (AcademicInternship) co;
        VrApp.getBean(CreateIntershipsActionCtrl.class).openDialog(a);
    }

    @Override
    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        return value != null && (mode==EditCtrlMode.NEW||mode==EditCtrlMode.UPDATE);
    }

    @Override
    public void invoke(Class entityType, Object obj, Object[] args) {
        //do nothing!
    }
}
