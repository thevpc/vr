/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import org.springframework.context.annotation.Scope;

/**
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        title = "Contacts",
        url = "#{vr.themePath}/contacts"
)
@Scope(value = "singleton")
public class ContactsCtrl {

    @OnPageLoad
    public void onLoad() {
        VrApp.getBean(VrMenuManager.class).getModel().setCurrentPageId("contacts");
        VrApp.getBean(VrMenuManager.class).setPageCtrl("contacts");
    }
}
