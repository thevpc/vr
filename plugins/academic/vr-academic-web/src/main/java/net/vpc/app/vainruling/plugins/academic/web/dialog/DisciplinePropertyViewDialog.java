/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.core.web.obj.PropertyView;
import org.springframework.stereotype.Component;
import net.vpc.app.vainruling.core.web.obj.PropertyViewDialog;

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
