/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.ContentPath;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.content.MessageTextService;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.notification.PollAware;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxFolder;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxReceived;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
public class MailboxPreviewCtrl implements PollAware,MessageTextService {


    @Override
    public void onPoll() {
        onRefresh();
    }

    @OnPageLoad
    @PostConstruct
    public void onRefresh() {
        MailboxPlugin p = VrApp.getBean(MailboxPlugin.class);
        AppUser user = UserSession.getCurrentUser();
        MailboxPreviewModel model = getModel();
        if (user != null) {
            int userId = user.getId();
            List<MailboxReceived> loadUnreadInbox = p.findLocalReceivedMessages(userId, 3, true, MailboxFolder.CURRENT);
            List<MessagePreview> previews = new ArrayList<>();
            for (MailboxReceived lo : loadUnreadInbox) {
                String cat = lo.getCategory();
                String subject = lo.getSubject();
                String content = lo.getContent();
//            String content = lo.getContent();
                previews.add(new MessagePreview(
                        lo,
                        lo.getSender(),
                        lo.getSender() == null ? null : lo.getSender().getContact().getFullName(),
                        VrUtils.strcut(cat, 36),
                        VrUtils.strcut(subject, 36),
                        VrUtils.strcut(content, 36),
                        VrUtils.getRelativeDateMessage(lo.getSendTime(), null)));
            }
            model.setInbox(previews);
            model.setUnreadCount(p.getLocalUnreadInboxCount(userId));
        } else {
            model.setInbox(new ArrayList<MessagePreview>());
            model.setUnreadCount(0);
        }
    }

    @Override
    public int getUnreadCount() {
        return getModel().getUnreadCount();
    }

    @Override
    public void loadContentTexts(String name) {
        onRefresh();
    }

    @Override
    public List<ContentText> getContentTextList(String id) {
        return (List)getModel().getInbox();
    }

    @Override
    public List<ContentText> getContentTextListHead(String id, int max) {
        List<ContentText> list = getContentTextList(id);
        if (list.size() > max) {
            return list.subList(0, max);
        }
        return list;
    }

    public MailboxPreviewModel getModel() {
        return VrApp.getBean(MailboxPreviewModel.class);
    }

    public static class MessagePreview implements ContentText{

        private MailboxReceived mailboxReceived;
        private AppUser user;
        private String from;
        private String category;
        private String subject;
        private String text;
        private String date;

        public MessagePreview(MailboxReceived mailboxReceived,AppUser user,String from, String category, String subject,String text, String date) {
            this.mailboxReceived = mailboxReceived;
            this.user = user;
            this.from = from;
            this.category = category;
            this.subject = subject;
            this.text = text;
            this.date = date;
        }

        @Override
        public String getDecoration() {
            return category;
        }

        public String getCategory() {
            return category;
        }

        public String getSubject() {
            return subject;
        }

        @Override
        public String getSubTitle() {
            return null;
        }

        public AppUser getUser() {
            return user;
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

        @Override
        public int getId() {
            return mailboxReceived.getId();
        }

        @Override
        public String getContent() {
            return mailboxReceived.getContent();
        }

        @Override
        public String getImageURL() {
            return null;
        }

        @Override
        public List<ContentPath> getAttachments() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public List<ContentPath> getImageAttachments() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public List<ContentPath> getNonImageAttachments() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public String getLinkClassStyle() {
            return null;
        }

        @Override
        public boolean isImportant() {
            return mailboxReceived.isImportant();
        }

        @Override
        public boolean isNoSubject() {
            return false;
        }

        @Override
        public String getLinkText() {
            return null;
        }

        @Override
        public String getLinkURL() {
            return null;
        }

        @Override
        public Date getPublishTime() {
            return mailboxReceived.getSendTime();
        }

        @Override
        public int getVisitCount() {
            return mailboxReceived.isRead()?1:0;
        }
    }

}
