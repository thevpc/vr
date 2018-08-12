package net.vpc.app.vainruling.core.service;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedOutput;
import net.vpc.app.vainruling.core.service.cache.EntityCache;
import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.ProfilePatternFilter;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.ListValueMap;
import net.vpc.common.util.MapList;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.upa.*;
import net.vpc.upa.expressions.UserExpression;
import net.vpc.upa.types.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import net.vpc.app.vainruling.core.service.model.content.ArticlesDisposition;
import net.vpc.app.vainruling.core.service.model.content.ArticlesDispositionGroup;
import net.vpc.app.vainruling.core.service.model.content.ArticlesDispositionGroupType;
import net.vpc.app.vainruling.core.service.model.content.ArticlesFile;
import net.vpc.app.vainruling.core.service.model.content.ArticlesItem;
import net.vpc.app.vainruling.core.service.model.content.ArticlesItemStrict;
import net.vpc.app.vainruling.core.service.model.content.ArticlesProperty;
import net.vpc.app.vainruling.core.service.model.content.FullArticle;
import net.vpc.app.vainruling.core.service.util.AppVersion;
import net.vpc.common.util.MutableDate;

class CorePluginBodyContentManager extends CorePluginBody {

    @Override
    public void onInstall() {
        CorePlugin core = getContext().getCorePlugin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppVersion vv = core.getAppVersion();
        core.getOrCreateAppPropertyValue("System.App.Title", null, vv.getLongName());
        core.getOrCreateAppPropertyValue("System.App.Description", null, vv.getLongName()+" Web Site");
        core.getOrCreateAppPropertyValue("System.App.Keywords", null, "vr");
        core.getOrCreateAppPropertyValue("System.App.Title.Major.Main", null, "My");
        core.getOrCreateAppPropertyValue("System.App.Title.Major.Secondary", null, "App");
        core.getOrCreateAppPropertyValue("System.App.Title.Minor.Main", null, "My");
        core.getOrCreateAppPropertyValue("System.App.Title.Minor.Secondary", null, "App");
        core.getOrCreateAppPropertyValue("System.App.Copyrights.Date", null, vv.getBuildDate());
        core.getOrCreateAppPropertyValue("System.App.Copyrights.Author.Name", null, vv.getAuthor());
        core.getOrCreateAppPropertyValue("System.App.Copyrights.Author.URL", null, vv.getAuthorUrl());
        core.getOrCreateAppPropertyValue("System.App.Copyrights.Author.Affiliation", null, vv.getAuthorAffiliation());
        core.getOrCreateAppPropertyValue("System.App.GotoWelcomeText", null, "My Space");
        core.getOrCreateAppPropertyValue("System.App.GotoLoginText", null, "My Space");

        findOrCreateDisposition("Welcome", "Home", "Home");
        findOrCreateDisposition("Testimonials", "Testimonials", "Testimonials");
        findOrCreateDisposition("Testimonials.Header", "Testimonials", null);
        findOrCreateDisposition("Services", "Services", "Services");
        findOrCreateDisposition("Services.Header", "Services", null);
        findOrCreateDisposition("Featured", "Featured", "Featured");
        findOrCreateDisposition("Featured.Header", "Featured", null);
        findOrCreateDisposition("About", "About", "About");
        findOrCreateDisposition("About.Header", "About", null);
        findOrCreateDisposition("News", "News", "Press");
        findOrCreateDisposition("News.Header", "Press", null);
        findOrCreateDisposition("Activities", "Activities", "Activities");
        findOrCreateDisposition("Activities.Header", "Activities", null);
        if (core.findFullArticlesByDisposition(null, "About.Header").isEmpty()) {
            core.save("ArticlesItem", new ArticlesItem("About", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About").isEmpty()) {
            core.save("ArticlesItem", new ArticlesItem("Nos Valeurs", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About").isEmpty()) {
            core.save("ArticlesItem", new ArticlesItem("Reinvention", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About").isEmpty()) {
            core.save("ArticlesItem", new ArticlesItem("Actualite", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About").isEmpty()) {
            core.save("ArticlesItem", new ArticlesItem("APropos", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "News.Header").isEmpty()) {
            ArticlesItem a = new ArticlesItem();
            a.setSubject("Press");
            a.setDisposition(findArticleDisposition("News.Header"));
            core.save("ArticlesItem", a);
        }
        if (core.findFullArticlesByDisposition(null, "Activities.Header").isEmpty()) {
            ArticlesItem a = new ArticlesItem();
            a.setSubject("Activities");
            a.setDisposition(findArticleDisposition("Activities.Header"));
            core.save("ArticlesItem", a);
        }
        if (core.findFullArticlesByDisposition(null, "Featured.Header").isEmpty()) {
            ArticlesItem a = new ArticlesItem();
            a.setSubject("Featured");
            a.setDisposition(findArticleDisposition("Featured.Header"));
            core.save("ArticlesItem", a);
        }
        if (core.findFullArticlesByDisposition(null, "Activities.Header").isEmpty()) {
            ArticlesItem a = new ArticlesItem();
            a.setSubject("Activities");
            a.setDisposition(findArticleDisposition("Activities.Header"));
            core.save("ArticlesItem", a);
        }
        if (core.findFullArticlesByDisposition(null, "Testimonials.Header").isEmpty()) {
            ArticlesItem a = new ArticlesItem();
            a.setSubject("Testimonials");
            a.setDisposition(findArticleDisposition("Testimonials.Header"));
            core.save("ArticlesItem", a);
        }
        if (core.findFullArticlesByDisposition(null, "Services.Header").isEmpty()) {
            ArticlesItem a = new ArticlesItem();
            a.setSubject("Services");
            a.setDisposition(findArticleDisposition("Services.Header"));
            core.save("ArticlesItem", a);
        }
        AppProfile publisher = core.findOrCreateProfile("Publisher");
        for (String right : CorePluginSecurity.getEntityRights(pu.getEntity("ArticlesItem"), true, true, true, false, false)) {
            core.addProfileRight(publisher.getId(), right);
        }
        for (String right : CorePluginSecurity.getEntityRights(pu.getEntity("ArticlesFile"), true, true, true, false, false)) {
            core.addProfileRight(publisher.getId(), right);
        }
    }

    public ArticlesItem findArticle(int articleId) {
        return UPA.getPersistenceUnit().findById(ArticlesItem.class, articleId);
    }

    public ArticlesDisposition findArticleDisposition(int articleDispositionId) {
        return UPA.getPersistenceUnit().findById(ArticlesDisposition.class, articleDispositionId);
    }

    public List<ArticlesDispositionGroupType> findArticleDispositionGroupTypes() {
        EntityCache entityCache = getContext().getCacheService().get(ArticlesDispositionGroupType.class);
        return entityCache.getValues();
    }

    public List<ArticlesDispositionGroup> findArticleDispositionGroups(int siteType) {
        final EntityCache entityCache = getContext().getCacheService().get(ArticlesDispositionGroup.class);
        return entityCache.getProperty("findArticleDispositionGroups:" + siteType, new Action<List<ArticlesDispositionGroup>>() {
            @Override
            public List<ArticlesDispositionGroup> run() {
                return UPA.getPersistenceUnit()
                        .createQuery("Select u from ArticlesDispositionGroup u where u.typeId=:typeId order by u.index,u.title")
                        .setParameter("typeId", siteType)
                        .getResultList();
            }
        });
    }

    public ArticlesDispositionGroup findArticleDispositionGroup(String name) {
        final EntityCache entityCache = getContext().getCacheService().get(ArticlesDispositionGroup.class);
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
        final EntityCache entityCache = getContext().getCacheService().get(ArticlesDisposition.class);
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
        if (a == null) {
            return null;
        }
        String aname = a.getLinkText();
        String aurl = a.getLinkURL();
        String acss = a.getLinkClassStyle();
        List<ArticlesFile> att = new ArrayList<>();
        if (!StringUtils.isEmpty(aname) || !StringUtils.isEmpty(aurl)) {
            if (StringUtils.isEmpty(aname)) {
                aname = VrUtils.getURLName(aurl);
            }
            if (StringUtils.isEmpty(aname)) {
                aname = "NoName";
            }
            ArticlesFile baseArt = new ArticlesFile();
            baseArt.setId(-1);
            baseArt.setName(aname);
            boolean added = false;
            if (aurl != null && aurl.startsWith("/")) {
                VFile vFile = getRootFileSystem0().get(aurl);
                if (vFile.isDirectory()) {
                    for (VFile file : vFile.listFiles()) {
                        ArticlesFile baseArt2 = new ArticlesFile();
                        baseArt2.setId(-1);
                        baseArt2.setName(file.getName());
                        baseArt2.setPath(file.getPath());
                        baseArt2.setStyle(acss);
                        att.add(baseArt2);
                    }
                    added = true;
                }
            }
            if (!added) {
                baseArt.setPath(aurl);
                baseArt.setStyle(acss);
                att.add(baseArt);
            }
        }
        List<ArticlesFile> c = findArticlesFiles(a.getId());
        if (c != null) {
            for (ArticlesFile articlesFile : c) {
                boolean added = false;
                aurl = articlesFile.getPath();
                if (aurl != null && aurl.startsWith("/")) {
                    VFile vFile = getRootFileSystem0().get(aurl);
                    if (vFile.isDirectory()) {
                        for (VFile file : vFile.listFiles()) {
                            ArticlesFile baseArt2 = new ArticlesFile();
                            baseArt2.setId(-1);
                            baseArt2.setName(file.getName());
                            baseArt2.setPath(file.getPath());
                            baseArt2.setStyle(acss);
                            att.add(baseArt2);
                        }
                        added = true;
                    }
                }
                if (!added) {
                    att.add(articlesFile);
                }
            }
        }
        FullArticle f = new FullArticle(new ArticlesItemStrict(a), att);
        return f;
    }

    private VirtualFileSystem getRootFileSystem0() {
        return UPA.getContext().invokePrivileged(new Action<VirtualFileSystem>() {
            @Override
            public VirtualFileSystem run() {
                return getContext().getCorePlugin().getRootFileSystem();
            }
        });
    }

    public void markArticleVisited(int articleId) {
//        TraceService.runSilenced(new Runnable() {
//            @Override
//            public void run() {
        UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                Entity entity = pu.getEntity(ArticlesItem.class);
                Document document = entity.createDocument();
                document.setObject("visitCount", new UserExpression("visitCount+1"));
                entity.createUpdateQuery()
                        .setValues(document)
                        .byId(articleId)
                        .execute();
            }
        });
//            }
//        });
    }

    @Deprecated
    public List<FullArticle> findFullArticlesByCategory(String disposition) {
        return findFullArticlesByDisposition(null, disposition);
    }

    public List<FullArticle> findFullArticlesByDisposition(String group, String disposition) {
        if (group == null) {
            UserSession userSession = getContext().getCorePlugin().getCurrentSession();
            group = userSession == null ? null : userSession.getSelectedSiteFilter();
        }
        if (StringUtils.isEmpty(group)) {
            group = "";
        }
        ArticlesDispositionGroup g = findArticleDispositionGroup(group);
        if (g == null) {
            g = findArticleDispositionGroup("II");
        }
        return findFullArticlesByDisposition(g == null ? -1 : g.getId(), true, disposition);
    }

    public List<FullArticle> findFullArticlesByDisposition(int dispositionGroupId, boolean includeNoDept, final String disposition) {
        AppUser u = getContext().getCorePlugin().getCurrentUser();
        return findFullArticlesByUserAndDisposition(u == null ? null : u.getLogin(), dispositionGroupId, includeNoDept, disposition);
    }

    public List<FullArticle> findFullArticlesByAnonymousDisposition(int dispositionGroupId, boolean includeNoDept, final String disposition) {
        return findFullArticlesByUserAndDisposition(null, dispositionGroupId, includeNoDept, disposition);
    }

    protected List<FullArticle> findFullArticlesByUserAndDisposition(final String login, int dispositionGroupId, boolean includeNoDept, final String disposition) {
        return UPA.getContext().invokePrivileged(new Action<List<FullArticle>>() {

            @Override
            public List<FullArticle> run() {
                List<FullArticle> all = new ArrayList<>();
                List<ArticlesItem> articles = findArticlesByUserAndCategory(login, dispositionGroupId, includeNoDept, disposition);
                int[] articleIds = new int[articles.size()];
                for (int i = 0; i < articleIds.length; i++) {
                    articleIds[i] = articles.get(i).getId();
                }
                ListValueMap<Integer, ArticlesFile> articlesFilesMap = new ListValueMap<Integer, ArticlesFile>();
                for (ArticlesFile articlesFile : findArticlesFiles(articleIds)) {
                    articlesFilesMap.put(articlesFile.getArticle().getId(), articlesFile);
                }
                for (ArticlesItem a : articles) {
                    ArticlesDisposition d = a.getDisposition();
                    if (d != null) {
                        int periodCalendarType = -1;
                        if (d.getPeriodType() != null && d.getMaxPeriod() > 0) {
                            switch (d.getPeriodType()) {
                                case DAY:
                                case WEEK:
                                case MONTH:
                                case YEAR: {
                                    periodCalendarType = d.getPeriodType().getCalendarId();
                                    break;
                                }
                            }
                        }
                        if (periodCalendarType > 0) {
                            DateTime dd = a.getSendTime();
                            MutableDate d2 = new MutableDate().addField(periodCalendarType, -d.getMaxPeriod());
                            if (dd.compareTo(d2.getDateTime()) < 0) {
                                continue;
                            }
                        }

                    }
                    String aname = a.getLinkText();
                    String aurl = a.getLinkURL();
                    String acss = a.getLinkClassStyle();
                    List<ArticlesFile> att = new ArrayList<>();
                    if (!StringUtils.isEmpty(aname) || !StringUtils.isEmpty(aurl)) {
                        if (StringUtils.isEmpty(aname)) {
                            aname = VrUtils.getURLName(aurl);
                        }
                        if (StringUtils.isEmpty(aname)) {
                            aname = "NoName";
                        }
                        ArticlesFile baseArt = new ArticlesFile();
                        baseArt.setId(-1);
                        baseArt.setName(aname);
                        baseArt.setPath(aurl);
                        baseArt.setStyle(acss);
                        boolean added = false;
                        if (aurl != null && aurl.startsWith("/")) {
                            VFile vFile = getRootFileSystem0().get(aurl);
                            if (vFile.isDirectory()) {
                                for (VFile file : vFile.listFiles()) {
                                    ArticlesFile baseArt2 = new ArticlesFile();
                                    baseArt2.setId(-1);
                                    baseArt2.setName(file.getName());
                                    baseArt2.setPath(file.getPath());
                                    baseArt2.setStyle(acss);
                                    att.add(baseArt2);
                                }
                                added = true;
                            }
                        }
                        if (!added) {
                            att.add(baseArt);
                        }
                    }

                    List<ArticlesFile> c = articlesFilesMap.get(a.getId());
                    if (c != null) {
                        for (ArticlesFile articlesFile : c) {
                            boolean added = false;
                            aurl = articlesFile.getPath();
                            if (aurl != null && aurl.startsWith("/")) {
                                VFile vFile = getRootFileSystem0().get(aurl);
                                if (vFile.isDirectory()) {
                                    for (VFile file : vFile.listFiles()) {
                                        ArticlesFile baseArt2 = new ArticlesFile();
                                        baseArt2.setId(-1);
                                        baseArt2.setName(file.getName());
                                        baseArt2.setPath(file.getPath());
                                        baseArt2.setStyle(acss);
                                        att.add(baseArt2);
                                    }
                                    added = true;
                                }
                            }
                            if (!added) {
                                att.add(articlesFile);
                            }
                        }
                    }
                    FullArticle f = new FullArticle(new ArticlesItemStrict(a), att);
                    all.add(f);
                }
                return all;
            }

        }, null);
    }

    protected List<ArticlesItem> findArticlesByUserAndCategory(String login, int dispositionGroupId, boolean includeNoDept, String... dispositions) {
        if (dispositions.length == 0) {
            return Collections.EMPTY_LIST;
        }
        StringBuilder queryStr = new StringBuilder("Select u from ArticlesItem u where ");
        queryStr.append(" u.deleted=false ");
        queryStr.append(" and u.archived=false");
        queryStr.append(" and (");
        Map<String, Object> disps = new HashMap<>();
        for (int i = 0; i < dispositions.length; i++) {
            String disposition = dispositions[i];
            if (i > 0) {
                queryStr.append(" or ");
            }
            if (login == null) {
                queryStr.append(" (u.disposition.name=:disposition" + i + " and u.disposition.requiresAuthentication=false)");
            } else {
                queryStr.append(" u.disposition.name=:disposition" + i);
            }
            disps.put("disposition" + i, disposition);
        }
        queryStr.append(" )");
        if (dispositionGroupId >= 0) {
            if (includeNoDept) {
                queryStr.append(" and (u.dispositionGroupId=:dispositionGroupId or u.dispositionGroupId=null)");
                disps.put("dispositionGroupId", dispositionGroupId);
            } else {
                queryStr.append(" and (u.dispositionGroupId=:dispositionGroupId)");
                disps.put("dispositionGroupId", dispositionGroupId);
            }
        }

        queryStr.append(" order by "
                + "  u.position"
                + ", u.important desc"
                + ", u.sendTime desc");

        Query query = UPA.getPersistenceUnit().createQuery(queryStr.toString()).setParameters(disps);
        List<ArticlesItem> all = query.getResultList();

        return getContext().getCorePlugin().filterByProfilePattern(all, null, login, new ProfilePatternFilter<ArticlesItem>() {
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

    public String getRSS(String rss) {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        getRSS(rss, o);
        return new String(o.toByteArray());
    }

    public void getRSS(String rss, OutputStream out) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        ArticlesDisposition t = pu.findByMainField(ArticlesDisposition.class, "rss." + rss);
        List<FullArticle> articles = findFullArticlesByDisposition(-1, true, "rss." + rss);
        try {
            String feedType = "rss_2.0";
//            String fileName = "feed.xml";

            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType(feedType);

            feed.setTitle(t == null ? "Vr RSS " + rss : t.getTitle());
            UPA.getContext().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    feed.setLink((String) getContext().getCorePlugin().getOrCreateAppPropertyValue("System.PublicWebSite", null, "https://github.com/thevpc/vr"));
                }
            });
            feed.setDescription(t == null ? null : t.getDescription());

            List<SyndEntry> entries = new ArrayList<>();
            SyndEntry entry;
            SyndContent description;
            for (FullArticle art : articles) {
                entry = new SyndEntryImpl();
                entry.setTitle(art.getSubject());
                entry.setLink(art.getLinkURL() == null ? feed.getLink() : art.getLinkURL());
                entry.setPublishedDate(art.getPublishTime());
                description = new SyndContentImpl();
                description.setType("text/html;charset=UTF-8");
                description.setValue(art.getContent());
                entry.setDescription(description);
                entry.setAuthor(art.getUser() == null ? null : art.getUser().getFullName());
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

//    @EntityActionList(entityType = ArticlesItem.class)
//    public String[] findArticleActions(){
//        return new String[]{"SendEmail"};
//    }
    public String getArticlesProperty(String value) {
        return getArticlesProperties().get(value);
    }

    public String getArticlesPropertyOrCreate(String value, String defaultValue) {
        String s = getArticlesProperties().get(value);
        if (StringUtils.isEmpty(s) && !StringUtils.isEmpty(defaultValue)) {
            PersistenceUnit pu = UPA.getPersistenceUnit();
            pu.invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    ArticlesProperty p = new ArticlesProperty();
                    p.setName(value);
                    p.setValue(defaultValue);
                    pu.persist(p);
                }
            });
            s = defaultValue;
        }
        return s;
    }

    public Map<String, String> getArticlesProperties() {
        final EntityCache entityCache = getContext().getCacheService().get(ArticlesProperty.class);
        Map<String, String> m = entityCache.getProperty("getArticlesProperties", new Action<Map<String, String>>() {
            @Override
            public Map<String, String> run() {
                Map<String, String> m = new HashMap<String, String>();
                MapList<Integer, ArticlesProperty> values = entityCache.getValues();
                for (ArticlesProperty u : values) {
                    String key = u.getName();
                    if (!StringUtils.isEmpty(key)) {
                        m.put(key, u.getValue());
                    }
                }
                return m;
            }
        });
        return m;
    }

    public ArticlesDisposition findOrCreateDisposition(String name, String description, String actionName) {
        ArticlesDisposition disposition = new ArticlesDisposition();
        disposition.setEnabled(true);
        disposition.setName(name);
        disposition.setDescription(description);
        disposition.setActionName(actionName);
        return getContext().getCorePlugin().findOrCreate(disposition);
    }

}
