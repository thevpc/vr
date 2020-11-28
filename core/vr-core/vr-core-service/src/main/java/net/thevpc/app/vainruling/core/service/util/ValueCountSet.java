/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author taha.bensalah@gmail.com
 */
public class ValueCountSet {

    Map<Object, ValueCount> map = new HashMap<Object, ValueCount>();

    public ValueCountSet() {
    }

    public void touchAllArr(Object[] o) {
        for (Object o1 : o) {
            touch(o1);
        }
    }

    public void touchAll(Object... o) {
        for (Object o1 : o) {
            touch(o1);
        }
    }

    public void touch(Object o) {
        ValueCount v = map.get(o);
        if (v == null) {
            v = new ValueCount();
            v.setValue(o);
//            v.setUserValue(userObj);
            map.put(o, v);
        }
    }

    public void inc(Object o) {
        ValueCount v = map.get(o);
        if (v == null) {
            v = new ValueCount();
            v.setValue(o);
//            v.setUserValue(userObj);
            map.put(o, v);
        }
        v.setCount(v.getCount() + 1);
    }

    public long getCountTotal() {
        long a = 0;
        for (ValueCount value : map.values()) {
            a += value.getCount();
        }
        return a;
    }

    public double getCountPercent(Object o) {
        long t = getCountTotal();
        if (t == 0) {
            return 0;
        }
        int v = getCount(o);
        return v * 100.0 / t;
    }

    public int getCount(Object o) {
        ValueCount v = map.get(o);
        if (v == null) {
            return 0;
        }
        return v.getCount();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

    public Set<Object> keySet() {
        return map.keySet();
    }
}
