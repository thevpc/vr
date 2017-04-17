package net.vpc.app.vainruling.core.restclient;

/**
 * Created by vpc on 2/26/17.
 */
public class TypedParam {
    private Object value;
    private Class type;

    public TypedParam(Object value, Class type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public Class getType() {
        return type;
    }
}
