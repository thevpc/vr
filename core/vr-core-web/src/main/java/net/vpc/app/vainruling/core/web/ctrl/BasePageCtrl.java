/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.util.PlatformReflector;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class BasePageCtrl extends BaseCtrl {

    public BreadcrumbItem getTitle() {
        UCtrl c = (UCtrl) PlatformReflector.getTargetClass(this).getAnnotation(UCtrl.class);
        return new BreadcrumbItem(c == null ? "" : c.title(), c == null ? "" : c.css(), "", "");
    }

}
