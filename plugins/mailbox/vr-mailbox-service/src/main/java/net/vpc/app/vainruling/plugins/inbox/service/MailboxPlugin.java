/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.service;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.CorePluginSecurity;
import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.model.content.ArticlesItem;
import net.vpc.app.vainruling.core.service.notification.VrNotificationEvent;
import net.vpc.app.vainruling.core.service.notification.VrNotificationManager;
import net.vpc.app.vainruling.core.service.notification.VrNotificationSession;
import net.vpc.app.vainruling.core.service.obj.AppEntityExtendedPropertiesProvider;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.core.service.util.VrPasswordStrategyRandom;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxFolder;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxMessageFormat;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxReceived;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxSent;
import net.vpc.common.gomail.*;
import net.vpc.common.gomail.datasource.StringsGoMailDataSource;
import net.vpc.common.gomail.modules.GoMailModuleProcessor;
import net.vpc.common.gomail.modules.GoMailModuleSerializer;
import net.vpc.common.io.InputStreamSource;
import net.vpc.common.io.OutputStreamSource;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.upa.*;
import net.vpc.upa.types.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPlugin
public class MailboxPlugin {
    public static final String SEND_EXTERNAL_MAIL_QUEUE = "sendExternalMailQueue";

    //    public void
    public static final String HEADER_THREAD_ID = "header.X-Thread";
    public static final String HEADER_PRIORITY = "header.X-Priority";
    public static final String HEADER_TO_PROFILES = "header.X-App-ToProfiles";
    public static final String HEADER_CC_PROFILES = "header.X-App-CCProfiles";
    public static final String HEADER_BCC_PROFILES = "X-App-BcCProfiles";
    public static final String HEADER_CATEGORY = "header.X-App-Category";
    public static final String SEND_WELCOME_MAIL_QUEUE = "sendWelcomeMailQueue";

    @Autowired
    public CorePlugin core;
    public GoMailFactory gomailFactory = DefaultGoMailFactory.INSTANCE;
    //    public GoMailModuleProcessor externalMailProcessor = new GoMailModuleProcessor(new VrExternalMailAgent());
    public GoMailModuleSerializer serializer = new GoMailModuleSerializer();
    GoMailListener notifPusher = new GoMailListener() {
        @Override
        public void onBeforeSend(GoMailMessage mail) {

        }

        @Override
        public void onAfterSend(GoMailMessage mail) {
            VrApp.getBean(VrNotificationSession.class).publish(new VrNotificationEvent(SEND_WELCOME_MAIL_QUEUE, 60, null, "to:" + mail.to() + " ; " + mail.subject(), null, Level.INFO));
        }

        @Override
        public void onSendError(GoMailMessage mail, Throwable exc) {
            VrApp.getBean(VrNotificationSession.class).publish(new VrNotificationEvent(SEND_WELCOME_MAIL_QUEUE, 60, null, "to:" + mail.to() + " ; " + mail.subject() + " : " + exc, null, Level.SEVERE));
        }
    };

    public MailboxPlugin() {
    }

    @Start
    private void start() {
        VrApp.getBean(VrNotificationManager.class).register(SEND_WELCOME_MAIL_QUEUE, SEND_WELCOME_MAIL_QUEUE, 200);
        VrApp.getBean(VrNotificationManager.class).register(SEND_EXTERNAL_MAIL_QUEUE, SEND_EXTERNAL_MAIL_QUEUE, 200);
    }

    @Install
    private void installService() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        core.createRight(MailboxPluginSecurity.RIGHT_CUSTOM_SITE_MAILBOX, "Mailbox");
        HashSet<String> goodProfiles = new HashSet<String>(Arrays.asList(
                "LocalMailUser", "HeadOfDepartment", "DirectorOfStudies", "Director", "Student"
        ));
        PersistenceUnit pu = UPA.getPersistenceUnit();
        for (AppProfile prof : core.findProfiles()) {
            if (goodProfiles.contains(prof.getName())) {
                core.addProfileRight(prof.getId(), MailboxPluginSecurity.RIGHT_CUSTOM_SITE_MAILBOX);
                for (String entityName : new String[]{"MailboxReceived","MailboxSent"}) {
                    Entity entity=pu.getEntity(entityName);
                    for (String right : CorePluginSecurity.getEntityRights(
                            entity,
                            true,
                            true,
                            true,
                            false,
                            false
                    )) {
                        if(!CorePluginSecurity.getEntityRightEditor(entity).equals(right)) {
                            core.addProfileRight(prof.getId(), right);
                        }
                    }
                }
            }
        }

        MailboxMessageFormat w = getWelcomeTemplate();
        if (w == null) {
            w = new MailboxMessageFormat();
            w.setName("WelcomeMail");
            w.setSubject("[ENISo][II] Nouveau Compte : ${mail_subject}");
            w.setPlainBody("${civility} ${firstName},\n"
                    + " Un nouveau compte a été crée pour vous sur eniso.info, le site officiel du département informatique Industrielle de l'ENISo.\n"
                    + " Ce compte vous permet d'être toujours informé sur les nouveautés du département et présente un espace indispensable\n"
                    + " pour coordonner entre administration, enseignants, et élèves ingénieurs.\n"
                    + " Merci donc de consulter régulièrement votre compte.\n"
                    + " Vos paramètres de connection sont les suivants :\n"
                    + "  + Identifiant : ${new_user_login}\n"
                    + "  + Mot de passe : ${new_user_password}\n"
                    + " Cordialement\n"
                    + " ${from_fullName}\n"
                    + " ${from_positionTitle1}\n"
                    + " ${from_positionTitle2}\n"
                    + " ${from_positionTitle3}");
            w.setFormattedBody("<p>${civility} ${firstName},</p>\n"
                    + " ${mail_body}\n"
                    + " <p>Un nouveau compte a été crée pour vous sur eniso.info, le site officiel du département informatique Industrielle de l'ENISo.</p>"
                    + " <p>Ce compte vous permet d'être toujours informé sur les nouveautés du département et présente un espace indispensable \n"
                    + " pour coordonner entre administration, enseignants, et élèves ingénieurs .</p>\n"
                    + " <p>Merci donc de consulter régulièrement votre compte.</p>\n"
                    + " <p>Vos paramètres de connection sont les suivants :\n"
                    + "  <ul>\n"
                    + "  <li>Identifiant : ${new_user_login}</li>\n"
                    + "  <li>Mot de passe : ${new_user_password}</li>\n"
                    + "  </ul>\n"
                    + "  </p>\n"
                    + " <p>Cordialement,</p>\n"
                    + " <p>${from_fullName}</p>\n"
                    + " <p>${from_positionTitle1}</p>\n"
                    + " <p>${from_positionTitle2}</p>\n"
                    + " <p>${from_positionTitle3}</p>\n"
                    + " <p>Restez connectés sur http://www.eniso.info </p>");
            w.setFooterEmbeddedImage("/Config/VisualIdentity/Institution.jpg");
            w.setPreferFormattedText(true);
            pu.persist(w);
        }
        {
            MailboxMessageFormat m = new MailboxMessageFormat();
            m.setName("DefaultExternalMail");
            m.setPreferFormattedText(true);
            m.setSubject("[ENISo][II] ${mail_subject}");
            m.setFormattedBody("<p>${if gender='F' then 'Chère' else 'Cher' end} ${civility} ${firstName},</p>"
                    + "\n ${mail_body}"
                    + "\n <p>Cordialement,</p>"
                    + "\n <p>${from_fullName}</p>"
                    + "\n <p>${from_positionTitle1}</p>"
                    + "\n <p>${from_positionTitle2}</p>"
                    + "\n <p>${from_positionTitle3}</p>"
                    + "\n <p>" + "Restez connectés sur http://www.eniso.info </p>"
            );
            m.setPlainBody("${if gender='F' then 'Chère' else 'Cher' end} ${civility} ${firstName},"
                    + "\n ${mail_body}"
                    + "\n Cordialement,"
                    + "\n ${from_fullName}"
                    + "\n ${from_positionTitle1}"
                    + "\n ${from_positionTitle2}"
                    + "\n ${from_positionTitle3}"
                    + "\n Restez connectés sur http://www.eniso.info"
            );
            m.setFooterEmbeddedImage("/Config/VisualIdentity/Institution.jpg");
            core.findOrCreate(m);
        }
        {
            MailboxMessageFormat m = new MailboxMessageFormat();
            m.setName("DefaultLocalMail");
            m.setPreferFormattedText(true);
            m.setSubject("${mail_subject}");
            m.setFormattedBody("<p>${if gender='F' then 'Chère' else 'Cher' end} ${civility} ${firstName},</p>"
                    + "\n ${mail_body}"
                    + "\n <p>Cordialement,</p>"
                    + "\n <p>${from_fullName}</p>"
                    + "\n <p>${from_positionTitle1}</p>"
                    + "\n <p>${from_positionTitle2}</p>"
                    + "\n <p>${from_positionTitle3}</p>"
                    + "\n <p>" + "Restez connectés sur http://www.eniso.info </p>");
            m.setPlainBody("${if gender='F' then 'Chère' else 'Cher' end} ${civility} ${firstName},"
                    + "\n ${mail_body}"
                    + "\n Cordialement"
                    + "\n ${from_fullName}"
                    + "\n ${from_positionTitle1}"
                    + "\n ${from_positionTitle2}"
                    + "\n ${from_positionTitle3}"
            );
            m.setFooterEmbeddedImage(null);
            m.setFooterEmbeddedImage("/Config/VisualIdentity/Institution.jpg");
            core.findOrCreate(m);
        }
        core.createRight(MailboxPluginSecurity.RIGHT_CUSTOM_ARTICLE_SEND_EXTERNAL_EMAIL, "Send External Email");
        core.createRight(MailboxPluginSecurity.RIGHT_CUSTOM_ARTICLE_SEND_INTERNAL_EMAIL, "Send Internal Email");
    }

    public boolean markRead(int mailboxReceivedId, boolean read) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        MailboxReceived msg = pu.findById(MailboxReceived.class, mailboxReceivedId);
        DateTime now = new DateTime();
        if (msg != null && msg.isRead() != read) {
            if (core.isCurrentSessionAdmin()) {
                msg.setRead(read);
                msg.setReadTime(now);
                pu.merge(msg);
                if (msg.isRead()) {
                    MailboxSent o = msg.getOutboxMessage();
                    if (o != null && !o.isRead()) {
                        o.setRead(true);
                        o.setReadTime(now);
                        pu.merge(o);
                    }
                }
                return true;
            } else {
                AppUser user = core.getCurrentUser();
                return UPA.getPersistenceUnit().invokePrivileged(new Action<Boolean>() {

                    @Override
                    public Boolean run() {
                        if (msg.getOwner() != null && user != null && user.getId() == msg.getOwner().getId()) {
                            msg.setRead(read);
                            msg.setReadTime(now);
                            pu.merge(msg);
                            if (msg.isRead()) {
                                MailboxSent o = msg.getOutboxMessage();
                                if (o != null && !o.isRead()) {
                                    o.setRead(true);
                                    o.setReadTime(now);
                                    pu.merge(o);
                                }
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
        return false;
    }

    public MailboxReceived findMailboxReceived(final int id) {
        return UPA.getPersistenceUnit().findById(MailboxReceived.class, id);
    }

    public MailboxSent findMailboxSent(final int id) {
        return UPA.getPersistenceUnit().findById(MailboxSent.class, id);
    }

    public List<MailboxReceived> findLocalReceivedMessagesThread(final int userId, final int maxCount, int threadId, final boolean unreadOnly, final MailboxFolder folder) {
        return findLocalReceivedMessages(userId, maxCount, threadId, true, unreadOnly, folder);
    }

    public List<MailboxReceived> findLocalReceivedMessages(final int userId, final int maxCount, final boolean unreadOnly, final MailboxFolder folder) {
        return findLocalReceivedMessages(userId, maxCount, -1, false, unreadOnly, folder);
    }

    public List<MailboxReceived> findLocalReceivedMessages(final int userId, final int maxCount, int threadId, boolean fullThread, final boolean unreadOnly, final MailboxFolder folder) {
        if (!core.isCurrentSessionAdmin() && core.getCurrentUserId() != userId) {
            throw new IllegalArgumentException("Not Allowed");
        }
        return UPA.getContext().invokePrivileged(new Action<List<MailboxReceived>>() {

            @Override
            public List<MailboxReceived> run() {
                String maxCountQL = "";//maxCount > 0 ? (" top " + maxCount + " ") : "";
                String unreadOnlyQL = unreadOnly ? (" and u.read=false ") : "";
                List<MailboxReceived> list = new ArrayList<>();
                String threadIdExpr = (threadId <= 0) ? "" : " and u.outboxMessage.threadId=" + threadId;
                switch (folder) {
                    case CURRENT: {
                        if (userId >= 0) {
                            String ownerExpr = "u.ownerId=:userId";
                            if (threadId > 0 && fullThread) {
                                ownerExpr = "(u.ownerId=:userId or u.senderId=:userId)";
                            }
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxReceived u where " + ownerExpr + " and u.deleted=false and u.archived=false " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getResultList();
                        } else {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxReceived u where and u.deleted=false and u.archived=false " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .getResultList();
                        }
                        break;
                    }
                    case DELETED: {
                        if (userId >= 0) {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + "  u from MailboxReceived u where u.ownerId=:userId and u.deleted=true " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getResultList();
                        } else {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + "  u from MailboxReceived u where u.deleted=true " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getResultList();
                        }
                        break;
                    }
                    case ARCHIVED: {
                        if (userId >= 0) {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxReceived u where u.ownerId=:userId and u.deleted=false and u.archived=true " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getResultList();
                        } else {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxReceived u where u.deleted=false and u.archived=true " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .getResultList();
                        }
                        break;
                    }
                }
                list = new ArrayList<MailboxReceived>(list);
                HashSet<Integer> visited = new HashSet<Integer>();
                for (Iterator<MailboxReceived> x = list.iterator(); x.hasNext(); ) {
                    MailboxReceived next = x.next();
                    if (next.getOwner().getId() != userId) {
                        //check if already visited
                        if (!visited.contains(next.getOutboxMessage().getId())) {
                            visited.add(next.getOutboxMessage().getId());
                        } else {
                            x.remove();
                        }
                    }
                }
                if (maxCount > 0) {
                    while (list.size() > maxCount) {
                        list.remove(list.size() - 1);
                    }
                }
                return list;
            }

        }, null);
    }

    public List<MailboxSent> findLocalSentMessages(final int userId, final int maxCount, int threadId, final boolean unreadOnly, final MailboxFolder folder) {
        if (!core.isCurrentSessionAdmin() && core.getCurrentUserId() != userId) {
            throw new IllegalArgumentException("Not Allowed");
        }
        return UPA.getContext().invokePrivileged(new Action<List<MailboxSent>>() {

            @Override
            public List<MailboxSent> run() {
                String maxCountQL = maxCount > 0 ? (" top " + maxCount + " ") : "";
                String unreadOnlyQL = unreadOnly ? (" and u.read=false ") : "";
                String threadIdExpr = (threadId <= 0) ? "" : " and u.threadId=" + threadId;
                switch (folder) {
                    case CURRENT: {
                        if (userId >= 0) {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxSent u where u.senderId=:userId and u.deleted=false and u.archived=false " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getResultList();
                        } else {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxSent u where and u.deleted=false and u.archived=false " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .getResultList();
                        }
                    }
                    case DELETED: {
                        if (userId >= 0) {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + "  u from MailboxSent u where u.senderId=:userId and u.deleted=true " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getResultList();
                        } else {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + "  u from MailboxSent u where u.deleted=true " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getResultList();
                        }
                    }
                    case ARCHIVED: {
                        if (userId >= 0) {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxSent u where u.senderId=:userId and u.deleted=false and u.archived=true " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getResultList();
                        } else {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxSent u where u.deleted=false and u.archived=true " + unreadOnlyQL + threadIdExpr + " order by u.sendTime desc")
                                    .getResultList();
                        }
                    }
                }
                return new ArrayList<>();
            }
        }, null);
        //    public List<MailboxReceived> loadInbox(int userId, int maxCount) {
        //        List<MailboxReceived> c = UPA.getPersistenceUnit()
        //                .createQuery("Select u from MailboxReceived u where u.ownerId=:userId order by u.sendTime desc")
        //                .setParameter("userId", userId).getEntityList();
        //        List<MailboxReceived> all = new ArrayList<>();
        //        if (maxCount <= 0) {
        //            all = c;
        //        } else {
        //            for (int i = 0; i < c.size() && i < maxCount; i++) {
        //                all.add(c.get(i));
        //            }
        //        }
        //        return all;
        //    }
        //
        //    public List<MailboxReceived> loadUnreadInbox(int userId, int maxCount) {
        //        List<MailboxReceived> c = UPA.getPersistenceUnit()
        //                .createQuery("Select u from MailboxReceived u where u.ownerId=:userId and u.read=false  and u.deleted=false and u.archived=false order by u.sendTime desc")
        //                .setParameter("userId", userId).getEntityList();
        //        List<MailboxReceived> all = new ArrayList<>();
        //        if (maxCount <= 0) {
        //            all = c;
        //        } else {
        //            for (int i = 0; i < c.size() && i < maxCount; i++) {
        //                all.add(c.get(i));
        //            }
        //        }
        //        return all;
        //    }
        //
    }

    public int getLocalUnreadInboxCount(final int userId) {
        if (!core.isCurrentSessionAdmin() && core.getCurrentUserId() != userId) {
            throw new IllegalArgumentException("Not Allowed");
        }
        return UPA.getContext().invokePrivileged(new Action<Integer>() {

            @Override
            public Integer run() {
                return ((Number) UPA.getPersistenceUnit()
                        .createQuery("Select Count(1) from MailboxReceived u where u.ownerId=:userId and u.read=false and u.deleted=false and u.archived=false")
                        .setParameter("userId", userId).getSingleValue()).intValue();
            }
        }, null);
    }

    GoMail createGoMail(MailData data) throws IOException {
        GoMail m = new GoMail();
        if (data.getProperties() != null) {
            m.getProperties().putAll(data.getProperties());
        }
        prepareSender(m);
        if (!data.isExternal()) {
            m.from(data.getFrom());
        }
        MailboxMessageFormat mailTemplate = data.getTemplateId() == null
                ? null
                ://getInternalMailTemplate():
                (MailboxMessageFormat) (UPA.getPersistenceUnit().findById(MailboxMessageFormat.class, data.getTemplateId()));
        Integer userId = null;
        if (data.getFrom() != null) {
            AppUser au = VrApp.getBean(CorePlugin.class).findUser(data.getFrom());
            userId = au == null ? null : au.getId();
        }
        if (userId == null) {
            AppUser au = VrApp.getBean(CorePlugin.class).getCurrentUser();
            userId = au == null ? null : au.getId();
        }
        prepareBody(
                data.getSubject(),
                data.getBody(),
                data.isRichText(),
                m,
                userId, mailTemplate);

        prepareRecipients(m, data.getEmailType(), data.getTo(), data.getToFilter(), !data.isExternal());
        if (!StringUtils.isEmpty(data.getCategory())) {
            m.getProperties().setProperty("header.X-App-Category", data.getCategory());
        }
        return m;
    }

    void prepareRecipients(GoMail m, RecipientType etype, String recipientProfiles, String filterExpression, boolean preferLogin) {
        MailboxPlugin emailPlugin = VrApp.getBean(MailboxPlugin.class);
        if (etype == null) {
            etype = RecipientType.TOEACH;
        }
        switch (etype) {
            case TO: {
                m.getProperties().setProperty(MailboxPlugin.HEADER_TO_PROFILES, recipientProfiles);
                emailPlugin.prepareToAll(m, recipientProfiles, filterExpression, preferLogin);
                break;
            }
            case TOEACH: {
                emailPlugin.prepareToEach(m, recipientProfiles, filterExpression, preferLogin);
                break;
            }
            case CC: {
                m.getProperties().setProperty(MailboxPlugin.HEADER_CC_PROFILES, recipientProfiles);
                emailPlugin.prepareCCAll(m, recipientProfiles, filterExpression, preferLogin);
                break;
            }
            case BCC: {
                m.getProperties().setProperty(MailboxPlugin.HEADER_BCC_PROFILES, recipientProfiles);
                emailPlugin.prepareBCCAll(m, recipientProfiles, filterExpression, preferLogin);
                break;
            }
        }
    }

    public void sendWelcomeEmail(List<AppUser> users, boolean applyFilter) {
        if (!UPA.getPersistenceUnit().getSecurityManager().isAllowedKey("Admin.SendWelcomeEmail")) {
            throw new IllegalArgumentException("Not Allowed");
        }
        for (AppUser u : users) {
            boolean ok = true;
            //should reload user
            u = core.findUser(u.getId());
            if (u != null) {
                if (applyFilter) {
                    if (u.isWelcomeSent() || u.isDeleted() || !u.isEnabled()) {
                        ok = false;
                    }
                }
                if (ok) {
                    try {
                        sendWelcomeEmail(u, null, notifPusher);
                    } catch (Exception exc) {
                        String email = u.getContact() == null ? null : u.getContact().getEmail();
                        VrApp.getBean(VrNotificationSession.class).publish(new VrNotificationEvent(SEND_WELCOME_MAIL_QUEUE, 60, null, "touser:" + u.getLogin() + " ; email=" + email + " : " + exc, null, Level.SEVERE));
                    }
                }
            }
        }
    }

    public void sendWelcomeEmail(boolean applyFilter) {
        if (!UPA.getPersistenceUnit().getSecurityManager().isAllowedKey("Admin.SendWelcomeEmail")) {
            throw new IllegalArgumentException("Not Allowed");
        }
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        for (AppUser u : core.findUsers()) {
            boolean ok = true;
            if (applyFilter) {
                if (u.isWelcomeSent() || u.isDeleted() || !u.isEnabled() || u.getConnexionCount() > 0) {
                    ok = false;
                }
            }
            if (ok) {
                sendWelcomeEmail(u, null, notifPusher);
            }
        }
    }

    private void sendWelcomeEmail(final AppUser u, Properties roProperties, GoMailListener listener) {
        try {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            if (!UPA.getPersistenceUnit().getSecurityManager().isAllowedKey("Admin.SendWelcomeEmail")) {
                throw new IllegalArgumentException("Not Allowed");
            }
            MailData m = new MailData();
            m.setExternal(true);
            m.setTo(u.getLogin());
            m.setRichText(true);
            m.setSubject(u.getLogin());
            m.setTemplateId(getWelcomeTemplate().getId());
            m.setCategory("Welcome");
            m.getProperties().setProperty("new_user_login", u.getLogin());
            if (StringUtils.isEmpty(u.getPasswordAuto())) {
                u.setPasswordAuto(VrPasswordStrategyRandom.INSTANCE.generatePassword(u.getContact()));
                u.setPassword(u.getPasswordAuto());
                UPA.getContext().invokePrivileged(TraceService.makeSilenced(new VoidAction() {
                    @Override
                    public void run() {
                        UPA.getPersistenceUnit().merge(u);
                    }
                }));
            }
            m.getProperties().setProperty("new_user_password", u.getPasswordAuto());
            GoMail email = createGoMail(m);
            sendExternalMail(email, roProperties, listener);
            u.setWelcomeSent(true);
            //cancel auto password!
            u.setPasswordAuto(null);
            UPA.getContext().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    UPA.getPersistenceUnit().merge(u);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(MailboxPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int sendLocalMail(GoMail email, final int sentMessageId, final boolean sendExternal) throws IOException {
        if (!UPA.getPersistenceUnit().getSecurityManager().isAllowedKey("sendLocalMail")) {
            throw new IllegalArgumentException("Not Allowed");
        }
        return sendMailByAgent(email, null, new GoMailListener() {
            @Override
            public void onBeforeSend(GoMailMessage mail) {
            }

            @Override
            public void onAfterSend(GoMailMessage m) {
                if (sendExternal) {
                    try {
                        GoMailMessage m2 = m.copy();
                        m2.setTo(createUsersEmails(m.to()));
                        m2.setCc(createUsersEmails(m.cc()));
                        m2.setBcc(createUsersEmails(m.bcc()));
                        sendExternalMail(m2.toGoMail(), null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onSendError(GoMailMessage mail, Throwable exc) {

            }
        }, new VrLocalMailAgent(email, sentMessageId));
    }

    public void sendLocalMail(MailboxSent message, Integer templateId, boolean persistOutbox) throws IOException {
        if (!UPA.getPersistenceUnit().getSecurityManager().isAllowedKey("sendLocalMail")) {
            throw new IllegalArgumentException("Not Allowed");
        }
        GoMail m = new GoMail();
        m.from(message.getSender().getLogin());
        message.setSendTime(new DateTime());
        boolean someSend = false;
        if (!StringUtils.isEmpty(message.getCcProfiles())) {
            m.namedDataSources().put("ccdatasource", createUsersEmailDatasource(message.getCcProfiles()));
            m.cc().add("${select login from ccdatasource}");
            someSend = true;
        }
        if (!StringUtils.isEmpty(message.getBccProfiles())) {
            m.namedDataSources().put("bccdatasource", createUsersEmailDatasource(message.getBccProfiles()));
            m.bcc().add("${select login from bccdatasource}");
            someSend = true;
        }
        if (!StringUtils.isEmpty(message.getToProfiles())) {
            if (message.isTemplateMessage()) {
                m.namedDataSources().put("todatasource", createUsersEmailDatasource(message.getToProfiles()));
                m.setExpandable(true);
                m.to("${login}");
            } else {
                m.namedDataSources().put("todatasource", createUsersEmailDatasource(message.getToProfiles()));
                m.to().add("${select login from todatasource}");
            }
            someSend = true;
        }
        if (StringUtils.isEmpty(message.getSubject())) {
            throw new IllegalArgumentException("Empty Subject");
        }
        if (StringUtils.isEmpty(message.getContent())) {
            throw new IllegalArgumentException("Empty Body");
        }
        if (!someSend) {
            throw new IllegalArgumentException("Missing destination");
        }
        MailboxMessageFormat mailTemplate = null;
        if (templateId != null) {
            mailTemplate = getMailTemplate(templateId);
        }
        if (mailTemplate != null) {
            prepareBody(message.getSubject(), message.getContent(), message.isRichText(), m, null, mailTemplate);
        } else {
            m.subject(message.getSubject());
            m.body().add(message.getContent(), GoMail.HTML_CONTENT_TYPE, message.isTemplateMessage());
        }
        m.setProperty(MailboxPlugin.HEADER_CATEGORY, message.getCategory());
        m.setProperty(MailboxPlugin.HEADER_TO_PROFILES, message.getToProfiles());
        m.setProperty(MailboxPlugin.HEADER_CC_PROFILES, message.getCcProfiles());
        if (message.getThreadId() > 0) {
            m.setProperty(MailboxPlugin.HEADER_THREAD_ID, String.valueOf(message.getThreadId()));
        }
        if (persistOutbox) {
            UPA.getPersistenceUnit().persist(message);
            if (message.getThreadId() <= 0) {
                message.setThreadId(message.getId());
                UPA.getPersistenceUnit().merge(message);
            }
        }
//        if (message.isExternalMessage()) {
//            try {
//
//                GoMail m2 = m.copy();
//                m2.setTo(createUsersEmails(m.to()));
//                m2.setCc(createUsersEmails(m.cc()));
//                m2.setBcc(createUsersEmails(m.bcc()));
//                m2.ssetToEachcreateUsersEmails(m.toeach()));
//                if (message.isTemplateMessage()) {
//
//                    MailboxMessageFormat mailTemplate = getExternalMailTemplate();
//                    CorePlugin core = VrApp.getBean(CorePlugin.class);
//                    prepareBody(
//                            message.getSubject(),
//                            message.getContent(),
//                            m2,
//                            message.getSender() == null
//                                    ? core.getCurrentUser().getId()
//                                    : message.getSender().getId(), mailTemplate);
//
//                }
//                sendExternalMail(m2, null, null);
//            } catch (Exception ex) {
//                Logger.getLogger(MailboxPlugin.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        sendLocalMail(m, message.getId(), message.isExternalMessage());
    }

    //    public void sendExternalMail(GoMail mail) throws IOException {
//        sendExternalMail(mail, null, null);
//    }
    public void sendExternalMail(GoMail email, Properties roProperties, GoMailListener listener) throws IOException {
        if (!UPA.getPersistenceUnit().getSecurityManager().isAllowedKey("sendExternalMail")) {
            throw new IllegalArgumentException("Not Allowed");
        }
//        email.setSimulate(true);
        int count = gomailFactory.createProcessor(new VrExternalMailAgent(gomailFactory), null).sendMessage(email, roProperties, listener);
        if (count <= 0) {
            throw new IllegalArgumentException("No valid Address");
        }
    }

    int sendMailByAgent(GoMail email, Properties roProperties, GoMailListener listener, GoMailAgent agent) throws IOException {
        GoMailModuleProcessor _processor = gomailFactory.createProcessor(agent, null);
        int count = _processor.sendMessage(email, roProperties, listener);
        if (count <= 0) {
            throw new IllegalArgumentException("No valid Address");
        }
        return count;
    }

    public String gomailToString(GoMail mail) throws IOException {
        return serializer.gomailToString(mail);
    }

    public GoMail gomailFromString(String mail) throws IOException {
        return serializer.gomailFromString(mail);
    }

    public void write(GoMail mail, GoMailFormat format, File file) throws IOException {
        serializer.write(mail, format, file);
    }

    public void write(GoMail mail, GoMailFormat format, OutputStreamSource file) throws IOException {
        serializer.write(mail, format, file);
    }

    public void write(GoMail mail, GoMailFormat format, OutputStream stream) throws IOException {
        serializer.write(mail, format, stream);
    }

    public GoMail read(GoMailFormat format, InputStream stream) throws IOException {
        return serializer.read(format, stream);
    }

    public GoMail read(GoMailFormat format, File file) throws IOException {
        return serializer.read(format, file);
    }

    public GoMail read(GoMailFormat format, InputStreamSource file) throws IOException {
        return serializer.read(format, file);
    }

    public Set<String> createUsersEmails(Set<String> recipientProfiles) {
        Set<String> rows = new HashSet<>();
        for (String t : recipientProfiles) {
            if (t.contains("@")) {
                rows.add(t);
            } else {
                rows.addAll(createUsersEmails(t));
            }
        }
        return rows;
    }

    public Set<String> createUsersEmails(String recipientProfiles) {
        Set<String> rows = new HashSet<>();
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        for (AppUser p : core.findUsersByProfileFilter(recipientProfiles, null)) {
            String email = p.getContact() == null ? null : p.getContact().getEmail();
            if (!StringUtils.isEmpty(email)) {
                rows.add(email);
            }
        }
        return rows;
    }

    public Set<String> createUsersLogins(String recipientProfiles) {
        Set<String> rows = new HashSet<>();
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        for (AppUser p : core.findUsersByProfileFilter(recipientProfiles, null)) {
            String login = p.getLogin();
            if (!StringUtils.isEmpty(login)) {
                rows.add(login);
            }
        }
        return rows;
    }

    public GoMailDataSource createUsersEmailDatasource(String recipientProfiles) {
        HashSet<String> allColumns = new HashSet<>(Arrays.asList("index", "id", "email", "login", "firstName", "lastName", "fullName", "civility", "department", "gender", "type", "profiles"));

        Map<String, AppEntityExtendedPropertiesProvider> beansOfTypeAppEntityExtendedPropertiesProvider = VrApp.getContext().getBeansOfType(AppEntityExtendedPropertiesProvider.class);
        for (Map.Entry<String, AppEntityExtendedPropertiesProvider> entrySet : beansOfTypeAppEntityExtendedPropertiesProvider.entrySet()) {
            Set<String> xtra = entrySet.getValue().getExtendedPropertyNames(AppUser.class);
            if (xtra != null) {
                for (String s : xtra) {
                    if (s != null && s.length() > 0) {
                        allColumns.add(s);
                    }
                }
            }
        }
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        List<String[]> rows = new ArrayList<>();
        for (AppUser p : core.findUsersByProfileFilter(recipientProfiles, null)) {
            String email = p.getContact().getEmail();
            if (!StringUtils.isEmpty(email)) {
                Map<String, Object> allValues = new HashMap<>();

                StringBuilder profilesString = new StringBuilder();
                for (AppProfile pp : core.findProfilesByUser(p.getId())) {
                    profilesString.append(";").append(pp.getName());
                }
                profilesString.append(";");
                allValues.put("index", (rows.size() + 1));
                allValues.put("id", (p.getId()));
                allValues.put("email", p.getContact().getEmail());
                allValues.put("login", p.getLogin());
                allValues.put("firstName", p.getContact().getFirstName());
                allValues.put("lastName", p.getContact().getLastName());
                allValues.put("fullName", p.resolveFullName());
                allValues.put("civility", p.getContact().getCivility() == null ? "" : p.getContact().getCivility().getName());
                allValues.put("department", p.getDepartment() == null ? "" : p.getDepartment().getName());
                allValues.put("gender", p.getContact().getGender() == null ? "" : p.getContact().getGender().getName());
                allValues.put("type", p.getType() == null ? "" : p.getType().getName());
                allValues.put("properties", profilesString.toString());

                for (Map.Entry<String, AppEntityExtendedPropertiesProvider> entrySet : beansOfTypeAppEntityExtendedPropertiesProvider.entrySet()) {
                    Map<String, Object> xtra = entrySet.getValue().getExtendedPropertyValues(p);
                    if (xtra != null) {
                        for (Map.Entry<String, Object> entrySet1 : xtra.entrySet()) {
                            if (entrySet1.getValue() != null) {
                                String k = entrySet1.getKey();
                                if (!allColumns.contains(k)) {
                                    throw new IllegalArgumentException("Column not found " + k);
                                }
                                allValues.put(k, entrySet1.getValue());
                            }
                        }
                    }
                }
                ArrayList<String> row = new ArrayList<>();
                for (String c : allColumns) {
                    Object v = allValues.get(c);
                    row.add(v == null ? null : String.valueOf(v));
                }
                rows.add(row.toArray(new String[row.size()]));
            }
        }
        return new StringsGoMailDataSource(rows.toArray(new String[rows.size()][]), allColumns.toArray(new String[allColumns.size()]));
    }

    public void prepareSender(GoMail email) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        email.from((String) core.getOrCreateAppPropertyValue("System.Email.Default.From", null, "vr.admin@gmail.com"));
        email.setCredentials(
                (String) core.getOrCreateAppPropertyValue("System.Email.Default.Credentials.User", null, "enisovr.admin"),
                (String) core.getOrCreateAppPropertyValue("System.Email.Default.Credentials.Password", null, "enisovr")
        );
        email.setSimulate(
                (Boolean) core.getOrCreateAppPropertyValue("System.Email.Default.Simulate", null, Boolean.TRUE)
        );
    }

    public void prepareToAll(GoMail m, String recipientProfiles, String filterExpression, boolean preferLogin) {
        m.namedDataSources().put("all", createUsersEmailDatasource(recipientProfiles));
        String mailAddr = preferLogin ? "login" : "email";
        m.to().add("${select " + mailAddr + " from all " + (StringUtils.isEmpty(filterExpression) ? "" : " where " + filterExpression) + "}");
    }

    public void prepareCCAll(GoMail m, String recipientProfiles, String filterExpression, boolean preferLogin) {
        m.namedDataSources().put("all", createUsersEmailDatasource(recipientProfiles));
        String mailAddr = preferLogin ? "login" : "email";
        m.cc().add("${select " + mailAddr + " from all " + (StringUtils.isEmpty(filterExpression) ? "" : " where " + filterExpression) + "}");
    }

    public void prepareBCCAll(GoMail m, String recipientProfiles, String filterExpression, boolean preferLogin) {
        m.namedDataSources().put("all", createUsersEmailDatasource(recipientProfiles));
        String mailAddr = preferLogin ? "login" : "email";
        m.bcc().add("${select " + mailAddr + " from all " + (StringUtils.isEmpty(filterExpression) ? "" : " where " + filterExpression) + "}");
    }

    public void prepareToEach(GoMail m, String recipientProfiles, String filterExpression, boolean preferLogin) {
        m.namedDataSources().put("all", createUsersEmailDatasource(recipientProfiles));
        m.repeatDatasource(GoMailModuleSerializer.deserializeDataSource(
                "all "
                        + (StringUtils.isEmpty(filterExpression) ? "" : " where " + filterExpression)
                )
        );
        String mailAddr = preferLogin ? "login" : "email";
        m.to().add("${" + mailAddr + "}");
    }

    public void prepareBody(String subject, String content, boolean richText, GoMail m, Integer userId, MailboxMessageFormat mailTemplate) {
        AppUser u = null;
        if (userId != null) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            u = core.findUser(userId);
        }
        if (content == null) {
            content = "";
        }
        if (subject == null) {
            subject = "";
        }
        String emailContent = null;
        String emailSubject = null;
        String footerEmbeddedImage = mailTemplate == null ? null : mailTemplate.getFooterEmbeddedImage();
        if (mailTemplate == null) {
            emailContent = content;
            emailSubject = subject;
        } else if (mailTemplate.isPreferFormattedText()) {
            emailContent = mailTemplate.getFormattedBody() == null ? "" : mailTemplate.getFormattedBody().replace("${mail_body}", content);
            emailSubject = mailTemplate.getSubject() == null ? "" : mailTemplate.getSubject().replace("${mail_subject}", subject);
        } else {
            emailContent = mailTemplate.getPlainBody() == null ? "" : mailTemplate.getPlainBody().replace("${mail_body}", content);
            emailSubject = mailTemplate.getSubject() == null ? "" : mailTemplate.getSubject().replace("${mail_subject}", subject);
        }
        if (StringUtils.isEmpty(emailContent)) {
            emailContent = "empty message";
        }
        if (StringUtils.isEmpty(emailSubject)) {
            emailSubject = "no subject";
        }

//                    if (u != null) {
//                emailContent = emailContent.replace("{from_fullName}", u.getFullName())
//                        .replace("{from_positionTitle1}", u.getPositionTitle1())
//                        .replace("{from_positionTitle2}", u.getPositionTitle2())
//                        .replace("{from_positionTitle3}", u.getPositionTitle3());
//            }
        if (u != null) {
            m.getProperties().setProperty("from_login", StringUtils.nonNull(u.getLogin()));
            m.getProperties().setProperty("from_type", StringUtils.nonNull(u.getType().getName()));
            m.getProperties().setProperty("from_fullName", StringUtils.nonNull(u.resolveFullName()));
            m.getProperties().setProperty("from_firstName", StringUtils.nonNull(u.getContact().getFirstName()));
            m.getProperties().setProperty("from_lastName", StringUtils.nonNull(u.getContact().getLastName()));
            m.getProperties().setProperty("from_positionTitle1", StringUtils.nonNull(u.getContact().getPositionTitle1()));
            m.getProperties().setProperty("from_positionTitle2", StringUtils.nonNull(u.getContact().getPositionTitle2()));
            m.getProperties().setProperty("from_positionTitle3", StringUtils.nonNull(u.getContact().getPositionTitle3()));
            m.getProperties().setProperty("from_gender", StringUtils.nonNull(u.getContact().getGender() == null ? null : u.getContact().getGender().getName()));
            m.getProperties().setProperty("from_department", StringUtils.nonNull(u.getDepartment() == null ? null : u.getDepartment().getName()));
        }
        m.subject(emailSubject);
        m.body().add(emailContent, richText ? GoMail.HTML_CONTENT_TYPE : GoMail.TEXT_CONTENT_TYPE, true);

        if (richText) {


                if (u != null) {
                    AppUser finalU = u;
                    UPA.getContext().invokePrivileged(new VoidAction() {
                        @Override
                        public void run() {
                            try {
                                if (!StringUtils.isEmpty(footerEmbeddedImage)) {
                                    CorePlugin fs = VrApp.getBean(CorePlugin.class);
                                    VirtualFileSystem ufs = fs.getUserFileSystem(finalU.getLogin());
                                    VirtualFileSystem allfs = fs.getRootFileSystem();
                                    if (ufs.exists(footerEmbeddedImage)) {
                                        m.footer("<img src=\"cid:part1\" alt=\"ATTACHMENT\"/>", GoMail.HTML_CONTENT_TYPE);
                                        VFile img = ufs.get(footerEmbeddedImage);
                                        m.attachment(img.readBytes(), img.probeContentType());
                                    } else if (allfs.exists(footerEmbeddedImage)) {
                                        m.footer("<img src=\"cid:part1\" alt=\"ATTACHMENT\"/>", GoMail.HTML_CONTENT_TYPE);
                                        VFile img = allfs.get(footerEmbeddedImage);
                                        m.attachment(img.readBytes(), img.probeContentType());
                                    }

                                }
                            } catch (IOException ex) {
                                Logger.getLogger(MailboxPlugin.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });

                }

        }
    }
//    public MailboxMessageFormat getExternalMailTemplate() {
//        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "DefaultExternalMail");
//    }
//
//    public MailboxMessageFormat getInternalMailTemplate() {
//        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "DefaultLocalMail");
//    }
//

    public MailboxMessageFormat getMailTemplate(int id) {
        return UPA.getPersistenceUnit().findById(MailboxMessageFormat.class, id);
    }

    public MailboxMessageFormat getWelcomeTemplate() {
        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "WelcomeMail");
    }

    public MailboxMessageFormat getExternalMailTemplate() {
        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "DefaultExternalMail");
    }

    public MailboxMessageFormat getInternalMailTemplate() {
        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "DefaultLocalMail");
    }

    public Set<String> findCategories() {
        return UPA.getPersistenceUnit().createQuery("Select distinct a.category from MailboxSent a order by a.category").getValueSet(0);
    }

    public List<String> autoCompleteCategory(String catName) {
        if (catName == null) {
            catName = "";
        }
        catName = catName.trim().toLowerCase();
        List<String> all = new ArrayList<>();
        for (String cat : findCategories()) {
            if (cat != null && cat.toLowerCase().contains(catName)) {
                all.add(cat);
            }
        }
        return all;
    }

    public List<String> autoCompleteCategoryExpression(String queryExpr) {
        if (queryExpr == null) {
            queryExpr = "";
        }
        List<String> all = new ArrayList<>();
        queryExpr = queryExpr.trim();
        if (!queryExpr.contains(" ") && !queryExpr.contains(",")
                && !queryExpr.contains("(") && !queryExpr.contains(")")
                && !queryExpr.contains("+") && !queryExpr.contains("|")
                && !queryExpr.contains("&")
                ) {
            all.addAll(autoCompleteCategory(queryExpr));
        }
        //reorder and remove duplicates
        return new ArrayList<String>(new TreeSet<String>(all));
    }

    public void sendExternalMail(ArticlesItem obj, String config) {
        if (obj == null) {
            return;
        }
        if (!UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(MailboxPluginSecurity.RIGHT_CUSTOM_ARTICLE_SEND_EXTERNAL_EMAIL)) {
            return;
        }
        SendExternalMailConfig c = VrUtils.parseJSONObject(config, SendExternalMailConfig.class);
        if (c == null) {
            c = new SendExternalMailConfig();
        }
        ArticlesItem a = obj;
        RecipientType etype = c.getEmailType();
        if (etype == null) {
            etype = RecipientType.TOEACH;
        }
        if (!StringUtils.isEmpty(a.getRecipientProfiles())) {
            MailData mailData = new MailData();
            mailData.setFrom(a.getSender() == null ? null : a.getSender().getLogin());
            mailData.setTemplateId(c.getTemplateId());
            mailData.setSubject(a.getSubject());
            mailData.setBody(a.getContent());
            mailData.setTo(a.getRecipientProfiles());
            mailData.setToFilter(a.getFilterExpression());
            mailData.setCategory("Article");
            mailData.setEmailType(etype);
            mailData.setRichText(true);
            mailData.setExternal(true);

//            GoMail m = new GoMail();
//
//            emailPlugin.prepareSender(m);
//
//            MailboxMessageFormat mailTemplate = getExternalMailTemplate();
//            emailPlugin.prepareBody(
//                    a.getSubject(),
//                    a.getContent(),
//                    m,
//                    a.getSender() == null
//                            ? core.getCurrentUser().getId()
//                            : a.getSender().getId(), mailTemplate);
//            prepareRecipients(m, etype, a.getRecipientProfiles(), a.getFilterExpression(), false);
            MailboxPlugin emailPlugin = VrApp.getBean(MailboxPlugin.class);
            try {
                GoMail m = emailPlugin.createGoMail(mailData);
                emailPlugin.sendExternalMail(m, null, new GoMailListener() {
                    @Override
                    public void onBeforeSend(GoMailMessage mail) {

                    }

                    @Override
                    public void onAfterSend(GoMailMessage mail) {
                        VrApp.getBean(VrNotificationSession.class).publish(new VrNotificationEvent(SEND_EXTERNAL_MAIL_QUEUE, 60, null, "to:" + mail.to() + " ; " + mail.subject(), null, Level.INFO));
                    }

                    @Override
                    public void onSendError(GoMailMessage mail, Throwable exc) {
                        VrApp.getBean(VrNotificationSession.class).publish(new VrNotificationEvent(SEND_EXTERNAL_MAIL_QUEUE, 60, null, "to:" + mail.to() + " ; " + mail.subject() + " : " + exc, null, Level.SEVERE));
                    }
                });
                VrApp.getBean(VrNotificationSession.class).publish(new VrNotificationEvent(SEND_EXTERNAL_MAIL_QUEUE, 60, null, "Send Finished", null, Level.INFO));

            } catch (IOException ex) {
                Logger.getLogger(CorePlugin.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        } else {
            throw new IllegalArgumentException("Could not send email to EVERY ONE");
        }
    }

    public void sendLocalMail(ArticlesItem obj, String config) {
        if (obj == null) {
            return;
        }
        if (!UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(MailboxPluginSecurity.RIGHT_CUSTOM_ARTICLE_SEND_INTERNAL_EMAIL)) {
            return;
        }
        ArticlesItem a = obj;
        SendExternalMailConfig c = VrUtils.parseJSONObject(config, SendExternalMailConfig.class);
        if (c == null) {
            c = new SendExternalMailConfig();
        }
        RecipientType type = c.getEmailType();
        if (type == null) {
            type = RecipientType.TO;
        }
        if (!StringUtils.isEmpty(a.getRecipientProfiles())) {
            MailData mailData = new MailData();
            mailData.setFrom(a.getSender() == null ? null : a.getSender().getLogin());
            mailData.setTemplateId(c.getTemplateId());
            mailData.setSubject(a.getSubject());
            mailData.setBody(a.getContent());
            mailData.setTo(a.getRecipientProfiles());
            mailData.setToFilter(a.getFilterExpression());
            mailData.setCategory("Article");
            mailData.setEmailType(type);
            MailboxPlugin mailboxPlugin = VrApp.getBean(MailboxPlugin.class);
            try {
                GoMail m = mailboxPlugin.createGoMail(mailData);
                mailboxPlugin.sendLocalMail(m, -1, false);
            } catch (Exception ex) {
                Logger.getLogger(MailboxPlugin.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        } else {
            throw new IllegalArgumentException("Could not send email to EVERY ONE");
        }
    }


}
