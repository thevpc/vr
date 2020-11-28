/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.mailbox.web.extensions.editor;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.VrEditorActionDialog;
import net.thevpc.app.vainruling.core.service.VrApp;


import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AppArticle",
        actionIcon = "envelope-square"
)
public class SendLocalMailAction implements VrEditorActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        SendExternalMailActionCtrl.Config c = new SendExternalMailActionCtrl.Config();
        VrApp.getBean(SendExternalMailActionCtrl.class).openDialog(c);
    }

    @Override
    public boolean isEnabled(String actionId, String entityType, VrAccessMode mode, Object value) {
        return value != null;
    }

//    @Override
//    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
//        VrApp.getBean(MailboxPlugin.class).sendLocalMail((AppArticle) obj, (String) args[0]);
//        return ActionDialogResult.VOID;
//    }

}
