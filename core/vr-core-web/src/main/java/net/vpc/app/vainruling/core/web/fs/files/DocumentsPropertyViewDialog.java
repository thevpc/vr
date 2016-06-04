/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.fs.files;

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
