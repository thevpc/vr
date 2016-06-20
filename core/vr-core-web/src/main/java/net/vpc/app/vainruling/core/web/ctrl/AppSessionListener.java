/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.agent.ActiveSessionsTracker;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author vpc
 */
@WebListener
public class AppSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
//        ApplicationContext context = 
//            WebApplicationContextUtils.getWebApplicationContext(
//                se.getSession().getServletContext()
//            );
//
//        ActiveSessionsTracker t=context.getBean(ActiveSessionsTracker.class); 
//        t.onCreate(null);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        try {
            VrApp.getBean(CorePlugin.class).logout();
        } catch (Exception e) {
            System.err.println(e);
        }
        try {
            String sid = se.getSession().getId();
            VrApp.getBean(ActiveSessionsTracker.class).onDestroy(sid);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
