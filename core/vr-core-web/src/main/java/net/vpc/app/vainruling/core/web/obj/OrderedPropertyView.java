/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

/**
 * @author taha.bensalah@gmail.com
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
