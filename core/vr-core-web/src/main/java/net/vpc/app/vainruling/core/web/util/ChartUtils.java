/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.util;

import java.util.*;

/**
 * @author vpc
 */
public class ChartUtils {

    public static void incKey(Map<String, Number> map, String key) {
        Number v = map.get(key);
        if (v == null) {
            v = 1;
            map.put(key, v);
        } else {
            double d = v.doubleValue() + 1;
            if (((int) d) == d) {
                map.put(key, ((int) d));
            } else {
                map.put(key, d);
            }
        }
    }

    public static void mergeMapKeys(Map<String, Number>... maps) {
        LinkedHashSet<String> keys = new LinkedHashSet<String>();
        for (Map<String, Number> m : maps) {
            for (Map.Entry<String, Number> i : m.entrySet()) {
                keys.add(i.getKey());
            }
        }
        for (Map<String, Number> m : maps) {
            Map<String, Number> list2 = new LinkedHashMap<>();
            for (String k : keys) {
                if (m.containsKey(k)) {
                    list2.put(k, m.get(k));
                } else {
                    list2.put(k, 0);
                }
            }
            m.clear();
            m.putAll(list2);
        }
    }

    public static List<KeyValStruct> toKeyValStructList(Map<String, Number> list) {
        List<KeyValStruct> ll = new ArrayList<KeyValStruct>();
        for (Map.Entry<String, Number> entry : list.entrySet()) {
            ll.add(new KeyValStruct(entry.getKey(), entry.getValue()));
        }
        return ll;
    }

    public static Map<String, Number> reverseSortCount(Map<String, Number> list, int groupsCount, String othersName) {
        List<KeyValStruct> ll = toKeyValStructList(list);
        Collections.sort(ll);
        Collections.reverse(ll);

        if (ll.size() > groupsCount) {
            StringBuilder sb = new StringBuilder();
            Number count = 0;
            List<KeyValStruct> ll2 = new ArrayList<>();
            for (int i = 0; i < groupsCount; i++) {
                ll2.add(ll.get(i));
            }
            for (int i = groupsCount; i < ll.size(); i++) {
                KeyValStruct v = ll.get(i);
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(v.n);
                count = v.v.doubleValue() + count.doubleValue();
            }
            if (count.intValue() == count.doubleValue()) {
                count = count.intValue();
            }
            if (othersName != null) {
                ll2.add(new KeyValStruct(othersName, count));
            }
            ll = ll2;
        }
        LinkedHashMap<String, Number> list2 = new LinkedHashMap<String, Number>();
        for (KeyValStruct a : ll) {
            list2.put(a.n, a.v);
        }
        return list2;
    }

    public static Map<String, Number> reverseSortCount(Map<String, Number> list) {
        List<KeyValStruct> ll = toKeyValStructList(list);
        Collections.sort(ll);
        Collections.reverse(ll);
        LinkedHashMap<String, Number> list2 = new LinkedHashMap<String, Number>();
        for (KeyValStruct a : ll) {
            list2.put(a.n, a.v);
        }
        return list2;
    }

    public static Map<String, Number> sortCount(Map<String, Number> list) {

        List<KeyValStruct> ll = toKeyValStructList(list);
        Collections.sort(ll);
        LinkedHashMap<String, Number> list2 = new LinkedHashMap<String, Number>();
        for (KeyValStruct a : ll) {
            list2.put(a.n, a.v);
        }
        return list2;
    }

    public static Map<String, Number> sortKey(Map<String, Number> list) {

        List<KeyValStruct> ll = toKeyValStructList(list);
        Collections.sort(ll, new Comparator<KeyValStruct>() {
            @Override
            public int compare(KeyValStruct o1, KeyValStruct o2) {
                String n1 = o1.n;
                String n2 = o2.n;
                return n1.compareTo(n2);
            }
        });
        LinkedHashMap<String, Number> list2 = new LinkedHashMap<String, Number>();
        for (KeyValStruct a : ll) {
            list2.put(a.n, a.v);
        }
        return list2;
    }

    public static class KeyValStruct implements Comparable<KeyValStruct> {

        String n;
        Number v;

        public KeyValStruct(String n, Number v) {
            this.n = n;
            this.v = v;
        }

        @Override
        public int compareTo(KeyValStruct o) {
            if (v == null) {
                if (o.v == null) {
                    //check next
                } else {
                    return -1;
                }
            } else if (o.v == null) {
                return 1;
            } else if (v.doubleValue() > o.v.doubleValue()) {
                return 1;
            } else if (v.doubleValue() < o.v.doubleValue()) {
                return -1;
            }
            return n.compareTo(o.n);
        }

    }

}
