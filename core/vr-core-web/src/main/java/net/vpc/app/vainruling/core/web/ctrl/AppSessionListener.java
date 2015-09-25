/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.core.service.ActiveSessionsTracker;
import net.vpc.app.vainruling.core.service.LoginService;

/**
 *
 * @author vpc
 */
@WebListener
public class AppSessionListener implements HttpSessionListener{

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
        try{
            VrApp.getBean(LoginService.class).logout();
        }catch(Exception e){
            System.err.println(e);
        }
        try{
            String sid = se.getSession().getId();
            VrApp.getBean(ActiveSessionsTracker.class).onDestroy(sid);
        }catch(Exception e){
            System.err.println(e);
        }
    }
    
}
