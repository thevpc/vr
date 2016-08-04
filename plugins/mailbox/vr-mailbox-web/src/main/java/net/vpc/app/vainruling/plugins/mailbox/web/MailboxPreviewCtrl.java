/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.notification.PollAware;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxFolder;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxReceived;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Site", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Apercu Messages",
        securityKey = "Custom.Inbox"
)
@ManagedBean
public class MailboxPreviewCtrl implements PollAware {

    private Model model = new Model();

    @Override
    public void onPoll() {
        onRefresh();
    }

    @OnPageLoad
    @PostConstruct
    public void onRefresh() {
        MailboxPlugin p = VrApp.getBean(MailboxPlugin.class);
        AppUser user = VrApp.getBean(UserSession.class).getUser();
        if (user != null) {
            int userId = user.getId();
            List<MailboxReceived> loadUnreadInbox = p.loadLocalMailbox(userId, 3, true, MailboxFolder.CURRENT);
            List<MessagePreview> previews = new ArrayList<>();
            for (MailboxReceived lo : loadUnreadInbox) {
                String subject = lo.getSubject();
//            String content = lo.getContent();
                previews.add(new MessagePreview(
                        lo.getSender() == null ? null : lo.getSender().getContact().getFullName(),
                        VrHelper.strcut(subject, 36),
                        VrHelper.getRelativeDateMessage(lo.getSendTime(), null)));
            }
            model.setInbox(previews);
            model.setUnreadCount(p.getLocalUnreadInboxCount(userId));
        } else {
            model.setInbox(new ArrayList<MessagePreview>());
            model.setUnreadCount(0);
        }
    }

    public Model getModel() {
        return model;
    }

    public static class MessagePreview {

        private String from;
        private String text;
        private String date;

        public MessagePreview(String from, String text, String date) {
            this.from = from;
            this.text = text;
            this.date = date;
        }

        public MessagePreview() {
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

    }

    public static class Model {

        private List<MessagePreview> inbox = new ArrayList<>();
        private int unreadCount;

        public List<MessagePreview> getInbox() {
            return inbox;
        }

        public void setInbox(List<MessagePreview> inbox) {
            this.inbox = inbox;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }

    }
}
