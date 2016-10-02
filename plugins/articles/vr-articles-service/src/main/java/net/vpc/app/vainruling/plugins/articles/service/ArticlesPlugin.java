/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.service;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import net.vpc.app.vainruling.core.service.*;
import net.vpc.app.vainruling.core.service.cache.CacheService;
import net.vpc.app.vainruling.core.service.cache.EntityCache;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.notification.VrNotificationEvent;
import net.vpc.app.vainruling.core.service.notification.VrNotificationManager;
import net.vpc.app.vainruling.core.service.notification.VrNotificationSession;
import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;
import net.vpc.app.vainruling.core.service.util.ProfilePatternFilter;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.articles.service.model.*;
import net.vpc.app.vainruling.plugins.inbox.service.MailData;
import net.vpc.app.vainruling.plugins.inbox.service.MailboxPlugin;
import net.vpc.app.vainruling.plugins.inbox.service.model.MailboxMessageFormat;
import net.vpc.common.gomail.GoMail;
import net.vpc.common.gomail.GoMailListener;
import net.vpc.common.gomail.GoMailMessage;
import net.vpc.common.gomail.RecipientType;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.ListValueMap;
import net.vpc.common.util.MapList;
import net.vpc.upa.Action;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.Query;
import net.vpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@AppPlugin
public class ArticlesPlugin {
    @Autowired
    private CacheService cacheService;

    public static final String SEND_EXTERNAL_MAIL_QUEUE = "sendExternalMailQueue";
    @Autowired
    CorePlugin core;

    public static ArticlesPlugin get() {
        return VrApp.getBean(ArticlesPlugin.class);
    }

    public ArticlesItem findArticle(int articleId) {
        return UPA.getPersistenceUnit().findById(ArticlesItem.class,articleId);
    }

    public ArticlesDisposition findArticleDisposition(int articleDispositionId) {
        return UPA.getPersistenceUnit().findById(ArticlesDisposition.class,articleDispositionId);
    }

    public ArticlesDispositionGroup findArticleDispositionGroup(String name) {
        final EntityCache entityCache = cacheService.get(ArticlesDispositionGroup.class);
        Map<String, ArticlesDispositionGroup> m = entityCache.getProperty("findArticleDispositionGroup", new Action<Map<String, ArticlesDispositionGroup>>() {
            @Override
            public Map<String, ArticlesDispositionGroup> run() {
                Map<String, ArticlesDispositionGroup> m = new HashMap<String, ArticlesDispositionGroup>();
                MapList<Integer, ArticlesDispositionGroup> values = entityCache.getValues();
                for (ArticlesDispositionGroup u : values) {
                    String key = u.getName();
                    if (!StringUtils.isEmpty(key)) {
                        m.put(key, u);
                    }
                }
                return m;
            }
        });
        return m.get(name);
    }

    public ArticlesDisposition findArticleDisposition(String name) {
        final EntityCache entityCache = cacheService.get(ArticlesDisposition.class);
        Map<String, ArticlesDisposition> m = entityCache.getProperty("findArticleDispositionByName", new Action<Map<String, ArticlesDisposition>>() {
            @Override
            public Map<String, ArticlesDisposition> run() {
                Map<String, ArticlesDisposition> m = new HashMap<String, ArticlesDisposition>();
                MapList<Integer, ArticlesDisposition> values = entityCache.getValues();
                for (ArticlesDisposition u : values) {
                    String key = u.getName();
                    if (!StringUtils.isEmpty(key)) {
                        m.put(key, u);
                    }
                }
                return m;
            }
        });
        return m.get(name);
    }

    public List<ArticlesFile> findArticlesFiles(int articleId) {
        return UPA.getPersistenceUnit().createQuery("Select u from ArticlesFile u where "
                        + " u.articleId=:articleId"
                        + " order by "
                        + "  u.position asc"
        )
                .setParameter("articleId", articleId)
                .getResultList();
    }

    public List<ArticlesFile> findArticlesFiles(int[] articleIds) {
        if (articleIds.length == 0) {
            return Collections.emptyList();
        }
        StringBuilder ids_string = new StringBuilder();
        ids_string.append(articleIds[0]);
        for (int i = 1; i < articleIds.length; i++) {
            ids_string.append(",");
            ids_string.append(articleIds[i]);
        }
        return UPA.getPersistenceUnit().createQuery("Select u from ArticlesFile u where "
                        + " u.articleId in(" + ids_string + ")"
                        + " order by "
                        + "  u.position asc"
        )
                .getResultList();
    }

    public FullArticle findFullArticle(int id) {
        //invoke privileged to find out article not created by self.
        //when using non privileged mode, users can see SOLELY articles they have created
        ArticlesItem a = UPA.getPersistenceUnit().invokePrivileged(new Action<ArticlesItem>() {
            @Override
            public ArticlesItem run() {
                return findArticle(id);
            }
        });
        if(a==null){
            return null;
        }
        String aname = a.getLinkText();
        String aurl = a.getLinkURL();
        String acss = a.getLinkClassStyle();
        List<ArticlesFile> att = new ArrayList<>();
        if (!StringUtils.isEmpty(aname) || !StringUtils.isEmpty(aurl)) {
            ArticlesFile baseArt = new ArticlesFile();
            baseArt.setId(-1);
            baseArt.setName(StringUtils.isEmpty(aname) ? "NoName" : aname);
            baseArt.setPath(aurl);
            baseArt.setStyle(acss);
            att.add(baseArt);
        }
        List<ArticlesFile> c = findArticlesFiles(a.getId());
        if (c != null) {
            att.addAll(c);
        }
        FullArticle f = new FullArticle(a, att);
        return f;
    }

    public List<FullArticle> findFullArticlesByUserAndCategory(final String login, int dispositionGroupId,boolean includeNoDept, final String disposition) {
        return UPA.getContext().invokePrivileged(new Action<List<FullArticle>>() {

            @Override
            public List<FullArticle> run() {
                List<FullArticle> all = new ArrayList<>();
                List<ArticlesItem> articles = findArticlesByUserAndCategory(login, dispositionGroupId, includeNoDept,disposition);
                int[] articleIds = new int[articles.size()];
                for (int i = 0; i < articleIds.length; i++) {
                    articleIds[i] = articles.get(i).getId();
                }
                ListValueMap<Integer, ArticlesFile> articlesFilesMap = new ListValueMap<Integer, ArticlesFile>();
                for (ArticlesFile articlesFile : findArticlesFiles(articleIds)) {
                    articlesFilesMap.put(articlesFile.getArticle().getId(), articlesFile);
                }
                for (ArticlesItem a : articles) {
                    String aname = a.getLinkText();
                    String aurl = a.getLinkURL();
                    String acss = a.getLinkClassStyle();
                    List<ArticlesFile> att = new ArrayList<>();
                    if (!StringUtils.isEmpty(aname) || !StringUtils.isEmpty(aurl)) {
                        ArticlesFile baseArt = new ArticlesFile();
                        baseArt.setId(-1);
                        baseArt.setName(StringUtils.isEmpty(aname) ? "NoName" : aname);
                        baseArt.setPath(aurl);
                        baseArt.setStyle(acss);
                        att.add(baseArt);
                    }

                    List<ArticlesFile> c = articlesFilesMap.get(a.getId());
                    if (c != null) {
                        att.addAll(c);
                    }
                    FullArticle f = new FullArticle(a, att);
                    all.add(f);
                }
                return all;
            }

        }, null);
    }

    public List<ArticlesItem> findArticlesByUserAndCategory(String login, int dispositionGroupId,boolean includeNoDept,String ... dispositions) {
        if(dispositions.length==0){
            return Collections.EMPTY_LIST;
        }
        StringBuilder queryStr=new StringBuilder("Select u from ArticlesItem u where ");
        queryStr.append(" u.deleted=false ");
        queryStr.append(" and u.archived=false");
        queryStr.append(" and (");
        Map<String,Object> disps=new HashMap<>();
        for (int i = 0; i < dispositions.length; i++) {
            String disposition = dispositions[i];
            if(i>0){
                queryStr.append(" or ");
            }
            queryStr.append(" u.disposition.name=:disposition"+i);
            disps.put("disposition"+i,disposition);
        }
        queryStr.append(" )");
        if(includeNoDept) {
            queryStr.append(" and (u.dispositionGroupId=:dispositionGroupId or u.dispositionGroupId=null)");
            disps.put("dispositionGroupId",dispositionGroupId);
        }else{
            queryStr.append(" and (u.dispositionGroupId=:dispositionGroupId)");
            disps.put("dispositionGroupId",dispositionGroupId);
        }

        queryStr.append(" order by "
                + "  u.position"
                + ", u.important desc"
                + ", u.sendTime desc");

        Query query = UPA.getPersistenceUnit().createQuery(queryStr.toString()).setParameters(disps);
        List<ArticlesItem> all = query.getResultList();

        return core.filterByProfilePattern(all, null, login, new ProfilePatternFilter<ArticlesItem>() {
            @Override
            public String getProfilePattern(ArticlesItem t) {
                AppUser s = t.getSender();
                String author = s == null ? null : s.getLogin();
                String p = t.getRecipientProfiles();
                if (StringUtils.isEmpty(p)) {
                    return p;
                }
                if (StringUtils.isEmpty(author)) {
                    return p;
                }
                return "( " + p + " ) , " + author;
            }
        });
    }

    @Start
    private void start() {
        VrApp.getBean(VrNotificationManager.class).register(SEND_EXTERNAL_MAIL_QUEUE, SEND_EXTERNAL_MAIL_QUEUE, 200);
    }

    @Install
    private void install() {
        core.createRight("Custom.Article.SendExternalEmail", "Send External Email");
        core.createRight("Custom.Article.SendInternalEmail", "Send Internal Email");
        ArticlesDisposition d;

        for (int i = 1; i <= 7; i++) {
            d = new ArticlesDisposition();
            d.setName("Main.Row" + i);
            d.setDescription("Page principale, Ligne " + i);
            core.findOrCreate(d);
        }

        d = new ArticlesDisposition();
        d.setName("Welcome");
        d.setDescription("Page de bienvenue");
        core.findOrCreate(d);

        d = new ArticlesDisposition();
        d.setName("News");
        d.setDescription("Page actualités");
        core.findOrCreate(d);

        d = new ArticlesDisposition();
        d.setName("Activities");
        d.setDescription("Page activités");
        core.findOrCreate(d);

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
        {
            AppProfile p = new AppProfile();
            p.setCode("Publisher");
            p.setName("Publisher");
            p = core.findOrCreate(p);
            core.addProfileRight(p.getId(), "ArticlesItem.DefaultEditor");
            core.addProfileRight(p.getId(), "ArticlesItem.Load");
            core.addProfileRight(p.getId(), "ArticlesItem.Navigate");
            core.addProfileRight(p.getId(), "ArticlesItem.Persist");
            core.addProfileRight(p.getId(), "ArticlesItem.Update");
            core.addProfileRight(p.getId(), "ArticlesItem.Remove");
            core.addProfileRight(p.getId(), "ArticlesFile.DefaultEditor");
            core.addProfileRight(p.getId(), "ArticlesFile.Load");
            core.addProfileRight(p.getId(), "ArticlesFile.Navigate");
            core.addProfileRight(p.getId(), "ArticlesFile.Persist");
            core.addProfileRight(p.getId(), "ArticlesFile.Update");
            core.addProfileRight(p.getId(), "ArticlesFile.Remove");
        }
    }

    public MailboxMessageFormat getExternalMailTemplate() {
        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "DefaultExternalMail");
    }

    public MailboxMessageFormat getInternalMailTemplate() {
        return UPA.getPersistenceUnit().findByMainField(MailboxMessageFormat.class, "DefaultLocalMail");
    }

    public void sendExternalMail(ArticlesItem obj, String config) {
        if (obj == null) {
            return;
        }
        if (!UPA.getPersistenceGroup().getSecurityManager().isAllowedKey("Custom.Article.SendExternalEmail")) {
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
//                            ? core.getUserSession().getUser().getId()
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
                        VrApp.getBean(VrNotificationSession.class).publish(new VrNotificationEvent(ArticlesPlugin.SEND_EXTERNAL_MAIL_QUEUE, 60, null, "to:" + mail.to() + " ; " + mail.subject(), null, Level.INFO));
                    }

                    @Override
                    public void onSendError(GoMailMessage mail, Throwable exc) {
                        VrApp.getBean(VrNotificationSession.class).publish(new VrNotificationEvent(ArticlesPlugin.SEND_EXTERNAL_MAIL_QUEUE, 60, null, "to:" + mail.to() + " ; " + mail.subject() + " : " + exc, null, Level.SEVERE));
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(ArticlesPlugin.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        } else {
            throw new IllegalArgumentException("Could not send email to EVERY ONE");
        }
    }

//    @EntityActionList(entityType = ArticlesItem.class)
//    public String[] findArticleActions(){
//        return new String[]{"SendEmail"};
//    }

    public void sendLocalMail(ArticlesItem obj, String config) {
        if (obj == null) {
            return;
        }
        if (!UPA.getPersistenceGroup().getSecurityManager().isAllowedKey("Custom.Article.SendInternalEmail")) {
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
                mailboxPlugin.sendLocalMail(m, true, false);
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
        List<FullArticle> articles = findFullArticlesByUserAndCategory(login, -1,true,"rss." + rss);
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
            for (FullArticle art : articles) {
                entry = new SyndEntryImpl();
                entry.setTitle(art.getSubject());
                entry.setLink(art.getLinkURL() == null ? feed.getLink() : art.getLinkURL());
                entry.setPublishedDate(art.getPublishTime());
                description = new SyndContentImpl();
                description.setType(GoMail.HTML_CONTENT_TYPE);
                description.setValue(art.getContent());
                entry.setDescription(description);
                entry.setAuthor(art.getUser() == null ? null : art.getUser().getContact().getFullName());
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

    public static class SendExternalMailConfig {

        private RecipientType emailType;
        private Integer templateId;

        public RecipientType getEmailType() {
            return emailType;
        }

        public void setEmailType(RecipientType emailType) {
            this.emailType = emailType;
        }

        public Integer getTemplateId() {
            return templateId;
        }

        public void setTemplateId(Integer templateId) {
            this.templateId = templateId;
        }

    }
}
