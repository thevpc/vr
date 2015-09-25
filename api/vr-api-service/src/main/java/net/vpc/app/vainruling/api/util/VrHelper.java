/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.util;

import com.google.gson.Gson;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppTrace;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.upa.CustomDefaultObject;
import net.vpc.upa.UPA;
import net.vpc.upa.config.Entity;
import net.vpc.upa.impl.util.Strings;
import net.vpc.upa.types.StringType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author vpc
 */
public class VrHelper {

    public static final CustomDefaultObject DEFAULT_OBJECT_CURRENT_USER = new CustomDefaultObject() {

        @Override
        public Object getObject() {
            return VrApp.getBean(UserSession.class).getUser();
        }
    };

    public static String extratTextFormHTML(String html) {
        return Jsoup.parse(html == null ? "" : html).text();
    }

    public static String extratPureHTML(String html) {
        if (html == null) {
            html = "";
        }
        Document d = Jsoup.parse(html == null ? "" : html);
        for (Element e : d.select("font")) {
            if (e.childNodeSize() > 0) {
                e.replaceWith(e.child(0));
            } else {
                e.remove();
            }
        }
        for (Element e : d.select("span")) {
            String s = e.attr("style");
            if (!Strings.isNullOrEmpty(s)) {
                e.attributes().remove("style");
            }
        }
        return d.html();
    }

    public static String getRelativeDateMessage(Date dte) {
        if (dte == null) {
            return "";
        }
        Date now = new Date();
        SimpleDateFormat dateOnlyFormat = DateFormatUtils.getFormat("yyyy-MM-dd");
        SimpleDateFormat yearMonthFormat = DateFormatUtils.getFormat("yyyy-MM");
        SimpleDateFormat yearFormat = DateFormatUtils.getFormat("yyyy");
        if (dateOnlyFormat.format(dte).equals(dateOnlyFormat.format(now))) {
            return DateFormatUtils.getFormat("HH:mm").format(dte);
        }
        if (yearMonthFormat.format(dte).equals(yearMonthFormat.format(now))) {
            Calendar dte0 = Calendar.getInstance();
            dte0.setTime(dte);
            Calendar now0 = Calendar.getInstance();
            now0.setTime(now);
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) - 1) {
                return "hier";
            }
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) - 2) {
                return "il y a 2 jours";
            }
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) - 3) {
                return "il y a 3 jours";
            }
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) + 1) {
                return "demain";
            }
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) + 2) {
                return "dans 2 jours";
            }
            if (dte0.get(Calendar.DAY_OF_MONTH) == now0.get(Calendar.DAY_OF_MONTH) + 3) {
                return "dans 3 jours";
            }

            return DateFormatUtils.getFormat("dd MMM").format(dte);
        }
        if (yearFormat.format(dte).equals(yearFormat.format(now))) {
            return DateFormatUtils.getFormat("dd MMM").format(dte);
        }
        return DateFormatUtils.getFormat("yyyy-MM-dd").format(dte);
    }

    public static String str(Object... a) {
        StringBuilder sb = new StringBuilder();
        for (Object x : a) {
            if (x != null) {
                sb.append(String.valueOf(x));
            }
        }
        return sb.toString();
    }

    public static String fstr(String format, Object... a) {
        UserSession s = null;
        s = VrApp.getBean(UserSession.class);
        Locale loc = s == null ? null : s.getLocale();
        if (loc == null) {
            loc = Locale.getDefault(Locale.Category.DISPLAY);
        }
        MessageFormat mf = new MessageFormat(format, loc);
        return mf.format(a);
    }

    public static String date(Date d, String format) {
        return d == null ? "" : new SimpleDateFormat(format).format(d);
    }

    public static String strcut(String value, net.vpc.upa.Entity e, String fieldName) {
        StringType d = (StringType) e.getField("data").getDataType();
        int m = d.getMax();
        if(m<=0){
            m=255;
        }
        return VrHelper.strcut(value, m);
    }

    public static String strcut(String value, int max) {
        if (value == null) {
            value = "";
        }
        if (value.length() > max) {
            value = value.substring(0, max - 3) + "...";
        }
        return value;
    }

    public static String strexpand(String value, String chars, int min) {
        if (value == null) {
            value = "";
        }
        while (value.length() < min) {
            if (value.length() + chars.length() < min) {
                value = value + chars;
            } else {
                value = value + chars.substring(0, min - (value.length() + chars.length()));
            }
        }
        return value;
    }

    public static String html2txt(String value) {
        return VrHelper.extratTextFormHTML(value);
    }

    public static <T> T parseObject(String cmd, Class<T> type) {
        Object arg = null;
        if (cmd != null) {
            Class pt = type;
            if (pt.equals(String.class)) {
                arg = cmd;
            } else if (pt.equals(Boolean.class)) {
                arg = Strings.isNullOrEmpty(cmd) ? null : Boolean.parseBoolean(cmd);
            } else if (pt.equals(Boolean.TYPE)) {
                arg = Strings.isNullOrEmpty(cmd) ? Boolean.FALSE : Boolean.parseBoolean(cmd);
            } else if (pt.equals(Integer.class)) {
                arg = Strings.isNullOrEmpty(cmd) ? null : Integer.parseInt(cmd);
            } else if (pt.equals(Integer.TYPE)) {
                arg = Strings.isNullOrEmpty(cmd) ? 0 : Integer.parseInt(cmd);
            } else if (pt.equals(Double.class)) {
                arg = Strings.isNullOrEmpty(cmd) ? null : Double.parseDouble(cmd);
            } else if (pt.equals(Double.TYPE)) {
                arg = Strings.isNullOrEmpty(cmd) ? 0.0 : Double.parseDouble(cmd);
            } else if (pt.equals(Long.class)) {
                arg = Strings.isNullOrEmpty(cmd) ? null : Long.parseLong(cmd);
            } else if (pt.equals(Long.TYPE)) {
                arg = Strings.isNullOrEmpty(cmd) ? 0L : Long.parseLong(cmd);
            } else if (pt.isPrimitive()) {
                throw new IllegalArgumentException("Not yet supported");
            } else if (pt.isInstance(Number.class)) {
                throw new IllegalArgumentException("Not yet supported");
            } else {
                Gson gson = new Gson();
                arg = gson.fromJson(cmd, pt);
            }
        }
        return (T) arg;
    }

}
