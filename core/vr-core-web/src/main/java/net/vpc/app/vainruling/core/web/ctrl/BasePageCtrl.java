/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.util.Reflector;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.app.vainruling.core.web.UCtrl;

/**
 *
 * @author vpc
 */
public abstract class BasePageCtrl extends BaseCtrl {

    public BreadcrumbItem getTitle() {
        UCtrl c = (UCtrl) Reflector.getTargetClass(this).getAnnotation(UCtrl.class);
        return new BreadcrumbItem(c == null ? "" : c.title(), c == null ? "" : c.css(), "", "");
    }

}
