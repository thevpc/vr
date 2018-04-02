/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.VrControllerInfo;
import net.vpc.app.vainruling.core.web.VrControllerInfoResolver;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import org.springframework.stereotype.Controller;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController(
        title = "Accueil",
        menu = "/Desktop",
        url = "modules/welcome"
)
@Controller
public class WelcomeCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    public void onLoad() {
        //site filter is no more bound to department
        //Vr vr = Vr.get();
        //UserSession userSession = vr.getCurrentSession();
        //vr.gotoPublicSubSite(userSession.getSelectedDepartment()==null?null:userSession.getSelectedDepartment().getCode());
        VrMenuManager menu = VrApp.getBean(VrMenuManager.class);
        menu.getModel().setCurrentPageId("welcome");
//        menu.setPageCtrl("welcome");
    }

    public static class Model {

    }
}
