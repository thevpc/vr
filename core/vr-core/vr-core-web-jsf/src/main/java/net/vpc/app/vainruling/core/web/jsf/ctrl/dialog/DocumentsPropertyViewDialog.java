/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.dialog;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.core.web.jsf.ctrl.obj.PropertyView;
import net.vpc.app.vainruling.core.web.jsf.ctrl.obj.PropertyViewDialog;
import net.vpc.app.vainruling.core.web.jsf.ctrl.obj.FieldPropertyView;
import org.springframework.stereotype.Component;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
public class DocumentsPropertyViewDialog implements PropertyViewDialog {

    @Override
    public String getControlType() {
        return UIConstants.Control.FILE;
    }
    @Override
    public boolean acceptAction(String action) {
        return true;
    }

    @Override
    public void openDialog(PropertyView propertyView, String action, String userInfo) {
        if("upload".equals(action)){
            DocumentsUploadDialogCtrl.Config c = new DocumentsUploadDialogCtrl.Config();
            c.setPath((String) propertyView.getValue());
            c.setUserInfo(userInfo);
            if (propertyView instanceof FieldPropertyView) {
                String type = ((FieldPropertyView) propertyView).getField().getProperties().getString(UIConstants.Form.CONTROL_FILE_TYPE);
                String fspath = ((FieldPropertyView) propertyView).getField().getProperties().getString(UIConstants.Form.CONTROL_FILE_PATH);
                c.setType(type);
                c.setFspath(fspath);
            }
            VrApp.getBean(DocumentsUploadDialogCtrl.class).openDialog(c);
        }else {
            DocumentsDialogCtrl.Config c = new DocumentsDialogCtrl.Config();
            c.setPath((String) propertyView.getValue());
            c.setUserInfo(userInfo);
            if (propertyView instanceof FieldPropertyView) {
                String type = ((FieldPropertyView) propertyView).getField().getProperties().getString(UIConstants.Form.CONTROL_FILE_TYPE);
                String fspath = ((FieldPropertyView) propertyView).getField().getProperties().getString(UIConstants.Form.CONTROL_FILE_PATH);
                c.setType(type);
                c.setFspath(fspath);
            }
            VrApp.getBean(DocumentsDialogCtrl.class).openDialog(c);
        }
    }

}
