/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author vpc
 */
public class ValueCountSet {

    Map<Object, ValueCount> map = new HashMap<Object, ValueCount>();

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
