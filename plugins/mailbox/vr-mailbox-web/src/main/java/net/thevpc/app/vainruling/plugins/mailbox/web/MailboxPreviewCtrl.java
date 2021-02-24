/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.mailbox.web;

import net.thevpc.app.vainruling.VrMessageTextService;
import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.VrPollService;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.thevpc.app.vainruling.plugins.inbox.service.MailboxPluginSecurity;
import net.thevpc.app.vainruling.plugins.inbox.model.MailboxFolder;
import net.thevpc.app.vainruling.plugins.inbox.model.MailboxReceived;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.thevpc.app.vainruling.core.service.model.strict.AppUserStrict;
import net.thevpc.app.vainruling.VrPage;
import net.thevpc.app.vainruling.core.service.content.VrContentPath;
import net.thevpc.app.vainruling.core.service.content.VrContentText;
import net.thevpc.app.vainruling.core.service.model.content.DefaultVrContentText;

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
    public List<VrContentText> getContentTextList(String id) {
        return (List) getModel().getInbox();
    }

    @Override
    public List<VrContentText> getContentTextListHead(String id, int max) {
        List<VrContentText> list = getContentTextList(id);
        if (list.size() > max) {
            return list.subList(0, max);
        }
        return list;
    }

    public MailboxPreviewModel getModel() {
        return VrApp.getBean(MailboxPreviewModel.class);
    }

    public static class MessagePreview extends DefaultVrContentText {

//        private MailboxReceived mailboxReceived;
        private String from;
        private String text;
        private String date;

        public MessagePreview() {
        }
        public MessagePreview(MailboxReceived mailboxReceived, AppUser user, String from, String category, String subject, String text, String date, String recipients) {
            setUser(new AppUserStrict(user,CorePlugin.get().getUserIcon(user==null?-1:user.getId())));
            setRecipients(recipients);
            setCategories(category==null?new String[0]:new String[]{category});
            setTitle(subject);
            setContent(mailboxReceived.getContent());
            setImportant(mailboxReceived.isImportant());
            setPublishTime(mailboxReceived.getSendTime());
            setVisitCount(mailboxReceived.isRead() ? 1 : 0);
            this.from = from;
            this.text = text;
            this.date = date;
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

}
