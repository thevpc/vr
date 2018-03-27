/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.VrControllerInfo;
import net.vpc.app.vainruling.core.web.VrControllerInfoResolver;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import org.springframework.context.annotation.Scope;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController
public class WelcomeCtrl implements VrControllerInfoResolver {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @Override
    public VrControllerInfo resolveVrControllerInfo(String cmd) {
        VrMenuManager mm = VrApp.getBean(VrMenuManager.class);
        VrControllerInfo o = mm.resolveBestVrControllerInfo(WelcomeAlternative.class, cmd);
        if (o == null) {
            return new VrControllerInfo(
                    "Accueil",
                    "",
                    "modules/welcome",
                    "",
                    ""
            );
        }
        return o;
    }

    @OnPageLoad
    public void onLoad() {
        //site filter is no more bound to department
        //Vr vr = Vr.get();
        //UserSession userSession = vr.getCurrentSession();
        //vr.gotoPublicSubSite(userSession.getSelectedDepartment()==null?null:userSession.getSelectedDepartment().getCode());
        VrMenuManager menu = VrApp.getBean(VrMenuManager.class);
        menu.getModel().setCurrentPageId("welcome");
        menu.setPageCtrl("welcome");
    }

    public static class Model {

    }
}
