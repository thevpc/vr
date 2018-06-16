/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.upa.Field;

/**
 * @author taha.bensalah@gmail.com
 */
public class CustomTarget {

    private final Object value;
    private final CustomTargetType type;

    public CustomTarget(Object value, CustomTargetType type) {
        this.value = value;
        this.type = type;
    }

    public static CustomTarget forField(Field f) {
        return new CustomTarget(f.getName(), CustomTargetType.FIELD);
    }

    public static CustomTarget forField(String f) {
        return new CustomTarget(f, CustomTargetType.FIELD);
    }

    public static CustomTarget forDataType(Class cls) {
        return new CustomTarget(cls, CustomTargetType.DATATYPE);
    }

    public static CustomTarget forPlatformType(Class cls) {
        return new CustomTarget(cls, CustomTargetType.PLATFORMTYPE);
    }

    public Object getValue() {
        return value;
    }

    public CustomTargetType getType() {
        return type;
    }

}
