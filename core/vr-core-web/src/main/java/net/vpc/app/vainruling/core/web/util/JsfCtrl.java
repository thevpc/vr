/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.util;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.core.service.util.VrHelper;
import net.vpc.app.vainruling.core.service.util.wiki.VrWikiParser;
import net.vpc.app.vainruling.core.web.themes.VrTheme;
import net.vpc.app.vainruling.core.web.themes.VrThemeFactory;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import org.springframework.stereotype.Controller;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author taha.bensalah@gmail.com
 */
@ManagedBean
@Controller
public class JsfCtrl {

    public JsfCtrl() {
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
        UserSession s = UserSession.getCurrentSession();
        if (s != null) {
            CorePlugin c = VrApp.getBean(CorePlugin.class);
            VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
            String themeId = s.getTheme();
            if(StringUtils.isEmpty(themeId)){
                themeId=(String) c.getAppPropertyValue("System.DefaultTheme", s.getUser()==null?null:s.getUser().getLogin());
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

    public <T> List<List<T>> groupListBy(int groupSize, List<T> anyList) {
        List<List<T>> grouped = new ArrayList<>();
        List<T> curr = new ArrayList<>();
        for (int i = 0; i < anyList.size(); i++) {
            if (curr.size() < groupSize) {
                curr.add(anyList.get(i));
            } else {
                grouped.add(curr);
                curr = new ArrayList<>();
                curr.add(anyList.get(i));
            }
        }
        if (curr.size() > 0) {
            grouped.add(curr);
        }
        return grouped;
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
        s = VrApp.getBean(UserSession.class);
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
            s = VrApp.getBean(UserSession.class);
        } catch (Exception e) {
            //ignore error
        }
        return VrHelper.getRelativeDateMessage(dte, s == null ? null : s.getLocale());
    }

    public String date(Date dte, Locale loc) {
        UserSession s = null;
        try {
            s = VrApp.getBean(UserSession.class);
        } catch (Exception e) {
            //ignore error
        }
        if (loc == null && s != null) {
            loc = s.getLocale();
        }
        return VrHelper.getRelativeDateMessage(dte, loc);
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
        return VrHelper.extratTextFormHTML(value);
    }

    public String html(String value) {
        return VrHelper.extractPureHTML(value);
    }

    public String wiki2Html(String value) {
        return VrWikiParser.convertToHtml(value, "Wiki");
    }

    public String getFacesContextPrefix() {
        return "r";
    }

    public String getFacesContext() {
        return getContext() + "/" + getFacesContextPrefix();
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
                s = VrApp.getBean(UserSession.class);
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

}
