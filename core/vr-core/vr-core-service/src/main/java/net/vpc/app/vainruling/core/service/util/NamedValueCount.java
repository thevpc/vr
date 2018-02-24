/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.util;

/**
 * @author taha.bensalah@gmail.com
 */
public class NamedValueCount {
    private String name;
    private Object userValue;
    private int count;

    public NamedValueCount() {
    }

    public NamedValueCount(String name, Object userValue, int count) {
        this.name = name;
        this.userValue = userValue;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
