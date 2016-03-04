/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.Record;
import net.vpc.upa.UPA;

/**
 *
 * @author vpc
 */
public class ObjSimpleSearch extends ObjSearch {

    private String expression;
    private List<String> tokens;

    public ObjSimpleSearch() {
        super("expr");
    }

    public ObjSimpleSearch(String expression) {
        super("expr");
        this.expression = expression;
        setExpression(expression);
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
        tokens = tokenize(expression);
    }

    @Override
    public List filterList(List list, String entityName) {
        List oldList = super.filterList(list, entityName);
        if (StringUtils.isEmpty(expression)) {
            return oldList;
        }
        List newList = new ArrayList();
        for (Object object : oldList) {
            if (acceptStringRecord(toStringRecord(object, entityName))) {
                newList.add(object);
            }
        }
        return newList;
    }

    private static List<String> tokenize(String expr) {
        ArrayList<String> all = new ArrayList<>();
        int i = 0;
        StringBuilder ss = new StringBuilder();
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
                    ss.delete(0, ss.length());
                }
            } else {
                ss.append(c);
            }
            i++;
        }
        if (ss.length() > 0) {
            all.add(normalize(ss.toString()));
        }
        return all;
    }

    private boolean acceptStringRecord(Map<String, String> rec) {
        if(tokens==null || tokens.isEmpty()){
            return true;
        }
        for (String token : tokens) {
            boolean ok=false;
            for (Map.Entry<String, String> entry : rec.entrySet()) {
                if (entry.getValue().contains(token)) {
                   ok=true;
                   break;
                }
            }
            if(!ok){
                return false;
            }
        }
        return true;
    }

    private Map<String, String> toStringRecord(Object o, String entityName) {
        Map<String, String> words = new HashMap<>();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Record r = pu.getEntity(entityName).getBuilder().entityToRecord(o, true);
        if (r != null) {
            for (Map.Entry<String, Object> entry : r.toMap().entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();
                if (v != null) {
                    Entity ve = pu.findEntity(v.getClass());
                    if (ve != null) {
                        Object mv = ve.getBuilder().getMainValue(v);
                        String v2 = String.valueOf(mv);
                        if (!StringUtils.isEmpty(v2)) {
                            words.put(k, normalize(v2));
                        }
                    } else if (v instanceof String) {
                        if (!StringUtils.isEmpty(v.toString())) {
                            words.put(k, normalize(v));
                        }
                    } else {
                        words.put(k, normalize(v));
                    }
                }
            }
        }
        return words;
    }

    private static String normalize(Object s) {
        if (s == null) {
            return null;
        }
        String ss = String.valueOf(s);
        return StringUtils.normalize(ss).toLowerCase();
    }
}
