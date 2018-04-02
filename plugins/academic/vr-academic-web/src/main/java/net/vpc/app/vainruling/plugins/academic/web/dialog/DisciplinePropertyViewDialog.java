/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.dialog;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.core.web.jsf.ctrl.obj.PropertyView;
import net.vpc.app.vainruling.core.web.jsf.ctrl.obj.PropertyViewDialog;
import org.springframework.stereotype.Component;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
public class DisciplinePropertyViewDialog implements PropertyViewDialog {
    @Override
    public String getControlType() {
        return UIConstants.Control.DISCIPLINE;
    }

    @Override
    public void openDialog(PropertyView propertyView, String action, String userInfo) {
        DisciplineDialogCtrl.Config c = new DisciplineDialogCtrl.Config();
        c.setExpression((String) propertyView.getValue());
        c.setSourceId(propertyView.getComponentId());
        c.setUserInfo(userInfo);
        VrApp.getBean(DisciplineDialogCtrl.class).openDialog(c);
    }

    @Override
    public boolean acceptAction(String action) {
        return true;
    }
}
