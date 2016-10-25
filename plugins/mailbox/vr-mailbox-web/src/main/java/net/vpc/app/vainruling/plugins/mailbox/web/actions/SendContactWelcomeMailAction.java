/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web.actions;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.app.vainruling.core.web.obj.ActionDialogResult;
import net.vpc.app.vainruling.core.web.obj.ObjCtrl;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AppContact.class,
        actionName = "sendContactWelcomeMail",
        actionLabel = "w-mail", actionStyle = "fa-envelope-o",
        dialog = true
)
public class SendContactWelcomeMailAction implements ActionDialog {

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
    public ActionDialogResult invoke(Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        MailboxPlugin mailboxPlugin = VrApp.getBean(MailboxPlugin.class);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        ObjCtrl objCtrl = VrApp.getBean(ObjCtrl.class);
        List<AppContact> selectedEntityObjects = objCtrl.getSelectedEntityObjects();
        List<AppUser> users=new ArrayList<>();
        for (AppContact selectedEntityObject : selectedEntityObjects) {
            AppUser u = core.findUserByContact(selectedEntityObject.getId());
            if(u!=null) {
                users.add(u);
            }
        }
        mailboxPlugin.sendWelcomeEmail(users, true);
        return ActionDialogResult.VOID;
    }

}
