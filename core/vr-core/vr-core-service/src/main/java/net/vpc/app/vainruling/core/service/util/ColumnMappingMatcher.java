/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;

/**
 *
 * @author vpc
 */
public class ColumnMappingMatcher {

    public static void match(Object o, Object[] columnTitles) {
        try {
            String[] scolumnTitles = new String[columnTitles.length];
            Map<String, Integer> m = new HashMap<>();
            for (int i = 0; i < scolumnTitles.length; i++) {
                String ss = StringUtils.normalizeString(Convert.toString(columnTitles[i]));
                scolumnTitles[i] = ss;
                m.put(ss, i);
            }
            for (Field declaredField : o.getClass().getDeclaredFields()) {
                ColumnMapping cm = declaredField.getAnnotation(ColumnMapping.class);
                if (cm != null) {
                    declaredField.setAccessible(true);
                    boolean ok = false;
                    for (String y : cm.value()) {
                        y=StringUtils.normalizeString(y);
                        if (m.get(y) != null) {
                            declaredField.set(o, m.get(y));
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        declaredField.set(o, -1);
                    }
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
