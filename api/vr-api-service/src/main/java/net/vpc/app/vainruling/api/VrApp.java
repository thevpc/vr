/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api;

import java.util.logging.Level;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.TransactionType;
import net.vpc.upa.UPA;
import net.vpc.upa.impl.util.Strings;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
public class VrApp implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        VrApp.context = ac;
    }

    public static <T extends Object> T getBean(Class<T> type) throws BeansException {
        return getContext().getBean(type);
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void runStandalone(String login, String password, boolean activateLog, String[] args) {
        if (activateLog) {
            net.vpc.common.utils.LogUtils.configure(Level.FINE, "net.vpc");
        }
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/stanalone-applicationContext.xml") {
            protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                getBeanFactory().registerScope("session", new SimpleThreadScope());
            }
        };
        if (!Strings.isNullOrEmpty(login)) {
            VrApp.getBean(UserSession.class).setSessionId("custom");
            VrApp.getBean(LoginService.class).login(login, password);
        }
        PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
        persistenceUnit.openSession();
        persistenceUnit.beginTransaction(TransactionType.REQUIRED);
        System.out.println("Starting...");
    }

    public static void stopStandalone() {
        PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
        persistenceUnit.commitTransaction();
    }
}
