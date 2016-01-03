/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.service;

import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxFolder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.AppEntityExtendedPropertiesProvider;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.Install;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppProfile;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.plugins.filesystem.service.FileSystemPlugin;
import net.vpc.app.vainruling.plugins.inbox.service.model.EmailType;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxMessageFormat;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxReceived;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxSent;
import net.vpc.common.streams.InputStreamSource;
import net.vpc.common.streams.OutputStreamSource;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.types.DateTime;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.common.gomail.GoMail;
import net.vpc.common.gomail.GoMailDataSource;
import net.vpc.common.gomail.GoMailFactory;
import net.vpc.common.gomail.GoMailFormat;
import net.vpc.common.gomail.GoMailListener;
import net.vpc.common.gomail.dataource.StringsGoMailDataSource;
import net.vpc.common.gomail.modules.GoMailAgent;
import net.vpc.common.gomail.modules.GoMailModuleProcessor;
import net.vpc.common.gomail.modules.GoMailModuleSerializer;
import net.vpc.upa.Action;

/**
 *
 * @author vpc
 */
@AppPlugin(version = "1.4", dependsOn = "fileSystemPlugin")
public class MailboxPlugin {

//    public void
    public static final String HEADER_PRIORITY = "header.X-Priority";
    public static final String HEADER_TO_PROFILES = "header.X-App-ToProfiles";
    public static final String HEADER_CC_PROFILES = "header.X-App-CCProfiles";
    public static final String HEADER_BCC_PROFILES = "X-App-BcCProfiles";
    public static final String HEADER_CATEGORY = "header.X-App-Category";
    public static final String TYPE_HTML = "text/html";

    public GoMailFactory gomail = new GoMailFactory();
//    public GoMailModuleProcessor externalMailProcessor = new GoMailModuleProcessor(new VrExternalMailAgent());
    public GoMailModuleSerializer serializer = new GoMailModuleSerializer();

    @Install
    public void installService() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        core.createRight("Custom.Site.Mailbox", "Mailbox");
        HashSet<String> goodProfiles = new HashSet<String>(Arrays.asList(
                "Teacher", "HeadOfDepartment", "DirectorOfStudies", "Director", "Student"
        ));
        for (AppProfile prof : core.findProfiles()) {
            if (goodProfiles.contains(prof.getName())) {
                core.addProfileRight(prof.getId(), "Custom.Site.Mailbox");
                core.addProfileRight(prof.getId(), "MailboxReceived.Load");
                core.addProfileRight(prof.getId(), "MailboxReceived.Persist");
                core.addProfileRight(prof.getId(), "MailboxReceived.Update");
                core.addProfileRight(prof.getId(), "MailboxReceived.Remove");
                core.addProfileRight(prof.getId(), "MailboxReceived.Navigate");
                core.addProfileRight(prof.getId(), "MailboxSent.Load");
                core.addProfileRight(prof.getId(), "MailboxSent.Persist");
                core.addProfileRight(prof.getId(), "MailboxSent.Update");
                core.addProfileRight(prof.getId(), "MailboxSent.Remove");
                core.addProfileRight(prof.getId(), "MailboxSent.Navigate");
            }
        }
    }

    public void markRead(int mailboxReceivedId, boolean read) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        MailboxReceived t = pu.findById(MailboxReceived.class, mailboxReceivedId);
        if (t != null) {
            DateTime now = new DateTime();
            if (t.isRead() != read) {
                t.setRead(read);
                t.setReadTime(now);
                pu.merge(t);
                if (t.isRead()) {
                    MailboxSent o = t.getOutboxMessage();
                    if (o != null) {
                        o.setRead(true);
                        o.setReadTime(now);
                    }
                }
            }

        }
    }

    public List<MailboxReceived> loadLocalMailbox(final int userId, final int maxCount, final boolean unreadOnly, final MailboxFolder folder) {
        return UPA.getContext().invokePrivileged(new Action<List<MailboxReceived>>() {

            @Override
            public List<MailboxReceived> run() {
                String maxCountQL = maxCount > 0 ? (" top " + maxCount + " ") : "";
                String unreadOnlyQL = unreadOnly ? (" and u.read=false ") : "";
                List<MailboxReceived> list = new ArrayList<>();
                switch (folder) {
                    case CURRENT: {
                        if (userId >= 0) {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxReceived u where u.ownerId=:userId and u.deleted=false and u.archived=false " + unreadOnlyQL + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getEntityList();
                        } else {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxReceived u where and u.deleted=false and u.archived=false " + unreadOnlyQL + " order by u.sendTime desc")
                                    .getEntityList();
                        }
                        break;
                    }
                    case DELETED: {
                        if (userId >= 0) {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + "  u from MailboxReceived u where u.ownerId=:userId and u.deleted=true " + unreadOnlyQL + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getEntityList();
                        } else {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + "  u from MailboxReceived u where u.deleted=true " + unreadOnlyQL + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getEntityList();
                        }
                        break;
                    }
                    case ARCHIVED: {
                        if (userId >= 0) {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxReceived u where u.ownerId=:userId and u.deleted=false and u.archived=true " + unreadOnlyQL + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getEntityList();
                        } else {
                            list = UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxReceived u where u.deleted=false and u.archived=true " + unreadOnlyQL + " order by u.sendTime desc")
                                    .getEntityList();
                        }
                        break;
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

    public List<MailboxSent> loadLocalOutbox(final int userId, final int maxCount, final boolean unreadOnly, final MailboxFolder folder) {
        return UPA.getContext().invokePrivileged(new Action<List<MailboxSent>>() {

            @Override
            public List<MailboxSent> run() {
                String maxCountQL = maxCount > 0 ? (" top " + maxCount + " ") : "";
                String unreadOnlyQL = unreadOnly ? (" and u.read=false ") : "";
                switch (folder) {
                    case CURRENT: {
                        if (userId >= 0) {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxSent u where u.senderId=:userId and u.deleted=false and u.archived=false " + unreadOnlyQL + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getEntityList();
                        } else {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxSent u where and u.deleted=false and u.archived=false " + unreadOnlyQL + " order by u.sendTime desc")
                                    .getEntityList();
                        }
                    }
                    case DELETED: {
                        if (userId >= 0) {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + "  u from MailboxSent u where u.senderId=:userId and u.deleted=true " + unreadOnlyQL + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getEntityList();
                        } else {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + "  u from MailboxSent u where u.deleted=true " + unreadOnlyQL + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getEntityList();
                        }
                    }
                    case ARCHIVED: {
                        if (userId >= 0) {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxSent u where u.senderId=:userId and u.deleted=false and u.archived=true " + unreadOnlyQL + " order by u.sendTime desc")
                                    .setParameter("userId", userId).getEntityList();
                        } else {
                            return UPA.getPersistenceUnit()
                                    .createQuery("Select " + maxCountQL + " u from MailboxSent u where u.deleted=false and u.archived=true " + unreadOnlyQL + " order by u.sendTime desc")
                                    .getEntityList();
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
        return UPA.getContext().invokePrivileged(new Action<Integer>() {

            @Override
            public Integer run() {
                return UPA.getPersistenceUnit()
                        .createQuery("Select u from MailboxReceived u where u.ownerId=:userId and u.read=false and u.deleted=false and u.archived=false")
                        .setParameter("userId", userId).getEntityList().size();
            }
        }, null);
    }

    public GoMail createGoMail(MailData data) throws IOException {
        GoMail m = new GoMail();

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
            AppUser au = VrApp.getBean(CorePlugin.class).getUserSession().getUser();
            userId = au == null ? null : au.getId();
        }
        prepareBody(
                data.getSubject(),
                data.getBody(),
                m,
                userId, mailTemplate);

        prepareRecipients(m, data.getEmailType(), data.getTo(), data.getToFilter(), !data.isExternal());
        if (!StringUtils.isEmpty(data.getCategory())) {
            m.getProperties().setProperty("header.X-App-Category", data.getCategory());
        }
        return m;
    }

    public void prepareRecipients(GoMail m, EmailType etype, String recipientProfiles, String filterExpression, boolean preferLogin) {
        MailboxPlugin emailPlugin = VrApp.getBean(MailboxPlugin.class);
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

    public void sendLocalMail(GoMail email, final boolean persistOutbox, final boolean sendExternal) throws IOException {
        sendMailByAgent(email, null, new GoMailListener() {
            @Override
            public void onBeforeSend(GoMail mail) {
            }

            @Override
            public void onAfterSend(GoMail m) {
                if (sendExternal) {
                    try {
                        GoMail m2 = m.copy();
                        m2.setTo(createUsersEmails(m.to()));
                        m2.setCc(createUsersEmails(m.cc()));
                        m2.setBcc(createUsersEmails(m.bcc()));
                        m2.setToeach(createUsersEmails(m.toeach()));
                        sendExternalMail(m2, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onSendError(GoMail mail, Throwable exc) {

            }
        }, new VrLocalMailAgent(email, persistOutbox));
    }

    

    
    
    public void sendLocalMail(MailboxSent message, Integer templateId, boolean persistOutbox) throws IOException {
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
            throw new IllegalArgumentException("Empty Sujet");
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
            prepareBody(message.getSubject(), message.getContent(), m, null, mailTemplate);
        } else {
            m.subject(message.getSubject());
            m.body().add(message.getContent(), MailboxPlugin.TYPE_HTML, message.isTemplateMessage());
        }
        m.setProperty(MailboxPlugin.HEADER_CATEGORY, message.getCategory());
        m.setProperty(MailboxPlugin.HEADER_TO_PROFILES, message.getToProfiles());
        m.setProperty(MailboxPlugin.HEADER_CC_PROFILES, message.getCcProfiles());
        if (persistOutbox) {
            UPA.getPersistenceUnit().persist(message);
        }
//        if (message.isExternalMessage()) {
//            try {
//
//                GoMail m2 = m.copy();
//                m2.setTo(createUsersEmails(m.to()));
//                m2.setCc(createUsersEmails(m.cc()));
//                m2.setBcc(createUsersEmails(m.bcc()));
//                m2.setToeach(createUsersEmails(m.toeach()));
//                if (message.isTemplateMessage()) {
//
//                    MailboxMessageFormat mailTemplate = getExternalMailTemplate();
//                    CorePlugin core = VrApp.getBean(CorePlugin.class);
//                    prepareBody(
//                            message.getSubject(),
//                            message.getContent(),
//                            m2,
//                            message.getSender() == null
//                                    ? core.getUserSession().getUser().getId()
//                                    : message.getSender().getId(), mailTemplate);
//
//                }
//                sendExternalMail(m2, null, null);
//            } catch (Exception ex) {
//                Logger.getLogger(MailboxPlugin.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        sendLocalMail(m, false, message.isExternalMessage());
    }

//    public void sendExternalMail(GoMail mail) throws IOException {
//        sendExternalMail(mail, null, null);
//    }
    public void sendExternalMail(GoMail email, Properties roProperties, GoMailListener listener) throws IOException {
//        email.setSimulate(true);

        int count = gomail.createProcessor(new VrExternalMailAgent()).send(email, roProperties, listener);
        if (count <= 0) {
            throw new IllegalArgumentException("No valid Address");
        }
    }

    public void sendMailByAgent(GoMail email, Properties roProperties, GoMailListener listener, GoMailAgent agent) throws IOException {
        GoMailModuleProcessor _processor = gomail.createProcessor(agent);
        int count = _processor.send(email, roProperties, listener);
        if (count <= 0) {
            throw new IllegalArgumentException("No valid Address");
        }
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
        for (AppUser p : core.resolveUsersByProfileFilter(recipientProfiles)) {
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
        for (AppUser p : core.resolveUsersByProfileFilter(recipientProfiles)) {
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
        for (AppUser p : core.resolveUsersByProfileFilter(recipientProfiles)) {
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
                allValues.put("fullName", p.getContact().getFullName());
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
        email.from((String) core.getOrCreateAppPropertyValue("System.Email.Default.From", null, "enisovr.admin@gmail.com"));
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

    public void prepareBody(String subject, String content, GoMail m, Integer userId, MailboxMessageFormat mailTemplate) {
        AppUser u = null;
        if (userId != null) {
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            u = core.findUser(userId);
        }
        String emailContent = null;
        String emailSubject = null;
        if (mailTemplate == null) {
            emailContent = content;
            emailSubject = subject;
        } else if (mailTemplate.isPreferFormattedText()) {
            emailContent = mailTemplate.getFormattedBody().replace("${mail_body}", content);
            emailSubject = mailTemplate.getSubject().replace("${mail_subject}", subject);
        } else {
            emailContent = mailTemplate.getPlainBody().replace("${mail_body}", content);
            emailSubject = mailTemplate.getSubject().replace("${mail_subject}", subject);
        }

//                    if (u != null) {
//                emailContent = emailContent.replace("{from_fullName}", u.getFullName())
//                        .replace("{from_positionTitle1}", u.getPositionTitle1())
//                        .replace("{from_positionTitle2}", u.getPositionTitle2())
//                        .replace("{from_positionTitle3}", u.getPositionTitle3());
//            }
        if (u != null) {
            m.getProperties().setProperty("from_login", StringUtils.nonnull(u.getLogin()));
            m.getProperties().setProperty("from_type", StringUtils.nonnull(u.getType().getName()));
            m.getProperties().setProperty("from_fullName", StringUtils.nonnull(u.getContact().getFullName()));
            m.getProperties().setProperty("from_firstName", StringUtils.nonnull(u.getContact().getFirstName()));
            m.getProperties().setProperty("from_lastName", StringUtils.nonnull(u.getContact().getLastName()));
            m.getProperties().setProperty("from_positionTitle1", StringUtils.nonnull(u.getContact().getPositionTitle1()));
            m.getProperties().setProperty("from_positionTitle2", StringUtils.nonnull(u.getContact().getPositionTitle2()));
            m.getProperties().setProperty("from_positionTitle3", StringUtils.nonnull(u.getContact().getPositionTitle3()));
            m.getProperties().setProperty("from_gender", StringUtils.nonnull(u.getContact().getGender() == null ? null : u.getContact().getGender().getName()));
            m.getProperties().setProperty("from_department", StringUtils.nonnull(u.getDepartment() == null ? null : u.getDepartment().getName()));
        }
        m.subject(emailSubject);
        m.body().add(emailContent, mailTemplate.isPreferFormattedText() ? "text/html" : "text/plain", true);
        boolean richText = mailTemplate.isPreferFormattedText();

        if (richText) {
            String footerEmbeddedImage = mailTemplate.getFooterEmbeddedImage();
            try {

                if (u != null) {
                    if (!StringUtils.isEmpty(footerEmbeddedImage)) {
                        FileSystemPlugin fs = VrApp.getBean(FileSystemPlugin.class);
                        VirtualFileSystem ufs = fs.getUserFileSystem(u.getLogin());
                        VirtualFileSystem allfs = fs.getFileSystem();
                        if (ufs.exists(footerEmbeddedImage)) {
                            m.footer("<img src=\"cid:part1\" alt=\"ATTACHMENT\"/>", "text/html");
                            VFile img = ufs.get(footerEmbeddedImage);
                            m.attachment(img.readBytes(), img.probeContentType());
                        } else if (allfs.exists(footerEmbeddedImage)) {
                            m.footer("<img src=\"cid:part1\" alt=\"ATTACHMENT\"/>", "text/html");
                            VFile img = allfs.get(footerEmbeddedImage);
                            m.attachment(img.readBytes(), img.probeContentType());
                        }
                    }

                }
            } catch (IOException ex) {
                Logger.getLogger(MailboxPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    public MailboxMessageFormat getMailTemplate(int id) {
        return UPA.getPersistenceUnit().findById(MailboxMessageFormat.class, id);
    }

    public MailboxMessageFormat getExternalMailTemplate() {
        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "DefaultExternalMail");
    }

    public MailboxMessageFormat getInternalMailTemplate() {
        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "DefaultLocalMail");
    }

}
