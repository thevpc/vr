package net.vpc.app.vainruling.core.web;

/**
 * manages visibility of menus (VrControllerInfo, VrMenuInfo)
 *
 * @author vpc
 */
public interface VrActionEnabler {

    boolean isEnabled(VrActionInfo data);
}
