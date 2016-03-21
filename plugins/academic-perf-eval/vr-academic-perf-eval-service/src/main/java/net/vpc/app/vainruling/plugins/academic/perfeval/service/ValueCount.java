/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.perfeval.service;

/**
 *
 * @author vpc
 */
public class ValueCount {
    private Object value;
    private Object userValue;
    private int count;

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
