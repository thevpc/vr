/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.util;

/**
 * @author taha.bensalah@gmail.com
 */
public class ValueCount {
    private Object value;
    private Object userValue;
    private int count;

    public ValueCount() {
    }

    public ValueCount(Object value, Object userValue, int count) {
        this.value = value;
        this.userValue = userValue;
        this.count = count;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getUserValue() {
        return userValue;
    }

    public void setUserValue(Object userValue) {
        this.userValue = userValue;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
