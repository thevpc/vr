/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web.actions;

import java.util.List;
import net.vpc.app.vainruling.api.EntityAction;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.api.web.obj.ActionDialog;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;

/**
 *
 * @author vpc
 */
@EntityAction(entityType = AppUser.class,
        actionName = "sendWelcomeMail",
        actionLabel = "welcome email", actionStyle = "fa-envelope-o",
        dialog = true
)
public class SendWelcomeMailAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        SendWelcomeMailActionCtrl.Config c = new SendWelcomeMailActionCtrl.Config();
        VrApp.getBean(SendWelcomeMailActionCtrl.class).openDialog(c);
    }

    @Override
    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        return true;//value != null;
    }

    @Override
    public void invoke(Class entityType, Object obj, Object[] args) {
        MailboxPlugin mailboxPlugin = VrApp.getBean(MailboxPlugin.class);
        ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
        mailboxPlugin.sendWelcomeEmail(objCtrl.getModel().getSelectedObjects(),true);
    }

}
