/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.util;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author taha.bensalah@gmail.com
 */
public class PlatformReflector {

    private final static Map<MethodInvokerKey, MethodInvoker[]> cachedMethodInvokers = new HashMap<>();

    //    invokeBean(String benName,String method,)
    public static Class getTargetClass(Object proxy) {
        if (proxy == null) {
            return null;
        }
        Class<?> t = AopUtils.getTargetClass(proxy);
        if (t == null) {
            return proxy.getClass();
        }
        return t;
    }

    public static InstanceInvoker[] findInstanceMethods(Object obj, String name, Class annoType, Class[] args) {
        MethodInvoker[] mi = findMethods(PlatformReflector.getTargetClass(obj), name, annoType, args);
        InstanceInvoker[] ii = new InstanceInvoker[mi.length];
        for (int i = 0; i < ii.length; i++) {
            ii[i] = new DefaultInstanceInvoker(mi[i], obj);
        }
        return ii;
    }

    public static InstanceInvoker[] findInstanceMethodsByAnnotation(Object obj, Class anno) {
        return findInstanceMethods(obj, null, anno, null);
    }

    public static InstanceInvoker[] findInstanceMethodsByAnnotation(Object obj, Class anno, Class[] args) {
        return findInstanceMethods(obj, null, anno, args);
    }

    public static InstanceInvoker[] findInstanceMethodsByName(Object obj, String name, Class[] args) {
        return findInstanceMethods(obj, name, null, args);
    }

    public static MethodInvoker[] findMethodsByAnnotation(Class clz, Class anno, Class[] args) {
        return PlatformReflector.findMethods(clz, null, anno, args);
    }

    public static MethodInvoker[] findMethodsByName(Class clz, String name, Class[] args) {
        return PlatformReflector.findMethods(clz, name, null, args);
    }

    public static MethodInvoker[] findMethods(Class clz, String name, Class annoType, Class[] args) {
        MethodInvokerKey k = new MethodInvokerKey(clz, name, annoType, args);
        MethodInvoker[] v = cachedMethodInvokers.get(k);
        if (v != null) {
            return v;
        }
        synchronized (cachedMethodInvokers) {
            v = cachedMethodInvokers.get(k);
            if (v != null) {
                return v;
            }
            Class c = clz;
            boolean checkDirect = false;
            List<MethodInvoker> found = new ArrayList<>();
            while (c != null) {
                if (checkDirect) {
                    Method m = null;
                    if (name != null && args != null) {
                        boolean ok = true;
                        for (int i = 0; i < args.length; i++) {
                            if (args[i] == null) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok) {
                            try {
                                m = c.getDeclaredMethod(name, args);
                            } catch (Exception ex) {
                                //
                            }
                        }
                    }
                    if (m != null) {
                        return new MethodInvoker[]{new DefaultMethodInvoker(m, annoType, 0)};
                    }
                } else {
                    for (Method m : c.getDeclaredMethods()) {
//                    if (m.getName().equals("start")) {
//                        System.out.println("Why");
//                    }
                        boolean accept = false;
                        if (name != null) {
                            if (m.getName().equals(name)) {
                                accept = true;
                            }
                        } else if (m.getAnnotation(annoType) != null) {
                            accept = true;
                        }
                        if (accept) {
                            if (args != null) {
                                accept = false;
                                if (args != null) {
                                    Class<?>[] t = m.getParameterTypes();
                                    if (!m.isVarArgs()) {
                                        if (t.length == args.length) {
                                            accept = true;
                                            for (int i = 0; i < t.length; i++) {
                                                if (args[i] == null || t[i].isAssignableFrom(args[i])) {
                                                    //ok
                                                } else {
                                                    accept = false;
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        throw new IllegalArgumentException("Not yet supported");
                                    }
                                }
                            }
                            if (accept) {
                                found.add(new DefaultMethodInvoker(m, annoType, found.size()));
                            }

                        }
                    }
                }
                c = c.getSuperclass();
            }
            Collections.sort(found, new Comparator<MethodInvoker>() {

                @Override
                public int compare(MethodInvoker o1, MethodInvoker o2) {
                    Class[] a1 = o1.getParameterTypes();
                    Class[] a2 = o2.getParameterTypes();
                    int x = a1.length - a2.length;
                    if (x != 0) {
                        return x;
                    }
                    for (int i = 0; i < a2.length; i++) {
                        if (a1[i].equals(a2[i])) {
                            //ok
                        } else if (a1[i].isAssignableFrom(a2[i])) {
                            return -1;
                        } else if (a2[i].isAssignableFrom(a1[i])) {
                            return 1;
                        }
                        int x1 = depth(a1[i]);
                        int x2 = depth(a2[i]);
                        int x3 = x1 - x2;
                        if (x != 0) {
                            return x3;
                        }
                    }
                    return ((DefaultMethodInvoker) o1).pos - ((DefaultMethodInvoker) o1).pos;
                }
            });
            MethodInvoker[] r = found.toArray(new MethodInvoker[found.size()]);
            cachedMethodInvokers.put(k, v);
            return r;
        }
    }

    private static int depth(Class clz) {
        if (clz == null) {
            return 0;
        }
        if (clz.equals(Object.class)) {
            return 0;
        }
        return 1 + depth(clz.getSuperclass());
    }

    public static MethodInvoker[] findMethods(Class c, String name, Object[] args) {
        Class[] targs = new Class[args.length];
        for (int i = 0; i < targs.length; i++) {
            targs[i] = args[i] == null ? null : PlatformReflector.getTargetClass(args[i]);
        }
        return findMethodsByName(c, name, targs);
    }

    public <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return (T) ((Advised) proxy).getTargetSource().getTarget();
        } else {
            return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
        }
    }

    public static class MethodInvokerKey {

        private final Class clz;
        private final String name;
        private final Class annoType;
        private final Class[] args;

        public MethodInvokerKey(Class clz, String name, Class annoType, Class[] args) {
            this.clz = clz;
            this.name = name;
            this.annoType = annoType;
            this.args = args;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + Objects.hashCode(this.clz);
            hash = 67 * hash + Objects.hashCode(this.name);
            hash = 67 * hash + Objects.hashCode(this.annoType);
            hash = 67 * hash + Arrays.deepHashCode(this.args);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MethodInvokerKey other = (MethodInvokerKey) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (!Objects.equals(this.clz, other.clz)) {
                return false;
            }
            if (!Objects.equals(this.annoType, other.annoType)) {
                return false;
            }
            if (!Arrays.deepEquals(this.args, other.args)) {
                return false;
            }
            return true;
        }

    }

    public interface MethodInvoker {

        <T extends Annotation> T getAnnotation();

        String getName();

        Class[] getParameterTypes();

        Class getReturnType();

        <T> T invoke(Object o, Object... args);
    }

    public interface InstanceInvoker {

        <T extends Annotation> T getAnnotation();

        Object getInstance();

        String getName();

        Class getReturnType();

        Class[] getParameterTypes();

        <T> T invoke(Object... args);
    }

    public static class DefaultInstanceInvoker implements InstanceInvoker {

        MethodInvoker m;
        Object instance;

        public DefaultInstanceInvoker(MethodInvoker m, Object instance) {
            this.m = m;
            this.instance = instance;
        }

        @Override
        public <T extends Annotation> T getAnnotation() {
            return m.getAnnotation();
        }

        @Override
        public Object getInstance() {
            return instance;
        }

        @Override
        public String getName() {
            return m.getName();
        }

        @Override
        public Class[] getParameterTypes() {
            return m.getParameterTypes();
        }

        @Override
        public <T> T invoke(Object... args) {
            return m.invoke(instance, args);
        }

        @Override
        public Class getReturnType() {
            return m.getReturnType();
        }

        @Override
        public String toString() {
            return m.toString();
        }

    }

    public static class DefaultMethodInvoker implements MethodInvoker {

        private Method m;
        private Class annoType;
        private int pos;

        public DefaultMethodInvoker(Method m, Class annoType, int pos) {
            m.setAccessible(true);
            this.m = m;
            this.annoType = annoType;
            this.pos = pos;
        }

        @Override
        public Class[] getParameterTypes() {
            return m.getParameterTypes();
        }

        @Override
        public <T extends Annotation> T getAnnotation() {
            return annoType == null ? null : (T) m.getAnnotation(annoType);
        }

        @Override
        public Class getReturnType() {
            return m.getReturnType();
        }

        @Override
        public String getName() {
            return m.getName();
        }

        @Override
        public String toString() {
            return m.toString();
        }

        @Override
        public <T> T invoke(Object o, Object... args) {
            try {
                return (T) m.invoke(o, args);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex) {
                if (ex.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) ex.getCause();
                }
                throw new RuntimeException(ex.getCause());
            }
        }

    }
}
