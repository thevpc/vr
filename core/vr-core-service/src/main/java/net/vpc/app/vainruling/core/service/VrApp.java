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

    public static ApplicationContext getContext() {
        return context;
    }

    public static void runStandalone(String login, String password, boolean activateLog) {
        running = true;
        if (activateLog) {
            net.vpc.common.util.LogUtils.configure(Level.FINE, "net.vpc");
        }
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext
                ("META-INF/stanalone-applicationContext.xml") {
            protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                getBeanFactory().registerScope("session", new SimpleThreadScope());
            }
        };
        if (!StringUtils.isEmpty(login)) {
            UserSession.get().setSessionId("custom");
            VrApp.getBean(CorePlugin.class).login(login, password);
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
