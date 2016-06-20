/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.app.vainruling.core.service.VrApp;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author vpc
 */
@Component
public class PropertyViewDialogManager {
    public PropertyViewDialog getPropertyViewDialog(String controlType) {
        ApplicationContext c = VrApp.getContext();
        for (String n : c.getBeanNamesForType(PropertyViewDialog.class)) {
            PropertyViewDialog t = (PropertyViewDialog) c.getBean(n);
            if (t.getControlType().equals(controlType)) {
                return t;
            }
        }
        return null;
    }
}
