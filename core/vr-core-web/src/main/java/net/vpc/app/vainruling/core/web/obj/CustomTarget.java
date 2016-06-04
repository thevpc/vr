/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

import net.vpc.upa.Field;

/**
 *
 * @author vpc
 */
public class CustomTarget {

    private Object value;
    private CustomTargetType type;

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

    public CustomTarget(Object value, CustomTargetType type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public CustomTargetType getType() {
        return type;
    }

}
