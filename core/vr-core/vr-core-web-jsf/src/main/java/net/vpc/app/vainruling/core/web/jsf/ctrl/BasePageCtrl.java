/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.app.vainruling.core.web.menu.VrControllerInfoAndObject;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class BasePageCtrl extends BaseCtrl {

    public BreadcrumbItem getTitle() {
        VrControllerInfoAndObject c = VrApp.getBean(VrMenuManager.class).resolveVrControllerInfoByInstance(VrUtils.getBeanName(this), null);

        return new BreadcrumbItem(c == null ? "" : c.getInfo().getTitle()
                , c == null ? "" : c.getInfo().getSubTitle()
                , c == null ? "" : c.getInfo().getCss()
                , ""
                , "");
    }

}
