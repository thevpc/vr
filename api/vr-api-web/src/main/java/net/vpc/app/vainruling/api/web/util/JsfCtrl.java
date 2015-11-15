/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.common.strings.StringUtils;
import org.springframework.stereotype.Controller;

/**
 *
 * @author vpc
 */
@ManagedBean
@Controller
public class JsfCtrl {

    public JsfCtrl() {
    }

    public String str(Object... a) {
        StringBuilder sb = new StringBuilder();
        for (Object x : a) {
            if (x != null) {
                sb.append(String.valueOf(x));
            }
        }
        return sb.toString();
    }

    public String nvlstr(Object... a) {
        for (Object x : a) {
            String s = x == null ? "" : String.valueOf(x);
            if (!StringUtils.isEmpty(s)) {
                return s;
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

    public String pureHtml(String value) {
        return VrHelper.extratPureHTML(value);
    }

    public String getContext() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return req.getContextPath();
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
