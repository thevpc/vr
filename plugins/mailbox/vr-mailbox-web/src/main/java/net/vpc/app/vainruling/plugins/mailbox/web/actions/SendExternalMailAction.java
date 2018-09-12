/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web.actions;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.EntityAction;

import net.vpc.app.vainruling.core.service.model.content.AppArticle;
import net.vpc.app.vainruling.core.web.obj.EntityViewActionDialog;
import net.vpc.upa.AccessMode;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@EntityAction(entityType = AppArticle.class,
        actionStyle = "fa-envelope-o"
)
public class SendExternalMailAction implements EntityViewActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        SendExternalMailActionCtrl.Config c = new SendExternalMailActionCtrl.Config();
        VrApp.getBean(SendExternalMailActionCtrl.class).openDialog(c);
    }

    @Override
    public boolean isEnabled(String actionId, Class entityType, AccessMode mode, Object value) {
        return value != null;
    }

//    @Override
//    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
//        VrApp.getBean(MailboxPlugin.class).sendExternalMail((AppArticle) obj, (String) args[0]);
//        return ActionDialogResult.VOID;
//    }

}
