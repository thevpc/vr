/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.filesystem.web.files;

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
public class DocumentsPropertyViewDialog implements PropertyViewDialog {

    @Override
    public String getControlType() {
        return UIConstants.ControlType.FILE;
    }

    @Override
    public void openDialog(PropertyView propertyView, String userInfo) {
        DocumentsDialogCtrl.Config c = new DocumentsDialogCtrl.Config();
        c.setPath((String) propertyView.getValue());
        c.setUserInfo(userInfo);
        VrApp.getBean(DocumentsDialogCtrl.class).openDialog(c);
    }

}
