/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj.dialog;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.core.web.obj.PropertyView;
import net.vpc.app.vainruling.core.web.obj.PropertyViewDialog;
import org.springframework.stereotype.Component;

/**
 * @author vpc
 */
@Component
public class ProfileExprPropertyViewDialog implements PropertyViewDialog {
    @Override
    public String getControlType() {
        return UIConstants.Control.PROFILE_EXPRESSION;
    }

    @Override
    public void openDialog(PropertyView propertyView, String userInfo) {
        ProfileExprDialogCtrl.Config c = new ProfileExprDialogCtrl.Config();
        c.setExpression((String) propertyView.getValue());
        c.setSourceId(propertyView.getComponentId());
        c.setUserInfo(userInfo);
        VrApp.getBean(ProfileExprDialogCtrl.class).openDialog(c);
    }

}
