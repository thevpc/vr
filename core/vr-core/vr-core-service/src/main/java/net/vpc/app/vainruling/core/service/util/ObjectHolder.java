package net.vpc.app.vainruling.core.service.util;

public class ObjectHolder<T> {
    private Object value;

    public ObjectHolder() {
    }

    public ObjectHolder(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
