/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

/**
 *
 * @author vpc
 */
public class OrderedPropertyView implements Comparable<OrderedPropertyView> {

    int index;
    PropertyView value;

    public OrderedPropertyView(int index, PropertyView value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public int compareTo(OrderedPropertyView o) {
        int i = value.getPosition() - o.value.getPosition();
        if (i != 0) {
            return i;
        }
        return index - o.index;
    }

    public int getIndex() {
        return index;
    }

    public PropertyView getValue() {
        return value;
    }

}
