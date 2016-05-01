/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.VrMenuManager;

/**
 *
 * @author vpc
 */
@UCtrl(
        title = "Activités",
        url = "activities"
)
@ManagedBean
@SessionScoped
public class ActivitiesCtrl {

    @OnPageLoad
    public void onLoad() {
        VrApp.getBean(VrMenuManager.class).getModel().setCurrentPageId("activities");
        VrApp.getBean(VrMenuManager.class).setPageCtrl("activities");
    }
}
