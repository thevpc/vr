/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import com.google.gson.JsonElement;
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
    
    public static TextSearchFilter forJson(String expression) {
        return new TextSearchFilter(expression, new ObjectToMapConverter() {
            @Override
            public Map<String, Object> convert(Object o) {
                JsonElement o2 = VrUtils.GSON.toJsonTree(o);
                return VrUtils.GSON.fromJson(o2, Map.class);
            }
        });
    }

    public static TextSearchFilter forEntity(String expression,String entityName) {
        return new TextSearchFilter(expression, new ObjectToMapConverter() {
                    @Override
                    public Map<String, Object> convert(Object o) {
                        return VrUtils.toStringRecord(o,entityName);
                    }
                }
        );
    }

    public TextSearchFilter(String expression, ObjectToMapConverter converter) {
        this.converter = converter;
        setExpression(expression);
    }
    
    private static List<String> tokenize(String expr) {
        ArrayList<String> all = new ArrayList<>();
        if (!StringUtils.isBlank(expr)) {
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
                        all.add(VrUtils.normalize(ss.toString()));
                        ss.delete();
                    }
                } else {
                    ss.append(c);
                }
                i++;
            }
            if (ss.length() > 0) {
                all.add(VrUtils.normalize(ss.toString()));
            }
        }
        return all;
    }
    
    public String getExpression() {
        return expression;
    }
    
    public void setExpression(String expression) {
        this.expression = expression;
        tokens = tokenize(expression);
    }
    
    public static List filterList(List list,String expression,String entityName) {
        TextSearchFilter textSearch = TextSearchFilter.forEntity(expression, entityName);
        return textSearch == null ? list : textSearch.filterList(list);
    }
    
    public List filterList(List list) {
        List oldList = list;
        if (StringUtils.isBlank(expression)) {
            return oldList;
        }
        List newList = new ArrayList();
        for (Object object : oldList) {
            Map<String, String> m = VrUtils.normalizeMap(this.converter.convert(object));
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
    
}
