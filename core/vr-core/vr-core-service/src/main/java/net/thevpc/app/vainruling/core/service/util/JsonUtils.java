/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.thevpc.common.util.MapUtils;

import net.thevpc.common.util.PlatformUtils;

/**
 *
 * @author vpc
 */
public class JsonUtils {

    private static Map<Class, TypeParser> delegates = new HashMap<Class, TypeParser>();

//    public static void main(String[] args) {
//        Integer parse = parse("\"15\"", Integer.class);
//        System.out.println(parse);
//    }

    static {
        register(String.class, new TypeParser() {
            @Override
            public Object parse(String value, Class type) {
                if (value == null) {
                    return null;
                }
                try {
                    return VrUtils.GSON.fromJson(value, type);
                } catch (RuntimeException ex) {
                    return value;
                }
            }
        });
        for (Class cls : PlatformUtils.getPrimitiveBoxingTypes()) {
            register(cls, new TypeParser() {
                @Override
                public Object parse(String value, Class type) {
                    if (value == null) {
                        return null;
                    }
                    value = value.trim();
                    return VrUtils.GSON.fromJson(value, type);
                }
            });
        }
        for (Class cls : PlatformUtils.getPrimitiveTypes()) {
            register(cls, new TypeParser() {
                @Override
                public Object parse(String value, Class type) {
                    if (value == null) {
                        return PlatformUtils.getDefaultValue(cls);
                    }
                    value = value.trim();
                    return VrUtils.GSON.fromJson(value, type);
                }
            });
        }
    }

    public static void register(Class type, TypeParser delegate) {
        if (delegate == null) {
            delegates.remove(type);
        } else {
            delegates.put(type, delegate);
        }
    }

    public static String format(Object any) {
        return VrUtils.GSON.toJson(any);
    }

    public static <T> T parse(String str, Class<T> type) {
        TypeParser o = delegates.get(type);
        if (o == null) {
            o = delegates.get(Object.class);
        }
        if (o != null) {
            return (T) o.parse(str, type);
        }
        if (str == null) {
            return null;
        }
        return VrUtils.GSON.fromJson(str, type);
    }

    public static String jsonMap(Object... keyVal) {
        return VrUtils.GSON.toJson(MapUtils.map(new LinkedHashMap<Object, Object>((keyVal == null || keyVal.length < 2) ? 1 : (keyVal.length / 2 + 1)), keyVal));
    }

    private interface TypeParser {

        Object parse(String value, Class type);
    }
}
