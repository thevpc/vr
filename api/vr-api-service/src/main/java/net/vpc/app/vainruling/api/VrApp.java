/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api;

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
public class VrApp implements ApplicationContextAware{
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        VrApp.context=ac;
    }

    public static <T extends Object> T getBean(Class<T> type) throws BeansException{
        return getContext().getBean(type);
    }
    
    public static ApplicationContext getContext() {
        return context;
    }

    public static void runStandalone(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/stanalone-applicationContext.xml") {
            protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                getBeanFactory().registerScope("session", new SimpleThreadScope());
            }
        };
        System.out.println("Starting...");
    }
}
