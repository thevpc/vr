/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.security.MemSessionStore;
import net.vpc.app.vainruling.core.service.security.SessionStore;
import net.vpc.app.vainruling.core.service.security.SessionStoreProvider;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.TransactionType;
import net.vpc.upa.UPA;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.vpc.upa.VoidAction;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
public class VrApp implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private static ApplicationContext context;
    private static boolean running = false;

//    @PostConstruct
//    private void initialize() {
//       
//    }
    public static <T extends Object> T getBean(Class<T> type) throws BeansException {
        return (T) validateBean(getContext().getBean(type));
    }

    public static <T extends Object> T getBean(String name) throws BeansException {
        return (T) validateBean(getContext().getBean(name));
    }

    public static List<Object> getBeansForAnnotations(Class type) throws BeansException {
        String[] appPluginBeans = getContext().getBeanNamesForAnnotation(type);
        List<Object> m = new ArrayList<>();
        for (String n : appPluginBeans) {
            m.add(getBean(n));
        }
        return m;
    }
    
    public static List<Class> getBeanTypesForAnnotations(Class type) throws BeansException {
        String[] appPluginBeans = getContext().getBeanNamesForAnnotation(type);
        List<Class> m = new ArrayList<>();
        for (String n : appPluginBeans) {
            m.add(getContext().getType(n));
        }
        return m;
    }

    public static Map<String, Object> getBeansMapForAnnotations(Class type) throws BeansException {
        String[] appPluginBeans = getContext().getBeanNamesForAnnotation(type);
        Map<String, Object> m = new HashMap<>();
        for (String n : appPluginBeans) {
            m.put(n, getBean(n));
        }
        return m;
    }

    public static <T extends Object> List<T> getBeansForType(Class<T> type) throws BeansException {
        String[] names = getContext().getBeanNamesForType(type);
        List<T> found = new ArrayList<>();
        for (String name : names) {
            found.add((T) getBean(name));
        }
        return found;
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> base = getContext().getBeansOfType(type);
        Map<String, T> ret = new HashMap<>();
        for (Map.Entry<String, T> entry : base.entrySet()) {
            ret.put(entry.getKey(), (T) validateBean(entry.getValue()));
        }
        return ret;
    }

    private static Object validateBean(Object ins) {
//        boolean requirePostContruct = false;
//        for (Field declaredField : ins.getClass().getDeclaredFields()) {
//            Autowired a = declaredField.getAnnotation(Autowired.class);
//            declaredField.setAccessible(true);
//            if (a != null) {
//                Object v = null;
//                try {
//                    v = declaredField.get(ins);
//                    if (v == null) {
//                        v = VrApp.getBean(declaredField.getType());
//                        declaredField.set(ins, v);
//                        requirePostContruct = true;
//                    }
//                } catch (Exception ex) {
//                    Logger.getLogger(CorePluginBodyPluginManager.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//        if (requirePostContruct) {
//            for (Method declaredMethod : ins.getClass().getDeclaredMethods()) {
//                PostConstruct a = declaredMethod.getAnnotation(PostConstruct.class);
//                if (a != null) {
//                    declaredMethod.setAccessible(true);
//                    try {
//                        declaredMethod.invoke(ins);
//                    } catch (Exception ex) {
//                        Logger.getLogger(CorePluginBodyPluginManager.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
//        }
        return ins;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void runStandalone(String login, String password, boolean activateLog) {
        running = true;
        if (activateLog) {
            net.vpc.common.util.LogUtils.configure(Level.FINE, "net.vpc");
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String goodFile = null;
        for (String s : new String[]{
            "META-INF/standalone-applicationContext.xml",
            "META-INF/default-standalone-applicationContext.xml"

        }) {
            if (contextClassLoader.getResource(s) != null) {
                goodFile = s;
                break;
            }
        }

        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(goodFile) {
            protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                getBeanFactory().registerScope("session", new SimpleThreadScope());
                getBeanFactory().registerSingleton("sessionStoreProvider", new SessionStoreProvider() {
                    MemSessionStore memSessionStore = new MemSessionStore();

                    @Override
                    public SessionStore resolveSessionStore() {
                        return memSessionStore;
                    }
                });
            }
        };
        if (!StringUtils.isBlank(login)) {
            CorePlugin core = CorePlugin.get();
            core.getCurrentSession().setSessionId("custom");
            core.getCurrentSession().getToken().setSessionId("custom");
            core.authenticate(login, password, "Standalone", null);
        }
        PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
        persistenceUnit.openSession();
        persistenceUnit.beginTransaction(TransactionType.REQUIRED);
        System.out.println("Starting...");
    }

    public static void stopStandalone() {
        if (running) {
            running = false;
            PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
            persistenceUnit.commitTransaction();
        }
    }

    public static void runStandalone() {
        VrApp.runStandalone(CorePlugin.USER_ADMIN, "admin", true);
    }

    public static void runStandaloneNoLog() {
        VrApp.runStandalone(CorePlugin.USER_ADMIN, "admin", false);
    }

    public static void runStandalone(String login, String password) {
        VrApp.runStandalone(login, password, true);
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        VrApp.context = ac;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        UPA.getPersistenceGroup().getPersistenceUnit("main").invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                VrApp.getBean(CorePlugin.class).start();
            }
        });
    }

}
