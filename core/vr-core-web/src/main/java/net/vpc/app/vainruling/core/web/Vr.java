/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.*;
import net.vpc.app.vainruling.core.service.model.*;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.service.util.wiki.VrWikiParser;
import net.vpc.app.vainruling.core.web.ctrl.ActiveSessionsCtrl;
import net.vpc.app.vainruling.core.web.ctrl.AppGlobalCtrl;
import net.vpc.app.vainruling.core.web.ctrl.LoginCtrl;
import net.vpc.app.vainruling.core.web.fs.files.DocumentsCtrl;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.app.vainruling.core.web.menu.VRMenuDef;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import net.vpc.app.vainruling.core.web.themes.VrTheme;
import net.vpc.app.vainruling.core.web.themes.VrThemeFactory;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.vfs.VFile;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import org.primefaces.model.StreamedContent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl
@Scope(value = "singleton")
public class Vr {
    private MessageTextService messageTextService;
    private TaskTextService taskTextService;
    private NotificationTextService notificationTextService;
    private CmsTextService cmsTextService;

    public static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00");

    public Vr() {
    }

    public static Vr get() {
        return VrApp.getBean(Vr.class);
    }

    public VrTheme getAppTheme() {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        String oldValue = UPA.getPersistenceUnit().invokePrivileged(new Action<String>() {
            @Override
            public String run() {
                CorePlugin c = VrApp.getBean(CorePlugin.class);
                return (String) c.getOrCreateAppPropertyValue("System.DefaultTheme", null, "");
            }
        });
        if (StringUtils.isEmpty(oldValue)) {
            oldValue = c.getAppVersion().getDefaultTheme();
        }
        if (StringUtils.isEmpty(oldValue)) {
            oldValue = "default";
        }
        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
        VrTheme theme = tfactory.getTheme(oldValue);
        if (theme != null) {
            return theme;
        }
        //force to default
        theme = tfactory.getTheme("default");
        if (theme != null) {
            return theme;
        }
        throw new IllegalArgumentException("Invalid Theme");
    }

    public VrTheme getUserTheme(String login) {
        CorePlugin c = VrApp.getBean(CorePlugin.class);
        String oldValue = (String) c.getAppPropertyValue("System.DefaultTheme", login);
        if (StringUtils.isEmpty(oldValue)) {
            oldValue = getAppTheme().getId();
        }
        if (StringUtils.isEmpty(oldValue)) {
            oldValue = "default";
        }
        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
        VrTheme theme = tfactory.getTheme(oldValue);
        if (theme != null) {
            return theme;
        }
        throw new IllegalArgumentException("Invalid Theme");
    }

    public VrTheme getTheme() {
        UserSession s = UserSession.get();
        if (s != null) {
            CorePlugin c = VrApp.getBean(CorePlugin.class);
            VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
            String themeId = s.getTheme();
            if (StringUtils.isEmpty(themeId)) {
                themeId = (String) c.getAppPropertyValue("System.DefaultTheme", s.getUser() == null ? null : s.getUser().getLogin());
                if (StringUtils.isEmpty(themeId)) {
                    themeId = getAppTheme().getId();
                }
                if (StringUtils.isEmpty(themeId)) {
                    themeId = "default";
                }
            }
            VrTheme theme = tfactory.getTheme(themeId);
            if (theme != null) {
                s.setTheme(themeId);
                return theme;
            }
        }
        return getAppTheme();
    }

    public <T> List<T> listHead(List<T> anyList,int maxSize) {
        if(anyList.size()>maxSize){
            return anyList.subList(0,maxSize);
        }
        return anyList;
    }

    public <T> List<T> listTail(List<T> anyList,int maxSize) {
        if(anyList.size()>maxSize){
            return anyList.subList(anyList.size()-maxSize,maxSize);
        }
        return anyList;
    }

    public <T> List<List<T>> splitListBy(int groupSize, List<T> anyList) {
        List<List<T>> grouped = new ArrayList<>();
        for (int i = 0; i < groupSize; i++) {
            grouped.add(new ArrayList<T>());
        }
        if(anyList!=null) {
            for (int i = 0; i < anyList.size(); i++) {
                grouped.get(i % groupSize).add(anyList.get(i));
            }
        }
        return grouped;
    }

    public <T> List<List<T>> groupListBy(int groupSize, List<T> anyList) {
        List<List<T>> grouped = new ArrayList<>();
        List<T> curr = new ArrayList<>();
        if(anyList!=null) {
            for (int i = 0; i < anyList.size(); i++) {
                if (curr.size() < groupSize) {
                    curr.add(anyList.get(i));
                } else {
                    grouped.add(curr);
                    curr = new ArrayList<>();
                    curr.add(anyList.get(i));
                }
            }
        }
        if (curr.size() > 0) {
            grouped.add(curr);
        }
        return grouped;
    }

    public int randomize(int a) {
        return (int) (Math.random() * a);
    }

    public int randomize(long a) {
        return (int) (Math.random() * a);
    }

    public double randomize(double a) {
        return (Math.random() * a);
    }

    public String randomize(String... items) {
        return items[randomize(items.length)];
    }

    public boolean isFSPath(String path) {
        if (path == null) {
            path = "";
        }
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        return !(path.startsWith("http://") || path.startsWith("https://") || path.startsWith("ftp://"));
    }

    public double abs(double a) {
        return Math.abs(a);
    }

    public double max(double a, double b) {
        return Math.max(a, b);
    }

    public double min(double a, double b) {
        return Math.min(a, b);
    }


    public int rand(int a) {
        return (int) (Math.random() * a);
    }

    public double rand(double a) {
        return (Math.random() * a);
    }

    public String rand(String... values) {
        return values[randomize(values.length)];
    }

    public int hashToMax(Object value, int max) {
        return Math.abs(value == null ? 0 : value.hashCode()) % max;
    }

    public String hashToArr(Object value, String... values) {
        return values[hashToMax(value, values.length)];
    }

    public double frame(double x, double a, double b) {
        if (x < a) {
            x = a;
        }
        if (x > b) {
            x = b;
        }
        return x;
    }

    public String articleImageOrRand(String imageURL) {
        if(imageURL!=null){
            String low = imageURL.toLowerCase();
            for (String s : new String[]{"jpg","jpeg","png","gif"}) {
                if(low.endsWith("."+s) || low.contains("."+s+"?")){
                    return url(imageURL);
                }
            }
            imageURL="";
        }
        return url((String)nvl(imageURL,strFormat("/Site/images/articles/%1s"
                ,randomize(
                        "article-01.jpg",
                        "article-02.jpg",
                        "article-03.jpg",
                        "article-04.jpg",
                        "article-05.jpg",
                        "article-06.jpg",
                        "article-07.jpg",
                        "article-08.jpg",
                        "article-09.jpg",
                        "article-10.jpg"
                )
        )));
    }

    public String articleImageOrDefault(String imageURL) {
        return url((String)nvl(imageURL,strFormat("/Site/images/articles/%1s"
                ,"article-01.jpg"
        )));
    }

    public String url(String path) {
        if (path == null) {
            path = "";
        }
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("ftp://")) {
            return path;
        }
        String prefix = getContext() + "/fs";
        if (!path.startsWith("/")) {
            prefix += "/";
        }
        return prefix + path;
    }

    /**
     * relative url, no context prefixed
     *
     * @param path
     * @return
     */
    public String urlr(String path) {
        if (path == null) {
            path = "";
        }
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("ftp://")) {
            return path;
        }
        String prefix = "/fs";
        if (!path.startsWith("/")) {
            prefix += "/";
        }
        return prefix + path;
    }

    public boolean isEmpty(Object... a) {
        return str(a).trim().length() == 0;
    }

    public String str(Object... a) {
        StringBuilder sb = new StringBuilder();
        if (a != null) {
            for (Object x : a) {
                if (x != null) {
                    sb.append(String.valueOf(x));
                }
            }
        }
        return sb.toString();
    }

    public String nvlstr(Object... a) {
        if (a != null) {
            for (Object x : a) {
                String s = x == null ? "" : String.valueOf(x);
                if (!StringUtils.isEmpty(s)) {
                    return s;
                }
            }
        }
        return "";
    }

    public Object nvl(Object... a) {
        for (Object x : a) {
            if (x != null) {
                if (x instanceof String) {
                    if (!StringUtils.isEmpty((String) x)) {
                        return x;
                    }
                } else {
                    return x;
                }
            }
        }
        return null;
    }

    public String strFormat(String format,Object... args) {
        return String.format(format,args);
    }

    public String strcat(Object... a) {
        StringBuilder sb = new StringBuilder();
        for (Object x : a) {
            if (x != null) {
                if (x instanceof String) {
                    if (!StringUtils.isEmpty((String) x)) {
                        sb.append((String) x);
                    }
                } else {
                    sb.append(x);
                }
            }
        }
        return sb.toString();
    }

    public String strcatsep(String sep, Object... a) {
        if (StringUtils.isEmpty(sep)) {
            sep = " ";
        }
        StringBuilder sb = new StringBuilder();
        for (Object x : a) {
            if (x != null) {
                String v = (x instanceof String) ? ((String) x) : String.valueOf(x);
                if (!StringUtils.isEmpty(v)) {
                    if (sb.length() > 0) {
                        sb.append(sep);
                    }
                    sb.append(v);
                }
            }
        }
        return sb.toString();
    }

    public String fstr(String format, Object... a) {
        UserSession s = null;
        s = UserSession.get();
        Locale loc = s == null ? null : s.getLocale();
        if (loc == null) {
            loc = Locale.getDefault(Locale.Category.DISPLAY);
        }
        MessageFormat mf = new MessageFormat(format, loc);
        return mf.format(a);
    }

    public String date(Date dte) {
        UserSession s = null;
        try {
            s = UserSession.get();
        } catch (Exception e) {
            //ignore error
        }
        return VrUtils.getRelativeDateMessage(dte, s == null ? null : s.getLocale());
    }

    public String date(Date dte, Locale loc) {
        UserSession s = null;
        try {
            s = UserSession.get();
        } catch (Exception e) {
            //ignore error
        }
        if (loc == null && s != null) {
            loc = s.getLocale();
        }
        return VrUtils.getRelativeDateMessage(dte, loc);
    }

    public String date(Date d, String format) {
        if (format.startsWith("#")) {
            return date(d, new Locale(format.substring(1)));
        }
        return d == null ? "" : new SimpleDateFormat(format).format(d);
    }

    public String strcut(String value, int max) {
        if (value == null) {
            value = "";
        }
        if (value.length() > max) {
            value = value.substring(0, max - 3) + "...";
        }
        return value;
    }

    public String strexpand(String value, String chars, int min) {
        if (chars == null || chars.length() == 0) {
            chars = " ";
        }
        if (value == null) {
            value = "";
        }
        while (value.length() < min) {
            if (value.length() + chars.length() <= min) {
                value = value + chars;
            } else {
                int x = min - value.length();
                if (x > chars.length()) {
                    x = chars.length();
                }
                value = value + chars.substring(0, x);
            }
        }
        return value;
    }

    public String html2txt(String value) {
        return VrUtils.html2text(value);
    }

    public String html(String value) {
        return VrUtils.extractPureHTML(value);
    }

    public String contentText2html(ContentText value) {
        if(value==null){
            return "";
        }
        return VrUtils.extractPureHTML(value.getContent());
    }

    public String wiki2Html(String value) {
        return VrWikiParser.convertToHtml(value, "Wiki");
    }

    public double percent(double val, double max) {
        return max == 0 ? 0 : ((val / max) * 100);
    }

    public double valOrPercent(boolean percent, double val, double max) {
        if (percent) {
            return max == 0 ? 0 : ((val / max) * 100);
        } else {
            return val;
        }
    }

    public Double nonNullValOrPercent(boolean percent, double val, double max) {
        if (percent) {
            return (max == 0 || val == 0) ? null : ((val / max) * 100);
        } else {
            return val == 0 ? null : val;
        }
    }

    public String valOrPercentString(boolean percent, double val, double max) {
        if (percent) {
            Double d = (max == 0 || val == 0) ? null : ((val / max) * 100);
            if (d != null) {
                return PERCENT_FORMAT.format(d) + "%";
            }
            return null;
        } else {
            if (val == 0) {
                return null;
            }
            if (Math.round(val) == val) {
                return String.valueOf((long) val);
            }
            return new DecimalFormat("0.0##").format(val);
        }
    }

    public String getFacesContextPrefix() {
        return "r";
    }

    public String getFacesContext() {
        return getContext() + "/" + getFacesContextPrefix();
    }

    public String getFullContext() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        StringBuilder sb= new StringBuilder();
        StringBuffer requestURL = req.getRequestURL();
        String contextPath = req.getContextPath();
        sb.append(requestURL.substring(0, requestURL.indexOf(contextPath)+ contextPath.length()));
        return sb.toString();
    }
    public String getContext() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return req.getContextPath();
    }

    public String getThemeContext() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String contextPath = req.getContextPath();
        return contextPath + "/themes/" + getTheme().getId();
//        return contextPath+"/META-INF/resources/themes/"+getTheme().getId();
    }

    public String getThemePath() {
//        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//        String contextPath = req.getContextPath();
        return "/themes/" + getTheme().getId();
    }

    public Locale getLocale(String preferred) {
        Locale loc = StringUtils.isEmpty(preferred) ? null : new Locale(preferred);
        if (loc == null) {
            UserSession s = null;
            try {
                s = UserSession.get();
            } catch (Exception e) {
                //ignore error
            }
            if (loc == null && s != null) {
                loc = s.getLocale();
            }
            if (loc == null) {
                loc = Locale.getDefault();
            }
        }
        return loc;
    }

    // Session Aware


    public String goBack() {
        return VrApp.getBean(VrMenuManager.class).goBack();
    }

    public String gotoPage(String command, String arguments) {
        return VrApp.getBean(VrMenuManager.class).gotoPage(command, arguments);
    }

    public String gotoPageObjItem(String entity, String id) {
        return VrApp.getBean(VrMenuManager.class).gotoPageObjItem(entity, id);
    }

    public UserSession getUserSession() {
        return UserSession.get();
    }

    public String buildMenu() {
        return VrApp.getBean(VrMenuManager.class).buildMenu();
    }

    public VRMenuDef getMenu() {
        return VrApp.getBean(VrMenuManager.class).getModel().getRoot();
    }

    public List<BreadcrumbItem> getBreadcrumb() {
        return VrApp.getBean(VrMenuManager.class).getModel().getBreadcrumb();
    }

    public BreadcrumbItem getTitleCrumb() {
        return VrApp.getBean(VrMenuManager.class).getModel().getTitleCrumb();
    }

    public String logout() {
        return VrApp.getBean(LoginCtrl.class).dologout();
    }


    public void notifyShutdown() {
        VrApp.getBean(AppGlobalCtrl.class).doNotifyShutdown();
    }

    public void cancelShutdown() {
        VrApp.getBean(AppGlobalCtrl.class).doCancelShutdown();
    }

    public boolean isShutdown() {
        return VrApp.getBean(AppGlobalCtrl.class).isShutdown();
    }

    public String getHeadMessageText() {
        return VrApp.getBean(AppGlobalCtrl.class).getHeadMessageText();
    }

    public String getHeadMessageStyle() {
        return VrApp.getBean(AppGlobalCtrl.class).getHeadMessageStyle();
    }

    public List<UserSession> getActiveSessions() {
        return VrApp.getBean(ActiveSessionsCtrl.class).getModel().getSessions();
    }

    public List<UserSession> getActiveSessionsByType(String type) {
        List<UserSession> r=new ArrayList<>();
        List<UserSession> activeSessions = getActiveSessions();
        if(activeSessions!=null) {
            for (UserSession userSession : activeSessions) {
                String name = (userSession != null && userSession.getUser() != null && userSession.getUser().getType() != null) ? userSession.getUser().getType().getName() : null;
                if (type == null || (
                        type.equals(name)
                )) {
                    r.add(userSession);
                }
            }
        }
        return r;
    }

    public int getPollInterval() {
        CorePlugin core = CorePlugin.get();
        AppProperty property = core.getAppProperty("System.PollInterval", null);
        if (property != null) {
            Object appPropertyValue = core.getAppPropertyValue(property);
            if (appPropertyValue != null) {
                if (appPropertyValue instanceof Number) {
                    return ((Number) appPropertyValue).intValue();
                } else if (appPropertyValue instanceof String) {
                    try {
                        return Integer.parseInt(String.valueOf(appPropertyValue));
                    } catch (Exception ex) {
                        //ignore
                    }
                }
            }
        }
        return 120;
    }

    public void onPoll() {
        VrApp.getBean(CorePlugin.class).onPoll();
    }

    public void error() throws Exception {
        throw new Exception("This is exception");
    }

    public StreamedContent downloadPath(String path) {
        return VrApp.getBean(DocumentsCtrl.class).downloadPath(path);
    }

    public CmsTextService getCmsTextService() {
        if(cmsTextService==null) {
            ApplicationContext c = VrApp.getContext();
            for (String n : c.getBeanNamesForType(CmsTextService.class)) {
                CmsTextService s = (CmsTextService) c.getBean(n);
                return cmsTextService=s;
            }
            throw new IllegalArgumentException();
        }
        return cmsTextService;
    }

    public NotificationTextService getNotificationTextService() {
        if(notificationTextService==null) {
            ApplicationContext c = VrApp.getContext();
            for (String n : c.getBeanNamesForType(NotificationTextService.class)) {
                NotificationTextService s = (NotificationTextService) c.getBean(n);
                return notificationTextService=s;
            }
            throw new IllegalArgumentException();
        }
        return notificationTextService;
    }

    public TaskTextService getTaskTextService() {
        if(taskTextService==null) {
            ApplicationContext c = VrApp.getContext();
            for (String n : c.getBeanNamesForType(TaskTextService.class)) {
                TaskTextService s = (TaskTextService) c.getBean(n);
                return taskTextService=s;
            }
            throw new IllegalArgumentException();
        }
        return taskTextService;
    }

    public MessageTextService getMessageTextService() {
        if(messageTextService==null) {
            ApplicationContext c = VrApp.getContext();
            for (String n : c.getBeanNamesForType(MessageTextService.class)) {
                MessageTextService s = (MessageTextService) c.getBean(n);
                return messageTextService=s;
            }
            throw new IllegalArgumentException();
        }
        return messageTextService;
    }

    public String getCurrentUserPhoto() {
        UserSession s = getUserSession();
        if(s!=null && s.getUser()!=null){
            return getUserPhoto(s.getUser().getId());
        }
        return null;
    }

    public String getUserPhoto(int id) {
        AppUser t = CorePlugin.get().findUser(id);
        AppContact c = t.getContact();
        boolean female = false;
        if (c != null) {
            AppGender g = c.getGender();
            if (g != null) {
                if ("F".equals(g.getCode())) {
                    female = true;
                }
            }
        }
        List<String> paths = new ArrayList<String>();
        for (String p : new String[]{"WebSite/me.png", "WebSite/me.jpg", "WebSite/me.gif"}) {
            paths.add(p);
        }
        if (female) {
            for (String p : new String[]{"WebSite/she.png", "WebSite/she.jpg", "WebSite/she.gif"}) {
                paths.add(p);
            }
        } else {
            for (String p : new String[]{"WebSite/he.png", "WebSite/he.jpg", "WebSite/he.gif"}) {
                paths.add(p);
            }
        }
        for (String p : new String[]{"WebSite/photo.png", "WebSite/photo.jpg", "WebSite/photo.gif"}) {
            paths.add(p);
        }

        VFile file = getUserAbsoluteFile(t.getId(), paths.toArray(new String[paths.size()]));

        String photo = (t == null || file == null) ? null : (file.getPath());
        return photo;
    }

    public String getUserPhotoFullURL(int id) {
        AppUser t = CorePlugin.get().findUser(id);
        AppContact c = t.getContact();
        boolean female = false;
        if (c != null) {
            AppGender g = c.getGender();
            if (g != null) {
                if ("F".equals(g.getCode())) {
                    female = true;
                }
            }
        }
        List<String> paths = new ArrayList<>();
        for (String p : new String[]{"WebSite/me.png", "WebSite/me.jpg", "WebSite/me.gif"}) {
            paths.add(p);
        }
        if (female) {
            for (String p : new String[]{"WebSite/she.png", "WebSite/she.jpg", "WebSite/she.gif"}) {
                paths.add(p);
            }
        } else {
            for (String p : new String[]{"WebSite/he.png", "WebSite/he.jpg", "WebSite/he.gif"}) {
                paths.add(p);
            }
        }
        for (String p : new String[]{"WebSite/photo.png", "WebSite/photo.jpg", "WebSite/photo.gif"}) {
            paths.add(p);
        }

        VFile file = getUserAbsoluteFile(t.getId(), paths.toArray(new String[paths.size()]));

        String photo = (t == null || file == null) ? null : getAppWebPath(file.getPath());
        return photo;
    }

    public VFile getUserAbsoluteFile(int id, String path) {
        AppUser t = CorePlugin.get().findUser(id);
        CorePlugin fs = VrApp.getBean(CorePlugin.class);
        if (t != null) {
            VFile thisTeacherPhoto = fs.getUserFolder(t.getLogin()).get(path);
            if (thisTeacherPhoto.exists()) {
                return thisTeacherPhoto;
            } else {
                //should by by user type!!
                VFile anyTeacherPhoto = fs.getProfileFolder("Teacher").get(path);
                if (anyTeacherPhoto.exists()) {
                    return anyTeacherPhoto;
                }
            }
        }
        return null;
    }

    public String getAppWebPath(String virtualAbsolutePath) {
        if (virtualAbsolutePath == null) {
            return null;
        }
        return "/fs/" + virtualAbsolutePath;
    }

    public String getAbsoluteWebPath(String virtualAbsolutePath) {
        if (virtualAbsolutePath == null) {
            return null;
        }
        return Vr.get().getContext() + "/fs/" + virtualAbsolutePath;
    }


    public VFile getUserAbsoluteFile(int id, String... path) {
        VFile[] p = getUserAbsoluteFiles(id, path);
        if (p.length == 0) {
            return null;
        }
        return p[0];
    }

    public VFile[] getUserAbsoluteFiles(int id, String[] path) {
        AppUser t = CorePlugin.get().findUser(id);
        CorePlugin fs = VrApp.getBean(CorePlugin.class);
        List<VFile> files = new ArrayList<VFile>();
        if (t != null) {
            VFile userFolder = fs.getUserFolder(t.getLogin());
            VFile profileFolder = fs.getProfileFolder("Teacher");
            for (String p : path) {
                VFile ff = userFolder.get(p);
                if (ff.exists()) {
                    files.add(ff);
                }
            }
            for (String p : path) {
                VFile ff = profileFolder.get(p);
                if (ff.exists()) {
                    files.add(ff);
                }
            }
        }
        return files.toArray(new VFile[files.size()]);
    }

    public String mapValue(String var,String defaultVal,String ... fromTo){
        for (int i = 0; i < fromTo.length; i+=2) {
            if(var.equals(fromTo[i])){
                return fromTo[i+1];
            }
        }
        return defaultVal;
    }

    public boolean allowed(String key){
        return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(key);
    }

    public String updatePublicDepartment(String departmentCode){
        AppDepartment department = CorePlugin.get().findDepartment(departmentCode);
        getUserSession().setSelectedDepartment(department);
        return gotoPage("publicIndex","");
    }
}
