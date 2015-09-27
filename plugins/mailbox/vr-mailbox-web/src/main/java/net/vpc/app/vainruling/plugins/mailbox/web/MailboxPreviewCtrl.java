/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxFolder;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxReceived;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Site", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Apercu Messages",
        securityKey = "Custom.Inbox"
)
@ManagedBean
public class MailboxPreviewCtrl {

    private Model model = new Model();

    @OnPageLoad
    @PostConstruct
    public void onRefresh() {
        MailboxPlugin p = VrApp.getBean(MailboxPlugin.class);
        int userId = VrApp.getBean(UserSession.class).getUser().getId();
        List<MailboxReceived> loadUnreadInbox = p.loadLocalMailbox(userId, 3,true,MailboxFolder.CURRENT);
        List<MessagePreview> previews = new ArrayList<>();
        for (MailboxReceived lo : loadUnreadInbox) {
            String subject = lo.getSubject();
//            String content = lo.getContent();
            previews.add(new MessagePreview(
                    lo.getSender()==null?null:lo.getSender().getFullName(), 
                    VrHelper.strcut(subject, 36), 
                    VrHelper.getRelativeDateMessage(lo.getSendTime(),null)));
        }
        model.setInbox(previews);
        model.setUnreadCount(p.getLocalUnreadInboxCount(userId));
    }

    public Model getModel() {
        return model;
    }

    public static class MessagePreview {

        private String from;
        private String text;
        private String date;

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

        public MessagePreview(String from, String text, String date) {
            this.from = from;
            this.text = text;
            this.date = date;
        }

        public MessagePreview() {
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
