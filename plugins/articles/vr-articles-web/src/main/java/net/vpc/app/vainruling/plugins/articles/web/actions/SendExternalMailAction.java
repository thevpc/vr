/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.web.actions;

import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.obj.ActionDialog;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
public class SendExternalMailAction implements ActionDialog{

    @Override
    public String getActionId() {
        return "sendExternalMail";
    }

    @Override
    public void openDialog(String actionId, String userInfo) {
        SendExternalMailActionCtrl.Config c = new SendExternalMailActionCtrl.Config();
        c.setUserInfo(userInfo);
        VrApp.getBean(SendExternalMailActionCtrl.class).openDialog(c);
    }
    
}
