package net.vpc.app.vainruling.core.service.pages;


/**
 * manages visibility of menus (VrControllerInfo, VrMenuInfo)
 *
 * @author vpc
 */
public interface VrActionEnabler {

    boolean isEnabled(VrActionInfo data);
}
