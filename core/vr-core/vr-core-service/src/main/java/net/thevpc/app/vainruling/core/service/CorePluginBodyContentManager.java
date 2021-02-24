package net.thevpc.app.vainruling.core.service;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedOutput;
import net.thevpc.app.vainruling.core.service.cache.EntityCache;
import net.thevpc.app.vainruling.core.service.model.AppProfile;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.app.vainruling.core.service.security.UserSession;
import net.thevpc.app.vainruling.core.service.util.AppVersion;
import net.thevpc.app.vainruling.core.service.util.ProfilePatternFilter;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.collections.KeyValueList;
import net.thevpc.common.vfs.VFile;
import net.thevpc.common.vfs.VirtualFileSystem;
import net.thevpc.upa.*;
import net.thevpc.upa.expressions.UserExpression;
import net.thevpc.upa.types.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.thevpc.app.vainruling.core.service.model.content.AppArticleDisposition;
import net.thevpc.app.vainruling.core.service.model.content.AppArticleDispositionGroup;
import net.thevpc.app.vainruling.core.service.model.content.AppArticleDispositionBundle;
import net.thevpc.app.vainruling.core.service.model.content.AppArticleFile;
import net.thevpc.app.vainruling.core.service.model.content.AppArticle;
import net.thevpc.app.vainruling.core.service.model.content.DefaultVrContentText;
import net.thevpc.app.vainruling.core.service.model.content.AppArticleProperty;
import net.thevpc.app.vainruling.core.service.model.content.DefaultVrContentPath;
import net.thevpc.app.vainruling.core.service.model.content.VrContentTextConfig;
import net.thevpc.common.util.MutableDate;
import net.thevpc.app.vainruling.core.service.content.VrContentPath;
import net.thevpc.app.vainruling.core.service.content.VrContentText;
import net.thevpc.app.vainruling.core.service.model.content.AppArticleDispositionBinding;
import net.thevpc.app.vainruling.core.service.model.content.ArticlesDispositionStrict;
import net.thevpc.app.vainruling.core.service.model.strict.AppUserStrict;

class CorePluginBodyContentManager extends CorePluginBody {

    private static final Pattern VR_FILESYSTEM_URL_PATTERN = Pattern.compile("(?<ctx>http(s)?://[a-z.]+(:[0-9]+)/([a-z-]+/))fs(?<a>/.*)");

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
        if (core.findFullArticlesByDisposition(null, "About.Header", null).isEmpty()) {
            core.save(new AppArticle("About", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About", null).isEmpty()) {
            core.save(new AppArticle("Nos Valeurs", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About", null).isEmpty()) {
            core.save(new AppArticle("Reinvention", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About", null).isEmpty()) {
            core.save(new AppArticle("Actualite", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "About", null).isEmpty()) {
            core.save(new AppArticle("APropos", null, findArticleDisposition("About.Header")));
        }
        if (core.findFullArticlesByDisposition(null, "News.Header", null).isEmpty()) {
            AppArticle a = new AppArticle();
            a.setSubject("Press");
            a.setDisposition(findArticleDisposition("News.Header"));
            core.save(a);
        }
        if (core.findFullArticlesByDisposition(null, "Activities.Header", null).isEmpty()) {
            AppArticle a = new AppArticle();
            a.setSubject("Activities");
            a.setDisposition(findArticleDisposition("Activities.Header"));
            core.save(a);
        }
        if (core.findFullArticlesByDisposition(null, "Featured.Header", null).isEmpty()) {
            AppArticle a = new AppArticle();
            a.setSubject("Featured");
            a.setDisposition(findArticleDisposition("Featured.Header"));
            core.save(a);
        }
        if (core.findFullArticlesByDisposition(null, "Testimonials.Header", null).isEmpty()) {
            AppArticle a = new AppArticle();
            a.setSubject("Testimonials");
            a.setDisposition(findArticleDisposition("Testimonials.Header"));
            core.save(a);
        }
        if (core.findFullArticlesByDisposition(null, "Services.Header", null).isEmpty()) {
            AppArticle a = new AppArticle();
            a.setSubject("Services");
            a.setDisposition(findArticleDisposition("Services.Header"));
            core.save(a);
        }
        AppProfile publisher = core.findOrCreateProfile("Publisher");

        ProfileRightBuilder prb = new ProfileRightBuilder();

        for (String right : CorePluginSecurity.getEntityRights(pu.getEntity(AppArticle.class), true, true, true, false, false)) {
            prb.addProfileRight(publisher.getId(), right);
        }
        for (String right : CorePluginSecurity.getEntityRights(pu.getEntity(AppArticleFile.class), true, true, true, false, false)) {
            prb.addProfileRight(publisher.getId(), right);
        }
        prb.execute();
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
                KeyValueList<Integer, AppArticleDispositionGroup> values = entityCache.getValues();
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
                KeyValueList<Integer, AppArticleDisposition> values = entityCache.getValues();
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
    public List<VrContentText> findFullArticlesByDisposition(Integer userId, String group, String disposition, VrContentTextConfig config) {
        if (group == null) {
            if (!getContext().getCorePlugin().isStarted()) {
                group = "";
            } else {
                Integer cuid = getContext().getCorePlugin().getCurrentUserId();
                if (userId == null || userId.equals(cuid)) {
                    UserSession userSession = getContext().getCorePlugin().getCurrentSession();
                    group = userSession == null ? null : userSession.getSelectedSiteFilter();
                }
            }
        }
        if (StringUtils.isBlank(group)) {
            group = "";
        }
        AppArticleDispositionGroup g = findArticleDispositionGroup(group);
        if (g == null) {
            g = findArticleDispositionGroup("II");
        }
        return findFullArticlesByDisposition(userId/*current user*/, g == null ? -1 : g.getId(), true, disposition, config);
    }

    public List<VrContentText> findFullArticlesByAnonymousDisposition(Integer dispositionGroupId, boolean includeNoDept, final String disposition, VrContentTextConfig config) {
        return findFullArticlesByDisposition(-1, dispositionGroupId, includeNoDept, disposition, config);
    }

    protected VrContentText articleToFullArticle(AppArticle a, VrContentTextConfig config) {
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
                if (dd == null) {
                    log.log(Level.SEVERE, "Article with null SendTime : Article.id=" + a.getId());
                }
                if (dd != null && dd.compareTo(d2.getDateTime()) < 0) {
                    return null;
                }
            }

        }
        AppArticleFile mainArticle = new AppArticleFile();
        mainArticle.setArticle(a);
        mainArticle.setId(-1);
        mainArticle.setName(a.getLinkText());
        mainArticle.setPath(a.getImageURL());
        mainArticle.setLinkPath(a.getLinkURL());
        mainArticle.setStyle(a.getLinkClassStyle());

        List<VrContentPath> attachments = new ArrayList<>();
        List<VrContentPath> imageAttachments = new ArrayList<>();
        List<VrContentPath> nonImageAttachments = new ArrayList<>();
        VrContentPath mainImage = null;
        VrContentPath[] mainList = expandArticleFileToContentPathArray(mainArticle, config);
        if (mainList.length == 1 && mainList[0].isImage()) {
            mainImage = mainList[0];
        } else {
            for (VrContentPath vrContentPath : mainList) {
                attachments.add(vrContentPath);
                if (vrContentPath.isImage()) {
                    imageAttachments.add(vrContentPath);
                } else {
                    nonImageAttachments.add(vrContentPath);
                }
            }
        }
        List<AppArticleFile> c = findArticleFiles(a.getId());
        if (c != null) {
            for (AppArticleFile articleFile : c) {
                mainList = expandArticleFileToContentPathArray(articleFile, config);
                for (VrContentPath vrContentPath : mainList) {
                    attachments.add(vrContentPath);
                    if (vrContentPath.isImage()) {
                        imageAttachments.add(vrContentPath);
                    } else {
                        nonImageAttachments.add(vrContentPath);
                    }
                }
            }
        }
        DefaultVrContentText aa = new DefaultVrContentText();
        AppUserStrict suser = new AppUserStrict();
        AppUser user = a.getSender();
        if (user == null) {
            suser.setId(-1);
            suser.setLogin("anonymous");
            suser.setFullName("anonymous");
            suser.setFullTitle("anonymous");
            suser.setIconPath(this.getContext().getCorePlugin().getUserIcon(-1));
        } else {
            suser.setId(user.getId());
            suser.setLogin(user.getLogin());
            suser.setFullName(user.getFullName());
            suser.setFullTitle(user.getFullTitle());
            suser.setGenderCode(user.getGender() != null ? user.getGender().getCode() : "M");
            suser.setIconPath(this.getContext().getCorePlugin().getUserIcon(a.getSender().getId()));
        }
        aa.setUser(suser);
        aa.setId(a.getId());
        aa.setRecipients(a.getRecipientProfiles());
        aa.setDecoration(a.getDecoration());
        aa.setContent(a.getContent());
        aa.setTitle(a.getSubject());
        aa.setSubTitle(a.getSubTitle());
        aa.setDispositionGroup(a.getDispositionGroup());
        aa.setPosition(a.getPosition());
        aa.setVisitCount(a.getVisitCount());
        aa.setNoTitle(a.isNoSubject());
        aa.setImportant(a.isImportant());
        aa.setDeleted(a.isDeleted());
        aa.setArchived(a.isArchived());
        aa.setPublishTime(a.getSendTime());
        Set<String> allCats = new HashSet<>();
        if (a.getDisposition() != null) {
            allCats.add(a.getDisposition().getName());
        }
        if (aa.getId() >= 0) {
            List<AppArticleDispositionBinding> found = UPA.getPersistenceUnit().createQuery("Select ab from AppArticleDispositionBinding ab where ab.articleId=:id")
                    .setParameter("id", aa.getId()).getResultList();
            for (AppArticleDispositionBinding ab : found) {
                if (ab.getDisposition() != null) {
                    allCats.add(a.getDisposition().getName());
                }
            }
        }
        aa.setCategories(allCats.stream().filter(x -> (x != null && x.trim().length() > 0)).distinct().toArray(String[]::new));
        aa.setMainPath(mainImage == null ? new DefaultVrContentPath() : mainImage);//never null
        aa.setAttachments(attachments);
        aa.setHasImageAttachments(imageAttachments.size() > 0);
        aa.setHasNonImageAttachments(nonImageAttachments.size() > 0);
        return aa;
    }

    private DefaultVrContentPath[] expandArticleFileToContentPathArray(AppArticleFile attachment, VrContentTextConfig config) {
        List<DefaultVrContentPath> all = new ArrayList<>();
        for (AppArticleFile a : expandArticleFiles(attachment)) {
            if (a != null) {
                DefaultVrContentPath b = articleFileToContentPath(a, config);
                if (b != null) {
                    all.add(b);
                }
            }
        }
        return all.toArray(new DefaultVrContentPath[0]);
    }

    private DefaultVrContentPath articleFileToContentPath(AppArticleFile attachment, VrContentTextConfig config) {
        String aname = attachment.getName();
        String path = attachment.getPath();
        String lpath = attachment.getLinkPath();
        if (StringUtils.isBlank(path) && !StringUtils.isBlank(lpath)) {
            path = lpath;
            lpath = null;
        }
        if (StringUtils.isBlank(lpath)) {
            lpath = null;
        }
        String pathLowercased = path != null ? path.toLowerCase() : "";
        if (StringUtils.isBlank(aname)) {
            aname = VrUtils.getURLName(path);
        }
        if (StringUtils.isBlank(aname)) {
            aname = "NoName";
        }
        String style = attachment.getStyle();
        String path0 = path;
        if (VrUtils.getImageTypeByName(pathLowercased, false) != null) {
            if (pathLowercased.startsWith("/")) {
                DefaultVrContentPath cp = (DefaultVrContentPath) UPA.getContext().invokePrivileged(() -> {
                    VFile file = CorePlugin.get().getRootFileSystem().get(path0);
                    return VrUtils.getOrResizePhotoContentPath(file, config);
                });
                cp.setName(aname);
                cp.setStyle(style);
                cp.setLinkPath(attachment.getLinkPath());
                return cp;
            } else {
                Pattern pat = VR_FILESYSTEM_URL_PATTERN;
                Matcher m = pat.matcher(pathLowercased);
                boolean resolved = false;
                if (m.find()) {
                    String pp = m.group("a");
                    DefaultVrContentPath cp = (DefaultVrContentPath) UPA.getContext().invokePrivileged(() -> {
                        VFile file = CorePlugin.get().getRootFileSystem().get(pp);
                        if (file.exists()) {
                            return VrUtils.getOrResizePhotoContentPath(file, config);
                        }
                        return null;
                    });
                    if (cp != null) {
                        cp.setName(aname);
                        cp.setStyle(style);
                        cp.setLinkPath(lpath);
                        cp.setImage(true);
                        return cp;
                    }
                }
                if (!resolved) {
                    DefaultVrContentPath cp = new DefaultVrContentPath();
                    cp.setName(aname);
                    cp.setPath(path);
                    cp.setStyle(style);
                    cp.setImage(true);
                    cp.setLinkPath(lpath);
                    return cp;
                }
            }
        } else {
            DefaultVrContentPath cp = new DefaultVrContentPath();
            cp.setName(aname);
            cp.setPath(path);
            cp.setStyle(style);
            return cp;
        }
        return null;
    }

    private AppArticleFile[] expandArticleFiles(AppArticleFile aFile) {
        List<AppArticleFile> all = new ArrayList<>();
        boolean added = false;
        String acss = aFile.getStyle();
        String aurl = aFile.getPath();
        String lpath = aFile.getLinkPath();
        if (StringUtils.isBlank(aurl) && !StringUtils.isBlank(lpath)) {
            aurl = lpath;
            lpath = null;
        }
        if (StringUtils.isBlank(lpath)) {
            lpath = null;
        }
        if (StringUtils.isBlank(aurl)) {
            aurl = null;
        }
        if (lpath == null && aurl == null) {
            return new AppArticleFile[0];
        }

        if (aurl != null && aurl.trim().length() > 0 && aurl.startsWith("/")) {
            VFile vFile = getRootFileSystem0().get(aurl);
            if (vFile.isDirectory()) {
                for (VFile file : vFile.listFiles()) {
                    AppArticleFile baseArt2 = new AppArticleFile();
                    baseArt2.setId(-1);
                    baseArt2.setName(file.getName());
                    baseArt2.setPath(file.getPath());
                    baseArt2.setLinkPath(lpath);
                    baseArt2.setStyle(acss);
                    all.addAll(Arrays.asList(expandArticleFiles(baseArt2)));
                }
                added = true;
            }
        }
        if (!added) {
            if (aurl != null && aurl.trim().length() > 0) {
                aurl = aurl.trim();
                AppArticleFile copy = new AppArticleFile();
                copy.setId(aFile.getId());
                copy.setName((aFile.getName() == null || aFile.getName().trim().length() == 0) ? VrUtils.getURLName(aurl) : aFile.getName());
                copy.setPath(aurl);
                copy.setLinkPath(lpath);
                copy.setStyle(aFile.getStyle());
                all.add(copy);
            }
        }
        return all.toArray(new AppArticleFile[0]);
    }

    public VrContentText findFullArticle(int id, VrContentTextConfig config) {
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
        return articleToFullArticle(a, config);
    }

    public List<VrContentText> findFullArticlesByDisposition(Integer userId, Integer dispositionGroupId, boolean includeNoDept, final String disposition, VrContentTextConfig config) {
        AppUser u = null;
        if (userId == null) {
            u = getContext().getCorePlugin().getCurrentUser();
            userId = u == null ? null : u.getId();
        } else if (userId.intValue() < 0) {
            userId = null;
        }
        Integer finalUserId = userId;
        return UPA.getContext().invokePrivileged(new Action<List<VrContentText>>() {

            @Override
            public List<VrContentText> run() {
                List<VrContentText> all = new ArrayList<>();
                List<AppArticle> articles = findArticlesByUserAndCategory(finalUserId, dispositionGroupId, includeNoDept, disposition);
                for (AppArticle a : articles) {
                    VrContentText fa = articleToFullArticle(a, config);
                    if (fa != null) {
                        all.add(fa);
                    }
                }
                return all;
            }

        }, null);
    }

    protected List<AppArticle> findArticlesByUserAndCategory(Integer userId, Integer dispositionGroupId, boolean includeNoDept, String... dispositions) {
        if (dispositions.length == 0) {
            return Collections.EMPTY_LIST;
        }
        StringBuilder queryStr = new StringBuilder("Select u from AppArticle u where ");
        queryStr.append(" u.deleted=false ");
        queryStr.append(" and u.archived=false");
        StringBuilder queryStr2 = new StringBuilder();
        StringBuilder queryStr3 = new StringBuilder();
        Map<String, Object> disps = new HashMap<>();
        for (int i = 0; i < dispositions.length; i++) {
            String disposition = dispositions[i];
            for (String disp : disposition.split("[,; ]+")) {
                if (queryStr2.length() > 0) {
                    queryStr2.append(" or ");
                }
                if (queryStr3.length() > 0) {
                    queryStr3.append(" or ");
                }
                if (userId == null) {
                    queryStr2.append(" (u.disposition.name=:disposition" + i + " and u.disposition.requiresAuthentication=false)");
                    queryStr3.append(" (ab.disposition.name=:disposition" + i + " and ab.disposition.requiresAuthentication=false)");
                } else {
                    queryStr2.append(" u.disposition.name=:disposition" + i);
                    queryStr3.append(" ab.disposition.name=:disposition" + i);
                }
                disps.put("disposition" + i, disposition);
            }
        }
        if (queryStr2.length() > 0) {
            queryStr.append(" and (");
            queryStr.append(" (");
            queryStr.append(queryStr2);
            queryStr.append(" ) or ");
            queryStr.append(" ");
            queryStr.append("exists( select 1 from AppArticleDispositionBinding ab where ab.articleId=u.id and (" + queryStr3 + "))");
            queryStr.append(" )");
        }
        if (dispositionGroupId != null && dispositionGroupId >= 0) {
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

        return getContext().getCorePlugin().filterByProfilePattern(all, userId, null, new ProfilePatternFilter<AppArticle>() {
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
        List<VrContentText> articles = findFullArticlesByDisposition(-1, null, true, "rss." + rss, null);
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
            for (VrContentText art : articles) {
                entry = new SyndEntryImpl();
                entry.setTitle(art.getTitle());
                entry.setLink(art.getMainPath() == null ? feed.getLink() : art.getMainPath().getPath());
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
//                KeyValueList<Integer, AppArticleProperty> values = entityCache.getValues();
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
