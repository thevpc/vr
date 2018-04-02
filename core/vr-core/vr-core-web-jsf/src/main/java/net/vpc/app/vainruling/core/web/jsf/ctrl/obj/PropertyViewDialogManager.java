/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl.obj;

import net.vpc.app.vainruling.core.service.VrApp;
import org.springframework.stereotype.Component;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
public class PropertyViewDialogManager {

    public PropertyViewDialog getPropertyViewDialog(String controlType, String action) {
        for (PropertyViewDialog t : VrApp.getBeansForType(PropertyViewDialog.class)) {
            if (t.getControlType().equals(controlType)) {
                return t;
            }
        }
        return null;
    }
}
