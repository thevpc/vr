/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vpc
 */
public class NamedDoubles {

    private Map<String, Double> vals = new HashMap<>();

    public boolean containsKey(String name) {
        return vals.containsKey(name);
    }
    
    public double get(String name) {
        return get(name, 0);
    }

    public void set(String name, double value) {
        vals.put(name, value);
    }

    public double get(String name, double defaultValue) {
        Double o = vals.get(name);
        if (o == null) {
            o = defaultValue;
            vals.put(name, o);
        }
        return o;
    }

    public double inc(String name) {
        return add(name, 1);
    }
    
    public double add(String name, double x) {
        double v = get(name) + x;
        set(name, v);
        return v;
    }
}
