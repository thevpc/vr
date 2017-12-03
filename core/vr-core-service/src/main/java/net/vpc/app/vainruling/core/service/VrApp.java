/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service;

import net.vpc.app.vainruling.core.service.security.UserSession;
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

import java.util.logging.Level;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
public class VrApp implements ApplicationContextAware {

    private static ApplicationContext context;
    private static boolean running = false;

    public static <T extends Object> T getBean(Class<T> type) throws BeansException {
        return getContext().getBean(type);
    }

    public static <T extends Object> T getBean(String name) throws BeansException {
        return (T) getContext().getBean(name);
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
        String goodFile=null;
        for (String s : new String[]{
                "META-INF/standalone-applicationContext.xml",
                "META-INF/default-standalone-applicationContext.xml"

        }) {
            if(contextClassLoader.getResource(s)!=null){
                goodFile=s;
                break;
            }
        }

        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(goodFile) {
            protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                getBeanFactory().registerScope("session", new SimpleThreadScope());
            }
        };
        if (!StringUtils.isEmpty(login)) {
            CorePlugin core = CorePlugin.get();
            core.getCurrentSession().setSessionId("custom");
            core.login(login, password,"Standalone",null);
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

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        VrApp.context = ac;
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
}
