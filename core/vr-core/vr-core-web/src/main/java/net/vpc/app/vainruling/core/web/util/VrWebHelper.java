/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.util;

import java.io.UnsupportedEncodingException;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.core.service.util.ObjectHolder;
import net.vpc.app.vainruling.core.web.themes.VrTheme;
import net.vpc.app.vainruling.core.web.themes.VrThemeFace;
import net.vpc.app.vainruling.core.web.themes.VrThemeFactory;
import net.vpc.common.strings.StringConverter;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.*;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.core.service.pages.VrBreadcrumbItem;
import net.vpc.app.vainruling.core.service.menu.VRMenuInfo;
import net.vpc.app.vainruling.core.service.menu.VrPageInfoAndObject;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import net.vpc.upa.exceptions.IllegalUPAArgumentException;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrWebHelper {

    public static final Map<String, String> extensionsToCss = new HashMap<String, String>();

    static {
        extensionsToCss.put("csv", "file-csv");

        extensionsToCss.put("txt", "file-txt");
        extensionsToCss.put("properties", "file-txt");

        extensionsToCss.put("log", "file-log");

        extensionsToCss.put("xls", "file-xls");
        extensionsToCss.put("xlsx", "file-xls");
        extensionsToCss.put("ods", "file-xls");

        extensionsToCss.put("doc", "file-doc");
        extensionsToCss.put("docx", "file-doc");
        extensionsToCss.put("odt", "file-doc");

        extensionsToCss.put("zip", "file-zip");
        extensionsToCss.put("tar", "file-zip");
        extensionsToCss.put("rar", "file-zip");

        extensionsToCss.put("pdf", "file-pdf");
        extensionsToCss.put("xml", "file-xml");
        extensionsToCss.put("css", "file-css");
        extensionsToCss.put("html", "file-html");

        extensionsToCss.put("png", "file-img");
        extensionsToCss.put("gif", "file-img");
        extensionsToCss.put("jpg", "file-img");
        extensionsToCss.put("jpeg", "file-img");
    }

    private static boolean invoke(Object o, String method, Class[] params, ObjectHolder ret, Object... values) {
        try {
            Method m = o.getClass().getDeclaredMethod(method, params);
            m.setAccessible(true);
            ret.setValue(m.invoke(values));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static HttpServletRequest getHttpServletRequest() {
        HttpServletRequest req = null;
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                req = ((ServletRequestAttributes) requestAttributes).getRequest();
            } else if (requestAttributes.getClass().getSimpleName().equals("JaxrsRequestAttributes")) {
                ObjectHolder ret = new ObjectHolder();
                invoke(requestAttributes, "getHttpServletRequest", new Class[0], ret);
                req = (HttpServletRequest) ret.getValue();
            } else if (requestAttributes.getClass().getSimpleName().equals("FacesRequestAttributes")) {
                ObjectHolder ret = new ObjectHolder();
                if (invoke(requestAttributes, "getFacesContext", new Class[0], ret)) {
                    if (invoke(ret.getValue(), "getExternalContext", new Class[0], ret)) {
                        if (invoke(ret.getValue(), "getRequest", new Class[0], ret)) {
                            req = (HttpServletRequest) ret.getValue();
                        }
                    }
                }
            } else {
                throw new IllegalUPAArgumentException("Unsupported");
            }
        } catch (java.lang.IllegalStateException e) {
            System.err.println("Unexpected Exception " + e);
        } catch (Exception e) {
            System.err.println("Unexpected Exception " + e);
//            e.printStackTrace();
        }
        return req;
    }

    public static void prepareUserSession() {

        UserToken s = CorePlugin.get().getCurrentToken();
        if (s != null) {
            if (s.getUserLogin() != null) {
                s.setPublicTheme(getUserPublicTheme(s.getUserLogin()).getId());
                s.setPrivateTheme(getUserPrivateTheme(s.getUserLogin()).getId());
            } else {
                s.setPublicTheme(getAppPublicTheme().getId());
                s.setPrivateTheme(getAppPrivateTheme().getId());
            }
            HttpServletRequest req = getHttpServletRequest();
            if (s.getSessionId() == null) {
                HttpSession session = req.getSession(true); // true == allow create
                s.setSessionId(session.getId());
            }
            if (s.getLocale() == null) {
                Locale locale = req.getLocale();
                s.setLocale(locale == null ? null : locale.toString());
            }
            if (s.getIpAddress() == null) {
                String ipAddress = req.getHeader("X-FORWARDED-FOR");
                if (ipAddress == null) {
                    ipAddress = req.getRemoteAddr();
                }
                s.setIpAddress(ipAddress);
            }
        }
    }

    public static Object evalSpringExpr(String expr) {
        if (expr == null) {
            return null;
        }
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(VrApp.getContext()));
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(expr);
        return expression.getValue(context);
    }

    public static String evalSpringExprMessage(String message) {
        if (message == null) {
            return null;
        }
        if (message.contains("#{")) {
            message = StringUtils.replacePlaceHolders(message, "#{", "}", new StringConverter() {
                @Override
                public String convert(String str) {
                    //TODO
                    if ("vr.publicThemeContext".equals(str)) {
                        return getPublicThemeContext();
                    }
                    if ("vr.privateThemeContext".equals(str)) {
                        return getPublicThemeContext();
                    }
                    if ("vr.publicThemePath".equals(str)) {
                        return getPublicThemePath();
                    }
                    if ("vr.privateThemePath".equals(str)) {
                        return getPrivateThemePath();
                    }
                    if ("vr.publicThemeRelativePath".equals(str)) {
                        String themePath = getPublicThemePath();
                        if (themePath.startsWith("/")) {
                            return themePath.substring(1);
                        }
                        return themePath;
                    }
                    if ("vr.privateThemeRelativePath".equals(str)) {
                        String themePath = getPrivateThemePath();
                        if (themePath.startsWith("/")) {
                            return themePath.substring(1);
                        }
                        return themePath;
                    }
                    return (String) evalSpringExpr(str);
                }
            });
        }
        return message;
    }

    public static VrTheme getAppPublicTheme() {
        CorePlugin core = CorePlugin.get();
        String oldValue = UPA.getPersistenceUnit().invokePrivileged(new Action<String>() {
            @Override
            public String run() {
                return (String) core.getOrCreateAppPropertyValue("System.DefaultPublicTheme", null, "");
            }
        });
        if (StringUtils.isBlank(oldValue)) {
            oldValue = core.getAppVersion().getDefaultPublicTheme();
        }
        if (StringUtils.isBlank(oldValue)) {
            oldValue = "default";
        }
        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
        VrTheme theme = tfactory.getTheme(VrThemeFace.PUBLIC, oldValue);
        if (theme != null) {
            return theme;
        }
        //force to default
        theme = tfactory.getTheme(VrThemeFace.PUBLIC, "default");
        if (theme != null) {
            return theme;
        }
        throw new IllegalArgumentException("Invalid Theme");
    }

    public static VrTheme getAppPrivateTheme() {
        CorePlugin core = CorePlugin.get();
        String oldValue = UPA.getPersistenceUnit().invokePrivileged(new Action<String>() {
            @Override
            public String run() {
                return (String) core.getOrCreateAppPropertyValue("System.DefaultPrivateTheme", null, "");
            }
        });
        if (StringUtils.isBlank(oldValue)) {
            oldValue = core.getAppVersion().getDefaultPrivateTheme();
        }
        if (StringUtils.isBlank(oldValue)) {
            oldValue = "default";
        }
        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
        VrTheme theme = tfactory.getTheme(VrThemeFace.PRIVATE, oldValue);
        if (theme != null) {
            return theme;
        }
        //force to default
        theme = tfactory.getTheme(VrThemeFace.PRIVATE, "default");
        if (theme != null) {
            return theme;
        }
        throw new IllegalArgumentException("Invalid Theme");
    }

    public static String getPublicThemeContext() {
        HttpServletRequest req = VrWebHelper.getHttpServletRequest();
        String contextPath = req.getContextPath();
        return contextPath + "/public-themes/" + getPublicTheme().getId();
//        return contextPath+"/META-INF/resources/themes/"+getTheme().getId();
    }

    public static String getPrivateThemeContext() {
        HttpServletRequest req = VrWebHelper.getHttpServletRequest();
        String contextPath = req.getContextPath();
        return contextPath + "/private-themes/" + getPrivateTheme().getId();
//        return contextPath+"/META-INF/resources/themes/"+getTheme().getId();
    }

    public static VrTheme getPublicTheme() {
        CorePlugin core = CorePlugin.get();
        UserToken s = core.getCurrentToken();
        if (s != null) {
            VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
            String themeId = s.getPublicTheme();
            if (StringUtils.isBlank(themeId)) {
                themeId = (String) core.getAppPropertyValue("System.DefaultPublicTheme", s.getUserLogin());
                if (StringUtils.isBlank(themeId)) {
                    themeId = getAppPublicTheme().getId();
                }
                if (StringUtils.isBlank(themeId)) {
                    themeId = "default";
                }
            }
            VrTheme theme = tfactory.getTheme(VrThemeFace.PUBLIC, themeId);
            if (theme != null) {
                s.setPublicTheme(themeId);
                return theme;
            }
        }
        return getAppPublicTheme();
    }

    public static VrTheme getPrivateTheme() {
        CorePlugin core = CorePlugin.get();
        UserToken s = core.getCurrentToken();
        if (s != null) {
            VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
            String themeId = s.getPrivateTheme();
            if (StringUtils.isBlank(themeId)) {
                themeId = (String) core.getAppPropertyValue("System.DefaultPrivateTheme", s.getUserLogin());
                if (StringUtils.isBlank(themeId)) {
                    themeId = getAppPrivateTheme().getId();
                }
                if (StringUtils.isBlank(themeId)) {
                    themeId = "default";
                }
            }
            VrTheme theme = tfactory.getTheme(VrThemeFace.PRIVATE, themeId);
            if (theme != null) {
                s.setPrivateTheme(themeId);
                return theme;
            }
        }
        return getAppPrivateTheme();
    }

    public static VrTheme getUserPrivateTheme(String login) {
        CorePlugin core = CorePlugin.get();
        String oldValue = (String) core.getAppPropertyValue("System.DefaultPrivateTheme", login);
        if (StringUtils.isBlank(oldValue)) {
            oldValue = getAppPrivateTheme().getId();
        }
        if (StringUtils.isBlank(oldValue)) {
            oldValue = "default";
        }
        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
        VrTheme theme = tfactory.getTheme(VrThemeFace.PRIVATE, oldValue);
        if (theme != null) {
            return theme;
        }
        throw new IllegalArgumentException("Invalid Theme");
    }

    public static VrTheme getUserPublicTheme(String login) {
        CorePlugin core = CorePlugin.get();
        String oldValue = (String) core.getAppPropertyValue("System.DefaultPublicTheme", login);
        if (StringUtils.isBlank(oldValue)) {
            oldValue = getAppPublicTheme().getId();
        }
        if (StringUtils.isBlank(oldValue)) {
            oldValue = "default";
        }
        VrThemeFactory tfactory = VrApp.getBean(VrThemeFactory.class);
        VrTheme theme = tfactory.getTheme(VrThemeFace.PUBLIC, oldValue);
        if (theme != null) {
            return theme;
        }
        throw new IllegalArgumentException("Invalid Theme");
    }

    public static String getPublicThemePath() {
//        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//        String contextPath = req.getContextPath();
        return "/public-themes/" + getPublicTheme().getId();
    }

    public static String getPrivateThemePath() {
//        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//        String contextPath = req.getContextPath();
        return "/private-themes/" + getPrivateTheme().getId();
    }

    public static String getPrivateTemplatePath() {
        if (isPrintableLayout()) {
            return getPrivateThemePath() + "/templates/private-template-printable.xhtml";
        }
        return getPrivateThemePath() + "/templates/private-template.xhtml";
    }

    public static boolean isPrintableLayout() {
        HttpServletRequest req = getHttpServletRequest();
        String paramValue = req == null ? null : req.getParameter("vr-layout");
        if ("printable".equals(paramValue)) {
            return true;
        }
        return false;
    }

    public static String getContext() {
        HttpServletRequest req = getHttpServletRequest();
        if (req == null) {
            return null;
        }
        return req.getContextPath();
    }

    public static String getFacesContextPrefix() {
        return "r";
    }

    public static boolean containsSelectItemId(List<SelectItem> all, Object old) {
        String s2 = old == null ? "" : String.valueOf(old);
        for (SelectItem selectItem : all) {
            Object p = selectItem.getValue();
            String s1 = p == null ? "" : String.valueOf(p);
            if (s1.equals(s2)) {
                return true;
            }
        }
        return false;
    }

    public static int revalidateSelectItemId(List<SelectItem> all, int old) {
        for (SelectItem selectItem : all) {
            Object p = selectItem.getValue();
            if (p instanceof Integer) {
                if (((Integer) p).intValue() == old) {
                    return old;
                }
            } else if (p instanceof String) {
                if (String.valueOf(old).equals(p)) {
                    return old;
                }
            }
        }
        return -1;
    }

    public static String revalidateSelectItemId(List<SelectItem> all, String old) {
        for (SelectItem selectItem : all) {
            Object p = selectItem.getValue();
            if (String.valueOf(old).equals(String.valueOf(p))) {
                return old;
            }
        }
        return null;
    }

    public static String getPrettyURL(VRMenuInfo info) {
        String context = VrWebHelper.getContext();
        if (context == null) {
            context = "";
        }
        if (context.length() > 0 && !context.endsWith("/")) {
            context = context + "/";
        }
        String p = context + "/p/" + info.getType();
        if (!StringUtils.isBlank(info.getCommand())) {
            try {
                p += "?a=" + URLEncoder.encode(info.getCommand(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(VRMenuInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return p;
    }

    public static VrBreadcrumbItem resolveBreadcrumbItemForBean(Object bean) {
        VrPageInfoAndObject c = resolveVrControllerInfoForBean(bean);
        return new VrBreadcrumbItem(c == null ? "" : c.getInfo().getTitle(),
                 c == null ? "" : c.getInfo().getSubTitle(),
                 c == null ? "" : c.getInfo().getCss(),
                 "",
                 "");

    }

    public static VrPageInfoAndObject resolveVrControllerInfoForBean(Object bean) {
        return VrApp.getBean(VrMenuManager.class).resolvePageInfoAndObjectByInstance(VrUtils.getBeanName(bean), null);
    }
}
