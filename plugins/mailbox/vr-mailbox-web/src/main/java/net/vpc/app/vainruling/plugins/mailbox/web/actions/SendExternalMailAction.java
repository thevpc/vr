/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web.actions;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;

import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.app.vainruling.core.web.obj.ActionDialogResult;
import net.vpc.app.vainruling.core.service.model.content.ArticlesItem;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.upa.AccessMode;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = ArticlesItem.class,
        actionName = "sendExternalMail",
        actionLabel = "email", actionStyle = "fa-envelope-o",
        dialog = true
)
public class SendExternalMailAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        SendExternalMailActionCtrl.Config c = new SendExternalMailActionCtrl.Config();
        VrApp.getBean(SendExternalMailActionCtrl.class).openDialog(c);
    }

    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return value != null;
    }

    @Override
    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
        VrApp.getBean(MailboxPlugin.class).sendExternalMail((ArticlesItem) obj, (String) args[0]);
        return ActionDialogResult.VOID;
    }

}
