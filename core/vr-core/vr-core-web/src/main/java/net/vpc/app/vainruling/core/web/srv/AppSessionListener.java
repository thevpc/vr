/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.srv;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.Session;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author taha.bensalah@gmail.com
 */
@WebListener
public class AppSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        try {
            String sid = se.getSession().getId();
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            UPA.getContext().invokePrivileged(new VoidAction() {
                @Override
                public void run() {
                    PersistenceUnit persistenceUnit = UPA.getPersistenceUnit();
                    Session s = persistenceUnit.getCurrentSession();
                    s.setParam(persistenceUnit,"Event","SessionDestroyed");
                    core.logout(sid);
                }
            });
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
