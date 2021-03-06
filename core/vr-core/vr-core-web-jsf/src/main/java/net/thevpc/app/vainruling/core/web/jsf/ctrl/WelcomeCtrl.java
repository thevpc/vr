/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import org.springframework.stereotype.Controller;
import net.thevpc.app.vainruling.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
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

    @VrOnPageLoad
    public void onLoad() {
        //site filter is no more bound to department
        //Vr vr = Vr.get();
        //UserSession userSession = vr.getCurrentSession();
        //vr.gotoPublicSubSite(userSession.getSelectedDepartment()==null?null:userSession.getSelectedDepartment().getCode());
        Vr.get().setCurrentPageId("welcome");
//        menu.setPageCtrl("welcome");
    }

    public static class Model {

    }
}
