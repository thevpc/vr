/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
public class BijectionMap<X, Y> {

    private Map<X, Y> x2y;
    private Map<Y, X> y2x;

    public BijectionMap(Class type) {
        try {
            x2y = (Map) type.newInstance();
            y2x = (Map) type.newInstance();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid type", ex);
        }
    }

    public void setDirect(X a, Y b) {
        if (x2y.containsKey(a)) {
            removeDirect(a);
        }
        x2y.put(a, b);
        y2x.put(b, a);
    }

    public void setIndirect(Y b, X a) {
        setDirect(a, b);
    }

    public Y getDirect(X x) {
        return x2y.get(x);
    }

    public X getIndirect(Y y) {
        return y2x.get(y);
    }

    public int size() {
        return y2x.size();
    }

    public void removeDirect(X x) {
        Y y = x2y.get(x);
        if (y != null) {
            x2y.remove(x);
            y2x.remove(y);
        }
    }

    public void removeIndirect(Y y) {
        X x = y2x.get(y);
        if (x != null) {
            x2y.remove(x);
            y2x.remove(y);
        }
    }

}
