/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.ctrl;

import net.vpc.app.vainruling.api.web.BreadcrumbItem;
import net.vpc.app.vainruling.api.web.UCtrl;

/**
 *
 * @author vpc
 */
public abstract class BasePageCtrl extends BaseCtrl {

    public BreadcrumbItem getTitle() {
        UCtrl c = getClass().getAnnotation(UCtrl.class);
        return new BreadcrumbItem(c == null ? "" : c.title(), c == null ? "" : c.css(), "", "");
    }

}
