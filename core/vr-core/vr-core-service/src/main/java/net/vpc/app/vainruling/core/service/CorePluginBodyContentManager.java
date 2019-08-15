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
import java.util.logging.Level;

import net.vpc.app.vainruling.core.service.model.content.AppArticleDisposition;
import net.vpc.app.vainruling.core.service.model.content.AppArticleDispositionGroup;
import net.vpc.app.vainruling.core.service.model.content.AppArticleDispositionBundle;
import net.vpc.app.vainruling.core.service.model.content.AppArticleFile;
import net.vpc.app.vainruling.core.service.model.content.AppArticle;
import net.vpc.app.vainruling.core.service.model.content.AppArticleStrict;
import net.vpc.app.vainruling.core.service.model.content.AppArticleProperty;
import net.vpc.app.vainruling.core.service.model.content.FullArticle;
import net.vpc.app.vainruling.core.service.util.AppVersion;
import net.vpc.common.io.PathInfo;
import net.vpc.common.util.MutableDate;

class CorePluginBodyContentManager extends CorePluginBody {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CorePluginBodyContentManager.class.getName());

    @Override
    public void onInstall() {
        CorePlugin core = getContext().getCorePlugin();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppVersion vv = core.getAppVersion();
        core.getOrCreateAppPropertyValue("System.App.Title", null, vv.getLongName());
        core.getOrCreateAppPropertyValue("System.App.Description", null, vv.getLongName() + " Web Site");
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

        findOrCreateArticleDisposition("Welcome", "Home", "Home");
        findOrCreateArticleDisposition("Testimonials", "Testimonials", "Testimonials");
        findOrCreateArticleDisposition("Testimonials.Header", "Testimonials", null);
        findOrCreateArticleDisposition("Services", "Services", "Services");
        findOrCreateArticleDisposition("Services.Header", "Services", null);
        findOrCreateArticleDisposition("Featured", "Featured", "Featured");
        findOrCreateArticleDisposition("Featured.Header", "Featured", null);
        findOrCreateArticleDisposition("About", "About", "About");
        findOrCreateArticleDisposition("About.Header", "About", null);
        findOrCreateArticleDisposition("News", "News", "Press");
        findOrCreateArticleDisposition("News.Header", "Press", null);
        findOrCreateArticleDisposition("Activities", "Activities", "Activities");
        findOrCreateArticleDisposition("Activities.Header", "Activities", null);
        if (core.findFullArticlesByDisposition(null, "About.Header").isEmpty()) {
            core.save(new AppArticle("About", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About").isEmpty()) {
            core.save(new AppArticle("Nos Valeurs", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About").isEmpty()) {
            core.save(new AppArticle("Reinvention", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About").isEmpty()) {
            core.save(new AppArticle("Actualite", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About").isEmpty()) {
            core.save(new AppArticle("APropos", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "News.Header").isEmpty()) {
            AppArticle a = new AppArticle();
            a.setSubject("Press");
            a.setDisposition(findArticleDisposition("News.Header"));
            core.save(a);
        }
        if (core.findFullArticlesByDisposition(null, "Activities.Header").isEmpty()) {
            AppArticle a = new AppArticle();
            a.setSubject("Activities");
            a.setDisposition(findArticleDisposition("Activities.Header"));
            core.save(a);
        }
        if (core.findFullArticlesByDisposition(null, "Featured.Header").isEmpty()) {
            AppArticle a = new AppArticle();
            a.setSubject("Featured");
            a.setDisposition(findArticleDisposition("Featured.Header"));
            core.save(a);
        }
        if (core.findFullArticlesByDisposition(null, "Testimonials.Header").isEmpty()) {
            AppArticle a = new AppArticle();
            a.setSubject("Testimonials");
            a.setDisposition(findArticleDisposition("Testimonials.Header"));
            core.save(a);
        }
        if (core.findFullArticlesByDisposition(null, "Services.Header").isEmpty()) {
            AppArticle a = new AppArticle();
            a.setSubject("Services");
            a.setDisposition(findArticleDisposition("Services.Header"));
            core.save(a);
        }
        AppProfile publisher = core.findOrCreateProfile("Publisher");
        for (String right : CorePluginSecurity.getEntityRights(pu.getEntity(AppArticle.class), true, true, true, false, false)) {
            core.addProfileRight(publisher.getId(), right);
        }
        for (String right : CorePluginSecurity.getEntityRights(pu.getEntity(AppArticleFile.class), true, true, true, false, false)) {
            core.addProfileRight(publisher.getId(), right);
        }
    }

    public AppArticle findArticle(int articleId) {
        return UPA.getPersistenceUnit().findById(AppArticle.class, articleId);
    }

    public AppArticleDisposition findArticleDisposition(int articleDispositionId) {
        return UPA.getPersistenceUnit().findById(AppArticleDisposition.class, articleDispositionId);
    }

    public List<AppArticleDispositionBundle> findArticleDispositionGroupTypes() {
        EntityCache entityCache = getContext().getCacheService().get(AppArticleDispositionBundle.class);
        return entityCache.getValues();
    }

    public List<AppArticleDispositionGroup> findArticleDispositionGroups(int bundleId) {
        final EntityCache entityCache = getContext().getCacheService().get(AppArticleDispositionGroup.class);
        return entityCache.getProperty("findArticleDispositionGroups:" + bundleId, new Action<List<AppArticleDispositionGroup>>() {
            @Override
            public List<AppArticleDispositionGroup> run() {
                return UPA.getPersistenceUnit()
                        .createQuery("Select u from AppArticleDispositionGroup u where u.bundleId=:bundleId order by u.index,u.title")
                        .setParameter("bundleId", bundleId)
                        .getResultList();
            }
        });
    }

    public AppArticleDispositionGroup findArticleDispositionGroup(String name) {
        final EntityCache entityCache = getContext().getCacheService().get(AppArticleDispositionGroup.class);
        Map<String, AppArticleDispositionGroup> m = entityCache.getProperty("findArticleDispositionGroup", new Action<Map<String, AppArticleDispositionGroup>>() {
            @Override
            public Map<String, AppArticleDispositionGroup> run() {
                Map<String, AppArticleDispositionGroup> m = new HashMap<String, AppArticleDispositionGroup>();
                MapList<Integer, AppArticleDispositionGroup> values = entityCache.getValues();
                for (AppArticleDispositionGroup u : values) {
                    String key = u.getName();
                    if (!StringUtils.isBlank(key)) {
                        m.put(key, u);
                    }
                }
                return m;
            }
        });
        return m.get(name);
    }

    public AppArticleDisposition findArticleDisposition(String name) {
        final EntityCache entityCache = getContext().getCacheService().get(AppArticleDisposition.class);
        Map<String, AppArticleDisposition> m = entityCache.getProperty("findArticleDispositionByName", new Action<Map<String, AppArticleDisposition>>() {
            @Override
            public Map<String, AppArticleDisposition> run() {
                Map<String, AppArticleDisposition> m = new HashMap<String, AppArticleDisposition>();
                MapList<Integer, AppArticleDisposition> values = entityCache.getValues();
                for (AppArticleDisposition u : values) {
                    String key = u.getName();
                    if (!StringUtils.isBlank(key)) {
                        m.put(key, u);
                    }
                }
                return m;
            }
        });
        return m.get(name);
    }

    public List<AppArticleFile> findArticleFiles(int articleId) {
        return UPA.getPersistenceUnit().createQuery("Select u from AppArticleFile u where "
                + " u.articleId=:articleId"
                + " order by "
                + "  u.position asc"
        )
                .setParameter("articleId", articleId)
                .getResultList();
    }

    public List<AppArticleFile> findArticleFiles(int[] articleIds) {
        if (articleIds.length == 0) {
            return Collections.emptyList();
        }
        StringBuilder ids_string = new StringBuilder();
        ids_string.append(articleIds[0]);
        for (int i = 1; i < articleIds.length; i++) {
            ids_string.append(",");
            ids_string.append(articleIds[i]);
        }
        return UPA.getPersistenceUnit().createQuery("Select u from AppArticleFile u where "
                + " u.articleId in(" + ids_string + ")"
                + " order by "
                + "  u.position asc"
        )
                .getResultList();
    }

    public FullArticle findFullArticle(int id) {
        //invoke privileged to find out article not created by self.
        //when using non privileged mode, users can see SOLELY articles they have created
        AppArticle a = UPA.getPersistenceUnit().invokePrivileged(new Action<AppArticle>() {
            @Override
            public AppArticle run() {
                return findArticle(id);
            }
        });
        if (a == null) {
            return null;
        }
        List<AppArticle> ok = filterArticles(Arrays.asList(a));
        if (ok.isEmpty()) {
            return null;
        }
        a = ok.get(0);

        String aname = a.getLinkText();
        String aurl = a.getLinkURL();
        String acss = a.getLinkClassStyle();
        List<AppArticleFile> att = new ArrayList<>();
        if (!StringUtils.isBlank(aname) || !StringUtils.isBlank(aurl)) {
            AppArticleFile baseArt = new AppArticleFile();
            baseArt.setId(-1);
            baseArt.setName(VrUtils.resolveFileName(aname, aurl));
            boolean added = false;
            if (aurl != null && aurl.startsWith("/")) {
                VFile vFile = getRootFileSystem0().get(aurl);
                if (vFile.isDirectory()) {
                    for (VFile file : vFile.listFiles()) {
                        AppArticleFile baseArt2 = new AppArticleFile();
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
        List<AppArticleFile> c = findArticleFiles(a.getId());
        if (c != null) {
            for (AppArticleFile articleFile : c) {
                boolean added = false;
                aurl = articleFile.getPath();
                if (aurl != null && aurl.startsWith("/")) {
                    VFile vFile = getRootFileSystem0().get(aurl);
                    if (vFile.isDirectory()) {
                        for (VFile file : vFile.listFiles()) {
                            AppArticleFile baseArt2 = new AppArticleFile();
                            baseArt2.setId(-1);
                            baseArt2.setName(VrUtils.resolveFileName(file.getName(), file.getPath()));
                            baseArt2.setPath(file.getPath());
                            baseArt2.setStyle(acss);
                            att.add(baseArt2);
                        }
                        added = true;
                    }
                }
                if (!added) {
                    att.add(articleFile);
                }
            }
        }
        FullArticle f = new FullArticle(new AppArticleStrict(a), att);
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
                Entity entity = pu.getEntity(AppArticle.class);
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

//    @Deprecated
//    public List<FullArticle> findFullArticlesByCategory(String disposition) {
//        return findFullArticlesByDisposition(null, disposition);
//    }
    public List<FullArticle> findFullArticlesByDisposition(String group, String disposition) {
        if (group == null) {
            if (!getContext().getCorePlugin().isStarted()) {
                group = "";
            } else {
                UserSession userSession = getContext().getCorePlugin().getCurrentSession();
                group = userSession == null ? null : userSession.getSelectedSiteFilter();
            }
        }
        if (StringUtils.isBlank(group)) {
            group = "";
        }
        AppArticleDispositionGroup g = findArticleDispositionGroup(group);
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
                List<AppArticle> articles = findArticlesByUserAndCategory(login, dispositionGroupId, includeNoDept, disposition);
                int[] articleIds = new int[articles.size()];
                for (int i = 0; i < articleIds.length; i++) {
                    articleIds[i] = articles.get(i).getId();
                }
                ListValueMap<Integer, AppArticleFile> articleFilesMap = new ListValueMap<Integer, AppArticleFile>();
                for (AppArticleFile articleFile : findArticleFiles(articleIds)) {
                    articleFilesMap.put(articleFile.getArticle().getId(), articleFile);
                }
                for (AppArticle a : articles) {
                    AppArticleDisposition d = a.getDisposition();
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
                            if(dd==null){
                                log.log(Level.SEVERE, "Article with null SendTime : Article.id="+a.getId());
                            }
                            if (dd!=null && dd.compareTo(d2.getDateTime()) < 0) {
                                continue;
                            }
                        }

                    }
                    String aname = a.getLinkText();
                    String aurl = a.getLinkURL();
                    String acss = a.getLinkClassStyle();
                    List<AppArticleFile> att = new ArrayList<>();
                    if (!StringUtils.isBlank(aname) || !StringUtils.isBlank(aurl)) {
                        if (StringUtils.isBlank(aname)) {
                            aname = VrUtils.getURLName(aurl);
                        }
                        if (StringUtils.isBlank(aname)) {
                            aname = "NoName";
                        }
                        AppArticleFile baseArt = new AppArticleFile();
                        baseArt.setId(-1);
                        baseArt.setName(aname);
                        baseArt.setPath(aurl);
                        baseArt.setStyle(acss);
                        boolean added = false;
                        if (aurl != null && aurl.startsWith("/")) {
                            VFile vFile = getRootFileSystem0().get(aurl);
                            if (vFile.isDirectory()) {
                                for (VFile file : vFile.listFiles()) {
                                    AppArticleFile baseArt2 = new AppArticleFile();
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

                    List<AppArticleFile> c = articleFilesMap.get(a.getId());
                    if (c != null) {
                        for (AppArticleFile articleFile : c) {
                            boolean added = false;
                            aurl = articleFile.getPath();
                            if (aurl != null && aurl.startsWith("/")) {
                                VFile vFile = getRootFileSystem0().get(aurl);
                                if (vFile.isDirectory()) {
                                    for (VFile file : vFile.listFiles()) {
                                        AppArticleFile baseArt2 = new AppArticleFile();
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
                                att.add(articleFile);
                            }
                        }
                    }
                    FullArticle f = new FullArticle(new AppArticleStrict(a), att);
                    all.add(f);
                }
                return all;
            }

        }, null);
    }

    protected List<AppArticle> findArticlesByUserAndCategory(String login, int dispositionGroupId, boolean includeNoDept, String... dispositions) {
        if (dispositions.length == 0) {
            return Collections.EMPTY_LIST;
        }
        StringBuilder queryStr = new StringBuilder("Select u from AppArticle u where ");
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
        List<AppArticle> all = query.getResultList();

        return getContext().getCorePlugin().filterByProfilePattern(all, null, login, new ProfilePatternFilter<AppArticle>() {
            @Override
            public String getProfilePattern(AppArticle t) {
                AppUser s = t.getSender();
                String author = s == null ? null : s.getLogin();
                String p = t.getRecipientProfiles();
                if (StringUtils.isBlank(p)) {
                    return p;
                }
                if (StringUtils.isBlank(author)) {
                    return p;
                }
                return "( " + p + " ) , " + author;
            }
        });
    }

    /**
     * removes articles that should not be seen by current user!
     *
     * @param articles
     * @return
     */
    public List<AppArticle> filterArticles(List<AppArticle> articles) {
        return getContext().getCorePlugin().filterByProfilePattern(articles, null, getContext().getCorePlugin().getCurrentUserLogin(), new ProfilePatternFilter<AppArticle>() {
            @Override
            public String getProfilePattern(AppArticle t) {
                AppUser s = t.getSender();
                String author = s == null ? null : s.getLogin();
                String p = t.getRecipientProfiles();
                if (StringUtils.isBlank(p)) {
                    return p;
                }
                if (StringUtils.isBlank(author)) {
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
        AppArticleDisposition t = pu.findByMainField(AppArticleDisposition.class, "rss." + rss);
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

//    @EntityActionList(entityType = AppArticle.class)
//    public String[] findArticleActions(){
//        return new String[]{"SendEmail"};
//    }
    public String getArticleProperty(int articleId, String name) {
        AppArticleProperty prop = UPA.getPersistenceUnit().createQuery("Select a from AppArticleProperty a where a.articleId=:id and a.name=:name")
                .setParameter("id", articleId)
                .setParameter("name", name)
                .getFirstResultOrNull();
        if (prop != null) {
            return prop.getValue();
        }
        return null;
    }

    public String findOrCreateArticleProperty(int articleId, String name, String defaultValue) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppArticleProperty prop = pu.createQuery("Select a from AppArticleProperty a where a.articleId=:id and a.name=:name")
                .setParameter("id", articleId)
                .setParameter("name", name)
                .getFirstResultOrNull();
        if (prop != null) {
            return prop.getValue();
        }
        AppArticle a = pu.findById(AppArticle.class, articleId);
        prop = new AppArticleProperty();
        prop.setName(name);
        prop.setArticle(a);
        prop.setValue(defaultValue);
        pu.persist(prop);
        return defaultValue;
    }

    public Map<String, String> getArticleProperties(int articleId) {
        List<AppArticleProperty> props = UPA.getPersistenceUnit().createQuery("Select a from AppArticleProperty a where a.articleId=:id")
                .setParameter("id", articleId)
                .getResultList();
        Map<String, String> all = new HashMap<>();
        for (AppArticleProperty prop : props) {
            all.put(prop.getName(), prop.getValue());
        }
        return all;
//        final EntityCache entityCache = getContext().getCacheService().get(AppArticleProperty.class);
//        Map<String, String> m = entityCache.getProperty("getArticlesProperties", new Action<Map<String, String>>() {
//            @Override
//            public Map<String, String> run() {
//                Map<String, String> m = new HashMap<String, String>();
//                MapList<Integer, AppArticleProperty> values = entityCache.getValues();
//                for (AppArticleProperty u : values) {
//                    String key = u.getName();
//                    if (!StringUtils.isBlank(key)) {
//                        m.put(key, u.getValue());
//                    }
//                }
//                return m;
//            }
//        });
//        return m;
    }

    public AppArticleDisposition findOrCreateArticleDisposition(String name, String description, String actionName) {
        AppArticleDisposition disposition = new AppArticleDisposition();
        disposition.setEnabled(true);
        disposition.setName(name);
        disposition.setDescription(description);
        disposition.setActionName(actionName);
        return getContext().getCorePlugin().findOrCreate(disposition);
    }

}
