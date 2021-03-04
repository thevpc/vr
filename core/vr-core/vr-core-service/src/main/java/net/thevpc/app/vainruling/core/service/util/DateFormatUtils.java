/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.util;

import net.thevpc.common.strings.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.thevpc.common.time.MutableDate;

/**
 * @author taha.bensalah@gmail.com
 */
public class DateFormatUtils {

    private static final Map<String, SimpleDateFormat> m = new HashMap<>();

    private DateFormatUtils() {
    }

    public static MutableDate parseMutableDate(String value, String format, Date defaultValue) {
        Date d = parse(value, format, defaultValue);
        if (d == null) {
            return null;
        }
        return new MutableDate(d);
    }

    public static Date parse(String value, String format, Date defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return getFormat(format).parse(value);
        } catch (ParseException e) {
            return defaultValue;
        }
    }

    public static SimpleDateFormat getFormat(String s) {
        synchronized (m) {
            if (m.containsKey(s)) {
                return m.get(s);
            } else {
                SimpleDateFormat v = new SimpleDateFormat(s);
                m.put(s, v);
                return v;
            }
        }
    }

}
