/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrOnPageLoad;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
//        title = "Activités",
        url = "activities"
)
public class ActivitiesCtrl {

    @VrOnPageLoad
    public void onLoad() {
        Vr.get().setCurrentPageId("activities");
    }
}
