package net.thevpc.app.vainruling.plugins.mailbox.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vpc on 9/11/16.
 */
@Component
@Scope("session")
public class MailboxPreviewModel {
    private List<MailboxPreviewCtrl.MessagePreview> inbox = new ArrayList<>();
    private int unreadCount;

    public List<MailboxPreviewCtrl.MessagePreview> getInbox() {
        return inbox;
    }

    public void setInbox(List<MailboxPreviewCtrl.MessagePreview> inbox) {
        this.inbox = inbox;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
