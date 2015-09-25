/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.service;

import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxFolder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.plugins.filesystem.service.FSUtils;
import net.vpc.app.vainruling.plugins.filesystem.service.FileSystemPlugin;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxMessageFormat;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxReceived;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxSent;
import net.vpc.app.vainruling.plugins.inbox.service.model.RecipientType;
import net.vpc.common.streams.InputStreamSource;
import net.vpc.common.streams.OutputStreamSource;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.impl.util.Strings;
import net.vpc.upa.types.DateTime;
import net.vpc.vfs.VFile;
import net.vpc.vfs.VirtualFileSystem;
import net.vpc.lib.gomail.GoMail;
import net.vpc.lib.gomail.GoMailBody;
import net.vpc.lib.gomail.GoMailBodyContent;
import net.vpc.lib.gomail.GoMailBodyList;
import net.vpc.lib.gomail.GoMailBodyPath;
import net.vpc.lib.gomail.GoMailBodyPosition;
import net.vpc.lib.gomail.GoMailContext;
import net.vpc.lib.gomail.GoMailDataSource;
import net.vpc.lib.gomail.GoMailFormat;
import net.vpc.lib.gomail.GoMailListener;
import net.vpc.lib.gomail.dataource.StringsGoMailDataSource;
import net.vpc.lib.gomail.modules.GoMailAgent;
import net.vpc.lib.gomail.modules.GoMailModuleProcessor;
import net.vpc.lib.gomail.modules.GoMailModuleSerializer;

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

    public GoMailModuleProcessor processor = new GoMailModuleProcessor();
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

    public List<MailboxReceived> loadLocalMailbox(int userId, int maxCount, boolean unreadOnly, MailboxFolder folder) {
        String maxCountQL = maxCount > 0 ? (" top " + maxCount + " ") : "";
        String unreadOnlyQL = unreadOnly ? (" and u.read=false ") : "";
        switch (folder) {
            case CURRENT: {
                if (userId >= 0) {
                    return UPA.getPersistenceUnit()
                            .createQuery("Select " + maxCountQL + " u from MailboxReceived u where u.ownerId=:userId and u.deleted=false and u.archived=false " + unreadOnlyQL + " order by u.sendTime desc")
                            .setParameter("userId", userId).getEntityList();
                } else {
                    return UPA.getPersistenceUnit()
                            .createQuery("Select " + maxCountQL + " u from MailboxReceived u where and u.deleted=false and u.archived=false " + unreadOnlyQL + " order by u.sendTime desc")
                            .getEntityList();
                }
            }
            case DELETED: {
                if (userId >= 0) {
                    return UPA.getPersistenceUnit()
                            .createQuery("Select " + maxCountQL + "  u from MailboxReceived u where u.ownerId=:userId and u.deleted=true " + unreadOnlyQL + " order by u.sendTime desc")
                            .setParameter("userId", userId).getEntityList();
                } else {
                    return UPA.getPersistenceUnit()
                            .createQuery("Select " + maxCountQL + "  u from MailboxReceived u where u.deleted=true " + unreadOnlyQL + " order by u.sendTime desc")
                            .setParameter("userId", userId).getEntityList();
                }
            }
            case ARCHIVED: {
                if (userId >= 0) {
                    return UPA.getPersistenceUnit()
                            .createQuery("Select " + maxCountQL + " u from MailboxReceived u where u.ownerId=:userId and u.deleted=false and u.archived=true " + unreadOnlyQL + " order by u.sendTime desc")
                            .setParameter("userId", userId).getEntityList();
                } else {
                    return UPA.getPersistenceUnit()
                            .createQuery("Select " + maxCountQL + " u from MailboxReceived u where u.deleted=false and u.archived=true " + unreadOnlyQL + " order by u.sendTime desc")
                            .getEntityList();
                }
            }
        }
        return new ArrayList<>();
    }

    public List<MailboxSent> loadLocalOutbox(int userId, int maxCount, boolean unreadOnly, MailboxFolder folder) {
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
    public int getLocalUnreadInboxCount(int userId) {
        return UPA.getPersistenceUnit()
                .createQuery("Select u from MailboxReceived u where u.ownerId=:userId and u.read=false and u.deleted=false and u.archived=false")
                .setParameter("userId", userId).getEntityList().size();
    }

    public void sendLocalMail(GoMail email, final boolean persistOutbox) {
        MailboxSent ms = null;
        if (persistOutbox) {
            ms = new MailboxSent();
            ms.setSubject(email.subject());
            ms.setContent(bodyToString(email.body()));
            String prio = email.getProperties().getProperty(MailboxPlugin.HEADER_PRIORITY);
            ms.setImportant(prio != null);
            ms.setToProfiles(email.getProperties().getProperty(MailboxPlugin.HEADER_TO_PROFILES));
            ms.setCcProfiles(email.getProperties().getProperty(MailboxPlugin.HEADER_CC_PROFILES));
            ms.setBccProfiles(email.getProperties().getProperty(MailboxPlugin.HEADER_BCC_PROFILES));
            ms.setSender(email.from() == null ? null
                    : email.from().contains("$")
                            ? VrApp.getBean(UserSession.class).getUser()
                            : VrApp.getBean(CorePlugin.class).findUser(email.from())
            );
            ms.setCategory(email.getProperties().getProperty(MailboxPlugin.HEADER_CATEGORY));
            ms.setSendTime(new DateTime());
            PersistenceUnit pu = UPA.getPersistenceUnit();
            pu.persist(ms);
        }
        final MailboxSent fms = ms;
        try {
            MailboxPlugin.this.sendExternalMail(email, null, null, new GoMailAgent() {

                @Override
                public void sendExpandedMail(GoMail mail, Properties roProperties, GoMailContext expr) throws IOException {
                    sendLocalExpandedMail(mail, fms);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(MailboxPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String bodyToString(GoMailBodyList body) {
        StringBuilder contentStr = new StringBuilder();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        try {
            writeGoMailBodyList(body, ps);
        } catch (IOException ex) {
            Logger.getLogger(MailboxPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        ps.flush();
        contentStr.append(baos.toString());
        return contentStr.toString();
    }

    public void writeGoMailBodyList(GoMailBodyList f, OutputStream stream) throws IOException {

        if (f != null) {
            PrintStream out = (stream instanceof PrintStream) ? ((PrintStream) stream) : new PrintStream(stream);
            for (GoMailBody b : f) {
                if (b.getPosition() == GoMailBodyPosition.ATTACHMENT) {
                    //ignore!
                } else {
                    if (b instanceof GoMailBodyPath) {
                        //
                    } else {
                        GoMailBodyContent c = (GoMailBodyContent) b;
                        if (b.getContentType().startsWith("text/")) {
                            String s = new String(c.getByteArray());
                            out.println();
                            out.println(s);
                        } else {
                            //ignore
                        }
                    }
                }
            }
        }

    }

    private void sendLocalExpandedMail(GoMail email, MailboxSent fms) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        CorePlugin core = VrApp.getBean(CorePlugin.class);

        String contentStr = bodyToString(email.body());
        String prio = email.getProperties().getProperty(MailboxPlugin.HEADER_PRIORITY);
        String toProfiles = email.getProperties().getProperty(MailboxPlugin.HEADER_TO_PROFILES);
        String ccProfiles = email.getProperties().getProperty(MailboxPlugin.HEADER_CC_PROFILES);

        AppUser fromUser = core.findUser(email.from());
        HashSet<String> visitedUsers = new HashSet<String>();
        String appCategory = email.getProperties().getProperty(MailboxPlugin.HEADER_CATEGORY);
        for (String to : email.to()) {
            if (!visitedUsers.contains(to)) {
                visitedUsers.add(to);
                MailboxReceived s = new MailboxReceived();
                s.setSubject(email.subject());
                s.setContent(contentStr);
                s.setToProfiles(toProfiles);
                s.setCcProfiles(ccProfiles);
                s.setImportant(prio != null);
                s.setSendTime(new DateTime());
                s.setSender(fromUser);
                s.setOwner(core.findUser(to));
                s.setRecipientType(RecipientType.TO);
                s.setCategory(appCategory);
                s.setOutboxMessage(fms);
                pu.persist(s);
            }
        }
        for (String cc : email.cc()) {
            if (!visitedUsers.contains(cc)) {
                visitedUsers.add(cc);
                MailboxReceived s = new MailboxReceived();
                s.setSubject(email.subject());
                s.setContent(contentStr);
                s.setToProfiles(toProfiles);
                s.setCcProfiles(ccProfiles);
                s.setImportant(prio != null);
                s.setSendTime(new DateTime());
                s.setSender(fromUser);
                s.setOwner(core.findUser(cc));
                s.setRecipientType(RecipientType.CC);
                s.setCategory(appCategory);
                s.setOutboxMessage(fms);
                pu.persist(s);
            }
        }
        for (String cc : email.bcc()) {
            if (!visitedUsers.contains(cc)) {
                visitedUsers.add(cc);
                MailboxReceived s = new MailboxReceived();
                s.setSubject(email.subject());
                s.setContent(contentStr);
                s.setToProfiles(toProfiles);
                s.setCcProfiles(ccProfiles);
                s.setImportant(prio != null);
                s.setSendTime(new DateTime());
                s.setSender(fromUser);
                s.setOwner(core.findUser(cc));
                s.setRecipientType(RecipientType.BCC);
                s.setCategory(appCategory);
                s.setOutboxMessage(fms);
                pu.persist(s);
            }
        }
    }

    public void sendLocalMail(MailboxSent message, boolean persistOutbox) {
        GoMail m = new GoMail();
        m.from(message.getSender().getLogin());
        message.setSendTime(new DateTime());
        if (!Strings.isNullOrEmpty(message.getCcProfiles())) {
            m.namedDataSources().put("ccdatasource", createUsersEmailDatasource(message.getCcProfiles()));
            m.cc().add("${select login from ccdatasource}");
        }
        if (!Strings.isNullOrEmpty(message.getBccProfiles())) {
            m.namedDataSources().put("bccdatasource", createUsersEmailDatasource(message.getBccProfiles()));
            m.bcc().add("${select login from bccdatasource}");
        }
        if (message.isTemplateMessage()) {
            m.namedDataSources().put("todatasource", createUsersEmailDatasource(message.getToProfiles()));
            m.setExpandable(true);
            m.to("${login}");
        } else {
            m.namedDataSources().put("todatasource", createUsersEmailDatasource(message.getToProfiles()));
            m.to().add("${select login from todatasource}");
        }
        m.subject(message.getSubject());
        m.body().add(message.getContent(), MailboxPlugin.TYPE_HTML, message.isTemplateMessage());
        m.setProperty(MailboxPlugin.HEADER_CATEGORY, message.getCategory());
        m.setProperty(MailboxPlugin.HEADER_TO_PROFILES, message.getToProfiles());
        m.setProperty(MailboxPlugin.HEADER_CC_PROFILES, message.getCcProfiles());
        if (persistOutbox) {
            UPA.getPersistenceUnit().persist(message);
        }
        if (message.isExternalMessage()) {
            try {
                sendExternalMail(m);
            } catch (Exception ex) {
                Logger.getLogger(MailboxPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        sendLocalMail(m, false);
    }

    public void sendExternalMail(GoMail mail) throws IOException {
        sendExternalMail(mail, null, null);
    }

    public void sendExternalMail(GoMail email, Properties roProperties, GoMailListener listener) throws IOException {
//        email.setSimulate(true);

        processor.send(email, roProperties, listener);
    }

    public void sendExternalMail(GoMail email, Properties roProperties, GoMailListener listener, GoMailAgent agent) throws IOException {
        GoMailModuleProcessor _processor = new GoMailModuleProcessor();
        if (agent != null) {
            _processor.setAgent(agent);
        }
        _processor.send(email, roProperties, listener);
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
            String email = p.getEmail();
            if (!Strings.isNullOrEmpty(email)) {
                Map<String, Object> allValues = new HashMap<>();

                StringBuilder profilesString = new StringBuilder();
                for (AppProfile pp : core.findProfilesByUser(p.getId())) {
                    profilesString.append(";").append(pp.getName());
                }
                profilesString.append(";");
                allValues.put("index", (rows.size() + 1));
                allValues.put("id", (p.getId()));
                allValues.put("email", p.getEmail());
                allValues.put("login", p.getLogin());
                allValues.put("firstName", p.getFirstName());
                allValues.put("lastName", p.getLastName());
                allValues.put("fullName", p.getFullName());
                allValues.put("civility", p.getCivitity() == null ? "" : p.getCivitity().getName());
                allValues.put("department", p.getDepartment() == null ? "" : p.getDepartment().getName());
                allValues.put("gender", p.getGender() == null ? "" : p.getGender().getName());
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
        m.to().add("${select " + mailAddr + " from all " + (Strings.isNullOrEmpty(filterExpression) ? "" : " where " + filterExpression) + "}");
    }

    public void prepareCCAll(GoMail m, String recipientProfiles, String filterExpression, boolean preferLogin) {
        m.namedDataSources().put("all", createUsersEmailDatasource(recipientProfiles));
        String mailAddr = preferLogin ? "login" : "email";
        m.cc().add("${select " + mailAddr + " from all " + (Strings.isNullOrEmpty(filterExpression) ? "" : " where " + filterExpression) + "}");
    }

    public void prepareBCCAll(GoMail m, String recipientProfiles, String filterExpression, boolean preferLogin) {
        m.namedDataSources().put("all", createUsersEmailDatasource(recipientProfiles));
        String mailAddr = preferLogin ? "login" : "email";
        m.bcc().add("${select " + mailAddr + " from all " + (Strings.isNullOrEmpty(filterExpression) ? "" : " where " + filterExpression) + "}");
    }

    public void prepareToEach(GoMail m, String recipientProfiles, String filterExpression, boolean preferLogin) {
        m.namedDataSources().put("all", createUsersEmailDatasource(recipientProfiles));
        m.repeatDatasource(GoMailModuleSerializer.deserializeDataSource(
                        "all "
                        + (Strings.isNullOrEmpty(filterExpression) ? "" : " where " + filterExpression)
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
        if (mailTemplate.isPreferFormattedText()) {
            emailContent = mailTemplate.getFormattedBody().replace("${mail_body}", content);
        } else {
            emailContent = mailTemplate.getPlainBody().replace("${mail_body}", content);
        }
        
//                    if (u != null) {
//                emailContent = emailContent.replace("{from_fullName}", u.getFullName())
//                        .replace("{from_positionTitle1}", u.getPositionTitle1())
//                        .replace("{from_positionTitle2}", u.getPositionTitle2())
//                        .replace("{from_positionTitle3}", u.getPositionTitle3());
//            }

        if(u!=null){
            m.getProperties().setProperty("from_login", StringUtils.nonnull(u.getLogin()));
            m.getProperties().setProperty("from_type", StringUtils.nonnull(u.getType().getName()));
            m.getProperties().setProperty("from_fullName", StringUtils.nonnull(u.getFullName()));
            m.getProperties().setProperty("from_firstName", StringUtils.nonnull(u.getFirstName()));
            m.getProperties().setProperty("from_lastName", StringUtils.nonnull(u.getLastName()));
            m.getProperties().setProperty("from_positionTitle1", StringUtils.nonnull(u.getPositionTitle1()));
            m.getProperties().setProperty("from_positionTitle2", StringUtils.nonnull(u.getPositionTitle2()));
            m.getProperties().setProperty("from_positionTitle3", StringUtils.nonnull(u.getPositionTitle3()));
            m.getProperties().setProperty("from_gender", StringUtils.nonnull(u.getGender()==null?null:u.getGender().getName()));
            m.getProperties().setProperty("from_department", StringUtils.nonnull(u.getDepartment()==null?null:u.getDepartment().getName()));
        }
        emailSubject = mailTemplate.getSubject().replace("${mail_subject}", subject);
        m.subject(emailSubject);
        m.body().add(emailContent, mailTemplate.isPreferFormattedText() ? "text/html" : "text/plain", true);
        boolean richText = mailTemplate.isPreferFormattedText();

        if (richText) {
            String footerEmbeddedImage = mailTemplate.getFooterEmbeddedImage();
            try {

                if (u != null) {
                    if (!Strings.isNullOrEmpty(footerEmbeddedImage)) {
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
}
