/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.web.jsf.Vr;
import org.springframework.context.annotation.Scope;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrOnPageLoad;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
//        title = "Contacts",
        url = "#{vr.privateThemePath}/contacts"
)
@Scope(value = "singleton")
public class ContactsCtrl {

    @VrOnPageLoad
    public void onLoad() {
        Vr.get().setCurrentPageId("contacts");
    }
}
