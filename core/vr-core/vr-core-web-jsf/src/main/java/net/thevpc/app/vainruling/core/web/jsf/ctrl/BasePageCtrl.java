/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.VrBreadcrumbItem;
import net.thevpc.app.vainruling.core.web.util.VrWebHelper;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class BasePageCtrl extends BaseCtrl {

    public VrBreadcrumbItem getTitle() {
        return VrWebHelper.resolveBreadcrumbItemForBean(this);
    }

}
