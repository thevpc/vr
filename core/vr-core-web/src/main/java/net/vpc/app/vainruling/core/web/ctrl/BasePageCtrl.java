/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.service.util.PlatformReflector;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.common.strings.StringUtils;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class BasePageCtrl extends BaseCtrl {

    public BreadcrumbItem getTitle() {
        Class cls = PlatformReflector.getTargetClass(this);
        UCtrl c = (UCtrl) cls.getAnnotation(UCtrl.class);
        String title = I18n.get().getOrNull("Controller." + VrUtils.getBeanName(cls));
        if(StringUtils.isEmpty(title)){
            title=c.title();
        }
        return new BreadcrumbItem(c == null ? "" : c.title()
                , c == null ? "" : c.subTitle()
                , c == null ? "" : c.css()
                , ""
                , "");
    }

}
