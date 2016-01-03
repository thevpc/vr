/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxReceived;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxSent;
import net.vpc.app.vainruling.plugins.inbox.service.model.RecipientType;
import net.vpc.common.gomail.GoMail;
import net.vpc.common.gomail.GoMailBody;
import net.vpc.common.gomail.GoMailBodyContent;
import net.vpc.common.gomail.GoMailBodyList;
import net.vpc.common.gomail.GoMailBodyPath;
import net.vpc.common.gomail.GoMailBodyPosition;
import net.vpc.common.gomail.GoMailContext;
import net.vpc.common.gomail.modules.GoMailAgent;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.types.DateTime;

/**
 *
 * @author vpc
 */
class VrLocalMailAgent implements GoMailAgent {

    private GoMail email;
    private MailboxSent fms;
    boolean persistOutbox;
    int total;

    public VrLocalMailAgent(GoMail email, boolean persistOutbox) {
        this.email = email;
        this.persistOutbox = persistOutbox;
    }

    @Override
    public int sendExpandedMail(GoMail mail, Properties roProperties, GoMailContext expr) throws IOException {
        MailboxSent ms = null;
        if (fms == null && persistOutbox) {
            ms = new MailboxSent();
            ms.setSubject(email.subject());
            ms.setContent(bodyToString(email.body()));
            String prio = email.getProperties().getProperty(MailboxPlugin.HEADER_PRIORITY);
            ms.setImportant(prio != null);
            ms.setToProfiles(email.getProperties().getProperty(MailboxPlugin.HEADER_TO_PROFILES));
            ms.setCcProfiles(email.getProperties().getProperty(MailboxPlugin.HEADER_CC_PROFILES));
            ms.setBccProfiles(email.getProperties().getProperty(MailboxPlugin.HEADER_BCC_PROFILES));
            ms.setSender(email.from() == null ? null : email.from().contains("$") ? VrApp.getBean(UserSession.class).getUser() : VrApp.getBean(CorePlugin.class).findUser(email.from()));
            ms.setCategory(email.getProperties().getProperty(MailboxPlugin.HEADER_CATEGORY));
            ms.setSendTime(new DateTime());
            fms = ms;
            PersistenceUnit pu = UPA.getPersistenceUnit();
            pu.persist(ms);
        }
        int c = sendLocalExpandedMail(mail, fms);
        total += c;
        return c;
    }

    private int sendLocalExpandedMail(GoMail email, MailboxSent fms) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        int count = 0;
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
                count++;
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
                count++;
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
                count++;
            }
        }
        return count;
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
                } else if (b instanceof GoMailBodyPath) {
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
