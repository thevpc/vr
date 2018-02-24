/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import net.vpc.common.strings.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
public class DateFormatUtils {

    private static final Map<String, SimpleDateFormat> m = new HashMap<>();

    private DateFormatUtils() {
    }


    public static Date parse(String value,String format,Date defaultValue) {
        if(StringUtils.isEmpty(value)){
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
