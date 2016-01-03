/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj;

import net.vpc.app.vainruling.api.VrApp;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
public class ActionDialogManager {
    public ActionDialog getActionDialog(String actionId){
        ApplicationContext c = VrApp.getContext();
        for (String n : c.getBeanNamesForType(ActionDialog.class)) {
            ActionDialog t=(ActionDialog)c.getBean(n);
            if(t.getActionId().equals(actionId)){
                return t;
            }
        }
        return null;
    }
}
