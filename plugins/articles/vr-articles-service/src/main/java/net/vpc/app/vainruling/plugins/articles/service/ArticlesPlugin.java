/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.service;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.EntityAction;
import net.vpc.app.vainruling.api.Install;
import net.vpc.app.vainruling.api.ProfilePatternFilter;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.plugins.articles.service.model.ArticlesDisposition;
import net.vpc.app.vainruling.plugins.articles.service.model.ArticlesFile;
import net.vpc.app.vainruling.plugins.articles.service.model.ArticlesItem;
import net.vpc.app.vainruling.plugins.articles.service.model.EmailType;
import net.vpc.app.vainruling.plugins.articles.service.model.FullArticle;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxMessageFormat;
import net.vpc.app.vainruling.plugins.filesystem.service.FileSystemPlugin;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.lib.gomail.GoMail;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import net.vpc.upa.impl.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author vpc
 */
@AppPlugin(version = "1.5", dependsOn = {"mailboxPlugin", "fileSystemPlugin"})
public class ArticlesPlugin {

    @Autowired
    CorePlugin core;
    @Autowired
    FileSystemPlugin fileSystemPlugin;

    public List<ArticlesFile> findArticlesFiles(int articleId) {
        return UPA.getPersistenceUnit().createQuery("Select u from ArticlesFile u where "
                + " u.articleId=:articleId"
                + " order by "
                + "  u.position asc"
        )
                .setParameter("articleId", articleId)
                .getEntityList();
    }

    public List<FullArticle> findFullArticlesByUserAndCategory(String login, String disposition) {
        List<FullArticle> all = new ArrayList<>();
        for (ArticlesItem a : findArticlesByUserAndCategory(login, disposition)) {
            FullArticle f = new FullArticle(a, findArticlesFiles(a.getId()));
            all.add(f);
        }
        return all;
    }

    public List<ArticlesItem> findArticlesByUserAndCategory(String login, String disposition) {
        List<ArticlesItem> all = UPA.getPersistenceUnit().createQuery("Select u from ArticlesItem u where "
                + " u.disposition.name=:disposition"
                + " and u.deleted=false"
                + " and u.archived=false"
                + " order by "
                + "  u.position desc"
                + ", u.important desc"
                + ", u.sendTime desc"
        )
                .setParameter("disposition", disposition)
                .getEntityList();

        return core.filterByProfilePattern(all, null, login, new ProfilePatternFilter<ArticlesItem>() {
            @Override
            public String getProfilePattern(ArticlesItem t) {
                return t.getRecipientProfiles();
            }
        });
    }

    @Install
    public void install() {
        ArticlesDisposition d;

        for (int i = 1; i <= 7; i++) {
            d = new ArticlesDisposition();
            d.setName("Main.Row" + i);
            d.setDescription("Page principale, Ligne " + i);
            core.insertIfNotFound(d);
        }

        d = new ArticlesDisposition();
        d.setName("Welcome");
        d.setDescription("Page de bienvenue");
        core.insertIfNotFound(d);
        
        d = new ArticlesDisposition();
        d.setName("News");
        d.setDescription("Page actualités");
        core.insertIfNotFound(d);
        
        d = new ArticlesDisposition();
        d.setName("Activities");
        d.setDescription("Page activités");
        core.insertIfNotFound(d);
        
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
            core.insertIfNotFound(m);
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
            core.insertIfNotFound(m);
        }
    }

    public MailboxMessageFormat getExternalMailTemplate() {
        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "DefaultExternalMail");
    }

    public MailboxMessageFormat getInternalMailTemplate() {
        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "DefaultLocalMail");
    }

//    @EntityActionList(entityType = ArticlesItem.class)
//    public String[] findArticleActions(){
//        return new String[]{"SendEmail"};
//    }
    @EntityAction(entityType = ArticlesItem.class, actionLabel = "email", actionStyle = "fa-envelope-o")
    public void sendExternalMail(Object obj) {
        if (obj == null || !(obj instanceof ArticlesItem)) {
            return;
        }
        ArticlesItem a = (ArticlesItem) obj;
        EmailType etype = a.getEmailType();
        if (etype == null) {
            etype = EmailType.TOEACH;
        }
        if (!Strings.isNullOrEmpty(a.getRecipientProfiles())) {
            GoMail m = new GoMail();
            MailboxPlugin emailPlugin = VrApp.getBean(MailboxPlugin.class);

            emailPlugin.prepareSender(m);

            MailboxMessageFormat mailTemplate = getExternalMailTemplate();
            emailPlugin.prepareBody(
                    a.getSubject(),
                    a.getContent(),
                    m,
                    a.getSender() == null
                            ? core.getUserSession().getUser().getId()
                            : a.getSender().getId(), mailTemplate);
            prepareRecipients(m, etype, a.getRecipientProfiles(), a.getFilterExpression(), false);
            try {
                emailPlugin.sendExternalMail(m);
            } catch (IOException ex) {
                Logger.getLogger(ArticlesPlugin.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        } else {
            throw new IllegalArgumentException("Could not send email to EVERY ONE");
        }
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

    @EntityAction(entityType = ArticlesItem.class, actionLabel = "inbox", actionStyle = "fa-envelope-square")
    public void sendLocalMail(Object obj) {
        if (obj == null || !(obj instanceof ArticlesItem)) {
            return;
        }
        ArticlesItem a = (ArticlesItem) obj;
        EmailType etype = a.getEmailType();
        if (etype == null) {
            etype = EmailType.TOEACH;
        }
        if (!Strings.isNullOrEmpty(a.getRecipientProfiles())) {
            GoMail m = new GoMail();
            MailboxPlugin mailboxPlugin = VrApp.getBean(MailboxPlugin.class);

            mailboxPlugin.prepareSender(m);
            m.from(a.getSender() == null ? null : a.getSender().getLogin());

            MailboxMessageFormat mailTemplate = getInternalMailTemplate();
            mailboxPlugin.prepareBody(
                    a.getSubject(),
                    a.getContent(),
                    m,
                    a.getSender() == null
                            ? core.getUserSession().getUser().getId()
                            : a.getSender().getId(), mailTemplate);

            prepareRecipients(m, etype, a.getRecipientProfiles(), a.getFilterExpression(), true);
            m.getProperties().setProperty("header.X-App-Category", "Article");
            try {
                mailboxPlugin.sendLocalMail(m, true);
            } catch (Exception ex) {
                Logger.getLogger(ArticlesPlugin.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        } else {
            throw new IllegalArgumentException("Could not send email to EVERY ONE");
        }
    }

    public void generateRSS(String login, String rss, OutputStream out) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ArticlesDisposition t = pu.findByMainField(ArticlesDisposition.class, "rss." + rss);
        List<ArticlesItem> articles = findArticlesByUserAndCategory(login, "rss." + rss);
        try {
            String feedType = "rss_2.0";
//            String fileName = "feed.xml";

            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType(feedType);

            feed.setTitle("ENISo Computer Engeneering Department");
            feed.setLink("http://www.eniso.info");
            feed.setDescription(t == null ? null : t.getDescription());

            List entries = new ArrayList();
            SyndEntry entry;
            SyndContent description;
            for (ArticlesItem art : articles) {
                entry = new SyndEntryImpl();
                entry.setTitle(art.getSubject());
                entry.setLink(art.getLinkURL() == null ? feed.getLink() : art.getLinkURL());
                entry.setPublishedDate(art.getSendTime());
                description = new SyndContentImpl();
                description.setType(MailboxPlugin.TYPE_HTML);
                description.setValue(art.getContent());
                entry.setDescription(description);
                entry.setAuthor(art.getSender() == null ? null : art.getSender().getFullName());
                entries.add(entry);
            }

            feed.setEntries(entries);

            Writer writer = new OutputStreamWriter(out);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
