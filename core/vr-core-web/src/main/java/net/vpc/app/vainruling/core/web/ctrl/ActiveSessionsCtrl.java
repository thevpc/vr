/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.core.service.ActiveSessionsTracker;
import net.vpc.common.utils.Chronometer;

/**
 *
 * @author vpc
 */
@UCtrl(
        title = "Sessions Actives",
        url = "modules/admin/activesessions",
        menu = "/Admin/Security",
        securityKey = "Custom.Admin.ActiveSessions"
)
@ManagedBean
@SessionScoped
public class ActiveSessionsCtrl {

    private Model model = new Model();

    public void onRefresh(){
        
    }
    public Model getModel() {
        return model;
    }

    public static class Model {

    }
    public void onUpdate(){
        
    }
    public List<UserSession> getActiveSessions(){
        return VrApp.getBean(ActiveSessionsTracker.class).getOrderedActiveSessions();
    }
    
    public String connectionPeriod(UserSession s){
        if(s==null){
            return "";
        }
        final Date t = s.getConnexionTime();
        return Chronometer.formatPeriod(System.currentTimeMillis()-t.getTime(), Chronometer.DatePart.s);
    }
}
