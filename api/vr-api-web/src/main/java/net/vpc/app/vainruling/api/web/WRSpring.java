/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
public class WRSpring implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WRSpring.applicationContext = applicationContext;
    }

    public static <T> T getSessionBean(Class<T> requiredType, HttpServletRequest req) {
        //workaround as JaxrsRequestAttributes is studidly returning null for session context
        HttpSession s = req.getSession(true);
        T t = (T) s.getAttribute(requiredType.getSimpleName());
        if (t == null) {
            try {
                t = requiredType.newInstance();
                s.setAttribute(requiredType.getSimpleName(), t);
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        return t;
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }

}
