/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web.extensions.editor;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.upa.AccessMode;

import java.util.List;
import net.vpc.app.vainruling.VrEditorActionDialog;
import net.vpc.app.vainruling.VrEditorAction;

/**
 * @author taha.bensalah@gmail.com
 */
@VrEditorAction(entityName = "AppUser",
        actionStyle = "fa-envelope-o"
)
public class SendUserWelcomeMailAction implements VrEditorActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        SendWelcomeMailActionCtrl.Config c = new SendWelcomeMailActionCtrl.Config();
        VrApp.getBean(SendWelcomeMailActionCtrl.class).openDialog(c);
    }

    @Override
    public boolean isEnabled(String actionId, String entityType, AccessMode mode, Object value) {
        return true;//value != null;
    }

//    @Override
//    public ActionDialogResult invoke(String actionId, Class entityType, Object obj, List<String> selectedIdStrings, Object[] args) {
//        MailboxPlugin mailboxPlugin = VrApp.getBean(MailboxPlugin.class);
//        EditorCtrl editorCtrl = VrApp.getBean(EditorCtrl.class);
//        mailboxPlugin.sendWelcomeEmail(editorCtrl.getSelectedEntityObjects(), true);
//        return ActionDialogResult.VOID;
//    }

}
