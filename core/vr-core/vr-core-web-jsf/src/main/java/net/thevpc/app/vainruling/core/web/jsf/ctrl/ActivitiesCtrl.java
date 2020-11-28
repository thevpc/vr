/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import net.thevpc.app.vainruling.VrPage;

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
