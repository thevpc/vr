/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.dialog;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.obj.PropertyView;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.obj.PropertyViewDialog;
import org.springframework.stereotype.Component;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
public class ProfileExprPropertyViewDialog implements PropertyViewDialog {
    @Override
    public String getControlType() {
        return UIConstants.Control.PROFILE_EXPRESSION;
    }
    @Override
    public boolean acceptAction(String action) {
        return true;
    }

    @Override
    public void openDialog(PropertyView propertyView, String action, String userInfo) {
        ProfileExprDialogCtrl.Config c = new ProfileExprDialogCtrl.Config();
        c.setExpression((String) propertyView.getValue());
        c.setSourceId(propertyView.getComponentId());
        c.setUserInfo(userInfo);
        VrApp.getBean(ProfileExprDialogCtrl.class).openDialog(c);
    }

}
