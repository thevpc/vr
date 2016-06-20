/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vpc
 */
public class DateFormatUtils {

    private static final Map<String, SimpleDateFormat> m = new HashMap<>();

    private DateFormatUtils() {
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
