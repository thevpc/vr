package net.vpc.app.vainruling.core.service.util;

import net.vpc.common.io.PathInfo;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Utils;
import net.vpc.common.vfs.VFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import net.vpc.common.util.PlatformTypeUtils;

public class VrPlatformUtils {
    public static final String SLASH = System.getProperty("file.separator");
    private static final Map<Class, ClassInfo> classInfos = new HashMap<>();

    public static String validatePath(String path) {
        if (path == null || SLASH.equals("/")) {
            return path;
        }
        return path.replace('/', path.charAt(0));
    }

    private static String toPropName(String r) {
        char[] chars = r.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static ClassInfo forType(Class type) {
        if (type == null) {
            return null;
        }
        synchronized (classInfos) {
            ClassInfo u = classInfos.get(type);
            if (u == null) {
                u = new ClassInfo(type);
                classInfos.put(type, u);
                u.buid();
            }
            return u;
        }
    }

    public static File changeFileSuffix(File file, String suffix) {
        PathInfo p = PathInfo.create(file);
        return new File(
                PathInfo.formatPath(p.getDirName(), p.getNamePart() + suffix, p.getExtensionPart())
        );
    }

    public static File changeFileExtension(File file, String suffix) {
        PathInfo p = PathInfo.create(file);
        return new File(
                PathInfo.formatPath(p.getDirName(), p.getNamePart(), suffix)
        );
    }

    public static VFile changeFileSuffix(VFile file, String suffix) {
        PathInfo p = PathInfo.create(file.getPath());
        return file.getFileSystem().get(
                PathInfo.formatPath(p.getDirName(), p.getNamePart() + suffix, p.getExtensionPart())
        );
    }

    public static VFile changeFileExtension(VFile file, String suffix) {
        PathInfo p = PathInfo.create(file.getPath());
        return file.getFileSystem().get(
                PathInfo.formatPath(p.getDirName(), p.getNamePart(), suffix)
        );
    }

    public static BufferedImage readImage(VFile input) throws IOException {
        try (InputStream in = input.getInputStream()) {
            return ImageIO.read(in);
        }
    }

    public static boolean writeImage(RenderedImage im,
                                String formatName,
                                VFile output) throws IOException {
        try (OutputStream in = output.getOutputStream()) {
            return ImageIO.write(im, formatName, in);
        }
    }

    public static class MethodKey {
        private String name;
        private Class[] params;

        public MethodKey(Method m) {
            this.name = m.getName();
            this.params = m.getParameterTypes();
        }

        public MethodKey(String name, Class[] params) {
            this.name = name;
            this.params = params;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MethodKey methodKey = (MethodKey) o;

            if (name != null ? !name.equals(methodKey.name) : methodKey.name != null) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(params, methodKey.params);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + Arrays.hashCode(params);
            return result;
        }
    }

    public static class MethodInfo {
        private Method method;
        private ClassInfo type;
        private MethodKey key;

        public MethodInfo(ClassInfo type, Method method, MethodKey key) {
            this.type = type;
            this.method = method;
            this.key = key;
        }

        public String getName() {
            return method.getName();
        }

        public Method getMethod() {
            return method;
        }

        public MethodKey getKey() {
            return key;
        }
    }

    public static class PropertyInfo {
        private String name;
        private ClassInfo parent;
        private ClassInfo type;
        private MethodInfo getter;
        private MethodInfo setter;

        public PropertyInfo(ClassInfo parent, String name, ClassInfo type) {
            this.name = name;
            this.parent = parent;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public ClassInfo getParent() {
            return parent;
        }

        public ClassInfo getType() {
            return type;
        }

        public Object get(Object instance) {
            if (getter == null) {
                throw new RuntimeException("No getter");
            }
            try {
                return getter.method.invoke(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Never");
            } catch (InvocationTargetException e) {
                Throwable r = e.getTargetException();
                if (r instanceof RuntimeException) {
                    throw (RuntimeException) r;
                }
                throw new RuntimeException(r);
            }
        }

        public boolean isQueriable() {
            return getter != null;
        }

        public boolean isUpdatable() {
            return setter != null;
        }

        public void set(Object instance, Object val) {
            if (setter == null) {
                throw new RuntimeException("No getter");
            }
            try {
                setter.method.invoke(instance, val);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Never");
            } catch (InvocationTargetException e) {
                Throwable r = e.getTargetException();
                if (r instanceof RuntimeException) {
                    throw (RuntimeException) r;
                }
                throw new RuntimeException(r);
            }
        }
    }

    public static class ClassInfo {
        private Class clazz;
        private Map<MethodKey, MethodInfo> methods = new HashMap();
        private Map<String, List<MethodInfo>> methodsByName = new HashMap<>();
        private Map<String, PropertyInfo> props = new HashMap<>();

        public ClassInfo(Class clazz) {
            this.clazz = clazz;
        }

        public Class getClazz() {
            return clazz;
        }

        public MethodInfo[] getDeepMethods(String name) {
            List<MethodInfo> o = new ArrayList<>();
            Set<MethodKey> visited = new HashSet<>();
            List<MethodInfo> methodInfos = methodsByName.get(name);
            if (methodInfos != null) {
                for (MethodInfo methodInfo : methodInfos) {
                    if (!visited.contains(methodInfo.key)) {
                        o.add(methodInfo);
                        visited.add(methodInfo.key);
                    }
                }
            }
            for (MethodInfo methodInfo : forType(clazz.getSuperclass()).getDeepMethods(name)) {
                if (!visited.contains(methodInfo.key)) {
                    o.add(methodInfo);
                    visited.add(methodInfo.key);
                }
            }
            return o.toArray(new MethodInfo[o.size()]);
        }

        public MethodInfo[] getDeepMethods() {
            List<MethodInfo> o = new ArrayList<>();
            Set<MethodKey> visited = new HashSet<>();
            for (MethodInfo methodInfo : methods.values()) {
                if (!visited.contains(methodInfo.key)) {
                    o.add(methodInfo);
                    visited.add(methodInfo.key);
                }
            }
            ClassInfo classInfo = forType(clazz.getSuperclass());
            if (classInfo != null) {
                for (MethodInfo methodInfo : classInfo.getDeepMethods()) {
                    if (!visited.contains(methodInfo.key)) {
                        o.add(methodInfo);
                        visited.add(methodInfo.key);
                    }
                }
            }
            return o.toArray(new MethodInfo[o.size()]);
        }

        public PropertyInfo[] getDeepProperties() {
            List<PropertyInfo> o = new ArrayList<>();
            Set<String> visited = new HashSet<>();
            for (PropertyInfo pinfo : props.values()) {
                if (!visited.contains(pinfo.name)) {
                    o.add(pinfo);
                    visited.add(pinfo.name);
                }
            }
            ClassInfo classInfo = forType(clazz.getSuperclass());
            if (classInfo != null) {
                for (PropertyInfo pinfo : classInfo.getDeepProperties()) {
                    if (!visited.contains(pinfo.name)) {
                        o.add(pinfo);
                        visited.add(pinfo.name);
                    }
                }
            }
            return o.toArray(new PropertyInfo[o.size()]);
        }

        public PropertyInfo getProperty(String name) {
            PropertyInfo y = props.get(name);
            if (y != null) {
                return y;
            }
            ClassInfo classInfo = forType(clazz.getSuperclass());
            if (classInfo != null) {
                PropertyInfo p = classInfo.getProperty(name);
                if (p != null) {
                    return p;
                }
            }
            return null;
        }

        private void buid() {
            for (Method method : clazz.getMethods()) {
                MethodKey k = new MethodKey(method);
                MethodInfo minfo = new MethodInfo(this, method, k);
                methods.put(k, minfo);
                String methodName = method.getName();
                List<MethodInfo> li = methodsByName.get(methodName);
                if (li == null) {
                    li = new ArrayList<>();
                    methodsByName.put(methodName, li);
                }
                li.add(minfo);

            }

            for (MethodInfo methodInfo : getDeepMethods()) {
                String methodName = methodInfo.getName();
                Method method = methodInfo.getMethod();
                if (methodName.startsWith("get")) {
                    if (method.getReturnType() != Void.TYPE && method.getParameterCount() == 0) {
                        String p = toPropName(methodName.substring(3));
                        PropertyInfo propInfo = props.get(p);
                        if (propInfo == null) {
                            propInfo = new PropertyInfo(this, p, forType(method.getReturnType()));
                            props.put(p, propInfo);
                        }
                        if (propInfo.getter == null) {
                            propInfo.getter = methodInfo;
                            propInfo.type = forType(method.getReturnType());
                        }
                    }
                } else if (methodName.startsWith("is")) {
                    if ((method.getReturnType().equals(Boolean.TYPE) || method.getReturnType().equals(Boolean.class)) && method.getParameterCount() == 0) {
                        String p = toPropName(methodName.substring(2));
                        PropertyInfo propInfo = props.get(p);
                        if (propInfo == null) {
                            propInfo = new PropertyInfo(this, p, forType(method.getReturnType()));
                            props.put(p, propInfo);
                        }
                        if (propInfo.getter == null) {
                            propInfo.getter = methodInfo;
                        }
                    }
                } else if (methodName.startsWith("set")) {
                    if (method.getParameterCount() == 1) {
                        String p = toPropName(methodName.substring(3));
                        PropertyInfo propInfo = props.get(p);
                        ClassInfo type = forType(method.getParameterTypes()[0]);
                        if (propInfo == null) {
                            propInfo = new PropertyInfo(this, p, type);
                            props.put(p, propInfo);
                        }
                        if (propInfo.setter == null) {
                            propInfo.setter = methodInfo;
                            if (propInfo.getter == null) {
                                propInfo.type = type;
                            }
                        }
                    }
                }
            }
        }
    }

    public static String[] getStringArrayConstantsValues(Class cls,String wpattern){
        List<String> all=new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            if(Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
                if(field.getType().equals(String.class) && StringUtils.matchesWildcardExpression(field.getName(),wpattern)){
                    try {
                        all.add((String)field.get(null));
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            }
        }
        return all.toArray(new String[all.size()]);
    }

    public static int[] getIntArrayConstantsValues(Class cls,String wpattern){
        List<Integer> all=new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            if(Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
                if(field.getType().equals(Integer.class) && StringUtils.matchesWildcardExpression(field.getName(),wpattern)){
                    try {
                        all.add((Integer)field.get(null));
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            }
        }
        return PlatformTypeUtils.toPrimitiveIntArray(all);
    }

    public static long[] getLongArrayConstantsValues(Class cls,String wpattern){
        List<Long> all=new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            if(Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
                if(field.getType().equals(Long.class) && StringUtils.matchesWildcardExpression(field.getName(),wpattern)){
                    try {
                        all.add((Long)field.get(null));
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            }
        }
        return PlatformTypeUtils.toPrimitiveLongArray(all);
    }
}
