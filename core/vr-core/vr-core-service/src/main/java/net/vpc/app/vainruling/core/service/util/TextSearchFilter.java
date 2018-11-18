/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import net.vpc.common.strings.StringBuilder2;
import net.vpc.common.strings.StringUtils;

import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
public class TextSearchFilter {

    private String expression;
    private List<String> tokens;
    private ObjectToMapConverter converter;

    public TextSearchFilter(String expression, ObjectToMapConverter converter) {
        this.expression = expression;
        this.converter = converter;
        setExpression(expression);
    }

    private static List<String> tokenize(String expr) {
        ArrayList<String> all = new ArrayList<>();
        if(!StringUtils.isEmpty(expr)) {
            int i = 0;
            StringBuilder2 ss = new StringBuilder2();
            while (i < expr.length()) {
                char c = expr.charAt(i);
                if (c == '\"' && ss.length() == 0) {
                    i++;
                    LOOP1:
                    while (i < expr.length()) {
                        c = expr.charAt(i);
                        if (c == '\"') {
                            break LOOP1;
                        } else {
                            ss.append(c);
                        }
                        i++;
                    }
                } else if (c == '\'' && ss.length() == 0) {
                    i++;
                    LOOP1:
                    while (i < expr.length()) {
                        c = expr.charAt(i);
                        if (c == '\'') {
                            break LOOP1;
                        } else {
                            ss.append(c);
                        }
                        i++;
                    }
                } else if (Character.isWhitespace(c)) {
                    if (ss.length() > 0) {
                        all.add(normalize(ss.toString()));
                        ss.delete();
                    }
                } else {
                    ss.append(c);
                }
                i++;
            }
            if (ss.length() > 0) {
                all.add(normalize(ss.toString()));
            }
        }
        return all;
    }

    private static String normalize(Object s) {
        if (s == null) {
            return "";
        }
        String ss = String.valueOf(s);
        return StringUtils.normalizeString(ss).toLowerCase();
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
        tokens = tokenize(expression);
    }

    public List filterList(List list) {
        List oldList = list;
        if (StringUtils.isEmpty(expression)) {
            return oldList;
        }
        List newList = new ArrayList();
        for (Object object : oldList) {
            Map<String, String> m = normalizeMap(this.converter.convert(object));
            if (acceptStringDocument(m)) {
                newList.add(object);
            }
        }
        return newList;
    }

    private boolean acceptStringDocument(Map<String, String> rec) {
        if (tokens == null || tokens.isEmpty()) {
            return true;
        }
        for (String token : tokens) {
            boolean ok = false;
            for (String val : new HashSet<String>(rec.values())) {
                if (val.contains(token)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    private Map<String, String> normalizeMap(Map<String, Object> map) {
        Map<String, String> map2=new HashMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            String v = normalize(e.getValue());
            if (!StringUtils.isEmpty(v)) {
                map2.put(e.getKey(), v);
            }
        }
        return map2;
    }
}
