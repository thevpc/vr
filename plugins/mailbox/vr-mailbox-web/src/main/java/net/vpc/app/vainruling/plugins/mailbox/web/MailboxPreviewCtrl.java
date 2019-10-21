/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.mailbox.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPluginSecurity;
import net.vpc.app.vainruling.plugins.inbox.model.MailboxFolder;
import net.vpc.app.vainruling.plugins.inbox.model.MailboxReceived;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.vpc.app.vainruling.core.service.content.ContentPath;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.model.strict.AppUserStrict;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrPathItem;
import net.vpc.app.vainruling.VrPollService;
import net.vpc.app.vainruling.VrMessageTextService;
import net.vpc.app.vainruling.VrOnPageLoad;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Site", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        //        title = "Apercu Messages",
        securityKey = MailboxPluginSecurity.RIGHT_CUSTOM_INBOX
)
public class MailboxPreviewCtrl implements VrPollService, VrMessageTextService {

    @Override
    public void onPoll() {
        onRefresh();
    }

    @Override
    public int getSupport(String name) {
        return "Mailbox".equals(name) ? 1 : -1;
    }

    @VrOnPageLoad
//    @PostConstruct
    public void onRefresh() {
        if (true) {
            return; //TODO FIX ME!!!!
        }
        MailboxPlugin p = VrApp.getBean(MailboxPlugin.class);
        AppUser user = CorePlugin.get().getCurrentUser();
        MailboxPreviewModel model = getModel();
        if (user != null) {
            int userId = user.getId();
            List<MailboxReceived> loadUnreadInbox = p.findLocalReceivedMessages(userId, 3, true, MailboxFolder.CURRENT);
            List<MessagePreview> previews = new ArrayList<>();
            for (MailboxReceived lo : loadUnreadInbox) {
                String cat = lo.getCategory();
                String subject = lo.getSubject();
                String content = lo.getContent();
                String recipients = lo.getToProfiles();
//            String content = lo.getContent();
                previews.add(new MessagePreview(
                        lo,
                        lo.getSender(),
                        lo.getSender() == null ? null : lo.getSender().getFullName(),
                        VrUtils.strcut(cat, 36),
                        VrUtils.strcut(subject, 36),
                        VrUtils.strcut(content, 36),
                        VrUtils.getRelativeDateMessage(lo.getSendTime(), null), recipients)
                );
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
        return (List) getModel().getInbox();
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

    public static class MessagePreview implements ContentText {

        private MailboxReceived mailboxReceived;
        private AppUserStrict user;
        private String from;
        private String category;
        private String subject;
        private String text;
        private String date;
        private String recipients;

        public MessagePreview(MailboxReceived mailboxReceived, AppUser user, String from, String category, String subject, String text, String date, String recipients) {
            this.mailboxReceived = mailboxReceived;
            this.user = new AppUserStrict(user);
            this.from = from;
            this.category = category;
            this.subject = subject;
            this.text = text;
            this.date = date;
            this.recipients = recipients;
        }

        @Override
        public String getRecipients() {
            return recipients;
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

        public AppUserStrict getUser() {
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
            return mailboxReceived.isRead() ? 1 : 0;
        }
    }

}
