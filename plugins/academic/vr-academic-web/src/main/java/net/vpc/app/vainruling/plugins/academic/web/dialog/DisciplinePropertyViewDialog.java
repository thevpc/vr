/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.ui.UIConstants;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import org.springframework.stereotype.Component;
import net.vpc.app.vainruling.api.web.obj.PropertyViewDialog;

/**
 *
 * @author vpc
 */
@Component
public class DisciplinePropertyViewDialog implements PropertyViewDialog {
    @Override
    public String getControlType() {
        return UIConstants.ControlType.DISCIPLINE;
    }

    @Override
    public void openDialog(PropertyView propertyView, String userInfo) {
        DisciplineDialogCtrl.Config c = new DisciplineDialogCtrl.Config();
        c.setExpression((String)propertyView.getValue());
        c.setSourceId(propertyView.getComponentId());
        c.setUserInfo(userInfo);
        VrApp.getBean(DisciplineDialogCtrl.class).openDialog(c);
    }

}
